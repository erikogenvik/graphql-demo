import Reflux from 'reflux';

let PeopleActions = Reflux.createActions([
  'fetchAll',
  'createPerson',
  'deletePerson'
]);

export default PeopleActions;
