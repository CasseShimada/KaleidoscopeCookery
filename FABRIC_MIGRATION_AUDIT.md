# Kaleidoscope Cookery Fabric 迁移审计

> 本文件是 Minecraft 26.2 Fabric 迁移的唯一事实来源和后续模型交接入口。除非有命令输出或代码对照证据，否则状态不得标记为 `VERIFIED`。

## 执行元数据

- 最后更新时间：2026-07-17 00:41（Asia/Shanghai，Carry On 2.9.1 适配、1.1.0.6 版本构建与双模式真实搬运闭环完成，待远端 Actions/Release）
- 执行模型：Codex（GPT-5）
- 当前分支：`26.2-fabric`
- 当前已提交 HEAD：`19f1ca4bb6dc9fba876b3d1fa8d1d0c07c105ebb`（`Record official upstream through 1d935a2c`）
- 远端关系：已提交 HEAD 与 `origin/26.2-fabric` 一致；本轮 Carry On 适配仍为未提交工作区修改
- 初始工作区状态：存在用户未提交修改，必须保留：
  - `scripts/verify_server_boundary.py`
  - `src/gametest/java/com/github/ysbbbbbb/kaleidoscopecookery/gametest/KaleidoscopeCookeryGameTests.java`
  - `src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/block/kitchen/OilPotBlock.java`
- 目标运行目录：`C:\Users\Admin\AppData\Local\Programs\Minecraft_Client\PCL2\.minecraft\versions\server-26.2-loader.0.19.3`
- 用户指定唯一存档兼容实测源：`C:\Users\Admin\AppData\Local\Programs\Minecraft_Client\PCL2\.minecraft\versions\world`（原目录只读；2026-07-16 用户明确该世界来自其自写 Fabric 模组，且无须/无法提供 Forge 或 NeoForge 世界）

## 版本与迁移基线

| 项目 | 值 | 状态 | 证据/说明 |
| --- | --- | --- | --- |
| Minecraft | 26.2 | VERIFIED | Gradle、目标 launcher/日志及最终目标客户端/专服均为 26.2 |
| Fabric Loader | 0.19.3 | VERIFIED | Gradle、目标版本目录与最终客户端/专服日志交叉确认 |
| Fabric API | `0.153.0+26.2` | VERIFIED | `gradle.properties`、依赖解析、最终 clean build 与目标/开发客户端运行确认；0.154.0 仅用于 opt-in 可选兼容 smoke |
| Java | 25（本机 `25.0.2`） | VERIFIED | toolchain/release、`java -version`、Gradle 与目标客户端/专服均使用 Oracle JDK 25.0.2 |
| 模组 ID | `kaleidoscope_cookery` | VERIFIED | Forge/NeoForge/Fabric 元数据、全注册/资源 namespace、最终 jar 与目标运行日志一致 |
| 原 Forge/NeoForge 基线 | `1d935a2c5773452aa8ad399938fac29a6d3d4695`（`upstream/main`） | AUDITED | 2026-06-26 最新完整 Forge 1.20.1 实现；包含最新 Create 石磨输入 capability 修复；当前分支与其有共同祖先 `b14408ee` |
| 辅助近版本参照 | `8eaa0d2d865e42eec988cfae1464cd4055423b30`（`upstream/1.21.1-neoforge`） | AUDITED | NeoForge 1.21.1，2026-06-17；用于较新 API/序列化行为参照，但缺少主分支之后的修复，不能代表最终功能全集 |
| 用户真实存档实现基线 | `f67a5574e0944cbc6354260327907ad7b6f0dc97`（本地 `26.1.2-fabric`） | AUDITED | 指定世界 DataVersion 4790 / MC 26.1.2 / Loader 0.19.1 / Fabric API 0.145.4；该分支是当前 HEAD 的直接祖先，差异为 0/209 commits，精确包含世界中的旧苦力怕 marker 实现 |

### 基线选择记录

- 已检查分支、远端、标签、最近 80 条全分支提交历史、候选构建配置、提交时间、树规模和共同祖先。
- `upstream/main@1d935a2c` 是 Forge 1.20.1、2026-06-26 的最新原框架实现，共 4214 个文件、387 个 Java 源文件；它在 1.4.0 功能同步后继续修复石磨与 Create 漏斗输入兼容。
- `upstream/1.21.1-neoforge@8eaa0d2d` 是 NeoForge 1.21.1、2026-06-17，共 4216 个文件、386 个 Java 源文件；虽 Minecraft 版本较新，但停止时间更早，且缺少 `#191` Create 输入修复。
- 当前 Fabric HEAD 与 `upstream/main` 的共同祖先为 `b14408eef2e710033a1a603f7a15d0a2994a9364`，与 NeoForge 分支无共同祖先输出；当前分支直接继承 `origin/latest-fabric@e878a49e` 后继续迁移。因此选择 `upstream/main@1d935a2c` 作为功能/行为/存档基线，将 NeoForge 1.21.1 仅作为 API 近版本辅助参照。
- 用户指定真实世界由 `26.1.2-fabric@f67a5574` 对应实现生成；该提交是当前 HEAD 的直接祖先。旧 `EntityJoinWorldEvent` 用持久 entity tag `kaleidoscope_cookery.creeper_mustard_avoid_goal` 作为一次性门禁，但 AI goal 本身不持久化；当前 `ServerEntityLoadEvent` 先精确移除旧猫/苦力怕 marker，再按实际 goal 类型去重并重新安装。真实副本的 28 只已标记苦力怕已验证实体全部保留、marker 28→0，不会因清理或重载漏装 AI。

## 项目模块与源码目录

| 模块/目录 | 用途 | 状态 | 备注 |
| --- | --- | --- | --- |
| `src/main/java` | 服务端安全的核心逻辑 | VERIFIED | 11/11 静态边界验证、默认 97/97 GameTest、Carry On 安装态 103/103、普通专服、目标完整栈和最新 marker-cleanup 真实副本 smoke 均通过 |
| `src/client/java` | 客户端入口、渲染、Screen、REI 等 | VERIFIED | 独立 source set；common 无 client import；普通/可选栈/目标联机客户端均完成初始化 |
| `src/datagen` | Fabric 数据生成入口与资源生成 | VERIFIED | 最终 clean-state 中 Advancements、Loot Tables、Recipes 三 provider 全部通过；163 个基线配方恢复且资源对照未解释项为 0 |
| `src/gametest` | GameTest 与测试辅助代码 | VERIFIED | 用户初始修改已保留并协作扩展；默认 97/97 required tests、Carry On 安装态（含其自带 6 项）103/103 通过 |
| `src/main/resources` | Fabric 元数据、Mixin、Access Widener、资产和数据 | VERIFIED | pack/mixin/AW/资源/数据静态验证、规范化基线路径对照和客户端/专服运行均通过 |
| `src/main/generated` | Fabric 数据生成产物 | VERIFIED | 当前资源 source set；配方、进度、战利品及其 26.2 单数资源目录已与 Forge `src/generated/resources` 规范化对照，基线仅有路径只剩 52 个已验证的 API/模型替代项 |
| `scripts` | 静态验证脚本 | VERIFIED | 全部 11 个 `verify_*.py` 同轮运行通过；保留并扩展用户的服务端边界检查 |

## 原版功能清单与功能矩阵

当前矩阵汇总逐对象源码对照、静态契约、GameTest 与最终运行证据。用户已明确实际存档范围只有其自写 Fabric 版本产生的指定世界，不存在 Forge/NeoForge 世界；该指定世界现已完成独立备份、客户端登录、保存、重载、全区块升级与前后 NBT 对照。

| 功能或对象 | 原实现位置 | 当前实现位置 | 存档相关 ID/字段 | 状态 | 验证证据 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 模组入口与生命周期 | Forge `KaleidoscopeCookery` 与全部自动事件 | Fabric common/client entrypoint、生命周期/交互事件与最小 Mixin | mod ID、初始化顺序 | VERIFIED | 下方 19 项 common 入口精确映射；11/11 静态验证、默认 97/97 GameTest、Carry On 安装态 103/103、目标客户端/专服先前产物通过 | 客户端配置仅由 client entrypoint 初始化 |
| 方块与方块状态 | 基线 `init/ModBlocks` 与 block 包 | 当前 block 包 | 方块 ID、属性名/合法值 | VERIFIED | 112 个基线方块 ID 均存在；31/31 自定义属性键、23/23 原版属性引用零差异；搪瓷盆和 41 类 FoodBite 运行夹具通过 | 恢复遗漏的 `quality=0..4`、缺字段默认 4、放置/食用/掉落/Jade 语义 |
| 物品与数据组件 | Forge `ModItems`/旧 NBT；Neo `ModDataComponents` | 当前 item/init 包 | 物品 ID、组件/NBT 键 | VERIFIED | 190 个基线物品 ID 均存在；油壶三端点、旧食品品质及含 Damage/名称/Sharpness III/自定义 tag 的丰富 Forge 栈均完成保存/二次读取 | 用户指定世界没有 Cookery 物品或方块实体端点；其玩家 4 件非 Cookery 物品在 26.2 往返和全区块升级后逐项保持 |
| 流体 | 基线无自定义流体注册；Forge Fluid API 参与容器交互 | 当前原版流体/Transfer 容器逻辑 | 流体/容器语义 | VERIFIED | 无自定义 Fluid/FluidType；茶壶通用整桶事务、油壶容量/过滤/提交回滚与实体倾倒均有 GameTest | 无流体注册 ID 迁移 |
| 实体 | Forge `ScarecrowEntity`、`SitEntity`、`ThrowableBaoziEntity` | 当前同名实体 | `HandItems`、`ArmorItems`、`ShoulderEntity`、`Item`、`SitType` | VERIFIED | 稻草人 Forge handler/稀疏槽/重存、旧鹦鹉 `EntityType.create`、投掷包子旧 `Item`、座椅整数 `SitType` 均通过 62/62 GameTest | 鹦鹉旧整数 Variant、Health、UUID 在实际释放路径保留，无需额外嵌套 DFU |
| 方块实体与库存 | Forge `ModBlocks.BLOCK_ENTITIES` 与 blockentity 包 | 当前 `ModBlocks`/blockentity 包 | BE ID、NBT、槽位顺序 | VERIFIED | 统一 `References.ITEM_STACK` DataFix 已接入全部 12 类含物品 BE；所有类型均有 Forge-shaped 端点证据，关键复杂类型完成保存/二次加载；GameTest 61/61 | 用户指定世界全量扫描没有 Cookery 方块或方块实体；适用的玩家/实体 Cookery 持久数据已完成真实副本往返 |
| 菜单/容器/Screen | 基线与当前全局扫描 | 当前交互/BE 实现 | 无 Menu 注册 | VERIFIED | 新旧均无 `MenuType` / `AbstractContainerMenu`；全部持久库存槽位由 BE 兼容夹具覆盖 | 无独立菜单存档契约 |
| 渲染/模型/动画 | Forge `ClientSetupEvent` / `ModModelEvent` / renderer、tooltip、particle 注册 | `src/client`、26.2 `assets/items` 与 models | renderer/model layer/附加模型 ID | VERIFIED | 14 BE renderer、3 entity renderer、6/6 model layer、2 tooltip、2 particle 集合一致；普通、遗留、可选栈及目标客户端资源初始化通过，用户指定世界真实客户端正常渲染并驻留 | Cookery 资源、注册和正常玩法渲染链已验证；逐对象黄金截图不属于用户最终确认的崩溃/功能验收范围 |
| 配方与序列化器 | 基线 `crafting/recipe`、`serializer`、`output` | `src/main/java/.../crafting`、generated data | recipe type/serializer ID、JSON 键 | VERIFIED | 8 个 type / 9 个 serializer、旧 JSON/StreamCodec、163 个恢复 ID、250 pot、72 stockpot、27 个空 carrier 数量配方均锁定；26.2 实际从 2046 增至 2209 recipes 且 90/90 GameTest | 已恢复 138 pot、17 stockpot、8 shapeless 缺失路径并保留当前新增配方；空 ingredients 回归由静态脚本禁止 |
| 创造栏 | Forge `init/ModCreativeTabs.java` | 当前 `init/ModCreativeTabs.java` | `cookery_main` / `cookery_food`、条目顺序 | VERIFIED | 两个 tab ID/图标一致；`verify_client_assets.py` 锁定 food-before-main、空杯仅茶区、工具及冷盘火腿相对顺序；修改后客户端资源初始化通过 | 已修复发现的四项顺序/分组回归 |
| 村民职业/POI/交易 | Forge `ModVillager` / `ModPoi` / `ModTradesEvent` | 当前 `ModVillager` / `ModPoi` 与 26.2 `villager_trade` / `trade_set` 数据 | `chef`、4 个 POI、5 级交易池 | VERIFIED | 职业/POI ID 与 ticket 参数一致；恢复 6/7/4/14/1 五级池及 12 个菜谱交易；真实 `getOffer` 行为夹具通过 | `set_recipe_record` 在报价生成期构造组件，68/68 GameTest 验证菜谱报价的价格、次数、XP、输入顺序/数量、输出和锅型 |
| 战利品与世界生成 | Forge block loot/全局 loot modifier/村庄池 | generated/resources、运行自定义掉落、Fabric loot 回调 | loot/worldgen ID | VERIFIED | 11 张 PlateRegistry bowl 表、104 block loot/12 custom drops、5 个村庄结构及五类池已锁定；8 个 Forge GLM 由 Fabric loot 回调替代，猪油/驴肉/草帽路径有静态与运行证据，91/91 GameTest 通过 | PlateBlock 由 super 产生旧碗再叠加剩余 servings；基线 16 个 GLM/辅助 loot 文件不进入 Fabric 包，行为由事件实现 |
| 粒子与音效 | Forge `ModParticles` / `ModSounds` | 当前同名 init 类 | particle/sound ID | VERIFIED | 2 个粒子、9 个声音事件及资源引用由 `verify_client_assets.py` 锁定；旧茶壶/垃圾桶声音已恢复，客户端 OpenAL/粒子图集加载通过 | 误用 Fabric 茶壶 ID 仅作为兼容别名保留 |
| 网络 payload | Forge `NetworkHandler` / `SimpleC2SModMessage` | Fabric `NetworkHandler`、两个 unit payload | 旧 channel `network` + action 0/1；当前 `flatulence` / `throwing_baozi` | VERIFIED | 67/67 GameTest + `verify_network_safety.py`：unit codec 零字段往返、服务端效果/物品/潜行/冷却/生成/消耗校验 | 单 tick 仅阻止同 tick 包洪泛；包子客户端恢复 Forge `LeftClickEmpty` 的 MISS 边界 |
| 茶壶物品流体/左键/实体交互 | Forge `TeapotItem`、`LeftClickEvent` | 当前 `TeapotItem`、`TeapotClearEvent` | `BLOCK_ENTITY_DATA`：`TeaFluidId`、`Status`、`Result` | VERIFIED | 67/67 GameTest：流体 ID/组件、拒绝二次装液、AttackBlock 清空、成品单份消耗及 3 点伤害 | 原版 `BucketPickup`/`BucketItem#getContent` 恢复流体源装壶；服务端发送熔岩粒子 |
| Capability/Attachment/DataMap 替代 | Forge item/fluid capability、磨盘实体 item-handler、Neo attachment/DataMap、厨师英雄礼物映射 | Fabric Transfer API、`MillstoneEntityItemStorage`、Fabric attachment、resource reload/注册表与最小礼物 Mixin | 胀气起点、12 个磨盘实体 ID、11 个堆肥条目、`chef_gift` | VERIFIED | 最新默认 97/97 GameTest：通用茶壶流体容器、油壶事务、磨盘第三方/带箱马存储及掉落物回退、厨师礼物、attachment/DataMap；11/11 静态验证 | Fabric 无标准 `ItemStorage.ENTITY`；第三方实体须向公开 `SOURCE` lookup 注册，原版带箱马由内建 fallback 适配 |
| 配置与饱腹代偿 | Forge `config/GeneralConfig.java`（9 个键）、`config/ClientConfig.java`（1 个键）、`event/effect/SatiatedShieldEvent` | 当前 JSON `GeneralConfig`/`ClientConfig`、Fabric `SatiatedShieldEvent` | `kaleidoscope_cookery-common.toml`、`kaleidoscope_cookery-client.toml`、两个 Fabric JSON | VERIFIED | 通用 9 项默认值/范围/TOML 映射和减伤公式；客户端 `ShowFoodEffectTooltips` 旧键/默认/非法值及外部模组抑制通过 69/69 GameTest | 两类旧 TOML 均只读导入；客户端 JSON 仅由 client entrypoint 初始化，普通专服不创建该文件 |
| 保鲜与农夫套装实体效果 | Forge `PreservationEvent` 完成食用事件、`ArmorEffectEvent` 全部 LivingEntity post tick | 当前 `PreservationEvent` / `FarmerArmorEffectEvent` + `LivingEntityMixin` | 效果 ID、食物组件、实体 tick cadence | VERIFIED | 90/90 GameTest 中覆盖完成消费、自定义 food、非玩家四件套与缺靴控制 | 已恢复完成食用和全部 LivingEntity 自身 tick 语义 |
| 垃圾桶隐藏/客户端表现 | Forge/NeoForge `ChangeTargetEvent`、`CameraEvent`、`PlayerRenderEvent` | `MobMixin`、`CameraMixin`、Avatar render-state/submit Mixins、overlay | `SitType=1`、骑乘关系 | VERIFIED | 真实 `Mob.setTarget` GameTest；client Mixin 静态契约；普通客户端完整资源初始化 | 新目标赋值被取消且旧目标保留；第一人称 pitch 固定 0；垃圾桶乘客 render state 在提交前取消 |
| Mixin/AW/反射访问 | Forge 事件、方块 hook、3 个既有 Mixin 与客户端 arm pose | 11 common + 5 client Mixin、3 个 AW 字段项 | 注入目标与访问契约 | VERIFIED | `verify_mixins.py`；90/90 GameTest；普通与遗留包客户端完成全部既有 Mixin 应用、OpenAL、资源重载及图集创建 | 新增 common `PlayerMixin` 只在玩家加载尾部读取旧 Forge 胀气临时坐标；其余条目已按双基线、26.2 方法形状和运行应用逐项锁定 |
| 第三方可选集成 | Forge `compat/{create,emi,harvest,kubejs,ponder,tetra}` 及 JEI/REI/Jade/Farmer's Delight | 当前 JEI/REI/Jade/Farmer's Delight/Food Effect Tooltips 代码与 Carry On/Serene Seasons 数据 | 插件 ID、可选依赖、数据 namespace | VERIFIED | 59-mod 客户端栈完成资源重载/OpenAL/全部图集；Farmer's Delight 真实配方安装态通过；Carry On 2.9.1 双名单模式 31 个安全方块共 62 次真实往返、85 个不安全方块补集锁定，安装态 103/103 | Carry On 保持纯数据可选适配，生产代码不链接其类；其他无 26.2 构件的集成继续按用户范围列为审计项 |
| 资源包/数据包 | Forge 主资源与 `legacy_pack` | resources/generated、内置 `legacy_resources_pack` | namespace、路径、资源格式 88..107 | VERIFIED | 默认关闭遗留包 1406 资源/839 模型已运行验证；主包 10 种基线语言、12/12 效果纹理、6 个 Ponder 场景；规范化路径对照为基线 2402、当前 2927、基线仅有 52 | 52 项精确分类为 36 个由 blockstate 旋转替代的桌子模型和 16 个由 Fabric loot 事件替代的 Forge GLM 文件，未解释项为 0 |
| 专用服务端边界 | Forge dist 边界与当前 main/client source sets | main/client source sets | 类加载边界 | VERIFIED | `verify_server_boundary.py`、普通专服、最终 `4B6AA...437B` 目标三模组栈副本联机及二次加载均通过 | 最终目标 3071 recipes / 2701 advancements、五维度保存，专服无客户端类加载错误，第二轮 ERROR=0 |

