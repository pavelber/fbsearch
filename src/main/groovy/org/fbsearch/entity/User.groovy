package org.fbsearch.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by Pavel on 1/16/2016.
 */
@Entity
@Table(name="users")
class User {
    @Id
    String username;
    String password;
    boolean enabled;
    UserStatus status;
    Date firstindexeddate;
    Date lastindexeddate;



}
