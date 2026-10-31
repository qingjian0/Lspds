# Compatibility policy

| Host | Channel | Version code | Static evidence | Runtime evidence | Enabled hooks |
|---|---|---:|---|---|---|
| DeepSeek 2.7.1 | Google Play | 288 | Supplied APKS and static report | Not yet tested on device | None |
| Other versions/channels | Unknown | — | Not mapped | Not tested | None |

Version strings and version codes are coarse gates only. Before enabling a feature hook, record exact DEX checksums, class and method descriptors, access flags, call-site evidence and device-side validation.

A host build that does not match a verified profile must remain observation-only. Never fall back to an old obfuscated symbol table.
