require('normalize.css');
require('styles/App.css');

import React from 'react';
import People from './People';
import NewPerson from './NewPerson';


class AppComponent extends React.Component {
  render() {
    return (
      <div className="index">
        <People />
        <NewPerson />
      </div>
    );
  }
}

AppComponent.defaultProps = {
};

export default AppComponent;
