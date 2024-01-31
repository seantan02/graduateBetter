import React, { useState, useEffect } from 'react';
import TextField from '@mui/material/TextField';
import Checkbox from '@mui/material/Checkbox';
import FormControlLabel from '@mui/material/FormControlLabel';
import Box from '@mui/material/Box';
import IconButton from '@mui/material/IconButton';
import ClearIcon from '@mui/icons-material/Clear';
import './style/styles.css';
import '@fontsource/roboto'; // Import the Roboto font


const CheckedListComponent = ({title, availableItems, onCheckedItemsChange}) => {
  const [checkedItems, setCheckedItems] = useState([]);
  const [filteredItems, setFilteredItems] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  const sortedAvailableMajors = [...availableItems].sort((a,b) =>a.localeCompare(b));
 // Handle checkbox change
  const handleCheckboxChange = majorName => {
    if (checkedItems.includes(majorName)) {
      setCheckedItems([...checkedItems.filter(item=>item!==majorName)]);
    } else {
      setCheckedItems([...checkedItems, majorName].sort((a,b) => a.localeCompare(b)));
    }
  };
  const searchString = "Search for " + title;

  useEffect(() => {
    setFilteredItems([...availableItems].sort((a,b) => a.localeCompare(b)));
  },[availableItems])

  const handleClearClick = () =>{
    setSearchTerm('');
    setFilteredItems([...availableItems].sort((a,b) => a.localeCompare(b)));
  }

  const handleSearchTermChange = (updatedSearchTerm) => {
    setSearchTerm(updatedSearchTerm);
    console.log(searchTerm, updatedSearchTerm);
    setFilteredItems(sortedAvailableMajors.filter(item => item.toLowerCase().includes(updatedSearchTerm.toLowerCase())));
  }

  useEffect(() => {
    onCheckedItemsChange(checkedItems);
  },[checkedItems, onCheckedItemsChange])

  return(
    <div className="col800px">
      <h3 className="noMargin">Checked {title}</h3>
      <div className="checkboxContainer">
        {checkedItems.length !== 0 && checkedItems.map(item => (
        <Box key={item}>
          <FormControlLabel
            control={
              <Checkbox
                checked={checkedItems.includes(item)}
                onChange={() => handleCheckboxChange(item)}
              />
            }
            label={item}
          />
        </Box>
        ))}
        {
          checkedItems.length===0 && 
        <Box key="None">
          <FormControlLabel
            control={
              <Checkbox
                checked={true}
                onChange={() => {}}
              />
            }
            label="None"
          />
        </Box>
        }
      </div>
      {/* SEARCH BAR*/}
      <div className="searchContainer">
        <TextField
          label={searchString}
          variant="outlined"
          fullWidth
          value={searchTerm}
          onChange={e => handleSearchTermChange(e.target.value)}
        />
       {/* Clear button */}
          <IconButton className="clearButton" onClick={handleClearClick}>
            <ClearIcon />
          </IconButton>
      </div>
      <div className="checkboxContainer">
      {/* Checkboxes */}
      {filteredItems.map(item => (
        <Box key={item}>
          <FormControlLabel
            control={
              <Checkbox
                checked={checkedItems.includes(item)}
                onChange={() => handleCheckboxChange(item)}
              />
            }
            label={item}
          />
        </Box>
      ))}
      </div>
    </div>
  )
}

export default CheckedListComponent;
