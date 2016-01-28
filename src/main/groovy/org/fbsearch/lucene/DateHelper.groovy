package org.fbsearch.lucene

import org.apache.lucene.document.DateTools

/**
 * Created by Pavel on 10/8/2015.
 */
class DateHelper {
    static String toString(long d){
        return DateTools.timeToString(d, DateTools.Resolution.MINUTE)
    }
}
