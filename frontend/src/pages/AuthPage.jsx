import React from 'react';
import { useLocation } from 'react-router-dom';
import { LoginForm } from '../components/LoginForm';
import { RegisterForm } from '../components/RegisterForm';
import { Logo } from '../components/Logo';

const authPageStyle = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  minHeight: '100vh',
  padding: '2rem',
};

const titleStyle = {
  marginBottom: '2rem',
  color: 'var(--text-light)',
  fontSize: '2rem',
};

const AuthPage = () => {
  const location = useLocation();
  const isLoginPage = location.pathname === '/login';

  return (
    <div style={authPageStyle}>
      <Logo />
      <h1 style={titleStyle}>
        {isLoginPage ? 'Login to Finara' : 'Register for Finara'}
      </h1>
      {isLoginPage ? <LoginForm /> : <RegisterForm />}
    </div>
  );
};

export default AuthPage;