package org.ljsearch.lucene

import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.store.RAMDirectory
import org.fbsearch.IndexedType
import org.fbsearch.lucene.LuceneIndexer
import org.fbsearch.lucene.LuceneSearcher
import org.fbsearch.lucene.FBPost
import spock.lang.Specification

import java.nio.file.Paths

/**
 * Created by Pavel on 10/8/2015.
 */
class FBIndexerTest extends Specification {


    LuceneIndexer indexer = new LuceneIndexer()
    LuceneSearcher seacher = new LuceneSearcher()

    def setup() {
       Directory index = new RAMDirectory();
      //  Directory index = FSDirectory.open(Paths.get(System.getProperty("user.home")+
       //         File.separator+"fbsearch"));

        indexer.init(index)

        indexer.add(new FBPost(
                url: "1",
                date: new Date(2014,4,4).time,
                caption: "hexbug.com",message: "Купил hexbugs\n" +
                "https://www.hexbug.com/hexbug-battle-spider.html\n" +
                "\n" +
                "Очень прикольно, но уж больно медленно они ходят мыши",
                name: "HEXBUG Battle Spider | HEXBUG",
                description: "<p>A unique, battle-hardened deco and hi-tech life-sensor that measures each hit adds to ultimate battle experience. The six-legged spider features 360 degree steering and an LED forward eye, allowing you to maneuver it around objects and control precisel",
                link: "https://www.hexbug.com/hexbug-battle-spider.html"));
        indexer.add(new FBPost(
                url: "2",
                date: new Date(2015,4,4).time,
                message: "test for search\n" +
                        "проверка связи\n" +
                        "не обращать внимания - тестирую скачивание постов мыши"

        ));
        indexer.optimizeAndClose()

        seacher.init(index)

    }

    def "test empty"() {
        when:
        def res = seacher.search(null, null, null)
        then:
        res.size() == 0
    }

    def "test text"() {
        when:
        def res = seacher.search("нету", null, null)
        then:
        res.size() == 0

        when:
        res = seacher.search("Купил", null, null)
        then:
        res.size() == 1

        when:
        res = seacher.search("больно медленно", null, null)
        then:
        res[0].url == "1"
        res.size() == 1

        when:
        res = seacher.search("внимания", null, null)
        then:
        res.size() == 1
        res[0].url == "2"

        when:
        res = seacher.search("ходит", null, null)
        then:
        res.size() == 1
        res[0].url == "1"

        when:
        res = seacher.search("battle", null, null)
        then:
        res.size() == 1
        res[0].url == "1"

        when:
        res = seacher.search("spider", null, null)
        then:
        res.size() == 1
        res[0].url == "1"

        when:
        res = seacher.search("spiders", null, null)
        then:
        res.size() == 1
        res[0].url == "1"
    }



    def "test dates"() {
        when:
        def res = seacher.search( "battle", new Date(2000, 1, 1), null)
        then:
        res.size() == 1
        res[0].url == "1"

        when:
        res = seacher.search("battles", new Date(2000, 1, 1), new Date(2015, 1, 1))
        then:
        res.size() == 1
        res[0].url == "1"

        when:
        res = seacher.search( "мыши", new Date(2000, 1, 1), new Date(2016, 1, 1))
        then:
        res.size() == 2

        when:
        res = seacher.search( "обращать", new Date(2014, 1, 1), new Date(2016, 1, 1))
        then:
        res[0].url == "2"

        when:
        res = seacher.search( "обращать", new Date(2014, 1, 1), new Date(2015, 5, 1))
        then:
        res[0].url == "2"
    }



}