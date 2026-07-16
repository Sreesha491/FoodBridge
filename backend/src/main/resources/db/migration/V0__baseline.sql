-- ════════════════════════════════════════════════════════════
--  FoodBridge – Flyway Baseline Migration
--  Version  : V0__baseline.sql
--  Purpose  : Establishes the baseline schema version marker.
--             Actual domain tables (users, donations, etc.)
--             are created in subsequent migrations (V1, V2 …)
--             added in Phase 2+.
-- ════════════════════════════════════════════════════════════

-- Enable UUID extension (PostgreSQL 13+ has it built-in, but we ensure it)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable pgcrypto (used for hashing in Phase 2)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
