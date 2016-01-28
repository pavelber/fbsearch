package org.fbsearch.services

import groovy.transform.CompileStatic
import org.fbsearch.entity.User
import org.fbsearch.entity.UserRepository
import org.fbsearch.entity.UserStatus
import org.fbsearch.lucene.FBPost
import org.fbsearch.lucene.IIndexer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
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
class StartDownloads implements IStartDownloads, Runnable {

    static Logger logger = LoggerFactory.getLogger(StartDownloads.class)

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
            downloadForUser(user)
        }
    }

    @Async
    public void downloadForUser(User user) {

        if (user.status == UserStatus.Registered || user.status == UserStatus.DownloadingOld) {
            ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(user.username);
            List<Connection<?>> connections = connectionRepository.findConnections(Facebook.class);
            logger.info("Starting downloading for user ${user.username} ${connections.size()}")
            for (Connection<?> connection : connections) {
                Facebook api = (Facebook) connection.getApi();
                FeedOperations feedOperations = api.feedOperations();
                int limit = 50;
                int offset = 0;
                PagedList<Post> posts;
                Long time = user.firstindexeddate == null ? null : user.firstindexeddate.time + 1000
                PagingParameters pagedListParameters = new PagingParameters(limit, offset, null, time);

                while (true) {
                    posts = feedOperations.getPosts(pagedListParameters);
                    if (user.firstindexeddate != null) {
                        posts.removeAll { it.createdTime.time >= user.firstindexeddate.time }
                    }
                    if (posts.size() == 0) {
                        break;
                    }
                    pagedListParameters = posts.getNextPage();
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
                    Date oldest = (posts.min { p -> p.getCreatedTime() }).createdTime
                    Date newest = (posts.max { p -> p.getCreatedTime() }).createdTime
                    user.lastindexeddate = (user.lastindexeddate == null) ? newest : (user.lastindexeddate.before(newest)) ? newest :
                            user.lastindexeddate
                    user.firstindexeddate = oldest;
                    user.status = UserStatus.DownloadingOld;
                    userRepository.save(user)
                }

                user.status = UserStatus.Normal
                userRepository.save(user)
            }
        }
    }

}
