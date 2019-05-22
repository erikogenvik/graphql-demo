package com.jayway.kdag.graphql

import com.couchbase.lite.CouchbaseLiteException
import graphql.ExecutionResult
import graphql.GraphQL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.io.IOException
import java.lang.invoke.MethodHandles


@RestController
@RequestMapping(value = ["/graphql"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class GraphQLResource @Throws(IllegalAccessException::class, NoSuchMethodException::class, InstantiationException::class, IOException::class, CouchbaseLiteException::class)
constructor() {

    private val dataStore = DataStore()
    private val schema = Schema()
    private val graphQL = GraphQL(schema.mainSchema)
    private val persons = Persons(dataStore)

    @CrossOrigin(origins = ["http://localhost:8000"])
    @RequestMapping(method = [RequestMethod.POST])
    internal fun query(@RequestBody query: String): ResponseEntity<*> {
        val result = graphQL.execute(query, persons)

        result.errors.forEach { graphQLError -> log.error("Error in query: {}", graphQLError.message) }
        return ResponseEntity.ok(result.data)
    }

    companion object {

        private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}
