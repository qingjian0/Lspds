# Architecture

## Release 0.1 — observation-only baseline

The module is scoped only to com.deepseek.chat. LspdsModule uses modern libxposed lifecycle callbacks. The current installer performs package and version identification only; it does not hook chat methods, rewrite requests, access credentials, or mutate databases.

## Planned layers

1. Framework adapter: libxposed lifecycle, capability checks, logging and per-hook failure isolation.
2. Host identity: package, channel, version, APK fingerprint and DEX-signature matching.
3. Symbol catalog: versioned, evidence-backed class and method descriptors.
4. Feature adapters: networking, conversation, export, prompts, UI and diagnostics.
5. Module UI: configuration and user-consented operations.
6. Storage: module-owned database and configuration.
7. Local API: loopback-only server with module-owned credentials.

## Hook policy

- Install only after exact descriptor and signature validation.
- Each hook is optional and isolated.
- Unknown host builds remain inert.
- Never log user content, identifiers, tokens, cookies, API keys or PoW data.
- Prefer passive metadata counters and redacted diagnostics.
