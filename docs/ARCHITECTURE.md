# 架构说明

## 当前阶段

当前仅建立 Android 模块 APK、现代 libxposed API 102 生命周期入口、模块元数据和初始宿主识别。当前代码不 Hook DeepSeek 聊天方法、不读取账号凭据、不采集消息正文、不修改网络请求、不访问或修改宿主数据库。

## 目标分层

1. Framework：libxposed lifecycle、框架能力探测、Hook Registry、HookHandle 管理。
2. Host：包名/渠道识别、版本 Profile、APK/split/DEX 指纹、符号解析。
3. Hooks：独立 HookInstaller、精确描述符校验、失败隔离和回滚。
4. Features：diagnostics、conversation、backup、prompt、local_api、appearance、privacy。
5. Data：模块自身 Room 数据库、设置、备份格式和迁移。
6. UI：Jetpack Compose + Material 3 模块设置界面。
7. Security：日志脱敏、本地 API 认证、速率限制和敏感数据生命周期管理。

详细技术决策见 PROJECT_PLAN.md；功能边界见 FEATURE_SPEC.md。

## Hook 安装策略

- 每个 Hook 都有唯一 Installer ID、目标宿主 Profile 和证据记录。
- 安装前校验类、方法、参数、返回类型、修饰符和必要字段。
- 安装成功后保存 HookHandle，支持禁用、移除或替换。
- 每个 Installer 独立异常边界；失败不阻断其他功能。
- 未知宿主或指纹不匹配时不安装业务 Hook。
- Hook 回调内不执行磁盘 I/O、网络请求或长时间数据库操作。

## 数据边界

模块自己的设置、提示词、置顶信息和索引存放在模块私有存储。宿主数据库先只读，只有 schema、事务、WAL 和恢复流程经过测试后才考虑写入。备份数据不上传、不提交到 GitHub。

## 本地 API 边界

规划中的 API 只绑定 127.0.0.1 / ::1，使用模块自有 API Key，明确支持的 OpenAI 兼容字段，不提供宿主凭据导出，也不绕过认证、PoW、限流或服务端授权。
