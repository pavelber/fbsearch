package org.fbsearch.services

import groovy.transform.CompileStatic
import org.fbsearch.IndexedType
import org.fbsearch.entity.User
import org.fbsearch.entity.UserRepository
import org.fbsearch.entity.UserStatus
import org.fbsearch.lucene.FBPost
import org.fbsearch.lucene.IIndexer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.UsersConnectionRepository
import org.springframework.social.facebook.api.*
import org.springframework.stereotype.Service

/**
 * Created by Pavel on 9/29/2015.
 */
@Service
@CompileStatic
class NewDownloads implements Runnable {

    static Logger logger = LoggerFactory.getLogger(NewDownloads.class)

    @Autowired
    UsersConnectionRepository usersConnectionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IIndexer indexer;


    @Override
    public void run() {
        def all = userRepository.findAll()
        all.each { user ->
            downloadNewPosts(user)
        }
    }

    public void downloadNewPosts(User user) {
        if (user.status == UserStatus.Normal) {
            ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(user.username);
            List<Connection<?>> connections = connectionRepository.findConnections(Facebook.class);
            for (Connection<?> connection : connections) {
                Facebook api = (Facebook) connection.getApi();
                FeedOperations feedOperations = api.feedOperations();
                int limit = 100;
                int offset = 0;
                //PagingParameters pagedListParameters = new PagingParameters(limit, offset, user.lastindexeddate?.time, null);
                //Because some bug we can't take after the date, so we take all and filter
                PagingParameters pagedListParameters = new PagingParameters(limit, offset, null, null);

                // assuming:
                // 1) lastindexeddate should not be null,
                // No more one page since last invocation (Problematic!!!)
                PagedList<Post> posts = feedOperations.getPosts(pagedListParameters);

                if (user.lastindexeddate != null) {
                    posts.removeAll { it.createdTime.time <= user.lastindexeddate.time }
                }

                posts.each { p ->
                    String url  = "https://www.facebook.com/"+p.id.replace("_","/posts/")
                    indexer.add(new FBPost(name: p.name,
                            message: p.message,
                            description: p.description,
                            caption: p.caption,
                            link: p.link,
                            url: url,
                            date: p.createdTime.time))
                }
                Date newest = (posts.max { p -> p.getCreatedTime() })?.createdTime
                if (newest != null) {
                    user.lastindexeddate = (user.lastindexeddate == null) ? newest : (user.lastindexeddate.before(newest)) ? newest :
                            user.lastindexeddate
                    println "newest = $newest"
                    user.lastindexeddate = newest;
                }
                userRepository.save(user)
            }
        }
    }

}
