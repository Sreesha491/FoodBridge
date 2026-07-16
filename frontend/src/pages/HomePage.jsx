import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '@/components/Navbar';

// ─── Stat Card Component ───────────────────────────────────────────
function StatCard({ value, label, icon }) {
  return (
    <div className="card-hover p-6 text-center animate-fade-in">
      <div className="text-3xl mb-2">{icon}</div>
      <div className="font-heading text-3xl font-bold gradient-text">{value}</div>
      <div className="text-neutral-400 text-sm mt-1">{label}</div>
    </div>
  );
}

// ─── Feature Card Component ────────────────────────────────────────
function FeatureCard({ icon, title, description }) {
  return (
    <div className="card-hover p-6 group animate-fade-in">
      <div className="w-12 h-12 rounded-xl bg-primary-500/10 border border-primary-500/20
                      flex items-center justify-center text-2xl mb-4
                      group-hover:bg-primary-500/20 transition-colors duration-300">
        {icon}
      </div>
      <h3 className="font-heading font-semibold text-white mb-2">{title}</h3>
      <p className="text-neutral-400 text-sm leading-relaxed">{description}</p>
    </div>
  );
}

// ─── Step Card Component ───────────────────────────────────────────
function StepCard({ step, title, description, forRole }) {
  const roleColors = {
    donor:     'from-primary-500 to-primary-700',
    recipient: 'from-accent-500 to-accent-700',
  };

  return (
    <div className="flex gap-4 animate-slide-in-left">
      <div className={`flex-shrink-0 w-10 h-10 rounded-full bg-gradient-to-br ${roleColors[forRole]} flex items-center justify-center font-bold text-white text-sm shadow-glow`}>
        {step}
      </div>
      <div>
        <h4 className="font-semibold text-white mb-1">{title}</h4>
        <p className="text-neutral-400 text-sm">{description}</p>
      </div>
    </div>
  );
}

/**
 * HomePage – FoodBridge landing page (Phase 1 scaffold).
 *
 * Sections:
 * 1. Hero          – headline, CTA, animated badge, health status
 * 2. Stats         – impact numbers
 * 3. Features      – platform capabilities
 * 4. How It Works  – donor vs recipient journey
 * 5. CTA Banner    – conversion section
 * 6. Footer        – links and copyright
 *
 * In Phase 2+ this page will surface live donation listings,
 * geolocation search, and personalized recommendations.
 */
