import React from 'react';
import { Link, Outlet } from 'react-router-dom';

export const Layout = () => {
  const sidebarStyle = {
    width: '250px',
    position: 'fixed',
    top: 0,
    left: 0,
    height: '100vh',
    backgroundColor: '#111827', // Darker than the main background
    padding: '20px',
    borderRight: '1px solid #374151'
  };

  const contentStyle = {
    marginLeft: '270px', // Sidebar width + padding
    padding: '20px'
  };

  const navLinkStyle = {
    color: '#e5e7eb',
    textDecoration: 'none',
    display: 'block',
    marginBottom: '15px',
    fontSize: '18px'
  };

  return (
    <div>
      <aside style={sidebarStyle}>
        <h1 style={{ color: '#f97316', marginBottom: '40px' }}>Finara</h1>
        <nav>
          <Link to="/dashboard" style={navLinkStyle}>Dashboard</Link>
          <Link to="/portfolio" style={navLinkStyle}>Portfolio</Link>
          <Link to="/transfers" style={navLinkStyle}>Transfers</Link>
          <Link to="/trading" style={navLinkStyle}>Trading</Link>
        </nav>
      </aside>
      <main style={contentStyle}>
        <Outlet />
      </main>
    </div>
  );
};
