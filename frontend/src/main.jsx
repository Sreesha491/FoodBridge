import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App.jsx';
import './index.css';

/**
 * FoodBridge React application entry point.
 *
 * Wraps the root <App /> in:
 * - React.StrictMode  – highlights potential issues in development
 * - BrowserRouter     – enables client-side routing via React Router v6
 */
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);
