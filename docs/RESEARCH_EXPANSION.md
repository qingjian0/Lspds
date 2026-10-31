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
