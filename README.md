# Kaleidoscope Cookery Unofficial 26 · 森罗物语：厨房（非官方移植）

<p align="center">
  <img src="src/main/resources/kaleidoscope_cookery_cover.png" alt="Kaleidoscope Cookery" width="820">
</p>

<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/Minecraft-Java%20Edition-brightgreen" alt="Minecraft"></a>
  <a href="#"><img src="https://img.shields.io/badge/NeoForge-26.1.2.94%20%7C%2026.2.0.67%20%7C%2026.3.0.118-orange" alt="NeoForge"></a>
  <a href="#"><img src="https://img.shields.io/badge/License-BSD--3--Clause%20%2B%20CC%20BY--NC--SA%204.0-blue" alt="License"></a>
  <a href="#"><img src="https://img.shields.io/badge/Status-Release%20Ready-success" alt="Status"></a>
</p>

> **本项目站在 [BuaichiZY](https://github.com/BuaichiZY) 的肩膀上。**
>
> 本仓库的 26.1.2 / 26.2 版本并非从零开始——它直接继承了 BuaichiZY 在 [KaleidoscopeCookery-26_Neo](https://github.com/BuaichiZY/KaleidoscopeCookery-26_Neo) 上完成的大量前期移植工作：NeoForge 26.1.2 的首次发布、26.2 的完整移植、KubeJS 8 支持、Java 工具链与 GitHub Actions 的修复、专用服务器类加载问题的解决，以及可移植的构建配置。没有这些铺垫，就没有本项目的今天。
>
> 我在其基础上继续推进：新增 26.3 版本、补齐 REI 集成与厨师交易/村庄结构、移植官方后续内容（Kitchen Shelf、skewer 家族、糖浆配方），并修复了若干数据与构建缺陷（详见下文「分工与传承」）。
>
> 本构建在 AI 辅助下完成。它实现了原版 Kaleidoscope Cookery 的绝大部分功能，但仍可能存在少量 bug。发现问题欢迎提交 issue，我会尽力修复。

> 以下介绍部分沿用官方原始文档。

---

## 📖 简介

**Kaleidoscope Cookery（森罗物语：厨房）** 是一个为 Minecraft Java Edition 开发的模组，致力于带来丰富的烹饪体验与全新的游戏内容。

本仓库是它的**非官方 NeoForge 26.x 移植版**，目前同时维护 **26.1.2 / 26.2 / 26.3** 三个 Minecraft 版本，每个版本对应一条独立分支。

---

## ✨ 功能特性

### 🍳 完整的烹饪体系
- 全新的烹饪系统与丰富的食物配方
- 煎、炒、烹、炸、蒸、烤多线并行

### 🔥 创新烹饪设备

| 设备 | 用途 |
|---|---|
| 灶台（Stove） | 基础热源与烹饪工作站 |
| 炒锅（Pot） | 炒制类料理 |
| 汤锅（Stockpot） | 炖煮、熬汤、糖浆 |
| 蒸笼（Steamer） | 蒸制类料理 |
| 茶壶（Teapot） | 冲泡茶饮 |
| 石磨（Millstone） | 研磨原料 |
| 砧板（Chopping Board） | 切配食材 |
| 烤肉架（Shawarma Spit） | 旋转烤肉 |
| 油壶（Oil Pot） | 储油与浇油 |
| 橱柜（Kitchen Shelf） | 厨具与食材收纳，支持红石自动补充 |

### 🥘 丰富的内容规模
- **569** 个模组配方
- **69** 个方块 / **147** 个物品
- 完整进度树（Advancement）
- 厨师村民职业与村庄厨房结构
- 果串（Skewer）家族：浆果、苹果、毛毛虫及裹糖变体

### 🎮 沉浸式体验
- 手持物品动态模型（油壶、面团、锅盖、菜谱等）
- 烹饪进度 HUD、锅具动画、汤底渲染
- 可选联动：**JEI** / **Jade** / **REI**（均为可选，不装也能正常游玩）

---

## 🖼️ 展示

![img](https://cdn.modrinth.com/data/v17FatAc/images/01385799f37a2874ffd44638c52c9a1b3faa0347.png)
![img](https://cdn.modrinth.com/data/v17FatAc/images/0d4c8bdb51059683adecab83ed426faa13deae5f.png)
![img](https://cdn.modrinth.com/data/v17FatAc/images/1725db57e319df4a57d2e0bde60a59fafd2e2e88.png)
![img](https://cdn.modrinth.com/data/v17FatAc/images/2b2dfb0e73b58e9f038ebb48364421ae323b9d5f.png)
![img](https://cdn.modrinth.com/data/v17FatAc/images/4bc9e2bb5f4f2a8081a4b1875187eedfb7b212df.png)

---

## 🤝 分工与传承

本项目是**接力开发**的成果，明确区分两段工作：

### 基座：BuaichiZY 的前期移植（本仓库 26.1.2 / 26.2 的起点）

以下工作**全部由 [BuaichiZY](https://github.com/BuaichiZY) 完成**，本项目直接在其成果上继续：

| 工作 | 说明 |
|---|---|
| **NeoForge 26.1.2 首次移植** | 完成从 Forge 1.20.1 到 NeoForge 26.1.2 的架构迁移，建立可构建的 26.1.2 分支 |
| **26.2 完整移植** | 将 26.1.2 的全部内容迁移到 Minecraft 26.2 |
| **KubeJS 8 支持** | 接入 KubeJS 8 与 Rhino，保留可选联动 |
| **Java 工具链配置** | 使 Java toolchain 配置可移植，修复跨环境构建问题 |
| **GitHub Actions 修复** | 修复 CI 的 Java toolchain 配置，使构建可持续集成 |
| **专用服务器类加载修复** | 解决 dedicated server 的 classloading 问题并恢复品牌信息 |
| **仓库基础设施** | README、`.gitattributes`、`.gitignore`、构建脚本等基础配置 |

> 本仓库 `26.1.2` 分支包含其全部 24 个历史提交，`26.2` 分支包含其 13 个历史提交——**完整保留了原作者署名与提交历史**。

### 接力：本移植版新增的工作

| 工作 | 说明 |
|---|---|
| **Minecraft 26.3 从零移植** | 新增完整 26.3 分支（API 全量适配、数据格式迁移） |
| **官方后续内容移植** | Kitchen Shelf（橱柜）、skewer 家族（9 物品 + 8 方块）、45 个汤锅糖浆配方 |
| **REI 集成** | 解除 exclude 并按 26.x API 适配，解锁 8/10 类别 |
| **厨师交易与村庄结构** | 26.x `TradeSet` 数据包 + 村庄厨房 template_pool 注入 |
| **数据/构建缺陷修复** | advancement、loot table、`build-local` 误跟踪、mixin refmap 等 |
| **测试体系** | 13 个 GameTest（配方解析、配方计数、厨师交易验证等） |

### 关于署名

- 本仓库所有分支均**保留 BuaichiZY 的原始提交**，Git 历史上可完整追溯其贡献。
- 本项目为**非官方移植**，与原作团队及 BuaichiZY 均无隶属关系。
- 如 BuaichiZY 认为署名方式需要调整，欢迎随时联系我修改。

---

## 📦 版本与分支

| 分支 | Minecraft | NeoForge | `mod_version` |
|---|---|---|---|
| [`26.1.2`](../../tree/26.1.2) | 26.1.2 | 26.1.2.94+ | `1.7.0-release-neoforge+mc26.1.2` |
| [`26.2`](../../tree/26.2) | 26.2 | 26.2.0.67+ | `1.7.0-release-neoforge+mc26.2` |
| [`26.3`](../../tree/26.3) | 26.3 | 26.3.0.118+ | `1.7.0-release-neoforge+mc26.3` |

> 每个版本分支独立维护，请下载与你的 Minecraft 版本对应的分支构建。

---

## 🔧 安装

1. 安装对应版本的 **NeoForge**（见上表）
2. 安装 **Java 25+**
3. 将构建出的 jar 放入 `.minecraft/mods/`
4. 启动游戏

**前置模组**：无强制前置。JEI / Jade / REI 为可选联动。

---

## 🛠️ 从源码构建

```bash
git clone https://github.com/Kansamu216/KaleidoscopeCookery-26-Neo.git
cd KaleidoscopeCookery-26-Neo
git checkout 26.3          # 或 26.2 / 26.1.2

./gradlew build
```

构建产物位于 **`build-local/libs/`**（注意：本项目的 `buildDirectory` 被设为 `build-local`，不是默认的 `build/`）。

### 环境要求

| 项 | 版本 |
|---|---|
| JDK（编译目标） | 25 |
| JDK（NeoForm Runtime） | 26 |
| Gradle | 9.2.1（通过 wrapper） |
| ModDevGradle | 2.0.143 ~ 2.0.148（按分支） |

> 需要同时提供 JDK 25 与 JDK 26，并通过 `-Dorg.gradle.java.installations.paths=<jdk25>,<jdk26>` 显式登记，否则 NeoForm 任务会失败。

---

## ✅ 验证状态

三个版本均通过以下验证：

| 项 | 26.1.2 | 26.2 | 26.3 |
|---|---|---|---|
| `./gradlew clean build` | ✅ | ✅ | ✅ |
| GameTest | ✅ 13/13 | ✅ 13/13 | ✅ 13/13 |
| 模组配方数 | 569 | 569 | 569 |
| 数据解析错误 | 0 | 0 | 0 |
| loot `Unreachable entry` | 0 | 0 | 0 |
| GitHub Actions | ✅ | ✅ | ✅ |

---

## ⚠️ 已知限制

| 项 | 说明 | 影响 |
|---|---|---|
| `datagen/**` | NeoForge 26.x 删除了旧 datagen API，待从零重写 | 仅影响开发期，不影响游玩 |
| `compat/{emi,create,farmersdelight,ponder,harvest}` | 上游均无 26.x 构建 | 装对应 mod 时无联动 |
| `compat/rei` 的磨石/汤锅分类 | 依赖 Create / Farmer's Delight | REI 中少 2 个配方分类 |
| 生物掉落表（猪/猪灵等） | loot table 格式迁移待完成 | 相关掉落暂缺 |
| 旧 `client/render`、`client/init` | 已被 `client/runtime` 适配层取代 | 无 |

> 详细说明见 [Issue #2](https://github.com/BuaichiZY/KaleidoscopeCookery-26_Neo/issues/2)。

---

## 📄 许可证

本项目采用双许可协议：

- **源代码**：[BSD 3-Clause License](LICENSE-CODE.txt)
- **资源文件**：[CC BY-NC-SA 4.0](LICENSE-ASSETS.txt)

---

## 💬 反馈与建议

- 🐛 [Issues](../../issues) — 提交 bug 报告
- 💡 [Issues](../../issues) — 功能建议与讨论

---

## 👥 开发团队与致谢

| 角色 | 贡献者 |
|---|---|
| **原作** | **Kaleidoscope Official Production Team**（[KaleidoscopeMods/KaleidoscopeCookery](https://github.com/KaleidoscopeMods/KaleidoscopeCookery)） |
| **26.1.2 / 26.2 前期移植（基座）** | **[BuaichiZY](https://github.com/BuaichiZY)**（[KaleidoscopeCookery-26_Neo](https://github.com/BuaichiZY/KaleidoscopeCookery-26_Neo)） |
| **26.3 移植与后续扩展** | Kansamu216（非官方） |

> 特别感谢 **BuaichiZY** 打下的坚实基础——本项目的每一条分支都建立在其提交历史之上。

---

*感谢所有为本项目做出贡献的开发者和玩家！*
