package com.jayway.kdag.graphql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Persons implements PersonMutation, PersonQuery {

    private final List<Person> people;

    Persons(List<Person> people) {
        this.people = people;
    }

    @Override
    public Person createPerson(String firstName, String lastName) {
        Person person = new Person();
        person.firstName = firstName;
        person.lastName = lastName;
        person.id = UUID.randomUUID().toString();
        people.add(person);
        return person;
    }

    @Override
    public List<Person> people() {
        return new ArrayList<>(people);
    }
}