## 注册对象映射表

注册调用、兼容别名、持久化端点和资源引用已由静态对照、GameTest 与最终运行共同验证。

| 注册表类型 | 原基线对象数 | 当前对象数 | ID 差异 | 状态 | 证据 |
| --- | ---: | ---: | --- | --- | --- |
| Block | 112 | 116 | 无缺失，新增 4 | VERIFIED | Forge 动态注册与 Fabric 集合对照、运行注册表 GameTest |
| Item | 190 | 204 | 无缺失，新增 14 | VERIFIED | Forge/Fabric 集合对照、运行注册表与最终目标加载 |
| Fluid | 0 | 0 | 无 | VERIFIED | 新旧源码与运行注册表均无自定义流体 |
| Entity Type | 3 | 3 | 无 | VERIFIED | `sit`、`scarecrow`、`throwable_baozi` ID 与旧数据夹具 |
| Block Entity Type | 15 | 15 | 历史 `recipe_block` 与迁移前 Fabric `recipe_book` 双 ID | VERIFIED | 两 ID 同时注册到兼容类型；静态脚本、运行注册表和旧端点夹具通过 |
| Menu Type | 0 | 0 | 无 | VERIFIED | 新旧源码全局扫描与最终运行注册表 |
| Recipe Type/Serializer | 8 / 9 | 8 / 9 | 无 | VERIFIED | 集合、九类旧 JSON/stream codec 与 2209 当前配方加载；`rice_bowl` 仅 serializer 与基线一致 |
| Particle Type | 2 | 2 | 无 | VERIFIED | 两 ID、客户端初始化与粒子图集通过 |
| Sound Event | 8 | 9 | 无缺失；新增误用 Fabric 茶壶 ID 的兼容别名 | VERIFIED | `ModSounds` / `sounds.json` 对照、`verify_client_assets.py`、客户端 OpenAL 初始化 |
| Mob Effect | 12 | 12 | 无 | VERIFIED | ID 集合、效果事件与运行语义 GameTest |
| Data Component | Neo 1.21.1：6 | 7（含兼容别名） | 同时保留 `oil_pot_oil_count` 与迁移前 Fabric `oil_pot_count` | VERIFIED | 双组件 ID + Forge `custom_data.oil_count` 读取迁移夹具，模型选择与重存通过 |
| Villager/POI | 1 / 4 | 1 / 4 | 无 | VERIFIED | `chef`、4 POI ID/ticket 参数、五级交易池与五类村庄结构运行测试 |

## Forge/NeoForge API 使用与替代方案

| 原 API/机制 | 原位置 | 当前替代 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| `@Mod`、事件总线与生命周期事件 | Forge/NeoForge 自动订阅事件逐项对照 | Fabric entrypoint/lifecycle/interaction events、必要的实体 Mixin | VERIFIED | 下表精确映射全部基线入口；11 common/5 client Mixin 静态契约与 90/90 GameTest，客户端门禁及两轮 smoke 通过 |
| `DeferredRegister` / `RegistryObject` | Forge/NeoForge init 包全量注册 | 原版 `Registry` + Fabric 注册 API | VERIFIED | 全局禁止扫描、逐注册表集合与最终目标运行注册表通过；仅明确历史冲突使用双 ID |
| Capability / ItemHandler | Forge/NeoForge `SpecialRecipeItemEvent` 在果篮/嬗变午餐袋后查询任意物品的 item-handler | 两个内置容器继续直接读写；其余玩家背包槽位通过可变 `ContainerItemContext` 查询 `ItemStorage.ITEM` | VERIFIED | `PlayerInventoryStorage.getSlot(slot)` 上下文枚举非空 view，并在事务内按实际 variant 提取；真实 shulker 3 个苹果经菜谱扣 2 后原槽保留 shulker + 1 苹果，82/82 GameTest |
| Capability / 实体 ItemHandler | Forge 磨盘对任意绑定 mob 查询 `ForgeCapabilities.ITEM_HANDLER`，失败后扫描磨盘上方掉落物 | 公开 `MillstoneEntityItemStorage.SOURCE` + Transfer `Storage<ItemVariant>`；内建带箱马 fallback；失败后保持原 3x3x1 `ItemEntity` 回退 | VERIFIED | 最新默认 97/97 GameTest：第三方 cow provider 每批提取 8；无效配方事务回滚；真实带箱驴使用 26.2 `INVENTORY_SLOT_OFFSET=500` 回滚/提交；掉落小麦供料 | Fabric Transfer API 8.0.11 没有标准 entity lookup，其他模组需按实体类型显式注册 Cookery lookup；这是目标 API 的明确集成边界 |
| Capability / 物品流体与油壶自动化 | Forge 茶壶任意流体容器、Neo 油壶 item handler | `FluidStorage.ITEM` 完整一桶事务；`OilPotStorage` 256 容量、仅油、全方向 `ItemStorage.SIDED` | VERIFIED | 94/94 与 95/95 GameTest：通用容器整桶转移/不足拒绝；油壶插入、提取、回滚、提交及方块状态同步 | 不再限定原版水桶；空茶壶不写 `0 minecraft:air` |
| Forge 网络通道 | Forge `network` channel / VarInt action 0..1 | 两个 Fabric serverbound unit payload | VERIFIED | 方向、unit codec、权威玩家/状态/冷却/频率与 MISS 边界由静态脚本、GameTest 和最终客户端联机验证 |
| Forge common 配置 | `GeneralConfig` + `SatiatedShieldEvent` | 自有 Fabric JSON 配置 + 旧 TOML 一次性只读导入 | VERIFIED | 恢复 9 项字段、范围、默认值与完整减伤算法；GameTest 覆盖 |
| DistExecutor / 客户端事件 | Forge/NeoForge `CameraEvent`、`PlayerRenderEvent`、HUD 等 | 独立 client source set/entrypoint、client-only Mixin/Fabric HUD API | VERIFIED | 垃圾桶相机/玩家隐藏、两 HUD crosshair 锚点、锅提示动作栏避让及胀气四门控均恢复；专服边界、编译和普通/遗留包客户端 smoke 通过 |
| DataMap/Attachment | Forge millstone DataMap、玩家 `ForgeData`；Neo attachment、compostable DataMap、厨师英雄礼物 | 原版 codec/resource reload + Fabric attachment/堆肥注册 + `GiveGiftToHeroMixin` | VERIFIED | 磨盘 12 项 reload 集合、11 个堆肥条目、唯一胀气 attachment 的旧 NBT/生命周期及 `chef_gift` 映射均静态或运行验证；最新默认 97/97 GameTest | 26.2 无公开英雄礼物注册 API，故仅该映射使用最小 Mixin |
| Access Transformer | Forge AT 与 NeoForge公开字段对照 | 3 个 AW 字段项 + 最小只读 client accessor | VERIFIED | `verify_mixins.py`、26.2 字节码与运行应用已逐条验证；HUD accessor 只暴露 overlay timer getter |

### Forge/NeoForge common 自动事件精确映射

| 基线入口/机制 | Forge 1.20.1 行为 | 当前 Fabric/原版入口 | 状态 | 当前证据/待验证点 |
| --- | --- | --- | --- | --- |
| `AddVillageStructuresEvent` / `ServerAboutToStartEvent` | 五类村庄房屋池各加入权重 4 的厨房 | `ServerLifecycleEvents.SERVER_STARTING` | VERIFIED | 五池 expanded/raw 权重真实 GameTest |
| `ArmorEffectEvent` / `LivingTickEvent` | 每 20 tick 为水中完整农夫四件套 LivingEntity 刷新 25 tick 海豚恩惠 | `LivingEntityMixin` TAIL -> `FarmerArmorEffectEvent` | VERIFIED | 非玩家完整/缺靴控制 GameTest |
| `ChangeTargetEvent` | 禁止 mob 把垃圾桶乘客设为目标 | `MobMixin` `setTarget` HEAD | VERIFIED | 真实 setter、可见控制、旧目标保持 GameTest |
| `EntityJoinWorldEvent` | 猫优先级 5 趴厨具目标；苦力怕优先级 3 躲避芥末效果实体 | `ServerEntityEvents.ENTITY_LOAD` | VERIFIED | 真实 load callback 安装优先级 5/3 goal，重复触发仍各仅一项 |
| `HoeUseEvent` / `RightClickBlock` | 水覆盖的 dirt/grass/path 被锄成耕地、耗 1 耐久并触发进度 | `UseBlockCallback` -> `WetFieldHoeUseEvent` | VERIFIED | 真实回调验证湿 dirt 变耕地/耗 1 耐久，干 dirt 控制 PASS |
| `LeftClickEvent` | 潜行左击清空手持茶壶；潜行空挥投掷包子 | `AttackBlockCallback` + client MISS 输入/payload | VERIFIED | 茶壶、网络权威与 MISS 边界 GameTest/静态契约 |
| `ModTradesEvent` | 厨师五级交易池及菜谱交易 | 26.2 `villager_trade` / `trade_set` 数据 | VERIFIED | 6/7/4/14/1 池与动态菜谱报价 GameTest |
| `RightClickEvent` | 毛虫喂幼鸡；果篮潜行持物取出及排除项 | `UseEntityCallback` + `UseBlockCallback` | VERIFIED | 两条真实回调链 GameTest |
| `ScarecrowFarmlandTrampleEvent` | 16 格内稻草人取消实体踩田 | `FarmBlockMixin` | VERIFIED | 实体踩踏/无实体缺水控制 GameTest |
| `SickleHarvestNetherWartEvent` | 成熟地狱疣镰刀收获并补种，未成熟阻止默认分支 | `SickleHarvestCallback` | VERIFIED | 显式结果及成熟补种 GameTest |
| `SpecialRecipeItemEvent` / item capability | 任意背包内物品容器参与菜谱计数与扣料 | Fabric Transfer `ItemStorage.ITEM` + action callbacks | VERIFIED | shulker 端到端事务扣料 GameTest |
| `FlatulenceEvent` + `FlatulenceEffect` | 四项客户端门控；服务端跳跃；`ForgeData.FlatulenceEffectStartingPosition` 保存方块坐标 | client input + unit payload + Fabric attachment + 旧 NBT 只读迁移 | VERIFIED | Forge-shaped `{X,Y,Z}` 迁移、移除清理、整数方块坐标、输入门禁和网络权威均已验证 |
| `HinderEvent` / `LivingDamageEvent` | 带阻碍效果的 LivingEntity 造成实际伤害后给目标缓慢 II 100 tick | `ServerLivingEntityEvents.AFTER_DAMAGE` | VERIFIED | 真实 mob damage 得到 100 tick Slowness II，无效果攻击者控制不生效 |
| `PreservationEvent` / 完成食用 | 有保鲜效果时移除本次食物施加的有害效果 | `LivingEntityMixin.completeUsingItem` | VERIFIED | 自定义 food、无关效果和无保鲜控制 GameTest |
| `ProjectileDodgeEvent` / projectile impact | 消耗 200 tick、尝试 16 次在 3 格内传送并跳过命中 | `ProjectileMixin` + `ProjectileDodgeHandler` | VERIFIED | 真实命中、持续时间与效果 flags GameTest |
| `SatiatedShieldEvent` / damage | 按配置和饱食度抵消伤害并保留溢出伤害 | `ServerLivingEntityEvents.ALLOW_DAMAGE` | VERIFIED | 完整算法/门禁/溢出 GameTest |
| `VitalityEvent` / `LivingDeathEvent` | 有活力效果的击杀者杀死成年 Ageable/Zombie 后生成对应幼体，僵尸 1/20 为幼年村民 | `ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY` | VERIFIED | 真实带效果击杀成年 cow 后恰生成一个同类幼体；僵尸概率/分支由静态契约锁定 |
| `BlockMixin` instant-smelting hook | 仅矿石掉落，按效果等级限量熔炼并保留余量 | `LootTableEvents.MODIFY_DROPS` | VERIFIED | 真实铜矿掉落在 amplifier 0 时恰转 1 个铜锭并保留 raw copper 余量 |
| Forge global loot modifiers | 6 类猪油、驴肉及戴草帽短草种子附加池 | `LootTableEvents.MODIFY` -> `ExtraLootTableDrop` | VERIFIED | 移除错误 empty；真实厨刀击杀验证驴肉两 roll、猪油必定一 roll，无厨刀驴肉控制为零；种子池参数由静态契约锁定 |

当前工作树、release jar、`jdeps` 与 runtimeClasspath 均无 Forge/NeoForge 生产框架、元数据或运行依赖，也无 Architectury 生产条目/字节码依赖。REI 公共 `EntryStacks` 签名要求 Architectury/basic-math 仅在 client compile classpath 可见；移除探针已证明该边界，项目源码不 import 两者。

## 存档兼容契约

以下项目全部是硬性契约；2026-07-16 用户将实际存档适配范围明确限定为其自写旧 Fabric 模组生成的指定世界，并确认不存在 Forge/NeoForge 相关存档。未取得该原件的只读清单及副本加载、保存、退出和二次加载证据前，整体兼容状态保持 `IN_PROGRESS` 或 `PARTIAL`；不再以不存在的 Forge/NeoForge 世界作为阻塞条件。

| 契约项 | 要求 | 状态 | 证据/风险 |
| --- | --- | --- | --- |
| mod ID / namespace | 保持 `kaleidoscope_cookery` | VERIFIED | 新旧元数据、全部注册/资源 namespace、release jar 与目标日志一致 |
| 注册 ID | 新旧所有注册对象 ID 不变，历史冲突显式兼容 | VERIFIED | 所有注册集合无基线缺失；recipe BE、油壶组件和声音历史差异均以双 ID/别名显式兼容并测试 |
| 方块状态 | 属性名称和合法值不变 | VERIFIED | 基线/当前逐类机械集合为自定义属性 31/31、原版属性引用 23/23、零差异；恢复 FoodBite `quality=0..4`/默认 4，全部 41 类运行检查通过 |
| 方块实体 | 类型 ID、NBT 键、值类型、默认值、库存槽位不变或显式迁移 | VERIFIED | 12/12 库存 BE 有 Forge-shaped 端点；锅/汤锅/茶壶/砧板/厨具架/烤肉架及磨石均验证保存后二次加载，槽位/数量/附加状态保持 |
| ItemStack | 旧 NBT/组件、耐久、附魔和自定义数据可读并保义 | VERIFIED | `LegacyItemStackCompat` 以 Forge 1.20.1 data version 3465 调用 `References.ITEM_STACK` DataFix；油壶三格式、实体栈、食品旧品质及钻石剑 Damage/名称/Sharpness III/自定义 tag 均解码、保存、二次读取通过 |
| 实体 | 类型 ID、NBT、UUID 引用、计时器和状态字段兼容 | VERIFIED | 3 个实体 ID 一致；全部模组自定义持久化字段有旧形状夹具与重存；旧鹦鹉 ShoulderEntity 的 Variant/Health/UUID 由当前实际创建路径保留 |
| 容器 | 槽位顺序和保存格式兼容 | VERIFIED | 全部 12 类库存 BE 的 Forge handler/直接栈端点、稀疏槽、复杂附加状态、保存后二次加载及磨石四输出槽均通过 |
| 配方 | type/serializer ID 与数据字段兼容 | VERIFIED | 九类 Forge-shaped JSON、27 个 `empty_carrier` 配方的历史语义、稳定 26.2 重编码和三种 Optional StreamCodec 往返均通过；2046 个当前配方加载 |
| 世界/POI/村民 | worldgen、结构、POI、职业引用不丢失 | VERIFIED | 4 POI/chef、五类村庄池、交易注册表与最终目标全新世界五维度创建/联机/保存通过 |
| 玩家数据 | 附加数据、进度、配方、计分板语义不变 | VERIFIED | 基线只有 `FlatulenceEffect` 两处 `getPersistentData()`，无玩家 capability/clone/provider/SavedData；当前唯一 attachment 的 Forge-shaped 读取、清理及进度兼容别名已验证 |
| 配置 | 文件名、键、类型与默认值兼容 | VERIFIED | 9/9 字段、范围与默认值已恢复；识别标准 Forge/NeoForge `kaleidoscope_cookery-common.toml` 的 `[cookery]`，旧文件只读，已有 Fabric JSON 优先 |
| 旧存档实测 | 用户指定的旧 Fabric 世界副本完成加载、保存、退出、二次加载 | VERIFIED | 原件 DataVersion 4790 / MC 26.1.2 / `fabric`，70 files / 29,711,647 bytes；独立备份后仅启动工作副本。原 UUID 客户端两次进入 Aether 原坐标，26.2 保存、重载、`--forceUpgrade` 5,955 个方块区块与最终 NBT 对照均成功；玩家物品、26/26 配方书、Cookery `raw_meatball` 配方/进度时间戳和 71 个 FD 方块全部保持。另取独立 smoke 副本加载全部 marker 区块后，28 只 creeper 全部保留且旧 marker 28→0。可部署副本为 `run/codex-fabric-2612-upgrade-20260716-1820`；原件、备份与该可部署副本未被清理 smoke 修改 |

## 网络协议兼容情况

