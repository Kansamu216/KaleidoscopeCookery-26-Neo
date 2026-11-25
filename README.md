# Kaleidoscope Cookery 森罗物语：厨房 — Minecraft 26.x NeoForge 移植

> 面向 Minecraft 26.1.2 / 26.2 / 26.3 的 NeoForge 移植版，内容以官方 KaleidoscopeCookery `1.7.0` 为基准。

![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2%20%7C%2026.2%20%7C%2026.3-brightgreen)
![Loader](https://img.shields.io/badge/NeoForge-26.x-orange)
![License](https://img.shields.io/badge/License-BSD%20%7C%20CC%20BY--NC--SA%204.0-blue)

## 这是什么

森罗物语：厨房是一个围绕"做饭"的 Minecraft 模组：锅、汤锅、蒸笼、磨石、砧板、茶壶、油壶、厨房货架、
竹筛、挂幡、长凳、八仙桌、稻草人、茶与糖果串……从种植、备料、烹饪到摆盘上桌，一整套厨房流程都在里面。

本仓库把原版模组（停在 1.20.1）带到 Minecraft 26.x 的 NeoForge 平台，按 MC 版本拆成三个分支独立维护。

## 维护者与接手关系

### 官方 / 上游 — KaleidoscopeCookery `1.7.0`
- 项目归属：**KaleidoscopeMods**
- 作者与维护者：**药水棒冰（PotionPopsicle）** 及 KaleidoscopeMods 团队、所有历史贡献者
- 上游仓库：<https://github.com/KaleidoscopeMods/KaleidoscopeCookery>
- 本移植的内容基准：官方 1.20.1 分支的 `1.7.0`

### 本仓库 — KaleidoscopeCookery-26-Neo（Minecraft 26.x）
- 接手维护者：**Kansamu216**（本仓库所有者，负责 26.x 移植、发布与后续维护）
- 26.x 移植的基础：**BuaichiZY** 发起的 [`BuaichiZY/KaleidoscopeCookery-26_Neo`](https://github.com/BuaichiZY/KaleidoscopeCookery-26_Neo)（其 26.1.2 / 26.2 NeoForge 移植）
- 本仓库：<https://github.com/Kansamu216/KaleidoscopeCookery-26-Neo>

**接手关系**：模组由 KaleidoscopeMods / 药水棒冰创作并维护至 1.20.1 `1.7.0`；BuaichiZY 将 26.1.2 / 26.2
移植到 NeoForge，并建立 26_Neo 仓库；本仓库由 Kansamu216 接手，在此基础上补齐 26.3、修复兼容与渲染问题，
并维护、发布三个 26.x 分支。

**问题反馈**：
- 26.x 移植相关（构建失败、崩溃、模组兼容、数据迁移）→ 到**本仓库**提 issue，找 **Kansamu216**；
- 模组内容设计、配方平衡、1.20.1 上游行为 → 到**上游仓库**反馈，找 **KaleidoscopeMods**。

## 版本分支

| 分支 | Minecraft | NeoForge | mod_version |
|---|---|---|---|
| [`26.1.2`](../../tree/26.1.2) | 26.1.2 | 26.1.2.94+ | `1.7.0-release-neoforge+mc26.1.2` |
| [`26.2`](../../tree/26.2) | 26.2 | 26.2.0.67+ | `1.7.0-release-neoforge+mc26.2` |
| [`26.3`](../../tree/26.3) | 26.3 | 26.3.0.118+ | `1.7.0-release-neoforge+mc26.3` |

三个分支功能一致，各自独立编译与发布；请下载与你 Minecraft 版本一致的分支构建。

## 安装

1. 安装对应版本的 **NeoForge**（见上表）；
2. 安装 **Java 25+**（Minecraft 26.x 要求）；
3. 把 `kaleidoscope_cookery-1.7.0-release-neoforge+mc<版本>.jar` 放进 `mods/`；
4. 启动游戏。没有强制前置模组。

可选联动：**Farmer's Delight（26.x 第三方移植）**、**Carry On**、**Jade**、**REI / JEI**、**KubeJS**、
**Every Compat（26.1.2）**。未安装时模组本身照常工作。

## 从源码构建

```bash
git clone https://github.com/Kansamu216/KaleidoscopeCookery-26-Neo.git
cd KaleidoscopeCookery-26-Neo
git checkout 26.3          # 或 26.1.2 / 26.2
./gradlew build
```

构建产物位于 `build-local/libs/`。开发环境可用 `./gradlew runClient` / `./gradlew runServer`，
`./gradlew runGameTestServer` 运行内置的 13 项 GameTest。

## 移植范围

- 内容与官方 `1.7.0` 对齐：方块、物品、配方、成就、战利品表与村庄结构均已迁移；
- 村庄厨房改为运行时注入，不再覆盖原版结构池，避免与其他村庄模组冲突；
- 渲染层按 Minecraft 26 的新渲染管线重写（方块实体渲染器、HUD 覆盖层、粒子、物品模型）；
- 不移植需要 26.x 构建但上游不存在的功能：Create / EMI / Ponder / Tetra / Sable，以及官方茶幡的贴墙形态等；
- 详细的已知限制见各分支维护记录（`_ref170/notes/`，不随仓库发布）。

## 授权

沿用上游的双许可证：

- **源代码**：BSD 3-Clause（见 `LICENSE-CODE.txt`）
- **资源文件**：CC BY-NC-SA 4.0（见 `LICENSE-ASSETS.txt`）

## 致谢

- **KaleidoscopeMods / 药水棒冰（PotionPopsicle）**：原模组与 `1.7.0` 的全部设计与素材；
- **BuaichiZY**：26.1.2 / 26.2 的 NeoForge 移植基础与 26_Neo 仓库；
- **Dr-WeiAL** 及所有向原项目提交过代码、翻译与反馈的贡献者；
- **NeoForged / NeoForge** 团队与 **ModDevGradle**：26.x 开发工具链；
- Farmer's Delight、Carry On、Every Compat、Moonlight 等联动模组的作者；
- 以及所有提交 issue、参与测试的玩家。

*Thanks to all the developers and players who have contributed to this project!*
