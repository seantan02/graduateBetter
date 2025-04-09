import { useState } from 'react';
import { Alert } from 'react-bootstrap';

const AdvisorAlert: React.FC = () => {
  const [showImportantInfo, setShowImportantInfo] = useState(true);
  const [showImportantNotice, setShowImportantNotice] = useState(true);

  return (
    <>
    {
      showImportantInfo &&
      <Alert variant="info" className="mb-4" onClose={() => setShowImportantInfo(false)} dismissible>
        <Alert.Heading>Important Information</Alert.Heading>
        <p>
          <strong>Welcome to Graduate Better where we aim to help you better figure out your way to graduate quicker, easier and better here at UW-Madison!</strong><br></br>
          Start by selecting your desired majors and certificates below and submit it!
        </p>
      </Alert>
    }
    {
      showImportantNotice && 
      <Alert variant="warning" className="mb-4" onClose={() => setShowImportantNotice(false)} dismissible>
        <Alert.Heading>Important Notice</Alert.Heading>
        <p>
          <strong>Always double-check with your academic advisor</strong> before making decisions based on this tool. 
          This software provides suggestions, but may not account for all program-specific requirements or recent changes.
        </p>
      </Alert>
    }
    </>
  );
};

export default AdvisorAlert;