# 🌉 FoodBridge – Smart Food Waste Reduction & Donation Platform

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://reactjs.org/)
[![Vite](https://img.shields.io/badge/Vite-5-646CFF?style=for-the-badge&logo=vite&logoColor=white)](https://vitejs.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

> A full-stack platform that intelligently bridges the gap between food donors and recipients — reducing waste and fighting hunger in communities.

---

## 📋 Table of Contents

- [About the Project](#-about-the-project)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
  - [Docker (Recommended)](#-option-1-docker-compose-recommended)
  - [Local Development](#-option-2-local-development)
- [API Documentation](#-api-documentation)
- [Environment Variables](#-environment-variables)
- [Development Phases](#-development-phases)
- [Contributing](#-contributing)

---

## 🎯 About the Project

**FoodBridge** connects food donors (restaurants, grocery stores, households, events) with recipients (NGOs, food banks, individuals in need). Key capabilities (across all phases):

- 🍱 **Smart Listings** — Donors post surplus food with expiry, location, and quantity
- 📍 **Geolocation Matching** — Recipients discover nearby available donations
- 🔔 **Real-time Notifications** — Instant alerts for new listings and status changes
- 📊 **Analytics Dashboard** — Impact tracking (kg saved, meals served, CO₂ offset)
- 🔒 **Role-based Access** — DONOR, RECIPIENT, and ADMIN roles

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.3.5 |
| **Build Tool** | Maven 3.9+ |
| **Security** | Spring Security 6 + JWT (Phase 2) |
| **ORM** | Spring Data JPA + Hibernate |
| **Database** | PostgreSQL 16 |
| **Migrations** | Flyway |
| **API Docs** | SpringDoc OpenAPI 3 (Swagger UI) |
| **Frontend** | React 18 + Vite 5 |
| **Styling** | Tailwind CSS v3 |
| **HTTP Client** | Axios |
| **Routing** | React Router v6 |
| **Containerization** | Docker + Docker Compose |

---

## 📁 Project Structure

```
FoodBridge/
├── backend/                          # Spring Boot REST API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/foodbridge/
│   │   │   │   ├── FoodBridgeApplication.java
│   │   │   │   ├── config/           # CORS, Security, OpenAPI config
│   │   │   │   └── common/           # Shared response, exceptions
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       ├── application-prod.yml
│   │   │       └── db/migration/     # Flyway SQL scripts
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                         # React + Vite SPA
│   ├── src/
│   │   ├── api/                      # Axios client & endpoints
│   │   ├── components/               # Shared UI components
│   │   ├── pages/                    # Route-level page components
│   │   ├── hooks/                    # Custom React hooks
│   │   ├── utils/                    # Helper utilities
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   ├── public/
│   ├── index.html
│   ├── vite.config.js
│   ├── tailwind.config.js
│   └── package.json
│
├── docker-compose.yml                # Orchestrates all services
├── .gitignore
└── README.md
```

---

## ✅ Prerequisites

Ensure the following are installed on your machine:

| Tool | Minimum Version | Check Command |
|---|---|---|
| Java JDK | 21 | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Node.js | 18+ | `node -v` |
| npm | 9+ | `npm -v` |
| Docker Desktop | Latest | `docker -v` |
| Git | 2.40+ | `git --version` |

---

## 🚀 Getting Started

### 🐳 Option 1: Docker Compose (Recommended)

> Spins up PostgreSQL + Backend + Frontend in one command.

```bash
# 1. Clone the repository
git clone https://github.com/your-username/FoodBridge.git
cd FoodBridge

# 2. Start all services
docker compose up --build

# 3. Access the application
#    Frontend:  http://localhost:5173
#    Backend:   http://localhost:8080
#    Swagger:   http://localhost:8080/swagger-ui.html
#    API Docs:  http://localhost:8080/api-docs
```

Stop all services:
```bash
docker compose down
```

Remove volumes (reset database):
```bash
docker compose down -v
```

---

### 💻 Option 2: Local Development

**Step 1 — Start PostgreSQL**
```bash
# Using Docker for just the database
docker run --name foodbridge-db \
  -e POSTGRES_DB=foodbridge \
  -e POSTGRES_USER=foodbridge \
  -e POSTGRES_PASSWORD=foodbridge123 \
  -p 5432:5432 \
  -d postgres:16-alpine
```

**Step 2 — Start Backend**
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Step 3 — Start Frontend**
```bash
cd frontend
npm install
npm run dev
```

**URLs:**
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

---

## 📖 API Documentation

Interactive API documentation is available via **Swagger UI**:

```
http://localhost:8080/swagger-ui.html
```

Raw OpenAPI JSON spec:
```
http://localhost:8080/api-docs
```

---

## 🔐 Environment Variables

All sensitive configuration is managed via Spring profiles and environment variables. Copy `.env.example` to `.env` and update values for production.

| Variable | Default (dev) | Description |
|---|---|---|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `foodbridge` | Database name |
| `DB_USERNAME` | `foodbridge` | Database user |
| `DB_PASSWORD` | `foodbridge123` | Database password |
| `SERVER_PORT` | `8080` | Backend server port |
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile |

---

## 🗺 Development Phases

| Phase | Status | Description |
|---|---|---|
| **Phase 1** | ✅ Complete | Project scaffold, configuration, Docker setup |
| **Phase 2** | 🔜 Planned | JWT Authentication, User registration/login |
| **Phase 3** | 🔜 Planned | Donation CRUD, Food listing lifecycle |
| **Phase 4** | 🔜 Planned | Geolocation matching, Maps integration |
| **Phase 5** | 🔜 Planned | Real-time notifications (WebSocket) |
| **Phase 6** | 🔜 Planned | Analytics dashboard, Admin panel |
| **Phase 7** | 🔜 Planned | Production deployment, CI/CD pipeline |

---

## 🤝 Contributing

This is a final-year academic project. Contributions and feedback are welcome via issues and pull requests.

---

## 📄 License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.

---

<p align="center">Built with ❤️ to reduce food waste and fight hunger.</p>
