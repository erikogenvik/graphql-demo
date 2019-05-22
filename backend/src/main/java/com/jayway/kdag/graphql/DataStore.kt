package com.jayway.kdag.graphql

import com.couchbase.lite.*
import org.springframework.stereotype.Component

import java.io.IOException
import java.util.HashMap

/**
 * Created by erik on 2017-04-21.
 */
@Component
class DataStore @Throws(IOException::class, CouchbaseLiteException::class)
constructor() {
    private var manager = Manager(JavaContext(), Manager.DEFAULT_OPTIONS)
    internal var database = manager.getDatabase("people")
}
