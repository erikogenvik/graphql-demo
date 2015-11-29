package com.jayway.kdag.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by erik on 2015-11-23.
 */
@RestController
@RequestMapping(value = "/graphql")
public class GraphQLResource {

    private List<Person> people = new ArrayList<>();

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
        GraphQL graphQL = new GraphQL(Schema.MainSchema);
        ExecutionResult result = graphQL.execute(query, people);
        return ResponseEntity.ok(result.getData());
    }
}
