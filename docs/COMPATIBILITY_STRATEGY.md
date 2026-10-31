# 宿主兼容性、版本映射与恢复策略

## 1. 版本身份不能只看 versionName

DeepSeek Android 的国内版与 Google Play/国际版可能在同一版本号下拥有不同的 versionCode、依赖、保护壳、资源和 R8 混淆结果。YunSeek 1.8.1 的公开说明称其 2.7.1 国内版 code 287、2.7.2 国内版 code 289、Google Play 2.7.2 code 290 使用独立宿主识别和映射。这说明同一个 2.7.x 不能视为同一符号空间。

本项目先前材料中的 Play 2.7.1 code 288 与国内 2.7.1 code 287 必须分开建档。

## 2. HostProfile 结构

每个宿主 Profile 至少记录：

- packageName
- versionName / longVersionCode
- channel（mainland / googlePlay / unknown）
- installer package（仅作辅助证据）
- signing certificate SHA-256 digest
- base APK 与 split APK 文件名、长度和 SHA-256
- 每个 classes*.dex 的 SHA-256
- DEX 数量和 class 数量
- 关键类/方法/字段结构签名
- 资源 ID / 资源名锚点（如适用）
- 分析工具版本、分析日期、来源
- 静态验证状态和设备验证状态

哈希只用于样本完整性和识别，不用于用户身份追踪。

## 3. 映射目录建议

host-maps/
  manifest.json
  mainland/
    2.7.1-287/
      profile.json
      symbols.json
      evidence.md
    2.7.2-289/
      profile.json
      symbols.json
      evidence.md
  google-play/
    2.7.1-288/
      profile.json
      symbols.json
      evidence.md
    2.7.2-290/
      profile.json
      symbols.json
      evidence.md

APK、APKS、DEX、反编译完整输出和数据库文件不放进公开仓库。公开仓库只存放最小必要的描述符、哈希、分析脚本和证据摘要。

## 4. 匹配算法

1. packageName 必须完全相等。
2. versionCode 与 versionName 用于候选 profile 初筛。
3. 渠道证据必须达到最小可信度；无法判定时标为 unknown。
4. 至少校验 base APK 与关键 DEX 指纹；split 组合不同则不能直接匹配。
5. 精确符号解析：类名、方法名、参数类型、返回类型、静态/实例修饰符、字段类型。
6. 运行时再次验证 Hook 目标的 declaring class、参数数量/类型、返回类型和必要字段。
7. 任一关键校验失败则对应功能禁用；不回退到旧版混淆映射。

## 5. 多级兼容状态

- UNRECOGNIZED：包名或样本不匹配；只显示诊断信息。
- IDENTIFIED_STATIC：静态样本与 profile 匹配；不代表设备 Hook 已通过。
- RUNTIME_PROBED：设备运行时找到预期类/方法；尚未启用修改性行为。
- HOOK_VALIDATED：明确 Hook 点完成运行时测试和稳定性回归。
- PARTIAL：部分 Hook 可用，其他功能单独禁用。
- BLOCKED：崩溃、结构不符或数据风险触发自动禁用。

## 6. 版本适配更新流程

1. 收集用户提供或官方渠道取得的 APK/APKS 样本。
2. 核实签名、渠道、versionCode 和 split 清单。
3. 离线提取 DEX 哈希和结构信息。
4. 对比旧版类、方法、调用链、序列化字段、资源和网络层。
5. 生成候选映射，不直接发布。
6. 静态审查、单元测试、模拟器/真机验证。
7. 以独立 Feature Flag 发布，保留回滚开关。
8. 在兼容矩阵标明已测设备、系统版本、LSPosed 版本和结果。

## 7. 故障恢复

- 每个 Hook 安装器使用独立异常边界和状态对象。
- Hook 安装失败不阻止其余模块启动。
- 连续启动崩溃时自动进入安全模式（关闭全部可选 Hook，仅保留诊断）。
- 数据写入前创建可校验备份；恢复失败原子回滚。
- 用户可从模块 APK 设置或 LSPosed 中关闭模块。
- 不做隐藏自身、持久化对抗卸载或阻止用户停用的行为。
