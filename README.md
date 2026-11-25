# 森罗厨房 · Kaleidoscope Cookery（Minecraft 26.x NeoForge 移植）

> 本项目是 [KaleidoscopeMods/KaleidoscopeCookery](https://github.com/KaleidoscopeMods/KaleidoscopeCookery)
> 的 **Minecraft 26.1.2 / 26.2 / 26.3 NeoForge 移植版**，内容以官方 1.7.0（1.20.1 分支）为基准。

## 这是什么

森罗厨房是一个围绕"做饭"展开的 Minecraft 模组：锅、汤锅、蒸笼、磨石、砧板、茶壶、油壶、
厨房货架、竹筛、挂幡、长凳、八仙桌、稻草人、摇签、茶与糖果串……从种植、备料、烹饪到摆盘上桌，
一整套厨房流程都在里面。

原版停留在 1.20.1，本项目把它带到了 Minecraft 26.x 的 NeoForge 平台，并按版本拆成三个分支独立维护。

## 版本分支

| 分支 | Minecraft | NeoForge | mod_version |
|---|---|---|---|
| [`26.1.2`](../../tree/26.1.2) | 26.1.2 | 26.1.2.94+ | `1.7.0-release-neoforge+mc26.1.2` |
| [`26.2`](../../tree/26.2) | 26.2 | 26.2.0.67+ | `1.7.0-release-neoforge+mc26.2` |
| [`26.3`](../../tree/26.3) | 26.3 | 26.3.0.118+ | `1.7.0-release-neoforge+mc26.3` |

请下载与你的 Minecraft 版本一致的分支构建。三个分支功能一致，各自独立编译、独立发布。

## 安装

1. 安装对应版本的 **NeoForge**（见上表）；
2. 安装 **Java 25+**（26.x 要求）；
3. 把 `build-local/libs/kaleidoscope_cookery-1.7.0-release-neoforge+mc<版本>.jar` 放进 `mods/`；
4. 启动游戏。没有强制前置模组。

可选联动：**Farmer's Delight（26.x 第三方移植）**、**Carry On**、**Jade**、**REI / JEI**、
**KubeJS**、**Every Compat（26.1.2）**。未安装时模组本身照常工作。

## 从源码构建

```bash
git clone https://github.com/Kansamu216/KaleidoscopeCookery-26-Neo.git
cd KaleidoscopeCookery-26-Neo
git checkout 26.3          # 或 26.1.2 / 26.2
./gradlew build
```

构建产物在 `build-local/libs/`。运行 `./gradlew runServer` / `./gradlew runClient` 可启动测试环境，
`./gradlew runGameTestServer` 运行内置的 13 项 GameTest。

## 移植说明

- 内容与官方 1.7.0 对齐：配方、方块、物品、成就、战利品与村庄结构均已迁移；
- 村庄厨房改为运行时注入，不再覆盖原版结构池（不会和其他村庄模组打架）；
- 渲染层按 Minecraft 26 的新渲染管线重写（方块实体渲染器、HUD 覆盖层、粒子）；
- 少数需要第三方 26.x 构建的功能不移植：Create / EMI / Ponder / Tetra / Sable，
  以及官方茶幡的贴墙形态等，详见各分支 `_ref170/notes/`（仓库外维护）。

## 致谢

- **KaleidoscopeMods / BuaichiZY**：原模组与 1.7.0 版本的全部设计与素材；
- **Dr-WeiAL** 及所有向原仓库提交过代码、翻译、反馈的贡献者；
- **NeoForge / NeoForged** 团队与 **ModDevGradle**：26.x 开发工具链；
- Farmer's Delight、Carry On、Every Compat、Moonlight 等联动模组的作者；
- 以及所有提交 issue、帮忙测试的玩家。

素材与代码版权遵循原仓库的授权（见 `LICENSE-CODE.txt` / `LICENSE-ASSETS.txt`）。
