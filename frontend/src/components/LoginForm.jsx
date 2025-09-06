import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../services/apiService';

const formStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: '1rem',
  maxWidth: '400px',
  margin: '2rem auto',
};

const inputStyle = {
  padding: '0.75rem',
  borderRadius: '6px',
  border: '1px solid var(--border-color)',
  backgroundColor: '#2D3748',
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

const errorStyle = {
  color: '#F56565', // A reddish color for errors
  textAlign: 'center',
  marginTop: '1rem',
};

export const LoginForm = () => {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials(prevState => ({ ...prevState, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      const response = await apiService.post('/auth/login', credentials);
      const { token } = response.data;
      
      // Store the token in localStorage
      localStorage.setItem('token', token);
      
      // Navigate to the dashboard
      navigate('/dashboard');

      // Force a full page reload to re-evaluate the auth status in App.jsx
      window.location.reload();

    } catch (err) {
      setError('Invalid email or password. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form style={formStyle} onSubmit={handleSubmit}>
      <input 
        type="email" 
        name="email"
        placeholder="Email" 
        style={inputStyle} 
        value={credentials.email}
        onChange={handleChange}
        required
      />
      <input 
        type="password"
        name="password"
        placeholder="Password" 
        style={inputStyle}
        value={credentials.password}
        onChange={handleChange}
        required
      />
      <button type="submit" style={buttonStyle} disabled={isLoading}>
        {isLoading ? 'Logging in...' : 'Login'}
      </button>
      {error && <p style={errorStyle}>{error}</p>}
    </form>
  );
};