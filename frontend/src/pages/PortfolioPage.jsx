// src/pages/PortfolioPage.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../services/apiService';

const pageStyle = { maxWidth: '1200px', margin: '0 auto' };
const headerStyle = { marginBottom: '2rem' };
const tableStyle = { width: '100%', borderCollapse: 'collapse', backgroundColor: '#2D3748', borderRadius: '8px', overflow: 'hidden', marginBottom: '3rem' };
const thStyle = { padding: '1rem', textAlign: 'left', borderBottom: '2px solid var(--background-dark)', color: 'var(--text-dark)', textTransform: 'uppercase', fontSize: '0.8rem', letterSpacing: '0.5px' };
const tdStyle = { padding: '1rem', borderBottom: '1px solid var(--background-dark)', verticalAlign: 'middle' };
const totalValueStyle = { fontSize: '2.5rem', fontWeight: 'bold', color: 'var(--primary-teal)', margin: '0 0 1rem 0' };
const plStyle = (isProfit) => ({ ...tdStyle, color: isProfit ? '#48BB78' : '#F56565', fontWeight: '500' });
const sellButtonStyle = { padding: '0.5rem 1rem', borderRadius: '6px', border: 'none', backgroundColor: '#F56565', color: '#1A202C', cursor: 'pointer', fontWeight: 'bold' };

const PortfolioPage = () => {
  const [portfolio, setPortfolio] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPortfolio = async () => {
      setIsLoading(true);
      try {
        const response = await apiService.get('/trading/portfolio');
        setPortfolio(response.data);
      } catch (err) {
        setError('Failed to load portfolio data.');
      } finally {
        setIsLoading(false);
      }
    };
    fetchPortfolio();
  }, []);

  const handleSellClick = (holding) => {
    navigate('/trading', { state: { defaultStock: { tickerSymbol: holding.tickerSymbol, companyName: holding.companyName, currentPrice: holding.currentPrice } } });
  };

  if (isLoading) return <p style={{ color: 'var(--text-dark)' }}>Loading portfolio...</p>;
  if (error) return <p style={{ color: '#F56565' }}>{error}</p>;

  return (
    <div style={pageStyle}>
      <div style={headerStyle}>
        <h2>My Current Holdings</h2>
        <p style={totalValueStyle}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(portfolio.totalValue)}</p>
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
            <th style={thStyle}>P/L</th>
            <th style={thStyle}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {portfolio.holdings.filter(h => h.quantity > 0).map(holding => {
            const costBasis = holding.averageBuyPrice * holding.quantity;
            const profitLoss = holding.currentValue - costBasis;
            return (
              <tr key={holding.tickerSymbol}>
                <td style={tdStyle}>{holding.tickerSymbol}</td>
                <td style={tdStyle}>{holding.companyName}</td>
                <td style={tdStyle}>{holding.quantity}</td>
                <td style={tdStyle}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(holding.averageBuyPrice)}</td>
                <td style={tdStyle}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(holding.currentPrice)}</td>
                <td style={tdStyle}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(holding.currentValue)}</td>
                <td style={plStyle(profitLoss >= 0)}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(profitLoss)}</td>
                <td style={tdStyle}><button style={sellButtonStyle} onClick={() => handleSellClick(holding)}>Sell</button></td>
              </tr>
            )
          })}
        </tbody>
      </table>

      {/* --- NEW TRADE HISTORY TABLE --- */}
      <div style={headerStyle}>
        <h2>Trade History</h2>
      </div>
      {portfolio.pastTrades && portfolio.pastTrades.length > 0 ? (
        <table style={tableStyle}>
          <thead>
            <tr>
              <th style={thStyle}>Ticker</th>
              <th style={thStyle}>Company</th>
              <th style={thStyle}>Quantity</th>
              <th style={thStyle}>Avg. Buy Price</th>
              <th style={thStyle}>Avg. Sell Price</th>
              <th style={thStyle}>Realized P/L</th>
            </tr>
          </thead>
          <tbody>
            {portfolio.pastTrades.map(trade => (
              <tr key={trade.tickerSymbol}>
                <td style={tdStyle}>{trade.tickerSymbol}</td>
                <td style={tdStyle}>{trade.companyName}</td>
                <td style={tdStyle}>{trade.quantity}</td>
                <td style={tdStyle}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(trade.averageBuyPrice)}</td>
                <td style={tdStyle}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(trade.averageSellPrice)}</td>
                <td style={plStyle(trade.profitOrLoss >= 0)}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(trade.profitOrLoss)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p style={{ color: 'var(--text-dark)'}}>You have no completed trade history yet.</p>
      )}
    </div>
  );
};

export default PortfolioPage;