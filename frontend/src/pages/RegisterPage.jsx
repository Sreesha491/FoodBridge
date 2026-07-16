import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';

const ROLES = [
  { value: 'DONOR', label: '🍱 Donor', desc: 'Individual or organisation donating food' },
  { value: 'RESTAURANT', label: '🏪 Restaurant', desc: 'Restaurant contributing surplus food' },
  { value: 'NGO', label: '🤝 NGO', desc: 'Non-governmental organisation receiving donations' },
  { value: 'DELIVERY_PARTNER', label: '🚚 Delivery Partner', desc: 'Transport donations to recipients' },
];

/**
 * Registration page for FoodBridge.
 *
 * Collects name, email, password, and role; calls the backend register endpoint;
 * persists JWT via AuthContext; then navigates to /dashboard.
 */
function RegisterPage() {
  const { register, isLoading } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'DONOR',
    phone: '',
  });
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [step, setStep] = useState(1); // 2-step form: 1=basic info, 2=role selection

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError('');
  };

  const validateStep1 = () => {
    if (!form.name.trim()) return 'Full name is required.';
    if (!form.email.trim()) return 'Email is required.';
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) return 'Please enter a valid email.';
    if (form.password.length < 8) return 'Password must be at least 8 characters.';
    if (form.password !== form.confirmPassword) return 'Passwords do not match.';
    return null;
  };

  const handleNextStep = () => {
    const err = validateStep1();
    if (err) { setError(err); return; }
    setError('');
    setStep(2);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await register({
        name: form.name.trim(),
        email: form.email.trim().toLowerCase(),
        password: form.password,
        role: form.role,
        phone: form.phone.trim() || undefined,
      });
      navigate('/dashboard', { replace: true });
    } catch (err) {
      const msg = err.response?.data?.message || 'Registration failed. Please try again.';
      setError(msg);
      setStep(1);
    }
  };

  return (
    <div className="register-page">
      <div className="register-container">
        {/* Brand */}
        <div className="register-brand">
          <div className="brand-icon">🌉</div>
          <h1 className="brand-name">FoodBridge</h1>
          <p className="brand-tagline">Join the movement to reduce food waste</p>
        </div>

        {/* Progress dots */}
        <div className="steps-indicator" aria-label="Registration step">
          <span className={`step-dot ${step >= 1 ? 'active' : ''}`} />
          <span className="step-line" />
          <span className={`step-dot ${step >= 2 ? 'active' : ''}`} />
        </div>

        {/* Card */}
        <div className="register-card">
          <h2 className="register-title">
            {step === 1 ? 'Create your account' : 'Choose your role'}
          </h2>
          <p className="register-subtitle">
            {step === 1 ? 'Step 1 of 2 – Basic information' : 'Step 2 of 2 – How will you use FoodBridge?'}
          </p>

          {error && (
            <div className="alert alert-error" role="alert" id="register-error">
              <span className="alert-icon">⚠️</span>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} noValidate>
            {/* ── Step 1: Basic Info ─────────────────── */}
            {step === 1 && (
              <div className="form-fields">
                <div className="form-group">
                  <label htmlFor="reg-name" className="form-label">Full name</label>
                  <input id="reg-name" name="name" type="text" autoComplete="name" required
                    value={form.name} onChange={handleChange} className="form-input"
                    placeholder="John Doe" />
                </div>

                <div className="form-group">
                  <label htmlFor="reg-email" className="form-label">Email address</label>
                  <input id="reg-email" name="email" type="email" autoComplete="email" required
                    value={form.email} onChange={handleChange} className="form-input"
                    placeholder="you@example.com" />
                </div>

                <div className="form-group">
                  <label htmlFor="reg-phone" className="form-label">Phone number <span className="optional">(optional)</span></label>
                  <input id="reg-phone" name="phone" type="tel" autoComplete="tel"
                    value={form.phone} onChange={handleChange} className="form-input"
                    placeholder="+91 98765 43210" />
                </div>

                <div className="form-group">
                  <label htmlFor="reg-password" className="form-label">Password</label>
                  <div className="input-wrapper">
                    <input id="reg-password" name="password" type={showPassword ? 'text' : 'password'}
                      autoComplete="new-password" required
                      value={form.password} onChange={handleChange} className="form-input"
                      placeholder="Minimum 8 characters" />
                    <button type="button" className="toggle-password"
                      onClick={() => setShowPassword((v) => !v)}
                      aria-label={showPassword ? 'Hide password' : 'Show password'}>
                      {showPassword ? '🙈' : '👁️'}
                    </button>
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="reg-confirm" className="form-label">Confirm password</label>
                  <input id="reg-confirm" name="confirmPassword" type="password"
                    autoComplete="new-password" required
                    value={form.confirmPassword} onChange={handleChange} className="form-input"
                    placeholder="Re-enter your password" />
                </div>

                <button id="reg-next-btn" type="button" className="btn btn-primary btn-full"
                  onClick={handleNextStep}>
                  Continue →
                </button>
              </div>
            )}

            {/* ── Step 2: Role Selection ─────────────── */}
            {step === 2 && (
              <div className="form-fields">
                <div className="role-grid" role="group" aria-label="Choose your role">
                  {ROLES.map((r) => (
                    <label key={r.value} className={`role-card ${form.role === r.value ? 'selected' : ''}`}
                      htmlFor={`role-${r.value}`}>
                      <input id={`role-${r.value}`} type="radio" name="role"
                        value={r.value} checked={form.role === r.value}
                        onChange={handleChange} className="role-radio" />
                      <span className="role-label">{r.label}</span>
                      <span className="role-desc">{r.desc}</span>
                    </label>
                  ))}
                </div>

                <div className="step2-actions">
                  <button type="button" id="reg-back-btn" className="btn btn-outline"
                    onClick={() => setStep(1)}>
                    ← Back
                  </button>
                  <button id="reg-submit-btn" type="submit" className="btn btn-primary flex-1"
                    disabled={isLoading}>
                    {isLoading ? (
                      <><span className="spinner" />Creating account…</>
                    ) : 'Create account'}
                  </button>
                </div>
              </div>
            )}
          </form>

          <div className="register-footer">
            <p>
              Already have an account?{' '}
              <Link to="/login" id="go-to-login" className="link">Sign in</Link>
            </p>
          </div>
        </div>
      </div>

      <style>{`
        .register-page {
          min-height: 100vh;
          background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 2rem 1rem;
          font-family: 'Inter', sans-serif;
        }
        .register-container {
          width: 100%;
          max-width: 460px;
          display: flex;
          flex-direction: column;
          gap: 1.25rem;
        }
        .register-brand { text-align: center; }
        .brand-icon { font-size: 2.5rem; display: block; margin-bottom: 0.35rem; }
        .brand-name {
          font-size: 1.75rem;
          font-weight: 800;
          background: linear-gradient(90deg, #f97316, #fb923c);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          margin: 0;
        }
        .brand-tagline { color: rgba(255,255,255,0.5); font-size: 0.85rem; margin: 0.2rem 0 0; }
        .steps-indicator {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 0;
        }
        .step-dot {
          width: 12px;
          height: 12px;
          border-radius: 50%;
          background: rgba(255,255,255,0.2);
          transition: background 0.3s;
        }
        .step-dot.active { background: #f97316; }
        .step-line {
          flex: 0 0 60px;
          height: 2px;
          background: rgba(255,255,255,0.15);
          margin: 0 0.5rem;
        }
        .register-card {
          background: rgba(255,255,255,0.06);
          backdrop-filter: blur(20px);
          -webkit-backdrop-filter: blur(20px);
          border: 1px solid rgba(255,255,255,0.12);
          border-radius: 1.25rem;
          padding: 2rem;
          box-shadow: 0 25px 50px rgba(0,0,0,0.4);
        }
        .register-title {
          font-size: 1.4rem;
          font-weight: 700;
          color: #fff;
          margin: 0 0 0.2rem;
        }
        .register-subtitle {
          color: rgba(255,255,255,0.45);
          font-size: 0.8rem;
          margin: 0 0 1.25rem;
        }
        .alert {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          padding: 0.7rem 1rem;
          border-radius: 0.625rem;
          font-size: 0.875rem;
          margin-bottom: 1rem;
        }
        .alert-error {
          background: rgba(239,68,68,0.15);
          border: 1px solid rgba(239,68,68,0.3);
          color: #fca5a5;
        }
        .form-fields { display: flex; flex-direction: column; gap: 1rem; }
        .form-group { display: flex; flex-direction: column; gap: 0.35rem; }
        .form-label {
          color: rgba(255,255,255,0.75);
          font-size: 0.85rem;
          font-weight: 500;
        }
        .optional { color: rgba(255,255,255,0.35); font-weight: 400; }
        .input-wrapper { position: relative; display: flex; align-items: center; }
        .form-input {
          width: 100%;
          padding: 0.7rem 1rem;
          background: rgba(255,255,255,0.08);
          border: 1px solid rgba(255,255,255,0.15);
          border-radius: 0.625rem;
          color: #fff;
          font-size: 0.9rem;
          transition: border-color 0.2s, box-shadow 0.2s;
          box-sizing: border-box;
        }
        .form-input::placeholder { color: rgba(255,255,255,0.25); }
        .form-input:focus {
          outline: none;
          border-color: #f97316;
          box-shadow: 0 0 0 3px rgba(249,115,22,0.2);
        }
        .toggle-password {
          position: absolute; right: 0.75rem;
          background: none; border: none; cursor: pointer;
          font-size: 1rem; color: rgba(255,255,255,0.4); padding: 0.2rem;
        }
        .role-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }
        .role-card {
          display: flex; flex-direction: column; gap: 0.25rem;
          padding: 0.9rem; border-radius: 0.75rem;
          border: 1.5px solid rgba(255,255,255,0.12);
          cursor: pointer;
          transition: all 0.2s;
          background: rgba(255,255,255,0.04);
        }
        .role-card:hover { border-color: rgba(249,115,22,0.4); background: rgba(249,115,22,0.06); }
        .role-card.selected {
          border-color: #f97316;
          background: rgba(249,115,22,0.12);
          box-shadow: 0 0 0 2px rgba(249,115,22,0.2);
        }
        .role-radio { display: none; }
        .role-label { color: #fff; font-size: 0.9rem; font-weight: 600; }
        .role-desc { color: rgba(255,255,255,0.4); font-size: 0.72rem; line-height: 1.3; }
        .step2-actions { display: flex; gap: 0.75rem; margin-top: 0.25rem; }
        .flex-1 { flex: 1; }
        .btn {
          display: inline-flex; align-items: center; justify-content: center; gap: 0.5rem;
          padding: 0.75rem 1.5rem; border-radius: 0.625rem;
          font-size: 0.9rem; font-weight: 600; cursor: pointer; border: none;
          transition: all 0.2s;
        }
        .btn-primary {
          background: linear-gradient(135deg, #f97316, #ea580c);
          color: #fff;
          box-shadow: 0 4px 15px rgba(249,115,22,0.3);
        }
        .btn-primary:hover:not(:disabled) {
          transform: translateY(-1px);
          box-shadow: 0 6px 20px rgba(249,115,22,0.4);
        }
        .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
        .btn-outline {
          background: transparent;
          color: rgba(255,255,255,0.7);
          border: 1px solid rgba(255,255,255,0.2);
        }
        .btn-outline:hover { border-color: rgba(255,255,255,0.4); color: #fff; }
        .btn-full { width: 100%; margin-top: 0.25rem; }
        .spinner {
          width: 16px; height: 16px;
          border: 2px solid rgba(255,255,255,0.3);
          border-top-color: #fff;
          border-radius: 50%;
          animation: spin 0.7s linear infinite;
        }
        @keyframes spin { to { transform: rotate(360deg); } }
        .register-footer {
          text-align: center; margin-top: 1.25rem;
          color: rgba(255,255,255,0.45); font-size: 0.85rem;
        }
        .link { color: #f97316; text-decoration: none; font-weight: 600; }
        .link:hover { text-decoration: underline; }
      `}</style>
    </div>
  );
}

export default RegisterPage;
