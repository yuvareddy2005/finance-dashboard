import React from 'react';
import { Link } from 'react-router-dom';
import { Logo } from '../components/Logo';

// Styles for the landing page
const pageStyle = {
  backgroundColor: 'var(--background-dark)',
  color: 'var(--text-light)',
  minHeight: '100vh',
  padding: '0 2rem',
};

const navStyle = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  padding: '1.5rem 0',
  maxWidth: '1200px',
  margin: '0 auto',
};

const heroStyle = {
  textAlign: 'center',
  padding: '6rem 0',
  maxWidth: '800px',
  margin: '0 auto',
};

const headlineStyle = {
  fontSize: '3.5rem',
  fontWeight: 'bold',
  lineHeight: '1.2',
  marginBottom: '1.5rem',
};

const subheadlineStyle = {
  fontSize: '1.25rem',
  color: 'var(--text-dark)',
  marginBottom: '2.5rem',
};

const ctaContainerStyle = {
  display: 'flex',
  justifyContent: 'center',
  gap: '1rem',
};

const ctaButtonStyle = {
  padding: '1rem 2rem',
  borderRadius: '8px',
  border: 'none',
  fontSize: '1rem',
  fontWeight: 'bold',
  cursor: 'pointer',
  textDecoration: 'none',
  display: 'inline-block',
  backgroundColor: 'var(--primary-teal)',
  color: '#1A202C',
};

const secondaryButtonStyle = {
  ...ctaButtonStyle,
  backgroundColor: 'transparent',
  border: '1px solid var(--border-color)',
  color: 'var(--text-light)',
};

const LandingPage = () => {
  return (
    <div style={pageStyle}>
      <header>
        <nav style={navStyle}>
          <Logo />
          <Link to="/login" style={secondaryButtonStyle}>Login</Link>
        </nav>
      </header>
      <main>
        <section style={heroStyle}>
          <h1 style={headlineStyle}>All Your Finances, in One Clear View.</h1>
          <p style={subheadlineStyle}>
            Finara is a powerful, secure, and intuitive platform to track your assets, trade stocks, and manage your financial life with confidence.
          </p>
          <div style={ctaContainerStyle}>
            <Link to="/register" style={ctaButtonStyle}>Get Started for Free</Link>
          </div>
        </section>
      </main>
    </div>
  );
};

export default LandingPage;