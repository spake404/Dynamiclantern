# Changelog

## 1.5.5 - 2026-06-12

### English

- Ported the 1.20.1 feature set to the NeoForge 1.21.1 branch.
- Migrated Curios integration to Curios 9.5.1 Optional-based APIs.
- Migrated item custom data used by the Cold Sweat Soulspring Lamp compatibility to Minecraft 1.21 data components.
- Migrated Epic Fight waist-layer registration to Epic Fight's 1.21 client event hooks.
- Fixed Curios render visibility handling for waist lanterns.
- Hiding a belt slot in Curios now hides Dynamic Lantern's waist model in both vanilla and Epic Fight rendering paths.
- Shader held-item lighting now stops when the Curios belt slot display is disabled.
- Replaced per-tick cache refresh with event-driven cache invalidation from Curios render-sync packets.
- Kept cached Curios lookups for normal rendering and shader lighting without ignoring visibility changes.
- Updated README and changelog for the 1.21.1 / 1.5.5 release.

### 中文

- 将 1.20.1 版本的功能迁移到 NeoForge 1.21.1 分支。
- 将 Curios 集成迁移到 Curios 9.5.1 的 Optional API。
- 将 Cold Sweat Soulspring Lamp 兼容逻辑使用的物品自定义数据迁移到 Minecraft 1.21 Data Components。
- 将 Epic Fight 腰间渲染层注册迁移到 Epic Fight 1.21 的客户端事件 hook。
- 修复 Curios 显示开关对腰间灯笼不生效的问题。
- 在 Curios 中关闭腰带槽位显示后，原版模型和 Epic Fight 模型路径下的腰间灯笼都会隐藏。
- 关闭 Curios 显示后，shader held-item lighting 也会同步停止。
- 移除按玩家 tick 刷新缓存的方案，改为 Curios 渲染显示同步包触发缓存失效。
- 保留正常渲染和 shader 光照的 Curios 缓存，同时不再忽略显示状态变化。
- 更新 1.21.1 / 1.5.5 版本的中英双语 README 和 CHANGELOG。

## 1.5.1 - 2026-06-12

### English

- Fixed duplicate waist lantern rendering when EpicFightCuriosCompat is installed together with Dynamic Lantern.
- Kept Dynamic Lantern in charge of lantern rendering while preserving EpicFightCuriosCompat behavior for other Curios items such as backpacks.
- Documented and retained the waist display whitelist, allowing player-configured blocks or items to render as waist lanterns.
- Reduced Curios belt inventory scans by sharing cached waist item and shader-light item lookups.
- Cached waist item rule resolution and player body bounds used by the renderer.
- Avoided registering Cold Sweat server event hooks when Cold Sweat is not installed.
- Reduced unnecessary NBT and registry lookups in hot paths.
- Updated README and changelog for the 1.5.1 release.

### 中文

- 修复同时安装 EpicFightCuriosCompat 和 Dynamic Lantern 时腰间灯笼重复渲染的问题。
- 灯笼渲染由 Dynamic Lantern 接管，同时保留 EpicFightCuriosCompat 对背包等其他 Curios 物品的兼容逻辑。
- 补充并保留腰间显示白名单说明，玩家配置的方块或物品可以作为腰间灯笼渲染。
- 复用腰间物品和 shader 光源物品缓存，减少 Curios 腰带槽位扫描次数。
- 缓存腰间物品规则解析结果，以及渲染器使用的玩家身体模型边界。
- 未安装 Cold Sweat 时不再注册对应的服务器事件兼容逻辑。
- 减少热路径中的无用 NBT 和注册表查询。
- 更新 1.5.1 版本的中英双语 README 和 CHANGELOG。

## 1.5.0 - 2026-06-12

### English

- Added built-in default support for `under_the_moon:moon_lamp`.
- Added dedicated `cold_sweat:soulspring_lamp` compatibility:
  - Curios belt equip support by default.
  - Curios attack fuel behavior mirroring the hand-held item.
  - Shader held-item lighting support by mapping it as a soul lantern.
- Updated shader lighting rules so light-emitting block items can participate without requiring the user whitelist.
- Reworked Epic Fight waist rendering internally, matching the EpicFightCuriosCompat belt-lantern placement without requiring that mod.
- Adjusted left-side Epic Fight placement slightly inward for better body symmetry.
- Kept Soulspring Lamp diagnostics available through `soulspringLampDebugLog`, disabled by default for release.
- Updated README and changelog with bilingual documentation.

### 中文

- 新增 `under_the_moon:moon_lamp` 内置默认支持。
- 新增 `cold_sweat:soulspring_lamp` 专门适配：
  - 默认支持放入 Curios 腰带槽位。
  - 放在 Curios 中时，补充与手持物品一致的攻击充能行为。
  - 按灵魂灯笼映射给 shader held-item lighting。
- 调整 shader 发光规则：本身会发光的方块物品无需加入玩家白名单，也可以参与光影手持发光识别。
- 内置 Epic Fight 腰间渲染定位，复刻 EpicFightCuriosCompat 的腰带灯笼位置，不再要求安装该模组。
- 将 Epic Fight 左侧悬挂位置略微向身体内侧收回，使左右两侧更对称。
- 保留 `soulspringLampDebugLog` 诊断开关，发布版默认关闭。
- 更新中英双语 README 和 CHANGELOG。

## 1.2.0

### English

- Added a configurable waist display item whitelist.
- Added hidden built-in waist item support plus runtime Curios belt support for configured item IDs.
- Added item-model rendering for non-block waist items.
- Restricted shader glow recognition to Curios belt items that are light-emitting block items or built-in shader-light exceptions.

### 中文

- 新增可配置的腰间显示物品白名单。
- 新增隐藏的内置腰间物品支持，并允许运行时为配置物品提供 Curios 腰带支持。
- 新增非方块腰间物品的物品模型渲染。
- 将 shader 发光识别收窄到 Curios 腰带中的发光方块物品或内置 shader 发光例外物品。

## 1.1.0

### English

- Improved left-side lantern placement.
- Updated README and project metadata.

### 中文

- 改进左侧灯笼位置。
- 更新 README 和项目元数据。
