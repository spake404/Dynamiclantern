# Dynamic Lantern

Dynamic Lantern is a NeoForge 1.21.1 mod that renders supported Curios belt items as visible waist lanterns and lets compatible shader packs recognize them for held-item lighting.

动态灯笼是一个 NeoForge 1.21.1 模组，可以把支持的 Curios 腰带物品渲染为腰间灯笼，并让兼容光影识别这些物品的手持发光效果。

## English

### Features

- Renders supported Curios belt items on the player's waist.
- Supports vanilla lanterns, soul lanterns, configured custom items, and built-in optional compatibility items.
- Supports item-model rendering for non-block waist items.
- Provides left-side and back-side placement options.
- Adds configurable pendulum-style swing physics.
- Lets Iris/Oculus shader held-item lighting use Curios belt items that are light-emitting block items, even when they are not in the user whitelist.
- Includes built-in shader lighting support for `cold_sweat:soulspring_lamp`, mapped as a soul lantern for shader held-item IDs.
- Includes internal Epic Fight model positioning, so EpicFightCuriosCompat is not required for the lantern position.
- Avoids duplicate lantern rendering when EpicFightCuriosCompat is installed together with Dynamic Lantern.
- Honors the Curios render visibility toggle, hiding both the waist model and shader held-item lighting when the slot display is disabled.
- Keeps Curios scans and optional compatibility hooks cached where possible to reduce render-thread and server-thread overhead.
- Does not add a dynamic light source.

### Built-In Defaults

The config screen only shows player-added item IDs. Built-in defaults are hidden from that list but are enabled automatically when their owning mod is installed.

- `minecraft:lantern`
- `minecraft:soul_lantern`
- `under_the_moon:moon_lamp`
- `cold_sweat:soulspring_lamp`
- Skinned Lanterns lantern variants

Optional item IDs are declared with `required: false`, so the game still loads normally when the optional mod is not installed.

### Cold Sweat Soulspring Lamp

`cold_sweat:soulspring_lamp` receives dedicated support:

- It can be equipped in the Curios belt slot by default.
- When equipped in Curios, it can use the same attack fuel behavior as the hand-held Soulspring Lamp.
- It participates in shader held-item lighting like a soul lantern while equipped in hand or Curios.
- It does not spoof hand rendering or hand animation, so normal held-item rendering is not disturbed.

### Configuration

The client config screen includes:

- Waist rendering toggle
- Swing physics toggle
- Swing strength
- Left-side placement
- Back placement
- Curios shader lighting override
- Additional waist display item whitelist

The client config file also includes `soulspringLampDebugLog` for Soulspring Lamp diagnostics. It is disabled by default in release builds.

### Requirements

- Minecraft `1.21.1`
- NeoForge `21.1.x`
- Curios `9.5.1+1.21.1`

### Optional Compatibility

- Iris/Oculus-compatible shader pipeline for held-item shader lighting
- Epic Fight
- EpicFightCuriosCompat, with Dynamic Lantern taking over lantern rendering while leaving other Curios compatibility behavior intact
- Cold Sweat
- Under the Moon
- Skinned Lanterns

## 中文

### 功能

- 将支持的 Curios 腰带物品渲染在玩家腰间。
- 默认支持原版灯笼、灵魂灯笼、玩家配置的自定义物品，以及内置的可选兼容物品。
- 支持非方块物品使用物品模型进行腰间渲染。
- 提供左侧悬挂和靠后位置选项。
- 提供可配置的摆动物理效果。
- Iris/Oculus 的 shader held-item lighting 可以识别 Curios 腰带中的发光方块物品，即使该物品没有加入玩家自定义白名单。
- 内置支持 `cold_sweat:soulspring_lamp` 的 shader 发光识别，并按灵魂灯笼交给光影处理。
- 内置 Epic Fight 模型定位，不再依赖 EpicFightCuriosCompat 来修正灯笼位置。
- 与 EpicFightCuriosCompat 同时安装时，会避免灯笼重复渲染。
- 遵守 Curios 的显示开关；关闭槽位显示时，腰间模型和 shader held-item lighting 都会一起关闭。
- 尽量缓存 Curios 扫描和可选兼容逻辑，降低渲染线程和服务器线程上的额外开销。
- 本模组不添加动态光源。

### 内置默认物品

设置界面只显示玩家手动添加的物品 ID。内置默认物品不会显示在该列表中，但在对应模组安装时会自动启用。

- `minecraft:lantern`
- `minecraft:soul_lantern`
- `under_the_moon:moon_lamp`
- `cold_sweat:soulspring_lamp`
- Skinned Lanterns 的灯笼变体

可选物品 ID 使用 `required: false` 声明，因此未安装对应模组时游戏也可以正常加载。

### Cold Sweat Soulspring Lamp

`cold_sweat:soulspring_lamp` 拥有专门适配：

- 默认可以放入 Curios 腰带槽位。
- 放在 Curios 中时，可以触发与手持 Soulspring Lamp 类似的攻击充能行为。
- 放在手中或 Curios 中时，会像灵魂灯笼一样参与 shader held-item lighting。
- 不伪造手部渲染或手部动画，因此不会影响玩家正常拿着其他物品时的渲染。

### 配置

客户端设置界面包含：

- 腰间渲染开关
- 摆动物理开关
- 摆动强度
- 左侧悬挂
- 靠后位置
- Curios 光影发光识别
- 额外腰间显示物品白名单

客户端配置文件还包含 `soulspringLampDebugLog`，用于 Soulspring Lamp 诊断日志。发布版默认关闭。

### 需求

- Minecraft `1.21.1`
- NeoForge `21.1.x`
- Curios `9.5.1+1.21.1`

### 可选兼容

- Iris/Oculus 兼容光影管线，用于 shader held-item lighting
- Epic Fight
- EpicFightCuriosCompat，灯笼渲染由 Dynamic Lantern 接管，其他 Curios 兼容逻辑保持不变
- Cold Sweat
- Under the Moon
- Skinned Lanterns

## Version / 版本

Current version / 当前版本：`1.5.5`

See [CHANGELOG.md](CHANGELOG.md) for release notes.
