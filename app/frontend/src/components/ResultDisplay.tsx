import { useState, useCallback, memo } from 'react';
import { Container, Row, Col, Card, Table, Form } from 'react-bootstrap';
// interfaces
import { ResultDisplayProps } from '../interfaces/Degree.tsx';

const ResultDisplay: React.FC<ResultDisplayProps> = memo(({ graduationPath }) => {
  console.log("Re-rendering");
  const [checkedCourses, setCheckedCourses] = useState<Set<string>>(new Set());

  // Function to handle checkbox change
  const handleCheckChange = useCallback((courseId: string) => {
    const newCheckedCourses = new Set(checkedCourses);
    
    if (newCheckedCourses.has(courseId)) {
      newCheckedCourses.delete(courseId);
    } else {
      newCheckedCourses.add(courseId);
    }
    
    setCheckedCourses(newCheckedCourses);
  }, [checkedCourses]);

  const uncheckedCourses = graduationPath.courses.filter(course => !checkedCourses.has(course.id));
  const checkedCoursesList = graduationPath.courses.filter(course => checkedCourses.has(course.id));

  return (
    <Container className="result-container mb-5">
      <Row className="justify-content-center">
        <Col xs={12} md={10}>
          <Card className="shadow">
            <Card.Header className="bg-success text-white">
              <h4 className="mb-0">Your Quickest Route to Graduation</h4>
              <p>The courses below <strong>DO NOT INCLUDE</strong> general studies courses such as ethic classes. 
                 Courses below are the major's specific courses which will satisfy each major you selected.</p>
            </Card.Header>
            <Card.Body>
              <Table responsive hover striped>
                <thead>
                  <tr>
                    <th>Course ID</th>
                    <th>Title</th>
                    <th className="text-center">Credits</th>
                    <th className="text-center">Completed</th>
                  </tr>
                </thead>
                <tbody>
                  {[...uncheckedCourses, ...checkedCoursesList].map(course => (
                    <tr 
                      key={course.id}
                      style={{ 
                        opacity: checkedCourses.has(course.id) ? 0.5 : 1,
                        backgroundColor: checkedCourses.has(course.id) ? '#f8f9fa' : undefined
                      }}
                    >
                      <td>{course.code}</td>
                      <td>{course.title}</td>
                      <td className="text-center">{course.credits}</td>
                      <td className="text-center">
                        <Form.Check 
                          type="checkbox" 
                          id={`course-${course.id}`}
                          checked={checkedCourses.has(course.id)}
                          onChange={() => handleCheckChange(course.id)}
                          label=""
                        />
                      </td>
                    </tr>
                  ))}
                </tbody>
                <tfoot>
                  <tr className="table-active fw-bold">
                    <td colSpan={2} className="text-end">Total:</td>
                    <td className="text-center">{graduationPath.totalCredits} credits</td>
                    <td></td>
                  </tr>
                  <tr className="table-active">
                    <td colSpan={2} className="text-end">Total Courses:</td>
                    <td className="text-center">{graduationPath.courses.length}</td>
                    <td></td>
                  </tr>
                  <tr className="table-active">
                    <td colSpan={2} className="text-end">Completed Courses:</td>
                    <td className="text-center">{checkedCourses.size}</td>
                    <td></td>
                  </tr>
                  <tr className="table-active">
                    <td colSpan={2} className="text-end">Remaining Courses:</td>
                    <td className="text-center">{graduationPath.courses.length - checkedCourses.size}</td>
                    <td></td>
                  </tr>
                </tfoot>
              </Table>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
});

export default ResultDisplay;