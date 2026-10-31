# DeepSeek Android Hook 深度研究与 Lspds 详细设计

> 研究日期：2026-10-31  
> 目标宿主：官方 DeepSeek Android，目标包名 `com.deepseek.chat`  
> 主仓库：`qingjian0/Lspds`  
> 主实现语言：Kotlin（新代码优先 `.kt`）  
> 本文性质：公开源码静态审阅、版本线索整理和工程设计。**不代表本文提到的 Hook 已在 Lspds 实现，也不代表所有参考项目的功能均已真机验证。**

---

## 0. 结论摘要

本轮对 Deekseep、YunSeek、com.dsmod.probe、hxreborn/Deekseep、DeepTranslate 和 GLMKit 做了仓库树、关键源码、README、功能文档及版本映射抽样审阅。结论：

1. **Lspds 不应以单个巨型 Hook 类为架构。** 应把宿主适配、Hook 安装、功能逻辑、数据存储和 UI 分开。
2. **版本映射是最核心的风险控制层。** 国内版、Google Play/国际版以及不同 versionCode 必须独立 Profile；相同 versionName 不意味着相同混淆类名。
3. **2.7.1 国内版与 Play 版必须分开。** YunSeek 源码明确记录国内 2.7.1/code287 的 `sx1` 请求类；用户此前提供的 Play 2.7.1 样本记录为 code288。不能把 `sx1` 映射复制到 Play code288。
4. **YunSeek 2.7.2 的映射策略值得借鉴，但其文档自己标明尚未完成真机全面验证。** 其 README 声称约 7600+ 类结构比对，36 条人工方法映射中 28 条迁移，8 条无法可靠定位的 Compose Hook 自动关闭。Lspds 必须保留“部分能力禁用”的策略，不能追求表面全功能。
5. **当前最适合 Lspds 的首批 Hook**：目标进程加载诊断、宿主 Profile 识别、只读请求边界探测、SSE parser 独立测试、模块设置入口。暂不移植数据库写入、账号切换、响应替换保护、图片中继或 Agent 工具执行。
6. **本地 API 应独立于宿主 Hook。** 参考项目的 Gateway/Backend 分层可借鉴，但 Lspds 默认只监听 loopback、只用模块自有 Key，不暴露宿主凭据，不开放局域网。
7. **最新宿主版本线索需要重新取样。** 公开第三方 APK 索引在 2026-10-30 出现 2.7.3/code292，但索引标出的包名为 `com.deepseek.chat.nov`，并非本项目锁定的 `com.deepseek.chat`。在未取得目标包 APK/APKS 并核验签名之前，不把 code292 归入 Lspds 的支持矩阵。

## 1. 审阅范围、方法与证据等级

### 1.1 本轮直接检查的仓库

