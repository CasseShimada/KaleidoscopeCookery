# Farmer's Delight 兼容审计（Minecraft 26.2 / Fabric）

审计日期：2026-07-22（Asia/Shanghai）

## 结论

Kaleidoscope Cookery 的 Farmer's Delight 兼容目标已更新并锁定到
`farmersdelight` `26.2-3.6.8+refabricated`。该模组仍为可选依赖；缺失时不触达目标类，
安装精确目标版本时启用 Cooking Pot → Stockpot 转换，安装其他版本时输出一次明确错误并禁用该转换。

最终实现通过无 Farmer's Delight、精确目标版本、JEI+REI 客户端组合和专用服务器四种运行场景。
默认发布 JAR 不包含 Farmer's Delight 类，也没有对其实现类的字节码类型链接。

## 仓库与修改前基线

- 分支：`26.2-fabric`
- HEAD：`a5f46c26563697b46e304106e92e97c773384417`
- Minecraft：`26.2`
- Fabric Loader：`0.19.3`
- Fabric API：`0.153.0+26.2`
- Java：`25`
- 修改前 Farmer's Delight smoke 依赖：`26.2-3.6.7`，在 `build.gradle` 中重复硬编码两次。
- 修改前 `fabric.mod.json`：`suggests.farmersdelight = "*"`。
- 修改前工作树除用户给出的未跟踪文件 `FARMERS_DELIGHT_COMPAT_PROMPT.md` 和
  `THREE_MOD_API_COMPAT_PROMPT.md` 外为干净状态；两文件均未修改。
- 修改前完整无模组命令成功：`test NO-SOURCE`，99/99 GameTests。
- 修改前 `-PfarmersDelightCompatSmoke=true runGameTest` 成功：Farmer's Delight 3.6.7，
  45 mods，2,519 recipes，99/99 GameTests。
- 修改前默认 `runtimeClasspath` 不含 Farmer's Delight；opt-in 路径解析 3.6.7。

## 目标发布构件和源码基线

### 发布构件基线（最高优先级）

- Modrinth 项目：`7vxePowz`（Farmer's Delight Refabricated）
- 版本 ID：`PR6Lz8x8`
- 版本号：`26.2-3.6.8`
- 类型/状态：`release` / `listed`
- 发布时间：`2026-07-22T00:07:41Z`
- Modrinth 文件名：`FarmersDelight-26.2-3.6.8+refabricated.jar`
- Gradle 解析文件名：`farmers-delight-refabricated-26.2-3.6.8.jar`
- 文件大小：`3,467,451` bytes
- SHA-256：`cad661340de553b74e1a332d989a09208358666187ff876dabc75abad590118f`
- SHA-1：`d243dd529e8123ce78b03b3a8b38aa6a556eb72a`
- SHA-512：
  `dbf6b0333d91c662c5657043b1f43865f1458c467921403b416df297e4335d3b7387b1bc52abeec76fa7414836a3a1a3bfb68aa8acc45694dfaf522c5d316c84`
- JAR 内 `fabric.mod.json`：ID `farmersdelight`，版本 `26.2-3.6.8+refabricated`，
  Minecraft 要求 `~26.2`。
- JAR 内 class tweaker：`classTweaker v2 official`。

执行时查询同 Minecraft/loader 的已列出稳定版本，3.6.8 为最新项；未升级 Minecraft。

### Refabricated 源码参考基线

- 仓库：<https://github.com/MehVahdJukaar/FarmersDelightRefabricated>
- 分支：`fabric/latest/26.2`
- 提交：[`aa719708dd448a132521dbba66646e59d2ad1a0b`](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/commit/aa719708dd448a132521dbba66646e59d2ad1a0b)
- 提交时间：`2026-07-22T00:07:07Z`
- 该提交的 `gradle.properties` 声明 `minecraft_version = 26.2`、
  `minecraft_required = ~26.2`、`mod_version = 26.2-3.6.8`。

该提交是发布时 `fabric/latest/26.2` 的分支头，版本属性与 34 秒后发布的构件一致，故作为精确
API 源码参考基线。上游未提供可重现构建证明将该 Git 提交与下载 JAR 做字节级映射，因此发布
JAR 的实测签名和哈希仍是最终依据，不将源码提交冒充二进制重现证明。

### 功能语义上游

