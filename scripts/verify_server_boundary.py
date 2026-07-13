#!/usr/bin/env python3
"""Check that common/server code does not import client-only classes."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery"
CLIENT_SRC = ROOT / "src/client/java/com/github/ysbbbbbb/kaleidoscopecookery"
DATAGEN_SRC = ROOT / "src/datagen/java/com/github/ysbbbbbb/kaleidoscopecookery"
MIXINS = ROOT / "src/main/resources/kaleidoscope_cookery.mixins.json"
CLIENT_MIXINS = ROOT / "src/client/resources/kaleidoscope_cookery.client.mixins.json"
FABRIC_MOD = ROOT / "src/main/resources/fabric.mod.json"
BUILD_GRADLE = ROOT / "build.gradle"
MOD_EVENTS = SRC / "init/ModEvents.java"
JADE_ITEM_STORAGE_PROVIDERS = (
    SRC / "compat/jade/block/FruitBasketComponentProvider.java",
    SRC / "compat/jade/block/KitchenwareRackComponentProvider.java",
    SRC / "compat/jade/block/TableComponentProvider.java",
    SRC / "compat/jade/block/PotComponentProvider.java",
    SRC / "compat/jade/block/StockpotComponentProvider.java",
    SRC / "compat/jade/block/SteamerComponentProvider.java",
)

LEGACY_CLIENT_LOCATIONS = (
    SRC / "KaleidoscopeCookeryClient.java",
    SRC / "client",
    SRC / "api/client",
    SRC / "mixin/client",
    SRC / "compat/jei",
    SRC / "compat/rei",
)

SERVER_EVENT_REGISTRATIONS = {
    "FarmerArmorEffectEvent": SRC / "event/server/effect/FarmerArmorEffectEvent.java",
    "HinderEffectEvent": SRC / "event/server/effect/HinderEffectEvent.java",
    "InstantSmeltingEffectEvent": SRC / "event/server/effect/InstantSmeltingEffectEvent.java",
    "SatiatedShieldEvent": SRC / "event/server/effect/SatiatedShieldEvent.java",
    "FlatulenceServerEvent": SRC / "event/server/effect/FlatulenceServerEvent.java",
    "PreservationEvent": SRC / "event/server/effect/PreservationEvent.java",
    "ServerEntityLoadEvent": SRC / "event/server/ServerEntityLoadEvent.java",
    "SickleHarvestNetherWartEvent": SRC / "event/server/SickleHarvestNetherWartEvent.java",
    "VitalityEffectEvent": SRC / "event/server/effect/VitalityEffectEvent.java",
}

INTERACTION_EVENT_REGISTRATIONS = {
    "CaterpillarChickenFeedEvent": SRC / "event/interaction/CaterpillarChickenFeedEvent.java",
    "WetFieldHoeUseEvent": SRC / "event/interaction/WetFieldHoeUseEvent.java",
}

LEGACY_SERVER_EVENT_PATHS = (
    SRC / "event/ArmorEffectHandler.java",
    SRC / "event/effect/FlatulenceServerEvent.java",
    SRC / "event/effect/HinderEvent.java",
    SRC / "event/effect/PreservationEvent.java",
    SRC / "event/effect/SatiatedShieldEvent.java",
    SRC / "event/effect/VitalityEvent.java",
    SRC / "event/SickleHarvestNetherWartEvent.java",
)

LEGACY_INTERACTION_EVENT_PATHS = (
    SRC / "event/HoeUseEvent.java",
    SRC / "event/PlayerUseEvent.java",
    SRC / "event/RightClickEvent.java",
    SRC / "event/WetFieldHoeUseEvent.java",
)

UNWIRED_LEGACY_EVENT_PATHS = (
    SRC / "api/event/IActionCancelable.java",
    SRC / "api/event/LivingDamageEvent.java",
    SRC / "api/event/MillstoneTakeItemEvent.java",
    SRC / "api/event/SickleHarvestEvent.java",
    SRC / "api/event/StockpotMatchRecipeEvent.java",
)

INSTANT_SMELTING_EVENT = SRC / "event/server/effect/InstantSmeltingEffectEvent.java"
LEGACY_INSTANT_SMELTING_MIXIN = SRC / "mixin/BlockMixin.java"
HINDER_EFFECT_EVENT = SRC / "event/server/effect/HinderEffectEvent.java"
VITALITY_EFFECT_EVENT = SRC / "event/server/effect/VitalityEffectEvent.java"
PROJECTILE_DODGE_HANDLER = SRC / "event/server/effect/ProjectileDodgeHandler.java"
PROJECTILE_MIXIN = SRC / "mixin/ProjectileMixin.java"
LEGACY_NEW_EFFECT_EVENTS = SRC / "event/server/effect/NewEffectEvents.java"
WET_FIELD_HOE_EVENT = SRC / "event/interaction/WetFieldHoeUseEvent.java"
CATERPILLAR_CHICKEN_FEED_EVENT = SRC / "event/interaction/CaterpillarChickenFeedEvent.java"
CHOPPING_BOARD_BLOCK_ENTITY = SRC / "blockentity/kitchen/ChoppingBoardBlockEntity.java"
CHOPPING_BOARD_RENDERER = CLIENT_SRC / "client/render/block/ChoppingBoardBlockEntityRender.java"
POT_BLOCK_ENTITY = SRC / "blockentity/kitchen/PotBlockEntity.java"
POT_RENDERER = CLIENT_SRC / "client/render/block/PotBlockEntityRender.java"
STOCKPOT_BLOCK = SRC / "block/kitchen/StockpotBlock.java"
STOCKPOT_BLOCK_ENTITY = SRC / "blockentity/kitchen/StockpotBlockEntity.java"
STOCKPOT_RENDERER = CLIENT_SRC / "client/render/block/StockpotBlockEntityRender.java"
MOB_SOUP_BASE_RENDERER = CLIENT_SRC / "client/render/soupbase/MobSoupBaseRender.java"
TEAPOT_BLOCK = SRC / "block/kitchen/TeapotBlock.java"
TEAPOT_BLOCK_ENTITY = SRC / "blockentity/kitchen/TeapotBlockEntity.java"
TEAPOT_RENDERER = CLIENT_SRC / "client/render/block/TeapotBlockEntityRender.java"
TEACUP_BLOCK = SRC / "block/drink/TeacupBlock.java"
EMPTY_CUP_BLOCK = SRC / "block/drink/EmptyCupBlock.java"
TABLE_BLOCK = SRC / "block/decoration/TableBlock.java"
CHAIR_BLOCK = SRC / "block/decoration/ChairBlock.java"
PLATE_BLOCK = SRC / "block/decoration/PlateBlock.java"
STACKABLE_FOOD_BLOCK = SRC / "block/decoration/StackableFoodBlock.java"
STOVE_BLOCK = SRC / "block/kitchen/StoveBlock.java"
ENAMEL_BASIN_BLOCK = SRC / "block/kitchen/EnamelBasinBlock.java"
SCARECROW_ITEM = SRC / "item/ScarecrowItem.java"
TRASH_CAN_BLOCK = SRC / "block/misc/TrashCanBlock.java"
TRASH_CAN_BLOCK_ENTITY = SRC / "blockentity/misc/TrashCanBlockEntity.java"
TRASH_CAN_RENDERER = CLIENT_SRC / "client/render/block/TrashCanBlockEntityRender.java"
FRUIT_BASKET_BLOCK = SRC / "block/decoration/FruitBasketBlock.java"
RECIPE_BLOCK = SRC / "block/misc/RecipeBlock.java"
OIL_POT_BLOCK = SRC / "block/kitchen/OilPotBlock.java"
SICKLE_NETHER_WART_EVENT = SRC / "event/server/SickleHarvestNetherWartEvent.java"
SICKLE_HARVEST_CALLBACK = SRC / "api/event/SickleHarvestCallback.java"
SICKLE_ITEM = SRC / "item/SickleItem.java"
RICE_CROP_BLOCK = SRC / "block/crop/RiceCropBlock.java"
BASE_CROP_BLOCK = SRC / "block/crop/BaseCropBlock.java"
CHILI_CROP_BLOCK = SRC / "block/crop/ChiliCropBlock.java"
MOD_LOOT_TABLES = SRC / "init/ModLootTables.java"
LOOT_TABLE_GENERATOR = DATAGEN_SRC / "datagen/LootTableGenerator.java"
BLOCK_INTERACT_LOOT_TABLES = DATAGEN_SRC / "datagen/lootable/BlockInteractLootTables.java"
SICKLE_HARVEST_BLACKLIST = (
    ROOT / "src/main/resources/data/kaleidoscope_cookery/tags/block/sickle_harvest_blacklist.json"
)

REQUIRED_SICKLE_HARVEST_BLACKLIST = {
    "minecraft:attached_melon_stem",
    "minecraft:attached_pumpkin_stem",
    "minecraft:melon_stem",
    "minecraft:pumpkin_stem",
}

CLIENT_ONLY_PATHS = (
    "client/",
    "mixin/client/",
    "compat/jei/",
    "compat/rei/",
    "api/client/",
)

CLIENT_ONLY_PATTERNS = (
    re.compile(r"^\s*import\s+net\.minecraft\.client\.", re.MULTILINE),
    re.compile(r"^\s*import\s+com\.github\.ysbbbbbb\.kaleidoscopecookery\.client\.", re.MULTILINE),
    re.compile(r"^\s*import\s+com\.github\.ysbbbbbb\.kaleidoscopecookery\.api\.client\.", re.MULTILINE),
    re.compile(r"^\s*import\s+me\.shedaniel\.rei\.api\.client\.", re.MULTILINE),
    re.compile(r"^\s*import\s+snownee\.jade\.api\.IWailaClientRegistration\s*;", re.MULTILINE),
    re.compile(r"^\s*import\s+snownee\.jade\.api\.ui\.", re.MULTILINE),
    re.compile(r"^\s*import\s+snownee\.jade\.api\.view\.IClientExtensionProvider\s*;", re.MULTILINE),
    re.compile(r"\bEnvType\.CLIENT\b"),
    re.compile(r"@Environment\s*\(\s*EnvType\.CLIENT\s*\)"),
)


def relative_java_path(path: Path) -> str:
    return path.relative_to(SRC).as_posix()


def is_client_only_path(path: Path) -> bool:
    rel = relative_java_path(path)
    return rel == "KaleidoscopeCookeryClient.java" or rel.startswith(CLIENT_ONLY_PATHS)


def main() -> int:
    errors: list[str] = []

    client_entrypoint = CLIENT_SRC / "KaleidoscopeCookeryClient.java"
    if not client_entrypoint.exists():
        errors.append(f"Client entrypoint is missing from the client source set: {client_entrypoint.relative_to(ROOT)}")
    for path in LEGACY_CLIENT_LOCATIONS:
        if path.is_file() or (path.is_dir() and any(path.rglob("*.java"))):
            errors.append(f"Client source remains in the common source set: {path.relative_to(ROOT)}")

    for path in sorted(SRC.rglob("*.java")):
        if is_client_only_path(path):
            continue
        text = path.read_text(encoding="utf-8")
        for pattern in CLIENT_ONLY_PATTERNS:
            if pattern.search(text):
                errors.append(f"{path.relative_to(ROOT)} contains client-only reference: {pattern.pattern}")

    chopping_board = CHOPPING_BOARD_BLOCK_ENTITY.read_text(encoding="utf-8")
    if "cacheModels" in chopping_board or "previousModel" in chopping_board:
        errors.append("ChoppingBoardBlockEntity still stores client-only model caches in common state.")
    chopping_board_renderer = CHOPPING_BOARD_RENDERER.read_text(encoding="utf-8")
    if "Mth.clamp(choppingBoard.getCurrentCutCount()" not in chopping_board_renderer:
        errors.append("Chopping board renderer does not clamp its derived model stage.")
    if '"chopping_board/" + modelId.getPath() + "/" + index' not in chopping_board_renderer:
        errors.append("Chopping board renderer does not derive the stage model from synchronized state.")
    pot_block_entity = POT_BLOCK_ENTITY.read_text(encoding="utf-8")
    if "StirFryAnimationData" in pot_block_entity or "animationData" in pot_block_entity:
        errors.append("PotBlockEntity still stores client-only stir-fry animation state.")
    if "public long seed" in pot_block_entity or "System.currentTimeMillis()" in pot_block_entity:
        errors.append("PotBlockEntity still exposes or wall-clock-generates its render seed.")
    pot_renderer = POT_RENDERER.read_text(encoding="utf-8")
    if "new WeakHashMap<>()" not in pot_renderer or "computeIfAbsent(pot" not in pot_renderer:
        errors.append("Pot renderer does not own weakly keyed per-block animation state.")
    if "Util.getMillis()" not in pot_renderer:
        errors.append("Pot renderer does not use Minecraft's monotonic client clock.")
    stockpot_block_entity = STOCKPOT_BLOCK_ENTITY.read_text(encoding="utf-8")
    if "renderEntity" in stockpot_block_entity or "clientTick()" in stockpot_block_entity:
        errors.append("StockpotBlockEntity still stores or ticks a client-only render entity.")
    if "public StockpotVisuals visuals" in stockpot_block_entity or "RecipeHolder<StockpotRecipe> recipe =" in stockpot_block_entity:
        errors.append("StockpotBlockEntity exposes or redundantly caches recipe rendering state.")
    if stockpot_block_entity.count("bucket.consume(1, user)") < 2:
        errors.append("Stockpot soup-base transfers do not use vanilla ItemStack.consume().")
    if "bucket.shrink(1)" in stockpot_block_entity:
        errors.append("Stockpot soup-base transfers still manually shrink bucket stacks.")
    stockpot_block = STOCKPOT_BLOCK.read_text(encoding="utf-8")
    if "level.isClientSide() || blockEntityType != ModBlocks.STOCKPOT_BE" not in stockpot_block:
        errors.append("StockpotBlock still installs a common block entity ticker on the client.")
    mob_soup_renderer = MOB_SOUP_BASE_RENDERER.read_text(encoding="utf-8")
    if "new WeakHashMap<>()" not in mob_soup_renderer or "renderEntities.put(stockpot" not in mob_soup_renderer:
        errors.append("Mob soup rendering does not own a weakly keyed entity cache.")
    if "renderEntity.tickCount = (int) world.getGameTime();" not in mob_soup_renderer:
        errors.append("Mob soup render entities do not follow client world time.")
    stockpot_renderer = STOCKPOT_RENDERER.read_text(encoding="utf-8")
    if "stockpot.getVisuals()" not in stockpot_renderer or "stockpot.recipe" in stockpot_renderer:
        errors.append("Stockpot renderer does not use the block entity's immutable visual snapshot.")
    teapot_block_entity = TEAPOT_BLOCK_ENTITY.read_text(encoding="utf-8")
    if "AnimationState" in teapot_block_entity or "clientTick(" in teapot_block_entity:
        errors.append("TeapotBlockEntity still stores or updates client-only animation state.")
    for name, source in (
        ("PotBlockEntity", pot_block_entity),
        ("StockpotBlockEntity", stockpot_block_entity),
        ("TeapotBlockEntity", teapot_block_entity),
    ):
        for legacy_shrink in ("stack.shrink(", "mainHandItem.shrink(", "getMainHandItem().shrink("):
            if legacy_shrink in source:
                errors.append(f"{name} still manually shrinks a player-provided stack: {legacy_shrink}")
    teapot_block = TEAPOT_BLOCK.read_text(encoding="utf-8")
    if "level.isClientSide() || blockEntityType != ModBlocks.TEAPOT_BE" not in teapot_block:
        errors.append("TeapotBlock still installs its block entity ticker on the client.")
    teapot_renderer = TEAPOT_RENDERER.read_text(encoding="utf-8")
    if "new WeakHashMap<>()" not in teapot_renderer or "boilingStates.computeIfAbsent(teapot" not in teapot_renderer:
        errors.append("Teapot renderer does not own weakly keyed boiling animation state.")
    trash_can_block_entity = TRASH_CAN_BLOCK_ENTITY.read_text(encoding="utf-8")
    if "AnimationState" in trash_can_block_entity or "clientTick(" in trash_can_block_entity:
        errors.append("TrashCanBlockEntity still stores or ticks client-only animation state.")
    trash_can_block = TRASH_CAN_BLOCK.read_text(encoding="utf-8")
    if "getTicker(" in trash_can_block:
        errors.append("TrashCanBlock still installs an unnecessary block entity ticker.")
    trash_can_renderer = TRASH_CAN_RENDERER.read_text(encoding="utf-8")
    if "new WeakHashMap<>()" not in trash_can_renderer or "animationStates.computeIfAbsent(trashCan" not in trash_can_renderer:
        errors.append("Trash can renderer does not own weakly keyed animation state.")

    entrypoints = json.loads(FABRIC_MOD.read_text(encoding="utf-8"))["entrypoints"]
    expected_optional_entrypoints = {
        "jei_mod_plugin": ["com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.ModJeiPlugin"],
        "rei_client": ["com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.ModREIClientPlugin"],
        "jade": [
            "com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin",
            "com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModClientPlugin",
        ],
    }
    for key, expected in expected_optional_entrypoints.items():
        if entrypoints.get(key) != expected:
            errors.append(f"fabric.mod.json {key} entrypoints should be {expected}.")

    build_text = BUILD_GRADLE.read_text(encoding="utf-8")
    for compat in ("jade", "rei"):
        excluded_path = f"compat/{compat}/**"
        if excluded_path in build_text:
            errors.append(f"build.gradle still excludes optional integration sources: {excluded_path}")

    for path in JADE_ITEM_STORAGE_PROVIDERS:
        provider_text = path.read_text(encoding="utf-8")
        if "ItemStackContainer" in provider_text:
            errors.append(f"Jade item storage provider wraps display data in ItemStackContainer: {path.relative_to(ROOT)}")
        if ".map(ItemStack::copy)" not in provider_text:
            errors.append(f"Jade item storage provider does not copy its ItemStack snapshot: {path.relative_to(ROOT)}")

    mixin_data = json.loads(MIXINS.read_text(encoding="utf-8"))
    client_mixin_data = json.loads(CLIENT_MIXINS.read_text(encoding="utf-8"))
    for mixin in mixin_data.get("mixins", []):
        if mixin.startswith("client."):
            errors.append(f"Client mixin listed in common mixins section: {mixin}")
    if mixin_data.get("client"):
        errors.append("Common Mixin config still contains a client section.")
    for mixin in client_mixin_data.get("client", []):
        if not mixin.startswith("client."):
            errors.append(f"Non-client mixin listed in client mixins section: {mixin}")
    if client_mixin_data.get("mixins"):
        errors.append("Client Mixin config contains a common mixins section.")

    mod_events = MOD_EVENTS.read_text(encoding="utf-8")
    event_registrations = SERVER_EVENT_REGISTRATIONS | INTERACTION_EVENT_REGISTRATIONS
    for event_class, path in event_registrations.items():
        if not path.exists():
            errors.append(f"Migrated server event source is missing: {path.relative_to(ROOT)}")
            continue
        event_text = path.read_text(encoding="utf-8")
        if re.search(r"public\s+static\s+void\s+register\s*\(\s*\)", event_text) is None:
            errors.append(f"{event_class} does not expose a no-argument register() entrypoint.")
        if f"{event_class}.register();" not in mod_events:
            errors.append(f"ModEvents does not register {event_class}.")

    for path in LEGACY_SERVER_EVENT_PATHS:
        if path.exists():
            errors.append(f"Legacy duplicate server event still exists: {path.relative_to(ROOT)}")

    for path in LEGACY_INTERACTION_EVENT_PATHS:
        if path.exists():
            errors.append(f"Legacy interaction event still exists: {path.relative_to(ROOT)}")

    for path in UNWIRED_LEGACY_EVENT_PATHS:
        if path.exists():
            errors.append(f"Unwired legacy event still exists: {path.relative_to(ROOT)}")

    action_event_path = SRC / "api/event/ActionEvent.java"
    if action_event_path.exists():
        errors.append("Legacy event-bus-style ActionEvent base class still exists.")
    recipe_item_event_path = SRC / "api/event/RecipeItemEvent.java"
    if "extends ActionEvent" in recipe_item_event_path.read_text(encoding="utf-8"):
        errors.append("RecipeItemEvent still extends the legacy ActionEvent base class.")
    recipe_item_event_text = recipe_item_event_path.read_text(encoding="utf-8")
    recipe_item_text = (SRC / "item/RecipeItem.java").read_text(encoding="utf-8")
    if "int[] needCount" in recipe_item_event_text or "new int[]{needCount}" in recipe_item_text:
        errors.append("Recipe item deduction still uses a single-element mutable array.")
    millstone_finish_event_path = SRC / "api/event/MillstoneFinishEvent.java"
    if millstone_finish_event_path.exists():
        errors.append("Millstone finish callbacks still allocate a legacy event wrapper.")
    action_event_callback = (SRC / "api/event/ActionEventCallback.java").read_text(encoding="utf-8")
    if "onMillstoneFinish(MillstoneBlockEntity millstone, @Nullable Mob bindEntity)" not in action_event_callback:
        errors.append("Millstone finish callback does not expose direct Fabric-style parameters.")

    satiated_shield_text = (SRC / "event/server/effect/SatiatedShieldEvent.java").read_text(encoding="utf-8")
    for required_reference in (
        "ServerLivingEntityEvents.ALLOW_DAMAGE.register(SatiatedShieldEvent::onAllowDamage)",
        "REMAINING_DAMAGE_BYPASS.contains(player.getUUID())",
        "source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)",
        "player.causeFoodExhaustion(exhaustionAmount)",
        "float excessExhaustion = exhaustionAmount - availableExhaustion",
        "applyBypassingShield(player, source, excessExhaustion / exhaustionPerDamage)",
        "return false;",
    ):
        if required_reference not in satiated_shield_text:
            errors.append(f"Satiated shield Fabric damage handling is missing {required_reference}.")
    for legacy_reference in ("LivingDamageEvent", "ModEvents.LIVING_ENTITY_HURT"):
        if legacy_reference in satiated_shield_text or legacy_reference in mod_events:
            errors.append(f"Satiated shield still uses legacy damage event bridge: {legacy_reference}.")

    if INSTANT_SMELTING_EVENT.exists():
        instant_smelting_text = INSTANT_SMELTING_EVENT.read_text(encoding="utf-8")
        for required_reference in (
            "LootTableEvents.MODIFY_DROPS",
            "LootContextParams.BLOCK_STATE",
            "LootContextParams.THIS_ENTITY",
            "ConventionalBlockTags.ORES",
            "isRootBlockLootTable",
        ):
            if required_reference not in instant_smelting_text:
                errors.append(f"Instant smelting event is missing {required_reference}.")
    if LEGACY_INSTANT_SMELTING_MIXIN.exists():
        errors.append(f"Legacy instant smelting mixin still exists: {LEGACY_INSTANT_SMELTING_MIXIN.relative_to(ROOT)}")
    if "BlockMixin" in mixin_data.get("mixins", []):
        errors.append("Legacy instant smelting BlockMixin is still registered.")

    if HINDER_EFFECT_EVENT.exists():
        hinder_text = HINDER_EFFECT_EVENT.read_text(encoding="utf-8")
        for required_reference in (
            "ServerLivingEntityEvents.AFTER_DAMAGE",
            "blocked || damageTaken <= 0.0F",
            "ModEffects.HINDER",
        ):
            if required_reference not in hinder_text:
                errors.append(f"Hinder effect event is missing {required_reference}.")

    if VITALITY_EFFECT_EVENT.exists():
        vitality_text = VITALITY_EFFECT_EVENT.read_text(encoding="utf-8")
        for required_reference in (
            "ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY",
            "killer instanceof LivingEntity",
            "ModEffects.VITALITY",
        ):
            if required_reference not in vitality_text:
                errors.append(f"Vitality effect event is missing {required_reference}.")

    if LEGACY_NEW_EFFECT_EVENTS.exists():
        errors.append(f"Legacy combined effect handler still exists: {LEGACY_NEW_EFFECT_EVENTS.relative_to(ROOT)}")

    if not PROJECTILE_DODGE_HANDLER.exists():
        errors.append(f"Projectile dodge handler is missing: {PROJECTILE_DODGE_HANDLER.relative_to(ROOT)}")
    else:
        projectile_handler_text = PROJECTILE_DODGE_HANDLER.read_text(encoding="utf-8")
        for required_reference in (
            "DODGE_DURATION_COST = 200",
            "living.hasEffect(ModEffects.PROJECTILE_DODGE)",
            "randomTeleport",
        ):
            if required_reference not in projectile_handler_text:
                errors.append(f"Projectile dodge handler is missing {required_reference}.")

    if not PROJECTILE_MIXIN.exists():
        errors.append(f"Projectile mixin is missing: {PROJECTILE_MIXIN.relative_to(ROOT)}")
    else:
        projectile_mixin_text = PROJECTILE_MIXIN.read_text(encoding="utf-8")
        for required_reference in (
            "hitTargetOrDeflectSelf",
            "CallbackInfoReturnable<ProjectileDeflection>",
            "ProjectileDodgeHandler.dodgeProjectile",
            "ProjectileDeflection.NONE",
        ):
            if required_reference not in projectile_mixin_text:
                errors.append(f"Projectile mixin is missing {required_reference}.")
        if 'method = "onHitEntity"' in projectile_mixin_text:
            errors.append("Projectile mixin still injects the overridable onHitEntity method.")
    if "ProjectileMixin" not in mixin_data.get("mixins", []):
        errors.append("ProjectileMixin is not registered as a common mixin.")

    if WET_FIELD_HOE_EVENT.exists():
        wet_field_text = WET_FIELD_HOE_EVENT.read_text(encoding="utf-8")
        for required_reference in (
            "UseBlockCallback.EVENT",
            "player.isSpectator()",
            "FluidTags.WATER",
            "level.mayInteract(player, pos)",
            "player.mayUseItemAt(pos, hitResult.getDirection(), stack)",
            "InteractionResult.FAIL",
            "if (!level.setBlockAndUpdate(pos, farmland))",
            "GameEvent.BLOCK_CHANGE",
            "player.hasInfiniteMaterials()",
            "USE_HOE_ON_WATER_FIELD",
        ):
            if required_reference not in wet_field_text:
                errors.append(f"Wet field hoe event is missing {required_reference}.")

    if CATERPILLAR_CHICKEN_FEED_EVENT.exists():
        chicken_feed_text = CATERPILLAR_CHICKEN_FEED_EVENT.read_text(encoding="utf-8")
        for required_reference in (
            "UseEntityCallback.EVENT",
            "player.isSpectator()",
            "player.getItemInHand(hand)",
            "player.hasInfiniteMaterials()",
            "USE_CATERPILLAR_FEED_CHICKEN",
        ):
            if required_reference not in chicken_feed_text:
                errors.append(f"Caterpillar chicken feed event is missing {required_reference}.")

    fruit_basket_text = FRUIT_BASKET_BLOCK.read_text(encoding="utf-8")
    for required_reference in (
        "useItemOn",
        "useWithoutItem",
        "if (!level.isClientSide())",
        "!player.hasInfiniteMaterials()",
    ):
        if required_reference not in fruit_basket_text:
            errors.append(f"Fruit basket native interaction is missing {required_reference}.")

    for name, path, expected_consumes in (
        ("TeacupBlock", TEACUP_BLOCK, 2),
        ("EmptyCupBlock", EMPTY_CUP_BLOCK, 1),
    ):
        drink_block_text = path.read_text(encoding="utf-8")
        if drink_block_text.count("itemInHand.consume(1, player)") < expected_consumes:
            errors.append(f"{name} does not use vanilla ItemStack.consume() for cup stacking.")
        if "itemInHand.shrink(" in drink_block_text:
            errors.append(f"{name} still manually shrinks cup stacks.")

    for name, path, consume_expression, expected_consumes, shrink_expression in (
        ("TableBlock", TABLE_BLOCK, "itemInHand.consume(1, player)", 3, "itemInHand.shrink("),
        ("ChairBlock", CHAIR_BLOCK, "itemInHand.consume(1, player)", 2, "itemInHand.shrink("),
        ("PlateBlock", PLATE_BLOCK, "stack.consume(1, player)", 1, "stack.shrink("),
        ("StackableFoodBlock", STACKABLE_FOOD_BLOCK, "stack.consume(1, player)", 1, "stack.shrink("),
    ):
        decoration_block_text = path.read_text(encoding="utf-8")
        if decoration_block_text.count(consume_expression) < expected_consumes:
            errors.append(f"{name} does not use vanilla ItemStack.consume() for player item consumption.")
        if shrink_expression in decoration_block_text:
            errors.append(f"{name} still manually shrinks player item stacks.")

    stove_block_text = STOVE_BLOCK.read_text(encoding="utf-8")
    if "itemInHand.consume(1, player)" not in stove_block_text:
        errors.append("StoveBlock does not use vanilla ItemStack.consume() for fire charges.")
    if "itemInHand.shrink(" in stove_block_text:
        errors.append("StoveBlock still manually shrinks fire charges.")

    scarecrow_item_text = SCARECROW_ITEM.read_text(encoding="utf-8")
    for required_reference in (
        "if (!level.isClientSide())",
        "stack.consume(1, context.getPlayer())",
    ):
        if required_reference not in scarecrow_item_text:
            errors.append(f"ScarecrowItem placement consumption is missing {required_reference}.")
    if "stack.shrink(" in scarecrow_item_text:
        errors.append("ScarecrowItem still manually shrinks the placement stack.")

    enamel_basin_text = ENAMEL_BASIN_BLOCK.read_text(encoding="utf-8")
    if "mainHandItem.consume(consumeCount, player)" not in enamel_basin_text:
        errors.append("EnamelBasinBlock does not use vanilla ItemStack.consume() for bulk oil insertion.")
    if "mainHandItem.shrink(" in enamel_basin_text:
        errors.append("EnamelBasinBlock still manually shrinks inserted oil stacks.")

    recipe_block_text = RECIPE_BLOCK.read_text(encoding="utf-8")
    for required_reference in (
        "useWithoutItem",
        "player.getMainHandItem().isEmpty()",
        "if (!level.removeBlock(pos, false))",
        "player.setItemInHand(InteractionHand.MAIN_HAND, returnedStack)",
        "GameEvent.BLOCK_DESTROY",
        "GameEvent.Context.of(player, state)",
    ):
        if required_reference not in recipe_block_text:
            errors.append(f"RecipeBlock native empty-hand interaction is missing {required_reference}.")
    if "useItemOn" in recipe_block_text:
        errors.append("RecipeBlock still handles empty-hand removal through the item interaction path.")
    remove_recipe_block_index = recipe_block_text.find("level.removeBlock(pos, false)")
    return_recipe_item_index = recipe_block_text.find(
        "player.setItemInHand(InteractionHand.MAIN_HAND, returnedStack)"
    )
    if remove_recipe_block_index > return_recipe_item_index:
        errors.append("RecipeBlock returns the recipe item before confirming block removal.")

    oil_pot_text = OIL_POT_BLOCK.read_text(encoding="utf-8")
    for required_reference in (
        "useItemOn",
        "hand != InteractionHand.MAIN_HAND || !stack.is(ModItems.OIL)",
        "stack.consume(addOilCount, player)",
        "useWithoutItem",
        "player.getMainHandItem().isEmpty()",
        "player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.OIL, takeCount))",
    ):
        if required_reference not in oil_pot_text:
            errors.append(f"Oil pot native interaction is missing {required_reference}.")
    if "stack.isEmpty()" in oil_pot_text:
        errors.append("OilPotBlock still handles empty-hand extraction through the item interaction path.")
    if "stack.shrink(addOilCount)" in oil_pot_text:
        errors.append("OilPotBlock still manually shrinks oil instead of using ItemStack.consume().")
    if oil_pot_text.count("GameEvent.BLOCK_CHANGE") < 2:
        errors.append("OilPotBlock does not emit block-change events for both oil transfer directions.")

    if SICKLE_NETHER_WART_EVENT.exists():
        sickle_nether_wart_text = SICKLE_NETHER_WART_EVENT.read_text(encoding="utf-8")
        for required_reference in (
            "SickleHarvestCallback.EVENT.register(SickleHarvestNetherWartEvent::onSickleHarvest)",
            "if (!player.gameMode.destroyBlock(pos))",
            "level.getBlockState(pos).isAir()",
            "Blocks.NETHER_WART.defaultBlockState()",
            "SickleHarvestCallback.Result.HARVESTED",
            "SickleHarvestCallback.Result.SKIP",
        ):
            if required_reference not in sickle_nether_wart_text:
                errors.append(f"Sickle nether wart event is missing {required_reference}.")
        for duplicate_side_effect in ("LevelEvent.PARTICLES_DESTROY_BLOCK",):
            if duplicate_side_effect in sickle_nether_wart_text:
                errors.append(f"Sickle nether wart event retains duplicate side effect: {duplicate_side_effect}.")

    if not SICKLE_HARVEST_CALLBACK.exists():
        errors.append(f"Sickle harvest callback is missing: {SICKLE_HARVEST_CALLBACK.relative_to(ROOT)}")
    else:
        sickle_callback_text = SICKLE_HARVEST_CALLBACK.read_text(encoding="utf-8")
        for required_reference in (
            "Event<SickleHarvestCallback> EVENT = EventFactory.createArrayBacked(",
            "Result.PASS",
            "SKIP(true, false)",
            "HARVESTED(true, true)",
        ):
            if required_reference not in sickle_callback_text:
                errors.append(f"Sickle harvest callback is missing {required_reference}.")
    for legacy_reference in ("ModEvents.SICKLE_HARVEST", "SickleHarvestEvent"):
        if legacy_reference in mod_events:
            errors.append(f"ModEvents still exposes legacy sickle event state: {legacy_reference}.")

    sickle_item_text = SICKLE_ITEM.read_text(encoding="utf-8")
    if re.search(
        r"public\s+boolean\s+canDestroyBlock\s*\([^)]*LivingEntity\s+\w+\s*\)\s*\{\s*return\s+true\s*;\s*\}",
        sickle_item_text,
        re.DOTALL,
    ) is None:
        errors.append("SickleItem does not preserve creative-mode block destruction.")
    for required_reference in (
        "ThreadLocal<BlockPos> activeHarvestPos",
        "pos.equals(activeHarvestPos.get())",
        "return super.mineBlock(stack, level, state, pos, entity)",
        "BlockPos previousHarvestPos = activeHarvestPos.get()",
        "activeHarvestPos.set(newPos)",
        "return harvestBlock(newPos, level, player, stack, blockState)",
        "} finally {",
        "activeHarvestPos.remove()",
        "activeHarvestPos.set(previousHarvestPos)",
        "SickleHarvestCallback.EVENT.invoker().harvest(player, stack, newPos, blockState)",
        "callbackResult.handled()",
        "callbackResult.costsDurability()",
    ):
        if required_reference not in sickle_item_text:
            errors.append(f"SickleItem scoped durability handling is missing {required_reference}.")
    if re.search(
        r"if\s*\(context\.getHand\(\)\s*!=\s*InteractionHand\.MAIN_HAND\)\s*\{\s*"
        r"return\s+InteractionResult\.PASS\s*;\s*\}",
        sickle_item_text,
        re.DOTALL,
    ) is None:
        errors.append("SickleItem does not reject unsupported offhand harvesting.")
    for required_reference in (
        "!player.hasInfiniteMaterials()",
        "breakCount > 0",
    ):
        if required_reference not in sickle_item_text:
            errors.append(f"SickleItem durability handling is missing {required_reference}.")

    crop_harvest_start = sickle_item_text.find("if (block instanceof CropBlock")
    crop_harvest_end = sickle_item_text.find("// 如果是植被", crop_harvest_start)
    if crop_harvest_start < 0 or crop_harvest_end < 0:
        errors.append("SickleItem crop harvest branch is missing.")
    else:
        crop_harvest_body = sickle_item_text[crop_harvest_start:crop_harvest_end]
        for required_reference in (
            "activeHarvestPos.set(newPos)",
            "serverPlayer.gameMode.destroyBlock(newPos)",
            "blockState.getFluidState().createLegacyBlock()",
            "riceCropBlock.replantAfterHarvestIfUnchanged",
        ):
            if required_reference not in crop_harvest_body:
                errors.append(f"SickleItem crop harvest is missing {required_reference}.")
        for legacy_reference in (
            "playerDestroy",
            "LevelEvent.PARTICLES_DESTROY_BLOCK",
        ):
            if legacy_reference in crop_harvest_body:
                errors.append(f"SickleItem crop harvest retains legacy side effect: {legacy_reference}.")
        harvest_marker_index = crop_harvest_body.find("activeHarvestPos.set(newPos)")
        destroy_block_index = crop_harvest_body.find("serverPlayer.gameMode.destroyBlock(newPos)")
        if harvest_marker_index > destroy_block_index:
            errors.append("SickleItem normalizes the rice durability marker after destroying the crop.")

    rice_crop_text = RICE_CROP_BLOCK.read_text(encoding="utf-8")
    for required_reference in (
        "replantAfterHarvestIfUnchanged",
        "bottomState.getFluidState().createLegacyBlock()",
        "middleState.getFluidState().createLegacyBlock()",
        "upperState.getFluidState().createLegacyBlock()",
        "getReplantedState(DOWN, bottomState)",
        "getReplantedState(MIDDLE, middleState)",
        "getReplantedState(UP, upperState)",
    ):
        if required_reference not in rice_crop_text:
            errors.append(f"RiceCropBlock harvest replanting is missing {required_reference}.")
    last_replant_guard_index = rice_crop_text.find("upperState.getFluidState().createLegacyBlock()")
    first_replant_write_index = rice_crop_text.find("level.setBlock(basePos, getReplantedState")
    if last_replant_guard_index > first_replant_write_index:
        errors.append("RiceCropBlock writes replanted sections before validating all harvested positions.")

    base_crop_text = BASE_CROP_BLOCK.read_text(encoding="utf-8")
    for required_reference in (
        "level instanceof ServerLevel serverLevel",
        "dropFromBlockInteractLootTable(",
        "level.getBlockEntity(pos)",
        "this.onUseBreakCrop(state, serverLevel, pos, player)",
        "state.setValue(AGE, ageAfterUse)",
        "GameEvent.BLOCK_CHANGE",
        "GameEvent.Context.of(player, harvestedState)",
    ):
        if required_reference not in base_crop_text:
            errors.append(f"BaseCropBlock native interaction harvest is missing {required_reference}.")
    loot_drop_index = base_crop_text.find("dropFromBlockInteractLootTable(")
    reset_crop_index = base_crop_text.find("this.onUseBreakCrop(state, serverLevel, pos, player)")
    if loot_drop_index > reset_crop_index:
        errors.append("BaseCropBlock resets the crop before evaluating its interaction loot table.")
    if "this.result" in base_crop_text:
        errors.append("BaseCropBlock still retains the legacy manual harvest result supplier.")

    chili_crop_text = CHILI_CROP_BLOCK.read_text(encoding="utf-8")
    if "ModLootTables.HARVEST_CHILI_CROP" not in chili_crop_text:
        errors.append("ChiliCropBlock does not use its block-interaction harvest loot table.")
    for legacy_reference in ("nextInt", "Block.popResource", "useWithoutItem"):
        if legacy_reference in chili_crop_text:
            errors.append(f"ChiliCropBlock retains legacy harvest logic: {legacy_reference}.")

    mod_loot_tables_text = MOD_LOOT_TABLES.read_text(encoding="utf-8")
    for required_reference in (
        'create("harvest/tomato_crop")',
        'create("harvest/chili_crop")',
        "Registries.LOOT_TABLE",
    ):
        if required_reference not in mod_loot_tables_text:
            errors.append(f"ModLootTables is missing {required_reference}.")

    loot_table_generator_text = LOOT_TABLE_GENERATOR.read_text(encoding="utf-8")
    if "LootContextParamSets.BLOCK_INTERACT" not in loot_table_generator_text:
        errors.append("LootTableGenerator does not register a BLOCK_INTERACT sub-provider.")

    block_interact_loot_text = BLOCK_INTERACT_LOOT_TABLES.read_text(encoding="utf-8")
    for required_reference in (
        "ModLootTables.HARVEST_TOMATO_CROP",
        "ModLootTables.HARVEST_CHILI_CROP",
        "AlternativesEntry.alternatives(",
        "LootItemRandomChanceCondition.randomChance(0.2F)",
    ):
        if required_reference not in block_interact_loot_text:
            errors.append(f"Block interaction loot generation is missing {required_reference}.")

    vegetation_harvest = re.search(
        r"if\s*\(block\s+instanceof\s+VegetationBlock.*?serverPlayer\s*\)\s*\{(?P<body>.*?)\n\s*\}",
        sickle_item_text,
        re.DOTALL,
    )
    if vegetation_harvest is None:
        errors.append("SickleItem vegetation harvest branch is missing.")
    else:
        vegetation_harvest_body = vegetation_harvest.group("body")
        for required_reference in (
            "return serverPlayer.gameMode.destroyBlock(newPos)",
            "!level.getBlockState(newPos).equals(blockState)",
        ):
            if required_reference not in vegetation_harvest_body:
                errors.append(f"SickleItem vegetation harvest is missing {required_reference}.")
        if "LevelEvent.PARTICLES_DESTROY_BLOCK" in vegetation_harvest_body:
            errors.append("SickleItem vegetation harvest retains duplicate destroy particles.")
    if "instanceof BushBlock" in sickle_item_text:
        errors.append("SickleItem still uses the legacy BushBlock-only vegetation boundary.")

    if not SICKLE_HARVEST_BLACKLIST.exists():
        errors.append(f"Sickle harvest blacklist is missing: {SICKLE_HARVEST_BLACKLIST.relative_to(ROOT)}")
    else:
        blacklist_data = json.loads(SICKLE_HARVEST_BLACKLIST.read_text(encoding="utf-8"))
        blacklist_values = {
            value for value in blacklist_data.get("values", []) if isinstance(value, str)
        }
        missing_blacklist_values = REQUIRED_SICKLE_HARVEST_BLACKLIST - blacklist_values
        for value in sorted(missing_blacklist_values):
            errors.append(f"Sickle harvest blacklist is missing {value}.")

    if errors:
        print("Server boundary verification failed:")
        print("\n".join(errors))
        return 1

    print("Server boundary verification passed.")
    print("  common/server sources contain no direct client-only imports")
    print(f"  migrated server events: {len(SERVER_EVENT_REGISTRATIONS)}")
    print(f"  migrated interaction events: {len(INTERACTION_EVENT_REGISTRATIONS)}")
    print(f"  legacy server event paths checked: {len(LEGACY_SERVER_EVENT_PATHS)}")
    print(f"  unwired legacy event paths checked: {len(UNWIRED_LEGACY_EVENT_PATHS)}")
    print(f"  Jade item storage snapshots: {len(JADE_ITEM_STORAGE_PROVIDERS)}")
    print(f"  common mixins: {len(mixin_data.get('mixins', []))}")
    print(f"  client mixins: {len(client_mixin_data.get('client', []))}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
