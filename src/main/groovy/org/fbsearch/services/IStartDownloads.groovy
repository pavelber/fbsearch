package org.fbsearch.services

import org.fbsearch.entity.User

/**
 * Created by Pavel on 1/19/2016.
 */
interface IStartDownloads {
    public void downloadForUser(User user);
}