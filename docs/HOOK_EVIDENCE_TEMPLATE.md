# Hook 证据记录模板

每一个准备启用的宿主 Hook 都必须复制本模板建立独立证据文件。没有填写完整的 Hook 不得进入稳定构建。

## 标识

- Hook ID：
- 功能模块：
- 目标渠道：
- DeepSeek versionName：
- versionCode：
- Profile ID：
- 证据等级：STATIC / RUNTIME_PROBED / HOOK_VALIDATED
- 记录日期：
- 参考 APK 样本来源：

## 样本完整性

- APK split 清单：
- 签名证书摘要：
- base APK SHA-256：
- DEX 文件列表：
- 相关 DEX SHA-256：
- 分析工具及版本：

## 目标符号

- Class descriptor：
- Method descriptor：
- 参数类型：
- 返回类型：
- 修饰符：
- 相关字段及类型：
- ClassLoader：
- 关键调用者：
- 关键被调用者：

## 证据链

1. 用户操作入口：
2. UI / ViewModel 入口：
3. 业务层调用：
4. 请求构造 / 数据层：
5. 目标方法：
6. 返回值 / 回调 / SSE：
7. UI 更新或数据库持久化：

## Hook 安装前校验

- [ ] 精确类描述符匹配
- [ ] 精确方法名匹配
- [ ] 参数数量和类型匹配
- [ ] 返回类型匹配
- [ ] static / instance 匹配
- [ ] 字段结构匹配
- [ ] 目标进程匹配
- [ ] 目标渠道与 DEX 指纹匹配

## 运行时验证

- Android 版本：
- 设备型号：
- LSPosed 版本：
- 模块构建版本：
- DeepSeek 版本/渠道：
- 安装结果：
- 冷启动结果：
- 热启动结果：
- 功能行为：
- 崩溃/ANR：
- 资源/内存影响：
- 回滚测试：

## 隐私检查

- [ ] 不记录 Authorization / Cookie / Token
- [ ] 不记录密码、账号标识、PoW 数据
- [ ] 默认不记录请求/响应正文
- [ ] 诊断数据本地保存且可清除
- [ ] 不改变认证、PoW、限流和服务端授权

## 结论

- 状态：
- 已知限制：
- 关闭/回滚方式：
- 复核人：
