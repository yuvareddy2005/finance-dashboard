import React from 'react';
import { useState } from 'react';
import apiService from '../services/apiService';
export const RegisterForm = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
  });
  const [message, setMessage] = useState(''); // To display success or error messages

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => { // <-- Make the function async
    e.preventDefault();
    setMessage(''); // Clear previous messages

    try {
      // Use our apiService to send a POST request
      const response = await apiService.post('/auth/register', formData);
      
      console.log('Registration successful:', response.data);
      setMessage('Registration successful! You can now log in.');
      // Optionally, clear the form
      setFormData({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
      });

    } catch (error) {
      console.error('Registration failed:', error.response ? error.response.data : error.message);
      setMessage('Registration failed. Please try again.');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Register for Finara</h2>
      {/* Input fields remain the same */}
      <div>
        <label>First Name:</label>
        <input type="text" name="firstName" value={formData.firstName} onChange={handleChange} required />
      </div>
      <div>
        <label>Last Name:</label>
        <input type="text" name="lastName" value={formData.lastName} onChange={handleChange} required />
      </div>
      <div>
        <label>Email:</label>
        <input type="email" name="email" value={formData.email} onChange={handleChange} required />
      </div>
      <div>
        <label>Password:</label>
        <input type="password" name="password" value={formData.password} onChange={handleChange} required />
      </div>
      <button type="submit">Register</button>
      
      {/* Display success or error messages */}
      {message && <p>{message}</p>}
    </form>
  );
};
