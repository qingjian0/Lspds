# Reference project audit

Audit performed 2026-10-31 against public GitHub repository metadata and source trees. Repository existence is verified; detailed feature claims are limited to files actually inspected.

| Repository | Relationship / license evidence | Research value | Caveat |
|---|---|---|---|
| lllucccian/Deekseep | Standalone; GPL-3.0 reported by GitHub | DeepSeek host compatibility and generation mapping | Existing mappings target older 2.x hosts |
| ZW-SYS/Deekseep-Custom | Standalone; GPL-3.0 reported | YunSeek customization | Audit changed files and provenance before copying |
| qingjian0/GLMKit | Fork; parent Sliverkiss/GLMKit; source yuzchu/GLMKit | Feature decomposition and local API concepts | Fork status verified |
| hxreborn/Deekseep | Public repository | Alternate DeepSeek implementation | Full source-path audit pending |
| Xposed-Modules-Repo/com.dsmod.probe | Public repository | Probe and diagnostics | Release/source lineage audit pending |
| LSPosed/LSPosed | GPL-3.0 | Framework behavior | Framework, not an app dependency |
| libxposed/api | Modern API | Typed interceptor-chain Hook API | API version must match framework |
| libxposed/example | Official example | Entry metadata and lifecycle | Keep metadata aligned with API target |
| HighCapable/YukiHookAPI | Public | Higher-level Hook abstraction | Adds dependency and compatibility cost |
| HighCapable/YukiHook | Public | Hook utilities | Audit exact release before use |
| LSPosed/LSPlugin | Public | Plugin architecture concepts | Not interchangeable with an Xposed module |
| errorman-awful/Dexterceptor | Public | Dex inspection/interception research | Verify maintenance and license |
| Familyye/HttpInterceptor | Public | HTTP instrumentation concepts | Does not prove DeepSeek transport |
| winhok/net-tap | Public | Multi-stack network instrumentation | Identify transport per host version |
| linuxwff789/wxbackup | Public | Backup workflow ideas | WeChat schema is not portable |
| Ianzb/HyperRefine | Public | Android UI/settings patterns | Not a DeepSeek hook source |
| MeiYongAI/Toki | Public | Host scanning and runtime organization | Audit dependencies and license |
| hxreborn/biometric-app-lock | Public | App-lock behavior | Permission and biometric UX need separate design |
| ZJF-Kedaya/Deekseep-fork | Public | Derivative comparison | Fork lineage and delta need full diff |
| zjkkzk/Deekseep22222 | Public | Derivative comparison | Fork lineage and delta need full diff |

## Technical conclusions

- Modern libxposed uses META-INF/xposed/java_init.list, module.prop and scope.list. Entry classes extend XposedModule and initialize in lifecycle callbacks. Hooks use a typed interceptor-chain model.
- The LSPosed modern API guide documents capability negotiation and per-hook configuration. Do not mix legacy XposedBridge calls into an API 102 module.
- Deekseep HostCompat demonstrates why versioned mappings and fail-closed behavior matter: it checks concrete class and method contracts instead of assuming all releases share one obfuscated table.
- Do not assume OkHttp is the only transport. Network instrumentation must identify the actual stack first.
- GPL compatibility and attribution must be checked file-by-file before porting. A top-level repository license does not automatically clear vendored dependencies.