function HomePage() {
  const [apiStatus, setApiStatus] = useState('checking'); // 'checking' | 'online' | 'offline'

  // Check backend connectivity on mount
  // Backend context-path is /api, so actuator is at /api/actuator/health
  useEffect(() => {
    fetch('/api/actuator/health')
      .then((res) => {
        if (res.ok) setApiStatus('online');
        else setApiStatus('offline');
      })
      .catch(() => setApiStatus('offline'));
  }, []);

  const stats = [
    { value: '0 kg',    label: 'Food Saved',     icon: '🥗' },
    { value: '0',       label: 'Meals Served',   icon: '🍽️' },
    { value: '0',       label: 'Active Donors',  icon: '🤝' },
    { value: '0 kg',    label: 'CO₂ Offset',     icon: '🌿' },
  ];

  const features = [
    {
      icon: '📋',
      title: 'Smart Food Listings',
      description: 'Donors post surplus food with expiry, quantity, allergen info, and pickup windows in under a minute.',
    },
    {
      icon: '📍',
      title: 'Geolocation Matching',
      description: 'Recipients instantly discover available donations nearby, sorted by distance and urgency.',
    },
    {
      icon: '🔔',
      title: 'Real-time Notifications',
      description: 'Instant alerts when new food is listed in your area or when a request is accepted.',
    },
    {
      icon: '📊',
      title: 'Impact Dashboard',
      description: 'Track your contribution — kilograms saved, meals served, and carbon emissions offset.',
    },
    {
      icon: '🔒',
      title: 'Verified Community',
      description: 'Role-based access ensures donors, recipients, and admins each see what matters to them.',
    },
    {
      icon: '⚡',
      title: 'Lightning Fast',
      description: 'Built on Java 21 Spring Boot with MongoDB Atlas for high-throughput, low-latency API responses.',
    },
  ];

  return (
    <>
      {/* Global navigation with Sign In / Get Started buttons */}
      <Navbar />

      {/* ═══ SECTION 1 – HERO ═══════════════════════════════════════ */}
      <section
        id="hero"
        className="relative bg-hero-gradient noise-overlay pt-32 pb-24 overflow-hidden"
        aria-labelledby="hero-heading"
      >
        {/* Decorative blobs */}
        <div className="absolute top-20 left-1/4 w-96 h-96 bg-primary-500/10 rounded-full blur-3xl pointer-events-none" aria-hidden="true" />
        <div className="absolute bottom-0 right-1/4 w-64 h-64 bg-accent-500/10 rounded-full blur-3xl pointer-events-none" aria-hidden="true" />

        <div className="container-section relative z-10">
          <div className="max-w-4xl mx-auto text-center">

            {/* API Health Badge */}
            <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full
                            bg-neutral-900/80 border border-neutral-800 text-xs text-neutral-400
                            mb-8 animate-fade-in" role="status" aria-live="polite">
              <span
                className={`w-2 h-2 rounded-full ${
                  apiStatus === 'online'   ? 'bg-primary-500 animate-pulse' :
                  apiStatus === 'offline'  ? 'bg-red-500' :
                  'bg-neutral-500 animate-pulse'
                }`}
                aria-hidden="true"
              />
              <span>
                {apiStatus === 'online'   && 'API Online · Backend Connected'}
                {apiStatus === 'offline'  && 'API Offline · Start the backend server'}
                {apiStatus === 'checking' && 'Connecting to API…'}
              </span>
            </div>

            {/* Headline */}
            <h1
              id="hero-heading"
              className="font-heading text-5xl sm:text-6xl lg:text-7xl font-extrabold
                         text-white leading-[1.1] mb-6 animate-fade-in"
            >
              Bridge the Gap Between{' '}
              <span className="gradient-text">Food Waste</span>{' '}
              and{' '}
              <span className="text-accent-400">Hunger</span>
            </h1>

            {/* Sub-headline */}
            <p className="text-neutral-400 text-lg sm:text-xl max-w-2xl mx-auto mb-10 animate-fade-in leading-relaxed">
              FoodBridge connects restaurants, households, and events with NGOs,
              food banks, and individuals — turning surplus food into hope.
            </p>

            {/* CTA Buttons */}
            <div className="flex flex-col sm:flex-row gap-4 justify-center animate-fade-in">
              <Link
                id="cta-get-started"
                to="/register"
                className="btn-primary px-8 py-3 text-base"
                aria-label="Create a free account"
              >
                🚀 Get Started Free
              </Link>
              <Link
                id="cta-sign-in"
                to="/login"
                className="btn-outline px-8 py-3 text-base"
                aria-label="Sign in to your account"
              >
                🔑 Sign In
              </Link>
            </div>

            {/* Trust badges */}
            <div className="flex items-center justify-center gap-6 mt-12 text-neutral-500 text-xs animate-fade-in">
              <span className="flex items-center gap-1.5"><span>✓</span> Free to use</span>
              <span className="w-px h-4 bg-neutral-700" aria-hidden="true" />
              <span className="flex items-center gap-1.5"><span>✓</span> No food wasted</span>
              <span className="w-px h-4 bg-neutral-700" aria-hidden="true" />
              <span className="flex items-center gap-1.5"><span>✓</span> Verified community</span>
            </div>
          </div>
        </div>
      </section>

      {/* ════════════════════════════════════════════════════════════
          SECTION 2 – IMPACT STATS
          ════════════════════════════════════════════════════════════ */}
      <section id="impact" className="py-20 bg-neutral-950" aria-labelledby="stats-heading">
        <div className="container-section">
          <h2 id="stats-heading" className="sr-only">Platform Impact Statistics</h2>
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            {stats.map((stat) => (
              <StatCard key={stat.label} {...stat} />
            ))}
          </div>
          <p className="text-center text-neutral-600 text-xs mt-6">
            Stats will update as the platform goes live · Phase 2+
          </p>
        </div>
      </section>

      {/* ════════════════════════════════════════════════════════════
          SECTION 3 – FEATURES
          ════════════════════════════════════════════════════════════ */}
      <section id="features" className="py-24 bg-neutral-900/40" aria-labelledby="features-heading">
        <div className="container-section">
          <div className="text-center mb-16">
            <span className="badge-green mb-4">Platform Features</span>
            <h2 id="features-heading" className="font-heading text-4xl font-bold text-white mt-4 mb-4">
              Everything you need to{' '}
              <span className="gradient-text">reduce waste</span>
            </h2>
            <p className="text-neutral-400 max-w-xl mx-auto">
              A complete ecosystem designed to make food donation effortless,
              transparent, and impactful.
            </p>
          </div>

          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {features.map((feat) => (
              <FeatureCard key={feat.title} {...feat} />
            ))}
          </div>
        </div>
      </section>

      {/* ════════════════════════════════════════════════════════════
          SECTION 4 – HOW IT WORKS
          ════════════════════════════════════════════════════════════ */}
      <section id="how-it-works" className="py-24 bg-neutral-950" aria-labelledby="how-heading">
        <div className="container-section">
          <div className="text-center mb-16">
            <span className="badge-amber mb-4">Simple Process</span>
            <h2 id="how-heading" className="font-heading text-4xl font-bold text-white mt-4 mb-4">
              How <span className="gradient-text">FoodBridge</span> Works
            </h2>
            <p className="text-neutral-400 max-w-xl mx-auto">
              Whether you're sharing surplus food or looking for a meal — it only takes a few steps.
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-12 max-w-4xl mx-auto">
            {/* Donor Journey */}
            <div className="card p-8">
              <div className="flex items-center gap-3 mb-8">
                <span className="badge-green text-sm px-3 py-1">For Donors</span>
                <span className="text-xl">🍱</span>
              </div>
              <div className="flex flex-col gap-6">
                <StepCard step="1" forRole="donor" title="Create a Listing" description="Post surplus food with quantity, expiry, location, and pickup time." />
                <StepCard step="2" forRole="donor" title="Get Matched" description="Recipients nearby are notified instantly about your available food." />
                <StepCard step="3" forRole="donor" title="Confirm Pickup" description="Accept a request and hand over the food at the agreed time." />
                <StepCard step="4" forRole="donor" title="Track Your Impact" description="See how many meals you've contributed to on your dashboard." />
              </div>
            </div>

            {/* Recipient Journey */}
            <div className="card p-8">
              <div className="flex items-center gap-3 mb-8">
                <span className="badge-amber text-sm px-3 py-1">For Recipients</span>
                <span className="text-xl">🤝</span>
              </div>
              <div className="flex flex-col gap-6">
                <StepCard step="1" forRole="recipient" title="Browse Listings" description="Search available donations near you filtered by food type and distance." />
                <StepCard step="2" forRole="recipient" title="Request Food" description="Send a pickup request to the donor with your preferred time." />
                <StepCard step="3" forRole="recipient" title="Pick Up" description="Collect the food at the agreed location and time." />
                <StepCard step="4" forRole="recipient" title="Confirm Receipt" description="Mark the donation as received and leave a thank-you note." />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ════════════════════════════════════════════════════════════
          SECTION 5 – CTA BANNER
          ════════════════════════════════════════════════════════════ */}
      <section id="cta" className="py-24 bg-gradient-to-r from-primary-900/40 via-neutral-950 to-accent-900/30" aria-labelledby="cta-heading">
        <div className="container-section text-center">
          <h2 id="cta-heading" className="font-heading text-4xl sm:text-5xl font-bold text-white mb-6">
            Ready to Make a <span className="gradient-text">Difference</span>?
          </h2>
          <p className="text-neutral-400 text-lg max-w-xl mx-auto mb-10">
            Join FoodBridge today. Every meal rescued is a step toward
            a world without food waste.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link id="cta-join-donor" to="/register" className="btn-primary px-10 py-3 text-base">
              Start Donating
            </Link>
            <Link id="cta-join-recipient" to="/register" className="btn-outline px-10 py-3 text-base">
              Find Food Near Me
            </Link>
          </div>
        </div>
      </section>

      {/* ════════════════════════════════════════════════════════════
          FOOTER
          ════════════════════════════════════════════════════════════ */}
      <footer className="bg-neutral-950 border-t border-neutral-900 py-12" role="contentinfo">
        <div className="container-section">
          <div className="flex flex-col md:flex-row justify-between items-center gap-6">
            {/* Brand */}
            <div className="flex items-center gap-2">
              <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center">
                <span className="text-white text-xs font-bold">FB</span>
              </div>
              <span className="font-heading font-bold text-white">
                Food<span className="gradient-text">Bridge</span>
              </span>
            </div>

            {/* Links */}
            <nav aria-label="Footer navigation">
              <ul className="flex flex-wrap gap-6 list-none text-sm text-neutral-500">
                {['About', 'Privacy Policy', 'Terms of Use', 'Contact'].map((link) => (
                  <li key={link}>
                    <a href="#" className="hover:text-neutral-300 transition-colors no-underline">
                      {link}
                    </a>
                  </li>
                ))}
              </ul>
            </nav>

            {/* Phase badge */}
            <div className="badge-gray text-xs">
              Phase 1 · v1.0.0-SNAPSHOT
            </div>
          </div>

          <div className="divider" />

          <p className="text-center text-neutral-600 text-xs">
            © {new Date().getFullYear()} FoodBridge. Built with ❤️ to reduce food waste and fight hunger.
            Final Year Project.
          </p>
        </div>
      </footer>
    </>
  );
}

export default HomePage;
