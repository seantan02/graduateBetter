// Footer.js
import React from 'react';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';

import mainBanner from './mainBanner.js';
const Footer = () => {
  return (
    <Paper
      elevation={3} // Add elevation for a shadow effect
      style={{ padding: '16px', textAlign: 'center' }}
    >
      <Typography variant="body2" color="textSecondary">
        © {new Date().getFullYear()} Graduate Better. All rights reserved.
      </Typography>
      <pre>{mainBanner}</pre>
    </Paper>
  );
};

export default Footer;
