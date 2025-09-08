import React from 'react';

const containerStyle = {
  display: 'flex',
  gap: '0.5rem',
  marginBottom: '1rem',
};

const buttonStyle = (isActive) => ({
  padding: '0.5rem 1rem',
  borderRadius: '6px',
  border: '1px solid var(--border-color)',
  cursor: 'pointer',
  fontSize: '0.9rem',
  fontWeight: '500',
  backgroundColor: isActive ? 'var(--primary-teal)' : 'transparent',
  color: isActive ? '#1A202C' : 'var(--text-dark)',
});

const TimeframeSelector = ({ selectedRange, onSelectRange }) => {
  const ranges = ['1M', '6M', '1Y', '5Y', 'ALL'];

  return (
    <div style={containerStyle}>
      {ranges.map(range => (
        <button
          key={range}
          style={buttonStyle(selectedRange === range)}
          onClick={() => onSelectRange(range)}
        >
          {range}
        </button>
      ))}
    </div>
  );
};

export default TimeframeSelector;