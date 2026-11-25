# KaleidoscopeCookery 26.x NeoForge 移植 — 交付说明

> 面向原维护者（BuaichiZY / KaleidoscopeMods）的提交说明。
> 生成时间：2026-11-25（Asia/Shanghai）

## 一、概述

本仓库基于 `BuaichiZY/KaleidoscopeCookery-26_Neo` 的 26.1.2 / 26.2 分支，完成 Minecraft **26.1.2 / 26.2 / 26.3** 三个版本的 NeoForge 移植与修复。每一条版本分支对应一个 MC 版本：

| 分支 | MC / NeoForge | mod_version |
|---|---|---|
| `26.1.2` | MC 26.1.2 / NeoForge 26.1.2.94 | `1.7.0-release-neoforge+mc26.1.2` |
| `26.2` | MC 26.2 / NeoForge 26.2.0.67 | `1.7.0-release-neoforge+mc26.2` |
| `26.3` | MC 26.3 / NeoForge 26.3.0.118 | `1.7.0-release-neoforge+mc26.3` |

远端：`https://github.com/Kansamu216/KaleidoscopeCookery-26-Neo`

## 二、本次移植的主要工作

### 26.3（从零移植）
- NeoForge 26.3 API 全面适配（`transfer` API 取代 `items`/`fluids.capability`、方块 codec、工具物品基类移除等）
- 数据格式迁移：advancement/loot_table/recipe（26.3 新格式）、`build-local` 构建目录、mixin refmap
- 移植官方功能：Kitchen Shelf、Chorus Fruit Skewer、完整 skewer 家族（glow_berry/sweet_berry/apple/caterpillar 及 candied 变体）、45 个汤锅糖浆配方
- 启用 REI 集成（26.x API 适配）
- 厨师村民交易 + 村庄厨房结构（datapack `TradeSet`/`template_pool`）
- 新增 12+ GameTest（配方解析、配方计数、厨师交易验证等）

### 26.2 / 26.1.2（同步）
- 同步上述 skewer/kitchen shelf/糖浆配方/REI/厨师交易等特性
- 修复 `mixins.json` 过时 refmap 引用
- 修复 `villager_trade` 数据加载顺序限制：使用运行时 loot function `RecipeFromIdFunction` 在交易生成时解析配方数据（替代 26.3 的 data-component 方案）

## 三、验收结果

三版本均通过以下验证（构建日志见 `docs/audit/` 附件或本地复现）：

| 项 | 26.1.2 | 26.2 | 26.3 |
|---|---|---|---|
| `./gradlew build` | ✅ SUCCESS | ✅ SUCCESS | ✅ SUCCESS |
| GameTest | ✅ 13/13 | ✅ 13/13 | ✅ 13/13 |
| 模组配方数 | ✅ 569 | ✅ 569 | ✅ 569 |
| loot `Unreachable entry` | ✅ 0 | ✅ 0 | ✅ 0 |
| 数据解析错误 | ✅ 0 | ✅ 0 | ✅ 0 |
| CI（GitHub Actions） | ✅ success | ✅ success | ✅ success |

## 四、已知限制（有意保留的排除项）

以下内容因上游生态或架构限制未迁移，均为**非阻塞**、不影响基础游玩：

| 项 | 原因 | 影响 |
|---|---|---|
| `client/render`、`client/init` 等旧渲染层 | 26.x 重写渲染管线，已被 `client/runtime` 适配层取代 | 无（runtime 层已注册 19 个渲染器并验证生效） |
| `datagen/**` | 26.x 删除全部旧 datagen API，需从零重写 | 无（仅影响开发期数据生成） |
| `compat/{emi,create,farmersdelight,ponder,harvest}` | 上游均无 26.x NeoForge 构建 | 装上对应 mod 时无联动 |
| `compat/rei` 的 Millstone/Stockpot 类别 | 依赖 Create/Farmer's Delight | REI 少 2 个配方分类 |
| `event/{ModTradesEvent,AddVillageStructuresEvent}` 源码 | 被 datapack 取代，源码保留但排除编译 | 无（功能由 datapack 实现） |
| loot_table（猪/猪灵掉落等）与 `global_loot_modifiers.json` | 数据格式迁移待后续 | 相关生物掉落暂缺 |

## 五、版本号与后续

1. **版本号**：已从 `1.4.1-release-neoforge+mcXX` 升到 **`1.7.0-release-neoforge+mcXX`**，
   因为内容已经与官方 1.7.0 对齐并超过竞品 1.6.0 构建。
2. **`RecipeFromIdFunction` 方案**：26.1.2/26.2 用运行时解析，与 26.3 的 data-component
   机制不同但行为一致，保留现状。
3. **仍未移植**：datagen（26.x 已删除旧 datagen API，改手写 JSON 生成）、
   Create/EMI/Tetra/Ponder 兼容（对应模组没有 26.x 构建）、26.3 的 loot API 重写
   （26.3 把 `LootItemFunction` 改成 `BiFunction` + `Holder` 条件，loot 包与村庄箱子战利品
   在该分支仍排除）。

## 五点五、2026-11-25 后续补充（兼容与验收）

- 兼容：Farmer's Delight（第三方 26.x 移植）、Carry On、Harvest with ease 三个分支全部接入；
  26.1.2 另接入 Every Compat（+ Moonlight）。
- 村庄结构：改为 mixin 运行时注入，顺带修掉了覆盖文件比原版少约 50 个房屋/群系的问题。
- 渲染：竹筛/厨架渲染器与蒸笼/厨架 HUD 覆盖层按官方 1.20.1 数值校准，HUD 支持 GUI scale。
- 关键技术债清理：26.1.2/26.2 的 `neoforge.items` 全部迁到 `transfer` API。
- 客户端：`runClient` 实跑验收，并修掉了"dev 客户端进世界必卡加载地形"的 GameTest 注册表同步崩溃。

## 六、测试环境

- JDK 26（NeoForm/NFRT）、JDK 25（编译目标）、Gradle 9.2.1、ModDevGradle 2.0.148
- 三版本本地 `clean build` + `runGameTestServer` 均通过
