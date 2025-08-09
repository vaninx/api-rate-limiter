# api-rate-limiter

Goal: a simple, fast service that limit how often clients can call an API

Scopes: per API key, per route, global ( route > identity > global )
Defaults: 100/min per API key (burst 20), /login 5 per 10s ( per IP ), write 30/min ( per user ), global 10k/min, daily 10k per key
Mode: in-memory (dev), Redis (multi-instance)
Responses : 429 with Retry-After + X-RateLimit-* headers
