package com.jayway.kdag.graphql

import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.Document
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.FluxSink
import java.lang.invoke.MethodHandles
import java.util.*

@Component
class Persons internal constructor(private val dataStore: DataStore) : PersonMutation, PersonQuery {

    val sinks = mutableListOf<FluxSink<Person>>()

    override fun createPerson(firstName: String, lastName: String): Person? {
        return try {
            // Create a new document (i.e. a record) in the database.
            val document = dataStore.database.createDocument()
            val properties = HashMap<String, Any>()
            properties["firstName"] = firstName
            properties["lastName"] = lastName
            document.putProperties(properties)

            val person = Person(document.id, firstName, lastName)
            sinks.forEach { it.next(person) }
            person
        } catch (e: CouchbaseLiteException) {
            log.error("Error when saving to database.", e)
            null
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
        return try {
            val result = dataStore.database.createAllDocumentsQuery().run()

            result.map { queryRow -> toPerson(queryRow.document) }
        } catch (e: CouchbaseLiteException) {
            log.error("Error when getting from database.", e)
            null
        }

    }

    fun subscribeNew(): Publisher<Person> {
        val processor = DirectProcessor.create<Person>()
        sinks.add(processor.sink())
        return processor
    }


    companion object {

        private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

        private fun toPerson(document: Document): Person {
            return Person(document.id, document.getProperty("firstName") as String, document.getProperty("lastName") as String)
        }
    }
}
