package com.jayway.kdag.graphql;

import com.couchbase.lite.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by erik on 2017-04-21.
 */
public class DataStore {
    Manager manager = new Manager(new JavaContext(), Manager.DEFAULT_OPTIONS);
    Database database = manager.getDatabase("people");

    public DataStore() throws IOException, CouchbaseLiteException {
    }

    public void setup() throws CouchbaseLiteException, IOException {

        // Create a new document (i.e. a record) in the database.
        Document document = database.createDocument();
        Map properties = new HashMap();
        properties.put("firstName", "John");
        document.putProperties(properties);


        // Update a document.
        document.update(new Document.DocumentUpdater() {
            @Override
            public boolean update(UnsavedRevision newRevision) {
                Map properties = newRevision.getUserProperties();
                properties.put("firstName", "Johnny");
                newRevision.setUserProperties(properties);
                return true;
            }
        });

        // Delete a document.
        document.delete();

    }

    Database getDatabase() {
        return database;
    }
}
