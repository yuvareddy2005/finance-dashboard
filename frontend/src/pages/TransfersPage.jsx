import React, { useState } from 'react';
import apiService from '../services/apiService';

const pageStyle = {
  maxWidth: '600px', // A narrower width is suitable for a form
  margin: '0 auto',
};

const formStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '1.5rem',
  padding: '2rem',
  backgroundColor: '#2D3748',
  borderRadius: '8px',
};

const inputStyle = {
  padding: '0.75rem',
  borderRadius: '6px',
  border: '1px solid var(--border-color)',
  backgroundColor: '#1A202C',
  color: 'var(--text-light)',
  fontSize: '1rem',
};

const buttonStyle = {
  padding: '0.75rem',
  borderRadius: '6px',
  border: 'none',
  backgroundColor: 'var(--primary-teal)',
  color: '#1A202C',
  fontSize: '1rem',
  fontWeight: 'bold',
  cursor: 'pointer',
};

const messageStyle = (isError) => ({
  textAlign: 'center',
  padding: '1rem',
  borderRadius: '6px',
  marginTop: '1.5rem',
  backgroundColor: isError ? 'rgba(245, 101, 101, 0.1)' : 'rgba(20, 184, 166, 0.1)',
  color: isError ? '#F56565' : '#14B8A6',
  border: `1px solid ${isError ? '#F56565' : '#14B8A6'}`,
});


const TransfersPage = () => {
  const [recipientEmail, setRecipientEmail] = useState('');
  const [amount, setAmount] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [isError, setIsError] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage(null);
    setIsError(false);

    try {
      await apiService.post('/p2p/transfers', { recipientEmail, amount });
      setMessage('Transfer successful!');
      setRecipientEmail('');
      setAmount('');
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Transfer failed. Please try again.';
      setMessage(errorMessage);
      setIsError(true);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={pageStyle}>
      <h2>Send Money</h2>
      <p style={{ color: 'var(--text-dark)', marginBottom: '2rem' }}>
        Transfer funds to another user instantly. All you need is their email address.
      </p>
      <form style={formStyle} onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="Recipient's Email"
          style={inputStyle}
          value={recipientEmail}
          onChange={(e) => setRecipientEmail(e.target.value)}
          required
        />
        <input
          type="number"
          placeholder="Amount (₹)"
          style={inputStyle}
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          required
          min="0.01"
          step="0.01"
        />
        <button type="submit" style={buttonStyle} disabled={isLoading}>
          {isLoading ? 'Sending...' : 'Send Money'}
        </button>
      </form>
      {message && <div style={messageStyle(isError)}>{message}</div>}
    </div>
  );
};

export default TransfersPage;