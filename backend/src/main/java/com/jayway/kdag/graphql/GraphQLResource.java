package com.jayway.kdag.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by erik on 2015-11-23.
 */
@RestController
@RequestMapping(value = "/graphql")
public class GraphQLResource {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<Person> people = new ArrayList<>();
    private final Schema schema = new Schema();

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

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity query(@RequestBody String query) {
        GraphQL graphQL = new GraphQL(schema.mainSchema);
        Persons persons = new Persons(people);
        ExecutionResult result = graphQL.execute(query, persons);

        result.getErrors().forEach(graphQLError -> log.error("Error in query.", graphQLError));
        return ResponseEntity.ok(result.getData());
    }
}
