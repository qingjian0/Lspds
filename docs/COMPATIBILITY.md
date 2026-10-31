# 当前兼容性状态

## 版本身份

| 渠道 | DeepSeek 版本 | versionCode | 当前证据 | 当前 Hook 状态 |
|---|---:|---:|---|---|
| 国内版 | 2.7.1 | 287 | YunSeek README / HostV271 源码声明 | 未在本项目验证 |
| Google Play / 国际版 | 2.7.1 | 288 | 此前上传的 APKS 静态分析资料 | 未在设备验证 |
| 国内版 | 2.7.2 | 289 | YunSeek README 声明 | 未在真机全面验证 |
| Google Play / 国际版 | 2.7.2 | 290 | YunSeek README 声明 | 未在真机全面验证 |

版本号和 versionCode 只用于候选识别，不能作为 Hook 兼容性证明。国内版 2.7.1 code 287 与 Play 版 2.7.1 code 288 必须分开处理。

## 兼容状态定义

- UNRECOGNIZED：只显示诊断信息，不安装业务 Hook。
- IDENTIFIED_STATIC：静态样本匹配，不代表运行时 Hook 通过。
- RUNTIME_PROBED：运行时结构探测通过，仍不代表修改性行为安全。
- HOOK_VALIDATED：指定 Hook 已完成设备测试和稳定性回归。
- PARTIAL：部分功能可用，其他功能单独关闭。
- BLOCKED：结构不符、崩溃或数据风险触发禁用。

## 规则

- 不允许未知版本回退到旧版混淆映射。
- 每个渠道/版本建立独立 profile 和 DEX 指纹。
- 每个 Hook 必须记录证据等级和测试设备。
- 未提供真机日志时，不宣称运行时验证通过。

完整算法见 COMPATIBILITY_STRATEGY.md。
