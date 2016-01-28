package org.fbsearch.controllers.util;

import org.fbsearch.entity.UserProfile;
import org.fbsearch.entity.UserRepository;
import org.fbsearch.services.IStartDownloads;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Service
public class AccountConnectionSignUpService implements ConnectionSignUp {

    @Autowired
    UsersDao usersDao;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BeanFactory beanFactory;

    IStartDownloads startDownloads;




    public String execute(Connection<?> connection) {
        if (startDownloads == null){
            startDownloads = beanFactory.getBean(IStartDownloads.class); // to prevent circular ref
        }
        org.springframework.social.connect.UserProfile profile = connection.fetchUserProfile();
        String userId = UUID.randomUUID().toString().replace("-","");
        usersDao.createUser(userId, new UserProfile(userId, profile));
        startDownloads.downloadForUser(userRepository.findOne(userId)); // should be async
        return userId;
    }
}