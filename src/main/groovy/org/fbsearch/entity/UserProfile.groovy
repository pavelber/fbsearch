package org.fbsearch.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by Pavel on 1/16/2016.
 */
@Entity
@Table(name="userprofile")
class UserProfile {
    @Id
    String userid;

    String name;

    String firstname;

    String lastname;

    String email;

    String username;

    UserProfile() {
    }

    public UserProfile(String userId, String name, String firstName, String lastName, String email, String username) {
        this.userid = userid;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;

        fixName();
    }

    public UserProfile(String userId, org.springframework.social.connect.UserProfile up) {
        this.userid = userId;
        this.name = up.getName();
        this.firstname = up.getFirstName();
        this.lastname = up.getLastName();
        this.email = up.getEmail();
        this.username = up.getUsername();
    }

    private void fixName() {
        // Is the name null?
        if (name == null) {

            // Ok, lets try with first and last name...
            name = firstName;

            if (lastName != null) {
                if (name == null) {
                    name = lastName;
                } else {
                    name += " " + lastName;
                }
            }

            // Try with email if still null
            if (name == null) {
                name = email;
            }

            // Try with username if still null
            if (name == null) {
                name = username;
            }

            // If still null set name to UNKNOWN
            if (name == null) {
                name = "UNKNOWN";
            }
        }
    }

}
