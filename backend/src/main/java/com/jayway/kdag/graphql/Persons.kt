package com.jayway.kdag.graphql

import com.couchbase.lite.Document
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.FluxSink
import java.lang.invoke.MethodHandles

@Component
class Persons internal constructor(private val dataStore: DataStore) : PersonMutation, PersonQuery {

    val sinks = mutableListOf<FluxSink<Person>>()

    override fun createPerson(firstName: String, lastName: String): Person {
        val document = dataStore.database.createDocument().putProperties(mapOf("firstName" to firstName, "lastName" to lastName))

        val person = Person(document.id, firstName, lastName)
        sinks.forEach { it.next(person) }
        return person

    }

    override fun deletePerson(id: String): Boolean {
        val existingDocument = dataStore.database.getExistingDocument(id)
        if (existingDocument != null) {
            return existingDocument.delete()
        }

        return false
    }

    override fun people(): List<Person>? {
        val result = dataStore.database.createAllDocumentsQuery().run()

        return result.map { queryRow -> toPerson(queryRow.document) }
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
