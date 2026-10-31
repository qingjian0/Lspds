# YunSeek 源码审计与增量移植清单

> 审计日期：2026-10-31
> 唯一业务底包：`ZW-SYS/Deekseep-Custom`
> 锁定上游 Commit：`c315b82451d97e9668e2452992f1a0a2fd4a784a`
> 目标仓库：`qingjian0/Lspds`
> 状态：公开源码与文档静态审计；不是 Lspds 已完成迁移、构建或真机验收的声明。

## 1. 审计结论

YunSeek 1.8.1 不是只有少量 Hook 的轻量底座，而是已经包含多代宿主映射、152 个主模块 Java 源文件、一个 relay 源文件、Compose Launcher、多个传统 Xposed 适配层、映射生成工具、回归测试和自定义打包脚本的完整项目。

Lspds 应当复用其成熟的核心实现和宿主适配体系，不应把现有空白 Hook 骨架当成业务主线。与此同时，YunSeek 的旧构建方式和 Lspds 当前 Gradle/libxposed API 102 骨架并非天然兼容；必须先建立一个可构建的适配边界，再逐批迁移。

## 2. 底包身份与文件规模

- 仓库：`https://github.com/ZW-SYS/Deekseep-Custom`
- Commit：`c315b82451d97e9668e2452992f1a0a2fd4a784a`
- 版本：README 声明 YunSeek 1.8.1 / module versionCode 37
- 模块 applicationId：`com.dsmod.probe`
- 宿主 scope：`com.deepseek.chat`
- 许可证：GPL-3.0
- Git tree：428 个路径项（本次固定 commit 的完整 tree 查询）
- 主核心：`module/src/com/dsmod/probe/` 下 152 个 Java 文件
- Relay：`module/src/com/dsmod/relay/ExpertRelayGate.java`
- Launcher：4 个 Kotlin/Compose 源文件
- 宿主映射：`hostmap287.txt.gz`、`hostmap289.txt.gz`、`hostmap290.txt.gz`
- 自动映射工具：`module/hostmap/` 下 Python 匹配、审计、成员映射、重写和生成脚本
- 测试：HostCompat、2.7.1 port、消息编辑、备份、Agent 状态、UI 语言、协议、会话删除等多项 JVM/脚本回归测试

本次已审阅的关键文件：
- `HostCompat.java`、`HostRemap.java`、`HostV271.java`
- `Main.java`、`LegacyXposedModule.java`
- `HookChatPipeline.java`、`HookSessionManagement.java`、`HookAttachmentPipeline.java`、`HookAgentPipeline.java`
- `module-universal/build.sh`、`scripts/build-all.sh`
- `module/hostmap/pipeline.sh`、`module/tests/.../HostCompatRegressionTest.java`
- `README.md`、`PORT_NOTES.md`、`docs/FEATURES.md`、`THIRD_PARTY_NOTICES.md`

## 3. YunSeek 内部架构

### 3.1 加载与 Hook 主链

公开移植笔记记录的主流程：

`handleLoadPackage → HostCompat.initialize → supportsMaintainedFeatureRuntime → 生命周期初始化 → performModuleInjection → installAllFeatureHooks`

主要职责：

- `HostCompat`：宿主版本/渠道识别、逻辑符号名与宿主符号映射。
- `HostRemap`：读取宿主映射表；类、字段、方法反射访问的双向映射与精确方法重定位。
- `Main`：模块生命周期、功能开关、安装调度、通用反射和公共服务；文件体量很大，应避免把新增功能继续塞进 Main。
- `LegacyXposedModule`：将内部 around-hook / Hooker / Chain 合同适配到传统 Xposed 回调。
- `HostV271`：对 2.7.1 保留的原始符号入口和历史响应处理。
- `HookChatPipeline`：请求构造、提示词处理、流式/消息链相关逻辑。
- `HookSessionManagement`：会话列表、会话状态、会话操作与本地辅助逻辑。
- `HookAttachmentPipeline`：附件、图片、文件及关联 UI/消息处理。
- `HookAgentPipeline`：Agent 工具、状态、发送门控和 UI/消息桥接。
- `HookUiSurface`、`HookNavigationBridge`：宿主 UI 和导航入口。
- `ChatBackupStore`、`ConversationTools`、`HistoryBridge`、`ResponsePreserver`：数据和历史能力。

