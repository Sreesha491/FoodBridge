/** @type {import('tailwindcss').Config} */
export default {
  // ── Content Paths ──────────────────────────────────────────────────────
  // Tailwind scans these files to determine which classes are used.
  // Only classes found here are included in the production CSS bundle.
  content: [
    './index.html',
    './src/**/*.{js,jsx,ts,tsx}',
  ],

  // ── Dark Mode ──────────────────────────────────────────────────────────
  // 'class' strategy: dark mode is toggled by adding 'dark' to <html>.
  darkMode: 'class',

  // ── Theme Extension ────────────────────────────────────────────────────
  theme: {
    extend: {
      // ── Brand Color Palette ─────────────────────────────────────────
      colors: {
        // Primary – warm green (food, nature, sustainability)
        primary: {
          50:  '#f0fdf4',
          100: '#dcfce7',
          200: '#bbf7d0',
          300: '#86efac',
          400: '#4ade80',
          500: '#22c55e',
          600: '#16a34a',  // Main brand color
          700: '#15803d',
          800: '#166534',
          900: '#14532d',
          950: '#052e16',
        },
        // Accent – warm amber (warmth, community, food)
        accent: {
          50:  '#fffbeb',
          100: '#fef3c7',
          200: '#fde68a',
          300: '#fcd34d',
          400: '#fbbf24',
          500: '#f59e0b',  // Main accent color
          600: '#d97706',
          700: '#b45309',
          800: '#92400e',
          900: '#78350f',
        },
        // Neutral – slate gray for text and backgrounds
        neutral: {
          50:  '#f8fafc',
          100: '#f1f5f9',
          200: '#e2e8f0',
          300: '#cbd5e1',
          400: '#94a3b8',
          500: '#64748b',
          600: '#475569',
          700: '#334155',
          800: '#1e293b',
          900: '#0f172a',
          950: '#020617',
        },
        // Semantic – success, warning, error, info
        success: '#22c55e',
        warning: '#f59e0b',
        error:   '#ef4444',
        info:    '#3b82f6',
      },

      // ── Typography ──────────────────────────────────────────────────
      fontFamily: {
        sans:  ['Inter', 'system-ui', 'sans-serif'],
        heading: ['Outfit', 'Inter', 'sans-serif'],
        mono:  ['JetBrains Mono', 'Fira Code', 'monospace'],
      },

      // ── Border Radius ────────────────────────────────────────────────
      borderRadius: {
        'xl':  '0.75rem',
        '2xl': '1rem',
        '3xl': '1.5rem',
        '4xl': '2rem',
      },

      // ── Box Shadow ───────────────────────────────────────────────────
      boxShadow: {
        'soft':      '0 2px 15px -3px rgba(0, 0, 0, 0.07), 0 10px 20px -2px rgba(0, 0, 0, 0.04)',
        'card':      '0 4px 24px -4px rgba(0, 0, 0, 0.12)',
        'card-hover':'0 8px 32px -4px rgba(0, 0, 0, 0.18)',
        'glow':      '0 0 20px rgba(34, 197, 94, 0.3)',
        'glow-accent':'0 0 20px rgba(245, 158, 11, 0.3)',
      },

      // ── Animation / Keyframes ────────────────────────────────────────
      keyframes: {
        fadeIn: {
          '0%':   { opacity: '0', transform: 'translateY(10px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        slideInLeft: {
          '0%':   { opacity: '0', transform: 'translateX(-20px)' },
          '100%': { opacity: '1', transform: 'translateX(0)' },
        },
        pulse: {
          '0%, 100%': { opacity: '1' },
          '50%':      { opacity: '0.5' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%':      { transform: 'translateY(-6px)' },
        },
      },
      animation: {
        'fade-in':      'fadeIn 0.5s ease-out forwards',
        'slide-in-left':'slideInLeft 0.4s ease-out forwards',
        'float':        'float 3s ease-in-out infinite',
      },

      // ── Backdrop Blur ────────────────────────────────────────────────
      backdropBlur: {
        xs: '2px',
      },

      // ── Screen Breakpoints ───────────────────────────────────────────
      screens: {
        'xs': '475px',
        // sm, md, lg, xl, 2xl remain Tailwind defaults
      },
    },
  },

  // ── Plugins ────────────────────────────────────────────────────────────
  plugins: [],
};