| 仓库 | 本轮检查的文件/路径 | 重点 |
|---|---|---|
| [lllucccian/Deekseep](https://github.com/lllucccian/Deekseep) | `HookChatPipeline.java`、`HookAgentPipeline.java`、`HookAttachmentPipeline.java`、`HookSessionManagement.java`、`HookUiSurface.java`、README、功能说明 | 原始 Hook 功能拆分、请求构造、Agent、附件、会话和 UI |
| [ZW-SYS/Deekseep-Custom](https://github.com/ZW-SYS/Deekseep-Custom) | `HostCompat.java`、`HostV271.java`、`HookChatPipeline.java`、README、映射相关说明 | 国内 2.7.1/2.7.2 和国际 2.7.2 的宿主识别与有限白名单 |
| [Xposed-Modules-Repo/com.dsmod.probe](https://github.com/Xposed-Modules-Repo/com.dsmod.probe) | `LocalApiGateway.java`、`docs/FEATURES.md`、`docs/VARIANTS.md`、协议回归测试 | 现代 API 102 变体、激活诊断、Gateway/Backend |
| [hxreborn/Deekseep](https://github.com/hxreborn/Deekseep) | README、功能矩阵、构建变体说明 | API 102/Legacy/Test 分层 |
| [sakukir/DeepTranslate](https://github.com/sakukir/DeepTranslate) | `DeepTranslateModule.kt`、`TextViewHook.kt`、`TextBatcher.kt`、`TranslationCache.kt` | Kotlin Hook 组织、批处理、UI 更新和缓存 |
| [qingjian0/GLMKit](https://github.com/qingjian0/GLMKit) | `LocalApiGateway.java`、`GlmBackend.java`、`HostCompat.java` | 协议 Gateway 与宿主 Backend 解耦 |

### 1.2 源码快照

以下为本轮检查时 Git tree 或文件 blob 的快照标识，便于后续复核。上游可能继续更新，后续实现必须固定到实际采用的 commit。

| 仓库 | branch | tree SHA |
|---|---|---|
| Deekseep | main | `dd087f399853e26325ca70fa601777ec3659cfc9` |
| YunSeek | main | `c315b82451d97e9668e2452992f1a0a2fd4a784a` |
| hxreborn/Deekseep | main | `d5769391cd9048eb48a8006c72e9ef29915d824d` |
| com.dsmod.probe | main | `72f1d290860247defd3825d5a447133d45a876c3` |
| DeepTranslate | main | `6738a82af4edafd47973388b78f81ac613f1285a` |
| GLMKit | main | `9066c7bf1e3c4b50e1d1ff0c225721164c62f3da` |

关键文件 SHA：
- YunSeek `HostV271.java`: `d1ead01c2ca86163eea291875113876c1faf7b3b`
- YunSeek `HostCompat.java`: `f305a9147013cca6d789f37cba450542deb1c85f`
- YunSeek `HookChatPipeline.java`: `810c7f2f882b2413f93d73b2c236c0f00eb4443e`
- Deekseep `HookChatPipeline.java`: `e720a7b74f25b497661c5854f77970c29c7bbafb`
- Deekseep `HookAgentPipeline.java`: `8facf0fb439df92a8db9b0921b7a4c3ad39cb6c4`
- Deekseep `HookAttachmentPipeline.java`: `8cbdad157ae39a75b7fccc49494c28f4db8f362f`
- com.dsmod.probe `LocalApiGateway.java`: `5ac1d5ba10ac49aafdf58689d7e3f0674b06ad4c`

### 1.3 证据等级

| 等级 | 定义 | 可否用于启用 Hook |
|---|---|---|
| E0 — 文档宣称 | README/Release 说明功能或支持版本 | 不可 |
| E1 — 静态源码线索 | 源码中存在类名、反射签名、字段和映射逻辑 | 仅可列候选 |
| E2 — 目标样本确认 | 在精确渠道/版本 APK/APKS 中核对 DEX、签名、类/方法/字段 | 可进入运行时探测 |
| E3 — 运行时探测 | 在目标设备找到同一 class loader 中的目标类与精确方法签名 | 可在只读/无副作用模式验证 |
| E4 — Hook 行为验证 | 安装/卸载、回调顺序、参数与返回值均验证，宿主回归通过 | 可按功能 Feature Flag 启用 |
| E5 — 稳定性验证 | 多设备/多轮会话/异常和升级场景回归通过 | 可标记正式支持 |

**本轮大多数发现处于 E0/E1。** 不把仓库代码能编译、README 写支持或符号名存在，等同于 E4/E5。

---

## 2. DeepSeek 宿主版本与渠道矩阵

### 2.1 已有版本证据

| 渠道 | versionName | versionCode | 证据来源 | 当前 Lspds 状态 |
|---|---:|---:|---|---|
| 国内官方版 | 2.7.1 | 287 | YunSeek README、HostCompat、HostV271 | 参考源码有白名单映射；Lspds 未验证 |
| Google Play/国际样本 | 2.7.1 | 288 | 用户此前提供的 Play APKS 静态分析样本 | 目标样本待重新核验；不得套用 code287 |
| 国内官方版 | 2.7.2 | 289 | YunSeek README、HostCompat | 有独立结构映射线索；README 声称未完成真机全面验证 |
| 国际/Google Play | 2.7.2 | 290 | YunSeek README、HostCompat | 有独立映射线索；不得与 289 混用 |
| 第三方 APK 索引所列版本 | 2.7.3 | 292 | APKMirror 页面，2026-10-30 | 页面标注包名 `com.deepseek.chat.nov`；不属于已确认的 Lspds 目标包 |
| Google Play 2.5.0 | 2.5.0 | 268 | YunSeek HostCompat 注释 | 仅该项目说明已分析 GP 样本；功能符号并非全部确认 |
| 国内官方版 | 2.4.1 | 2.4.1 | 257 | Deekseep/YunSeek HostCompat | 历史参考版本 |
| 国内官方版 | 2.3.6 | 2.3.6 | 249 | Deekseep/YunSeek HostCompat | 历史参考版本 |
| 国内/Play | 2.3.4 | 245/246 | Deekseep/YunSeek 文档 | 渠道需分开 |
| 国内官方版 | 2.3.0 | 237 | Deekseep/YunSeek 文档 | 历史参考版本 |
| 国内/Play | 2.2.2 | 233/236 | Deekseep/com.dsmod.probe 文档 | 明确独立映射 |

来源提示：Deekseep 的公开兼容文档说明 2.2.2 国内 code233 与 Play code236 使用独立 APK/映射；YunSeek README 声明 2.7.1 国内 code287、2.7.2 国内 code289、国际版 code290，并说明 2.7.2 映射尚未完成真机全面验证。第三方 APK 索引在 2026-10-30 显示 2.7.3/code292，但包名为 `com.deepseek.chat.nov`，因此只作为“新版本存在”的提醒，不能作为本项目宿主版本依据。

### 2.2 为什么不能只判断 versionName

同一版本名可能存在：
- 国内/Play 不同渠道；
- 不同 `versionCode`；
- 不同签名证书；
- base APK 与 split APK 组合差异；
- 不同 R8 混淆类名；
- 不同 Compose 编译结果或资源 ID；
- 不同保护/启动代码。

YunSeek 的 HostCompat 使用多个锚点区分构建，不是只读取版本字符串。例如：
- `com.pairip.licensecheck.LicenseActivity` 作为一个渠道线索；
- completion request 类的构造器形状；
- 目标类实现的 transport marker interface；
- kotlinx.serialization serializer/serial name；
- 版本特定的 ViewModel 契约。

这些锚点必须组合使用。单一标志缺失不能证明是另一渠道。

### 2.3 Lspds 版本 Profile 目录

建议结构：

```text
app/src/main/assets/host-profiles/
  manifest.json
  mainland/
    2.7.1-287/profile.json
    2.7.2-289/profile.json
  google-play/
    2.7.1-288/profile.json
    2.7.2-290/profile.json
  unknown/
    deny.json
docs/host-evidence/
  mainland/2.7.1-287.md
  mainland/2.7.2-289.md
  google-play/2.7.1-288.md
  google-play/2.7.2-290.md
```

每个 Profile 至少包含：
- packageName；
- versionName、longVersionCode；
- channel 与渠道证据；
- signing certificate digest；
- base/split APK SHA-256；
- classes*.dex SHA-256；
- DEX 数量、class 数量；
- 关键类/方法/字段 descriptor；
- serializer 名称/字段名锚点；
- 资源名/资源 ID（只作辅助）；
- 样本来源、分析工具版本、日期；
- 静态验证级别和设备验证级别。

APK、APKS、DEX、反编译完整输出、数据库和真实日志不提交公开仓库。公开仓库只存放最小必要哈希、descriptor、分析脚本与证据摘要。

---

## 3. 参考项目 Hook 类结构审计

### 3.1 Deekseep / YunSeek 核心类

本轮源码树中识别到的核心文件：

| Hook/组件类 | 职责（按源码结构/方法名归纳） | Lspds 设计映射 |
|---|---|---|
| `HostCompat` | 识别渠道/版本、旧逻辑符号到当前宿主符号转换 | `HostProfileResolver` + `SymbolTable`，拆分静态 Profile 与运行时探测 |
| `HostRemap` | 版本符号映射、类加载与 unmapped sentinel | `HostSymbolResolver`，严格拒绝未映射符号 |
| `HostV271` | 2.7.1 国内版有限白名单 Hook | 每个版本单独的 `HostAdapter`，禁止把它当成通用版本表 |
| `HookChatPipeline` | 请求对象构造边界、提示词拼接、请求事件、流式/会话协作 | `ChatRequestObserver`（初期只读）和 `PromptTransformFeature`（后期可选） |
| `HookAgentPipeline` | Agent/tool UI、工具执行状态、Compose/Canvas 交互与展示 | 暂不移植；以后拆成独立 UI observer 与工具协议适配 |
| `HookAttachmentPipeline` | 文件/图片消息、模型文件卡片、图片与附件路径处理 | 先只读附件元数据；暂不做专家模式中继 |
| `HookSessionManagement` | 侧栏会话、原生导航、选择/删除、活动会话状态 | `SessionReadAdapter`；首期只读，不注入批量删除 |
| `HookUiSurface` | 设置入口、消息气泡、输入区、Compose UI 适配 | `SettingsEntryFeature` 与独立 UI adapter |
| `HookAccountLoginSecurity` | 登录/账号相关 Hook | 不移植凭据捕获、登录绕过或账号池逻辑 |
| `HookConfigRollout` | 功能开关/灰度配置 | 参考 feature flags，不做远程强制配置 |
| `HookLogOverlay` | 可视化诊断和事件记录 | 只保留脱敏、用户主动开启的本地诊断 |
| `HookNavigationBridge` / `HostNavigationCatalog` | 宿主导航和页面路由适配 | 独立导航观察层 |
| `ResponsePreserver` / `HistoryBridge` | 本地响应状态保存、历史同步和注入内容清理 | 先研究状态模型；不做安全拒绝绕过 |
| `ChatBackupStore` / `ChatEditorUi` | 数据备份、会话编辑 UI | 先做 schema inspector 和只读导出，写入后置 |

### 3.2 2.7.1 国内版 HostV271 白名单的真实边界

YunSeek 的 `HostV271.java` 文件头写明它是 **Mainland DeepSeek 2.7.1 / versionCode 287** 专用白名单。该类源码标出的候选结构：

| 逻辑类型 | 2.7.1 国内混淆符号 | 源码说明 | 证据 |
|---|---|---|---|
| `ChatFullCompletionRequest` | `sx1` | serializer `qx1`，实现 marker interface `du1`；常规构造器 11 参数，prompt 在 index 2 | E1 |
| `ChatHistoryMessagesResponseBizData` | `ey1` | serializer `cy1`；字段 a=ServerChatSession、b=消息列表、c=cache_control、d=cache_reset_at | E1 |
| `AppStaticMessage` | `gy` | serializer `dy`；fragments 列表字段 `v` | E1 |
| `RequestFragment` | `v9a` | 构造器 `(int, String)`；fragment 字段 a=type、b=id、c=content | E1 |
| 设置页面事件分发器 | `gt0.Q(String,String,Boolean,Boolean,Boolean,int)` | 注释描述 `page_show` analytics dispatcher；页面参数可观察 settings route | E1 |

HostV271 当前只尝试：
1. 设置入口 Hook；
2. `HookChatPipeline.INSTANCE.hookChatRequest()`；
3. 历史响应构造后的 system prompt 清理。

该文件明确说明，找不到可信映射的旧逻辑符号会解析成不可加载的 sentinel，防止旧 Hook 误挂到无关类。Lspds 必须沿用“未映射即禁用”的原则。

**重要：这张表只适用于 code287 的源码线索。不能复制到 Google Play code288。**

### 3.3 HostCompat 的版本识别策略

YunSeek `HostCompat.initialize(ClassLoader)` 展示了分层识别思路：
- 先检查 PairIP LicenseActivity 线索；
- 再检查候选 completion request 类是否具有精确的 11 参数构造器形状；
- 同时检查类是否实现预期 marker interface；
- 识别出 2.7.x 后尝试激活该版本映射；
- 映射表不存在时进入 limited mode，而不是无条件套用旧版本；
- 2.5.0 GP 单独识别 `r51`，并只开放已确认的 request 类、edit request 类和 repository 方法；
- 对尚未验证的 GP 2.3.6 等路径明确不猜测。

YunSeek 的 `HostCompat.name271()` 对 2.7.1 白名单只映射：
- 旧逻辑键 `ew0` → `sx1`（completion request）；
- 旧逻辑键 `pw0` → `ey1`（history response）；
- 其他逻辑键 → 不可加载 sentinel。

这意味着“完整旧功能集”不能仅凭两个符号就认为已迁移。Lspds 需给每个 Feature 定义最低符号集合，并在安装前逐项检查。

---

## 4. Chat Request Hook 详细设计

### 4.1 参考源码的请求边界

YunSeek `HookChatPipeline.hookChatRequest(ClassLoader)` 的核心策略：
- 找到版本对应的 `ChatFullCompletionRequest`；
- 遍历构造器；
- 识别 Kotlin serialization synthetic constructor（首参为 int）；
- 常规构造器 prompt 位于 index 2，synthetic 版本候选 index 3；
- 跳过 synthetic constructor，避免把序列化/复制路径误认为真实发送边界；
- 仅在参数类型满足 String 等精确条件时安装；
- 通过原始 Hook chain 调用宿主原方法；
- 对本地 API 请求设置 ThreadLocal 标记，避免重复注入 UI 全局提示词；
- 通过 conversation scope、parent message id 和 turn counter 控制每轮提示词注入。

Lspds 第一阶段**不改写 prompt**，只验证调用频率、构造器 descriptor 和脱敏元数据。

### 4.2 目标请求模型

建议抽象：

```kotlin
data class HostChatRequestSnapshot(
    val hostProfileId: String,
    val requestKind: RequestKind,
    val promptLength: Int,
    val attachmentCount: Int?,
    val reasoningEnabled: Boolean?,
    val searchEnabled: Boolean?,
    val constructorDescriptor: String,
    val observedAtElapsedMs: Long
)

enum class RequestKind {
    NEW_MESSAGE,
    EDIT_OR_REGENERATE,
    RESUME,
    UNKNOWN
}
```

注意：初期不得把原始 prompt、会话 ID、消息 ID、附件路径或凭据放进诊断对象。只保留必要计数和 descriptor。

### 4.3 Lspds Hook 安装契约

```kotlin
interface HostFeatureInstaller {
    val id: String
    fun probe(context: HostRuntimeContext): ProbeResult
    fun install(context: HostRuntimeContext): InstallResult
    fun uninstall()
}

sealed interface ProbeResult {
    data class Supported(val evidence: EvidenceRef) : ProbeResult
    data class Unsupported(val reason: String) : ProbeResult
    data class Ambiguous(val candidates: List<String>) : ProbeResult
}

sealed interface InstallResult {
    data class Installed(val handles: List<HookHandleRef>) : InstallResult
    data class Skipped(val reason: String) : InstallResult
    data class Failed(val category: String) : InstallResult
}
```

真实实现需要与 libxposed API 102 的 HookHandle 类型适配；上述接口是 Lspds 自己的领域契约，不是 libxposed API 原样类型。

### 4.4 请求 Hook 验收

- 每个支持 Profile 精确匹配目标构造器。
- 只读模式下原始参数、返回对象和异常不被修改。
- Hook 回调必须调用原始 chain，不能吞掉异常或伪造响应。
- Hook 安装失败只禁用该功能，不影响其他 Installer。
- 重复调用 install 不得重复 Hook。
- 关闭 Feature Flag 后无额外读取/事件记录。
- 不记录 Prompt、Authorization、Cookie、PoW 或账号信息。

---

## 5. SSE / Streaming Hook 设计

### 5.1 分离传输观察与 SSE 解码

Deekseep 的 HookChatPipeline 与 HookAgentPipeline 有大量流式消息、响应片段、推理状态和工具 UI 交互。Lspds 不应把 SSE parser 嵌入宿主混淆类适配器。

设计分层：

1. **TransportProbe**：只识别当前实际网络栈和响应边界。
2. **SseDecoder**：纯 Kotlin、与 Android UI 和宿主符号无关。
3. **HostStreamAdapter**：把宿主版本特定事件映射成统一内部事件。
4. **StreamObserver**：仅观察计数/状态；初期不修改事件。
5. **ProtocolAdapter**：未来本地 API 使用，与宿主 Hook 解耦。

### 5.2 统一事件模型

```kotlin
sealed interface StreamEvent {
    data class Data(val value: String) : StreamEvent
    data class EventName(val value: String) : StreamEvent
    data class Id(val value: String) : StreamEvent
    data class Retry(val millis: Long) : StreamEvent
    data object Done : StreamEvent
    data class Comment(val value: String) : StreamEvent
    data class ParseError(val category: String) : StreamEvent
}
```

### 5.3 必须覆盖的解析测试

- CRLF、LF、CR；
- UTF-8 字符跨 buffer/chunk；
- 多行 data 合并；
- event/id/retry/comment；
- 空行触发 dispatch；
- EOF 时未完成帧处理；
- `[DONE]`；
- 空 data；
- 超大帧/超大行限额；
- 取消、断连和异常；
- 连续多个 event；
- JSON 内容中包含换行转义。

SSE parser 不应假设一个网络 read 就是一个完整 event。

### 5.4 安全边界

- 默认只记录传输层、状态码、耗时、字节数和错误类别。
- 正文/推理内容观察默认关闭。
- 不尝试绕过 TLS pinning、证书校验、认证、PoW、服务端限流或拒绝。
- 不记录原始 Authorization/Cookie/Set-Cookie。
- 不把服务端未下发的数据“恢复”出来。

---

## 6. HookAgentPipeline / Agent UI 研究与策略

Deekseep 的 `HookAgentPipeline.java` 是大型 UI/事件适配器，源码包含：
- 工具执行 claim/dedupe；
- 工具调用状态与输出追踪；
- Compose/Canvas 绘制对象观察；
- Agent tool log touch surface；
- 无障碍语义节点扫描；
- 点击手势识别与详情 Sheet；
- thinking/heartbeat 流状态。

这些能力说明 DeepSeek 的 Agent UI 不只是网络请求：工具状态、流式消息、Compose 绘制和交互层都需要关联。

**Lspds 不在首期执行 Agent 工具 Hook。** 未来若做只读 Agent 诊断，分成：
- `AgentEventObserver`：观察已公开/已呈现的状态；
- `AgentUiAdapter`：仅在确认 UI 版本后定位视图；
- `AgentToolProtocol`：独立协议模型，不直接执行宿主工具；
- `AgentTraceStore`：仅存脱敏计数和生命周期状态。

不得通过 Hook 注入任意工具执行、读取其他应用数据或绕过宿主授权。Agent UI 类名必须由精确 Profile 提供，不得在所有版本上使用一个 R8 名称。

---

## 7. HookAttachmentPipeline / 图片和文件链路

Deekseep 的 `HookAttachmentPipeline.java` 源码可见以下方向：
- 模型文件卡片/附件跳转；
- 宿主文件 API 与 composer 对象捕获；
- 消息构造和历史数据中的图片保留；
- 专家模式相关 UI/功能开关；
- 流式模型文件输出累积；
- 本地图片路径/URI 处理。

Lspds 采纳顺序：
1. 只读读取附件类型、数量和已脱敏 MIME 类别；
2. 验证 SAF/系统 Photo Picker 的 URI 权限；
3. 做模块侧文件导出和用户确认；
4. 最后才评估宿主消息附件编辑。

首期不做：
- 专家模式解锁；
- 视觉请求中继；
- 私有图片接口重放；
- 伪造服务端附件状态；
- 对宿主私有文件目录进行无备份覆盖。

附件 Hook 的回归用例必须覆盖图片、普通文件、文件 URI 失效、权限撤销、重复附件、历史消息重载和进程重启。

---

## 8. HookSessionManagement / 会话与数据库设计

### 8.1 参考实现揭示的复杂性

Deekseep 的 `HookSessionManagement.java` 不只是一个侧栏长按 Hook。源码可见：
- Compose 侧栏会话行和原生 click callback；
- 多选状态、行边界采集和覆盖层；
- 会话当前状态与原生 session repository；
- 本地会话合并；
- 删除后延迟可见性和 UI 修复；
- 原生导航和跨页面会话打开。

`ChatEditorUi`、`ChatBackupStore`、`ResponsePreserver`、`HistoryBridge` 又引入数据库、消息 parent chain、推理片段、附件和历史同步。只改 SQLite 一张表会造成 UI、内存状态和数据库不一致。

### 8.2 Lspds 的安全顺序

**阶段 A：只读**
- 获取 DB schema 版本、表名、字段、索引和外键；
- 读取 session list 的最小元数据；
- 对话导出只由用户显式触发；
- 不保存会话 ID 到日志；
- 不在模块启动时全量扫描聊天正文。

**阶段 B：备份**
- 检查 SQLite WAL/SHM；
- 设计一致性快照；
- 记录 DB schema/version 与 Profile；
- 备份写入模块私有目录；
- 提供完整性校验和恢复预演。

**阶段 C：索引**
- 只对用户选择的会话建立索引；
- 可暂停、可删除、可重建；
- 限制容量并支持过期清理。

**阶段 D：写入**
- 只在用户明确确认后启用；
- 先备份；
- 事务化；
- 版本校验；
- 写入失败原子回滚；
- 重启后检查宿主 repository 和 UI 一致性。

批量删除、编辑历史回复、推理片段编辑和图片替换均属于高风险功能，默认关闭，必须有设备回归证据。

---

## 9. UI Hook 与模块设置入口

### 9.1 参考项目做法

Deekseep 的 `HookUiSurface` 和 `HostV271` 展示两种路径：
- 观察宿主 settings route / analytics page event；
- 在设置页面内注入原生 View 或 Compose-compatible 内容。

YunSeek 2.7.1 的设置入口候选锚点是 `gt0.Q(...)` 的 `page_show` 事件，检测参数是否为 `settings` 后，在主线程显示/隐藏模块入口；找不到时回退到 always-visible entry。

com.dsmod.probe 的文档强调，现代 API 的模块加载状态与目标进程加载状态应分开。模块自身 Activity 能启动，不代表 DeepSeek 进程已经加载 Hook。

### 9.2 Lspds 建议

优先级：
1. 模块 Activity 独立设置页（无宿主 Hook）；
2. 目标进程 heartbeat 与模块状态联动；
3. 运行时确认宿主设置入口后再注入；
4. UI Hook 失败时保留独立 Activity，不要让入口失败阻断其他功能。

不把 Compose 私有内部函数作为唯一 UI 注入点。避免在全局 `View`/`TextView` 高频方法上做昂贵反射或网络操作。

---

## 10. Kotlin Hook 工程组织参考：DeepTranslate

DeepTranslate 不是 DeepSeek 内部 Hook 项目，但其 Kotlin 实现值得借鉴：
- `DeepTranslateModule.kt`：现代 `XposedModule` 入口，在 `onPackageLoaded` 中按包名初始化；
- `hook/BaseHook.kt`：Hook 基础封装；
- `hook/TextViewHook.kt`：按精确 overload Hook 多种 `TextView.setText`；
- `TextBatcher.kt`：线程安全队列、批次窗口、单线程 executor、主线程回调；
- `TranslationCache.kt`：模块私有 SQLite 缓存。

Lspds 可以借鉴：
- 每类 Hook 独立文件；
- 精确 overload 选择；
- 弱引用 UI 对象，避免 Activity/View 泄漏；
- 后台执行与主线程更新分离；
- 批量任务、缓存和失败回退；
- Hook 单点失败不影响其他 Hook。

不照搬：
- 全局 TextView Hook 作为 DeepSeek 消息 Hook 的替代；
- 自动向第三方 API 上传宿主所有文本；
- 与本项目无关的 SHA-256 文本缓存实现；
- 对 TextView.setText 的无差别翻译逻辑。

DeepTranslate 仅是 Kotlin Hook 结构参考，不为 DeepSeek 内部符号提供任何证据。

---

## 11. LocalApiGateway / GLMKit 架构研究

### 11.1 参考结构

com.dsmod.probe `LocalApiGateway.java` 可见：
- `Backend` 接口；
- `CompletionRequest`；
- `CompletionResult`；
- `DeltaSink`；
- `GatewayException`；
- OpenAI/Anthropic protocol mode；
- request deadline、response state TTL、tool history；
- ServerSocket、accept thread、worker pool；
- SSE heartbeat、请求取消和状态统计。

GLMKit 采用类似分层：Gateway 负责协议入口，Backend 把外部请求转换为宿主能力，避免把 HTTP 协议解析和 DeepSeek 反射 Hook 写在一起。

### 11.2 Lspds 目标架构

```text
LocalApiServer
  ├── HttpRequestParser
  ├── Authenticator (module-owned key)
  ├── OpenAiChatCompletionsAdapter
  ├── SseResponseWriter
  ├── RequestRegistry / Cancellation
  └── CompletionBackend
        └── HostChatAdapter
              └── Version-specific Hook/Reflection adapter
```

强制约束：
- 默认只绑定 `127.0.0.1` 和 `::1`；
- 不默认开放 LAN、0.0.0.0 或公网隧道；
- 使用模块自己的随机 Gateway Key；
- 不把 DeepSeek Cookie、Authorization 或会话凭据返回给调用方；
- 每个请求有独立 requestId、deadline、取消标记和并发限额；
- 客户端断开后取消宿主 Flow/请求收集；
- 不支持的参数明确返回错误；
- 不宣称与 OpenAI 全量兼容，只标记已测试的子集。

参考项目曾使用局域网地址和较宽松的功能配置；Lspds 不照搬其网络暴露策略。

---

## 12. 版本映射与反射实现规范

### 12.1 统一符号描述符

不再只保存混淆类名字符串。每个 Hook target 使用描述符：

```kotlin
data class MethodDescriptor(
    val owner: String,
    val name: String?,
    val parameterTypes: List<String>,
    val returnType: String,
    val isStatic: Boolean?,
    val minMatches: Int = 1,
    val maxMatches: Int = 1
)

data class FieldDescriptor(
    val owner: String,
    val name: String,
    val type: String,
    val isStatic: Boolean?
)
```

实际符号表必须可按 Profile 选择，禁止将 code287 的 owner/name 写成全局常量。

### 12.2 结构锚点优先级

1. 序列化 serial name / descriptor；
2. 构造器参数类型与顺序；
3. marker interface；
4. 精确方法签名；
5. 字段类型、读写关系；
6. 调用点与数据流；
7. analytics/resource string；
8. 单纯短混淆名（最低可信度）。

字符串锚点只能辅助，不能独立决定 Hook 安装。

### 12.3 Hook 解析与安装失败

- class not found：该 Feature 标记 Unsupported；
- descriptor 匹配多个 overload：标记 Ambiguous，不安装；
- 方法参数/返回类型不符：Block；
- Hook API 抛异常：记录异常类别和 Profile ID，不输出敏感对象；
- 任何单个 Hook 失败不得使其他 Feature 安装失败；
- 对每个 HookHandle 保存生命周期，支持统一 uninstall；
- 运行时检测重复安装并幂等处理。

### 12.4 不允许的 fallback

- 不允许“找不到新版本符号就使用旧版类名”；
- 不允许只按 class simpleName 匹配；
- 不允许任意 hook 所有同名方法；
- 不允许以类存在作为完整功能兼容证明；
- 不允许把国内版映射用于 Play；
- 不允许把一个版本的 DEX mapping 通过比例/相似度推定为另一个版本的完整映射。

---

## 13. Lspds Kotlin 目标目录

建议后续演进为：

```text
app/src/main/kotlin/org/qingjian/lspds/
  entry/
    LspdsModule.kt
    HostProcessLifecycle.kt
  runtime/
    HostRuntimeContext.kt
    HookRegistry.kt
    HookInstaller.kt
    HookHandleRegistry.kt
    FeatureState.kt
  host/
    HostProfile.kt
    HostProfileResolver.kt
    HostSignatureProbe.kt
    HostDexFingerprint.kt
    HostSymbolResolver.kt
    descriptor/
      MethodDescriptor.kt
      FieldDescriptor.kt
    adapters/
      HostAdapter.kt
      mainland/
        Mainland271Adapter.kt
        Mainland272Adapter.kt
      googleplay/
        GooglePlay271Adapter.kt
        GooglePlay272Adapter.kt
  features/
    diagnostics/
      HostLoadHeartbeatFeature.kt
      RuntimeProbeFeature.kt
    settings/
      SettingsEntryFeature.kt
    chat/
      ChatRequestObserver.kt
      ChatRequestSnapshot.kt
    stream/
      SseDecoder.kt
      HostStreamAdapter.kt
      StreamObserver.kt
    session/
      SessionReadAdapter.kt
      SessionExportFeature.kt
    backup/
      DatabaseSchemaInspector.kt
      BackupCoordinator.kt
    localapi/
      LocalApiServer.kt
      OpenAiProtocolAdapter.kt
      CompletionBackend.kt
      SseResponseWriter.kt
  data/
    ModuleDatabase.kt
    FeaturePreferences.kt
    DiagnosticStore.kt
  ui/
    MainActivity.kt
    settings/
    diagnostics/
```

新文件默认 Kotlin。若某参考 Hook 只有 Java 源码，不意味着必须复制 Java；先翻译为 Kotlin，并通过测试确认行为等价。只有 JNI/Java ABI 或第三方 Java API 明确要求时才保留 Java interop。

---

## 14. Lspds 分阶段实现顺序与验收

| 阶段 | 实现内容 | 先决条件 | 验收标准 |
|---|---|---|---|
| P0 | CI、Gradle Wrapper、API 102 生命周期稳定 | 工程骨架 | CI 测试与 APK 构建通过 |
| P1 | HostProfile、渠道识别、DEX/签名指纹 | 取得目标 APKS | 精确识别 code288；未知构建 fail-closed |
| P2 | HookRegistry、descriptor resolver、单点失败隔离 | P1 | Hook 重复安装无副作用；失败不影响其他 Feature |
| P3 | 目标进程 heartbeat、只读 runtime probe | P2 | 能区分模块启用和宿主进程加载 |
| P4 | Settings Entry Hook | P3 + 精确页面锚点 | Hook 失败可回退独立 Activity |
| P5 | Chat request observer | P3 + 请求构造器 E2 证据 | 不改请求、不记正文、回归通过 |
| P6 | SSE decoder 与 stream observer | 独立 parser 测试 | 完整覆盖 chunk/CRLF/cancel/error |
| P7 | 只读 session schema / export | DB 样本与备份方案 | 不写宿主 DB；导出内容由用户主动选择 |
| P8 | DB backup / restore | P7 | WAL/SHM 一致性、恢复和故障注入通过 |
| P9 | Local API 最小子集 | P5/P6 | loopback-only、自有 Key、取消/超时和协议测试 |
| P10 | UI/Agent/附件等高级 Hook | 每个功能独立 E2/E3 | 独立开关、设备回归、可回滚 |

**优先目标版本**：先重新验证用户提供的 Google Play 2.7.1/code288 样本；国内 2.7.1/code287 作为单独 Profile 参考。2.7.2 code289/290 等待真实目标 APK/APKS 和独立证据。2.7.3/code292 暂不进入目标矩阵，因公开索引的包名与目标包不一致。

---

## 15. 安全、隐私和许可证边界

### 15.1 功能边界

不实现：
- 绕过登录、PoW、风控、限流、服务端拒绝或访问控制；
- 提取/导出 DeepSeek 账号 Cookie、Bearer Token 或其他认证材料；
- 账号池、账号共享、地区/登录限制解锁；
- SSL pinning bypass 或证书校验绕过；
- 未经用户同意的聊天正文采集；
- 静默批量删除、静默数据库写入或隐蔽持久化。

### 15.2 许可证

- Deekseep/YunSeek 声明 GPL-3.0-only；如吸收其衍生代码，必须遵守 GPL-3.0-only 的版权、许可证和源代码提供义务。
- DeepTranslate 声明 MIT；移植需保留 LICENSE 和版权声明。
- hxreborn/Deekseep 与 com.dsmod.probe 的 GitHub SPDX 元数据标为 MIT，但 README/Workflow/代码来源可能涉及上游衍生；必须逐文件核验，不能只凭仓库徽章。
- 未声明许可或闭源 Closed 版本只允许研究，不复制实现。
- 本文的架构描述和符号摘要不等于授权复制任何源码。

---

## 16. 参考来源与外部版本线索

- [Deekseep upstream](https://github.com/lllucccian/Deekseep)：功能、历史兼容矩阵和 GPL-3.0-only 声明。其 README 已声明公开维护暂停，不能假定后续功能仍有公开源码。
- [YunSeek / Deekseep-Custom](https://github.com/ZW-SYS/Deekseep-Custom)：HostV271、HostCompat、HookChatPipeline 和 2.7.x 映射说明。
- [com.dsmod.probe](https://github.com/Xposed-Modules-Repo/com.dsmod.probe)：API 102/Legacy 变体、激活检测、功能和兼容文档。
- [hxreborn/Deekseep](https://github.com/hxreborn/Deekseep)：现代/Legacy/Test 变体组织。
- [DeepTranslate](https://github.com/sakukir/DeepTranslate)：Kotlin Xposed 模块结构、TextView Hook、批处理和缓存。
- [GLMKit](https://github.com/qingjian0/GLMKit)：Gateway/Backend 协议分层。
- [libxposed API](https://github.com/libxposed/api)：现代模块 API。
- [LSPosed](https://github.com/LSPosed/LSPosed)：框架本身。
- [APKMirror 2.7.3 listing](https://www.apkmirror.com/apk/deepseek/dseek-your-ai-assistant/dseek-your-ai-assistant-2-7-3-release/)：第三方索引显示 2026-10-30 的 2.7.3/code292，但该页面显示包名 `com.deepseek.chat.nov`，不能直接视为 `com.deepseek.chat` 的目标样本。

---

## 17. 最终工程决策

1. Lspds 采用 Kotlin-first、现代 libxposed API 102。
2. 以 `HostProfile + HostSymbolResolver + HookRegistry + Feature Installer` 为核心架构。
3. 国内与 Google Play 分别建 Profile，按 versionCode、签名、DEX 和结构锚点识别。
4. 先做加载诊断、宿主探测、设置入口和只读请求观察。
5. SSE parser 独立实现并充分单测。
6. 会话、附件、数据库和本地 API 均后置，逐项设置权限边界。
7. 未经 E2/E3 证据的符号不安装 Hook；未经 E4/E5 证据的功能不标记支持。
8. 任何功能都必须可单独关闭、可诊断、可回滚；未知版本默认不 Hook。

这套设计的目标不是“Hook 越多越好”，而是在官方客户端频繁更新、R8 混淆和多渠道分叉的情况下，保持每个 Hook 可证明、可隔离、可撤销。
