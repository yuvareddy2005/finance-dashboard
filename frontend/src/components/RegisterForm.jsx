import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // We'll use this to redirect after registration
import apiService from '../services/apiService';

// Reusing styles from LoginForm for consistency
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
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  gap: '0.5rem',
};

const errorStyle = {
  color: '#F56565', // A reddish color for errors
  textAlign: 'center',
  marginTop: '1rem',
};


export const RegisterForm = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      await apiService.post('/auth/register', formData);
      // If registration is successful, automatically navigate to the login page
      navigate('/login');
    } catch (err) {
      // Set a user-friendly error message
      const errorMessage = err.response?.data?.message || 'Registration failed. Please try again.';
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form style={formStyle} onSubmit={handleSubmit}>
      <input 
        type="text" 
        name="firstName" 
        placeholder="First Name" 
        style={inputStyle} 
        value={formData.firstName}
        onChange={handleChange}
        required 
      />
      <input 
        type="text" 
        name="lastName" 
        placeholder="Last Name" 
        style={inputStyle}
        value={formData.lastName}
        onChange={handleChange} 
        required 
      />
      <input 
        type="email" 
        name="email" 
        placeholder="Email" 
        style={inputStyle} 
        value={formData.email}
        onChange={handleChange}
        required 
      />
      <input 
        type="password" 
        name="password" 
        placeholder="Password" 
        style={inputStyle} 
        value={formData.password}
        onChange={handleChange}
        required 
      />
      <button type="submit" style={buttonStyle} disabled={isLoading}>
        {isLoading ? 'Registering...' : 'Register'}
      </button>
      {error && <p style={errorStyle}>{error}</p>}
    </form>
  );
};