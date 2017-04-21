package com.jayway.kdag.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/graphql")
public class GraphQLResource {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<Person> people = new ArrayList<>();
    private final Schema schema = new Schema();
    private final GraphQL graphQL = new GraphQL(schema.mainSchema);
    private final Persons persons = new Persons(people);

    public GraphQLResource() throws IllegalAccessException, NoSuchMethodException, InstantiationException {
    }

    @PostConstruct
    void postConstruct() {
        Person person = new Person();
        person.firstName = "Erik";
        person.lastName = "Ogenvik";
        person.id = "erik_ogenvik";
        people.add(person);
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity query(@RequestBody String query) {


        ExecutionResult result = graphQL.execute(query, persons);

        result.getErrors().forEach(graphQLError -> log.error("Error in query.", graphQLError));
        return ResponseEntity.ok(result.getData());
    }
}
