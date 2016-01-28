package org.fbsearch.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by Pavel on 1/16/2016.
 */
@Entity
@Table(name="userconnection")
class UserConnection {
    @Id
    String userid;

    String providerid;
    String provideruserid;
    int rank;
    String displayname;
    String profileurl;
    String imageurl;
    String accesstoken;
    String secret;
    String refreshtoken;
    Long expiretime;

    UserConnection() {
    }


}
