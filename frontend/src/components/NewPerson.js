import React from 'react';
import PeopleActions from '../actions/PeopleActions';

class NewPerson extends React.Component {

  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div>
        First name: <input type="text" onChange={event => this.setState({firstName: event.target.value})} />
        <br/>
        Last name: <input type="text" onChange={event => this.setState({lastName: event.target.value})} />
        <br/>
        <button onClick={event => PeopleActions.createPerson(this.state)}>Create new</button>
      </div>
    );
  }
}

export default NewPerson;
