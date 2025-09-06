import React from 'react';
import Dashboard from './Dashboard'; // Import the new Dashboard component

// A simple placeholder for our Login Page
export const LoginPage = () => {
  return <h2>Login Page</h2>;
};

// A simple placeholder for our Registration Page
export const RegisterPage = () => {
  return <h2>Register Page</h2>;
};

// The DashboardPage now simply renders our dedicated Dashboard component
export const DashboardPage = () => {
  return <Dashboard />;
};
