# Security and privacy model

- No credential, cookie, bearer token, API key, password, account ID, prompt or answer text is logged or exported.
- No authentication, PoW, quota or server-side access-control bypass is in scope.
- Diagnostics are opt-in, metadata-only, redacted, bounded and user-clearable.
- A future local API binds to 127.0.0.1 by default and uses a separate module-owned secret.
- Database operations require a verified schema, a backup, explicit confirmation and transactional rollback.
- Never commit signing keys, keystores, tokens or private APKs.
- Report vulnerabilities privately to the relevant maintainers.
