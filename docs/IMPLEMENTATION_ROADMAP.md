# 实施路线图与阶段验收

## Stage 0 — 规划基线（当前阶段）

- [x] 明确唯一主仓库和 Kotlin 优先原则
- [x] 初步核验核心参考仓库、fork 关系和许可证元数据
- [x] 区分国内版 2.7.1 code 287 与 Play 版 2.7.1 code 288
- [x] 建立架构、功能规格、兼容策略和研究扩展文档
- [ ] 逐项复核关键文件和许可证文本
- [ ] 完成宿主 APKS/split/DEX 样本复核

验收：文档中的事实、静态线索、待验证假设分栏；无“已实现”误报。

## Stage 1 — 工程稳定

- Kotlin source set 与 AGP 9 内置 Kotlin 支持验证
- libxposed API 102 entry 和 ProGuard/R8 keep rules
- Gradle Wrapper 固定版本并提交 wrapper JAR
- CI 安装 SDK、运行单测、assembleDebug、上传 APK
- 检查 APK 的包名、入口 metadata、scope 和签名
- 加入 CI 构建产物 SHA-256

验收：CI 全绿；下载 APK 可安装；LSPosed 能识别模块。此阶段不安装宿主业务 Hook。

## Stage 2 — 宿主样本和版本映射

- 复核 Play 2.7.1 code 288 的 APKS 与 splits
- 获取/复核国内 2.7.1 code 287 和 2.7.2 样本
- 构建 DEX hash、签名证书和结构指纹
- 对照 Deekseep/YunSeek 公开符号表
- 完成请求构造、SSE、历史同步和数据库调用链图
- 记录每个符号的证据等级

验收：每个 profile 都有独立 evidence.md；没有跨渠道映射猜测。

## Stage 3 — Hook runtime foundation

- HookInstaller interface / registry
- 精确反射签名解析和能力探测
- HookHandle 统一登记、关闭和替换
- 独立失败状态与安全模式
- 诊断页面显示每个 Installer 的状态

验收：人为制造一个 Hook 解析失败，其他 Installer 仍正常；禁用 Hook 后宿主可正常运行。

## Stage 4 — 低风险用户功能

- 模块本地提示词库
- 会话置顶（先使用模块侧关联 ID）
- 用户主动触发的 Markdown/TXT 导出
- 模块设置、主题、字体和诊断导出

验收：不修改宿主数据库、不读取凭据；数据迁移测试通过。

## Stage 5 — 网络观察与 SSE

- 确认各版本实际 transport stack
- 先仅观察元数据
- 实现标准 SSE parser 单测
- 处理 CRLF、跨 chunk UTF-8、multi-line data、event/id/retry、[DONE]、取消、断连、异常
- 正文诊断必须单独 opt-in、明确风险、默认关闭且本地短期保存

验收：网络观察不改变请求；关闭功能后不再收集；不记录凭据和正文。

## Stage 6 — 对话数据能力

- 先只读 schema inspector
- 备份 DB + WAL/SHM + 附件引用
- 只读导出与索引
- 事务化编辑与恢复
- 备份兼容和 schema migration

验收：恢复到测试副本后完整性检查通过；故障注入可回滚；真实设备验证后才标记可用。

## Stage 7 — Local API

- 设计 OpenAI Chat Completions 子集
- 只绑定 loopback
- 模块自有 API Key
- 上游调用只复用用户正常客户端已授权能力，不暴露/导出宿主 token
- 请求队列、并发限制、取消和超时
- SSE stream 转换、背压、断连清理
- 模型信息与能力声明准确

验收：curl / Python SDK 兼容测试；无远程监听；认证材料不出模块进程。

## Stage 8 — 高级功能与发布

- 多配置档案
- 应用锁与防窥
- 自动备份策略
- 兼容性诊断与更新提示（无强制联网验证）
- Release APK、校验和、签名说明、回滚说明
- 每个支持版本独立回归

验收：发布说明有精确支持矩阵、测试设备、LSPosed 版本和已知限制。
