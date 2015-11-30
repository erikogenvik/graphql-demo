import Reflux from 'reflux';
import PeopleActions from '../actions/PeopleActions';
import $ from 'jquery';

let PeopleStore = Reflux.createStore({
    listenables: PeopleActions,
    people: [],

    fetchAll: function() {
      $.ajax({
        url: 'http://localhost:8080/graphql',
        type: 'POST',
        processData: false,
        contentType: 'application/json',
        data: "{people{firstName, lastName}}",
        success: data => {
          this.people = data.people;
          this.trigger(this.people);
        }
      });
    },

    init: function() {
      this.fetchAll();
    }
});

export default PeopleStore;
