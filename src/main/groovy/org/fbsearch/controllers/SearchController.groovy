package org.fbsearch.controllers

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import org.fbsearch.entity.UserRepository
import org.fbsearch.lucene.ISearcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import java.security.Principal

/**
 * Created by Pavel on 10/5/2015.
 */
@Controller
@CompileStatic
class SearchController {

    @Autowired
    protected ISearcher seacher

    @Autowired
    UserRepository repo


    @RequestMapping("/search")
    @ResponseBody
    String search( Principal currentUser,
            @RequestParam("term") String term,
            @RequestParam(value = "year", required = false) String yearStr
    ) {
        term = (term == 'null' || term == 'undefined') ? "" : term
        Integer year = (yearStr == 'null' || yearStr == "" || yearStr == null || yearStr == 'undefined') ? null : Integer.parseInt(yearStr)
        Date from, to;
        if (year != null) {
            from = new GregorianCalendar(year, Calendar.JANUARY, 1).time
            to = new GregorianCalendar(year + 1, Calendar.JANUARY, 1).time
        }
        def results = seacher.search(currentUser.name,term, from, to)
        return new JsonBuilder(results).toPrettyString()
    }
}
