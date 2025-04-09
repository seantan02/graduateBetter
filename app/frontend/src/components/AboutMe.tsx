import { Container, Row, Col, Card } from 'react-bootstrap';

const AboutMe = () => {
  return (
    <Container className="mt-5">
      <Row className="justify-content-center">
        <Col xs={12} md={10} lg={8}>
          <Card className="shadow-sm">
            <Card.Body>
              <h2 className="text-center mb-4">About Graduate Better</h2>
              
              <h4 className="mt-4">Our Mission</h4>
              <p>
                Graduate Better is designed to help students navigate their academic journey 
                with ease. We're committed to providing a tool that simplifies course planning, 
                helps you choose the right majors, and finds the most efficient path to graduation.
              </p>
              
              <h4 className="mt-4">How It Works</h4>
              <p>
                Simply select your majors, add the courses you've already completed, and specify 
                any courses you're particularly interested in taking. Our intelligent algorithm 
                will calculate the quickest route to graduation, helping you save time and make 
                informed decisions about your academic future.
              </p>
              
              <h4 className="mt-4">Our Team</h4>
              <p>
                Graduate Better was first thought of by the founder Sean Tan Siong Ann, who is currently a Junior
                here at UW-Madison. This idea was realized by mainly Sean but his friend Anton Cheng was also a major
                contributor to some of the crucial algorithms. This team will continue to grow and continue to deliver
                better product to the students of Madison, On Wisconsin!
              </p>
              
              <h4 className="mt-4">Contact Us</h4>
              <p>
                Have questions, suggestions, or feedback? We'd love to hear from you! 
                Reach out to us at <a href="mailto:graduatebetter@gmail.com">graduatebetter@gmail.com</a>.
              </p>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default AboutMe;