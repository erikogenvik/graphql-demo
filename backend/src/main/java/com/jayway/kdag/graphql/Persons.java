package com.jayway.kdag.graphql;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.QueryEnumerator;
import javaslang.collection.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Persons implements PersonMutation, PersonQuery {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DataStore dataStore;

    Persons(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    private static Person toPerson(Document document) {
        Person person = new Person();
        person.id = document.getId();
        person.firstName = (String) document.getProperty("firstName");
        person.lastName = (String) document.getProperty("lastName");
        return person;
    }

    @Override
    public Person createPerson(String firstName, String lastName) {
        try {
            // Create a new document (i.e. a record) in the database.
            final Document document = dataStore.getDatabase().createDocument();
            final Map<String, Object> properties = new HashMap<>();
            properties.put("firstName", firstName);
            properties.put("lastName", lastName);
            document.putProperties(properties);

            final Person person = new Person();
            person.firstName = firstName;
            person.lastName = lastName;
            person.id = document.getId();
            return person;
        } catch (CouchbaseLiteException e) {
            log.error("Error when saving to database.", e);
            return null;
        }
    }

    @Override
    public List<Person> people() {
        try {
            final QueryEnumerator result = dataStore.getDatabase().createAllDocumentsQuery().run();

            return Stream.ofAll(result).map(queryRow -> toPerson(queryRow.getDocument())).toJavaList();
        } catch (CouchbaseLiteException e) {
            log.error("Error when getting from database.", e);
            return null;
        }
    }
}
