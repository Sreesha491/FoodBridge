import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

/**
 * Vite configuration for FoodBridge frontend.
 *
 * Key settings:
 *  - React plugin for JSX transform + HMR
 *  - Path alias: '@' → src/ (clean imports everywhere)
 *  - Dev server on port 5173 with API proxy to backend (8080)
 *  - Build output to dist/ with source maps for debugging
 *
 * @see https://vitejs.dev/config/
 */
export default defineConfig({
  plugins: [
    react(),
  ],

  resolve: {
    alias: {
      // Use '@/components/...' instead of '../../components/...'
      '@': path.resolve(__dirname, './src'),
    },
  },

  server: {
    port: 5173,
    host: true,           // Bind to 0.0.0.0 so Docker can expose the port
    strictPort: true,     // Fail if port is already in use
    open: false,          // Don't auto-open browser in Docker

    // Proxy API calls to the Spring Boot backend
    // Any request to /api/* is forwarded to localhost:8080
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
      },
      '/actuator': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
      },
    },
  },

  preview: {
    port: 5173,
    host: true,
  },

  build: {
    outDir: 'dist',
    sourcemap: true,
    rollupOptions: {
      output: {
        // Separate vendor chunk for better caching
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom'],
          http: ['axios'],
        },
      },
    },
  },
});
