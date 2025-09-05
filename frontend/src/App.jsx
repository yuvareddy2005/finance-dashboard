import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import { LoginPage, RegisterPage, DashboardPage } from './pages/AppPages';
import Layout from './components/Layout'; // Import the new Layout

function App() {
  const isAuthenticated = !!localStorage.getItem('token');

  // Styles for the public-facing pages (like the example you sent)
  const publicNavStyle = {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    padding: '1rem',
    gap: '2rem',
  };

  const navLinkStyle = {
    color: 'var(--text-dark)',
    textDecoration: 'none',
    fontSize: '1rem',
    fontWeight: '500',
    transition: 'color 0.2s ease-in-out',
  };

  // This is a simple way to add hover styles directly in the component
  const handleMouseOver = (e) => e.target.style.color = 'var(--primary-teal)';
  const handleMouseOut = (e) => e.target.style.color = 'var(--text-dark)';

  // If the user is authenticated, we show the main app layout
  if (isAuthenticated) {
    return (
      <Router>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" />} />
          <Route element={<Layout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            {/* We will add other protected pages like /portfolio here later */}
          </Route>
        </Routes>
      </Router>
    );
  }

  // If the user is NOT authenticated, we show the public-facing layout
  return (
    <Router>
      <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
        <header style={publicNavStyle}>
          <Link to="/features" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Features</Link>
          <Link to="/about" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>About Us</Link>
          <Link to="/login" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Login</Link>
          <Link to="/register" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Sign Up</Link>
        </header>

        <main style={{ flexGrow: 1, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            {/* Default to login page if no other route matches */}
            <Route path="*" element={<Navigate to="/login" />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;

