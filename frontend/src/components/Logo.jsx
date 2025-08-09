import React from 'react';

// A simple, abstract SVG placeholder for the Finara logo.
// It's designed to look like a stylized 'F'.
export const Logo = () => {
  return (
    <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M10 10H30V16H10V10Z" fill="#f97316"/>
      <path d="M10 24H22V30H10V24Z" fill="#f97316"/>
    </svg>
  );
};
