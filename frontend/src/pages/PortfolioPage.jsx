import React, { useState, useEffect } from 'react';
import apiService from '../services/apiService';

const pageStyle = {
  maxWidth: '1200px',
  margin: '0 auto',
};

const headerStyle = {
  marginBottom: '2rem',
};

const tableStyle = {
  width: '100%',
  borderCollapse: 'collapse',
  backgroundColor: '#2D3748',
  borderRadius: '8px',
  overflow: 'hidden',
};

const thStyle = {
  padding: '1rem',
  textAlign: 'left',
  borderBottom: '2px solid var(--background-dark)',
  color: 'var(--text-dark)',
  textTransform: 'uppercase',
  fontSize: '0.8rem',
  letterSpacing: '0.5px',
};

const tdStyle = {
  padding: '1rem',
  borderBottom: '1px solid var(--background-dark)',
};

const totalValueStyle = {
    fontSize: '2.5rem',
    fontWeight: 'bold',
    color: 'var(--primary-teal)',
    margin: '0 0 1rem 0',
};

const PortfolioPage = () => {
  const [portfolio, setPortfolio] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPortfolio = async () => {
      try {
        const response = await apiService.get('/trading/portfolio');
        setPortfolio(response.data);
      } catch (err) {
        setError('Failed to load portfolio data.');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };
    fetchPortfolio();
  }, []);

  if (isLoading) return <p style={{ color: 'var(--text-dark)' }}>Loading portfolio...</p>;
  if (error) return <p style={{ color: '#F56565' }}>{error}</p>;

  return (
    <div style={pageStyle}>
      <div style={headerStyle}>
        <h2>My Portfolio</h2>
        <p style={totalValueStyle}>
          {new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(portfolio.totalValue)}
        </p>
      </div>
      <table style={tableStyle}>
        <thead>
          <tr>
            <th style={thStyle}>Ticker</th>
            <th style={thStyle}>Company</th>
            <th style={thStyle}>Quantity</th>
            <th style={thStyle}>Avg. Buy Price</th>
            <th style={thStyle}>Current Price</th>
            <th style={thStyle}>Current Value</th>
          </tr>
        </thead>
        <tbody>
          {portfolio.holdings.map(holding => (
            <tr key={holding.tickerSymbol}>
              <td style={tdStyle}>{holding.tickerSymbol}</td>
              <td style={tdStyle}>{holding.companyName}</td>
              <td style={tdStyle}>{holding.quantity}</td>
              <td style={tdStyle}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(holding.averageBuyPrice)}</td>
              <td style={tdStyle}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(holding.currentPrice)}</td>
              <td style={tdStyle}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(holding.currentValue)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default PortfolioPage;