### 3.2 兼容层不是单一映射表

`HostCompat` 先识别宿主，再决定映射代际；`HostRemap` 加载对应表。映射表记录 class、field、method、alias、手工 relocation 和 2.7.x 原始符号转译等类型。

`HostRemap` 的方法匹配会校验运行时参数类型描述符，而不是只按方法名或参数数量猜测。未匹配类/成员按 fail-closed 路径处理。这套设计应作为 Lspds 的核心兼容机制保留。

映射生成链以 Androguard 静态提取为基础，结合结构签名、唯一字符串、引用传播、模糊邻域评分、成员匹配和人工审核。DexKit 可作为后续运行时/离线定位工具进行对照评估，但不应在没有基准测试前替换这套已存在的管线。

## 4. 宿主版本状态：必须分渠道、分证据等级

| 宿主 | versionCode | 公开资料状态 | Lspds 策略 |
|---|---:|---|---|
| 国内 2.7.1 | 287 | 有明确类/构造器锚点、映射表、HostRemap 与 verifier 记录；移植笔记记载部分功能设备反馈 | 先复用映射和白名单/验证逻辑，再做独立回归 |
| Google Play 2.7.1 | 288 | YunSeek 1.8.1 README 未列为支持目标 | 必须拿到 APKS 样本，独立建档 |
| 国内 2.7.2 | 289 | 有单独映射表；README 明确称仅完成沙盒映射校验，未全面真机验证 | 默认实验性/关闭，先 verifier 后设备回归 |
| Google Play/国际 2.7.2 | 290 | 有独立映射表；README 明确称仅完成沙盒映射校验，未全面真机验证 | 与 289 分开验收，不复用 289 表 |
| 其他版本 | — | 未确认 | fail-closed，不安装业务 Hook |

### 4.1 一个重要的文档/实现差异

YunSeek README 的概述强调 2.7.2 自动结构映射和失配关闭；`PORT_NOTES.md` 的最新进度记录则明确写出 2026-10-31 的 289/290 工作仍有待完成事项：逐版本手工 method relocation、运行 verifier、确认 `HostRemap` 版本表选择及剩余 Hook 缺失项。README 同时说明 289/290 尚未完成真机全面测试。

因此，Lspds 的兼容性表必须以代码、映射文件、verifier 输出和真实设备结果为准，不可只复制 README 的支持列表。289/290 暂不标记 Stable。

### 4.2 287 的具体静态锚点

YunSeek 的 2.7.1 移植笔记记录：
- `ChatFullCompletionRequest`：`sx1`，serializer `qx1`，marker interface `du1`，11 参数普通构造器，prompt 位于参数 index 2。
- `ChatHistoryMessagesResponseBizData`：`ey1`，serializer `cy1`。
- `AppStaticMessage`：`gy`，fragments 字段 `v`。
- `RequestFragment`：`v9a`，构造器 `(int, String)`。
- 设置页面 analytics 候选：`gt0.Q(String,String,Boolean,Boolean,Boolean,int)`。

这些只作为 287 profile 的证据，不得复制到 code288 或其他渠道。

## 5. 2.7.1 功能实际状态（依据 PORT_NOTES）

移植笔记在 2026-10-25 至 2026-10-31 记录的 287 状态：

### 已记录为较有把握的功能
- DeepSeek 设置页入口。
- 自定义 system prompt 注入与 Java 插件 prompt。
- 历史响应 system wrapper 清理。
- SQLite 只读数据工具：会话导出、搜索、统计、备份。
- 单会话复制/导出/分享/搜索工具。
- 字体缩放。

