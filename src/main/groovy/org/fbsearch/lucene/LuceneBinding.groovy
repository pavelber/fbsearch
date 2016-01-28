package org.fbsearch.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer

/**
 * Created by Pavel on 1/15/2016.
 */
final class  LuceneBinding {
    static final String MESSAGE_FIELD = "message";
    static final String LINK_FIELD = "link";
    static final String NAME_FIELD = "name";
    static final String CAPTION_FIELD = "caption";
    static final String DESCRIPTION_FIELD = "description";
    static final String URL_FIELD = "url";
    static final String DATE_FIELD = "date";
    static final String USER_FIELD = "user";


    static final String TYPE_FIELD = "contenttype";

    /* Russian */

    static final String RUS_MESSAGE_FIELD = "rusmessage";
    static final String RUS_NAME_FIELD = "rusname";
    static final String RUS_CAPTION_FIELD = "ruscaption";
    static final String RUS_DESCRIPTION_FIELD = "rusdescription";


    /* English */

    static final String ENG_MESSAGE_FIELD = "engmessage";
    static final String ENG_NAME_FIELD = "engname";
    static final String ENG_CAPTION_FIELD = "engcaption";
    static final String ENG_DESCRIPTION_FIELD = "engdescription";





    static Analyzer getAnalyzer() {

        Map<String, Analyzer> analyzers =
                new HashMap<String, Analyzer>();
        analyzers.put(RUS_MESSAGE_FIELD, new RussianAnalyzer());
        analyzers.put(RUS_NAME_FIELD, new RussianAnalyzer());
        analyzers.put(RUS_CAPTION_FIELD, new RussianAnalyzer());
        analyzers.put(RUS_DESCRIPTION_FIELD, new RussianAnalyzer());
        analyzers.put(ENG_MESSAGE_FIELD, new EnglishAnalyzer());
        analyzers.put(ENG_NAME_FIELD, new EnglishAnalyzer());
        analyzers.put(ENG_CAPTION_FIELD, new EnglishAnalyzer());
        analyzers.put(ENG_DESCRIPTION_FIELD, new EnglishAnalyzer());

        return new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzers);
    }
}
