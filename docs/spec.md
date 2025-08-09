 # API Rate Limiter — Project Specification

## Problem
APIs slow down or fail when too many requests arrive at once. One client can also use more than their fair share. This service limits how often a user, API key, or IP can call the API, while still allowing normal use and short bursts.

## Scope
**In scope:** per-API-key, per-route, and global limits; bursts; config file for rules with hot-reload; in-memory (dev) and Redis (prod-style); clear 429 responses; metrics/logs.
**Out of scope:** multi-region consistency, billing, full API gateway features.

## Identities & Scopes
- Primary identity: API key → else User ID → else IP
- Scopes: per API key, per route, global
- Precedence: Route > Identity > Global
- If any scope is over limit → deny

## Default Rules
- Per API key: 100 requests/min, burst 20
- Login (`/login`) per IP: 5 requests/10 seconds
- Writes (`/payments`, `/orders`) per user: 30 requests/min, burst 10
- Global safety cap: 10,000 requests/min (all traffic)
- Daily quota per API key: 10,000/day

## Behaviour
- Allow: add `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`
- Deny: HTTP 429, plus `Retry-After` and same `X-RateLimit-*` headers; optional JSON with reason/scope

## Storage
- Dev: In-memory
- Prod-style: Redis with atomic updates

## Fail Mode
- Fail-open if Redis is unavailable (allow but log warning)

## Non-Functional Targets
- Latency: ≤ 2 ms (in-memory), ≤ 10 ms (Redis)
- Throughput: ≥ 10k checks/sec/node
- Availability: 99.9% single region
- No limit overrun under concurrency

## Observability
- Metrics: allowed_total, blocked_total, decision_latency_ms, backend_errors_total
- Logs: time, route, identity, decision, remaining, retry_after, request_id

## Acceptance Tests
1. API key limit: 100/min, burst behaviour works
2. Login limit: 5/10s per IP
3. Write limit: 30/min per user
4. Global cap: blocks when > 10k/min
5. Daily quota: blocks after 10,000/day
