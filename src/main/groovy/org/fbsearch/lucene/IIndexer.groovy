package org.fbsearch.lucene

import javax.annotation.PreDestroy

/**
 * Created by Pavel on 10/5/2015.
 */
interface IIndexer {
    @PreDestroy
    void optimizeAndClose()

    void add(FBPost post)

    void commit() throws IOException
}
