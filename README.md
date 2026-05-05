# HubReader

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android)](https://developer.android.com/)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue)](https://apilevels.com/)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-green)](https://developer.android.com/guide/topics/manifest/uses-sdk-element)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-latest-4285F4)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

一款现代化的 Android 应用，用于浏览和探索 GitHub 仓库，采用简洁的 Material Design 3 设计风格。

## ✨ 功能特性

- 🔍 **搜索** - 通过关键词搜索 GitHub 仓库，支持实时搜索建议
- 📈 **热门** - 发现热门仓库，支持按时间范围和编程语言筛选
- 📖 **探索** - 按语言和主题分类浏览热门仓库
- ⭐ **收藏** - 收藏你喜欢的仓库，方便快速访问
- 📄 **README 查看器** - 使用 Markdown 渲染查看仓库 README
- 🌙 **深色模式** - 完整的深色主题支持，可跟随系统切换
- 🎨 **动态颜色** - Material You 动态颜色支持（Android 12+）
- 🌐 **多语言** - 支持中文和英文本地化
- 📱 **现代 UI** - 基于 Jetpack Compose 和 Material Design 3 构建

## 📸 截图

| 搜索 | 热门 | 收藏 | 仓库详情 |
|------|------|------|----------|
| *在此添加截图* | *在此添加截图* | *在此添加截图* | *在此添加截图* |

## 🛠️ 技术栈

### 架构
- **MVVM**（Model-View-ViewModel）架构模式
- **Repository** 模式进行数据抽象
- **单 Activity** 架构配合 Jetpack Navigation

### UI
- **Jetpack Compose** - 声明式 UI 工具包
- **Material Design 3** - 现代设计系统
- **Coil** - 异步图片加载
- **WebView** - README Markdown 渲染

### 数据与网络
- **Retrofit** - 类型安全的 HTTP 客户端
- **OkHttp** - HTTP 客户端，带日志拦截器
- **Kotlinx Serialization** - JSON 解析
- **Room** - 本地数据库，用于收藏持久化
- **DataStore Preferences** - 设置存储

### 异步与响应式
- **Kotlin Coroutines** - 异步编程
- **Kotlin Flow** - 响应式流
- **StateFlow** - 状态管理

### 其他
- **Navigation Compose** - 应用内导航
- **ViewModel** - UI 相关数据生命周期管理
- **Adaptive Icons** - Android 自适应启动图标

## 📦 安装

### 前置要求
- Android Studio Hedgehog 或更高版本
- JDK 11 或更高版本
- Android SDK 36

### 构建与运行
```bash
# 克隆仓库
git clone https://github.com/你的用户名/HubReader.git
cd HubReader

# 在 Android Studio 中打开并运行，或使用命令行：
./gradlew assembleDebug

# 安装到连接的设备
./gradlew installDebug
```

## 🏗️ 项目结构

```
app/src/main/java/com/example/hubreader/
├── data/
│   ├── local/           # Room 数据库、DAO、实体、设置
│   ├── model/           # 数据类（GitHubRepo 等）
│   ├── remote/          # Retrofit 客户端、API 服务
│   └── repository/      # 仓库层
├── ui/
│   ├── components/      # 可复用的 Compose 组件
│   ├── navigation/      # 导航图和路由
│   ├── screens/         # 屏幕 Composable
│   └── theme/           # 主题、颜色、排版
├── viewmodel/           # ViewModel 和工厂类
└── MainActivity.kt      # 单 Activity 入口
```

## 🎯 核心功能详情

### 搜索
- 500ms 防抖的实时搜索
- 本地持久化的搜索历史
- 热门搜索推荐
- 按语言筛选

### 热门
- 时间范围筛选：今天、本周、本月
- 编程语言筛选
- 基于 GitHub 搜索 API

### 仓库详情
- 仓库统计（Stars、Forks、Watchers、Issues）
- 主题/标签展示
- 使用 GitHub Markdown CSS 完整渲染 README
- 主题自适应 WebView

### 收藏
- 使用 Room 持久化本地存储
- 实时收藏状态同步
- 底部导航栏快速访问

## 🔧 配置

### GitHub API 速率限制
- 已认证：5,000 次请求/小时
- 未认证：60 次请求/小时

要提高请求限制，请在 `RetrofitClient.kt` 中添加你的 GitHub Token：
```kotlin
.header("Authorization", "token 你的GITHUB_TOKEN")
```

## 📱 最低要求

- **Android**：7.0（API 24）或更高
- **目标版本**：Android 15（API 36）
- **架构**：arm64-v8a、armeabi-v7a、x86_64

## 🤝 贡献

欢迎贡献代码！请随时提交 Pull Request。

1. Fork 本仓库
2. 创建你的特性分支（`git checkout -b feature/amazing-feature`）
3. 提交你的更改（`git commit -m '添加某个很棒的特性'`）
4. 推送到分支（`git push origin feature/amazing-feature`）
5. 提交 Pull Request

## 📄 许可证

```
Copyright 2024 HubReader Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🙏 致谢

- [GitHub API](https://docs.github.com/en/rest) - 仓库数据
- [github-markdown-css](https://github.com/sindresorhus/github-markdown-css) - README 样式
- [Material Design 3](https://m3.material.io/) - 设计系统
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - UI 工具包

## 📬 联系方式

如果你有任何问题或建议，欢迎提交 Issue 或联系我。

---

⭐ 如果觉得这个项目有帮助，请给个 Star！
