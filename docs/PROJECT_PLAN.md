# Lspds 项目总体规划与技术决策

> 规划基线 v1.0 · 2026-10-31  
> 唯一主仓库：https://github.com/qingjian0/Lspds  
> 目标宿主：官方 DeepSeek Android（com.deepseek.chat）  
> 主语言：Kotlin

## 1. 项目目标

Lspds 是独立 Android LSPosed 增强模块。在不修改、不重新打包官方 DeepSeek APK 的前提下，通过 LSPosed 在用户授权的设备和应用进程中提供可选增强能力。

交付目标：
1. 可安装、可被现代 LSPosed 识别和启用的模块 APK。
2. 按 DeepSeek 渠道、版本和 DEX 指纹隔离的宿主适配层。
3. 独立、可关闭、失败隔离的 Hook 功能模块。
4. 对话管理、备份恢复、提示词、诊断和本地 API 等增强功能。
5. 完整 Kotlin 源码、可复现构建、自动化测试、中文文档和兼容记录。

项目不隶属于 DeepSeek，不包含官方 APK、账号系统或云端代理服务。

## 2. 设计原则

- Kotlin 优先：新增 Android 业务逻辑、Hook、UI、存储、网络和测试默认使用 Kotlin。Java 仅用于必要的第三方接口、原始移植代码或兼容工具。
- 证据驱动：任何宿主 Hook 必须关联具体版本、渠道、DEX 哈希、类/方法描述符和验证记录。
- 未知版本关闭：未知或指纹不匹配的宿主不安装业务 Hook。
- 模块独立：每个 Hook 安装器独立执行；单点失败不影响模块启动或其他功能。
- 最小权限：仅声明实现所需权限；本地 API 默认只监听回环地址。
- 隐私优先：默认不读取、不保存、不记录聊天正文和认证凭据。诊断以元数据、计数和耗时为主。
- 可回滚：危险操作先备份，数据库写入必须事务化并提供恢复路径。
- 许可证合规：项目主许可证 GPL-3.0-only；移植代码保留原始版权、许可证和来源，第三方依赖逐项登记。
- 不依赖云端验证：不设置激活码、远程授权、遥测或强制联网检查。

## 3. 宿主版本事实与边界

不能把“2.7.1”视为唯一版本身份。至少需要区分：

| 渠道 | 版本 | versionCode | 当前证据 |
|---|---:|---:|---|
| 国内版 | 2.7.1 | 287 | YunSeek 1.8.1 README 与 HostV271 源码声明 |
| Google Play / 国际版 | 2.7.1 | 288 | 本项目此前收到的 APKS 静态分析资料，仍需重验 APK 签名和 split |
| 国内版 | 2.7.2 | 289 | YunSeek README 声明；README 明确称尚未真机全面验证 |
| Google Play / 国际版 | 2.7.2 | 290 | YunSeek README 声明；README 明确称尚未真机全面验证 |

版本号和 versionCode 只是初筛信息。真正的适配身份必须包含渠道线索、签名证书摘要、APK split 集合、DEX 哈希和结构特征。不同渠道不可共用未经验证的混淆映射。

此前记录的候选方法 Lrx3.g(Lvv1, Long):Lh60 只作为待核对静态线索，不视为已验证 Hook 点。国内版 2.7.1 的 YunSeek HostV271.java 提供了另一组符号证据，必须独立复核，不能直接套用到 Google Play 2.7.1。

## 4. 目标架构

- framework：libxposed 生命周期、能力探测、Hook Registry、HookHandle 管理。
- host：包名/渠道识别、版本 Profile、APK 与 DEX 指纹、符号解析。
- hooks：独立 HookInstaller、精确描述符校验、失败隔离和回滚。
- features：diagnostics、conversation、backup、prompt、local_api、appearance、privacy。
- data：模块自身 Room 数据库、设置、备份格式和迁移。
- ui：Jetpack Compose + Material 3 模块设置界面。
- security：日志脱敏、API 认证、速率限制、敏感数据生命周期管理。

建议包结构：
- org.qingjian.lspds.framework
- org.qingjian.lspds.host
- org.qingjian.lspds.hooks
- org.qingjian.lspds.features.*
- org.qingjian.lspds.data.*
- org.qingjian.lspds.ui.*
- org.qingjian.lspds.security

## 5. 技术选型

