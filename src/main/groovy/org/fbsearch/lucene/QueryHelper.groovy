package org.fbsearch.lucene

import org.apache.commons.lang3.StringUtils
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.ParseException
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TermRangeQuery
import org.apache.lucene.util.BytesRef

public final class QueryHelper {

    public static Query generate(String story) throws ParseException {
        QueryParser parser = new MultiFieldQueryParser(
                [
                        LuceneBinding.CAPTION_FIELD,
                        LuceneBinding.DESCRIPTION_FIELD,
                        LuceneBinding.LINK_FIELD,
                        LuceneBinding.MESSAGE_FIELD,
                        LuceneBinding.NAME_FIELD,
                        /* Russian */
                        LuceneBinding.RUS_CAPTION_FIELD,
                        LuceneBinding.RUS_DESCRIPTION_FIELD,
                        LuceneBinding.RUS_MESSAGE_FIELD,
                        LuceneBinding.RUS_NAME_FIELD,
                        /* English */
                        LuceneBinding.ENG_CAPTION_FIELD,
                        LuceneBinding.ENG_DESCRIPTION_FIELD,
                        LuceneBinding.ENG_MESSAGE_FIELD,
                        LuceneBinding.ENG_NAME_FIELD,
                        /* Hebrew */
                        // LuceneBinding.HEB_TITLE_FIELD,
                        // LuceneBinding.HEB_CONTENT_FIELD
                ] as String[],
                LuceneBinding.getAnalyzer());

        /* Operator OR is used by default */

        parser.setDefaultOperator(QueryParser.Operator.AND);

        return parser.parse(QueryParser.escape(story));
    }


    public static Query generate(String words, String username, Date dateFrom, Date dateTo) throws ParseException {

        BooleanQuery.Builder builder = new BooleanQuery.Builder(); ;
        if (!StringUtils.isEmpty(words)) {
            Query qf = generate(words);
            builder = builder.add(qf, BooleanClause.Occur.MUST);
        }

        Query qf = new TermQuery(new Term(LuceneBinding.USER_FIELD, username    ));
        builder = builder.add(qf, BooleanClause.Occur.MUST);

        if (dateFrom != null || dateTo != null) {
            TermRangeQuery dateQuery = new TermRangeQuery(LuceneBinding.DATE_FIELD,
                    dateFrom == null ? null : new BytesRef(DateHelper.toString(dateFrom.time)),
                    dateTo == null ? null : new BytesRef(DateHelper.toString(dateTo.time)), true, false);
            builder = builder.add(dateQuery, BooleanClause.Occur.MUST);
        }


        return builder.build();

    }
}