### 在 287 上明确禁用或未安装
- Agent/MCP/heartbeat Hook 与渲染 Hook（后续有 Agent 相关 2.7.1 修复记录，必须按具体 commit 核验，不可概括为全部可用）。
- dual chat、anti-recall/no-censor、response preserver、thinking code copy。
- 专家/额度解锁、登录/地区解锁、风险 SDK 中和、设备标识伪装。
- 专家图片中继、附件全类型、Local API（公开开源构建未包含）。
- 气泡/头像/主题/鲸鱼动画/文字波浪等宿主 UI Hook。
- 侧栏多选、原生设置行、导航路由管理、远程配置/更新弹窗相关 Hook。
- ChatEditorUi 的 287 数据库写入当时仍标记为未设备验证。

### 后续进展记录
`PORT_NOTES.md` 还记载了 Agent 手动确认门、自动模式限速、工具结果回传、图片导入等 2026-10-27/28 更新。但这类历史记录并不自动证明 1.8.1 当前 commit 对所有目标宿主都已通过真机验收；迁移时需逐项绑定 commit、测试记录和宿主 profile。

## 6. 安全与隐私审计注意事项

YunSeek README 声明将密码/注册信息捕获、风险 SDK 绕过、设备标识伪装、地区/登录/额度解锁等列为不实施功能，并使用 `HARD_LINES_ENABLED = false` 作为代码门控。静态搜索也能在相关入口看到该门控。

但相关 Java 源文件仍存在于源码树（例如账号安全 Hook、注册桥接、Google 登录解锁相关实现）。因此，Lspds 不能只继承一个布尔开关就认为安全边界已完成。迁移时必须：

1. 从构建源集/功能注册表中排除不需要的敏感 Hook。
2. 检查调用图，确认不存在旁路安装点、旧配置文件激活路径或远程开关重新开启路径。
3. 不迁移凭据捕获、认证/地区/额度绕过、风控中和、设备标识伪装、PoW 获取/重放等能力。
4. 不采集宿主 Cookie、Authorization、Bearer Token、密码、聊天正文或附件内容作为诊断日志。
5. Agent 仅保留用户可见、限速、可取消、需确认的合法本地自动化；不实现伪装或规避检测。
6. Local API 若重新实现，只允许 loopback 监听并使用 Lspds 自有 API Key；绝不转发宿主凭据。

## 7. 与 Lspds 当前工程的关键整合问题

Lspds 当前骨架：
- Gradle / AGP 工程。
- namespace/applicationId 为 `org.qingjian.lspds`。
- 现代 libxposed API 102 入口。
- 自有 manifest、scope 和 CI。

YunSeek 1.8.1 主模块：
- 传统 Xposed `handleLoadPackage` 入口。
- `LegacyXposedModule` around-hook adapter。
- `module-universal/build.sh` 通过 javac → d8 → aapt2 → zipalign → apksigner 构建。
- 独立 Compose Launcher 使用 Gradle/Kotlin。
- module 核心本身不是当前 Lspds 的 Gradle Android Library module。

这意味着不能把 YunSeek Java 文件简单复制到 `app/src/main/kotlin` 就宣称迁移完成。建议采用两层兼容方案：

**阶段 A：先让 YunSeek 核心可构建、可验证。**
- 保留 YunSeek 的 Hooker/Chain 合同与宿主映射逻辑。
- 将主核心作为独立 core source set/module 管理。
- 保留 Lspds 的 applicationId、scope 和自己的发布身份。
- 先建立传统 Xposed 兼容构建，作为功能基线；不与现有 API 102 入口混装/重复 Hook。

**阶段 B：再决定现代 API 102 适配。**
- 为 YunSeek Hooker/Chain 合同编写独立的 libxposed API 102 adapter。
- 对比 before/after/around 语义、原方法调用、参数替换、构造器 Hook、异常传播、Hook 优先级和取消行为。
- 用同一套 core regression suite 验证 Legacy 与 API 102 adapter 行为一致。
- 在 API 102 adapter 通过测试前，不发布双入口包、不让两个入口同时安装同一批 Hook。

如果 API 102 的 around-hook 语义无法无损承接，先保留 YunSeek 的 Legacy 构建路径，不为了统一 API 而重写已验证 Hook。

## 8. 参考项目源码级筛选结果