| 领域 | 首选 | 采用条件 / 备注 |
|---|---|---|
| 语言 | Kotlin | 新业务代码默认 Kotlin |
| Hook API | libxposed API 102 | 主线；不与 legacy XposedBridge 混用 |
| Hook 辅助 | 先用官方 API；必要时评估 EzHookTool / YukiHook | 先做兼容性 spike，核对许可证和维护状态 |
| 混淆定位 | 离线 DEX 分析 + 版本化符号表；必要时 DexKit | 动态扫描仅作受限 fallback，不可模糊匹配后直接修改行为 |
| UI | Jetpack Compose + Material 3 | 模块独立 UI；宿主 UI 注入另行评估 |
| 持久化 | Room / SQLite | 模块自身数据优先 Room；宿主数据库先只读 |
| 网络 | Ktor Server 或 NanoHTTPD 评估 | Loopback 默认、独立 API Key、限流、可关闭 |
| 流式协议 | 独立 SSE parser | 不把 DeepSeek 私有事件当作通用 API 协议 |
| 序列化 | kotlinx.serialization | JSON schema 版本化 |
| 异步 | Kotlin Coroutines | Hook 回调中不阻塞、不做长时间 I/O |
| 构建 | Gradle Kotlin DSL + AGP | CI 固定 JDK/Gradle/SDK |
| 测试 | JUnit + 按需 Robolectric/Instrumentation | parser、映射、脱敏、迁移、回滚均需测试 |

依赖不能仅因参考项目使用就引入。每项依赖需记录用途、许可证、版本、维护状态和替代方案。

## 6. Hook 注册与故障隔离

每个功能实现独立 HookInstaller：
- 输入：宿主 Profile、ClassLoader、框架能力。
- 前置：精确匹配类、方法、参数、返回类型、修饰符和必要字段。
- 执行：通过 libxposed HookBuilder 安装 Hook。
- 结果：返回安装状态、失败原因、目标描述符和证据 ID。
- 清理：保留 HookHandle，支持关闭或替换。
- 失败：记录脱敏诊断，不阻断其他 Installer。

禁止全局模糊 Hook、只按类名判断版本、旧版映射兜底新版、Hook 回调内磁盘/网络 I/O、默认采集请求响应正文。

## 7. 功能优先级

P0 工程和兼容性底座：模块加载、目标包作用域、宿主识别、版本指纹、诊断页、功能开关、崩溃隔离、构建发布。

P1 低风险功能：本地提示词库、模块侧会话置顶、单条消息导出、模块 UI 主题/字体、脱敏网络元数据诊断。

P2 宿主链路确认后：对话搜索、历史查看、对话导出、数据库备份恢复、消息编辑、批量管理。

P3 网络与协议：OpenAI Chat Completions 兼容 API、SSE 桥接、取消和背压、模型元信息。必须基于用户本机已授权客户端的正常功能，不输出宿主认证凭据，不绕过鉴权、PoW、限流或服务端权限。

P4 高级体验：多配置档案、界面定制、应用锁、防窥、自动备份计划和扩展接口。

## 8. 阶段交付与验收

| 阶段 | 产物 | 验收条件 |
|---|---|---|
| A 规划 | 调研、架构、功能规格、兼容策略、风险清单 | 文档齐全，事实与推测分栏 |
| B 工程基线 | Kotlin 工程、API 102 入口、CI | CI 编译通过，APK 可安装 |
| C 宿主识别 | 渠道/版本/DEX 指纹和映射目录 | 已知样本正确识别，未知样本 fail-closed |
| D Hook 底座 | Registry、Installer、HookHandle 管理 | 单个 Hook 失败不影响模块，禁用后可恢复 |
| E 诊断 | opt-in 元数据诊断 | 默认关闭、无正文/凭据、可清除 |
| F 数据功能 | 备份/导出/恢复 | 备份可还原、事务测试通过、真机验证 |
| G API | Release APK、OpenAI API、SSE | 协议测试、取消/超时/并发测试、loopback 安全 |
| H 稳定发布 | APK、校验和、中文说明 | CI、签名、安装、LSPosed 激活和目标版本回归完成 |

## 9. 明确不做

- 不抓取或导出密码、Cookie、Bearer Token、设备指纹或账号凭据。
- 不绕过登录、验证码、PoW、风控、配额、地区限制或服务端权限。
- 不伪造客户端身份、规避封禁或自动化滥用。
- 不将 DeepSeek 私有 APK、DEX、数据库、签名密钥或用户聊天数据提交到公开仓库。
- 不把静态发现、代码可编译或 CI 通过描述成真实设备 Hook 成功。
