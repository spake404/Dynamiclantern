# Changelog

## 1.6.1 - 2026-06-14

### English

- Fixed `IllegalStateException: Cannot get config value before config is loaded` at startup caused by `Diagnostics.log()` reading config values during mod construction.
- Removed premature `CuriosRendererRegistry.load()` call from the startup renderer registration path. Curios itself calls `load()` during `EntityRenderersEvent.AddLayers`, which fires after all model layers are ready. This avoids triggering broken renderer constructors from other mods (e.g. Cataclysm) that access model layers too early.

### 中文

- 修复启动时 `Diagnostics.log()` 在模组构造阶段读取配置值导致的 `IllegalStateException: Cannot get config value before config is loaded` 崩溃。
- 移除启动渲染器注册路径中过早的 `CuriosRendererRegistry.load()` 调用。Curios 自身会在 `EntityRenderersEvent.AddLayers` 中调用 `load()`，该事件在所有模型层就绪后触发，避免因其他 mod（如 Cataclysm）的渲染器构造函数过早访问模型层而导致崩溃。

## 1.6.0 - 2026-06-14

### English

- Added config-loaded guards around Dynamic Lantern waist item rule resolution.
- Fixed a startup failure where Curios could call `dynamiclantern:waist_renderable` before the NeoForge client config finished loading.
- Avoided reading `waistRenderableItems` before `dynamiclantern-client.toml` is loaded.
- Kept built-in default waist items available during early startup while deferring player-configured waist items until the config is loaded.
- Prevented early empty config reads from being cached as final waist item rules.

### 中文

- 为 Dynamic Lantern 的腰间物品规则解析增加配置加载状态保护。
- 修复 Curios 可能在 NeoForge 客户端配置加载完成前调用 `dynamiclantern:waist_renderable`，导致启动失败的问题。
- 避免在 `dynamiclantern-client.toml` 加载前读取 `waistRenderableItems`。
- 启动早期仍保留内置默认腰间物品可用，玩家配置的腰间物品会延后到配置加载完成后再解析。
- 避免把启动早期的空配置读取结果缓存成最终腰间物品规则。

## 1.5.5 - 2026-06-12

### English

- Ported the 1.20.1 feature set to the NeoForge 1.21.1 branch.
- Migrated Curios integration to Curios `9.5.1+1.21.1` Optional-based APIs.
- Migrated Cold Sweat Soulspring Lamp custom item data to Minecraft 1.21 data components.
- Migrated Epic Fight waist-layer registration to Epic Fight's 1.21 client event hooks.
- Fixed Epic Fight duplicate or misplaced waist lantern rendering by letting Dynamic Lantern handle the lantern layer while preserving other Curios compatibility behavior.
- Fixed Curios render visibility handling for waist lanterns.
- Hiding a belt slot in Curios now hides Dynamic Lantern's waist model in both vanilla and Epic Fight rendering paths.
- Shader held-item lighting now stops when the Curios belt slot display is disabled.
- Added a Dynamic Lantern Curios belt validator for configured waist items.
- Newly added waist display items can now be equipped in the Belt slot and show the Curios Belt tooltip without re-entering the world.
- Kept built-in default waist items hidden from the settings list while allowing them by default when their owning mod is installed.
- Kept `cold_sweat:soulspring_lamp` as a built-in waist item and shader-light item, with Curios attack fuel behavior support.
- Replaced per-tick cache refresh with event-driven cache invalidation from Curios render-sync packets.
- Kept cached Curios lookups for normal rendering and shader lighting without ignoring visibility changes.
- Added `renderDiagnosticLog`, disabled by default, so release builds do not print render diagnostics during normal gameplay.
- Updated README and changelog with bilingual documentation for the 1.21.1 / 1.5.5 release.

### 中文

- 将 1.20.1 版本的功能迁移到 NeoForge 1.21.1 分支。
- 将 Curios 集成迁移到 Curios `9.5.1+1.21.1` 的 Optional API。
- 将 Cold Sweat Soulspring Lamp 兼容逻辑使用的物品自定义数据迁移到 Minecraft 1.21 Data Components。
- 将 Epic Fight 腰间渲染层注册迁移到 Epic Fight 1.21 的客户端事件 hook。
- 修复 Epic Fight 模型下灯笼重复渲染或位置错误的问题，由 Dynamic Lantern 接管灯笼渲染，同时保留其他 Curios 兼容逻辑。
- 修复 Curios 显示开关对腰间灯笼不生效的问题。
- 在 Curios 中关闭腰带槽位显示后，原版模型和 Epic Fight 模型路径下的腰间灯笼都会隐藏。
- 关闭 Curios 显示后，shader held-item lighting 也会同步停止。
- 新增 Dynamic Lantern 自己的 Curios 腰带槽位 validator，用于玩家配置的腰部显示物品。
- 新增到腰部显示名单的物品现在可以立刻放入 Belt 槽位，并显示 Curios 的 Belt tooltip，不需要重新进入世界。
- 内置默认腰部物品仍然不会显示在设置列表中，但在对应模组安装时会默认允许。
- 保留 `cold_sweat:soulspring_lamp` 的内置腰部物品和 shader 发光支持，并支持 Curios 中的攻击燃料行为。
- 移除按玩家 tick 刷新缓存的方案，改为 Curios 渲染显示同步包触发缓存失效。
- 保留正常渲染和 shader 光照的 Curios 缓存，同时不再忽略显示状态变化。
- 新增 `renderDiagnosticLog`，默认关闭，发布版正常游玩时不会输出渲染诊断日志。
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
- 补充并保留腰间显示名单说明，玩家配置的方块或物品可以作为腰间灯笼渲染。
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
  - 放在 Curios 中时，补充与手持物品一致的攻击燃料行为。
  - 按灵魂灯笼映射给 shader held-item lighting。
- 调整 shader 发光规则：本身会发光的方块物品无需加入玩家名单，也可以参与光影手持发光识别。
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

- 新增可配置的腰间显示物品名单。
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
