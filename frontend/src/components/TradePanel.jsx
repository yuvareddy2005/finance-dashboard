// src/components/TradePanel.jsx
import React, { useState, useEffect } from 'react';
import apiService from '../services/apiService';

const formStyle = { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', alignItems: 'center' };
const inputStyle = { padding: '0.75rem', borderRadius: '6px', border: '1px solid var(--border-color)', backgroundColor: '#1A202C', color: 'var(--text-light)', fontSize: '1rem' };
const buttonStyle = (type) => ({ padding: '0.75rem', borderRadius: '6px', border: 'none', backgroundColor: type === 'BUY' ? '#48BB78' : '#F56565', color: '#1A202C', fontSize: '1rem', fontWeight: 'bold', cursor: 'pointer' });
const messageStyle = (isError) => ({ textAlign: 'center', padding: '1rem', borderRadius: '6px', marginTop: '1rem', backgroundColor: isError ? 'rgba(245, 101, 101, 0.1)' : 'rgba(20, 184, 166, 0.1)', color: isError ? '#F56565' : '#14B8A6', border: `1px solid ${isError ? '#F56565' : '#14B8A6'}` });
const infoStyle = { marginTop: '1rem', color: 'var(--text-dark)', fontSize: '0.9rem' };
const totalStyle = { fontSize: '1.1rem', color: 'var(--text-light)', fontWeight: '500' };

const TradePanel = ({ selectedStock, portfolio, account, onTradeSuccess }) => {
  const [quantity, setQuantity] = useState('');
  const [totalValue, setTotalValue] = useState(0); // <-- New state for the calculated total
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [isError, setIsError] = useState(false);

  // This effect calculates the total value whenever the quantity or selected stock changes
  useEffect(() => {
    if (quantity > 0 && selectedStock?.currentPrice) {
      setTotalValue(parseFloat(quantity) * selectedStock.currentPrice);
    } else {
      setTotalValue(0);
    }
    setMessage(null);
    setIsError(false);
  }, [quantity, selectedStock]);

  const handleSubmit = async (orderType) => {
    if (!quantity || quantity <= 0) {
      setMessage("Please enter a valid quantity.");
      setIsError(true);
      return;
    }
    setIsLoading(true);
    setMessage(null);
    setIsError(false);
    try {
      await apiService.post('/trading/orders', { tickerSymbol: selectedStock.tickerSymbol, quantity, orderType });
      setMessage(`Successfully executed ${orderType} order!`);
      setQuantity('');
      onTradeSuccess();
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Trade failed. Please try again.';
      setMessage(errorMessage);
      setIsError(true);
    } finally {
      setIsLoading(false);
    }
  };

  const holding = portfolio?.holdings.find(h => h.tickerSymbol === selectedStock?.tickerSymbol);

  return (
    <div>
      <div style={formStyle}>
        <input type="number" placeholder="Quantity" style={inputStyle} value={quantity} onChange={(e) => setQuantity(e.target.value)} required min="0.01" step="0.01" />
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button style={buttonStyle('BUY')} disabled={isLoading} onClick={() => handleSubmit('BUY')}>Buy</button>
          <button style={buttonStyle('SELL')} disabled={isLoading || !holding || holding.quantity <= 0} onClick={() => handleSubmit('SELL')}>Sell</button>
        </div>
      </div>
      <div style={infoStyle}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span>Estimated Total:</span>
          <span style={totalStyle}>{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(totalValue)}</span>
        </div>
        <hr style={{borderColor: 'var(--border-color)', opacity: 0.5, margin: '0.75rem 0'}}/>
        <p style={{ margin: '0.5rem 0' }}>Cash Available: {new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(account?.balance || 0)}</p>
        <p style={{ margin: '0.5rem 0' }}>Shares Owned: {holding?.quantity || 0}</p>
      </div>
      {message && <div style={messageStyle(isError)}>{message}</div>}
    </div>
  );
};

export default TradePanel;