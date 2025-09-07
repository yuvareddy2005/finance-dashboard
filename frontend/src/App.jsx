import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import AuthPage from './pages/AuthPage';
import Dashboard from './pages/Dashboard';
import PortfolioPage from './pages/PortfolioPage';
import TransfersPage from './pages/TransfersPage';

function App() {
  // We'll check for the token in localStorage to see if the user is authenticated
  const isAuthenticated = !!localStorage.getItem('token');

  return (
    <Routes>
      {/* Public Routes: If authenticated, redirect to dashboard */}
      <Route path="/login" element={!isAuthenticated ? <AuthPage /> : <Navigate to="/dashboard" />} />
      <Route path="/register" element={!isAuthenticated ? <AuthPage /> : <Navigate to="/dashboard" />} />

      {/* Private Routes: If not authenticated, redirect to login */}
      <Route element={isAuthenticated ? <Layout /> : <Navigate to="/login" />}>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/portfolio" element={<PortfolioPage />} />
        <Route path="/transfers" element={<TransfersPage />} />
        {/* We will add more private routes like /portfolio here later */}
      </Route>

      {/* Default redirect */}
      <Route path="*" element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />} />
    </Routes>
  );
}

export default App;