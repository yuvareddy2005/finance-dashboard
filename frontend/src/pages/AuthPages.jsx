import React from 'react';
import { RegisterForm } from '../components/RegisterForm';
import { LoginForm } from '../components/LoginForm'; // <-- Import the new Login Form

// The Login Page now renders our form component
export const LoginPage = () => {
  return <LoginForm />;
};

// The Register Page renders the registration form
export const RegisterPage = () => {
  return <RegisterForm />;
};

// A simple placeholder for our main Dashboard Page
export const DashboardPage = () => {
  return <h2>Dashboard</h2>;
};
