import logo from './logo.svg';
import React,  {useState} from 'react';
import MajorComponent from './MajorComponent.js'
import './App.css';

function App() {
   const [checkedItemsFromChild, setCheckedItemsFromChild] = useState([]);

  const handleCheckedItemsChange = (checkedItems) => {
    // Do something with the checked items in the parent component
    setCheckedItemsFromChild(checkedItems);
  };

  return (
    <div className="App">
    <div> <h1> Graduate Better </h1></div>
    <MajorComponent availableMajors={['Data Science','Computer Science', 'Psychology', 'Economics']} onCheckedItemsChange={handleCheckedItemsChange} />
    </div>
  );
}

export default App;
