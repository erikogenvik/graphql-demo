curl -H "Content-Type: application/json" -X POST -d "{people {firstName}}"  localhost:8080/graphql

curl -H "Content-Type: application/json" -X POST -d "mutation M {createPerson(firstName: \"first\", lastName: \"last\") {firstName, lastName}}"  localhost:8080/graphql
