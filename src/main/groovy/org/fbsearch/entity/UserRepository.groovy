package org.fbsearch.entity

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by Pavel on 9/29/2015.
 */
interface UserRepository extends JpaRepository<User,String> {
}
