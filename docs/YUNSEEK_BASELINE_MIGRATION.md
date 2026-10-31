# YunSeek 基线迁移与功能融合方案

> 状态：已确定技术路线；尚不代表 YunSeek 源码已迁入、构建成功或真机验证通过。
> 目标仓库：`qingjian0/Lspds`
> 主底包：`ZW-SYS/Deekseep-Custom`（YunSeek 1.8.1，GPL-3.0）

## 1. 决策

Lspds 后续不再以当前空白 Hook 骨架作为业务功能的主要来源，而以 YunSeek 1.8.1 的公开源码为业务基线，在其上进行独立改造、版本扩展和功能融合。

采用“**YunSeek 主干 + 经审计的功能移植 + Kotlin 新增层**”模式：

- 优先复用 YunSeek 已有的宿主识别、映射加载、Hook 生命周期和成熟功能实现。
- 不把其他项目的类名、混淆符号或 Hook 直接复制进来；先确认目标版本、调用契约、依赖和许可。
- 新增功能优先 Kotlin；YunSeek 已验证的 Java Hook 暂时保留，避免为了语言统一进行高风险重写。
- 逐步将稳定、边界清晰的通用能力迁移为 Kotlin；宿主专属 Hook 与业务逻辑分层。
- 不把预编译 APK 当作源码底包，不把 DeepSeek 官方 APK、DEX 或反编译完整产物提交到公开仓库。

## 2. 已核验的底包事实

本次核验到的 YunSeek 仓库信息：

- 仓库：`https://github.com/ZW-SYS/Deekseep-Custom`
- 本次锁定的上游基线 commit：`c315b82451d97e9668e2452992f1a0a2fd4a784a`（2026-10-31；后续更新必须显式审查差异后再升级）
- README 标识版本：YunSeek 1.8.1，模块 versionCode 37。
- 模块 applicationId：`com.dsmod.probe`。
- 许可证：GPL-3.0。
- README 声明的宿主支持范围：国内版 2.7.1/code287、国内版 2.7.2/code289、Google Play/国际版 2.7.2/code290。
- 已核验的关键源码路径：`HostCompat.java`、`HostV271.java`、`HookChatPipeline.java`、`HookAgentPipeline.java`、`HookAttachmentPipeline.java`、`HookSessionManagement.java`、`RemoteFeatureFlags.java`。
- README 自述 2.7.2 的结构映射有自动匹配和失配关闭机制；其同时明确指出部分 2.7.2 Hook 尚未完成真机全面验证。因此，仓库说明不能替代 Lspds 自己的回归测试。

以上是对公开仓库元数据、README 和源码目录的核验，不等于本项目已完成源码迁入。

## 3. 版本兼容策略

| DeepSeek 渠道 | versionCode | Lspds 处理 |
|---|---:|---|
| 国内版 2.7.1 | 287 | 可复用 YunSeek 对应 profile 作为候选基线，迁入后仍需回归 |
| Google Play 2.7.1 | 288 | 独立 profile；YunSeek 1.8.1 README 未声明支持，不能套用 287 |
| 国内版 2.7.2 | 289 | 可复用 YunSeek 对应 profile 作为候选基线，需验证其自动映射结果 |
| Google Play/国际版 2.7.2 | 290 | 可复用 YunSeek 对应 profile 作为候选基线，需验证其自动映射结果 |
| 其他版本 | 未确认 | 默认识别但不安装业务 Hook；先采样本再建立 profile |

宿主身份必须综合 packageName、versionCode、渠道证据、签名证书、base/split 组合和 DEX 指纹。versionName 单独不能作为 Hook 开关依据。

用户此前提供过的 Play 2.7.1 APKS（历史记录标注 code288）必须独立复核。若无法从当前文件库取回原样本，需要重新上传；不得把国内 287 的 `sx1` 等映射直接写入 Play 288。

## 4. 功能融合矩阵

| 功能域 | 主参考 | 融合方式 | 优先级 | 默认风险策略 |
|---|---|---|---:|---|
| 宿主识别与映射 | YunSeek HostCompat | 保留多渠道 profile 结构，增加证据清单和 Lspds 诊断 | P0 | 未知版本 fail-closed |
| Chat 请求链 | YunSeek HookChatPipeline | 保留精确构造器匹配、请求标记和按版本映射；补齐 Kotlin 观察接口 | P0 | 先只观察，不改写 |
| 原生设置入口 | YunSeek + Deekseep HookUiSurface | 保留宿主入口，Lspds 自有设置页面和功能开关 | P0 | 入口失败不影响宿主 |
| 提示词库 | YunSeek | 本地分类、编辑、导入导出；请求注入单独开关 | P1 | 用户主动启用；不上传 |
| 会话管理 | YunSeek + Deekseep HookSessionManagement | 置顶、隐藏标记、搜索导航、批量操作；先本地元数据和只读 | P1 | 数据库写入后置 |
| 消息工具 | YunSeek + Deekseep | Markdown/TXT 导出、纯文本复制、代码块提取、消息编辑 | P1 | 导出需用户触发 |
| 数据中心 | YunSeek + com.dsmod.probe | 配置备份/恢复、数据库只读检查、备份校验 | P1 | 修改前备份、事务回滚 |
| SSE/响应链 | Deekseep HookChatPipeline / ResponsePreserver | 独立 SSE parser、流式事件观察、响应保留能力评估 | P2 | 不伪造服务端响应、不绕过服务端拒绝 |
| 附件/图片 | Deekseep HookAttachmentPipeline | 附件元数据与 UI 能力独立适配 | P2 | 不读取非用户授权文件 |
| Agent UI | Deekseep HookAgentPipeline | 状态展示、触摸/无障碍交互独立安装 | P2 | 默认半自动、操作确认、限速 |
| 本地 API | com.dsmod.probe / GLMKit | Gateway、Backend、协议层分离；SSE、取消、超时 | P2 | 仅 loopback、自有 Key |
| UI 外观 | YunSeek + DeepTranslate 的弱引用/批处理思路 | 主题、字体、输入框等独立 feature | P3 | 失败自动关闭 |
| 运行诊断 | com.dsmod.probe + hxreborn/Deekseep | API 加载、宿主识别、Hook 状态、脱敏日志 | P0 | 默认不记录正文和凭据 |

