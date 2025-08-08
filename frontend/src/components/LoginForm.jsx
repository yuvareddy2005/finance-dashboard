import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // <-- Import for redirection
import apiService from '../services/apiService'; // <-- Import our API service

export const LoginForm = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [message, setMessage] = useState('');
  const navigate = useNavigate(); // Hook for programmatic navigation

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');

    try {
      // Use our apiService to send a POST request to the login endpoint
      const response = await apiService.post('/auth/login', formData);
      
      // Extract the token from the response
      const { token } = response.data;
      
      // Store the token in the browser's local storage
      localStorage.setItem('token', token);

      console.log('Login successful, token stored.');
      setMessage('Login successful! Redirecting...');

      // Redirect to the dashboard page after a short delay
      setTimeout(() => {
        navigate('/dashboard');
      }, 1000);

    } catch (error) {
      console.error('Login failed:', error.response ? error.response.data : error.message);
      setMessage('Login failed. Please check your credentials.');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Login to Finara</h2>
      <div>
        <label>Email:</label>
        <input type="email" name="email" value={formData.email} onChange={handleChange} required />
      </div>
      <div>
        <label>Password:</label>
        <input type="password" name="password" value={formData.password} onChange={handleChange} required />
      </div>
      <button type="submit">Login</button>
      {message && <p>{message}</p>}
    </form>
  );
};
