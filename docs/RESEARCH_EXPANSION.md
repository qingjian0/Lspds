# 开源项目扩展调研记录

> 核验日期：2026-10-31。此文档区分“仓库存在/元数据已核验”和“源码已逐项审阅”。未审阅的功能不作为已验证事实。仓库许可证以 GitHub 元数据为线索，代码移植前必须复核具体文件的 LICENSE 与版权头。

## A. DeepSeek / Deekseep / YunSeek / GLMKit 核心项目

| 项目 | 关系/许可 | 已核实内容 | 对 Lspds 的价值 |
|---|---|---|---|
| [lllucccian/Deekseep](https://github.com/lllucccian/Deekseep) | GPL-3.0；原始上游 | DeepSeek Xposed 模块；版本/渠道兼容层；维护者公开说明暂停公共仓库更新 | 原始业务实现和历史映射参考；保留作者归属 |
| [ZW-SYS/Deekseep-Custom](https://github.com/ZW-SYS/Deekseep-Custom) | GPL-3.0；README 明确基于 Deekseep | YunSeek 1.8.1；国内 2.7.1 code 287、国内 2.7.2 code 289、Google Play 2.7.2 code 290；独立映射表和多项 UI/数据功能 | 当前最重要的 2.7.x 参考；README 对 2.7.2 明确保留真机验证限制 |
| [qingjian0/GLMKit](https://github.com/qingjian0/GLMKit) | Fork；parent Sliverkiss/GLMKit，source yuzchu/GLMKit；API 元数据未返回 SPDX | GLM Xposed、本地 API、对话 UI、HostCompat、OpenAI tool bridge 等代码路径 | 学习 API 适配层和功能拆分；GLM 私有协议不能直接视为 DeepSeek 协议 |
| [hxreborn/Deekseep](https://github.com/hxreborn/Deekseep) | GitHub API 报告 MIT | 现代 API 102、传统 Xposed、测试/诊断构建；对话编辑、提示词、数据库备份等 README 功能声明 | 对比现代入口、分变体构建和测试架构；移植需保留 MIT 声明 |
| [Xposed-Modules-Repo/com.dsmod.probe](https://github.com/Xposed-Modules-Repo/com.dsmod.probe) | GitHub API 报告 MIT | Deekseep 名称的公开镜像/模块仓库条目 | 比较发布包、安装说明和兼容性声明；核实镜像与上游差异 |
| [ZJF-Kedaya/Deekseep-fork](https://github.com/ZJF-Kedaya/Deekseep-fork) | Fork parent/source 均为 lllucccian/Deekseep；GPL-3.0 | README 标明 Open edition 衍生项目 | 做 fork 差异审计，核查新增文件来源 |
| [zjkkzk/Deekseep22222](https://github.com/zjkkzk/Deekseep22222) | Fork parent/source 均为 lllucccian/Deekseep；GPL-3.0 | README 标明 Deekseep 衍生项目 | 与其他 fork 比较修复和文档，不重复移植上游代码 |

### YunSeek 1.8.1 的重要核验结果

- README 声明 2.7.1 国内版 code 287；与本项目已有 Play 2.7.1 code 288 资料不是同一构建。
- README 声明国内 2.7.2 code 289、Google Play 2.7.2 code 290。
- README 描述 2.7.2 映射通过约 7600 个类的结构比对，且有 8 个无法确认的 Compose 符号自动关闭；同时明确称未在真机全面测试。
- HostV271.java 注释列出国内版 2.7.1 的序列化类/字段/构造器线索，并说明使用稳定锚点定位；不能替代对 Play 2.7.1 的独立验证。
- YunSeek README 声明底层继承 Deekseep GPL 代码并保留原作者署名。若移植其衍生代码，必须同时保留上游和 YunSeek 贡献者的版权/许可证声明。

## B. 框架、Hook API、动态定位

| 项目 | 许可证/状态 | 研究用途 | 采用建议 |
|---|---|---|---|
| [LSPosed/LSPosed](https://github.com/LSPosed/LSPosed) | GPL-3.0 | 框架生命周期、作用域、加载机制 | 不作为 APK 运行时依赖 |
| [libxposed/api](https://github.com/libxposed/api) | Apache-2.0 | 现代 Xposed API 102、HookBuilder、Interceptor Chain | 当前主线 compileOnly |
| [libxposed/example](https://github.com/libxposed/example) | Apache-2.0 | 官方示例、入口注册和资源文件 | 对照模块元数据与构建 |
| [HighCapable/YukiHookAPI](https://github.com/HighCapable/YukiHookAPI) | 需复核仓库迁移后的许可/版本 | Kotlin DSL 与 Hook 抽象 | 先做 API 102 兼容性验证 |
| [HighCapable/YukiHook](https://github.com/HighCapable/YukiHook) | Apache-2.0 | 现代 Hook API/模块方案 | 可作为替代架构研究 |
| [LSPosed/LSPlugin](https://github.com/LSPosed/LSPlugin) | Apache-2.0 | 插件化、配置和模块工程 | 仅借鉴可复用模式 |
| [KyuubiRan/EzXHelper](https://github.com/KyuubiRan/EzXHelper) | MIT（公开项目资料） | Kotlin 反射/Hook 辅助，面向传统 API 与 API 101 | 不直接假定兼容 API 102 |
| [lingqiqi5211/EzHookTool](https://github.com/lingqiqi5211/EzHookTool) | Maven 元数据报告 MIT | libxposed 和 Xposed Hook helper | 候选依赖；检查 API 102 支持 |
| [DexKit](https://github.com/LuckyPray/DexKit) | 以仓库 LICENSE 为准 | DEX 类/方法/字段特征搜索 | 评估 native ABI、体积、性能和维护 |
| [errorman-awful/Dexterceptor](https://github.com/errorman-awful/Dexterceptor) | 需复核 LICENSE | OkHttp 运行时拦截、请求元数据与规则 | 只借鉴观察机制；不采用默认 SSL bypass |
| [YifePlayte/WOMMO](https://github.com/YifePlayte/WOMMO) | 需复核 LICENSE | API 102、Kotlin hook family、Compose/Native 混合实现 | 研究模块生命周期和 Hook 基类组织 |
| [hxreborn/biometric-app-lock](https://github.com/hxreborn/biometric-app-lock) | GPL-3.0 | 生物识别锁应用模块 | 研究 BiometricPrompt 和锁定状态机 |

## C. 网络、数据库与数据工具

| 项目 | 许可/状态 | 研究用途 | 采用建议 |
|---|---|---|---|
| [Familyye/HttpInterceptor](https://github.com/Familyye/HttpInterceptor) | 待核对 | Xposed HTTP/HTTPS 请求记录 | 只研究 Hook 位置与流式读取；默认不保存正文 |
| [winhok/net-tap](https://github.com/winhok/net-tap) | 待核对 | OkHttp、Cronet、gRPC、URLConnection、Ktor 等多传输层探测 | 传输层枚举参考；不引入 TLS key logging |
| [Chucker](https://github.com/ChuckerTeam/chucker) | Apache-2.0 | OkHttp 诊断 UI、数据模型 | 借鉴 UI/过滤/容量控制；其主要面向应用内集成 |
| [fatihsokmen/xposed-connection-monitor](https://github.com/fatihsokmen/xposed-connection-monitor) | 未声明许可证 | Kotlin Xposed 网络监控、SQLite 日志 | 只做架构参考；未授权前不复制代码 |
| [linuxwff789/wxbackup](https://github.com/linuxwff789/wxbackup) | 待核对 | 备份流程/数据导出 | 只借鉴通用备份流程，不移植微信数据模型 |
| [Room](https://developer.android.com/training/data-storage/room) | AndroidX | 模块自身数据库 | 首选模块本地设置、提示词、置顶和索引 |
| [SQLite](https://sqlite.org/) | Public Domain | 事务、WAL、备份与完整性 | 宿主数据只读优先，写入需验证 schema 和回滚 |

## D. UI、模块工程和其他增强项目

| 项目 | 许可/状态 | 研究用途 | 采用建议 |
|---|---|---|---|
| [Ianzb/HyperRefine](https://github.com/Ianzb/HyperRefine) | AGPL-3.0 | HyperOS 增强模块和设置界面 | 只参考 UI 组织；AGPL 需单独评估 |
| [MeiYongAI/Toki](https://github.com/MeiYongAI/Toki) | MIT | libxposed API 102 + Material 3 Kotlin 模块 | 参考现代模块工程和 UI |
| [Jetpack Compose samples](https://github.com/android/compose-samples) | Apache-2.0 | Compose UI 架构 | 模块独立 UI 优先 |
| [Material 3](https://github.com/material-components/material-components-android) | Apache-2.0 | 组件和设计系统 | 以官方依赖和文档为准 |

## E. 后续扩展检索池

后续可继续审查这些方向的真实项目：Android DexKit sample、EzHookTool、Kotlin Xposed network monitor、OkHttp Chucker、Android Pluto、YukiHook、libxposed/helper、libxposed/service、LSPosed module repository、Jetpack Compose samples、Room migration samples。只有核实实际仓库、源码、许可证和维护状态后，才加入正式移植清单。

## F. 许可证与代码来源规则

1. GPL-3.0-only 主项目可吸收 GPL-3.0 兼容代码，但必须保留原版权、作者、许可证和修改说明。
2. MIT/Apache-2.0 代码移植时保留 LICENSE、NOTICE 和必要 attribution。
3. AGPL-3.0 代码不自动纳入；需单独评估网络交互和衍生作品义务。
4. 未声明许可证的仓库只允许研究，不复制源代码。
5. GitHub fork 的 parent/source 字段只证明仓库关系，不证明 fork 中每个文件的来源；必须查看 git diff、文件版权头和历史。
6. 公开仓库不提交 DeepSeek APK/APKS、DEX、反编译完整源码、真实聊天数据库、账号资料、密钥或设备日志。


## G. 已检查源码的结构性观察

### YunSeek / Deekseep 2.7.1 国内版适配线索

对 ZW-SYS/Deekseep-Custom 的 README、HostV271.java、HostCompat.java 和 HookChatPipeline.java 做了源码级抽样检查：

- HostV271.java 注释明确限定为国内版 DeepSeek 2.7.1（versionCode 287），不能直接外推到 Play 版 code 288。
- 该文件说明其 2.7.1 适配是通过 kotlinx.serialization serial name、analytics 字符串和精确构造器/方法签名等稳定锚点定位，而非直接从国内 2.4.1 APK 做完整结构 diff。
- 注释列出的候选结构包括 ChatFullCompletionRequest（sx1，serializer qx1）、ChatHistoryMessagesResponseBizData（ey1）、AppStaticMessage（gy）以及 RequestFragment（v9a）；历史消息字段和片段字段也有明确描述。
- HookChatPipeline.java 包含请求构造、SSE 事件观察、会话处理、历史/消息逻辑和 Local API 连接点；其规模较大，适合拆分为独立 Kotlin feature adapter，而不是整文件搬运。
- 该项目的部分路径会对宿主 SQLite 数据执行写操作。Lspds 不应直接照搬写库行为，必须先独立验证 schema、事务、WAL 和恢复。

### GLMKit 本地 API 架构观察

对 qingjian0/GLMKit 的 LocalApiGateway.java 和 GlmBackend.java 做了抽样检查：

- Gateway 与后端通过 Backend、CompletionRequest、CompletionResult、DeltaSink 等边界分离。
- Gateway 负责本地 HTTP/JSON/SSE 接口，Backend 负责把兼容请求转换为宿主请求并解析响应。
- 代码包含 Chat Completions、Responses 和 Anthropic Messages 等不同输入转换路径，以及独立的 SSE emitter。
- 其实现包含 ServerSocket 监听和多个流式事件发射器，适合借鉴“协议入口与宿主适配器分离”的架构。
- GlmBackend 中发现了直接记录 Authorization token 前缀的日志语句。Lspds 明确禁止这种行为，不复制该日志方式，也不输出宿主凭据。

以上属于源码抽样观察，不等于该代码已经通过 Lspds 的安全、兼容或性能审查。

## H. 参考项目的优先审阅顺序

1. YunSeek：HostV271、HostCompat、HostRemap、HookChatPipeline、数据备份、提示词和设置导出。
2. 原始 Deekseep：上游来源、历史兼容矩阵、LICENSE、符号映射和功能差异。
3. GLMKit：LocalApiGateway、GlmBackend、OpenAiToolBridge、数据存储和线程/取消管理。
4. libxposed API / example：生命周期、HookBuilder、HookHandle、入口 metadata、R8 keep rules。
5. net-tap / Dexterceptor / HttpInterceptor：传输层发现和流式响应读取，明确排除 TLS key logging 与 SSL pinning bypass。
6. YukiHook / EzHookTool / DexKit：做独立 spike 后再决定是否引入依赖。
7. Toki / WOMMO / biometric-app-lock / HyperRefine：模块 UI、Kotlin 结构和应用锁。


## I. 2026-10-31 新增 DeepSeek Hook 项目检索

本轮检索重点是直接针对 `com.deepseek.chat` 的运行时 Hook，而不是把所有使用 DeepSeek API 的 Android 项目都误列为 DeepSeek 客户端 Hook。下表区分原始项目、真实 GitHub fork、发布镜像/模块仓库和来源不清的衍生仓库。

### I-1. 直接面向官方 DeepSeek 客户端

| 仓库 | GitHub 关系 / 许可证元数据 | 检索到的技术与功能 | 对 Lspds 的价值 | 审计结论 |
|---|---|---|---|---|
| [lllucccian/Deekseep](https://github.com/lllucccian/Deekseep) | 原始上游；GPL-3.0 | 现代/传统 Xposed、多版本适配、模块入口、提示词注入、聊天编辑、响应替换保护、数据库备份、账号/图片/本地 API 工具 | 核心功能与宿主符号映射研究首选 | README 当前标记开源项目维护暂停/无限期 hiatus；公开源码可研究，不能假定 Closed 版功能有源码 |
| [ZW-SYS/Deekseep-Custom](https://github.com/ZW-SYS/Deekseep-Custom) | 独立仓库；GPL-3.0 元数据；YunSeek | 2.7.x 国内版适配、HostV271、HostCompat、HookChatPipeline、请求/SSE/会话链路 | 当前较新宿主版本的关键研究来源 | 2.7.1 国内版 code 287 与 Play code 288 必须分开；README 自述部分适配仍缺真机全面验证 |
| [hxreborn/Deekseep](https://github.com/hxreborn/Deekseep) | 独立仓库；GitHub SPDX MIT；仓库 README 的 Actions 指向 haoyangtu09-art/Deekseep | libxposed API 102、传统 Xposed、诊断 probe、提示词、响应保留、会话编辑、推理片段修复、搜索、Markdown 导出、数据库备份 | 现代 API 102 工程组织、Hook 修复和回归测试参考 | 先核对真实 canonical owner 和逐文件来源；MIT 许可不代表可以忽略第三方 GPL 来源 |
| [Xposed-Modules-Repo/com.dsmod.probe](https://github.com/Xposed-Modules-Repo/com.dsmod.probe) | 独立发布仓库；GitHub SPDX MIT 元数据 | 稳定/测试/Legacy/Play 变体；模块激活 Binder 与 UID heartbeat；功能说明记录设置入口、prompt、CONTENT_FILTER response preservation、编辑器、SQLite 备份、侧栏批量操作 | 模块激活检测、变体打包、详细功能文档和故障排查参考 | README 明确要求按国内/Play 的具体 versionCode 选包；逐文件源代码来源与上游差异仍需审计 |
| [hhz522/Deepseek-Lsposed](https://github.com/hhz522/Deepseek-Lsposed) | GitHub API 确认 fork=true，parent/source 均为 lllucccian/Deekseep；GPL-3.0 | README 的 SOURCE_URL 指向原始 Deekseep | 追踪社区 fork 差异、兼容补丁和历史版本 | 不能把它算作全新独立实现；需要对比 fork commit 和实际 diff |
| [ZJF-Kedaya/Deekseep-fork](https://github.com/ZJF-Kedaya/Deekseep-fork) | GitHub API 确认 fork=true，parent/source 为 lllucccian/Deekseep；GPL-3.0 | README 声明基于上游；开源 Open 与闭源 Closed 的功能分流；2.3.x 兼容信息 | fork 变更、开源/闭源功能边界与版本矩阵参考 | README 明确 Local API 已移入未公开源码的 Closed 版；不得复制闭源实现 |
| [zjkkzk/Deekseep22222](https://github.com/zjkkzk/Deekseep22222) | GitHub API 确认 fork=true，parent/source 为 lllucccian/Deekseep；GPL-3.0 | 与上游兼容说明相近，包含 SOURCE_URL/NOTICE | 对比上游历史改动 | 属于上游 fork，不计为独立技术实现 |
| [KEJIYUNB/DeepSeekmok](https://github.com/KEJIYUNB/DeepSeekmok) | GitHub API 显示非 fork；MIT 元数据；README 徽章/下载链接指向 lllucccian/Deekseep | README 功能和版本说明高度沿用 Deekseep；本轮未找到可确认的 SOURCE_URL 文件 | 作为来源审计样本，检查代码和文档是否重打包 | 仓库关系和源代码来源未确认；在取得 commit diff、版权头和完整 LICENSE 前，仅作线索，不复制代码 |
| [DeepSeekmok README](https://github.com/KEJIYUNB/DeepSeekmok/blob/main/README_CN.md) | 同上 | 声称 MIT，但功能/发布说明引用 Deekseep 上游 | GPL 来源与 MIT 声明冲突排查案例 | 必须逐文件核对；仓库许可证徽章不能覆盖上游 GPL 文件的义务 |

### I-2. 相邻但不是 DeepSeek 客户端 Hook

| 仓库 | 许可证 / 类型 | 技术点 | 适用范围 |
|---|---|---|---|
| [sakukir/DeepTranslate](https://github.com/sakukir/DeepTranslate) | MIT；独立 LSPosed 全局翻译模块 | Kotlin Hook、libxposed API 102、TextView.setText Hook、批量聚合、翻译缓存、DeepSeek API 调用 | 可研究 Kotlin Hook 组织、批处理、缓存和模块设置；它 Hook 的是通用 Android 文本 UI，不是 DeepSeek 客户端内部请求链 |
| [libxposed/api](https://github.com/libxposed/api) 与 [libxposed/example](https://github.com/libxposed/example) | Apache-2.0 元数据 | 新式 XposedModule 生命周期、Hook API 和 metadata | Lspds 当前模块框架 |
| [LSPosed/LSPosed](https://github.com/LSPosed/LSPosed) | GPL-3.0 | 框架加载、作用域、兼容性和诊断 | 框架行为参考，不直接移植框架内部代码 |

### I-3. 重要源码观察

**A. 模块激活不等于目标进程已加载**

`Xposed-Modules-Repo/com.dsmod.probe` 的功能文档描述了 modern libxposed 服务 Binder 与目标进程 UID 校验 heartbeat 的组合。Lspds 应采用类似的双状态模型：

- Framework enabled：框架服务可用/模块已启用。
- Host process loaded：`com.deepseek.chat` 的目标进程确实加载了模块。
- 不再使用“模块自身 Activity 可启动”作为 Hook 生效的证据。

**B. 不要依赖 Compose 内部扩展点作为唯一入口**

该项目选择在 DeepSeek 设置路由中注入原生 View，而不是假设宿主 Compose 有稳定公开扩展点。Lspds 应将 UI 注入与 Compose 结构解耦，先寻找稳定的宿主导航/设置入口，并提供独立模块 Activity 作为回退。

**C. 响应保护是客户端状态合并问题，不是服务端绕过**

源码说明中提到的 response preservation 发生在客户端已经收到流式文本后，阻止后续本地 `CONTENT_FILTER` 模板替换已收到的对象。它不能恢复服务端没有下发的数据，也不应绕过服务器安全策略。Lspds 只可把它作为本地显示状态一致性研究，不实现服务端拒绝绕过。

**D. SQLite 编辑必须比读取更谨慎**

现有模块的编辑器涉及多表、推理片段、图片持久化、sidecar 和宿主内存状态同步。Lspds 不能只更新一行 SQLite 就宣称支持编辑；必须同时理解 native session list、消息 parent chain、缓存版本、WAL 和进程内对象，并先实现只读导出与恢复演练。

**E. 版本矩阵按渠道隔离**

已检索到的 Deekseep 文档曾明确列出国内 2.2.2 code 233 与 Google Play 2.2.2 code 236 使用不同映射；YunSeek 又列出国内 2.7.1 code 287 与 Play 2.7.1 code 288。versionName 相同不代表 DEX 一致。

### I-4. 参考项目的可信度分级

- **A级（直接源码研究）**：原始 Deekseep、YunSeek、hxreborn/Deekseep、Xposed-Modules-Repo/com.dsmod.probe。仍需对具体 commit、LICENSE、代码差异逐项记录。
- **B级（真实 fork / 变体差异）**：hhz522、ZJF-Kedaya、zjkkzk。它们可提供 fork 历史和补丁线索，但不算独立架构实现。
- **C级（来源待确认）**：KEJIYUNB/DeepSeekmok。仓库元数据、MIT 声明与 README 中的上游引用存在需要进一步核验的地方。
- **D级（相邻技术）**：DeepTranslate。可复用通用 Kotlin/LSPosed 设计经验，但不是 DeepSeek 宿主 Hook 证据。

### I-5. Lspds 采纳策略

1. 不直接复制整个 Deekseep/YunSeek 大型 Hook 类；先按功能拆为 Kotlin adapters。
2. 先完成 HostProfile、签名/DEX 指纹和独立 Hook Installer，再迁移具体 Hook。
3. 先做无正文、无凭据的加载诊断和结构探测，再做可选的只读请求元数据诊断。
4. SSE 解析器必须独立于宿主混淆类，使用纯 Kotlin 单元测试覆盖 UTF-8 分片、CRLF、multiline data、DONE、取消和异常帧。
5. 本地 API 仅绑定 loopback，使用模块自己的 Gateway Key；不复制任何宿主 Authorization/Cookie。
6. 对话编辑、账号切换、批量删除、图片中继和实验功能全部单独开关，默认关闭；先实现备份、校验、事务与恢复。
7. 对每个实际移植文件建立 THIRD_PARTY_NOTICES 记录；无法确认来源或许可证的代码只读研究，不进入构建。

### I-6. 本轮检索边界

本轮已核对 GitHub 仓库 API 的 fork、parent/source 和 SPDX license 元数据，并抽查了 README、SOURCE_URL、FEATURES 等文件。GitHub SPDX 元数据不等于法律审查，也未对每个仓库做完整 git diff。Stars、更新时间和 README 的功能宣称会变化，正式采用前仍需固定 commit SHA 并复核源码。所有功能描述均是“项目文档声称/源码中可见”，不是 Lspds 已实现或真机验证结果。
