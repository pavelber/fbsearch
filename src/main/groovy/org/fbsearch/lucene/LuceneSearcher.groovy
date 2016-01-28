package org.fbsearch.lucene

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.DateTools
import org.apache.lucene.document.Document
import org.apache.lucene.search.*
import org.apache.lucene.search.highlight.*
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import java.nio.file.Paths

/**
 * Created by Pavel on 10/5/2015.
 */
@Service
@CompileStatic
class LuceneSearcher implements ISearcher {

    public static final int MAX_LENGTH_FIRST_LINE = 100
    @Value('${index.dir}')
    protected String indexDir

    protected SearcherManager mgr


    def init() {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        init(dir)
    }

    def init(Directory dir) {
        mgr = new SearcherManager(dir, SearcherFactory.newInstance());
    }

    @Override
    Set<FBPost> search(String text, Date from, Date to) {

        synchronized (this) {//todo bad
            if (mgr == null) {
                init()
            }
        }

        mgr.maybeRefresh()
        def searcher = mgr.acquire()
        Set<FBPost> results = new HashSet()
        if (!StringUtils.isEmpty(text) ||
                from != null || to != null
        ) {
            try {
                Query q = QueryHelper.generate(text, from, to);
                SortField startField = new SortField(LuceneBinding.DATE_FIELD, SortField.Type.STRING_VAL, true);

                Sort sort = new Sort(startField);
                Collector collector = TopFieldCollector.create(sort, 400, true, false, false);
                searcher.search(q, collector);
                // def q1 = new QueryParser("content", LuceneBinding.getAnalyzer()).parse("+contenttype:Post")
                //def q1 = new QueryParser("caption", LuceneBinding.getAnalyzer()).parse("+caption:hexbug")
                //searcher.search(q1, collector);
                ScoreDoc[] hits = collector.topDocs().scoreDocs;
                for (int i = 0; i < hits.length; ++i) {
                    int docId = hits[i].doc;
                    Document d = searcher.doc(docId);

                    def content = d.get(LuceneBinding.MESSAGE_FIELD)
                    if (content == null) {
                        content = d.get(LuceneBinding.DESCRIPTION_FIELD)
                    }
                    if (content == null) {
                        content = d.get(LuceneBinding.CAPTION_FIELD)
                    }

                    if (content == null) {
                        content = d.get(LuceneBinding.NAME_FIELD)
                    }


                    if (content == null) {
                        content = d.get(LuceneBinding.LINK_FIELD)
                    }


                    String citation = createCitation(q, content)
                    results << new FBPost(
                            name: d.get(LuceneBinding.NAME_FIELD),
                            caption: d.get(LuceneBinding.CAPTION_FIELD),
                            description: d.get(LuceneBinding.DESCRIPTION_FIELD),
                            link: d.get(LuceneBinding.LINK_FIELD),
                            url: d.get(LuceneBinding.URL_FIELD),
                            date: DateTools.stringToDate(d.get(LuceneBinding.DATE_FIELD)).time,
                            message: citation)

                }
            } finally {
                mgr.release(searcher)
            }
        }
        return results
    }

    private String createCitation(Query q, String content) {
        String citation
        def highlited = getHighlightedField(q, LuceneBinding.analyzer, LuceneBinding.MESSAGE_FIELD, content)
        if (highlited != null) {
            citation = highlited
        } else {
            def line = content.split("\n")[0]
            citation = ((line.length() > MAX_LENGTH_FIRST_LINE) ? line.substring(0, MAX_LENGTH_FIRST_LINE) : line) + " ... "
        }
        return citation
    }

    private String getHighlightedField(Query query, Analyzer analyzer, String fieldName, String fieldValue) throws IOException, InvalidTokenOffsetsException {
        Formatter formatter = new SimpleHTMLFormatter("<mark>", "</mark>");
        QueryScorer queryScorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, queryScorer);
        highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, 100));
        highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
        return highlighter.getBestFragment(analyzer, fieldName, fieldValue);
    }

}
