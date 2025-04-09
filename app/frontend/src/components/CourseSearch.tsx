import { useState, useEffect, useCallback, useRef } from 'react';
import { Form, Badge, CloseButton } from 'react-bootstrap';

import { Course, CourseSearchProps } from '../interfaces/Course.tsx'
import { API_URL } from '../utils/env.ts';

const CourseSearch: React.FC<CourseSearchProps> = ({ selectedCourses, setSelectedCourses, title }) => {
  const [availableCourses, setAvailableCourses] = useState<Course[]>([]);
  const [searchText, setSearchText] = useState('');
  const [suggestions, setSuggestions] = useState<Course[]>([]);
  const timeoutIdRef = useRef<NodeJS.Timeout>(null);
  
  const debounceSearch = useCallback((searchKey: string, delay: number) => {
      if (timeoutIdRef.current) {
        clearTimeout(timeoutIdRef.current);
      }
      
      timeoutIdRef.current = setTimeout(async () => {
        if (searchKey.trim() === '') {
          setSuggestions([]);
          return;
        }
        
        try {
          const res = await fetch(`${API_URL}/courses/search?searchKey=${searchKey}`, {
            method: "GET"
          });
          const data = await res.json();
          setAvailableCourses(data);
          
          // Filter after getting new data
          const filteredCourses = data.filter(
            (course: Course) => !selectedCourses.some(selected => selected.id === course.id)
          );
          setSuggestions(filteredCourses);
        } catch (error) {
          console.error("Error searching courses:", error);
          setSuggestions([]);
        }
      }, delay);
    },
    [API_URL, selectedCourses] // Only depend on these values
  );

  useEffect(() => {
    debounceSearch(searchText, 1000);

    // Cleanup timeout on unmount
    return () => {
      if (timeoutIdRef.current) {
        clearTimeout(timeoutIdRef.current);
      }
    };
  }, [searchText, debounceSearch]);
  
  const handleSelectCourse = (course: Course) => {
    setSelectedCourses([...selectedCourses, course]);

    setSuggestions(suggestions.filter(suggestion => suggestion.id !== course.id));
  };
  
  const handleRemoveCourse = (courseId: string) => {
    setSelectedCourses(selectedCourses.filter(course => course.id !== courseId));

    // If search text is not empty, update suggestions to include removed major if it matches search
    if (searchText.trim() !== '') {
      const removedMajor = availableCourses.find(course => course.id === courseId);
      if (removedMajor && removedMajor.code.toLowerCase().includes(searchText.toLowerCase())) {
        setSuggestions([...suggestions, removedMajor]);
      }
    }
  };
  
  return (
    <div className="course-search mb-4">
      <Form.Group>
        <Form.Label className="fw-bold">{title}</Form.Label>
        <div className="position-relative">
          <div className="selected-items p-2 border rounded mb-1 d-flex flex-wrap align-items-center">
            {selectedCourses.map(course => (
              <Badge 
                key={course.id} 
                bg="success" 
                className="me-2 mb-1 d-flex align-items-center p-2"
              >
                {course.code}
                <CloseButton 
                  variant="white" 
                  onClick={() => handleRemoveCourse(course.id)} 
                  className="ms-2"
                  style={{ fontSize: '0.65rem' }}
                />
              </Badge>
            ))}
            <Form.Control
              type="text"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              placeholder={selectedCourses.length ? "" : "Search by smallest prefix: e.g. COMP SCI 571 (not MATH 571), BIOLOGY 101"}
              className={selectedCourses.length ? "flex-grow-1 border-0" : ""}
              style={selectedCourses.length ? { width: 'auto', minWidth: '100px' } : {}}
            />
          </div>
          
          {suggestions.length > 0 && (
            <div className="suggestions position-absolute w-100 border rounded shadow-sm bg-white z-10">
              {suggestions.map(course => (
                <div 
                  key={course.id}
                  className="suggestion p-2 border-bottom cursor-pointer"
                  onClick={() => handleSelectCourse(course)}
                  style={{ cursor: 'pointer' }}
                >
                  {course.code}
                </div>
              ))}
            </div>
          )}
        </div>
      </Form.Group>
    </div>
  );
};

export default CourseSearch;