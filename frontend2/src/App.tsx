import React, {Component} from "react";
import './App.css';
import ApolloClient from "apollo-client/ApolloClient";
import {ApolloProvider, Mutation, Query} from 'react-apollo';
import {InMemoryCache} from "apollo-cache-inmemory";
import {createHttpLink} from "apollo-link-http";
import gql from "graphql-tag";


const client = new ApolloClient({
    link: createHttpLink({uri: 'http://localhost:8080/graphql/apollo'}),
    cache: new InMemoryCache()
});


const GET_PEOPLE = gql`
    {
        people {
            id
            firstName
            lastName
        }
    }
`;

const CREATE_PERSON = gql`
    mutation createPerson($firstName: String!, $lastName: String!){
        createPerson(firstName: $firstName, lastName: $lastName) {
            id
            firstName
            lastName
        }
    }
`;

interface Data {
    people: Array<{ id: string; firstName: string, lastName: string }>;
}

interface Variables {
    firstName: string;
    lastName: string
}

class App extends Component<{}, Variables> {

    render() {
        return (
            <ApolloProvider client={client}>
                <div className="App">
                    <header className="App-header">
                        <Mutation<any, Variables> mutation={CREATE_PERSON}>
                            {(createPerson) => (
                                <div>
                                    First name: <input type="text"
                                                       onChange={event => this.setState({firstName: event.target.value})}/>
                                    <br/>
                                    Last name: <input type="text"
                                                      onChange={event => this.setState({lastName: event.target.value})}/>
                                    <br/>
                                    <button onClick={event => createPerson({variables: this.state})}>Create new</button>
                                </div>
                            )}
                        </Mutation>
                        <Query<Data> query={GET_PEOPLE}>
                            {({loading, error, data}) => {
                                if (loading) return "Loading...";
                                if (error) return `Error! ${error.message}`;
                                if (!data) return 'No data';

                                return (<ul>{data.people.map((person => {
                                    return (<li>{person.firstName} {person.lastName}</li>)
                                }))}</ul>);
                            }}
                        </Query>
                    </header>
                </div>
            </ApolloProvider>
        );
    }


}

export default App;