| 项目 | 本次核验 | 与 YunSeek 的关系 | 可用增量 | 不应直接复制 |
|---|---|---|---|---|
| `lllucccian/Deekseep` | 主干文件存在；HostCompat/Main/HookChatPipeline blob 与 YunSeek 不同 | 直接上游 | 对照上游原始逻辑、变更来源和回归差异 | 旧宿主映射、未适配 2.7.x 的符号 |
| `hxreborn/Deekseep` | 源码树含 API102、Legacy、Inject、MTest 多构建目录；VARIANTS/FEATURES 已读 | 架构参考 | API 102 与 Legacy 双轨、消息编辑/推理片段、搜索导航、备份、诊断 | 专家中继、敏感账号功能；旧版本映射 |
| `ZJF-Kedaya/Deekseep-fork` | 目录与上游分支有较大增量；README 仍主要说明 2.3.x 兼容 | 旧版 Fork | 作为旧功能/历史补丁对照 | 旧版本混淆映射、账号/风险相关实现 |
| `hhz522/Deepseek-Lsposed` | 公开树与 ZJF Fork 结构/README 高度相似 | 衍生 Fork | 仅在找到独有 commit 或文件差异时审阅 | 与已审阅 Fork 重复的代码 |
| `zjkkzk/Deekseep22222` | README blob 与 hhz522 仓库相同；树结构高度相似 | 同源/近重复候选 | 只审阅实际独有提交 | 重复代码和过时映射 |
| `KEJIYUNB/DeepSeekmok` | 公开树有多变体、实验功能和 Local API 文档；README 兼容范围较旧 | 历史功能参考 | 备份/恢复、协议设计、构建变体和旧实现差异 | README 所列账号解锁、风险中和、设备标识伪装等功能 |
| `hairball033/DeePX` | Git tree 只有 README、LICENSE 和 `DeePX.zip`；本次尚未解压审阅 ZIP | 候选参考 | 待解压后确认模块布局、Agent/UI/宿主适配 | 未审阅源码前不采纳任何符号或功能 |
| `LuckyPray/DexKit` | 项目元数据存在，需单独核验当前 API 与许可证 | DEX 工具 | 离线/运行时类结构定位实验 | 不替换 YunSeek 现有 Androguard 生成映射管线 |
| Androguard | YunSeek hostmap 脚本已经使用该技术路线 | 已有工具依赖 | 保留并完善 DEX 提取、结构签名与回归 | 不把离线匹配结果当作设备 Hook 验证 |
| Frida | 通用动态分析工具 | 测试工具 | 仅在自有/获授权测试设备做符号和调用验证 | 不用于绕过认证、风控或服务端限制 |
| OkHttp / okhttp-sse | 通用网络库 | 协议技术参考 | SSE 解析、取消、超时、流背压的独立单测 | 不替换宿主网络栈或截获认证凭据 |
| WCDB / SQLite | YunSeek 直接用 SQLiteDatabase 访问本地 DB | 数据层参考 | schema 检查、备份一致性、事务测试 | 不假定跨版本 schema 恒定 |

### 8.1 许可审计

- YunSeek 与原始 Deekseep：GPL-3.0。
- hxreborn 仓库 GitHub 元数据标记 MIT；移植前仍应核验 LICENSE 文件与具体文件版权。
- 其他 Fork 的 LICENSE 元数据/README 声明不总是一致，必须检查文件头、NOTICE、上游来源和实际许可证文本。
- 不能因为一个 Fork 的仓库显示 MIT，就推断其从 GPL 上游派生的全部文件都能按 MIT 重新许可。
- 所有引入文件需记录来源仓库、commit、原始路径、许可证、版权头、修改记录。

## 9. 功能移植队列（按风险与依赖排序）

