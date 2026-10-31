# Roadmap

## Phase 0 — repository baseline
- [x] Establish canonical repository and source layout.
- [x] Add modern libxposed module metadata and lifecycle entry.
- [x] Add fail-closed host version identification.
- [x] Add initial unit test and CI workflow.
- [ ] Run CI and confirm APK artifact.

## Phase 1 — static host mapping
- [ ] Verify all DeepSeek 2.7.1 APK splits and DEX hashes.
- [ ] Complete the chat request and streaming response call graph.
- [ ] Identify transport implementation and database schema.
- [ ] Publish evidence-backed host mapping.

## Phase 2 — safe diagnostics
- [ ] Add opt-in, redacted network metadata diagnostics.
- [ ] Add independent hook installers with rollback.
- [ ] Test host stability and cold/warm start.

## Phase 3 — data and conversation features
- [ ] Read-only conversation export.
- [ ] Backup and restore with schema versioning and transaction safety.
- [ ] Prompt library and pinning in module-owned storage.
- [ ] Conversation editing only after transactional proof and explicit user action.

## Phase 4 — local API and advanced features
- [ ] OpenAI-compatible local API with loopback binding and module-owned API key.
- [ ] SSE bridge and cancellation/backpressure tests.
- [ ] Multi-profile support without extracting host credentials.
- [ ] Optional UI customization and app lock.

Every item remains incomplete until source, tests and device evidence are recorded.