- 状态：`VERIFIED`。
- Forge 1.20.1 只有 `kaleidoscope_cookery:network`、协议 `1.0.0`、PLAY_TO_SERVER、一个 VarInt action：`0=FLATULENCE`、`1=THROW_BAOZI`；NeoForge 1.21.1 等价改为 `simple_c2s` payload。
- Fabric 26.2 将两个无业务字段动作拆为 serverbound unit payload：`kaleidoscope_cookery:flatulence` 与 `kaleidoscope_cookery:throwing_baozi`；方向和动作语义等价，避免接收未知 discriminant。跨 Minecraft/加载器客户端本来不具备线协议互通条件，因此不保留旧 Forge channel 字节格式，但必须保留两种玩法行为。
- Fabric receiver 从 `context.player()` 获取权威玩家；payload 不携带物品、效果、位置等客户端字段。当前还校验效果、手持、潜行、连接状态、实体生成结果，并使用原版消费 API。
- 放屁与包子均使用 1 tick 服务端节流，仅拒绝同 tick 重复包而不弱化基线点击频率；`ClientPreAttackCallback` 要求 `HitResult.Type.MISS`，与 Forge `LeftClickEmpty` 一致。
- 67/67 GameTest 已验证两种 unit codec 不写入任何客户端字段，服务端拒绝缺少效果/潜行状态的动作，并验证放屁跳跃、同 tick 限流、包子实体生成、物品消费与冷却；静态脚本同时固定接收方向、权威字段和 MISS 边界。
- 存档兼容不要求 Forge 客户端与 Fabric 服务端跨加载器直连，但 Fabric 客户端与 Fabric 专服必须完成注册表和 payload 同步验证。

## 客户端与服务端边界

- 状态：`VERIFIED`。
- 当前存在 `src/client` 独立 source set，这是结构证据，不是运行证据。
- 必须检查 `src/main` 中所有 `net.minecraft.client`、客户端 Fabric API、渲染类静态引用以及可选兼容插件入口。
- `verify_server_boundary.py` 已证明 common/server 无直接客户端 import；普通 `runServer` 在 Java 25 / Loader 0.19.3 下加载 43 mods、2046 配方并到达 `Done`，新世界保存后二次启动再次到达 `Done`，两次均通过 `stop` 保存三维度并正常退出。
- 无可选兼容模组的 `runClient` 加载 51 mods 后进入 `Minecraft* 26.2` 主界面；Indigo/OpenGL、模组资源重载、字体、声音引擎及全部图集创建成功。开发占位账户仅产生预期的 Mojang/Realms 401，不属于模组错误；测试后确认无 Java 进程残留。

## 第三方兼容情况

| 集成 | 当前观察 | 状态 | 待验证事项 |
| --- | --- | --- | --- |
| REI | 当前 `src/client/.../compat/rei`；Fabric `clientCompileOnly`，其公开签名迫使编译期可见 Architectury/basic-math | VERIFIED | 机械基线复查发现并恢复遗漏的 flex pot、flex stockpot、teapot，现与 JEI 同为精确 8 类且 category/display 双注册；自定义 Ingredient 改用 26.2 `SlotDisplay` 解析。完整安装栈进入旧世界副本后完成 START/END reload 并同步 2446 recipes；release jar/runtimeClasspath/jdeps 均不含 REI/Architectury |
| JEI | 基线与当前均有八类 recipe category/plugin；当前使用 26.2 common/fabric API 的 `clientCompileOnly` | VERIFIED | 已升级并实测 JEI 30.11.0.67；修复对只读同步 recipe list 原地排序/追加导致的 `UnsupportedOperationException`。完整安装栈进入世界后 Cookery plugin 注册 8 类配方且无 plugin ERROR；Create 转换配方展示仍从属 Create 26.2 上游缺口 |
| Jade | 基线与当前均有 server/client plugin/provider；当前 15 个有状态 BE 均有 provider | VERIFIED | 安装态暴露并补齐 `food_bite_block`、`kitchenware_rack`、`steamer` 配置翻译；验证器现锁定全部 13 个 UID 的 en_us/zh_cn 键。Jade 26.2.9+fabric 完整栈中 common/client plugin 均加载且不再触发 missing translation 断言 |
| Farmer's Delight | Forge 将 Cooking Pot 配方转换为汤锅执行/JEI/REI 展示；当前通过反射/ID 边界保持可选 | VERIFIED | 26.2 Fabric 3.6.7 安装态 Loader 45 mods / 2519 recipes / 96 tests；真实 beef stew、含 `fabric:any` 的 dumplings/cabbage rolls 三者的输入数、输出、时间、碗 carrier、汤锅实际匹配与 viewer 枚举全部通过；默认 runtimeClasspath 不含该模组 |
| Serene Seasons | 当前保留春/夏/秋 block/item 作物标签并已迁为 26.2 单数 registry 目录 | AUDITED | Cookery 标签成员由静态校验/GameTest 验证；现有第三方 jar manifest 不支持 26.2。用户明确忽略其他模组影响，列为范围外 |
| Carry On | 当前同时提供 `carryon:block_whitelist` 与 `carryon:block_blacklist`；Forge 基线以 IMC 动态拉黑全部模组方块 | VERIFIED | 用户提供的本地 26.2 / 2.9.1 源码与成品已实际加载。31 个单方块持久 BE（9 个设备/容器、11 椅、11 桌）可搬运；其余 85 个无 BE、食物/作物或多方块结构明确拉黑。黑名单/白名单模式下 31 个方块共 62 次搬起放下，坐标无关完整 NBT 与非朝向状态逐项相等；磨盘、蒸笼、烤肉架、3x3 冷盘拒绝搬起。默认 97/97、安装态 103/103 GameTest 通过 |
| Create | Forge `CreateCompat`/`MillstoneCompat` 与漏斗顶部输入 | AUDITED | 当前顶部 `ItemStorage.SIDED` 事务行为已 GameTest；Create 无 26.2 API，按用户指示列为范围外 |
| Harvest With Ease | Forge `HarvestCompat`/`CropHarvestEvent` | AUDITED | 第三方只到 MC 26.1.2，按用户指示列为范围外 |
| Tetra | Forge modular item 特殊工具集成 | AUDITED | 目标 26.2/Fabric API 不存在，按用户指示列为范围外 |
| EMI | Forge 八类 Cookery 配方与 Create milling 展示 | AUDITED | EMI 无 26.2 文件，按用户指示列为范围外；现有翻译键保留 |
| KubeJS | Forge Cookery recipe schema 与 soup base builder | AUDITED | KubeJS 只到 MC 26.1.2，按用户指示列为范围外 |
| Ponder | Forge 七类设备/食谱场景、标签和屏幕 | AUDITED | Ponder addon 无 26.2/Fabric 构件，按用户指示列为范围外 |
| Food Effect Tooltips | Forge `CompatRegistry` 检测 `foodeffecttooltips` 后关闭自身效果行，避免重复；另受客户端 `ShowFoodEffectTooltips` 控制 | VERIFIED | Fabric 2.1.2+26.2 安装态随 59-mod 客户端栈进入旧世界副本并正常保存退出；当前用 Loader mod ID 弱检测，无第三方类依赖，配置关闭、外部安装抑制重复行与默认分支继续由最新默认 97/97 GameTest 锁定 |

## 已修改文件及原因

| 文件 | 修改原因 | 状态 |
| --- | --- | --- |
| `FABRIC_MIGRATION_AUDIT.md` | 按要求建立持续审计、证据与交接入口 | VERIFIED |
| `src/main/resources/data/carryon/tags/block/{block_whitelist,block_blacklist}.json` | 将旧全量拉黑改为精确 31 个安全持久 BE / 85 个不安全方块补集，同时支持 Carry On 黑名单和白名单配置 | VERIFIED |
| `src/main/resources/fabric.mod.json` | 将已验证的 Carry On 2.9.1 声明为可选建议依赖，不形成硬依赖 | VERIFIED |
| `gradle.properties` | 将新增兼容功能的发布版本从 `1.1.0.5-fabric+mc26.2` 递增为 `1.1.0.6-fabric+mc26.2` | VERIFIED |
| `build.gradle` | 增加 `carryOnCompatJar` opt-in 本地 runtime，仅供反射安装态 GameTest，不进入默认运行依赖或成品 JAR | VERIFIED |
| `src/gametest/.../{CarryOnCompatTestAccess,KaleidoscopeCookeryGameTests}.java` | 无编译期 Carry On 依赖地调用真实搬起/放下处理器；双名单模式覆盖全部 31 个安全方块的完整 NBT/状态往返及多方块拒绝 | VERIFIED |
| `scripts/verify_data_pack.py` | 锁定 Carry On 31/85 精确分区、无重叠、无遗漏、无未注册成员 | VERIFIED |
| `src/main/java/.../init/ModDataComponents.java` | 恢复 NeoForge `oil_pot_oil_count`，保留当前 Fabric `oil_pot_count` 兼容别名 | VERIFIED |
| `src/main/java/.../item/OilPotItem.java` | 读取并迁移 Neo/当前 Fabric 组件及 Forge `custom_data.oil_count`，保留无关自定义数据 | VERIFIED |
| `src/main/java/.../init/ModBlocks.java` | 同时注册 `recipe_book` 与最新 Forge/Neo `recipe_block` 方块实体类型 | VERIFIED |
| `src/main/java/.../block/kitchen/EnamelBasinBlock.java` | 恢复 0..32 油量状态范围和空盆默认值 | VERIFIED |
| `src/main/java/.../init/ModSounds.java`、`entity/SitEntity.java` | 恢复旧茶壶/垃圾桶声音 ID 与垃圾桶离座音效，保留误用 Fabric ID 别名 | VERIFIED |
| `src/main/resources/assets/kaleidoscope_cookery/{sounds.json,lang/*,items/oil_pot.json,blockstates/enamel_basin.json}` | 同步声音、旧组件模型选择与 0..32 搪瓷盆模型映射 | VERIFIED |
| `src/gametest/.../KaleidoscopeCookeryGameTests.java` | 增加注册 ID、旧油壶数据迁移、搪瓷盆状态和声音兼容断言；与用户原修改协作 | VERIFIED |
| `scripts/verify_block_entities.py` | 精确记录并校验 `recipe_block` 只读兼容类型归一到 `RECIPE_BLOCK_BE` | VERIFIED |
| `src/main/java/.../config/GeneralConfig.java` | 恢复 Forge 9 项设置、范围与默认值；已有 JSON 优先，旧 common TOML 只读导入并生成 Fabric JSON | VERIFIED |
| `src/main/java/.../event/server/effect/SatiatedShieldEvent.java` | 恢复最低食物等级、饥饿效果、减伤百分比/上限/下限、疲劳和弱点倍率算法 | VERIFIED |
| `src/gametest/.../config/GeneralConfigTestAccess.java`、GameTest | 覆盖旧 TOML 9 键映射、非法范围回退、激活门槛及过量伤害 | VERIFIED |
| `src/datagen/.../lootable/BlockLootTables.java`、生成的搪瓷盆 loot table | 恢复破坏时掉落盆本体，并按 `oil_count` 逐单位完整返还油；油掉落沿用 Forge 不受爆炸存活条件约束的语义 | VERIFIED |
| `src/main/java/.../util/LegacyItemStackCompat.java` | 检测旧大写 `Count`，以 data version 3465 运行 Mojang ItemStack DataFix；兼容 Forge `ItemStackHandler` 的 `Size`/`Items`/`Slot` 与直接栈/单槽 handler | VERIFIED |
| `src/main/java/.../util/LegacyIngredientCompat.java` | 先读当前结构化 `Ingredient`，再兼容锅旧 `Carrier` JSON 字符串，并将旧 `{item:...}` / `{tag:...}` 归一化后交给官方 codec | VERIFIED |
| `FruitBasketBlockEntity`、`TableBlockEntity`、`RecipeBlockEntity`、`ChoppingBoardBlockEntity`、`KitchenwareRacksBlockEntity` | 将自定义字段中的旧 Forge 物品栈和 ItemStackHandler 接入统一兼容读取，保留槽位顺序 | VERIFIED |
| `ShawarmaSpitBlockEntity`、`SteamerBlockEntity`、`PotBlockEntity`、`StockpotBlockEntity`、`TeapotBlockEntity`、`TrashCanBlockEntity` | 将输入、结果、盖子、存储等自定义字段接入统一兼容读取；锅同时兼容旧 `Carrier` | VERIFIED |
| `crafting/output/RandomOutput.java`、`MillstoneRecipe.java`、`MillstoneRecipeSerializer.java` | 恢复 1..4 个概率输出、`result`/`results` 双格式、0..1 概率钳制和 registry-aware stream codec；保留当前 Fabric `carrier` 扩展 | VERIFIED |
| `MillstoneBlockEntity.java`、`MillstoneBlock.java`、`MillstoneSpecialFinishEvent.java` | 恢复四输出槽与逐输入/逐输出投掷；兼容 Forge handler、迁移前 Fabric 单栈、旧 UUID/Carrier；保存槽位、破坏掉落、手动/漏斗输出均不只处理首槽 | VERIFIED |
| `compat/{jade,jei,rei}` 磨石类别、`lang/{en_us,zh_cn}.json` | Jade/JEI/REI 展示全部磨石输出，JEI 恢复概率提示文案 | VERIFIED |
| `src/gametest/.../KaleidoscopeCookeryGameTests.java`（库存 BE 夹具扩展） | 覆盖锅、汤锅、茶壶、砧板、厨具架、烤肉架旧栈、槽位、Carrier/盖子/状态字段及保存后二次加载 | VERIFIED |
| `entity/ScarecrowEntity.java`、`entity/ThrowableBaoziEntity.java` | 稻草人双手/护甲 Forge handler 与投掷包子父类 `Item` 字段接入统一旧栈 DataFix；加载前清空稀疏装备槽 | VERIFIED |
| `src/gametest/.../KaleidoscopeCookeryGameTests.java`（实体夹具） | 覆盖稻草人装备/肩部数据、投射物非默认栈、座椅 `SitType` 及保存后二次加载 | VERIFIED |
| 九类 `crafting/serializer`、`LegacyIngredientCompat`、配方结果 codec | 恢复 Forge 对象/数组 Ingredient、`result.item`、Flex 锅空 carrier、汤锅 `empty_carrier` 优先级、茶壶空 ingredient | VERIFIED |
| `crafting/recipe/{FlexPotRecipe,StockpotRecipe,TeapotRecipe}.java`、锅/汤锅消费端、JEI/REI 类别 | 以 Optional 表达 26.2 已删除的 `Ingredient.EMPTY` 语义；仅存在时校验、消耗和展示容器/原料 | VERIFIED |
| `network/NetworkHandler.java`、`client/event/BaoziThrowClientEvent.java`、`scripts/verify_network_safety.py` | 恢复基线点击频率与 `LeftClickEmpty` 边界，同时保留单 tick 服务端防洪泛和全部权威校验 | VERIFIED |
| `item/TeapotItem.java`、`event/interaction/TeapotClearEvent.java`、`init/ModEvents.java` | 恢复手持茶壶从流体源装液、潜行左击清空、成品/熔岩对实体倾倒及粒子/声音 | VERIFIED |
| `src/gametest/.../KaleidoscopeCookeryGameTests.java`（网络/茶壶夹具） | 覆盖两个 unit payload、服务端动作校验/限流/生成/消耗，以及茶壶流体组件、清空回调、成品倾倒 | VERIFIED |
| `src/main/java/.../init/ModCreativeTabs.java`、厨师交易数据与 `scripts/verify_data_pack.py` | 恢复 Forge 创造栏分组/顺序与 12 个菜谱交易，固定五级交易池语义；菜谱交易使用延迟 modifier 结构 | VERIFIED |
| `src/main/java/.../loot/SetRecipeRecordFunction.java`、`init/ModLootTypes.java` | 注册 `set_recipe_record` loot function，仅用物品 ID/配方类型 bootstrap，并在报价生成期构造含 `ItemStack` 的持久组件 | VERIFIED |
| `src/gametest/.../KaleidoscopeCookeryGameTests.java`（交易行为夹具） | 从动态 `VILLAGER_TRADE` 注册表执行真实 `getOffer`，验证菜谱报价价格、次数、经验及完整 `RECIPE_RECORD` | VERIFIED |
| `scripts/verify_block_entities.py`、`scripts/verify_client_assets.py` | 校验磨石多输出防御性快照，并记录 `recipe_block` 兼容类型归一后共享主 renderer | VERIFIED |
| `scripts/verify_server_boundary.py` | 将饱腹护盾静态契约同步到完整 Forge 公式，同时保留用户的 OilPot 交互边界检查 | VERIFIED |
| `event/server/effect/{PreservationEvent,FarmerArmorEffectEvent}.java`、`mixin/LivingEntityMixin.java` | 将保鲜移到完成食用后，并以全部 LivingEntity 自身 tick 恢复农夫套装效果 | VERIFIED |
| `mixin/MobMixin.java`、`util/TrashCanTargeting.java`、`init/ModEvents.java` | 在真实 `Mob.setTarget` 入口拒绝垃圾桶乘客，移除不完整的 per-tick 玩家扫描，同时保留入座时旧目标清理 | VERIFIED |
| `client/render/TrashCanRenderState.java`、三个 client Mixin 与两个 Mixin 配置 | 恢复垃圾桶第一人称 pitch=0 和玩家渲染取消；使用 26.2 render-state 数据跨 extract/submit 传递 | VERIFIED |
| `scripts/verify_mixins.py`、`scripts/verify_server_boundary.py`、GameTest | 锁定完成食用、LivingEntity cadence、目标赋值、client-only 渲染契约并增加三项运行夹具 | VERIFIED |
| `src/main/java/.../config/GeneralConfig.java` | 生产日志使用中性 legacy common config 描述，避免框架名称残留；旧 TOML 文件名/键读取不变 | VERIFIED |
| `api/event/RecipeItemEvent.java`、`item/RecipeItem.java` | 在保留旧回调构造器的同时携带每个玩家真实背包槽位的可变 `ContainerItemContext` | VERIFIED |
| `event/SpecialRecipeItemEvent.java`、`scripts/verify_recipes.py`、GameTest | 恢复任意 `ItemStorage.ITEM` 容器枚举与可提交事务扣料；保留果篮/午餐袋直接路径并禁止 `withConstant` 扣料 | VERIFIED |
| `event/interaction/FruitBasketTakeOutEvent.java`、`FruitBasketBlock.java`、`ModEvents.java` | 用真实 `UseBlockCallback` 恢复 26.2 潜行持物取出，并恢复调试棒、烟花、女仆物品及非顶面方块物品放行 | VERIFIED |
| `scripts/verify_server_boundary.py`、GameTest（果篮持物交互） | 锁定新交互事件注册与全部旧条件；真实 Fabric 回调链验证普通/方块/排除物品分支 | VERIFIED |
| `client/event/FlatulenceClientEvent.java`、`scripts/verify_network_safety.py` | 恢复 Forge/NeoForge overlay、screen、鼠标捕获和窗口激活四项游戏内门控；按 26.2 `Minecraft.gui` accessor 更新静态契约 | VERIFIED |
| `client/resources/LegacyResourcePack.java`、客户端入口、主语言文件 | 通过 Fabric Resource Loader 以 `NORMAL` 恢复默认关闭的内置旧版材质包及本地化标题/描述 | VERIFIED |
| `resourcepacks/legacy_resources_pack/**` | 机械恢复 Forge 1400 资源基线，更新资源格式为 88..107；补齐 81 个独立模型 particle，并用三个 26.2 display-context selector 替代 Forge `separate_transforms` | VERIFIED |
| `client/event/{PotOverlayEvent,TrashCanOverlayEvent}.java`、`mixin/client/HudAccessor.java`、client Mixin 配置 | 将两 HUD 恢复到 crosshair 之后，并在 action-bar overlay 存在时让锅提示上移 12 像素 | VERIFIED |
| `scripts/verify_client_assets.py` | 锁定客户端初始化集合、内置包注册/格式/资源量、无 Forge 模型 loader、独立模型 particle、三组 2D/3D 显示上下文及 HUD 契约 | VERIFIED |
| `mixin/PlayerMixin.java`、`util/LegacyPlayerDataCompat.java`、common Mixin 配置 | 在 26.2 玩家读取结束后只读迁移 `ForgeData.FlatulenceEffectStartingPosition={X,Y,Z}`，仅在旧效果仍存在且新 attachment 未设置时应用 | VERIFIED |
| `effect/FlatulenceEffect.java` | 将首次起点从精确位置恢复为 Forge 基线整数方块坐标；移除时继续清理临时 attachment | VERIFIED |
| `event/server/loot/ExtraLootTableDrop.java` | 移除猪油池错误的等权 empty，恢复满足厨刀条件后每 roll 必定产油 | VERIFIED |
| `scripts/{verify_mixins,verify_server_boundary}.py`、GameTest | 锁定旧玩家数据 Mixin、AI 参数/去重、额外 loot 池，并运行验证六类 common 事件和胀气旧数据 | VERIFIED |
| `api/storage/MillstoneEntityItemStorage.java`、`inventory/transfer/ChestedHorseItemStorage.java` | 以公开 Fabric EntityApiLookup 恢复磨盘对绑定实体库存的扩展入口；按 26.2 槽 ID 500 偏移事务包装原版带箱马 | VERIFIED |
| `blockentity/kitchen/MillstoneBlockEntity.java`、`init/registry/CommonRegistry.java` | 从硬编码带箱马改为通用实体 storage lookup，并恢复 capability 未供料时扫描上方掉落物的 Forge 回退 | VERIFIED |
| `scripts/verify_server_boundary.py`、GameTest（磨盘实体存储） | 锁定 lookup/注册/事务/500 槽偏移/掉落物回退；运行验证第三方 provider、真实带箱驴回滚与提交 | VERIFIED |

