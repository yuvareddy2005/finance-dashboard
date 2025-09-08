// src/components/Modal.jsx
import React from 'react';

const overlayStyle = {
  position: 'fixed',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  backgroundColor: 'rgba(0, 0, 0, 0.7)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  zIndex: 1000,
};

const modalStyle = {
  backgroundColor: '#2D3748',
  padding: '2rem',
  borderRadius: '8px',
  width: '90%',
  maxWidth: '500px',
  textAlign: 'center',
};

const buttonStyle = {
  padding: '0.75rem 1.5rem',
  borderRadius: '6px',
  border: 'none',
  backgroundColor: 'var(--primary-teal)',
  color: '#1A202C',
  fontSize: '1rem',
  fontWeight: 'bold',
  cursor: 'pointer',
  marginTop: '1.5rem',
};

const Modal = ({ isOpen, onClose, children }) => {
  if (!isOpen) return null;

  return (
    <div style={overlayStyle} onClick={onClose}>
      <div style={modalStyle} onClick={e => e.stopPropagation()}>
        {children}
        <button style={buttonStyle} onClick={onClose}>
          Get Started
        </button>
      </div>
    </div>
  );
};

export default Modal;