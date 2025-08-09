import React from 'react';
import { RegisterForm } from '../components/RegisterForm';
import { LoginForm } from '../components/LoginForm';
import { Dashboard } from '../pages/Dashboard'; // <-- Import the new Dashboard component

// The Login Page renders our form component
export const LoginPage = () => {
  return <LoginForm />;
};

// The Register Page renders the registration form
export const RegisterPage = () => {
  return <RegisterForm />;
};

// The DashboardPage now renders our new Dashboard component
export const DashboardPage = () => {
  return <Dashboard />;
};
