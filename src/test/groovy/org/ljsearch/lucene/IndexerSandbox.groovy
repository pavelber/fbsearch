package org.ljsearch.lucene

import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.fbsearch.IndexedType
import org.fbsearch.lucene.FBPost
import org.fbsearch.lucene.LuceneIndexer
import org.fbsearch.lucene.LuceneSearcher

import java.nio.file.Paths

/**
 * Created by Pavel on 10/8/2015.
 */
class IndexerSandbox {


    public static void main(String[] args) {
        LuceneIndexer indexer = new LuceneIndexer()
        LuceneSearcher seacher = new LuceneSearcher()
        Directory index = FSDirectory.open(Paths.get(System.getProperty("user.home")+
                File.separator+"fbsearch"));


        seacher.init(index)
        def res = seacher.search("Купил", null, null)
        println res.size()

    }


}