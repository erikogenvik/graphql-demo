package com.jayway.kdag.graphql

import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.Document
import com.couchbase.lite.QueryEnumerator
import javaslang.collection.Stream
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.invoke.MethodHandles
import java.util.HashMap

class Persons internal constructor(private val dataStore: DataStore) : PersonMutation, PersonQuery {

    override fun createPerson(firstName: String, lastName: String): Person? {
        try {
            // Create a new document (i.e. a record) in the database.
            val document = dataStore.database.createDocument()
            val properties = HashMap<String, Any>()
            properties["firstName"] = firstName
            properties["lastName"] = lastName
            document.putProperties(properties)

            val person = Person()
            person.firstName = firstName
            person.lastName = lastName
            person.id = document.id
            return person
        } catch (e: CouchbaseLiteException) {
            log.error("Error when saving to database.", e)
            return null
        }

    }

    override fun deletePerson(id: String): Boolean {
        try {
            val existingDocument = dataStore.database.getExistingDocument(id)
            if (existingDocument != null) {
                return existingDocument.delete()
            }
        } catch (e: CouchbaseLiteException) {
            log.error("Error when deleting from database.", e)
        }

        return false
    }

    override fun people(): List<Person>? {
        try {
            val result = dataStore.database.createAllDocumentsQuery().run()

            return result.map { queryRow -> toPerson(queryRow.getDocument()) }
        } catch (e: CouchbaseLiteException) {
            log.error("Error when getting from database.", e)
            return null
        }

    }

    companion object {

        private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

        private fun toPerson(document: Document): Person {
            val person = Person()
            person.id = document.id
            person.firstName = document.getProperty("firstName") as String
            person.lastName = document.getProperty("lastName") as String
            return person
        }
    }
}
