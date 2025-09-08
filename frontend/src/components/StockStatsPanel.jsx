import React from 'react';

const panelStyle = {
  backgroundColor: '#2D3748',
  borderRadius: '8px',
  padding: '2rem',
};

const gridStyle = {
  display: 'grid',
  gridTemplateColumns: 'repeat(4, 1fr)', // 4 columns
  gap: '1.5rem',
};

const statItemStyle = {
  display: 'flex',
  flexDirection: 'column',
};

const statLabelStyle = {
  color: 'var(--text-dark)',
  fontSize: '0.9rem',
  marginBottom: '0.5rem',
};

const statValueStyle = {
  color: 'var(--text-light)',
  fontSize: '1.2rem',
  fontWeight: '500',
};

const formatCurrency = (value) => {
    if (value === null || value === undefined) return 'N/A';
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(value);
}

const StockStatsPanel = ({ stats, isLoading }) => {
  if (isLoading) {
    return <div style={panelStyle}><p style={{color: 'var(--text-dark)'}}>Loading stats...</p></div>;
  }
  if (!stats) {
    return <div style={panelStyle}><p style={{color: 'var(--text-dark)'}}>Stats not available.</p></div>;
  }

  return (
    <div style={panelStyle}>
      <div style={gridStyle}>
        <div style={statItemStyle}>
          <span style={statLabelStyle}>Open</span>
          <span style={statValueStyle}>{formatCurrency(stats.open)}</span>
        </div>
        <div style={statItemStyle}>
          <span style={statLabelStyle}>Prev. Close</span>
          <span style={statValueStyle}>{formatCurrency(stats.prevClose)}</span>
        </div>
        <div style={statItemStyle}>
          <span style={statLabelStyle}>Volume</span>
          <span style={statValueStyle}>{stats.volume}</span>
        </div>
        <div style={statItemStyle}>
          <span style={statLabelStyle}>Total Traded Value</span>
          <span style={statValueStyle}>{stats.totalTradedValue}</span>
        </div>
        <div style={statItemStyle}>
          <span style={statLabelStyle}>Upper Circuit</span>
          <span style={statValueStyle}>{formatCurrency(stats.upperCircuit)}</span>
        </div>
        <div style={statItemStyle}>
          <span style={statLabelStyle}>Lower Circuit</span>
          <span style={statValueStyle}>{formatCurrency(stats.lowerCircuit)}</span>
        </div>
      </div>
    </div>
  );
};

export default StockStatsPanel;