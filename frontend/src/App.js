import React,  {useState, useEffect} from 'react';
import CheckedListComponent from './CheckedListComponent.js'
import About from './About.js';
import Footer from './Footer.js';
import Header from './Header.js';
import Button from '@mui/material/Button';
import '@fontsource/roboto'; // Import the Roboto font
import './App.css';
import './style/styles.css';


function App() {
  const [availableMajors, setAvailableMajors] = useState([]);
  const [availableClasses, setAvailableClasses] = useState([]);
  const [checkedMajors, setCheckedMajors] = useState([]);
  const [checkedClasses, setCheckedClasses] = useState([]);
  const [loadingPath, setLoadingPath] = useState(false);

  const [shortestPath, setShortestPath] = useState([]);
  const authToken = "";

  const handleCheckedMajorChange = (checkedItems) => {
    // Do something with the checked items in the parent component
    setCheckedMajors(checkedItems);
  };

  const handleCheckedClassesChange = (checkedItems) => {
    // Do something with the checked items in the parent component
    setCheckedClasses(checkedItems);
  };
  useEffect(()=>{
    fetch('/api/v1/degree/getAll', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + authToken,
      },
    }).then(response => response.json()).then(data => {
      // Populate the initial select element
      setAvailableMajors(data);
    }).catch(error => console.error('Error fetching degrees:', error));
    fetch('/api/v1/classes/getAll', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + authToken,
      },
    }).then(response => response.json()).then(data => {
      // Populate the initial select element
      setAvailableClasses(data);
    }).catch(error => console.error('Error fetching degrees:', error));
  }, []);
  const callShortestPath = () => {
    if(checkedMajors.size() < 2){
      setLoadingPath(true);
      // Make a POST request to getShortestPath
      fetch('/api/v1/degree/getShortestPath', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + authToken,
        },
        body: JSON.stringify({ degreeIds: checkedMajors }),
      })
        .then(response => response.json())
        .then(shortestPath => {
          // Display the shortest path in a nice format
          setShortestPath(shortestPath);

          // Hide loading spinner after response is received
          setLoadingPath(false);
        })
        .catch(error => {
          console.error('Error fetching shortest path:', error);

          // Hide loading spinner in case of an error
          setLoadingPath(false);
        });
    }
  }
  return (
    <div className="App">
    <Header />
    <div className="headerPadding" />
    <div className="bg1">
      <CheckedListComponent title="Majors" availableItems={availableMajors} onCheckedItemsChange={handleCheckedMajorChange} />
    </div>
    <div className="bg2">
      <CheckedListComponent title="Classes" availableItems={availableClasses} onCheckedItemsChange={handleCheckedClassesChange}  />
    </div>
    <div className="bg1">
      <div className="col800px">
        <Button variant='outlined' onClick={
          () => {
            callShortestPath();
          }
        }>Calculate!</Button> 
      </div>
    </div>
    <div className="bg2">
    <About />
    </div>
    <Footer />
    </div>
  );
}

export default App;
