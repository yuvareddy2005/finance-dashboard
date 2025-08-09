import React, { useState, useEffect } from 'react';
import { RegisterForm } from '../components/RegisterForm';
import { LoginForm } from '../components/LoginForm';
import apiService from '../services/apiService'; // <-- Import our API service

// A simple placeholder for our Login Page
export const LoginPage = () => {
  return <LoginForm />;
};

// The Register Page renders the registration form
export const RegisterPage = () => {
  return <RegisterForm />;
};

// The Dashboard Page now fetches data from a protected endpoint
export const DashboardPage = () => {
  const [message, setMessage] = useState('');

  useEffect(() => {
    // This function will run when the component mounts
    const fetchProtectedData = async () => {
      try {
        const response = await apiService.get('/users/hello');
        setMessage(response.data); // Set the message from the backend
      } catch (error) {
        console.error('Failed to fetch protected data:', error);
        setMessage('Failed to load data. You might not be authenticated.');
      }
    };

    fetchProtectedData();
  }, []); // The empty array means this effect runs only once

  return (
    <div>
      <h2>Dashboard</h2>
      <p><strong>Message from backend:</strong> {message}</p>
    </div>
  );
};
