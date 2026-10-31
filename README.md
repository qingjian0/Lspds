# Lspds — DeepSeek Android LSPosed Enhancement

> An independent, open-source LSPosed enhancement module for the official DeepSeek Android client (`com.deepseek.chat`).

**Status: research and safe-probe baseline (v0.1.0-dev).** This repository is the canonical home for all project code, tests, build configuration, research notes, and release artifacts.

- **Target:** official DeepSeek Android package `com.deepseek.chat`
- **Initial host profile:** Google Play DeepSeek 2.7.1, versionCode 288
- **Framework API:** modern libxposed API 102 (compile-only)
- **Language:** Java 21
- **Minimum Android:** 8.0 / API 26 (framework API availability still depends on the installed LSPosed build)
- **License:** GPL-3.0-only

## Current implementation status

| Area | Status | Evidence |
|---|---|---|
| Modern libxposed module entry | In progress | Project skeleton is being established |
| Host package and version gate | Planned for first probe | No unsupported build should receive hooks |
| DeepSeek 2.7.1 request probe | Static candidate only | DEX analysis identifies `Lrx3.g(Lvv1, Long):Lh60`; runtime match is not verified |
| SSE protocol utility | Planned, independent parser tests first | Main completion parser is not yet proven |
| Local OpenAI/Anthropic-compatible API | Not implemented | Requires safe, on-device host-adapter validation |
| Chat editor/history/backup | Not implemented | Requires verified DB schema and transactional tests |
| Multi-account management | Not implemented | No credential extraction or session-token export |
| UI customization/app lock | Not implemented | Later phase |
| APK / device validation | Not yet available | CI and an actual LSPosed device run are required |

Do not interpret a planned feature, DEX symbol, or successful compilation as proof of runtime compatibility.

## Repository layout

- `app/` — Android module APK and LSPosed entry
- `docs/` — architecture, source audit, compatibility matrix and reverse-engineering evidence
- `scripts/` — reproducible developer utilities
- `.github/workflows/` — Android build and test automation

## Build

The project uses Gradle 9.5.1, Android Gradle Plugin 9.2.1, JDK 21 and Android SDK 37.

```bash
gradle --version
gradle testDebugUnitTest assembleDebug
```

The CI workflow installs the required toolchain. A release build must be signed with a user-controlled key; signing keys and passwords must never be committed.

## Safety and privacy

- The initial adapter is **observation-only** and fail-closed.
- Never log prompts, generated answer text, account identifiers, session IDs, cookies, bearer tokens, API keys, passwords, or PoW challenge/solution material.
- Do not bypass DeepSeek authentication, proof-of-work, rate limits, or server-side access controls.
- The local API, when implemented, must bind to loopback by default and use an independent module-owned API key. Do not expose the host app's credentials.
- Database edits and backups must be user-initiated, transactional, restorable, and tested against a copy before production use.
- Each optional hook must install independently. A missing symbol must not prevent the module from loading.

## Research sources

See [Reference project audit](docs/REFERENCE_AUDIT.md), [DeepSeek 2.7.1 static findings](docs/DEEPSEEK_271_STATIC_FINDINGS.md), [architecture](docs/ARCHITECTURE.md), [compatibility policy](docs/COMPATIBILITY.md), and [roadmap](docs/ROADMAP.md).

## Disclaimer

Lspds is an independent community project and is not affiliated with DeepSeek, LSPosed, or the authors of referenced projects. Use only on devices and application data you own or are authorized to inspect.