package org.fbsearch.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by Pavel on 1/16/2016.
 */
@Entity
@Table(name="authorities")
class Authority {
    @Id
    String username
    String authority
}
