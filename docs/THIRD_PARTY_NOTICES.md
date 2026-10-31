# Third-party notices

本项目当前以 GPL-3.0-only 发布。以下为规划和研究阶段参考的第三方项目。**列入本清单不代表已经复制、链接或分发其代码。**

| 项目 | 许可证线索 | 当前使用状态 |
|---|---|---|
| libxposed/api | Apache-2.0 | compileOnly API dependency |
| libxposed/example | Apache-2.0 | research reference only |
| LSPosed | GPL-3.0 | framework reference only |
| Deekseep | GPL-3.0 | source research; no bulk copy recorded |
| YunSeek / Deekseep-Custom | GPL-3.0 | source research; no bulk copy recorded |
| GLMKit | Fork metadata; license requires file-level verification | source research |
| hxreborn/Deekseep | GitHub metadata reports MIT | source research |
| YukiHook | Apache-2.0 metadata | source research |
| EzHookTool | Maven metadata reports MIT | candidate; not yet added |
| Chucker | Apache-2.0 | research only |
| HyperRefine | AGPL-3.0 | research only; no code import planned |

## Import procedure

Before importing any third-party source or binary dependency:

1. Record exact repository URL, commit SHA, file path and author.
2. Read the exact LICENSE and any NOTICE file at that commit.
3. Preserve original copyright and license headers.
4. Add a concise modification notice where required.
5. Record dependency coordinates and version in Gradle.
6. Update this file with the actual imported files and licenses.
7. Verify GPL-3.0 compatibility and any additional attribution obligations.

Repositories without an explicit license are reference-only. GitHub's fork metadata does not prove the provenance or license of every file.