注意：初始工作区的 3 个用户修改不属于本轮修改，必须在后续工作中保留并与其协作。

## 测试与验证日志

| 时间 | 命令/检查 | 结果 | 关键证据或错误 | 状态 |
| --- | --- | --- | --- | --- |
| 2026-07-15 | `git status --short --branch` | 成功 | 当前分支领先 196；3 个用户修改 | AUDITED |
| 2026-07-15 | `git log --oneline --decorate --graph --all -n 80` | 成功 | 当前 Fabric 分支持续行为修复历史 | AUDITED |
| 2026-07-15 | `git branch -a --verbose --no-abbrev`、`git tag --list`、`git remote -v` | 成功 | 找到 NeoForge 1.21.1 与 Fabric 多分支，两个远端 | AUDITED |
| 2026-07-15 | 候选分支 `build.gradle` / `gradle.properties` / manifest、提交关系和树规模比较 | 成功 | 锁定 `upstream/main@1d935a2c`；NeoForge 1.21.1 作为辅助参照 | AUDITED |
| 2026-07-15 | 当前 `build.gradle`、`gradle.properties`、`fabric.mod.json` | 成功 | Loom 1.15.5；MC 26.2；Loader 0.19.3；Fabric API 0.153.0+26.2；Java 25；client/main 分离 | AUDITED |
| 2026-07-15 | `.\gradlew.bat tasks --all --console=plain` | 成功 | 枚举到 clean/build/test、compileJava、compileClientJava、runDatagen、runGameTest、runClient、runServer 等目标任务 | AUDITED |
| 2026-07-15 | `java -version`、`.\gradlew.bat --version` | 成功 | Java/Gradle launcher/daemon 均使用 Oracle JDK 25.0.2；Gradle 9.4.0 | AUDITED |
| 2026-07-15 | 当前树 Forge/NeoForge/Architectury 全局扫描 | 部分通过 | 无 Forge/NeoForge 生产痕迹；build 中仍有 REI 用 `architectury-api` 直接 compileOnly 声明 | IN_PROGRESS |
| 2026-07-15 | Forge/Fabric 注册调用机械集合差异 | 部分通过 | Block 112/116、Item 190/204、Entity 3/3、BE 15/15、Effect 12/12、Particle 2/2、serializer 9/9；发现兼容 ID 差异 | IN_PROGRESS |
| 2026-07-15 | Forge/Neo/Fabric 搪瓷盆、油壶组件、声音与配置语义对照 | 发现回归 | 状态范围、油壶旧数据、两个声音注册和七个配置项/完整减伤算法未保持 | IN_PROGRESS |
| 2026-07-15 | `compileJava compileClientJava compileGametestJava` | 成功 | 第一批兼容实现编译通过；仅有显式旧组件 API deprecation 提示 | AUDITED |
| 2026-07-15 | `verify_block_entities.py`、`verify_resources.py`、`verify_pack_metadata.py`、`verify_release_hygiene.py` | 成功 | 双 BE ID、116 方块/204 物品资源、pack 范围和无 Forge 生产痕迹通过 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain` | 成功 | Minecraft 26.2 / Loader 0.19.3 / Fabric API 0.153.0+26.2 / Java 25 实际启动；53/53 required tests passed | VERIFIED |
| 2026-07-15 | `.\gradlew.bat compileJava compileGametestJava --console=plain` | 成功 | 完整配置/TOML 导入与减伤实现编译通过 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（配置迁移后） | 成功 | 同一目标版本栈实际启动；55/55 required tests passed | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runDatagen --console=plain`（搪瓷盆掉落修复后） | 成功 | 目标版本栈运行全部 3 个 provider，生成 1..32 的逐单位油掉落与盆本体掉落 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（搪瓷盆掉落修复后） | 成功 | 56/56 required tests passed；实际破坏 `oil_count=13` 的盆得到 13 个油与 1 个盆 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（Forge 1.20.1 栈探针） | **失败（预期发现）** | 56/57 通过；唯一失败证明 26.2 `ItemStack.CODEC` 不能读取 `{id:"minecraft:apple",Count:3b}` | BLOCKED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（统一旧栈 DataFix 与 BE 端点接入后） | 成功 | 58/58 required tests passed；直接旧栈、Forge `ItemStackHandler`、篮子/桌子/食谱块/蒸笼/垃圾桶的物品数与槽位顺序均通过 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat compileJava compileClientJava compileGametestJava --console=plain`（磨石恢复后） | 成功 | 核心、Jade/JEI/REI 客户端消费端及新增 GameTest 均在 Java 25 下编译 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（磨石多输出/旧存档恢复后） | 成功 | 60/60 required tests passed；2046 个配方全部加载；验证 `result`/`results`、概率钳制、逐输入投掷、Forge 四槽/UUID/Carrier、Fabric 单栈及保存后二次加载 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（全部库存 BE Forge-shaped 端点扩展后） | 成功 | 61/61 required tests passed；锅 9 槽/result/Carrier、汤锅 9 槽/result/lid、茶壶 input/result、砧板、厨具架、烤肉架及其附加状态/重存均通过 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（实体旧数据兼容后） | 成功 | 62/62 required tests passed；稻草人手/甲 handler 与稀疏槽、旧鹦鹉 Variant/Health/UUID 实际创建、投掷包子非默认旧 `Item`、座椅整数 `SitType` 均保存重载 | VERIFIED |
| 2026-07-15 | Forge/Fabric 九类 recipe serializer 与旧生成 JSON 逐字段对照 | 发现回归 | 26.2 `Ingredient.CODEC` 不接受旧对象形状；旧结果普遍使用 `item` 而非 `id`；Flex 锅缺省 carrier 应为空；Stockpot 的 27 个内置旧配方依赖 `empty_carrier:true` 且其优先于 `carrier`；Teapot 缂省 ingredient 应为空 | IN_PROGRESS |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（首版配方兼容实现） | 失败 | 模组入口静态初始化失败：26.2 `Ingredient` 构造器对空 HolderSet 抛出 `UnsupportedOperationException: Ingredients can't be empty`；证明旧 `Ingredient.EMPTY` 必须迁移为显式 Optional 语义，不能伪造空 Ingredient | IN_PROGRESS |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（Optional 修正后） | 62/63 通过 | 2046 个现有配方全部加载；唯一失败为旧 Ingredient 数组同时含 item 与 tag 时，26.2 HolderSet codec 要求同质集合并将 `#minecraft:planks` 错误回退为 item ID；兼容层需逐分支解析后合并具体 holder | IN_PROGRESS |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（旧配方 JSON 完整修正后） | 成功 | 63/63 required tests passed；2046 个当前配方加载；九类 serializer 均读取 Forge 对象/数组 Ingredient 与 `result.item`；item/tag 混合 OR、FlexPot 空 carrier、Stockpot `empty_carrier` 优先级、Teapot 空 ingredient 及稳定重编码均通过 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat compileGametestJava runGameTest --console=plain`（配方 StreamCodec 往返） | 成功 | 63/63 required tests passed；FlexPot/Stockpot 空 carrier 与 Teapot 空 ingredient 经 `RegistryFriendlyByteBuf` 编码解码后保持为空；2046 个当前配方加载 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat compileGametestJava --console=plain`、`python scripts/verify_network_safety.py` | 成功 | 网络/茶壶测试编译；2 个 serverbound unit payload、权威玩家/字段、1 tick 限流与 MISS 点击边界静态检查通过 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（网络测试首轮） | 失败 | 66/67 通过；`makeMockServerPlayer` 无连接，公开 `addEffect` 尝试同步效果包时 NPE；属于测试夹具限制，尚未执行被测处理器 | AUDITED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（效果夹具修正后） | 失败 | 66/67 通过；原版 `ServerItemCooldowns` 向无连接的 mock player 同步冷却包时 NPE；生产逻辑已生成实体，但测试不能继续验证消费 | AUDITED |
| 2026-07-15 | `.\gradlew.bat runGameTest --console=plain`（mock 冷却夹具修正后） | 成功 | Minecraft 26.2 / Loader 0.19.3 / Java 25；67/67 required tests passed；2046 配方加载；网络 unit codec/权威校验/同 tick 限流/生成消费与茶壶装液/清空/实体倾倒全部通过 | VERIFIED |
| 2026-07-15 | 全部 11 个 `python scripts/verify_*.py`（首轮） | 7 成功 / 4 失败 | BE 脚本仍期望磨石旧单输出字段；客户端脚本未识别 `recipe_block` 归一 renderer；release hygiene 命中兼容导入日志；服务端脚本仍断言完整公式恢复前的临时护盾算法 | AUDITED |
| 2026-07-15 | 失败的 4 个静态脚本修正后复跑 | 成功 | BE 16 类型/15 有状态；14 个 BE renderer；2578 文本无禁止框架残留；common/server 无客户端 import，9 个服务端事件与 2 个交互事件边界通过 | VERIFIED |
| 2026-07-15 | 全部 11 个静态脚本汇总 | 成功 | block entities、client assets、client recipe lookup、datapack、mixins/AW、network、pack metadata、recipes、release hygiene、resources、server boundary 全部通过 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat clean compileJava compileClientJava test build --console=plain` | 成功 | clean 后核心/客户端/GameTest 编译、AW、check、jar/build 全通过；`test` 为 NO-SOURCE；build 挂接的 GameTest 67/67 通过，2046 配方加载 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runDatagen --console=plain` | 成功 | Java 25 / MC 26.2 / Loader 0.19.3；Advancements、Loot Tables、Recipes 三个 provider 全部完成 | VERIFIED |
| 2026-07-15 | release jar 条目、`jdeps`、runtimeClasspath `dependencyInsight` | 成功 | jar 3,356,091 bytes / 3427 entries / SHA-256 `AA0DB49927325C4748DFE7BE52EF3F93B8DF55CE5075536DEC1DBEF078571446`；无 Forge/NeoForge metadata/classes、无 Architectury 字节码依赖；runtimeClasspath 对三者均无匹配 | VERIFIED |
| 2026-07-15 | clientCompileClasspath `dependencyInsight --dependency architectury` | 发现待办 | 仍有显式 `architectury-api:21.0.3+fabric` compile-only 声明；不进入运行产物，但须尝试移除并验证 REI 客户端编译 | IN_PROGRESS |
| 2026-07-15 | Stockpot datagen 19 个新增 `carrier` 字段与 Forge 基线逐例抽查 | 成功 | Forge 对应配方均显式 `{item:"minecraft:bowl"}`；26.2 稳定编码为 `"minecraft:bowl"`，修复旧 Fabric 生成物漏 carrier 的玩法回归 | VERIFIED |
| 2026-07-15 | 移除 Architectury `clientCompileOnly` 后 `compileClientJava` | 失败（依赖边界探针） | REI 26.2 `EntryStacks.of` 公共类签名含 `dev.architectury.fluid.FluidStack`，item-only 调用也需 javac 解析；证明该依赖由 REI API 强制，而非核心迁移抽象 | AUDITED |
| 2026-07-15 | 恢复 REI 专用 Architectury `clientCompileOnly` 后 `compileClientJava` | 成功 | 源码无 Architectury import；仅第三方 REI 编译签名可见，runtimeClasspath/jar/jdeps 均无依赖 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat build --console=plain`（最新 datagen/依赖边界后） | 成功 | 67/67 GameTest、2046 配方、check/AW/jar/build 再次通过 | VERIFIED |
| 2026-07-15 | 普通 `.\gradlew.bat runServer --args nogui` 首次启动 | 预期停于 EULA | Loader/MC/43 mods 已完成类加载，开发运行目录缺少 EULA 同意；仅修改仓库 `run/eula.txt`，未触碰外部实例 | AUDITED |
| 2026-07-15 | 延迟 `stop` 的普通 `runServer` 新世界启动 | 成功 | Java 25 / MC 26.2 / Loader 0.19.3；创建 `run/world`、加载 2046 配方、到达 `Done (1.770s)`，三维度保存并正常退出 | VERIFIED |
| 2026-07-15 | 同一 `run/world` 第二次普通专服启动 | 成功 | 保存后的新世界再次到达 `Done (0.358s)`，无注册表/客户端类加载错误，再次三维度保存并正常退出 | VERIFIED |
| 2026-07-15 | `.\gradlew.bat runClient --console=plain`（不安装 JEI/REI/Jade） | 成功后人工终止 | MC 26.2 / Loader 0.19.3 / Java 25；窗口 `Minecraft* 26.2`，OpenGL 3.3/NVIDIA、Indigo、资源重载、item render replacer、OpenAL 与图集均完成；无 crash report | VERIFIED |
| 2026-07-15 | 客户端进程清理 | 成功 | 终止本次 Gradle/client 三个 PID 后复查 `No Java processes remain`；未打开或修改世界 | VERIFIED |
| 2026-07-15 | 目标 PCL2 实例只读配置/日志审计 | 成功 | 独立专服布局；launcher 1.1.1、MC 26.2、Loader 0.19.3、Java 25.0.2；最新日志到达 `Done`；mods 为 Fabric API、旧 Cookery jar、Aether、Twilight Forest | AUDITED |
| 2026-07-15 | 目标实例模组产物对照 | 发现待部署 | 目标 Cookery jar SHA-256 `D43160817A6081AE72C5A5AA4D70013542D482332474659D8E13A1D76894B7CB`，当前构建为 `ACC9242EDFB41AF2FC0300208DE2F1FD496A5FF4C73E5ACF8332DC22B7DD6699`，须备份旧 jar 后部署测试 | IN_PROGRESS |
| 2026-07-15 | 目标实例 12 个 `level.dat` 只读 NBT 审计 | 当时未找到旧世界，后续已由用户指定样本解除 | 全部 DataVersion 4903 / Version 26.2 / WasModded=1 / ServerBrands `fabric`，启用 Fabric Cookery 数据包且无 Forge/FML/NeoForge 顶层键；2026-07-16 用户另行指定的 26.1.2 Fabric 世界已完成适配 | AUDITED |
| 2026-07-15 | 目标旧 jar 备份、当前 jar 部署与 `world` 副本创建 | 成功 | 备份 `codex-smoke-mods/...pre-20260715-1618.jar` hash 为旧 `D431...B7CB`；mods jar hash 为当前 `ACC9...6699`；只复制原 `world` 到 `codex-current-20260715-1618`，原件未启动 | VERIFIED |
| 2026-07-15 | 目标实例完整 44-mod 栈首次启动副本 | 成功 | Aether + Twilight Forest + 当前 Cookery；2907 配方/2689 进度/92 biome；25566 到达 `Done (0.366s)`，原版/Aether/暮色五维度保存并正常退出 | VERIFIED |
| 2026-07-15 | 目标实例同一副本二次加载 | 成功 | 再次到达 `Done (0.388s)`，五维度再次保存并正常退出；无 Cookery 注册、配方、类加载或存档错误 | VERIFIED |
| 2026-07-15 | Forge/NeoForge `ModCreativeTabs` / `ModTradesEvent` 与当前 26.2 实现逐项对照 | 发现并修复回归 | 主栏错误加入空杯、工具/标签顺序变化；12 个菜谱交易缺失，三级池被替换且四级池额外加入一项；26.2 附魔物品的 additional-cost 机制与 Forge 动态价格等价 | IN_PROGRESS |
| 2026-07-15 | `compileJava processResources`、`verify_data_pack.py`、`verify_client_assets.py`（创造栏/交易恢复后） | 成功 | 5 个 trade set、35 个 trade 文件；精确锁定 6/7/4/14/1 五级池、12 个 `recipe_record` 输入输出与创造栏顺序；Java 编译通过 | AUDITED |
| 2026-07-15 | `runGameTest`（首版交易组件恢复） | 失败（已定位） | 12 个交易在动态 `villager_trade` registry bootstrap 时直接解码 `RecipeRecord`；其内部 `ItemStack.CODEC` 早于物品默认组件完成，全部报 `Item ... does not have components yet` | IN_PROGRESS |
| 2026-07-15 16:53 | 12 个菜谱交易延迟组件实现与 `git diff --check` | 实现完成、待运行 | 注册 `kaleidoscope_cookery:set_recipe_record`，交易 JSON bootstrap 仅解码 item ID/type；全部 12 个交易改用单一 `given_item_modifiers`，静态契约同步到新结构 | IN_PROGRESS |
| 2026-07-15 16:54 | `compileJava`、`verify_data_pack.py`、`verify_client_assets.py` | 成功 | Java 编译通过；2 个 loot function、35 个交易文件及 5 个精确交易池通过；创造栏分组/相对顺序和客户端资源契约通过 | AUDITED |
| 2026-07-15 16:56 | `runGameTest`（延迟菜谱组件） | 成功 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；动态交易注册表完成 bootstrap，2046 配方加载，67/67 required tests passed | AUDITED |
| 2026-07-15 17:00 | `compileGametestJava`（实际菜谱报价夹具） | 成功 | 26.2 动态 `VILLAGER_TRADE` 注册表、LootContext 与 `VillagerTrade#getOffer` 行为测试编译通过；待运行第 68 项测试 | AUDITED |
| 2026-07-15 17:02 | `runGameTest`（实际菜谱报价夹具首轮） | 67/68 通过 | 唯一失败发生在调用交易前：`VILLAGER_TRADE` LootContext 拒绝测试夹具加入的 `minecraft:tool` 参数；移除不允许参数后复跑，生产交易实现无失败 | AUDITED |
| 2026-07-15 17:03 | `runGameTest`（报价夹具上下文探针） | 67/68 通过 | 移除 tool 后确认 `minecraft:additional_cost_component_allowed` 是必需参数；反编译 26.2 `AbstractVillager#addOffersFromTradeSet` 证明原版传入 `Unit.INSTANCE`，夹具据此对齐 | AUDITED |
| 2026-07-15 17:05 | `runGameTest`（实际菜谱报价最终夹具） | 成功 | 68/68 required tests passed；动态注册表真实 `VillagerTrade#getOffer` 生成报价，验证 3 绿宝石、16 次、4 XP、`RECIPE_ITEM`、4 个有序单件输入、braised beef 输出与 pot 类型 | VERIFIED |
| 2026-07-15 17:23 | `compileJava compileClientJava compileGametestJava`、`runGameTest`（客户端 tooltip 配置/弱集成） | 成功 | 三个 source set 编译；69/69 required tests passed，锁定旧 client TOML、默认/非法值、用户关闭和外部 `foodeffecttooltips` 抑制分支 | VERIFIED |
| 2026-07-15 17:28 | `-PfarmersDelightCompatSmoke=true runGameTest`（首轮测试依赖探针） | 失败（未启动游戏） | Loom 1.15.5 当前配置无旧式 `modRuntimeOnly` dependency method，Gradle 在 build.gradle:90 配置阶段失败；须改用本工程可用 runtime 配置后复跑 | AUDITED |
| 2026-07-15 17:32 | `dependencyInsight` + `-PfarmersDelightCompatSmoke=true runGameTest` | 成功 | runtimeOnly 仅在显式开关下解析 Farmer's Delight Refabricated 26.2-3.6.7；Loader 45 mods、2356 配方、70/70 tests；真实 beef stew 输入/输出/时间/碗 carrier 及汤锅匹配通过 | AUDITED |
| 2026-07-15 17:40 | `python scripts/verify_data_pack.py`、`compileGametestJava` | 成功 | Carry On blacklist 精确覆盖 116 个当前注册方块；可选标签运行测试编译通过 | AUDITED |
| 2026-07-15 17:40 | `-PsereneSeasonsCompatSmoke=true runGameTest` | 失败（第三方阻塞） | Modrinth 26.2 标记文件的内部 manifest 实为 `minecraft=26.1.2` 且缺 `glitchcore>=26.1.2.0.0`；Fabric Loader 0.19.3 在测试启动前拒绝，Cookery 代码未执行 | BLOCKED |
| 2026-07-15 17:46 | `-PclientCompatSmoke=true runClient`（首轮完整可选客户端栈） | 失败 | 59 mods 中 JEI/REI/Jade/Food Effect Tooltips/FD 均加载，Cookery REI plugin 已注册；Architectury 21.0.3 调用 Fabric API 0.153 已删除的 `ScreenKeyboardEvents.allowCharType`，初始化期 `NoSuchMethodError`；本次新进程清理后 0 残留 | IN_PROGRESS |
| 2026-07-15 17:48 | Architectury 21.0.4 下 `compileClientJava` + 完整栈 `runClient` | 编译成功 / Loader 拒绝 | 21.0.4 修复旧调用但 manifest 要求 Fabric API >=0.154.0，默认目标为 0.153.0；无 crash report且新进程清理 0 残留。仅 REI 安装态 smoke 应升级其运行前置，主模组最低版本不应被无条件抬高 | IN_PROGRESS |
| 2026-07-15 17:55 | `-PclientCompatSmoke=true runClient`（21.0.4 + smoke-only Fabric API 0.154.0） | 成功后正常关闭 | MC 26.2 / Loader 0.19.3 / Java 25；59 mods；JEI/REI/Jade/Food Effect Tooltips/Farmer's Delight 均加载，Cookery REI/Jade 插件注册；OpenGL、资源重载、OpenAL、原版/方块/物品/GUI/JEI 全部图集完成；无新 crash report，进程正常关闭后 0 Java 残留。开发账号 Mojang/Realms 401 与模组无关 | VERIFIED |
| 2026-07-15 17:57 | `.\gradlew.bat runGameTest --console=plain`（Carry On 标签测试加入后） | 成功 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；2046 recipes；71/71 required tests passed；测试服完成三维度保存并关闭 | VERIFIED |
| 2026-07-15 17:58 | `.\gradlew.bat -PfarmersDelightCompatSmoke=true runGameTest --console=plain` | 成功 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；Farmer's Delight 3.6.7；45 mods / 2356 recipes / 2070 advancements；71/71 required tests passed；测试服完成三维度保存并关闭 | VERIFIED |
| 2026-07-15 18:05 | `compileJava compileGametestJava`、`verify_mixins.py`、`runGameTest`（生物桶标签过滤恢复） | 成功 | 26.2 `MobBucketItem.type` shadow 后经实体注册表 holder 检查 `rice_growth_booster`；静态契约锁定调用，运行标签验证 cod=true/cow=false；9 common + 1 client Mixin / 3 AW 项通过；71/71 GameTest | VERIFIED |
| 2026-07-15 18:09 | `compileJava compileGametestJava`、`verify_mixins.py`、`runGameTest`（油块非粘性恢复） | 成功 | 删除错误的 `isSticky=true` 注入；`canStickToEachOther` 对任一侧油块返回 false；反射 26.2 实际私有判定验证油块非粘性且与石头/史莱姆/蜂蜜双向不粘；72/72 GameTest | VERIFIED |
| 2026-07-15 18:14 | `compileJava compileGametestJava`、`verify_mixins.py`、`verify_server_boundary.py`、`runGameTest`（活力效果精确恢复） | 成功 | `VigorEffect` 每效果 tick 在冲刺时将总 exhaustion 设为 0；最小 `FoodDataAccessor` 取代 `ServerPlayer` ordinal redirect；非冲刺 exhaustion 保留、无效果原版冲刺消耗仍存在；72/72 GameTest | VERIFIED |
| 2026-07-15 18:19 | `compileJava compileGametestJava`、`verify_mixins.py`、`verify_server_boundary.py`、`runGameTest`（稻草人农田路径） | 成功 | `FarmBlockMixin` 仅对非空实体应用 16 格稻草人保护；实际 `FarmlandBlock.turnToDirt` 验证同一稻草人附近 entity=null 仍退化、实体调用仍保留耕地；73/73 GameTest | VERIFIED |
| 2026-07-15 18:31 | Forge `1d935a2c`、NeoForge `upstream/1.21.1-neoforge` 与 26.2 字节码对照（剩余 Mixin/AW） | 审计完成，测试实现中 | 面粉仍为 10 tick 水中等量转换；寒带速度公式、粉雪条件、弹射物 200 tick/3 格/16 次与姿势角度一致。26.2 坠落方块前两个失败落地方支在掉落前已销毁实体，超时分支在掉落后才销毁，因此 Fabric 回调取消原方法时必须显式 `discard()`；3 个 AW 精确替代基线 `templates` 可访问和 `rawTemplates` 可访问/可写，`SERVER_STARTING` 对应 Forge/NeoForge `ServerAboutToStartEvent` | IN_PROGRESS |
| 2026-07-15 18:32 | `verify_mixins.py`、`compileJava compileClientJava compileGametestJava`、`runGameTest`（剩余 Mixin/AW） | 成功 | 静态校验锁定 9 common + 1 client Mixin、3 个精确 AW、注入点/公式/角度/效果字段；MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25 加载 2046 配方，78/78 required tests passed。实际验证面粉 10 tick 等量转换、蒸笼超时实体销毁及双层物品/进度、寒带速度与粉雪、弹射物跳过碰撞并保留效果标志、五类村庄池各 4 展开权重 + 1 原始权重项 | VERIFIED |
| 2026-07-15 18:43 | Forge/NeoForge 自动订阅事件与当前 Fabric 注册首轮对照 | 发现四项回归 | 保鲜在 use-start 而非 finish 触发且错误依赖 5 种食物标签；农夫套装遗漏非玩家 LivingEntity 并用 server tick；垃圾桶只清 32 格内既有目标；第一人称 pitch 与玩家隐藏两个客户端事件完全缺失 | IN_PROGRESS |
| 2026-07-15 18:56 | `verify_mixins.py`、`verify_server_boundary.py`、三 source set 编译、`runGameTest`（事件回归首轮） | 静态/编译成功，80/81 GameTest | 保鲜完成消费和垃圾桶真实 `Mob.setTarget` 均通过；农夫套装夹具手工调用 `tick()` 前设为 19，但 26.2 的 `tickCount` 由服务器调度器在实体 `tick()` 外更新，生产 handler 正确跳过，须将夹具对齐为进入第 20 tick 的状态后复跑 | IN_PROGRESS |
| 2026-07-15 18:59 | `runGameTest`（农夫套装夹具对齐 26.2 调度） | 成功 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；2046 recipes；81/81 required tests。完成消费后保鲜仅移除食物声明的有害效果，非玩家第 20 tick 四件套/缺件控制、垃圾桶隐藏/可见/保留旧目标均通过 | VERIFIED |
| 2026-07-15 19:01 | `runClient`（垃圾桶 client Mixin 首轮） | 失败（已定位） | Loader/Indigo/OpenGL 初始化后应用 `CameraMixin` 时失败：26.2 `Camera` 的公开 yaw getter 实名为 `yRot()`，首版错误 shadow `getYRot()`；客户端在资源重载前退出并生成 crash report，无残留本项目 Java 进程 | IN_PROGRESS |
| 2026-07-15 19:03 | `compileClientJava`、`verify_mixins.py`、普通 `runClient`（修正 `Camera.yRot()`） | 成功后人工终止 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；4 个 client Mixin 全部应用；Indigo/OpenGL、资源重载、item render replacer、OpenAL、blocks/items/gui 等图集完成，无新 crash；仅开发账号/Realms 401；本次项目 Java 进程清理后为 0 | VERIFIED |
| 2026-07-15 19:14 | `compileJava`、`compileGametestJava`、`verify_recipes.py`、`verify_server_boundary.py`、`runGameTest`（通用配方容器） | 成功 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；2046 recipes；82/82 required tests。真实玩家槽位 shulker 内容可被菜谱计数，扣 2 个苹果后事务回写为 1，锅收到 2 份原料；静态契约锁定 mutable slot context 且禁止常量上下文 | VERIFIED |
| 2026-07-15 19:21 | `compileJava compileGametestJava`、`verify_server_boundary.py`、`runGameTest`（果篮持物交互） | 成功 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；2046 recipes；83/83 required tests。Fabric `UseBlockCallback` 对普通持物/顶面方块取出返回 consume，非顶面方块、调试棒、烟花返回 pass 且内容不变 | VERIFIED |
| 2026-07-15 19:24 | `compileClientJava`（放屁输入门禁首轮） | 失败（已定位） | Forge 1.20.1 的 `Minecraft.getOverlay()` / 公开 `screen` 在 26.2 已迁到 `Minecraft.gui.overlay()` / `gui.screen()`；静态字符串脚本首轮未发现 API 失配，已按 26.2 `Gui` 字节码公开 accessor 纠正 | IN_PROGRESS |
| 2026-07-15 19:31 | `python scripts/verify_network_safety.py`、`compileClientJava`（胀气输入门禁修正） | 成功 | 2 个 C2S payload 静态边界通过；`FlatulenceClientEvent` 在 26.2 映射下成功编译，overlay、screen、鼠标捕获、窗口激活四项门控与 key-edge 更新顺序均锁定 | VERIFIED |
| 2026-07-15 19:46 | Forge `ClientSetupEvent` / `ModModelEvent` / tooltip / entity renderer / particle provider 与当前客户端入口集合对照 | 发现两项回归 | 7 个旧 item property 已由 26.2 item-model 条件等价替代，3 个锅内附加模型由 `ITEM_MODEL` 组件和 `assets/items` 替代，14 BE renderer、3 entity renderer、6 layer、2 tooltip、2 particle 集合一致；但可选旧材质包 1400 个资源及入口整体缺失，两个 HUD 锚点和锅提示动作栏避让未保持；同路径复算基线有 357 个 block model 声明 `render_type`，当前为 389 | IN_PROGRESS |
| 2026-07-15 19:50 | `verify_client_assets.py`、`verify_mixins.py`、`compileClientJava processResources`、普通及首轮遗留包 `runClient` | 静态/编译/普通客户端成功；遗留包发现模型警告 | 1403 个机械恢复资产、资源格式 88..107、Fabric `NORMAL` 注册及 HUD 第五个 client Mixin 均应用；启用日志确认遗留包排在主 Cookery 包之后，但暴露 80 个切菜板模型和 4 个特殊 item model 缺失 particle/Forge loader 兼容警告 | IN_PROGRESS |
| 2026-07-15 20:01 | 遗留模型 26.2 转换、`python scripts/verify_client_assets.py`、`compileClientJava processResources`、`git diff --check` | 成功 | 81 个无父显式纹理模型全部具有 particle；`forge:separate_transforms` 为 0；火腿片/果篮/茶壶用精确 Forge 视角集合选择 2D 模型并保留 3D fallback；1406 资产、839 模型、389 render types | VERIFIED |
| 2026-07-15 20:02 | 启用 `kaleidoscope_cookery:legacy_resources_pack` 的 `runClient` | 成功后人工终止 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；资源管理器顺序以主 Cookery 后的遗留包结尾；item model codec、item render replacer、OpenAL 及 particles/blocks/items/gui 等全部主要图集完成；Cookery 模型/纹理/资源 WARN/ERROR=0，仅开发账号 Mojang/Realms 两条 401；选项恢复为空且本项目进程清理为 0 | VERIFIED |
| 2026-07-15 20:25 | `runGameTest`（common 事件首轮） | 88/90 | AI、湿地锄地、阻碍、活力、即时冶炼算法和额外 loot 均通过；两项 mock `ServerPlayer` 因无 connection 在 `addEffect/removeEffect` 发包处 NPE，属于夹具入口错误 | IN_PROGRESS |
| 2026-07-15 20:27 | `verify_mixins.py`、`verify_server_boundary.py`、`compileJava compileGametestJava`、`runGameTest` | 成功 | 11 common + 5 client Mixin、3 AW；MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25 加载 2046 配方，90/90 required tests passed；真实验证 AI 去重、湿地锄地、Hinder、Vitality、即时冶炼余量、厨刀 loot 及旧 ForgeData 胀气起点/清理/整数坐标 | VERIFIED |
| 2026-07-15 20:38 | `verify_resources.py`、`verify_client_assets.py`、`processResources`、`git diff --check`（主资源规范化补齐） | 成功 | 恢复 `es_es`（含 5 个当前基线键）、Hinder/Instant Smelting/Projectile Dodge/Vitality 四个效果图标及 6 个主包 Ponder NBT；锁定 204 items、116 blocks、12/12 effects/textures、10 languages、6 main-pack Ponder scenes；遗留包保持 1406 assets/839 models | VERIFIED |
| 2026-07-15 20:53 | `compileDatagenJava`、`runDatagen`、`verify_recipes.py`（基线配方全集恢复） | 成功 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；恢复规范化对照中的 163 个缺失 ID（138 pot/17 stockpot/8 shapeless），修复 15 个空 ingredients 配方；624 recipe files、250 pot、72 stockpot；验证器锁定数量输入/输出、`c:grain/rice`、三组 1..9 空 carrier 与旧无序合成原料 | IN_PROGRESS |
| 2026-07-15 20:56 | 全部 `verify_*.py` 首轮、`compileJava compileClientJava compileGametestJava` | 三类编译成功；静态套件第 9 项失败（已定位） | 前 8 项含配方验证全部成功；`verify_release_hygiene.py` 把仅位于 `clientCompatSmoke=true` 可选运行夹具中的 Cloth Config 误判为生产依赖。该 runtime-only 条目是已验证 REI 客户端栈所需，不能删除；验证器需限定到生产配置后重跑 | IN_PROGRESS |
| 2026-07-15 20:58 | 全部 11 个 `verify_*.py`（修正可选运行依赖边界后） | 成功 | block entities/client assets/client recipe lookup/data pack/mixins/network/pack metadata/recipes/release hygiene/resources/server boundary 同轮通过；发布卫生扫描 3741 个文本文件，Cloth Config 仅允许位于 `clientCompatSmoke=true` runtime-only 条件块 | VERIFIED |
| 2026-07-15 21:00 | `runGameTest`（163 个基线配方恢复） | 成功 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；实际加载 2209 recipes（此前 2046，精确增加 163）与 1856 advancements；90/90 required tests passed，三维度保存并正常关闭 | VERIFIED |
| 2026-07-15 21:10 | `compileDatagenJava`、`runDatagen`、`verify_resources.py`、`verify_server_boundary.py`、`compileGametestJava`、`runGameTest`（11 张盘装 loot） | 成功 | 恢复 11 张 bowl + `survives_explosion` 表；104 block loot / 12 custom drops；PlateBlock 从 super 取旧碗后叠加 servings。真实完整苹果盘掉 1 bowl + 4 apples，空盘只掉 1 bowl；2209 recipes，91/91 required tests，三维度保存关闭 | VERIFIED |
| 2026-07-15 21:18 | `verify_client_assets.py`、`processResources`、`compileClientJava`（两个基线 PNG 最终验证） | 成功 | 主包 `textures/gui/jei/teapot.png` 与 `textures/block/stone_bricks.png` 存在且 SHA-256 精确锁定为 `480c7610...` / `24415444...`；客户端纹理引用扫描覆盖整个 client source set（含 `compat/jei`） | VERIFIED |
| 2026-07-15 21:28 | `verify_client_assets.py`、`verify_resources.py`、定向 `git diff --check`（四个模型差异分类与作物修复） | 成功 | 主包精确恢复基线 `cross`；24 个辣椒/生菜/番茄阶段使用 `y=-1..15` 父模型，24 个水稻阶段保持 vanilla `cross`；11 种桌子的 352 个状态完整使用标准模型 `y=180/270` 替代旧 `*_rot`，资源仍为 204 items / 116 blocks | VERIFIED |
| 2026-07-15 21:29 | `processResources compileClientJava`（作物模型修复） | 成功 | Loom 1.15.5；资源实际合并成功，common/client classes 均通过，`compileClientJava` 无需重编且整体 BUILD SUCCESSFUL | VERIFIED |
| 2026-07-15 21:38 | `compileGametestJava runGameTest`（common/FTB 标签运行夹具首轮） | 编译失败（已定位） | 新断言把注册 ID `rice` 误写为不存在的 `ModItems.RICE`；真实字段为 `RICE_SEED`。失败发生在 GameTest 启动前，生产资源与前四项静态验证均已通过 | IN_PROGRESS |
| 2026-07-15 21:39 | `compileGametestJava runGameTest`（修正标签运行夹具） | 成功 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；加载 2209 recipes / 1856 advancements；实际验证煎蛋、碎生肉、野生稻/稻米、四把厨刀及两张 FTB 水稻标签；91/91 required tests，三维度保存关闭 | VERIFIED |
| 2026-07-15 21:48 | `compileJava compileDatagenJava runDatagen processResources compileGametestJava runGameTest`（旧 advancement / 草帽 seed loot） | 成功 | 三个 provider 完成；旧 recipe advancement 别名使实际加载量从 1856 精确增至 1859，配方保持 2209；common 与 GameTest 编译、资源合并成功，91/91 required tests，三维度保存关闭 | VERIFIED |
| 2026-07-15 21:54 | 全部 11 个 `verify_*.py`、全树 `git diff --check`、Forge 基线路径规范化复算 | 成功 | 11/11 静态验证同轮通过；release hygiene 扫描 3760 个文本文件；`git diff --check` 退出码 0，仅有既存 LF→CRLF 提示。规范化资源为基线 2402、当前 2927，基线仅有 52：36 个桌子 `*_rot` 模型 + 16 个 Forge GLM/辅助 loot 文件，未分类项 0 | VERIFIED |
| 2026-07-15 22:14 | `compileJava`、`compileGametestJava`（Transfer capability / 厨师礼物首轮） | 生产编译成功；测试编译失败（已定位） | 茶壶通用 FluidStorage、油壶 ItemStorage 与厨师礼物 Mixin 的 common 编译成功；新油壶夹具把 lookup 的静态类型 `Storage<ItemVariant>` 当成单槽类型调用 `getAmount()`，两处符号不存在。生产实现无错误，夹具将先验证 lookup 身份再用具体 storage 检查金额 | IN_PROGRESS |
| 2026-07-15 22:17 | `runGameTest`（Transfer capability / 厨师礼物首轮运行） | 93/94，失败已定位 | 油壶事务与厨师礼物映射通过；通用茶壶夹具的不足一桶拒绝路径向无 connection 的 mock `ServerPlayer` 发 action-bar，夹具入口错误导致 NPE。关服还暴露空茶壶把 `0 minecraft:air` 写入 `Input/Result` 的 26.2 codec 警告；改用非玩家 LivingEntity 测拒绝路径并仅保存非空栈 | IN_PROGRESS |
| 2026-07-15 22:20 | `verify_block_entities.py`、`verify_server_boundary.py`、`compileGametestJava`（空茶壶保存修正首轮） | 两个静态脚本成功；测试编译失败（已定位） | 空栈保护与 Transfer 静态契约均通过；夹具补丁上下文误命中相邻旧水桶测试，造成旧方法缺 `player`、新方法缺 `user` 共 13 个符号错误。仅测试局部变量声明受影响，按方法边界精确修正 | IN_PROGRESS |
| 2026-07-15 22:24 | `compileJava compileClientJava compileGametestJava processResources runGameTest`（Capability/DataMap 首轮收口） | 成功 | MC 26.2 / Loader 0.19.3 / Fabric API 0.153.0 / Java 25；2209 recipes / 1859 advancements；通用茶壶 FluidStorage、油壶事务与厨师英雄礼物通过，空茶壶 codec 警告消失；94/94 required tests，三维度保存关闭 | VERIFIED |
| 2026-07-15 22:25 | 全部 11 个 `verify_*.py`、`compileJava compileClientJava processResources`、全树 `git diff --check` | 成功 | 11/11 静态验证同轮通过；release hygiene 扫描 3762 个文本文件；common/client/resource 编译处理成功；`git diff --check` 退出码 0，仅既存 LF→CRLF 提示 | VERIFIED |
| 2026-07-15 22:37 | `runGameTest`（磨盘实体 lookup 首轮） | 94/95，夹具值已定位 | 新 lookup、通用 provider 与生产逻辑已启动；夹具误把实际存在 `cobblestone_from_stone` 磨盘配方的石头当无效输入，实体存储按设计成功供料而未走掉落物回退 | AUDITED |
| 2026-07-15 22:39 | `compileJava compileGametestJava`、`verify_server_boundary.py`、`runGameTest`（修正夹具） | 成功 | 无效输入改为配方全集中不存在的基岩；第三方 cow storage 的拒绝提取回滚、掉落小麦回退及有效 storage 11→3 均通过；95/95 required tests | VERIFIED |
| 2026-07-15 22:42 | 26.2 `AbstractHorse` 字节码、`compileJava compileGametestJava`、`verify_server_boundary.py`、`runGameTest`（原版带箱马适配） | 成功 | 发现实体槽 ID 必须为 `INVENTORY_SLOT_OFFSET(500)+inventoryIndex`，修复旧 Fabric 0..N 错误；真实带箱驴库存未提交 11→11、提交 11→3；2209 recipes / 1859 advancements，95/95，三维度保存关闭 | VERIFIED |
| 2026-07-15 22:44 | `clean compileJava compileClientJava test build runDatagen runGameTest`（最终 clean-state 首轮） | 失败（已定位并修复） | Gradle 9.4 检测到同一组合命令中 `sourcesJar` 使用 `runDatagen` 输出却无声明顺序；不是源码、数据或 GameTest 失败。为 `sourcesJar` 增加对 `runDatagen` 的 `mustRunAfter`，仅在两任务同时被请求时排序，不使普通 build 隐式执行 datagen | AUDITED |
| 2026-07-15 22:47 | `.\gradlew.bat clean compileJava compileClientJava test build runDatagen runGameTest --console=plain`（排序修正后最终 clean-state 复跑） | 成功 | common/client 编译、check、AW、jar/build 全通过；`test` 为 `NO-SOURCE`；Advancements/Loot Tables/Recipes 三个 datagen provider 全部完成；2209 recipes / 1859 advancements；95/95 required GameTest，三维度保存并正常关闭 | VERIFIED |
| 2026-07-15 22:49 | 全部 11 个 `scripts/verify_*.py`、release jar、`jdeps`、`runtimeClasspath` 复核 | 成功 | 11/11 静态验证通过；最终 jar 5,416,338 bytes / 5224 entries / SHA-256 `BF639A4E49F2D31D4163CCA8CAB1AE26DD38BFEF5B4E1C17A4748386CCD247C9`；无 Forge/NeoForge 类或元数据、无 Architectury 条目或字节码依赖；runtimeClasspath 无 Forge/NeoForge/Architectury 依赖；`git diff --check` 退出码 0，仅既存 LF→CRLF 提示 | VERIFIED |
| 2026-07-15 22:56 | PID 27624 `jcmd VM.system_properties`、目标实例进程/端口/mods/log 只读检查 | 成功 | PID 27624 的 `user.dir` 与 `fabric.gameJarPath` 均指向外部 `The-Aether-fabric\build\integration\client-rendering-server-20260715`，监听 25584/25585 且有活动连接，不属于目标 PCL2 实例并保持不动；目标实例无运行进程，mods 中 Cookery 仍为 3,356,342-byte `ACC924...6699` 产物，可安全备份后替换 | VERIFIED |
| 2026-07-15 22:57 | 目标旧 jar 备份、最终 jar 部署与 `world` 只读副本创建 | 成功 | 目标 `ACC924...6699` jar 已备份到 `codex-smoke-mods/...pre-final-20260715-2257.jar` 并逐字节哈希复核；mods 已部署最终 `BF639...247C9`；原 `world` 仅复制为新 `codex-final-20260715-2257`，原件和 `migration-smoke-20260715-drying-rack` 均未启动或修改 | VERIFIED |
| 2026-07-15 22:58 | 目标实例最终 `BF639...247C9`、Aether、Twilight Forest 完整栈首次启动 `codex-final-20260715-2257` 副本 | 成功 | Java 25 / MC 26.2 / Loader 0.19.3；3071 recipes / 2701 advancements；到达 `Done (0.324s)`，原版/Aether/暮色五维度保存，日志 0 ERROR，服务端退出码 0；完整日志保留为 `codex-server-final-20260715-2257-pass1.log` | VERIFIED |
| 2026-07-15 22:59 | 同一 `codex-final-20260715-2257` 副本二次加载 | 成功 | 最终 jar hash 仍为 `BF639...247C9`；3071 recipes / 2701 advancements；重保存后再次到达 `Done (0.456s)`，五维度保存，日志 0 ERROR，服务端退出码 0；完整日志保留为 `codex-server-final-20260715-2257-pass2.log` | VERIFIED |
| 2026-07-15 23:04-23:10 | 开发客户端 + 目标 Aether/Twilight Forest JAR、`--quickPlayMultiplayer 127.0.0.1:25570` 联机首轮 | 未发起握手（已定位） | 客户端 PID 34852 已加载三模组、OpenGL、资源、OpenAL 和全部主要图集，但 clean 后的新 `run` 目录停在原版首次启动 `Welcome to Minecraft` 无障碍确认页；两轮目标专服均无 ERROR、超时后五维度保存并正常退出。Continue 最终生效时专服已退出，客户端按既定地址得到预期 `Connection refused`，证明参数解析正确并已持久化首次启动状态；客户端随后经窗口关闭正常退出，须在服务端就绪后无门控复跑 | AUDITED |
| 2026-07-15 23:15-23:18 | 无欢迎页门控的目标客户端联机探针 | TCP 已连接，登录包未编码（已定位） | 客户端连接到 `127.0.0.1:25570`，服务端记录连接后断开；客户端原版 `ServerboundHelloPacket` 明确报 `String too big (was 17 characters, max 16)`，测试名 `CodexCookerySmoke` 超过协议上限，尚未进入 Fabric 注册表同步。服务端 0 ERROR、五维度保存，客户端经窗口关闭正常退出；改用 12 字符 `CookerySmoke` 复跑 | AUDITED |
| 2026-07-15 23:21-23:23 | `CookerySmoke` 开发客户端连接最终目标专服 `codex-final-20260715-2257` | 联机成功；副本暴露独立第三方数据风险 | 客户端三模组资源初始化后连接；服务端记录 `logged in with entity id 92` 与 `joined the game`，客户端加载进度并仅因受控关服断开，`runClient` 为 `BUILD SUCCESSFUL`；证明 Cookery/Fabric 握手、注册表和网络同步成功。玩家加载副本出生区块时另有 2 条 recoverable ERROR：目标 mods 未安装 Farmer's Delight，但原 `world` 含 `farmersdelight:wild_beetroots` / `sandy_shrub`，原版用 default 恢复；与 Cookery 无关但该目标世界的第三方数据保存为 `PARTIAL`，原 `world` 未被启动或修改 | PARTIAL |
| 2026-07-15 23:25-23:28 | 最终目标完整栈新建 `codex-final-newworld-20260715-2324` 并由开发客户端联机 | 成功 | 最终 `BF639...247C9` + Aether + Twilight Forest；3071 recipes / 2701 advancements；新世界生成后 `Done (2.470s)`，`CookerySmoke` 完成登录、进入并实际驻留；服务端 0 ERROR、退出码 0、五维度保存，客户端 `runClient` 为 `BUILD SUCCESSFUL` 且正常窗口退出。服务端/客户端证据日志分别为 `codex-server-final-newworld-20260715-2324-client-connect.log` / `codex-client-final-newworld-20260715-2324-connect.log` | VERIFIED |
| 2026-07-15 23:50 | Forge 基线与当前 block property 声明、实际注册参数及 blockstate 逐类机械对照 | 发现待修复回归 | 其余属性名/合法值可一一对应；但全部 `FoodBiteBlock` 丢失 Forge `quality=0..4` 状态，当前 blockstate 也仅枚举 bites/facing。该状态承载放置食品品质、食用倍率、完整食物掉落组件与 Jade 品质显示，旧世界缺失时必须默认 4；本项在修复和运行夹具通过前保持 `IN_PROGRESS` | IN_PROGRESS |
| 2026-07-15 23:54 | `compileJava compileClientJava compileDatagenJava compileGametestJava`（品质状态/丰富旧栈首轮） | 三个生产 source set 成功；测试编译失败（已定位） | 新测试漏 import `FoodBiteBlock`，产生 8 个同源 symbol error；生产、客户端和 datagen 修改均已编译，补测试 import 后原命令复跑 | IN_PROGRESS |
| 2026-07-15 23:55 | `compileJava compileClientJava compileDatagenJava compileGametestJava`（补测试 import） | 成功 | common/client/datagen/GameTest 四 source set 全部通过；仅既有 deprecated API 提示 | VERIFIED |
| 2026-07-15 23:56 | `runDatagen`（食品品质掉落） | 成功 | Advancements/Loot Tables/Recipes 三 provider 全部完成；41 张 FoodBite loot table 对 `quality=0..3` 写入对应 `kaleidoscope_cookery:quality` 组件，缺字段默认 4 继续不写组件 | VERIFIED |
| 2026-07-15 23:57-23:58 | `runGameTest`（方块品质/丰富旧 ItemStack） | 成功 | 2209 recipes / 1859 advancements；96/96 required tests。覆盖全部注册 FoodBite 的 `quality=0..4`/默认 4、放置/掉落/食用倍率、旧品质 NBT 迁移，以及钻石剑 Damage/名称/Sharpness III/自定义 tag 的 DataFix、保存和二次读取 | VERIFIED |
| 2026-07-15 23:59 | 全部 11 个 `verify_*.py`（品质修复首轮） | 10/11 | 前十项通过；`verify_server_boundary.py` 只搜索旧字面量 `player.getFoodData().eat(foodProperties)`，品质实现改用同方法的数值重载后 `.find()` 为 -1，误报进食早于状态更新；生产源码实际仍先确认 `level.setBlock`，验证器需泛化为 `eat(` | IN_PROGRESS |
| 2026-07-16 00:00 | 全部 11 个 `verify_*.py`（进食重载断言泛化后） | 成功 | 11/11；服务端边界继续确认 bite 状态先提交再进食，客户端资产、数据包、配方、资源、Mixin、网络、发布卫生全部通过 | VERIFIED |
| 2026-07-16 00:01 | Forge/当前方块属性集合复算、Forge 玩家持久入口复扫 | 成功 | block 包自定义属性键 31/31、`BlockStateProperties` 引用 23/23，逐文件 diff 均为 0；基线玩家持久数据只有胀气起点两次访问，无玩家 capability/clone/provider/SavedData | VERIFIED |
| 2026-07-16 00:04 | `clean compileJava compileClientJava test build runDatagen runGameTest`、11 个 `verify_*.py`、release jar/`jdeps`/`runtimeClasspath` 最终复核 | 成功 | 同一 clean-state 下 2209 recipes / 1859 advancements、96/96 GameTest、11/11 静态验证；jar 5,424,947 bytes / 5225 entries / SHA-256 `4B6AA7F47847E5C2F0B38BDB54CD784604634041CF4A2C8F91208DDDF985437B`；jar/字节码/运行依赖均无 Forge/NeoForge/Architectury，`git diff --check` 退出码 0（仅既有 LF→CRLF 提示） | VERIFIED |
| 2026-07-16 00:10 | 目标实例最终 jar 备份与部署 | 成功 | 原部署 `BF639...247C9`（5,416,338 bytes）已逐字节备份为 `codex-smoke-mods/kaleidoscopecookery-1.1.0.5-fabric+mc26.2-pre-quality-final-20260716-0008.jar`；目标 `mods` 已部署 `4B6AA...437B`（5,424,947 bytes）并复核哈希。PID 27624 已由既有 `jcmd` 证据确认属于外部 Aether 集成目录，未终止、重启或修改 | VERIFIED |
| 2026-07-16 00:13 | 原目标 `world` 只读复制为 `codex-quality-final-20260716-0010` | 成功 | 复制前后均为 48 files / 13,487,346 bytes，`level.dat` SHA-256 均为 `0E6514A6BE77A36B6706AB82EA30A07AB6B9A146D4A19A4FA55C8123C5717CED`；原 `world` 最新文件时间仍为 2026-07-01 23:30:04，未启动或修改；受保护的 drying-rack 副本未读取写入 | VERIFIED |
| 2026-07-16 00:16-00:18 | 最终 `4B6AA...437B` 目标完整栈启动副本并由三模组客户端联机保存退出 | 成功；复现已知第三方数据风险 | Java 25 / MC 26.2 / Loader 0.19.3，3071 recipes / 2701 advancements；专服 `Done (0.360s)`，客户端加载 53 mods 后 `CookerySmoke` 登录并进入，驻留 10 秒后 `save-all flush` 成功，五维度正常保存，专服退出码 0；客户端因正常关服断开、窗口正常关闭且 Gradle `BUILD SUCCESSFUL`。4 条 ERROR 均为副本不同区块中缺失 `farmersdelight:wild_beetroots` / `sandy_shrub` 的 recoverable default 恢复，Cookery ERROR=0；证据为 `codex-server-quality-final-20260716-0010-client-connect.log` / `codex-client-quality-final-20260716-0010-connect.log` | PARTIAL |
| 2026-07-16 00:21 | 同一 `codex-quality-final-20260716-0010` 副本二次加载保存 | 成功 | 最终目标三模组栈再次加载 3071 recipes / 2701 advancements并到达 `Done`；`save-all flush` 成功，原版/Aether/暮色五维度均保存，服务端退出码 0、全部 ERROR=0、Cookery ERROR=0；证明首轮 default 恢复后的副本可再次加载和重存，证据为 `codex-server-quality-final-20260716-0010-pass2.log` | VERIFIED |
| 2026-07-16 00:24 | 临时客户端模组清理与最终只读复核 | 成功 | `run/mods` 临时 Aether/Twilight JAR 按复制前哈希删除后为空；目标/备份 Cookery 仍分别为 `4B6AA...437B` / `BF639...247C9`；原 `world` 的 `level.dat` 仍为 `0E6514...7CED` 且最新写入仍为 2026-07-01 23:30:04，`server.properties` 仍为 `level-name=world` / `server-port=25565`；受保护 drying-rack 目录时间仍为 2026-07-15 16:29:40；外部 PID 27624 仍存活并监听 25584/25585，本次 25570 已释放。最终 `git diff --check` 退出码 0（仅既有 LF→CRLF 提示）；未跟踪审计文件另行扫描 468 行、尾随空白 0 | VERIFIED |
| 2026-07-16 17:59 | 用户指定 `versions\world` 原件的只读文件/NBT 鉴定 | 成功，迁移测试进行中 | 用户明确只需适配其自写 Fabric 模组存档且不存在 Forge/NeoForge 世界。原件 70 files / 29,711,647 bytes；`level.dat` 455 bytes、SHA-256 `46140A68EAFF48A5C5E851A4B00625F580A851002E6820FBBBEC86C42958F30C`、DataVersion 4790、MC 26.1.2、`fabric`、启用 Cookery/Farmer's Delight/Aether/Twilight 数据包；单个玩家在 Aether 且配方书含 `kaleidoscope_cookery:raw_meatball`。尚未复制或启动原件 | IN_PROGRESS |
| 2026-07-16 18:15 | 指定原件 38 个 Anvil 文件 / 6,229 chunks 全量只读 NBT 与 26.1.2 分支对照 | 成功 | 全部 chunk DataVersion 4790；Cookery 方块/BE/自定义实体均为 0。唯一实体持久数据是 28 只原版 creeper 的旧 marker；玩家同时保存 `raw_meatball` recipeBook 两表和 `recipes/food/raw_meatball` 已完成 advancement。世界另含 71 个 Farmer's Delight 方块（39 sandy shrub + 32 wild beetroots），故测试栈必须安装 26.2 FD，不能再次用缺失模组的 default 恢复污染副本 | AUDITED |
| 2026-07-16 18:20 | 指定原件的独立备份与 `run` 工作副本 | 成功 | 原件、`versions/codex-save-backups/world-fabric-26.1.2-20260716-1820` 备份、`run/codex-fabric-2612-upgrade-20260716-1820` 工作副本均为 70 files / 29,711,647 bytes；逐相对路径/长度/文件 SHA-256 聚合树哈希均为 `F9C7B0D565A67C5DE452BF620115495BD13F2E40322A9F0FC4DF520245F48C1A`，`level.dat` 均为 `46140A68...F30C`。原件和备份不用于启动 | VERIFIED |
| 2026-07-16 18:08-18:12 | 指定 26.1.2 Fabric 世界第一轮 26.2 升级、原 UUID 客户端登录、flush 与正常停止 | 成功，发现测试栈缺项 | 46-mod server 到达 `Done (0.449s)`，原版完成硬链接文件结构升级；`CasseShimada` 以原 UUID 在 Aether 原坐标登录并驻留 57 秒，客户端 54 mods / 47 advancements，随后正常关闭；控制台 `save-all flush` 和 `stop` 均成功，Gradle 两端 `BUILD SUCCESSFUL`。玩家 UUID/坐标/4 物品、`raw_meatball` recipeBook 两表和 advancement 时间戳原样保留，DataVersion 4790→4903；71 个 FD 方块未丢。服务器无 ERROR；客户端唯一 ERROR 是开发 token profile-key 401。因未安装 FTB Ultimine，保存移除了缺失 `ftbultimine` data pack，故此轮只作探针，不作为最终副本 | PARTIAL |
| 2026-07-16 18:15-18:24 | 新鲜副本使用旧 FTB Library/Ultimine 26.1.2.3 的完整注册栈升级、客户端登录与保存 | 成功，第三方客户端需测试垫片 | 49-mod server 在 26.2 到达 `Done (0.359s)`，保留 `ftbultimine` enabled pack 且 0 ERROR。未修改旧 FTB 客户端因 26.2 字段变更在首 tick 报 `NoSuchFieldError Minecraft.screen`；仅测试目录中的 jar 副本移除两个旧客户端 entrypoint 后保持 common 注册，26.2 客户端完成注册表同步，`CasseShimada` 以原 UUID/原 Aether 坐标登录并加载 47 advancements。正常关客户端、`save-all flush`、`stop`，Gradle 双端成功；此垫片不进入源码、发布 jar 或用户原模组 | PARTIAL |
| 2026-07-16 18:28-18:31 | 保留 FTB pack 的同一副本二次服务端加载、第二次客户端登录、第三次独立日志重载 | 成功 | 第二轮客户端再次连接并接收世界/47 advancements；第三轮 49-mod server 单独捕获完整日志，`Done (0.460s)`、`save-all flush`、五维度保存、正常停止、`BUILD SUCCESSFUL`，服务端 ERROR=0。最终 NBT：DataVersion 4903、FTB/Cookery/FD/Aether/Twilight packs 均在，UUID/坐标/4 物品、26+26 配方书、原 advancement 时间戳、71 个 FD 方块、28 个 creeper marker 全部保持 | VERIFIED |
| 2026-07-16 18:35-18:36 | 不依赖旧 FTB 客户端的可部署副本二次加载保存 | 成功 | 使用 Aether/Twilight/FD 26.2 及 Cookery、无 FTB Library/Ultimine；第一次迁移已按原版语义移除缺失的旧 `ftbultimine` enabled pack，第二次 46-mod server `Done (0.376s)`、0 ERROR、flush、五维度保存、正常停止、`BUILD SUCCESSFUL`。业务 NBT 与 71 个 FD 方块、28 个 Cookery marker 再次保持 | VERIFIED |
| 2026-07-16 18:38-18:39 | 可部署副本 `--forceUpgrade` 与全量 NBT 复扫 | 成功 | 原版报告 `Forcing world upgrade`，遍历 5,955 个方块区块并在 5 秒内完成 world optimization，随后 `Done (0.371s)`、flush/stop/五维度保存、0 ERROR、`BUILD SUCCESSFUL`。最终副本 71 files / 30,171,834 bytes，`level.dat` DataVersion 4903 / SHA-256 `D6D1620033D4BDC420DF1719D709AEAB9FEA229807153BF6441A27DC897ACBE4`；原 UUID/位置/物品、配方书、进度时间戳、71 个 FD 方块与 28 个旧 marker 不变。升级器未重写的 1,397 个 Aether block records 与 20 个 Aether entity records 仍标 DataVersion 4790，但已在 26.2 全量遍历并可重复加载；其他维度及全部 POI 已重写 | VERIFIED |
| 2026-07-16 18:40 | 原件/备份只读终检、运行环境清理与发布 jar 复核 | 成功 | 原件和独立备份仍各 70 files / 29,711,647 bytes，70 个相对路径逐文件长度+SHA-256 比较差异为 0，`level.dat` 均保持 `46140A68...F30C`，且二者 file ID 不同、不是硬链接。`run/mods` 恢复为空，`server.properties` 恢复 `world`/25565/online/secure，25570 无本任务监听；发布 jar 仍为 5,424,947 bytes / `4B6AA...437B` | VERIFIED |
| 2026-07-16 19:16 | 用户最终验收范围确认与 Cookery-only 证据复核 | 成功 | 用户明确只要求 KaleidoscopeCookery 正常游玩并安全读取其自写 Fabric 26.1.2 世界，不存在 Forge/NeoForge 旧世界，且可忽略其他模组影响。既有真实客户端证据再次核对：原 UUID `CasseShimada` 在原 Aether 坐标登录、读取 47 advancements、正常驻留/退出；多轮 Cookery 26.2 服务端均到达 `Done`、flush、五维度保存和正常停止，Cookery ERROR=0。最终副本保持玩家位置/4 物品、26+26 配方书、`raw_meatball` 进度时间戳与 28/28 旧 Cookery creeper marker；因此在用户明确的 Cookery-only 范围内完成验收 | VERIFIED |
| 2026-07-16 19:35-19:46 | 旧 AI marker 惰性清理实现、自动化、部署与真实副本定向 smoke | 成功 | `ServerEntityLoadEvent` 精确删除旧猫/苦力怕标签，仍按运行 Goal 实例去重；GameTest 验证两个 marker 清除、无关 tag 保留、重复事件后 Goal 各恰好一个。`compileJava compileGametestJava test`、`build`、96/96 GameTest、11/11 `verify_*.py` 均通过；新 jar 5,425,072 bytes / 5225 entries / SHA-256 `BDA6D1BAD4862D399765572F856528FE55587A6604E8FE0665BDFD52509BC431`。目标旧 `4B6AA...437B` 已备份后部署新 jar；独立 `codex-marker-cleanup-20260716-1942` 副本强载覆盖 28 只实体的 72 个区块，两次 flush、正常退出码 0、Cookery ERROR=0；离线复扫仍为 28 只 creeper，marker 28→0。临时 FD jar 已按哈希移除，`server.properties` 恢复 `world`/25565，25570 释放 | VERIFIED |
| 2026-07-16 20:45-21:00 | Forge 第三方适配基线机械复查、最新 26.2 构件清单与首轮安装态客户端 | 发现并定位三类真实回归 | 当时公开构件检索仅取得 FD 3.6.7、Jade 26.2.9、JEI 30.11.0.67、REI 26.2.820、Food Effect Tooltips 2.1.2，尚未取得 Create/EMI/KubeJS/Ponder/Tetra/Harvest With Ease/Carry On 的 26.2 构件；Carry On 后由用户在 2026-07-17 提供本地 26.2 分支并完成验证。源码对照发现 REI 从基线 8 类退为 5 类；首轮 59-mod 客户端另暴露 Jade 三个缺失 config 翻译以及 JEI 对只读 recipe list 原地排序的异常 | IN_PROGRESS |
| 2026-07-16 21:00-21:13 | REI/JEI/Jade 修复、FD 安装态 GameTest 与完整客户端旧世界副本 pass 2 | 成功 | 恢复 REI flex pot/flex stockpot/teapot，普通/灵活汤锅缺失 soup base 改为警告继续；REI Ingredient 以 `SlotDisplay` 支持 `fabric:any`。JEI 两个可变列表入口修复；Jade 13/13 UID 翻译锁定。FD 安装态 2519 recipes、96/96，三条代表性 cooking recipe 均转换/匹配/viewer 枚举通过。59-mod 客户端载入旧世界副本，Jade 双插件、Cookery REI 插件、JEI 8 类均完成，REI 同步 2446 recipes；正常窗口关闭、三维度保存、Gradle `BUILD SUCCESSFUL`，Cookery/JEI/Jade plugin ERROR=0，证据为 `run/client-compat-smoke-20260716-2110-pass2.stdout.log` | VERIFIED |
| 2026-07-16 21:15-21:18 | 最终 clean 自动化、产物边界、部署与目标完整栈专服副本 | 成功 | `clean compileJava compileClientJava test build runDatagen runGameTest` 成功，2209 recipes / 1859 advancements、96/96、11/11；JAR 5,444,836 bytes / 5231 entries / SHA-256 `512EDC0D6031F454B11F4839BB97CF9C0704E394E1D883AD2DDC8B3C8B57715E`，JAR/`jdeps`/runtimeClasspath 禁止依赖 0，`git diff --check`=0。旧部署 `BDA6D1...BC431` 已备份为 `codex-smoke-mods/...pre-third-party-compat-20260716-2116.jar` 后部署新 JAR。目标 Aether/Twilight 44-mod 栈在独立 `codex-integrations-final-20260716-2118` 副本加载 3071 recipes / 2701 advancements、`Done (0.357s)`、五维度 flush/stop、退出码 0、Cookery ERROR=0；用户原 `versions\world` 未启动或修改，25570 释放 | VERIFIED |
| 2026-07-17 00:09-00:41 | 用户提供的本地 Carry On 26.2 / 2.9.1 源码审计、双方构建、双名单真实搬运与 1.1.0.6 clean 自动化 | 成功 | Carry On 自身 `test build` 通过且只保留其既有 `.github`/审计修改；Cookery 将原 116 全拉黑细分为 31 个可搬运持久 BE 与 85 个不安全补集，并同时发布 whitelist/blacklist。真实 `PickupHandler`/`PlacementHandler` 在 blacklist 与 whitelist 两种配置下对全部 31 个安全方块完成 62 次搬起—放下，逐项保持坐标无关完整 BE NBT、库存/茶液 ID/颜色/油量/锅盖及所有非朝向方块属性；4 类多方块结构拒绝搬起。无 Carry On 的 clean `build runDatagen runGameTest` 为 97/97；安装本地 JAR 后连同 Carry On 自带 6 项为 103/103；2209 recipes / 1859 advancements、11/11 `verify_*.py` 通过。`1.1.0.6` JAR 5,445,139 bytes / 5232 entries / SHA-256 `AB8347BF3A06F28AAE235ECB5F8C743F69F058DB27A9E835BA820F544BBD124A` | VERIFIED |