- 仓库：<https://github.com/vectorwing/FarmersDelight>
- 分支：`1.21`
- 提交：[`57fd50f23249b008ca694a4df36d85e0dcdff64f`](https://github.com/vectorwing/FarmersDelight/commit/57fd50f23249b008ca694a4df36d85e0dcdff64f)
- 提交时间：`2026-05-13T16:08:44Z`

Refabricated 提交 `aa7e745...` 的第二父提交为上述 upstream 提交，之后该内容被继续合入
Refabricated 的 26.1/26.2 线。因此它仅用于行为和数据语义对照；本项目未引入 Forge/NeoForge
类或依赖。

## 发现的问题和修复

1. **版本声明失真**：3.6.7 在两个 runtime smoke 分支中重复硬编码，元数据用 `"*"` 宣称
   未验证的未来版本。现以 `gradle.properties` 的 `farmers_delight_version` 为单一构建来源，
   元数据处理后精确为 `=26.2-3.6.8+refabricated`。
2. **构件没有完整性约束**：新增非传递 `farmersDelightCompatArtifact` 和
   `verifyFarmersDelightCompatArtifact`，检查大小、SHA-256、内部 ID/版本/Minecraft 范围、
   class tweaker 头和关键类条目。该完整模组 JAR 不进入编译 classpath。
3. **旧适配器依赖脆弱内部实现**：删除 `ItemHandler`、`RecipeWrapper`、动态 `Proxy` 和旧 getter
   猜测，改为只绑定 3.6.8 实际公开的 `input()`、`result()`、`container()`、
   `containerOverride()`、`getExperience()`、`getCookTime()`、`category()`。
4. **静默失败**：删除 `catch (Exception ignored)` 和静默空结果。目标版本安装后，签名漂移、
   错误返回类型或转换异常都会记录明确诊断并使测试/调用失败。
5. **可选依赖类边界不严格**：公共边界类不引用任何 Farmer's Delight 类型；只有 Loader 确认
   模组存在且版本满足项目元数据后，版本固定适配器才以字符串名称加载目标类。
6. **结果语义丢失**：旧实现把目标 `ItemStackTemplate` 降为普通 `ItemStack`，可能丢失数量和数据
   组件。新实现保留原模板；测试配方验证 count=2 和 `minecraft:custom_data` 从转换、实际烹饪到
   取出成品均保持不变。
7. **容器规则错误**：旧实现把 `container()` 为空的配方一律虚构为碗，导致 dumplings 和
   cabbage rolls 与目标行为不符。新实现使用目标已解析的 `container()`：显式 override、结果
   crafting remainder 和目标 override 表均由 Farmer's Delight 自己决定；`null` 表示无容器。
   目标自身按 item 类型验证/消耗容器，Cookery 的 `Ingredient` carrier 与之保持同等语义。
8. **输入匹配绕行内部容器**：改为 Cookery 的等价 `StackedItemContents` 匹配，严格保留 1～6 个
   原料、重复原料、任意空槽和“不得多一个非空原料”的规则。
9. **viewer 可能重复**：JEI/REI 共用追加入口现在按配方 ID 去重；安装态测试把 viewer 数量与
   目标 `cooking` RecipeType 的原始数量比较，并重复追加一次验证不增长。
10. **reload 未覆盖**：不缓存目标配方或转换结果；新增真实 `reloadResources` GameTest，reload 后
    再次查找、转换和匹配。
11. **跨模组标签不完整**：补齐双向现代标签 `c:foods/vegetable`、`c:foods/tomato`、
    `c:tools/knife`，保留旧 `c:vegetables`、`c:tools/knives` 和
    `farmersdelight:tools/knives`；熟米饭、刀具、卷心菜/洋葱/番茄、富饶土壤均有安装态断言。
    所有直接引用 Farmer's Delight 条目均为 `required: false`。
12. **缺少持续集成**：CI 现在除无模组 GameTest 外，另执行精确目标版本的
    `-PfarmersDelightCompatSmoke=true runGameTest`；新增静态/发布 JAR 回归脚本。

## 转换语义

| Farmer's Delight 字段 | Cookery 表示 | 结果 |
|---|---|---|
| recipe ID | 原 `RecipeHolder` key | 保留 |
| 1～6 ingredients | `StockpotRecipe.ingredients` | 保留，包括重复项 |
| result `ItemStackTemplate` | `StockpotRecipe.result` | 数量和组件保留 |
| resolved container | `Optional<Ingredient> carrier` | 与目标 item-type 消耗语义一致 |
| cook time | `StockpotRecipe.time` | 保留 |
| experience | 无对应字段 | 无法表示；访问器仍被严格签名/返回值校验 |
| recipe-book category | 无对应字段 | 无法表示；访问器仍被调用以暴露 API 漂移 |
| group/show notification | Stockpot 模型无对应语义 | 不适用于目标 CookingPotRecipe 公共执行模型 |

## 自动化覆盖

- Farmer's Delight 缺失：适配器不激活、viewer 不追加、服务端匹配返回空，且 target-only JSON
  由 Fabric load condition 跳过。
- 精确版本识别：断言 Loader 报告 `26.2-3.6.8+refabricated`。
- 目标内置 Cooking Pot 配方：28 份；测试集合覆盖 1、2、3、4、5、6 个输入。
- 代表配方：beef stew、dumplings、cabbage rolls。
- 专用条件测试配方：重复原料、count=2、custom data、显式玻璃瓶、37 ticks、2.5 XP、drinks
  category。
- 第二份条件配方：无 JSON container 的 experience bottle，验证目标
  `INGREDIENT_REMAINDER_OVERRIDES` 自动解析为玻璃瓶。
- Stockpot 真实路径：加水、放入三份原料、盖盖、完成、开盖、拒绝错误 carrier、接受玻璃瓶、
  取出并保留组件。
- 数据包 reload：服务器实际 reload 后再次转换和匹配。
- JEI/REI：共用配方集合无漏项、无重复 ID；60-mod 客户端组合初始化两套 viewer。
- 标签：双方蔬菜/番茄/刀具、双方熟米饭和 rich soil farmland。
- 发布边界：默认 runtime 无目标模组，opt-in 只解析 3.6.8，发布 JAR 不打包/硬链接目标类。

## 实际执行的验证

| 命令/检查 | 结果 |
|---|---|
| `git status --short`、`git branch --show-current`、`git rev-parse HEAD` | 基线已记录；两份用户 prompt 保持未跟踪且未修改 |
| `./gradlew ... dependencies/dependencyInsight`（修改前） | 默认 runtime 无 FD；opt-in 为 3.6.7 |
| `./gradlew clean compileJava compileClientJava test build runDatagen runGameTest --warning-mode all --console=plain`（修改前） | 成功；`test NO-SOURCE`；99/99 |
| `./gradlew -PfarmersDelightCompatSmoke=true runGameTest ...`（修改前） | 成功；3.6.7；99/99 |
| `./gradlew compileJava compileGametestJava processResources ...` | 成功；目标构件完整性任务通过 |
| 首轮 3.6.8 GameTest | 3 项失败并已定位：测试配方与 apple cider 冲突、旧测试虚构 bowl、实际 stockpot 因冲突选择较长配方；改用唯一重复原料 fixture 并修正目标容器语义 |
| 次轮 3.6.8 GameTest | 1 项失败并已定位：测试在盖子未取下时尝试取成品；补齐真实开盖步骤 |
| `./gradlew clean compileJava compileClientJava test build runDatagen runGameTest --warning-mode all --console=plain`（最终） | 成功；44 mods；2,209 recipes；`test NO-SOURCE`；101/101 |
| `./gradlew -PfarmersDelightCompatSmoke=true runGameTest --warning-mode all --console=plain`（最终） | 成功；45 mods；2,521 recipes（含两份条件 fixture）；101/101；reload 成功 |
| `./gradlew -PclientCompatSmoke=true -PclientCompatGameTests=true runClientGameTest --warning-mode all --console=plain` | 成功；60 mods；FD 3.6.8、JEI 30.11.0.67、REI 26.2.820；资源重载、OpenGL、OpenAL、全部图集完成；退出码 0 |
| `./gradlew -PfarmersDelightCompatSmoke=true runServer ...`，检测 `Done` 后输入 `stop` | 成功；44 mods；2,519 recipes；`Done (3.482s)`；三维度保存；退出码 0 |
| `Get-ChildItem scripts -Filter "verify_*.py" ...` | 13 个 verifier 全部成功；新 FD verifier 报告精确版本、SHA、边界及行为覆盖通过 |
| 默认 `runtimeClasspath` | Farmer's Delight 行数 0 |
| opt-in `dependencyInsight` 和 `farmersDelightCompatArtifact` | 均只解析 `26.2-3.6.8` |
| `git diff --exit-code -- src/main/generated` | 成功，无 datagen 漂移 |
| `git diff --check` | 成功；仅 Git 的预存 LF→CRLF 工作树提示，无 whitespace error |
| 发布 JAR ZIP 检查 | `BUNDLED_TARGET_ENTRIES=0`；元数据 suggestion 精确；无 hard dependency |
| `jdeps --multi-release base --ignore-missing-deps -q -recursive <release.jar>` | 退出码 0；`vectorwing.farmersdelight` 外部链接 0 |
| `javap -verbose ...CookingPotCompat` | 目标 `CONSTANT_Class` 引用 0；仅 4 行版本固定类名字符串 |

## 日志审查与剩余风险

- 无 Cookery ERROR、配方解析失败、未知标签、目标 API 初始化失败或 GameTest 失败留存。
- `run/crash-reports` 中只有 2026-07-15/16 的 3 份既存客户端报告；本次 2026-07-22 的
  GameTest、客户端和专服运行没有生成新 crash report。
- 首次 clean GameTest 在生成运行目录前由原版记录一次缺失 `server.properties` 的 ERROR，随后按默认值
  正常创建并完成 101/101；这是 Loom/原版首次运行文件状态，不是模组加载错误。
- 客户端组合中 JEI 对未安装的可选 Amecs 类发出两条 mixin target WARN；新建测试配置还记录一次
  `Illegal option value 0 for Anisotropic Filtering`，随后 OpenGL/资源/图集全部成功。两者均来自第三方
  或临时客户端配置，Cookery 和 Farmer's Delight 配方适配无 ERROR。
- Farmer's Delight 3.6.8 的专服日志会对其自身两个客户端 accessor/invoker mixin 目标发出 WARN
  (`GhostSlots`、`GuiGraphicsExtractor`)；服务端仍完成加载、世界生成、保存和正常关闭。该外部模组
  mixin 声明不在只允许修改 Cookery 的范围内。
- 用 `-Dfabric-tag-conventions-v2.missingTagTranslationWarning=VERBOSE` 复跑安装态 101/101 后，
  通用“未翻译 item tag”警告精确展开为 Farmer's Delight 提供给另两个可选模组的
  `createaddition:plant_foods` 和 `create:upright_on_belt`；不是 Cookery/FD 食材标签缺失或解析失败。
  Create 与 Create Crafts & Additions 均不在本次运行栈，且这些外部 namespace 的显示名不应由
  Cookery 冒充所有者。
- 兼容声明故意只接受 3.6.8；未来 Farmer's Delight 版本必须更新哈希/API 基线并重跑测试，不能
  自动视为兼容。
- 目标没有独立发布 API 构件；完整 mod JAR 的 class tweaker v2 也不能安全作为当前 Loom 1.15.5 的
  compileOnly API。最小反射适配器因此仍是必要边界，但已由精确元数据、构件哈希、公开签名校验和
  安装态失败即失败测试约束。
- Cookery Stockpot 模型无法表示目标 XP 和 recipe-book category；这是模型能力差异，未伪造或静默
  映射为其他字段。

## 修改文件

- `.github/workflows/gradle-publish-26.2.yml`
- `build.gradle`
- `gradle.properties`
- `scripts/verify_farmers_delight_compat.py`
- `src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/compat/farmersdelight/FarmersDelightCompat.java`
- `src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/compat/farmersdelight/CookingPotCompat.java`
- `src/main/resources/fabric.mod.json`
- `src/main/resources/assets/kaleidoscope_cookery/lang/en_us.json`
- `src/main/resources/data/c/tags/item/vegetables.json`
- `src/main/resources/data/c/tags/item/foods/vegetable.json`
- `src/main/resources/data/c/tags/item/foods/tomato.json`
- `src/main/resources/data/c/tags/item/tools/knife.json`
- `src/gametest/java/com/github/ysbbbbbb/kaleidoscopecookery/gametest/KaleidoscopeCookeryGameTests.java`
- `src/gametest/resources/data/kaleidoscope_cookery_gametest/recipe/farmers_delight/component_container.json`
- `src/gametest/resources/data/kaleidoscope_cookery_gametest/recipe/farmers_delight/implicit_override_container.json`
- `FARMERS_DELIGHT_COMPAT_AUDIT.md`
