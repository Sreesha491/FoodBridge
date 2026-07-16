# ──────────────────────────────────────────────────────────────
#  FoodBridge Frontend – Dockerfile
#  Dev server (not production build) for Docker Compose usage.
#  For production, use a multi-stage build (node → nginx).
# ──────────────────────────────────────────────────────────────
FROM node:20-alpine

# Set working directory
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm ci --silent

# Copy source code
COPY . .

EXPOSE 5173

# Start Vite dev server bound to 0.0.0.0 (required for Docker)
CMD ["npm", "run", "dev"]