GameTest 退出后 Gradle/Log4j 报告 Windows 无法删除仍被占用的 `build/run/gameTest/logs/latest.log`，但任务退出码为 0、服务端完成保存与关闭，不影响本次 53 项测试结论；后续若复现为残留进程则单独调查。

最终迁移自动化闭环已完成：common/client 编译、`test`、`build`、datagen、默认 97/97 GameTest、Carry On 2.9.1 安装态 103/103、11/11 静态验证、产物内容及先前 `jdeps`/运行依赖检查均有成功证据。最新未提交 `1.1.0.6` 产物 `AB8347...D124A` 在 `1.1.0.5` 的旧世界/AI marker/FD/Jade/JEI/REI/Food Effect Tooltips 与目标完整栈证据上，新增 Carry On 黑/白名单双模式的全部 31 个安全方块真实往返和 85 个不安全方块补集约束。用户明确不存在 Forge/NeoForge 世界，因此不再把不存在的样本列作本次范围阻塞项。

## 已知问题、阻塞项与风险

1. `VERIFIED`：用户已明确实际兼容范围只包含其自写旧 Fabric 模组的指定 MC 26.1.2 世界，且不存在 Forge/NeoForge 世界。该原件已完成全量只读清单、独立非硬链接备份、仅副本的客户端登录/保存/多次重载、`--forceUpgrade` 和最终逐项 NBT 对照；原件与备份终检仍逐文件完全相同。
2. `AUDITED`：功能基线已锁定为 `upstream/main@1d935a2c`；NeoForge 1.21.1 仅作近版本辅助参照。
3. `VERIFIED`：当前已提交 HEAD 与 `origin/26.2-fabric` 同步；本轮 Carry On 适配仅存在于未提交工作区，其状态以本文件中的源码对照和测试为准。
4. `AUDITED`：用户有 3 个初始未提交修改；其测试边界、OilPot 交互返回值调整与服务端扫描意图均保留，禁止回退。
5. `VERIFIED`：Gradle、Java 25.0.2、依赖解析、目标 PCL2 Loader/MC/Java 配置和最终客户端/专服实际日志版本已交叉确认。
6. `VERIFIED`：注册集合、全部持久化域和规范化资源路径已完成对照；方块属性 31/31 + 23/23、玩家存储入口及丰富旧 ItemStack 也已最终复扫。
7. `VERIFIED`：REI 客户端源码不 import Architectury/basic-math；移除 Architectury `clientCompileOnly` 的边界探针因 REI 公开 `EntryStacks` 签名暴露 `FluidStack` 而无法编译，证明该声明仅用于第三方 Fabric REI API 的编译类型解析。两项均不在核心 runtimeClasspath、release jar 或 `jdeps` 结果中，不能视为生产多加载器兼容层。
8. `VERIFIED`：`recipe_book` 与最新 Forge/Neo `recipe_block` 方块实体类型均注册，兼容类型归一及旧端点由静态脚本/GameTest 验证。
9. `VERIFIED`：搪瓷盆恢复 `oil_count=0..32` 与空默认，并由 GameTest 验证 13 单位油的完整掉落。
10. `VERIFIED`：油壶 Neo 组件 ID、迁移前 Fabric ID 与 Forge 旧 `oil_count` NBT 已兼容并有夹具；丰富 ItemStack 也已独立收口。用户指定世界没有 Cookery 物品/方块实体端点，真实玩家库存与 Cookery 配方/进度已完成副本往返。
11. `VERIFIED`：`block.teapot.processing` 和 `block.trash_can` 声音 ID/定义已恢复，误用 Fabric 茶壶 ID 保留兼容别名。
12. `VERIFIED`：通用配置 9/9 个键、范围、默认值、旧 Forge common TOML 只读导入及其完整减伤算法已由 55 项 GameTest 验证。
13. `VERIFIED`：Minecraft 26.2 `ItemStack.CODEC` 不能直接读取 Forge 1.20.1 自定义 BE 内嵌旧栈；统一 `References.ITEM_STACK` DataFix 已接入并以 Forge-shaped 夹具验证全部 12 类库存 BE，复杂槽位/附加状态和保存后二次加载为 61/61 GameTest 的组成部分。
14. `VERIFIED`：磨石 1..4 `RandomOutput`、每输入逐输出独立投掷、四槽 `OutputItem`、旧 UUID/Carrier、迁移前 Fabric 单栈、破坏/取出/漏斗与客户端展示已恢复；60/60 GameTest 且 2046 个实际配方加载成功。
15. `VERIFIED`：稻草人 `ShoulderEntity` 使用旧 Forge 鹦鹉形状时，26.2 `EntityType.create` 通过鹦鹉专用 legacy variant codec 正确保留整数 Variant、Health 和 UUID；无须对该字段强行重复运行 `References.ENTITY` DataFix。
16. `VERIFIED`：九个自定义 recipe serializer 已接入 registry-aware 兼容 codec，读取 Forge `{item:...}`、`{tag:...}`、混合 Ingredient 数组及 `result.item`；63/63 配方阶段 GameTest 与 2046 个当前配方加载通过。
17. `VERIFIED`：FlexPot 缺省 carrier、Stockpot `empty_carrier`（基线内置 27 个配方实际依赖，且 true 优先于显式 carrier）和 Teapot 可缺省 ingredient 已用 Optional 恢复，并通过 JSON 与 StreamCodec GameTest。
18. `AUDITED`：Minecraft 26.2 已禁止构造空 `Ingredient`，首次兼容实现的 GameTest 启动栈在 `Ingredient.<init>` 明确失败；FlexPot carrier、Stockpot carrier 与 Teapot ingredient 的旧空值必须建模为 `Optional<Ingredient>`，相关 JSON/StreamCodec 和消费端需要同步调整。
19. `AUDITED`：26.2 Ingredient 的 HolderSet 表示不能直接解码 Forge 可接受的 item/tag 混合 OR 数组；需在 registry-aware ops 下逐个解析旧数组分支、展开 tag holder 并合并为直接 Ingredient，不能只把对象机械改成字符串。
20. `VERIFIED`：两个 C2S unit payload 仅表达动作，服务端从连接取得玩家并验证效果/物品/潜行/冷却/连接/实体生成；1 tick 节流与 MISS 边界已由静态脚本和 67/67 GameTest 验证。
21. `VERIFIED`：手持茶壶已用原版 BucketPickup/BucketItem 恢复装液，用 Fabric AttackBlockCallback 恢复潜行左击清空，并恢复成品茶/熔岩实体倾倒、伤害、声音和粒子；组件与行为夹具通过。
22. `VERIFIED`：12 个 Forge 菜谱交易和精确五级交易池已恢复；首版直接组件因 26.2 动态交易注册早于物品默认组件完成而失败，自定义 loot function 延迟构造后动态注册表、2046 配方及真实菜谱报价的完整组件语义通过 68/68 GameTest。
23. `VERIFIED`：客户端 `ShowFoodEffectTooltips` 默认值、旧 TOML 只读导入、用户关闭及 `foodeffecttooltips` 安装时抑制重复效果行已由 69/69 GameTest 锁定；2.1.2+26.2 安装态也已完成客户端资源初始化。
24. `VERIFIED`：Farmer's Delight Refabricated 26.2-3.6.7 的新 `input/result/container` 与 `ItemStackTemplate` API 已兼容，同时保留旧 getter 支持；安装态 70/70 GameTest 验证真实 beef stew 的完整转换、汤锅匹配与 recipe viewer 枚举。
25. `VERIFIED`：用户提供的 Carry On `26.2` 分支 / 2.9.1 构件已完成源码审计、自身构建和 Cookery 安装态测试。适配以纯数据标签将 116 个方块精确分成 31 个可安全搬运的持久 BE 与 85 个不安全补集，兼容 Carry On 的 blacklist/whitelist 两种模式；全部安全方块共 62 次真实搬起放下保持完整 NBT 与非朝向状态，多方块结构运行拒绝。默认 97/97、安装态 103/103 GameTest 和 11/11 静态验证通过，生产代码不硬链接 Carry On。
26. `AUDITED`：Serene Seasons Modrinth 版本 `13sXhUkI` 虽将文件标为 MC 26.2，实际 jar manifest 要求 MC 26.1.2 和 GlitchCore；目标 Loader 的硬依赖错误可复现。用户已明确忽略其他模组影响，故该第三方错误不阻塞 Cookery-only 验收。
27. `VERIFIED`：完整客户端可选栈使用 Architectury 21.0.4 与仅限 smoke 的 Fabric API 0.154.0 成功完成初始化；默认构建仍保持 Fabric API 0.153.0，未无条件抬高 Cookery 最低运行要求。
28. `VERIFIED`：Forge `MobBucketItemMixin` 的 `rice_growth_booster` 桶实体过滤已恢复；26.2 经 `BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(type)` 检查标签，固定 3x3 扫描不变，静态契约与 cod/cow 运行标签、71/71 GameTest 均通过。
29. `VERIFIED`：Forge/NeoForge 油块非粘性语义已恢复；Mixin 不再覆盖 `isSticky`，仅在原版缺少扩展点的 `canStickToEachOther` 对任一侧油块返回 false，静态契约与 26.2 私有判定运行测试、72/72 GameTest 均通过。
30. `VERIFIED`：活力效果已恢复 Forge/NeoForge 的每 tick 冲刺总 exhaustion 清零；26.2 用只访问 `FoodData.exhaustionLevel` 的 accessor 实现，已移除脆弱且语义较弱的 `ServerPlayer` ordinal redirect，静态边界与 72/72 GameTest 通过。
31. `VERIFIED`：稻草人农田保护已限定为非空实体踩踏；26.2 缺水 `entity=null` 路径继续退化，实体路径在 16 格稻草人范围内取消，静态契约和成对运行测试、73/73 GameTest 通过。
32. `VERIFIED`：剩余 Mixin/AW 已完成双基线源码、26.2 字节码与运行语义对照；面粉、蒸笼坠落、寒带疾行、粉雪、弹射物、举盘姿势和五类村庄池均由静态契约与 78/78 GameTest 锁定。蒸笼显式 `discard()` 是 26.2 超时掉落取消路径所必需，不能按旧源码表象删除。
33. `VERIFIED`：保鲜已从 `UseItemCallback` 移到 `LivingEntity.completeUsingItem` 的实际完成路径；HEAD 保存原食物栈，在 `finishUsingItem` 后、`stopUsingItem` 前按 `FOOD`/`CONSUMABLE` 声明移除有害效果，不再依赖 5 项标签；自定义标签外苹果、无保鲜控制和无关 Hunger 均由 81/81 GameTest 锁定。
34. `VERIFIED`：农夫套装已由 `LivingEntity.tick` TAIL 对全部 LivingEntity 使用自身 `tickCount % 20`；水中非玩家完整四件套得到 25 tick 海豚的恩惠，缺靴控制不生效，81/81 GameTest 通过。
35. `VERIFIED`：垃圾桶隐藏已在 `Mob.setTarget` HEAD 取消骑乘 `SitType=TRASH_CAN` 玩家赋值；隐藏目标、可见控制及取消时保留旧目标由真实 setter GameTest 验证，入座局部清理继续处理骑乘前已有目标。
36. `VERIFIED`：垃圾桶第一人称相机已在 `Camera.update` TAIL 保留 `yRot()` 并将 pitch 设为 0；玩家隐藏通过 `AvatarRenderer.extractRenderState` 写入 Fabric typed key、`LivingEntityRenderer.submit` HEAD 取消。首轮 smoke 暴露并修正 getter 实名后，静态契约、客户端编译、普通客户端资源初始化及专服边界均通过。
37. `VERIFIED`：Forge/NeoForge `SpecialRecipeItemEvent` 的任意 item-handler 回退已由 Fabric Transfer API 等价恢复。事件从 `PlayerInventoryStorage` 真实槽位取得 mutable context，按非空 storage view 计数，并在单事务内按实际 variant 提取以保持旧 `ItemStack.is(item)` 忽略组件差异的语义；shulker 端到端夹具与 82/82 GameTest 通过。
38. `VERIFIED`：Forge/NeoForge `RightClickEvent` 的果篮潜行主手持物取出已用 `UseBlockCallback` 恢复。26.2 `ServerPlayerGameMode` 的 secondary-use 字节码证明方块 `useItemOn` 不可作为该入口；新事件保留调试棒、烟花火箭、可选 `touhou_little_maid:smart_slab_has_maid` 和非顶面方块物品放行，真实回调链与 83/83 GameTest 通过。
39. `VERIFIED`：Forge 与 NeoForge `FlatulenceEvent.isInGame` 的 overlay 为空、screen 为空、鼠标已捕获和窗口激活四项门控均已恢复；26.2 使用 `client.gui.overlay()` / `client.gui.screen()`。静态网络契约与客户端编译通过，既有 key-edge 状态更新顺序和服务端权威跳跃保持不变。
40. `VERIFIED`：Forge 默认关闭的内置旧版材质包已由 Fabric Resource Loader `NORMAL` 注册；1406 个资产和 839 个模型可在 26.2 实际启用。81 个无父显式纹理模型补齐 particle，三个 Forge `separate_transforms` 改为精确显示上下文 selector；启用态资源重载、OpenAL/全部主要图集完成且 Cookery 模型/资源警告为 0。
41. `VERIFIED`：锅与垃圾桶 HUD 均恢复注册在 `VanillaHudElements.CROSSHAIR` 之后；锅提示通过最小只读 `HudAccessor` 在 action-bar overlay timer 大于 0 时上移 12 像素。静态契约、第五个 client Mixin 编译/运行应用和两轮客户端资源初始化通过。
42. `VERIFIED`：Forge 1.20.1 胀气临时起点 `ForgeData.FlatulenceEffectStartingPosition={X,Y,Z}` 已由 `PlayerMixin` 在读取尾部只读迁移；只在效果仍存在且新 attachment 未设置时应用，首次记录恢复整数方块坐标，效果移除清理。Forge-shaped NBT 与运行状态 GameTest 通过；用户指定 Fabric 世界没有该字段，实际适用的配方/进度与实体 marker 已完成真实副本往返。
43. `VERIFIED`：Forge 六类猪油附加池的每 roll 必定产油语义已恢复，错误等权 `EmptyLootItem` 已移除；pig/zombified piglin 一次及 hoglin/piglin/piglin brute/zoglin 两次常量由静态契约锁定，真实厨刀猪/驴击杀和无厨刀控制 GameTest 通过。
44. `VERIFIED`：规范化对照发现的 163 个基线仅有 Cookery 配方已从 datagen 恢复：138 pot、17 stockpot、8 根级 shapeless；当前新增配方保留。`verify_recipes.py` 锁定全部旧路径、1..9 输入/输出、米饭 grain tag、三组空 carrier 及八类无序合成原料；实际加载配方由 2046 精确增至 2209，90/90 GameTest 通过。
45. `VERIFIED`：`SimplePotRecipeProvider` 与 `FoodBiteRecipeProvider` 的数组-as-Object 回归已改为真正展开，原 9 个鸡蛋及 6 个史莱姆球空 ingredients 配方已生成有效原料；`verify_recipes.py` 现拒绝任一 Cookery 空 ingredients 列表，datagen、全静态套件与 2209 配方实际加载通过。
46. `VERIFIED`：`verify_release_hygiene.py` 现区分生产依赖与 `clientCompatSmoke=true` 的 opt-in runtime-only 集成栈；继续禁止条件块外 Cloth Config，并锁定条件块内只能为 `runtimeOnly`。修正后全部 11 个静态脚本同轮通过，已验证 REI smoke 运行依赖保留。
47. `VERIFIED`：规范化基线仅有的 11 个 PlateRegistry block loot table 已恢复为单 bowl + `survives_explosion`；`PlateBlock.getDrops` 改为以 `super` 结果为基础再追加剩余 servings，保留 Fabric 端增强且恢复旧爆炸语义。资源脚本锁定 11 张表结构和 104/12 分类，真实完整盘/空盘 GameTest 与 91/91 全套通过。
48. `VERIFIED`：`textures/gui/jei/teapot.png` 与未引用但属于基线主包的 `textures/block/stone_bricks.png` 已按 Forge blob 原样恢复；SHA-256 分别锁定为 `480c7610...` / `24415444...`。客户端纹理引用扫描扩至整个 client source set，`verify_client_assets.py`、`processResources`、`compileClientJava` 通过。
49. `VERIFIED`：Forge 基线主资源包与当前主资源包直接按相对路径比较的 4 个 model JSON 已逐项分类。主包已按基线恢复 `block/cross.json`，并恢复辣椒/生菜/番茄 24 个阶段的 `y=-1..15` 父链；24 个水稻阶段保持基线 vanilla `cross`。三个桌子 `*_rot` 父模型由升级提交有意替换为标准 `left/middle/right` 模型的 blockstate `y=180/270` 旋转，默认主包、item definition 与 Java 均无旧引用，旧文件仍在内置遗留包；验证器锁定 11 种桌子共 352 个状态。资源脚本、`processResources`、客户端编译通过。
50. `VERIFIED`：Forge 基线 34 个 `forge:item` common tag 已逐成员规范化并锁定到 `c:item`，同时保留当前驴肉、卷心菜、`foods/*` 等扩展。已恢复空 `c:doughs` 扩展链、煎蛋、碎生肉、野生稻/稻米和 `c:tools/knives`；两张 `ftbultimine:block` 水稻黑名单也已按 26.2 单数 registry 目录恢复。`verify_data_pack.py`、客户端/资源/配方脚本、`processResources` 通过；关键成员与两张 FTB 标签由 91/91 GameTest 运行验证。
51. `VERIFIED`：三个旧 recipe advancement ID 已以 26.2 predicate 格式作为兼容别名恢复，同时保留当前新分类 ID；实际 advancement 由 1856 精确增至 1859。Forge GLM 数据不进入 Fabric 包，其 8 个 modifier 由 `LootTableEvents.MODIFY` 等价替代：猪油/驴肉已有运行证据，草帽池已从错误近似恢复为 4 个 Cookery 12.5% + 3 个 vanilla 2% 的七个等权候选、头部草帽条件和 Fortune×2。datapack/server 脚本、datagen、资源处理与 91/91 GameTest 通过。
52. `VERIFIED`：Forge 主包与当前主包按 1.20.1→26.2 资源目录、`forge:`→`c:` common tag 规范化后，路径集合为基线 2402、当前 2927、基线仅有 52。36 个桌子 `*_rot` 文件由已锁定的 352 个 blockstate 旋转变体替代；Forge 的 1 个 GLM 索引、8 个 modifier 和 7 个辅助 loot table 共 16 个文件由 Fabric loot 事件替代。未解释基线路径为 0。
53. `VERIFIED`：Capability/Attachment/DataMap 基线入口已收口。茶壶使用通用 `FluidStorage.ITEM` 整桶事务，油壶全方向暴露 256 容量仅油 storage；磨盘以公开 `MillstoneEntityItemStorage.SOURCE` 查询任意注册 provider，内建带箱马 500 槽偏移事务适配，并恢复未供料时掉落物回退。唯一 Neo attachment、11 个 compostable、12 个磨盘绑定数据和厨师英雄礼物均有静态/运行证据；Fabric 不存在标准实体 item storage，第三方实体须显式注册该 Cookery lookup。
54. `VERIFIED`：方块属性最终机械对照发现并修复 Forge `FoodBiteBlock.QUALITY`（序列化名 `quality`、合法值 0..4、缺字段默认 4）。放置、品质营养/效果倍率、41 张完整食物品质掉落与 Jade 行均恢复；旧物品 `tag["kaleidoscope_cookery:quality"]` 惰性迁入当前组件并保留无关 custom data。96/96 GameTest 与 11/11 静态验证通过。
55. `AUDITED`：早期目标副本在未装 Farmer's Delight 时对 `wild_beetroots` / `sandy_shrub` 做了原版 default 恢复；用户指定 26.1.2 世界的正式适配测试已安装 FD 并保持全部 71 个方块。用户现明确忽略其他模组影响，此项不属于 Cookery-only 完成阻塞。
56. `VERIFIED`：用户指定 26.1.2 Fabric 世界的真实 Cookery 持久数据只有玩家 `raw_meatball` 配方书/进度和 28 个原版 creeper 的 `kaleidoscope_cookery.creeper_mustard_avoid_goal` marker；世界没有 Cookery 方块、BE 或自定义实体。当前实体加载事件先精确删除旧猫/苦力怕 marker，再按实际 goal 实例而非 marker 门禁安装 AI。GameTest 覆盖清理、无关 tag 保留和 Goal 幂等；真实独立副本加载全部 28 个实体后仍保留 28 只 creeper，marker 28→0，Cookery ERROR=0。
57. `AUDITED`：FTB Ultimine Fabric 的公开文件列表截至 2026-07-16 仅到 MC 26.1.2.5，无受支持的 26.2 构件。测试已证明保留其 common 注册时世界可登录；推荐副本按缺失可选模组的原版语义移除旧 pack，且世界无 FTB 方块/实体。用户明确忽略其他模组影响，此项不属于 Cookery-only 完成阻塞。
58. `VERIFIED`：REI 与 JEI 均精确恢复 Forge 基线八类 Cookery 配方。静态验证锁定类集合和 REI category/display 双注册；安装态客户端执行真实 recipe sync 后 Cookery REI/JEI plugin 无 ERROR。首轮漏掉的三类、JEI 只读列表异常和 Jade config translation 断言均已有回归约束。
59. `AUDITED`：FD 3.6.7 自带的 REI plugin 对其两条含 `fabric:any` 的原生 cooking display 仍记录 `Failed to fill display`，归属第三方 plugin；不会导致退出或阻断世界加载。Cookery 不依赖该 filler：自己的兼容层已将同两条配方转换为汤锅配方，服务端实际匹配与 viewer 枚举通过，REI 侧改用 `SlotDisplay` 解析这些自定义 Ingredient。用户已允许忽略其他模组自身影响，此项不阻塞 Cookery 适配结论。

