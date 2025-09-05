import React from 'react';

// A simple, abstract SVG placeholder logo that uses our theme's teal color
export const Logo = () => {
  return (
    <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path 
        d="M20 0C8.954 0 0 8.954 0 20C0 31.046 8.954 40 20 40C31.046 40 40 31.046 40 20C40 8.954 31.046 0 20 0ZM22.5 27.5H17.5V12.5H22.5V17.5H27.5V22.5H22.5V27.5Z" 
        fill="var(--primary-teal)"
      />
    </svg>
  );
};