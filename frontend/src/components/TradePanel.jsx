import React, { useState, useEffect } from 'react';
import Lottie from 'react-lottie';
import apiService from '../services/apiService';
import tradeAnimationData from '../assets/animations/Loading.json'; // <-- Import the new animation

// Styles for the modal
const modalOverlayStyle = { position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0, 0, 0, 0.7)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 };
const modalContentStyle = { backgroundColor: '#2D3748', padding: '2rem', borderRadius: '8px', width: '90%', maxWidth: '400px', textAlign: 'center' };
const modalMessageStyle = { color: 'var(--text-light)', fontSize: '1.2rem', fontWeight: '500', marginTop: '1rem' };
const modalErrorStyle = { ...modalMessageStyle, color: '#F56565'};

// Styles for the main component
const formStyle = { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', alignItems: 'center' };
const inputStyle = { padding: '0.75rem', borderRadius: '6px', border: '1px solid var(--border-color)', backgroundColor: '#1A202C', color: 'var(--text-light)', fontSize: '1rem' };
const buttonStyle = (type) => ({ padding: '0.75rem', borderRadius: '6px', border: 'none', backgroundColor: type === 'BUY' ? '#48BB78' : '#F56565', color: '#1A202C', fontSize: '1rem', fontWeight: 'bold', cursor: 'pointer' });
const infoStyle = { marginTop: '1rem', color: 'var(--text-dark)', fontSize: '0.9rem' };
const totalStyle = { fontSize: '1.1rem', color: 'var(--text-light)', fontWeight: '500' };

const TradePanel = ({ selectedStock, portfolio, account, onTradeSuccess }) => {
  const [quantity, setQuantity] = useState('');
  const [totalValue, setTotalValue] = useState(0);
  const [tradeState, setTradeState] = useState('idle'); // idle, processing, error
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    if (quantity > 0 && selectedStock?.currentPrice) {
      setTotalValue(parseFloat(quantity) * selectedStock.currentPrice);
    } else {
      setTotalValue(0);
    }
  }, [quantity, selectedStock]);

  const handleSubmit = async (orderType) => {
    if (!quantity || quantity <= 0) return;
    setTradeState('processing');

    try {
      await apiService.post('/trading/orders', { tickerSymbol: selectedStock.tickerSymbol, quantity, orderType });
      // Wait for the animation to play (2.5 seconds)
      setTimeout(() => {
        setTradeState('idle');
        setQuantity('');
        onTradeSuccess();
      }, 2500);
    } catch (err) {
      const msg = err.response?.data?.message || 'Trade failed. Please try again.';
      setErrorMessage(msg);
      setTradeState('error');
    }
  };
  
  const holding = portfolio?.holdings.find(h => h.tickerSymbol === selectedStock?.tickerSymbol);

  const tradeLottieOptions = {
    loop: false, // Play the animation only once
    autoplay: true,
    animationData: tradeAnimationData,
    rendererSettings: { preserveAspectRatio: 'xMidYMid slice' }
  };

  return (
    <>
      {tradeState !== 'idle' && (
        <div style={modalOverlayStyle}>
          <div style={modalContentStyle}>
            {tradeState === 'processing' && (
              <>
                <Lottie options={tradeLottieOptions} height={150} width={150} />
                <p style={modalMessageStyle}>Submitting Order...</p>
              </>
            )}
            {tradeState === 'error' && (
              <>
                <p style={modalErrorStyle}>{errorMessage}</p>
                <button style={{...buttonStyle('SELL'), marginTop: '1rem'}} onClick={() => setTradeState('idle')}>Close</button>
              </>
            )}
          </div>
        </div>
      )}

      <div>
        <div style={formStyle}>
          <input type="number" placeholder="Quantity" style={inputStyle} value={quantity} onChange={(e) => setQuantity(e.target.value)} required min="0.01" step="0.01" />
          <div style={{ display: 'flex', gap: '1rem' }}>
            <button style={buttonStyle('BUY')} onClick={() => handleSubmit('BUY')}>Buy</button>
            <button style={buttonStyle('SELL')} disabled={!holding || holding.quantity <= 0} onClick={() => handleSubmit('SELL')}>Sell</button>
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
      </div>
    </>
  );
};

export default TradePanel;