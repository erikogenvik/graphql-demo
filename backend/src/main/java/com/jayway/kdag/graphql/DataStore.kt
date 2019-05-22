package com.jayway.kdag.graphql

import com.couchbase.lite.*

import java.io.IOException
import java.util.HashMap

/**
 * Created by erik on 2017-04-21.
 */
class DataStore @Throws(IOException::class, CouchbaseLiteException::class)
constructor() {
    internal var manager = Manager(JavaContext(), Manager.DEFAULT_OPTIONS)
    internal var database = manager.getDatabase("people")

    @Throws(CouchbaseLiteException::class, IOException::class)
    fun setup() {

        // Create a new document (i.e. a record) in the database.
        val document = database.createDocument()
        val properties = mapOf("firstName" to "John")
        document.putProperties(properties)


        // Update a document.
        document.update { newRevision ->
            val properties = newRevision.userProperties
            properties["firstName"] = "Johnny"
            newRevision.userProperties = properties
            true
        }

        // Delete a document.
        document.delete()

    }
}
