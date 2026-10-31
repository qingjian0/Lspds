# Lspds — DeepSeek Android LSPosed Enhancement

> 面向官方 DeepSeek Android 客户端（com.deepseek.chat）的独立 LSPosed 增强模块。

**项目状态：规划与工程基线阶段（v0.1.0-dev）。** 本仓库是唯一主仓库，集中保存全部代码、测试、构建配置、调研资料、文档和发布产物。

- 目标宿主：com.deepseek.chat
- 主语言：Kotlin；Java 仅在必要的兼容/移植场景保留
- Hook API：现代 libxposed API 102（compileOnly）
- 当前优先宿主：Google Play DeepSeek 2.7.1（此前样本记录 versionCode 288，待重验）
- 兼容性原则：渠道、versionCode、签名和 DEX 指纹联合识别；未知版本 fail-closed
- 项目许可证：GPL-3.0-only

## 当前状态

| 领域 | 状态 |
|---|---|
| libxposed 入口和模块元数据 | 已建立初始骨架，待 CI 验证 |
| Kotlin 主代码迁移 | 初始入口、宿主识别、Activity 和测试已迁移 |
| 宿主识别 | 仅有初始版本号 gate，尚未完成渠道/DEX 指纹 |
| DeepSeek 请求与 SSE Hook | 未实现；只有静态候选线索 |
| 对话管理、备份、提示词 | 未实现 |
| OpenAI 兼容本地 API | 未实现 |
| APK 和真实设备验证 | 尚未完成 |

不要把静态分析线索、代码提交或 CI 构建成功当作真实设备 Hook 成功。

## 规划与技术文档

- [项目总体规划与技术决策](docs/PROJECT_PLAN.md)
- [开源项目扩展调研记录](docs/RESEARCH_EXPANSION.md)
- [功能规格与验收标准](docs/FEATURE_SPEC.md)
- [宿主兼容性策略](docs/COMPATIBILITY_STRATEGY.md)
- [实施路线图](docs/IMPLEMENTATION_ROADMAP.md)
- [架构说明](docs/ARCHITECTURE.md)
- [参考项目审计](docs/REFERENCE_AUDIT.md)
- [DeepSeek 2.7.1 静态分析线索](docs/DEEPSEEK_271_STATIC_FINDINGS.md)
- [安全与隐私](docs/SECURITY.md)

## 构建

当前构建基线：JDK 21、Gradle 9.4.1、AGP 9.2.1、Android SDK 37。Kotlin 代码以 JDK 17 字节码目标为当前设置，需由 CI 最终验证。

构建命令：gradle :app:testDebugUnitTest :app:assembleDebug

Release APK 应使用用户自有签名密钥；keystore、密码和 Token 不得提交到仓库。

## 隐私与安全

- 初始版本保持 observation-only，不安装未经验证的宿主 Hook。
- 默认不记录提示词、模型回答、账号标识、会话 ID、Cookie、Bearer Token、API Key、密码或 PoW 数据。
- 不绕过 DeepSeek 登录、PoW、限流、风控或服务端访问控制。
- 未来本地 API 默认只监听 loopback，并使用模块自有 API Key，不暴露宿主凭据。
- 数据库编辑和恢复必须用户主动触发、先备份、事务化并可回滚。

## 免责声明

Lspds 是独立社区项目，与 DeepSeek、LSPosed 及参考项目作者无隶属关系。仅在你拥有或获授权的设备和数据上使用。
