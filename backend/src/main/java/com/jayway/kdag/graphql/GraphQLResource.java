package com.jayway.kdag.graphql;

import com.couchbase.lite.CouchbaseLiteException;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;


@RestController
@RequestMapping(value = "/graphql", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GraphQLResource {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DataStore dataStore = new DataStore();
    private final Schema schema = new Schema();
    private final GraphQL graphQL = new GraphQL(schema.mainSchema);
    private final Persons persons = new Persons(dataStore);

    public GraphQLResource() throws IllegalAccessException, NoSuchMethodException, InstantiationException, IOException, CouchbaseLiteException {
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity query(@RequestBody String query) {
        ExecutionResult result = graphQL.execute(query, persons);

        result.getErrors().forEach(graphQLError -> log.error("Error in query: {}", graphQLError.getMessage()));
        return ResponseEntity.ok(result.getData());
    }
}
