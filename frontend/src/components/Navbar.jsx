import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';

/**
 * Global navigation bar for FoodBridge.
 *
 * Features:
 * - Brand logo with gradient text
 * - Navigation links
 * - Authentication state (login/logout/dashboard)
 * - Glass-morphism effect on scroll
 * - Mobile hamburger menu
 */
function Navbar() {
  const [isScrolled, setIsScrolled] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  // Apply glass effect when user scrolls down
  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleLogout = async () => {
    await logout();
    navigate('/');
    setIsMobileMenuOpen(false);
  };

  const navLinks = [
    { label: 'How It Works', href: '/#how-it-works' },
    { label: 'Donate Food',   href: '/#donate' },
    { label: 'Find Food',     href: '/#find' },
    { label: 'Impact',        href: '/#impact' },
  ];

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
        isScrolled
          ? 'glass-dark border-b border-neutral-800/60 shadow-soft'
          : 'bg-transparent'
      }`}
      role="banner"
    >
      <div className="container-section">
        <nav
          className="flex items-center justify-between h-16"
          aria-label="Main navigation"
        >
          {/* ── Brand Logo ────────────────────────────────────── */}
          <Link
            to="/"
            className="flex items-center gap-2 group no-underline"
            aria-label="FoodBridge home"
          >
            {/* Icon */}
            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center shadow-glow group-hover:shadow-glow transition-shadow duration-300">
              <span className="text-white text-sm font-bold select-none">FB</span>
            </div>

            {/* Name */}
            <span className="font-heading font-bold text-xl text-white">
              Food<span className="gradient-text">Bridge</span>
            </span>
          </Link>

          {/* ── Desktop Nav Links ──────────────────────────────── */}
          <ul className="hidden md:flex items-center gap-1 list-none" role="list">
            {navLinks.map((link) => (
              <li key={link.label}>
                <a
                  href={link.href}
                  className="px-3 py-2 rounded-lg text-sm font-medium text-neutral-400
                             hover:text-neutral-100 hover:bg-neutral-800/60
                             transition-all duration-200 no-underline"
                >
                  {link.label}
                </a>
              </li>
            ))}
          </ul>

          {/* ── Desktop Auth CTA & Links ────────────────────────────── */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated ? (
              <>
                <Link to="/market" className="px-3 py-2 text-sm font-medium text-neutral-300 hover:text-white transition-colors">
                  Food Market
                </Link>
                <Link to="/orders" className="px-3 py-2 text-sm font-medium text-neutral-300 hover:text-white transition-colors">
                  Orders
                </Link>
                <Link to="/dashboard" className="btn-ghost text-sm" aria-label="Dashboard">
                  Dashboard
                </Link>
                <div className="flex items-center gap-2 bg-neutral-800/50 border border-neutral-700 rounded-full px-3 py-1">
                  <span className="text-sm font-medium text-white">{user?.name?.split(' ')[0] || 'User'}</span>
                </div>
                <button onClick={handleLogout} className="btn-outline text-sm py-1.5 px-3">
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="btn-ghost text-sm" aria-label="Sign in">
                  Sign In
                </Link>
                <Link to="/register" className="btn-primary text-sm" aria-label="Get started">
                  Get Started
                </Link>
              </>
            )}
          </div>

          {/* ── Mobile Hamburger ───────────────────────────────── */}
          <button
            id="mobile-menu-toggle"
            className="md:hidden btn-ghost p-2"
            aria-expanded={isMobileMenuOpen}
            aria-controls="mobile-menu"
            aria-label={isMobileMenuOpen ? 'Close menu' : 'Open menu'}
            onClick={() => setIsMobileMenuOpen((prev) => !prev)}
          >
            <span className="sr-only">{isMobileMenuOpen ? 'Close' : 'Menu'}</span>
            <div className="w-5 h-4 flex flex-col justify-between">
              <span className={`block h-0.5 bg-neutral-300 rounded transition-all duration-300 ${isMobileMenuOpen ? 'rotate-45 translate-y-[7px]' : ''}`} />
              <span className={`block h-0.5 bg-neutral-300 rounded transition-all duration-300 ${isMobileMenuOpen ? 'opacity-0' : ''}`} />
              <span className={`block h-0.5 bg-neutral-300 rounded transition-all duration-300 ${isMobileMenuOpen ? '-rotate-45 -translate-y-[9px]' : ''}`} />
            </div>
          </button>
        </nav>

        {/* ── Mobile Menu ─────────────────────────────────────── */}
        <div
          id="mobile-menu"
          className={`md:hidden overflow-hidden transition-all duration-300 ${
            isMobileMenuOpen ? 'max-h-[500px] pb-4 opacity-100' : 'max-h-0 opacity-0'
          }`}
          aria-hidden={!isMobileMenuOpen}
        >
          <ul className="flex flex-col gap-1 list-none pt-2 m-0 p-0">
            {navLinks.map((link) => (
              <li key={link.label}>
                <a
                  href={link.href}
                  className="block px-3 py-2.5 rounded-lg text-sm font-medium
                             text-neutral-400 hover:text-neutral-100 hover:bg-neutral-800
                             transition-all duration-200 no-underline"
                  onClick={() => setIsMobileMenuOpen(false)}
                >
                  {link.label}
                </a>
              </li>
            ))}
          </ul>
          <div className="flex flex-col gap-2 mt-4 pt-4 border-t border-neutral-800">
            {isAuthenticated ? (
              <>
                <div className="px-3 py-2 text-sm text-neutral-300 mb-2">
                  Signed in as <strong className="text-white">{user?.name}</strong>
                </div>
                <Link to="/market" onClick={() => setIsMobileMenuOpen(false)} className="btn-ghost w-full justify-center text-sm">
                  Food Market
                </Link>
                <Link to="/orders" onClick={() => setIsMobileMenuOpen(false)} className="btn-ghost w-full justify-center text-sm">
                  Orders
                </Link>
                <Link to="/dashboard" onClick={() => setIsMobileMenuOpen(false)} className="btn-primary w-full justify-center text-sm">
                  Dashboard
                </Link>
                <button onClick={handleLogout} className="btn-outline w-full justify-center text-sm">
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" onClick={() => setIsMobileMenuOpen(false)} className="btn-outline w-full justify-center text-sm">
                  Sign In
                </Link>
                <Link to="/register" onClick={() => setIsMobileMenuOpen(false)} className="btn-primary w-full justify-center text-sm">
                  Get Started
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}

export default Navbar;