## 5. 必须保留的安全边界

- 不实现登录绕过、PoW 绕过、风控规避、地区/额度/专家解锁或服务端访问控制绕过。
- 不读取、导出、记录或显示宿主 Cookie、Authorization、Bearer Token、密码、注册凭据或设备指纹。
- 不默认采集聊天正文、思维链或附件内容；诊断日志只记录脱敏元数据。
- Agent 默认半自动，涉及发送、删除、写入或外部操作时要求用户确认；设置限速和总量上限。
- Local API 仅监听 loopback，使用 Lspds 自己生成和保存的 API Key；不把宿主凭据转发给调用方。
- 数据库默认只读；任何写入、编辑、删除和恢复功能必须先备份，使用事务并支持回滚。
- 每个 Feature 独立开关、独立异常边界和状态；一个 Hook 失败不得阻断其他模块或宿主启动。

## 6. 代码迁移与包名策略

YunSeek 当前模块包名为 `com.dsmod.probe`，Lspds 现有模块包名为 `org.qingjian.lspds`。迁移时保留 Lspds 自己的 applicationId，避免与 YunSeek/Deekseep 已安装模块冲突。

迁移步骤：

1. 固定 YunSeek 1.8.1 的 Git commit SHA，保存来源、许可证、作者和文件级来源记录。
2. 盘点源码、资源、Gradle 插件、依赖、生成脚本、映射资源、测试和第三方许可。
3. 将 YunSeek 核心源码按目录分批导入 Lspds；不直接覆盖 Lspds 的 Manifest、applicationId、libxposed 元数据和 CI。
4. 保留必要的原始 Java 文件与版权头；新增功能优先 Kotlin。
5. 把宿主符号、Feature Installer、存储、UI 和 Local API 分层；避免把 YunSeek 的静态全局状态扩散到新模块。
6. 每批迁移先跑构建和单测，再做 APK 元数据检查；之后才进行宿主静态和设备验证。
7. 更新第三方许可证清单、NOTICE 和 README，明确 Lspds 的修改范围及上游来源。

## 7. 实施阶段与验收

### P0 — 锁定底包与可构建基线

- 固定 YunSeek commit SHA 与完整许可证文本。
- 导入核心源码和必要资源，保持 Lspds applicationId。
- 合并 Gradle/依赖，解决 Java/Kotlin 编译与 libxposed API 102 的边界。
- 确认 scope 只有 `com.deepseek.chat`。
- CI 完成 unit tests、assembleDebug、APK 元数据检查。

验收：CI 构建出 Lspds APK；LSPosed 可识别模块；未启用未验证的业务 Hook。

### P1 — HostProfile 与基础 Hook Runtime

- 移植 HostCompat 与映射资源加载器。
- 统一 HostProfile、符号解析、HookRegistry、Feature Installer。
- 加入逐项 Hook 诊断和安全模式。
- 对 287/289/290 做静态 Profile 对照；288 独立建档。

验收：未知宿主不安装业务 Hook；单个 Hook 失败不影响其他功能。

### P2 — 基础功能融合

- 设置入口、提示词库、模型显示名、会话置顶。
- 单条消息导出、快捷复制、搜索和会话导航。
- 所有本地功能先不依赖数据库写入。

验收：本地功能单测、迁移测试、隐私审计通过。

### P3 — 数据与网络能力

- 数据库 schema 只读检查、备份/恢复。
- SSE parser 与响应事件观察。
- Local API loopback gateway。

验收：故障注入回滚；API 不暴露宿主凭据；SSE 取消和断连无资源泄漏。

### P4 — 高级 UI/Agent/附件

- Agent 状态和确认流程。
- 附件/图片能力。
- 主题、字体、输入框和外观优化。

验收：各功能独立开关；不支持的宿主能力自动禁用；真实设备回归后才标记稳定。

## 8. 当前未完成事项

- [ ] YunSeek 源码尚未完整迁入 Lspds。
- [ ] 尚未锁定 YunSeek 的具体 commit SHA 作为本项目 vendor 基线。
- [ ] 尚未验证 YunSeek 与 Lspds 当前 Gradle/AGP/libxposed 配置的完整兼容。
- [ ] 尚未重新取得并核验 Play 2.7.1/code288 APKS。
- [ ] 尚未完成 Lspds APK 构建、安装和 LSPosed 真机测试。
- [ ] 上述功能矩阵均为迁移计划，不是已实现清单。

## 9. 上游来源

- YunSeek：<https://github.com/ZW-SYS/Deekseep-Custom>
- Deekseep 上游：<https://github.com/lllucccian/Deekseep>
- com.dsmod.probe：<https://github.com/Xposed-Modules-Repo/com.dsmod.probe>
- DeepTranslate：<https://github.com/sakukir/DeepTranslate>
- GLMKit：<https://github.com/qingjian0/GLMKit>

引用和再分发前必须逐项检查对应版本的 LICENSE、NOTICE 和第三方依赖许可；本项目现有 GPL-3.0 许可证与 YunSeek 的 GPL-3.0 主许可证方向一致，但不能因此省略第三方组件义务。
