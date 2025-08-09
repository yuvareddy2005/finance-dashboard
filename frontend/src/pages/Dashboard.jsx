import React from 'react';

export const Dashboard = () => {
  const cardStyle = {
    backgroundColor: '#1f2937',
    padding: '20px',
    borderRadius: '8px',
    marginBottom: '20px',
  };

  return (
    <div>
      <h1>Dashboard</h1>
      <p style={{ color: '#9ca3af', marginBottom: '30px' }}>Welcome back! Here's a summary of your financial activity.</p>

      <div style={cardStyle}>
        <h2>Portfolio Value</h2>
        <p style={{ fontSize: '2em', color: '#f97316' }}>₹ 0.00</p>
      </div>

      <div style={cardStyle}>
        <h2>Recent Transactions</h2>
        <p style={{ color: '#9ca3af' }}>Transaction list will be displayed here...</p>
      </div>
    </div>
  );
};
