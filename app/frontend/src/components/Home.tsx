import { useCallback, useState, useRef } from 'react';
import { Container, Button, Row, Col } from 'react-bootstrap';
//components
import MajorSearch from './MajorSearch';
import CourseSearch from './CourseSearch';
import LoadingAnimation from './LoadingAnimation';
import ResultDisplay from './ResultDisplay';
import AdvisorAlert from './AdvisorAlert';
// interfaces
import { Course, CourseResult } from '../interfaces/Course.tsx'
import { Major } from '../interfaces/Major.tsx'
import { GraduationPath } from '../interfaces/Degree.tsx'
import { API_URL } from '../utils/env.ts';
import VerificationForm from '../components/VerificationForm.tsx';
// utils
import { handleRequestVerification, handleVerification, isLoggedIn } from "../utils/auth.ts"

function Home() {
  const [showLogin, setShowLogin] = useState(false);
  const [selectedMajors, setSelectedMajors] = useState<Major[]>([]);
  const [selectedCompletedCourses, setSelectedCompletedCourses] = useState<Course[]>([]);
  const [selectedIntendedCourses, setSelectedIntendedCourses] = useState<Course[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [graduationPath, setGraduationPath] = useState<GraduationPath | null>(null);
  // optimization to prevent unneccesary API calls
  const prevInputsRef = useRef<{ majors: Major[] | null; completedCourses: Course[] | null; intendedCourses: Course[] | null }>({
    majors: null,
    completedCourses: null,
    intendedCourses: null,
  });
  const cachedResultRef = useRef<GraduationPath>(null);

  const sendDegreeRouteRequest = useCallback(async (): Promise<void> => {
    let graduationCourseList: CourseResult[] = []
    let totalCredits: number = 0;
    const inputsChanged = (
      prevInputsRef.current.majors !== selectedMajors 
      || prevInputsRef.current.completedCourses !== selectedCompletedCourses 
      || prevInputsRef.current.intendedCourses !== selectedIntendedCourses
    )

    if(!inputsChanged){
      setGraduationPath(cachedResultRef.current);
      return;
    }

    const res = await fetch(`${API_URL}/degree/shortest-route`,{
      method: "POST",
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/plain',
        'Authorization': `Bearer ${localStorage.getItem("accessToken")}`
      },
      body: JSON.stringify({
        "majorIds": selectedMajors.map(m => m.id),
        "courseTakenIds": [...selectedCompletedCourses.map(c => c.id), ...selectedIntendedCourses.map(c => c.id)]
      })
    });

    if(![200, 201, 202].includes(res.status)){
      alert("Selected majors computation were too costly, please try selecting less majors or different majors. We are working on better performance!")
      return;
    }

    const data = await res.json();
  
    if(!data || data.length == 0){
      console.log("Degree Route Request: data received is null or length of 0!");
      return;
    }

    // We want to ignore classes that contains "++" or "--"
    var selectedCompletedCoursesId: string[] = selectedCompletedCourses.map(c => c.id);
    for(let i = 0; i < data.length; i++){
      let course: Course = data[i];
      if(course.code.includes("++") || course.code.includes("--")){
        continue;
      }
      // ignore ones that are completed
      if(selectedCompletedCoursesId.includes(course.id)){
        continue;
      }

      graduationCourseList.push({
        id: course.id,
        code: course.code,
        title: course.title,
        credits: course.maxCredits
      });
      totalCredits += course.minCredits;
    }
    // set it when we're done
    graduationCourseList.sort((a, b) => {
      let courseAPieces: string[] = a.code.split(" ");
      let courseANumber: number = parseInt(courseAPieces[courseAPieces.length-1]);
      let courseBPieces: string[] = b.code.split(" ");
      let courseBNumber: number = parseInt(courseBPieces[courseBPieces.length-1]);

      return courseANumber - courseBNumber;
    });

    // set references
    prevInputsRef.current.majors = selectedMajors;
    prevInputsRef.current.completedCourses = selectedCompletedCourses;
    prevInputsRef.current.intendedCourses = selectedIntendedCourses;
    cachedResultRef.current = ({
      courses: graduationCourseList,
      totalCredits: totalCredits
    });

    setGraduationPath({
      courses: graduationCourseList,
      totalCredits: totalCredits
    });
  }, [selectedMajors, selectedCompletedCourses, selectedIntendedCourses])

  const handleFindRoute = async () => {
      if(await isLoggedIn()){
        setIsLoading(true);
        setGraduationPath(null);
        
        // Simulate API call
        setTimeout(async () => {
          // send POST request
          sendDegreeRouteRequest();
          setIsLoading(false);
        }, 2000);
        return;
      }
      // not logged in
      setShowLogin(true);
  };

  return (
    <div className="app">
      <Container className="mt-4">
        <AdvisorAlert />
        
        <Row className="justify-content-center mb-4">
          <Col xs={12} md={10} lg={8}>
            <MajorSearch 
              selectedMajors={selectedMajors} 
              setSelectedMajors={setSelectedMajors} 
            />
          </Col>
        </Row>
        
        {selectedMajors.length > 0 && (
          <Row className="justify-content-center mb-4">
            <Col xs={12} md={10} lg={8}>
              <CourseSearch 
                selectedCourses={selectedCompletedCourses} 
                setSelectedCourses={setSelectedCompletedCourses} 
                title="Any courses you have completed?"
              />
            </Col>
          </Row>
        )}

        {selectedMajors.length > 0 && (
          <Row className="justify-content-center mb-4">
            <Col xs={12} md={10} lg={8}>
              <CourseSearch 
                selectedCourses={selectedIntendedCourses}
                setSelectedCourses={setSelectedIntendedCourses}
                title="Any courses you want to do for the majors/ certificates?"
              />
            </Col>
          </Row>
        )}
        
        {selectedMajors.length > 0 && (
          <Row className="justify-content-center mb-5">
            <Col xs={12} className="text-center">
              <Button 
                variant="primary" 
                size="lg" 
                onClick={handleFindRoute}
                disabled={isLoading}
              >
                Find me the quickest route to graduate!
              </Button>
            </Col>
          </Row>
        )}
        
        {isLoading && <LoadingAnimation />}
        
        {graduationPath && <ResultDisplay graduationPath={graduationPath} />}
      </Container>

      <VerificationForm
        show={showLogin}
        onHide={() => setShowLogin(false)}
        handleRequestVerification={handleRequestVerification}
        handleVerification={handleVerification}
      />
    </div>
  );
}

export default Home;