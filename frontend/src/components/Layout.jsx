import React from 'react';
import { Link, Outlet } from 'react-router-dom';
import { Logo } from './Logo';

const Layout = () => {
  // Styles for the main container to add padding around the floating header
  const layoutContainerStyle = {
    padding: '1rem',
  };

  const headerStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '0.75rem 2rem',
    borderRadius: '9999px', // A large value makes it pill-shaped
    backgroundColor: '#2D3748', // A slightly lighter dark gray for the "island"
  };

  const navStyle = {
    display: 'flex',
    gap: '1.5rem',
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

  const contentStyle = {
    padding: '2rem',
  };

  return (
    <div style={layoutContainerStyle}>
      <header style={headerStyle}>
        <Logo />
        <nav style={navStyle}>
          <Link to="/dashboard" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Dashboard</Link>
          <Link to="/portfolio" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Portfolio</Link>
          <Link to="/transfers" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Transfers</Link>
          <Link to="/trading" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Trading</Link>
        </nav>
      </header>
      <main style={contentStyle}>
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;

