import { Container, Row, Col } from 'react-bootstrap';
import './LoadingAnimation.css';

const LoadingAnimation: React.FC = () => {
  return (
    <Container className="loading-container my-5">
      <Row className="justify-content-center">
        <Col xs={12} className="text-center">
          <div className="badger-loader">
            <div className="badger-face">
              <div className="badger-ear-left"></div>
              <div className="badger-ear-right"></div>
              <div className="badger-head">
                <div className="badger-eye-left"></div>
                <div className="badger-eye-right"></div>
                <div className="badger-nose"></div>
                <div className="badger-stripe"></div>
              </div>
            </div>
            <p className="loading-text mt-3">Calculating your fastest path to graduation...</p>
          </div>
        </Col>
      </Row>
    </Container>
  );
};

export default LoadingAnimation;