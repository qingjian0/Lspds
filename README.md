# Lspds — DeepSeek Android LSPosed Enhancement

> 面向官方 DeepSeek Android 客户端（com.deepseek.chat）的独立 LSPosed 增强模块。

**项目状态：YunSeek 底包迁移与工程整合阶段。** 本仓库是唯一主仓库，集中保存全部代码、测试、构建配置、调研资料、文档和发布产物。

- 目标宿主：`com.deepseek.chat`
- 业务底包：YunSeek 1.8.1（`ZW-SYS/Deekseep-Custom`，GPL-3.0）
- 开发策略：YunSeek 主干 + 经审计的功能移植 + Kotlin 新增层
- 代码语言：新增代码优先 Kotlin；已验证的 YunSeek Java Hook 暂时保留，逐步迁移
- Lspds 模块包名：`org.qingjian.lspds`（不沿用 YunSeek 的 `com.dsmod.probe`）
- Hook API：现代 libxposed API 102（compileOnly）
- 兼容性原则：渠道、versionCode、签名和 DEX 指纹联合识别；未知版本 fail-closed
- 项目许可证：GPL-3.0-only

## 当前底包与宿主范围

YunSeek 1.8.1 README 声明支持国内 DeepSeek 2.7.1/code287、国内 2.7.2/code289、Google Play/国际 2.7.2/code290。Play 2.7.1/code288 不在该支持列表中，必须独立建立 Profile，不能套用国内 code287 的混淆符号。

**重要：确定 YunSeek 为底包不代表源码已经迁入或 APK 已构建。** 当前阶段需要固定上游 commit、盘点源码/依赖/许可，再分批迁移并通过 CI。

## 当前状态

| 领域 | 状态 |
|---|---|
| YunSeek 作为业务底包 | 已确定 |
| 上游来源与 GPL-3.0 | 已核验 |
| 迁移方案与功能融合矩阵 | 已写入文档 |
| YunSeek 源码完整迁入 | 未完成 |
| Gradle / libxposed API 102 整合 | 未验证 |
| 宿主 287/289/290 Profile 迁移 | 未完成 |
| Play 288 独立 Profile | 待重新核验 APKS 样本 |
| 请求/SSE/Agent/附件/会话 Hook | 待迁移和回归 |
| APK 和真实设备验证 | 尚未完成 |

不要把静态分析线索、代码提交或 CI 构建成功当作真实设备 Hook 成功。

## 规划与技术文档

- [YunSeek 底包迁移与功能融合方案](docs/YUNSEEK_BASELINE_MIGRATION.md)
- [项目总体规划与技术决策](docs/PROJECT_PLAN.md)
- [开源项目扩展调研记录](docs/RESEARCH_EXPANSION.md)
- [DeepSeek Hook 深度研究与详细设计](docs/DEEPSEEK_HOOK_DESIGN.md)
- [功能规格与验收标准](docs/FEATURE_SPEC.md)
- [宿主兼容性策略](docs/COMPATIBILITY_STRATEGY.md)
- [实施路线图](docs/IMPLEMENTATION_ROADMAP.md)
- [Hook 证据记录模板](docs/HOOK_EVIDENCE_TEMPLATE.md)
- [第三方许可证与署名清单](docs/THIRD_PARTY_NOTICES.md)
- [架构说明](docs/ARCHITECTURE.md)
- [参考项目审计](docs/REFERENCE_AUDIT.md)
- [DeepSeek 2.7.1 静态分析线索](docs/DEEPSEEK_271_STATIC_FINDINGS.md)
- [安全与隐私](docs/SECURITY.md)

## 构建

工程整合阶段构建基线：JDK 21、Gradle 9.4.1、AGP 9.2.1、Android SDK 36。最终版本以迁移后的 Gradle Wrapper 和 CI 实测为准。

Release APK 应使用用户自有签名密钥；keystore、密码和 Token 不得提交到仓库。

## 隐私与安全

- 未验证的宿主版本不安装业务 Hook。
- 默认不记录提示词、模型回答、账号标识、会话 ID、Cookie、Bearer Token、API Key、密码或 PoW 数据。
- 不绕过 DeepSeek 登录、PoW、限流、风控或服务端访问控制。
- Local API 默认只监听 loopback，并使用模块自有 API Key，不暴露宿主凭据。
- 数据库编辑和恢复必须用户主动触发、先备份、事务化并可回滚。
- Agent 默认半自动；发送、删除、写入等动作需用户确认。

## 免责声明

Lspds 是独立社区项目，与 DeepSeek、LSPosed 及参考项目作者无隶属关系。仅在你拥有或获授权的设备和数据上使用。
