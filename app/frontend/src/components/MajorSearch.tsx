import { useState, useEffect } from 'react';
import { Form, Badge, CloseButton } from 'react-bootstrap';

import { Major, MajorSearchProps } from '../interfaces/Major.tsx'
import { API_URL } from '../utils/env.ts';

const MajorSearch: React.FC<MajorSearchProps> = ({ selectedMajors, setSelectedMajors }) => {
  const [searchText, setSearchText] = useState('');
  const [suggestions, setSuggestions] = useState<Major[]>([]);
  const [availableMajors, setAvailableMajors] = useState<Major[]>([]);

  useEffect(() => {  // only gets called once
    const populateAvailableMajors = async () => {
      let lastReceivedMajorId: number = 0;
      let lastReceivedMajorsLength: number = 0;
      let majors: Array<Major> = [];
  
      while(true){
        const res = await fetch(`${API_URL}/majors?lastId=${lastReceivedMajorId}`, {
          method: "GET"
        });
        const data = await res.json();
        if(!data[0] || data.length == 0){
          break;
        }
        // add majors
        majors = majors.concat(data);

        lastReceivedMajorId = data[data.length-1]?.id;
        if(!lastReceivedMajorId){
          break;
        }
  
        lastReceivedMajorId = parseInt(data[data.length-1].id);
        if(lastReceivedMajorsLength > data.length){
          break;
        }
        lastReceivedMajorsLength = data.length;
      }
      setAvailableMajors(majors);
    }
    populateAvailableMajors();
  
  }, [API_URL]);

  
  useEffect(() => {
    if (searchText.trim() === '') {
      setSuggestions([]);
      return;
    }
    
    // Filter majors that match the search text and haven't been selected yet
    const filteredMajors = availableMajors.filter(
      major => 
        !selectedMajors.some(selected => selected.id === major.id) && 
        major.title.toLowerCase().includes(searchText.toLowerCase())
    );    
    setSuggestions(filteredMajors);
  }, [searchText, selectedMajors]);
  
  const handleSelectMajor = (major: Major) => {
    // check if it exceeds 3
    if(selectedMajors.length >= 3){
      alert("Due to limited computational resources, maximum number of majors are 3. Please select only 3 majors / certificates.");
      return;
    }
    // Add the selected major to the list
    setSelectedMajors([...selectedMajors, major]);
    
    // Important: Don't clear the search text - this keeps the search results visible
    // We just need to update the suggestions to remove the selected major
    setSuggestions(suggestions.filter(suggestion => suggestion.id !== major.id));
  };
  
  const handleRemoveMajor = (majorId: string) => {
    setSelectedMajors(selectedMajors.filter(major => major.id !== majorId));
    
    // If search text is not empty, update suggestions to include removed major if it matches search
    if (searchText.trim() !== '') {
      const removedMajor = availableMajors.find(major => major.id === majorId);
      if (removedMajor && removedMajor.title.toLowerCase().includes(searchText.toLowerCase())) {
        setSuggestions([...suggestions, removedMajor]);
      }
    }
  };
  
  return (
    <div className="major-search mb-4">
      <Form.Group>
        <Form.Label className="fw-bold">Select intended majors/ certificates</Form.Label>
        <div className="position-relative">
          <div className="selected-items p-2 border rounded mb-1 d-flex flex-wrap align-items-center">
            {selectedMajors.map(major => (
              <Badge 
                key={major.id} 
                bg="primary" 
                className="me-2 mb-1 d-flex align-items-center p-2"
              >
                {major.title}
                <CloseButton 
                  variant="white" 
                  onClick={() => handleRemoveMajor(major.id)} 
                  className="ms-2"
                  style={{ fontSize: '0.65rem' }}
                />
              </Badge>
            ))}
            <Form.Control
              type="text"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              placeholder={selectedMajors.length ? "" : "Type to search..."}
              className={selectedMajors.length ? "flex-grow-1 border-0" : ""}
              style={selectedMajors.length ? { width: 'auto', minWidth: '100px' } : {}}
            />
          </div>
          
          {suggestions.length > 0 && (
            <div className="suggestions position-absolute w-100 border rounded shadow-sm bg-white z-10">
              {suggestions.map(major => (
                <div 
                  key={major.id}
                  className="suggestion p-2 border-bottom cursor-pointer"
                  onClick={() => handleSelectMajor(major)}
                  style={{ cursor: 'pointer' }}
                >
                  {major.title}
                </div>
              ))}
            </div>
          )}
        </div>
      </Form.Group>
    </div>
  );
};

export default MajorSearch;