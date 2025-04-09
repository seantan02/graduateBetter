import React, { useState } from 'react';
import { Modal, Button, Form, Row, Col, Image, Alert, Spinner } from 'react-bootstrap';
// assets
import logo from '../assets/images/studio-ghiblie-badger.jpg';

interface VerificationFormProps {
  show: boolean;
  onHide: () => void;
  handleRequestVerification: (email: string) => Promise<boolean>;
  handleVerification: (email: string, code?: string) => Promise<boolean>;
}

const VerificationForm: React.FC<VerificationFormProps> = ({ show, onHide, handleRequestVerification, handleVerification }) => {
  const [email, setEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [step, setStep] = useState<'email' | 'verification'>('email');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'danger'; text: string } | null>(null);

  const handleRequestCode = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);
    
    try {
      const success = await handleRequestVerification(email);
      if (success) {
        setStep('verification');
        setMessage({ type: 'success', text: 'Verification code sent to your email!' });
      } else {
        setMessage({ type: 'danger', text: 'Failed to send verification code. Please try again.' });
      }
    } catch (error) {
      setMessage({ type: 'danger', text: 'An error occurred. Please try again later.' });
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyCode = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);
    
    try {
      const success = await handleVerification(email, verificationCode);
      if (success) {
        setMessage({ type: 'success', text: 'Email verification successful!' });
        // Optionally close the modal or redirect after successful verification
        setTimeout(() => onHide(), 1500);
      } else {
        setMessage({ type: 'danger', text: 'Invalid verification code. Please try again.' });
      }
    } catch (error) {
      setMessage({ type: 'danger', text: 'An error occurred. Please try again later.' });
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    if (step === 'verification') {
      setStep('email');
      setVerificationCode('');
      setMessage(null);
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered size="lg">
      <Modal.Body>
        <Row className="g-0">
          <Col
            md={6}
            className="d-flex flex-column align-items-center justify-content-center p-4 border-end"
          >
            <Image
              src={logo}
              alt="Logo"
              fluid
              className="mb-3"
              style={{ maxWidth: '100%', height: 'auto', maxHeight: '200px' }}
            />
            <p className="text-muted text-center">
              Welcome! Graduate Better is a project initiated by Sean Tan (a current graduating junior here at UW-Madison), aimed to help
              students here at UW-Madison to more efficiently figure out the easiest way to complete their multi-majoring or multi-minoring dream.
            </p>
          </Col>
          <Col xs={12} md={6} className="p-4">
            <h5 className="mb-4 text-center">WISC Student Verification</h5>
            
            {message && (
              <Alert variant={message.type} className="mb-3">
                {message.text}
              </Alert>
            )}
            
            {step === 'email' ? (
              <Form onSubmit={handleRequestCode}>
                <Form.Group controlId="formEmail" className="mb-3">
                  <Form.Label>WISC Email address</Form.Label>
                  <Form.Control
                    type="email"
                    placeholder="Enter your WISC email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    disabled={loading}
                  />
                </Form.Group>
                <div className="d-grid">
                  <Button variant="primary" type="submit" disabled={loading}>
                    {loading ? (
                      <>
                        <Spinner as="span" animation="border" size="sm" className="me-2" />
                        Sending...
                      </>
                    ) : (
                      'Request Code'
                    )}
                  </Button>
                </div>
              </Form>
            ) : (
              <Form onSubmit={handleVerifyCode}>
                <Form.Group controlId="formEmail" className="mb-3">
                  <Form.Label>Email address</Form.Label>
                  <div className="d-flex">
                    <Form.Control
                      type="email"
                      value={email}
                      disabled
                      className="me-2"
                    />
                    <Button 
                      variant="outline-secondary" 
                      onClick={resetForm} 
                      size="sm"
                      style={{ whiteSpace: 'nowrap' }}
                    >
                      Change
                    </Button>
                  </div>
                </Form.Group>
                
                <Form.Group controlId="formVerificationCode" className="mb-3">
                  <Form.Label>Verification Code</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Enter verification code"
                    value={verificationCode}
                    onChange={(e) => setVerificationCode(e.target.value)}
                    required
                    disabled={loading}
                  />
                </Form.Group>
                
                <div className="d-grid">
                  <Button variant="primary" type="submit" disabled={loading}>
                    {loading ? (
                      <>
                        <Spinner as="span" animation="border" size="sm" className="me-2" />
                        Verifying...
                      </>
                    ) : (
                      'Verify Email'
                    )}
                  </Button>
                </div>
              </Form>
            )}
          </Col>
        </Row>
      </Modal.Body>
    </Modal>
  );
};

export default VerificationForm;