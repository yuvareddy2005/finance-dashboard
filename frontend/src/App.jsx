import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'; // Import Navigate
import { LoginPage, RegisterPage, DashboardPage } from './pages/AuthPages';
import { Layout } from './components/Layout';

function App() {
  return (
    <Router>
      <Routes>
        {/* Add a route to redirect the root path to the login page */}
        <Route path="/" element={<Navigate to="/login" />} />

        {/* Public routes that don't have the sidebar */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Protected routes that will be nested inside the Layout */}
        <Route element={<Layout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          {/* We will add routes for Portfolio, Transfers, etc. here later */}
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
