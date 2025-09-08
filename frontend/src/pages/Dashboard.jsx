import React, { useEffect, useState } from 'react';
import apiService from '../services/apiService';
import PortfolioChart from '../components/PortfolioChart';

// A simple styled card component for our dashboard
const Card = ({ title, children, isLoading }) => {
  const cardStyle = {
    backgroundColor: '#2D3748',
    borderRadius: '8px',
    padding: '1.5rem',
    marginBottom: '1rem',
    minHeight: '100px',
  };
  const titleStyle = {
    marginTop: '0',
    marginBottom: '0.5rem',
    color: 'var(--text-light)',
    fontSize: '1.1rem',
    fontWeight: '500',
  };
  return (
    <div style={cardStyle}>
      <h3 style={titleStyle}>{title}</h3>
      <div>{isLoading ? <p style={{ color: 'var(--text-dark)'}}>Loading...</p> : children}</div>
    </div>
  );
};

const Dashboard = () => {
  const [dashboardData, setDashboardData] = useState({
    portfolioValue: 0,
    cashBalance: 0,
    recentTransactions: [],
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [portfolioRes, accountRes, transactionsRes] = await Promise.all([
          apiService.get('/trading/portfolio'),
          apiService.get('/accounts/my-account'),
          apiService.get('/transactions/my-transactions')
        ]);

        setDashboardData({
          portfolioValue: portfolioRes.data.totalValue,
          cashBalance: accountRes.data.balance,
          recentTransactions: transactionsRes.data,
        });

      } catch (err) {
        setError('Failed to load dashboard data. Please try again later.');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const dashboardStyle = {
    maxWidth: '1200px',
    margin: '0 auto',
  };

  const welcomeHeaderStyle = {
    marginBottom: '2rem',
  };

  const valueStyle = {
    fontSize: '2.5rem',
    fontWeight: 'bold',
    color: 'var(--primary-teal)',
  };
  
  const transactionListStyle = {
    listStyle: 'none',
    padding: 0,
  };

  const transactionItemStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    padding: '0.5rem 0',
    borderBottom: '1px solid var(--border-color)',
  };

  const amountStyle = (type) => ({
    color: type === 'CREDIT' ? '#48BB78' : '#F56565',
    fontWeight: '500',
  });


  if (error) {
    return <p style={{ color: '#F56565' }}>{error}</p>;
  }

  return (
    <div style={dashboardStyle}>
      <div style={welcomeHeaderStyle}>
        <h2>Dashboard</h2>
        <p style={{ color: 'var(--text-dark)' }}>
          Welcome back! Here's a summary of your financial activity.
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
        <Card title="Portfolio Value" isLoading={isLoading}>
          <p style={valueStyle}>
            {new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(dashboardData.portfolioValue)}
          </p>
        </Card>
        <Card title="Cash Balance" isLoading={isLoading}>
          <p style={valueStyle}>
            {new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(dashboardData.cashBalance)}
          </p>
        </Card>
      </div>

      <PortfolioChart />

      <Card title="Recent Transactions" isLoading={isLoading}>
        <ul style={transactionListStyle}>
          {dashboardData.recentTransactions.length > 0 ? (
            dashboardData.recentTransactions.map(tx => (
              <li key={tx.id} style={transactionItemStyle}>
                <span>{tx.description}</span>
                <span style={amountStyle(tx.type)}>
                  {tx.type === 'DEBIT' ? '-' : '+'}
                  {new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(tx.amount)}
                </span>
              </li>
            ))
          ) : (
            <p style={{ color: 'var(--text-dark)' }}>No recent transactions found.</p>
          )}
        </ul>
      </Card>
    </div>
  );
};

export default Dashboard;