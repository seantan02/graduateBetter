import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';
// components
import NavbarComponent from './components/Navbar';
import Home from './components/Home';
import AboutMe from './components/AboutMe';


function App() {
  return (
    <div className="app">
      <Router>
        <NavbarComponent />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/about" element={<AboutMe />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;