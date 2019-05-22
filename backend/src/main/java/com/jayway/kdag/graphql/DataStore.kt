package com.jayway.kdag.graphql

import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.JavaContext
import com.couchbase.lite.Manager
import org.springframework.stereotype.Component
import java.io.IOException


@Component
class DataStore @Throws(IOException::class, CouchbaseLiteException::class)
constructor() {
    private var manager = Manager(JavaContext(), Manager.DEFAULT_OPTIONS)
    internal var database = manager.getDatabase("people")
}
