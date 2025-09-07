import React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { Logo } from './Logo';

const Layout = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
    window.location.reload();
  };

  const layoutContainerStyle = {
    padding: '1rem',
  };

  const headerStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '0.75rem 2rem',
    borderRadius: '9999px',
    backgroundColor: '#2D3748',
  };

  const navContainerStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '3rem',
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
    cursor: 'pointer',
  };
  
  // v-- THIS IS THE ONLY SECTION THAT CHANGED --v
  const logoutButtonStyle = {
    ...navLinkStyle,
    background: 'none',
    border: 'none',
    padding: 0,
    fontFamily: 'inherit',
    color: '#F56565',
  };

  const handleMouseOver = (e) => e.target.style.color = 'var(--primary-teal)';
  const handleMouseOut = (e) => e.target.style.color = 'var(--text-dark)';
  const handleLogoutMouseOver = (e) => e.target.style.color = '#FC8181';
  const handleLogoutMouseOut = (e) => e.target.style.color = '#F56565';

  const contentStyle = {
    padding: '2rem',
  };

  return (
    <div style={layoutContainerStyle}>
      <header style={headerStyle}>
        <Logo />
        <div style={navContainerStyle}>
          <nav style={navStyle}>
            <Link to="/dashboard" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Dashboard</Link>
            <Link to="/portfolio" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Portfolio</Link>
            <Link to="/transfers" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Transfers</Link>
            <Link to="/trading" style={navLinkStyle} onMouseOver={handleMouseOver} onMouseOut={handleMouseOut}>Trading</Link>
          </nav>
          <button 
            onClick={handleLogout} 
            style={logoutButtonStyle}
            onMouseOver={handleLogoutMouseOver}
            onMouseOut={handleLogoutMouseOut}
          >
            Logout
          </button>
        </div>
      </header>
      <main style={contentStyle}>
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;