import React from 'react';
import PeopleStore from '../stores/PeopleStore';
import PeopleActions from '../actions/PeopleActions';

class People extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      people: []
    };
  }

  componentDidMount() {
    this.unsubscribe = PeopleStore.listen(people => this.setState({
      people: people
    }));
  }

  componentWillUnmount() {
    this.unsubscribe();
  }

  render() {
    return (
      <ul>
        {this.state.people.map(person => (
          <li>{person.firstName} {person.lastName} <button onClick={event => PeopleActions.deletePerson(person)}>Delete</button></li>
        ))}
      </ul>
    );
  }
}

export default People;
