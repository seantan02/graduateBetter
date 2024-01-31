import React, { useState, useEffect } from 'react';
import TextField from '@mui/material/TextField';
import Checkbox from '@mui/material/Checkbox';
import FormControlLabel from '@mui/material/FormControlLabel';
import Box from '@mui/material/Box';
import IconButton from '@mui/material/IconButton';
import ClearIcon from '@mui/icons-material/Clear';
import './style/styles.css';


const MajorComponent = ({availableMajors, onCheckedItemsChange}) => {
  const [checkedMajors, setCheckedMajors] = useState([]);
  const [filteredMajors, setFilteredMajors] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  const sortedAvailableMajors = [...availableMajors].sort((a,b) =>a.localeCompare(b));
 // Handle checkbox change
  const handleCheckboxChange = majorName => {
    if (checkedMajors.includes(majorName)) {
      setCheckedMajors([...checkedMajors.filter(item=>item!==majorName)]);
    } else {
      setCheckedMajors([...checkedMajors, majorName].sort((a,b) => a.localeCompare(b)));
    }
  };

  useEffect(() => {
    setFilteredMajors([...availableMajors].sort((a,b) => a.localeCompare(b)));
  },[availableMajors])

  const handleClearClick = () =>{
    setSearchTerm('');
    setFilteredMajors([...availableMajors].sort((a,b) => a.localeCompare(b)));
  }

  const handleSearchTermChange = (updatedSearchTerm) => {
    setSearchTerm(updatedSearchTerm);
    console.log(searchTerm, updatedSearchTerm);
    setFilteredMajors(sortedAvailableMajors.filter(item => item.toLowerCase().includes(updatedSearchTerm.toLowerCase())));
  }

  useEffect(() => {
    onCheckedItemsChange(checkedMajors);
  },[checkedMajors, onCheckedItemsChange])

  return(
    <div  id="MajorComponent">
    <div className="col800px">
      <h2>Checked Majors</h2>
      <div className="checkboxContainer">
        {checkedMajors.map(item => (
        <Box key={item}>
          <FormControlLabel
            control={
              <Checkbox
                checked={checkedMajors.includes(item)}
                onChange={() => handleCheckboxChange(item)}
              />
            }
            label={item}
          />
        </Box>
        ))}
      </div>
      {/* SEARCH BAR*/}
      <h2>All Majors</h2>
      <div className="searchContainer">
        <TextField
          label="Search for majors"
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
      {filteredMajors.map(item => (
        <Box key={item}>
          <FormControlLabel
            control={
              <Checkbox
                checked={checkedMajors.includes(item)}
                onChange={() => handleCheckboxChange(item)}
              />
            }
            label={item}
          />
        </Box>
      ))}
      </div>
    </div>
    </div>
  )
}

export default MajorComponent;
