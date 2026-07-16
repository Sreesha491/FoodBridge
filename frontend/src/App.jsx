import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from '@/context/AuthContext';
import ProtectedRoute from '@/components/ProtectedRoute';
import HomePage from '@/pages/HomePage';
import LoginPage from '@/pages/LoginPage';
import RegisterPage from '@/pages/RegisterPage';
import DashboardPage from '@/pages/DashboardPage';

import FoodMarketPage from '@/pages/FoodMarketPage';
import OrdersPage from '@/pages/OrdersPage';

/**
 * Root application component.
 *
 * Provides global AuthContext (manages JWT + user session) and defines
 * top-level route structure using React Router v6.
 *
 * Routes:
 *   /              → HomePage (public landing page)
 *   /login         → LoginPage (public)
 *   /register      → RegisterPage (public)
 *   /dashboard     → DashboardPage (requires authentication)
 *   /market        → FoodMarketPage (requires authentication)
 *   /orders        → OrdersPage (requires authentication)
 *   /unauthorized  → 403 page
 *   *              → redirect to /
 */
function App() {
  return (
    <AuthProvider>
      <Routes>
        {/* ── Public routes ───────────────────────────── */}
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* ── Protected routes ─────────────────────────── */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/market"
          element={
            <ProtectedRoute>
              <FoodMarketPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/orders"
          element={
            <ProtectedRoute>
              <OrdersPage />
            </ProtectedRoute>
          }
        />

        {/* ── Fallback ──────────────────────────────────── */}
        <Route path="/unauthorized" element={
          <div style={{
            minHeight: '100vh',
            background: '#0d0d1a',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontFamily: 'Inter, sans-serif',
            gap: '1rem',
          }}>
            <div style={{ fontSize: '4rem' }}>🚫</div>
            <h1 style={{ margin: 0, fontSize: '2rem' }}>403 – Forbidden</h1>
            <p style={{ color: 'rgba(255,255,255,0.5)', margin: 0 }}>
              You don&apos;t have permission to access this page.
            </p>
            <a href="/" style={{
              padding: '0.6rem 1.5rem',
              background: '#f97316',
              color: '#fff',
              textDecoration: 'none',
              borderRadius: '0.625rem',
              fontWeight: 600,
            }}>
              Go Home
            </a>
          </div>
        } />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AuthProvider>
  );
}

export default App;
