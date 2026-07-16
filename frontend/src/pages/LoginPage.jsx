import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';

/**
 * Login page for FoodBridge.
 *
 * Authenticates with email + password, stores JWT in localStorage via AuthContext,
 * then redirects to the original destination (or /dashboard).
 */
function LoginPage() {
  const { login, isLoading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || '/dashboard';

  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await login(form.email.trim(), form.password);
      navigate(from, { replace: true });
    } catch (err) {
      const msg = err.response?.data?.message || 'Invalid email or password. Please try again.';
      setError(msg);
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        {/* Logo / Brand */}
        <div className="login-brand">
          <div className="brand-icon">🌉</div>
          <h1 className="brand-name">FoodBridge</h1>
          <p className="brand-tagline">Smart Food Waste Reduction & Donation Platform</p>
        </div>

        {/* Card */}
        <div className="login-card">
          <h2 className="login-title">Welcome back</h2>
          <p className="login-subtitle">Sign in to your account to continue</p>

          {error && (
            <div className="alert alert-error" role="alert" id="login-error">
              <span className="alert-icon">⚠️</span>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="login-form" noValidate>
            <div className="form-group">
              <label htmlFor="login-email" className="form-label">Email address</label>
              <input
                id="login-email"
                name="email"
                type="email"
                autoComplete="email"
                required
                value={form.email}
                onChange={handleChange}
                className="form-input"
                placeholder="you@example.com"
              />
            </div>

            <div className="form-group">
              <label htmlFor="login-password" className="form-label">Password</label>
              <div className="input-wrapper">
                <input
                  id="login-password"
                  name="password"
                  type={showPassword ? 'text' : 'password'}
                  autoComplete="current-password"
                  required
                  value={form.password}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  className="toggle-password"
                  onClick={() => setShowPassword((v) => !v)}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? '🙈' : '👁️'}
                </button>
              </div>
            </div>

            <button
              id="login-submit-btn"
              type="submit"
              className="btn btn-primary btn-full"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <span className="spinner" />
                  Signing in…
                </>
              ) : 'Sign in'}
            </button>
          </form>

          <div className="login-footer">
            <p>
              Don&apos;t have an account?{' '}
              <Link to="/register" id="go-to-register" className="link">
                Create one now
              </Link>
            </p>
          </div>
        </div>
      </div>

      <style>{`
        .login-page {
          min-height: 100vh;
          background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 2rem 1rem;
          font-family: 'Inter', sans-serif;
        }
        .login-container {
          width: 100%;
          max-width: 420px;
          display: flex;
          flex-direction: column;
          gap: 1.5rem;
        }
        .login-brand {
          text-align: center;
        }
        .brand-icon {
          font-size: 3rem;
          margin-bottom: 0.5rem;
          display: block;
        }
        .brand-name {
          font-size: 2rem;
          font-weight: 800;
          background: linear-gradient(90deg, #f97316, #fb923c);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          margin: 0;
        }
        .brand-tagline {
          color: rgba(255,255,255,0.55);
          font-size: 0.85rem;
          margin: 0.25rem 0 0;
        }
        .login-card {
          background: rgba(255,255,255,0.06);
          backdrop-filter: blur(20px);
          -webkit-backdrop-filter: blur(20px);
          border: 1px solid rgba(255,255,255,0.12);
          border-radius: 1.25rem;
          padding: 2rem;
          box-shadow: 0 25px 50px rgba(0,0,0,0.4);
        }
        .login-title {
          font-size: 1.5rem;
          font-weight: 700;
          color: #fff;
          margin: 0 0 0.25rem;
        }
        .login-subtitle {
          color: rgba(255,255,255,0.5);
          font-size: 0.875rem;
          margin: 0 0 1.5rem;
        }
        .alert {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          padding: 0.75rem 1rem;
          border-radius: 0.625rem;
          font-size: 0.875rem;
          margin-bottom: 1rem;
        }
        .alert-error {
          background: rgba(239,68,68,0.15);
          border: 1px solid rgba(239,68,68,0.3);
          color: #fca5a5;
        }
        .alert-icon { font-size: 1rem; }
        .login-form {
          display: flex;
          flex-direction: column;
          gap: 1.25rem;
        }
        .form-group {
          display: flex;
          flex-direction: column;
          gap: 0.4rem;
        }
        .form-label {
          color: rgba(255,255,255,0.75);
          font-size: 0.875rem;
          font-weight: 500;
        }
        .input-wrapper {
          position: relative;
          display: flex;
          align-items: center;
        }
        .form-input {
          width: 100%;
          padding: 0.75rem 1rem;
          background: rgba(255,255,255,0.08);
          border: 1px solid rgba(255,255,255,0.15);
          border-radius: 0.625rem;
          color: #fff;
          font-size: 0.95rem;
          transition: border-color 0.2s, box-shadow 0.2s;
          box-sizing: border-box;
        }
        .form-input::placeholder { color: rgba(255,255,255,0.3); }
        .form-input:focus {
          outline: none;
          border-color: #f97316;
          box-shadow: 0 0 0 3px rgba(249,115,22,0.2);
        }
        .toggle-password {
          position: absolute;
          right: 0.75rem;
          background: none;
          border: none;
          cursor: pointer;
          font-size: 1.1rem;
          color: rgba(255,255,255,0.5);
          padding: 0.25rem;
          line-height: 1;
        }
        .btn {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          gap: 0.5rem;
          padding: 0.75rem 1.5rem;
          border-radius: 0.625rem;
          font-size: 0.95rem;
          font-weight: 600;
          cursor: pointer;
          border: none;
          transition: all 0.2s;
        }
        .btn-primary {
          background: linear-gradient(135deg, #f97316, #ea580c);
          color: #fff;
          box-shadow: 0 4px 15px rgba(249,115,22,0.35);
        }
        .btn-primary:hover:not(:disabled) {
          transform: translateY(-1px);
          box-shadow: 0 6px 20px rgba(249,115,22,0.45);
        }
        .btn-primary:active:not(:disabled) { transform: translateY(0); }
        .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
        .btn-full { width: 100%; margin-top: 0.25rem; }
        .spinner {
          width: 18px; height: 18px;
          border: 2px solid rgba(255,255,255,0.3);
          border-top-color: #fff;
          border-radius: 50%;
          animation: spin 0.7s linear infinite;
        }
        @keyframes spin { to { transform: rotate(360deg); } }
        .login-footer {
          text-align: center;
          margin-top: 1.25rem;
          color: rgba(255,255,255,0.5);
          font-size: 0.875rem;
        }
        .link {
          color: #f97316;
          text-decoration: none;
          font-weight: 600;
        }
        .link:hover { text-decoration: underline; }
      `}</style>
    </div>
  );
}

export default LoginPage;
