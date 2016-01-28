package org.fbsearch.lucene

import org.fbsearch.IndexedType;

class FBPost {
    String message
    String name
    String link
    String caption
    String description
    String url
    IndexedType type
    String username
    long date

    boolean equals(final o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        final FBPost post = (FBPost) o

        if (url != post.url) return false

        return true
    }

    int hashCode() {
        return url.hashCode()
    }
}