| 阶段 | 功能/工程 | 来源 | 迁移策略 | 验收条件 |
|---|---|---|---|---|
| P0 | YunSeek 源码快照与 LICENSE/NOTICE | YunSeek | 固定 commit、文件级 provenance | 可追溯来源和许可证 |
| P0 | Core build + Xposed adapter | YunSeek | 建立隔离 source set，保持 Lspds 包名 | CI 构建成功，scope 仅目标包 |
| P0 | HostCompat/HostRemap | YunSeek | 原样迁入后加单测，不重写映射算法 | 287/289/290 静态测试；未知版本 fail-closed |
| P0 | 287 request/profile 基础 Hook | YunSeek | 先迁入并关闭非必要 Feature | JVM verifier + 目标设备回归 |
| P1 | 提示词库/注入 | YunSeek + hxreborn | 保留请求构造器精确匹配；用户显式开关 | 请求/历史不重复注入 |
| P1 | 单会话复制、导出、搜索 | YunSeek + hxreborn | 默认只读 DB；导出需用户触发 | schema 与数据完整性测试 |
| P1 | 备份/恢复 | YunSeek + hxreborn | 先只做备份；恢复必须备份前态、事务和校验 | 故障注入可回滚 |
| P1 | 消息编辑/Thinking | hxreborn | 独立 Feature，先只读预览，再验证写入 | 逐版本 DB schema 与设备验证 |
| P2 | SSE 解析与事件观察 | 独立实现/OkHttp 技术参考 | 仅观察公开可见的流事件，不记录正文 | 取消、超时、断连、背压测试 |
| P2 | Agent 半自动工具桥 | YunSeek | 保留确认卡、限速、可取消；不做伪装 | 每次外部动作可见且需确认 |
| P2 | 附件/图片 UI | YunSeek + Deekseep | 只接入用户主动选择的文件 | URI 权限与生命周期测试 |
| P2 | API 102 adapter | hxreborn + libxposed | 单独适配 Hooker/Chain 契约 | 双适配器同一测试集通过 |
| P3 | Local API | 重新设计 | loopback-only、自有 Key、无宿主凭据 | 网络安全与并发/取消测试 |
| P3 | 主题/字体/输入 UI | YunSeek | 独立 UI Feature，版本探测失败自动关闭 | Compose 版本回归 |

## 10. 下一阶段实际操作

1. 在 Lspds 单独分支建立 YunSeek 固定源码快照/子模块，不覆盖 main 分支和现有 CI。
2. 先迁入构建所需的最小源集、Xposed adapter、资源和 hostmap 文件。
3. 明确先以 Legacy adapter 建立可运行基线，随后实现 API 102 adapter；禁止双重注册 Hook。
4. 删除/排除不符合 Lspds 安全边界的源码文件和注册路径，而不是仅靠配置开关隐藏 UI。
5. 以 287 为首个可验收 profile；289/290 先做静态验证；288 等待 Play APKS。
6. 生成 Lspds 自己的构建、单测、APK 元数据、Hook 安装状态与设备验证报告。

## 11. 本次未完成事项

- [ ] DeePX.zip 尚未解压做源码审计。
- [ ] hxreborn 具体消息编辑/推理片段实现尚未逐方法比较。
- [ ] 各 Fork 尚未做完整 Git commit ancestry 和文件级差异审计。
- [ ] DexKit 版本/API 与 YunSeek hostmap 管线尚未做性能对比。
- [ ] YunSeek 源码尚未迁入 Lspds。
- [ ] Lspds 还没有基于 YunSeek 的构建产物或真机验收记录。
- [ ] Play 2.7.1/code288 APKS 尚未重新取得并核验。

## 12. 上游来源

- YunSeek：`https://github.com/ZW-SYS/Deekseep-Custom`
- Deekseep：`https://github.com/lllucccian/Deekseep`
- hxreborn：`https://github.com/hxreborn/Deekseep`
- DeePX：`https://github.com/hairball033/DeePX`
- ZJF Fork：`https://github.com/ZJF-Kedaya/Deekseep-fork`
- KEJIYUNB：`https://github.com/KEJIYUNB/DeepSeekmok`
- hhz522：`https://github.com/hhz522/Deepseek-Lsposed`
- zjkkzk：`https://github.com/zjkkzk/Deekseep22222`
- DexKit：`https://github.com/LuckyPray/DexKit`
