require('normalize.css');
require('styles/App.css');

import React from 'react';
import People from './People';


class AppComponent extends React.Component {
  render() {
    return (
      <div className="index">
        <People />
      </div>
    );
  }
}

AppComponent.defaultProps = {
};

export default AppComponent;