## 下一步唯一明确动作

用户已用 `continue` 授权继续交付。当前唯一明确动作是提交并推送 `1.1.0.6` 到 `26.2-fabric`，等待 GitHub Actions 远端构建成功并用远端构件创建正式 release；在远端验证完成前不替换目标实例现有的 `1.1.0.5`。正式使用存档时继续保留 `versions/codex-save-backups/world-fabric-26.1.2-20260716-1820`，绝不要覆盖原 `versions\world`。

## 给后续模型的接手摘要

最终本地自动化已通过：common/client 编译、build、三类 datagen、2209 recipes / 1859 advancements、默认 97/97 GameTest、Carry On 2.9.1 安装态 103/103（含其自带 6 项）和 11/11 静态验证均成功。Carry On 适配将 116 个 Cookery 方块分为 31 个可搬运持久 BE 与 85 个不安全补集；blacklist/whitelist 两种模式下全部安全方块共 62 次真实往返保持坐标无关完整 NBT 与非朝向状态，4 类多方块结构拒绝搬起。待发布的 `1.1.0.6` 本地产物为 5,445,139 bytes、5232 entries、SHA-256 `AB8347BF3A06F28AAE235ECB5F8C743F69F058DB27A9E835BA820F544BBD124A`；目标实例仍部署已发布的 `1.1.0.5`，下一步为提交、推送、Actions 远端构建和正式 release。既有旧世界/完整栈证据继续有效；用户原 `versions\world` 未启动或修改。Carry On 仓库既有修改未触碰；外部进程和受保护 smoke 目录不得触碰。
