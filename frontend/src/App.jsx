import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import AuthPage from './pages/AuthPage';
import Dashboard from './pages/Dashboard';
import PortfolioPage from './pages/PortfolioPage';
import TransfersPage from './pages/TransfersPage';
import TradingPage from './pages/TradingPage';
import LandingPage from './pages/LandingPage'; // <-- Import the new page

function App() {
  const isAuthenticated = !!localStorage.getItem('token');

  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={!isAuthenticated ? <LandingPage /> : <Navigate to="/dashboard" />} />
      <Route path="/login" element={!isAuthenticated ? <AuthPage /> : <Navigate to="/dashboard" />} />
      <Route path="/register" element={!isAuthenticated ? <AuthPage /> : <Navigate to="/dashboard" />} />

      {/* Private Routes */}
      <Route element={isAuthenticated ? <Layout /> : <Navigate to="/login" />}>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/portfolio" element={<PortfolioPage />} />
        <Route path="/transfers" element={<TransfersPage />} />
        <Route path="/trading" element={<TradingPage />} />
      </Route>

      {/* Default redirect: send unauthenticated users to the landing page */}
      <Route path="*" element={<Navigate to={isAuthenticated ? "/dashboard" : "/"} />} />
    </Routes>
  );
}

export default App;