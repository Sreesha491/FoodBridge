import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import dashboardService from '@/api/dashboardService';

const ROLE_LABELS = {
  ADMIN: 'Platform Administrator',
  DONOR: 'Food Donor',
  RESTAURANT: 'Restaurant Partner',
  NGO: 'NGO Partner',
  DELIVERY_PARTNER: 'Delivery Partner',
};

const ROLE_ICONS = {
  ADMIN: '⚙️',
  DONOR: '🍱',
  RESTAURANT: '🏪',
  NGO: '🤝',
  DELIVERY_PARTNER: '🚚',
};

/**
 * Dashboard page for FoodBridge.
 *
 * Displays a welcome message with the user's name and role,
 * and fetches real-time aggregated stats from the backend.
 */
function DashboardPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  
  const [stats, setStats] = useState({
    totalFoodItems: 0,
    activeDonations: 0,
    totalOrders: 0,
    deliveriesDone: 0
  });
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await dashboardService.getStats();
        setStats(data);
      } catch (error) {
        console.error("Failed to load dashboard stats", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchStats();
  }, []);

  const handleLogout = async () => {
    await logout();
    navigate('/login', { replace: true });
  };

  const roleLabel = ROLE_LABELS[user?.role] || user?.role || 'User';
  const roleIcon = ROLE_ICONS[user?.role] || '👤';

  return (
    <div className="dashboard">
      {/* Header */}
      <header className="dash-header">
        <div className="dash-logo">
          <span>🌉</span>
          <span className="dash-logo-text">FoodBridge</span>
        </div>
        <div className="dash-user">
          <div className="user-pill">
            <span className="user-role-icon">{roleIcon}</span>
            <span className="user-name">{user?.name || 'User'}</span>
          </div>
          <button id="dashboard-logout-btn" onClick={handleLogout} className="btn-logout">
            Sign out
          </button>
        </div>
      </header>

      {/* Main */}
      <main className="dash-main">
        {/* Welcome Banner */}
        <section className="welcome-banner">
          <div className="welcome-content">
            <p className="welcome-greeting">Good to see you back 👋</p>
            <h1 className="welcome-title">Hello, {user?.name?.split(' ')[0] || 'there'}!</h1>
            <p className="welcome-role">
              You are logged in as a{' '}
              <span className="role-badge">{roleIcon} {roleLabel}</span>
            </p>
          </div>
          <div className="welcome-visual">🌉</div>
        </section>

        {/* Quick Stats */}
        <section className="stats-section">
          <h2 className="section-title">Platform Overview</h2>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-icon">🍱</div>
              <div className="stat-label">Food Items Listed</div>
              <div className="stat-value">{isLoading ? '...' : stats.totalFoodItems}</div>
              <div className="stat-note">
                <Link to="/market" className="stat-link">View Food Market →</Link>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icon">🤝</div>
              <div className="stat-label">Active Donations</div>
              <div className="stat-value">{isLoading ? '...' : stats.activeDonations}</div>
              <div className="stat-note">Available for pickup</div>
            </div>
            <div className="stat-card">
              <div className="stat-icon">📦</div>
              <div className="stat-label">Orders Placed</div>
              <div className="stat-value">{isLoading ? '...' : stats.totalOrders}</div>
              <div className="stat-note">
                <Link to="/orders" className="stat-link">View Orders →</Link>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icon">🚚</div>
              <div className="stat-label">Deliveries Done</div>
              <div className="stat-value">{isLoading ? '...' : stats.deliveriesDone}</div>
              <div className="stat-note">Successfully delivered</div>
            </div>
          </div>
        </section>

        {/* API Explorer */}
        <section className="api-section">
          <div className="api-card">
            <div className="api-icon">📖</div>
            <div className="api-content">
              <h3>Explore the API</h3>
              <p>Full API documentation is available via Swagger UI — try any endpoint directly in your browser.</p>
            </div>
            <a
              id="swagger-link"
              href="http://localhost:8081/api/swagger-ui.html"
              target="_blank"
              rel="noreferrer"
              className="btn-api"
            >
              Open Swagger UI →
            </a>
          </div>
        </section>
      </main>

      <style>{`
        * { box-sizing: border-box; }
        .dashboard {
          min-height: 100vh;
          background: #0d0d1a;
          font-family: 'Inter', sans-serif;
          color: #fff;
        }
        .dash-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 1rem 2rem;
          background: rgba(255,255,255,0.04);
          border-bottom: 1px solid rgba(255,255,255,0.08);
          position: sticky;
          top: 0;
          z-index: 10;
          backdrop-filter: blur(12px);
        }
        .dash-logo {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          font-size: 1.4rem;
        }
        .dash-logo-text {
          font-weight: 800;
          background: linear-gradient(90deg, #f97316, #fb923c);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
        .dash-user {
          display: flex;
          align-items: center;
          gap: 1rem;
        }
        .user-pill {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          background: rgba(255,255,255,0.08);
          border: 1px solid rgba(255,255,255,0.12);
          border-radius: 2rem;
          padding: 0.4rem 0.9rem;
          font-size: 0.875rem;
        }
        .user-role-icon { font-size: 1rem; }
        .user-name { color: rgba(255,255,255,0.85); font-weight: 500; }
        .btn-logout {
          background: none;
          border: 1px solid rgba(255,255,255,0.15);
          color: rgba(255,255,255,0.6);
          padding: 0.4rem 0.9rem;
          border-radius: 0.5rem;
          font-size: 0.8rem;
          cursor: pointer;
          transition: all 0.2s;
        }
        .btn-logout:hover {
          border-color: rgba(239,68,68,0.4);
          color: #fca5a5;
          background: rgba(239,68,68,0.08);
        }
        .dash-main {
          max-width: 1100px;
          margin: 0 auto;
          padding: 2rem 1.5rem;
          display: flex;
          flex-direction: column;
          gap: 2.5rem;
        }
        .welcome-banner {
          background: linear-gradient(135deg, rgba(249,115,22,0.15), rgba(234,88,12,0.08));
          border: 1px solid rgba(249,115,22,0.2);
          border-radius: 1.25rem;
          padding: 2rem;
          display: flex;
          align-items: center;
          justify-content: space-between;
        }
        .welcome-greeting {
          color: rgba(255,255,255,0.5);
          font-size: 0.875rem;
          margin: 0 0 0.3rem;
        }
        .welcome-title {
          font-size: 2rem;
          font-weight: 800;
          margin: 0 0 0.5rem;
          background: linear-gradient(90deg, #fff, rgba(255,255,255,0.75));
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
        .welcome-role {
          color: rgba(255,255,255,0.6);
          font-size: 0.9rem;
          margin: 0;
        }
        .role-badge {
          background: rgba(249,115,22,0.15);
          border: 1px solid rgba(249,115,22,0.3);
          color: #fb923c;
          padding: 0.2rem 0.6rem;
          border-radius: 1rem;
          font-size: 0.85rem;
          font-weight: 600;
        }
        .welcome-visual {
          font-size: 4rem;
          opacity: 0.6;
        }
        .section-title {
          font-size: 1.1rem;
          font-weight: 700;
          color: rgba(255,255,255,0.7);
          margin: 0 0 1rem;
          text-transform: uppercase;
          letter-spacing: 0.05em;
        }
        .stats-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 1rem;
        }
        .stat-card {
          background: rgba(255,255,255,0.04);
          border: 1px solid rgba(255,255,255,0.08);
          border-radius: 1rem;
          padding: 1.5rem;
          display: flex;
          flex-direction: column;
          gap: 0.4rem;
          transition: all 0.2s;
        }
        .stat-card:hover {
          background: rgba(255,255,255,0.07);
          border-color: rgba(249,115,22,0.2);
          transform: translateY(-2px);
        }
        .stat-icon { font-size: 1.75rem; margin-bottom: 0.5rem; }
        .stat-value {
          font-size: 2.5rem;
          font-weight: 800;
          color: #fff;
          line-height: 1;
          margin-bottom: 0.25rem;
        }
        .stat-label {
          color: rgba(255,255,255,0.7);
          font-weight: 600;
          font-size: 0.9rem;
        }
        .stat-note {
          color: rgba(255,255,255,0.4);
          font-size: 0.8rem;
          margin-top: auto;
          padding-top: 0.5rem;
        }
        .stat-link {
          color: #f97316;
          text-decoration: none;
          font-weight: 500;
        }
        .stat-link:hover { text-decoration: underline; }
        .api-card {
          display: flex;
          align-items: center;
          gap: 1.25rem;
          background: rgba(255,255,255,0.04);
          border: 1px solid rgba(255,255,255,0.08);
          border-radius: 1rem;
          padding: 1.5rem;
        }
        .api-icon { font-size: 2rem; flex-shrink: 0; }
        .api-content { flex: 1; }
        .api-content h3 { margin: 0 0 0.25rem; font-size: 1rem; font-weight: 700; color: #fff; }
        .api-content p { margin: 0; color: rgba(255,255,255,0.5); font-size: 0.85rem; }
        .btn-api {
          flex-shrink: 0;
          display: inline-block;
          padding: 0.6rem 1.1rem;
          background: linear-gradient(135deg, #f97316, #ea580c);
          color: #fff;
          text-decoration: none;
          border-radius: 0.625rem;
          font-size: 0.85rem;
          font-weight: 600;
          white-space: nowrap;
          transition: all 0.2s;
          box-shadow: 0 4px 12px rgba(249,115,22,0.3);
        }
        .btn-api:hover {
          transform: translateY(-1px);
          box-shadow: 0 6px 18px rgba(249,115,22,0.4);
        }
        @media (max-width: 600px) {
          .dash-header { padding: 0.75rem 1rem; }
          .welcome-visual { display: none; }
          .welcome-title { font-size: 1.5rem; }
          .api-card { flex-direction: column; align-items: flex-start; }
        }
      `}</style>
    </div>
  );
}

export default DashboardPage;
