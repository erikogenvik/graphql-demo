import React, {Component} from "react";
import './App.css';
import ApolloClient from "apollo-client/ApolloClient";
import {ApolloProvider, Mutation, Query} from 'react-apollo';
import {InMemoryCache} from "apollo-cache-inmemory";
//import {createHttpLink} from "apollo-link-http";
import gql from "graphql-tag";
import {WebSocketLink} from "apollo-link-ws";


//const httpLink = createHttpLink({uri: 'http://localhost:8080/graphql/apollo'});

const wsLink = new WebSocketLink({
    uri: `ws://localhost:8080/ws/graphql`,
    options: {
        reconnect: true
    }
});


const client = new ApolloClient({
    link: wsLink,
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

const SUBSCRIBE_NEW_PERSON = gql`
    subscription subscribeNew{
        subscribeNew {
            id
            firstName
            lastName
        }
    }
`;

interface Person {
    id: string;
    firstName: string,
    lastName: string
}

interface Data {
    people: Array<Person>;
}

interface Variables {
    firstName: string;
    lastName: string
}

class App extends Component<{}, Variables> {

    subscriber?: () => void;

    componentWillUnmount(): void {
        if (this.subscriber) {
            this.subscriber()
        }
    }

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
                            {({loading, error, data, subscribeToMore}) => {
                                if (loading) return "Loading...";
                                if (error) return `Error! ${error.message}`;
                                if (!data) return 'No data';

                                if (!this.subscriber) {
                                    this.subscriber = subscribeToMore<{ subscribeNew: Person }>({
                                        document: SUBSCRIBE_NEW_PERSON,
                                        updateQuery: (prev, {subscriptionData}) => {
                                            if (!subscriptionData.data || !subscriptionData.data.subscribeNew)
                                                return prev;
                                            if (!prev) {
                                                return {people: [subscriptionData.data.subscribeNew]};
                                            }
                                            const newItem = subscriptionData.data.subscribeNew;

                                            return Object.assign({}, prev, {
                                                people: [newItem, ...prev.people]
                                            });
                                        }
                                    });
                                }

                                return (<ul>{data.people.map((person => {
                                    return (<li key={person.id}>{person.firstName} {person.lastName}</li>)
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
