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


## Additional DeepSeek-specific repositories reviewed (2026-10-31)

The following projects were researched. This list does not assert that their code has been imported.

| Repository | GitHub license metadata | Provenance / use |
|---|---|---|
| [lllucccian/Deekseep](https://github.com/lllucccian/Deekseep) | GPL-3.0 | Primary upstream; preserve GPL-3.0-only notices for any future derived source |
| [ZW-SYS/Deekseep-Custom](https://github.com/ZW-SYS/Deekseep-Custom) | GPL-3.0 | YunSeek; direct source research |
| [hxreborn/Deekseep](https://github.com/hxreborn/Deekseep) | MIT | README build workflow points to haoyangtu09-art/Deekseep; inspect exact canonical source and file provenance before import |
| [Xposed-Modules-Repo/com.dsmod.probe](https://github.com/Xposed-Modules-Repo/com.dsmod.probe) | MIT | Published module repository; inspect source lineage file-by-file |
| [hhz522/Deepseek-Lsposed](https://github.com/hhz522/Deepseek-Lsposed) | GPL-3.0 | GitHub confirms fork of lllucccian/Deekseep |
| [ZJF-Kedaya/Deekseep-fork](https://github.com/ZJF-Kedaya/Deekseep-fork) | GPL-3.0 | GitHub confirms fork of lllucccian/Deekseep |
| [zjkkzk/Deekseep22222](https://github.com/zjkkzk/Deekseep22222) | GPL-3.0 | GitHub confirms fork of lllucccian/Deekseep |
| [KEJIYUNB/DeepSeekmok](https://github.com/KEJIYUNB/DeepSeekmok) | MIT | README references upstream Deekseep; provenance unresolved; research only |
| [sakukir/DeepTranslate](https://github.com/sakukir/DeepTranslate) | MIT | Adjacent general-purpose LSPosed translation module; not a DeepSeek-host hook implementation |

GitHub API metadata was checked on 2026-10-31. SPDX metadata and fork fields are discovery aids, not a substitute for reviewing the exact commit, source headers, LICENSE, NOTICE, and file history. In particular, a fork or mirror's repository-level license does not automatically relicense copied upstream files.
