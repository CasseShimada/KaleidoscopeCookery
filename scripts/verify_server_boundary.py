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
OVERLAY_DATA = SRC / "compat/overlay/CookeryOverlayData.java"
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
    "HinderEffectEvent": SRC / "event/server/effect/HinderEffectEvent.java",
    "InstantSmeltingEffectEvent": SRC / "event/server/effect/InstantSmeltingEffectEvent.java",
    "SatiatedShieldEvent": SRC / "event/server/effect/SatiatedShieldEvent.java",
    "FlatulenceServerEvent": SRC / "event/server/effect/FlatulenceServerEvent.java",
    "ServerEntityLoadEvent": SRC / "event/server/ServerEntityLoadEvent.java",
    "SickleHarvestNetherWartEvent": SRC / "event/server/SickleHarvestNetherWartEvent.java",
    "VitalityEffectEvent": SRC / "event/server/effect/VitalityEffectEvent.java",
}

LIVING_ENTITY_MIXIN_EVENTS = {
    "FarmerArmorEffectEvent": SRC / "event/server/effect/FarmerArmorEffectEvent.java",
    "PreservationEvent": SRC / "event/server/effect/PreservationEvent.java",
}

INTERACTION_EVENT_REGISTRATIONS = {
    "CaterpillarChickenFeedEvent": SRC / "event/interaction/CaterpillarChickenFeedEvent.java",
    "FruitBasketTakeOutEvent": SRC / "event/interaction/FruitBasketTakeOutEvent.java",
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
SERVER_ENTITY_LOAD_EVENT = SRC / "event/server/ServerEntityLoadEvent.java"
EXTRA_LOOT_TABLE_DROP = SRC / "event/server/loot/ExtraLootTableDrop.java"
PROJECTILE_DODGE_HANDLER = SRC / "event/server/effect/ProjectileDodgeHandler.java"
PROJECTILE_MIXIN = SRC / "mixin/ProjectileMixin.java"
VIGOR_EFFECT = SRC / "effect/VigorEffect.java"
FOOD_DATA_ACCESSOR = SRC / "mixin/FoodDataAccessor.java"
WARMTH_EFFECT = SRC / "effect/WarmthEffect.java"
LEGACY_NEW_EFFECT_EVENTS = SRC / "event/server/effect/NewEffectEvents.java"
WET_FIELD_HOE_EVENT = SRC / "event/interaction/WetFieldHoeUseEvent.java"
CATERPILLAR_CHICKEN_FEED_EVENT = SRC / "event/interaction/CaterpillarChickenFeedEvent.java"
FRUIT_BASKET_TAKE_OUT_EVENT = SRC / "event/interaction/FruitBasketTakeOutEvent.java"
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
TEAPOT_ITEM = SRC / "item/TeapotItem.java"
TEAPOT_RENDERER = CLIENT_SRC / "client/render/block/TeapotBlockEntityRender.java"
TEACUP_BLOCK = SRC / "block/drink/TeacupBlock.java"
EMPTY_CUP_BLOCK = SRC / "block/drink/EmptyCupBlock.java"
TABLE_BLOCK = SRC / "block/decoration/TableBlock.java"
CHAIR_BLOCK = SRC / "block/decoration/ChairBlock.java"
COOK_STOOL_BLOCK = SRC / "block/decoration/CookStoolBlock.java"
PLATE_BLOCK = SRC / "block/decoration/PlateBlock.java"
STACKABLE_FOOD_BLOCK = SRC / "block/decoration/StackableFoodBlock.java"
FOOD_BITE_BLOCK = SRC / "block/food/FoodBiteBlock.java"
FOOD_BITE_ONE_BY_TWO_BLOCK = SRC / "block/food/FoodBiteOneByTwoBlock.java"
FOOD_BITE_THREE_BY_THREE_BLOCK = SRC / "block/food/FoodBiteThreeByThreeBlock.java"
MILLSTONE_BLOCK = SRC / "block/kitchen/MillstoneBlock.java"
STOVE_BLOCK = SRC / "block/kitchen/StoveBlock.java"
ENAMEL_BASIN_BLOCK = SRC / "block/kitchen/EnamelBasinBlock.java"
SCARECROW_ITEM = SRC / "item/ScarecrowItem.java"
SCARECROW_ENTITY = SRC / "entity/ScarecrowEntity.java"
SIT_ENTITY = SRC / "entity/SitEntity.java"
KITCHEN_SHOVEL_ITEM = SRC / "item/KitchenShovelItem.java"
RAW_DOUGH_ITEM = SRC / "item/RawDoughItem.java"
THROWABLE_BAOZI_ENTITY = SRC / "entity/ThrowableBaoziEntity.java"
TRASH_CAN_BLOCK = SRC / "block/misc/TrashCanBlock.java"
TRASH_CAN_BLOCK_ENTITY = SRC / "blockentity/misc/TrashCanBlockEntity.java"
TRASH_CAN_RENDERER = CLIENT_SRC / "client/render/block/TrashCanBlockEntityRender.java"
FRUIT_BASKET_BLOCK = SRC / "block/decoration/FruitBasketBlock.java"
RECIPE_BLOCK = SRC / "block/misc/RecipeBlock.java"
STRUNG_MUSHROOMS_BLOCK = SRC / "block/misc/StrungMushroomsBlock.java"
CHILI_RISTRA_BLOCK = SRC / "block/misc/ChiliRistraBlock.java"
STRAW_BLOCKS = SRC / "block/misc/StrawBlocks.java"
OIL_POT_BLOCK = SRC / "block/kitchen/OilPotBlock.java"
OIL_POT_STORAGE = SRC / "inventory/transfer/OilPotStorage.java"
MILLSTONE_BLOCK_ENTITY = SRC / "blockentity/kitchen/MillstoneBlockEntity.java"
MILLSTONE_ENTITY_STORAGE = SRC / "api/storage/MillstoneEntityItemStorage.java"
CHESTED_HORSE_STORAGE = SRC / "inventory/transfer/ChestedHorseItemStorage.java"
COMMON_REGISTRY = SRC / "init/registry/CommonRegistry.java"
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
    tick_cooking_start = pot_block_entity.find("private void tickCooking(")
    tick_cooking_end = pot_block_entity.find("private void tickPutIngredient(", tick_cooking_start)
    tick_cooking = pot_block_entity[tick_cooking_start:tick_cooking_end]
    oil_state_update = "level.setBlockAndUpdate(worldPosition, state.setValue(SHOW_OIL, false))"
    if oil_state_update not in tick_cooking:
        errors.append("PotBlockEntity does not confirm the oil-visibility update before finishing cooking.")
    if tick_cooking.find(oil_state_update) > tick_cooking.find("this.status = FINISHED"):
        errors.append("PotBlockEntity finishes cooking before confirming the oil-visibility update.")
    for required_reference in ("this.setChangedAndSync()", "GameEvent.BLOCK_CHANGE", "GameEvent.Context.of(state)"):
        if required_reference not in tick_cooking:
            errors.append(f"PotBlockEntity cooking completion is missing {required_reference}.")
    tick_finished_start = pot_block_entity.find("private void tickFinished(")
    tick_finished_end = tick_cooking_start
    tick_finished = pot_block_entity[tick_finished_start:tick_finished_end]
    if "this.setChangedAndSync()" not in tick_finished:
        errors.append("PotBlockEntity does not immediately synchronize the finished-to-burnt transition.")
    reset_start = pot_block_entity.find("private boolean reset(Level level, LivingEntity user)")
    reset_end = pot_block_entity.find("protected void saveAdditional(", reset_start)
    reset_method = pot_block_entity[reset_start:reset_end]
    reset_state_update = "level.setBlockAndUpdate(worldPosition, resetState)"
    if reset_start < 0 or reset_state_update not in reset_method:
        errors.append("PotBlockEntity reset does not confirm the block-state update.")
    if reset_method.find(reset_state_update) > reset_method.find("this.inputs ="):
        errors.append("PotBlockEntity clears its contents before confirming the block-state reset.")
    for required_reference in ("this.setChangedAndSync()", "GameEvent.BLOCK_CHANGE", "GameEvent.Context.of(user, state)"):
        if required_reference not in reset_method:
            errors.append(f"PotBlockEntity transactional reset is missing {required_reference}.")
    takeout_start = pot_block_entity.find("public boolean takeOutProduct(")
    takeout_end = pot_block_entity.find("private void sendActionBarMessage(", takeout_start)
    takeout_methods = pot_block_entity[takeout_start:takeout_end]
    if takeout_methods.count("if (!this.reset(level, user))") != 3:
        errors.append("PotBlockEntity does not confirm reset before every product-delivery path.")
    if "this.reset();" in pot_block_entity or "public void reset()" in pot_block_entity:
        errors.append("PotBlockEntity still exposes or uses the non-transactional reset method.")
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
    teapot_use_item = teapot_block.split("public @NotNull InteractionResult useItemOn(", 1)[-1].split(
        "public @NotNull InteractionResult useWithoutItem(", 1
    )[0]
    if "ItemStack mainHandItem = stack;" not in teapot_use_item:
        errors.append("TeapotBlock does not use the stack supplied to its item interaction entry point.")
    if "player.getMainHandItem()" in teapot_use_item or "mainHandItem.isEmpty()" in teapot_use_item:
        errors.append("TeapotBlock retains legacy hand lookup or empty-stack handling in useItemOn.")
    for required_reference in (
        "ContainerItemContext.forPlayerInteraction(player, hand)",
        "FluidStorage.ITEM.find(mainHandItem, containerContext)",
        "teapot.addTeaFluid(level, player, fluidStorage)",
        "teapot.removeTeaFluid(level, player, fluidStorage)",
    ):
        if required_reference not in teapot_use_item:
            errors.append(f"TeapotBlock generic fluid-storage dispatch is missing {required_reference}.")
    for hardcoded_bucket in ("Items.WATER_BUCKET", "Items.LAVA_BUCKET", "Items.BUCKET"):
        if hardcoded_bucket in teapot_use_item:
            errors.append(f"TeapotBlock still hardcodes a vanilla fluid container: {hardcoded_bucket}")
    for required_reference in (
        "fluidStorage.extract(fluidVariant, FluidConstants.BUCKET, transaction) != FluidConstants.BUCKET",
        "fluidStorage.insert(fluidVariant, FluidConstants.BUCKET, transaction) != FluidConstants.BUCKET",
        "if (!level.isClientSide()) {\n                transaction.commit();",
        "FluidVariantAttributes.getEmptySound(fluidVariant)",
        "FluidVariantAttributes.getFillSound(fluidVariant)",
    ):
        if required_reference not in teapot_block_entity:
            errors.append(f"TeapotBlockEntity generic one-bucket transfer is missing {required_reference}.")
    teapot_item = TEAPOT_ITEM.read_text(encoding="utf-8")
    for required_reference in (
        "ContainerItemContext.withConstant(pickup)",
        "FluidStorage.ITEM.find(pickup, containerContext)",
        "view.getResource().getFluid()",
    ):
        if required_reference not in teapot_item:
            errors.append(f"Held teapot generic pickup inspection is missing {required_reference}.")
    if "pickup.getItem() instanceof BucketItem" in teapot_item:
        errors.append("Held teapot still restricts picked-up fluid detection to BucketItem.")
    teapot_renderer = TEAPOT_RENDERER.read_text(encoding="utf-8")
    if "new WeakHashMap<>()" not in teapot_renderer or "boilingStates.computeIfAbsent(teapot" not in teapot_renderer:
        errors.append("Teapot renderer does not own weakly keyed boiling animation state.")
    trash_can_block_entity = TRASH_CAN_BLOCK_ENTITY.read_text(encoding="utf-8")
    if "AnimationState" in trash_can_block_entity or "clientTick(" in trash_can_block_entity:
        errors.append("TrashCanBlockEntity still stores or ticks client-only animation state.")
    trash_can_block = TRASH_CAN_BLOCK.read_text(encoding="utf-8")
    if "getTicker(" in trash_can_block:
        errors.append("TrashCanBlock still installs an unnecessary block entity ticker.")
    for required_reference in (
        "ItemStack itemInHand = stack;",
        "return trashCan.putItem(itemInHand, !player.hasInfiniteMaterials())",
        "? InteractionResult.CONSUME",
        ": InteractionResult.TRY_WITH_EMPTY_HAND",
        "return trashCan.withdrawItem(player) ? InteractionResult.CONSUME : InteractionResult.PASS;",
    ):
        if required_reference not in trash_can_block:
            errors.append(f"TrashCanBlock interaction result handling is missing {required_reference}.")
    seat_spawn_index = trash_can_block.find("if (!level.addFreshEntity(entitySit))")
    trash_enter_event_index = trash_can_block.find(
        "level.blockEvent(pos, state.getBlock(), TrashCanBlockEntity.EVENT_ENTER, 0)"
    )
    if seat_spawn_index < 0 or trash_enter_event_index < seat_spawn_index:
        errors.append("TrashCanBlock triggers entry effects before confirming seat entity creation.")
    if "if (!player.startRiding(entitySit, true, true))" not in trash_can_block:
        errors.append("TrashCanBlock does not discard its seat entity when mounting fails.")
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

    overlay_data_text = OVERLAY_DATA.read_text(encoding="utf-8")
    if ".map(ItemStack::copy)" not in overlay_data_text:
        errors.append("Shared overlay item-storage snapshots are not defensive copies.")
    for path in JADE_ITEM_STORAGE_PROVIDERS:
        provider_text = path.read_text(encoding="utf-8")
        if "ItemStackContainer" in provider_text:
            errors.append(f"Jade item storage provider wraps display data in ItemStackContainer: {path.relative_to(ROOT)}")
        if "CookeryOverlayData.itemStorage(" not in provider_text:
            errors.append(f"Jade item storage provider bypasses the shared defensive snapshot: {path.relative_to(ROOT)}")

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

    living_entity_mixin_text = (SRC / "mixin/LivingEntityMixin.java").read_text(encoding="utf-8")
    for event_class, path in LIVING_ENTITY_MIXIN_EVENTS.items():
        if not path.exists():
            errors.append(f"Living-entity event handler source is missing: {path.relative_to(ROOT)}")
            continue
        if f"{event_class}." not in living_entity_mixin_text:
            errors.append(f"LivingEntityMixin does not delegate to {event_class}.")
        if f"{event_class}.register();" in mod_events:
            errors.append(f"ModEvents still globally registers Mixin-bound handler {event_class}.")

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
        "FINAL_DAMAGE_BYPASS.contains(player.getUUID())",
        "source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)",
        "float finalDamage = calculateFinalDamage(player, source, originalDamage, config)",
        "finalDamage += Math.max(0, reducedDamage - absorbedDamage)",
        "player.causeFoodExhaustion(Math.max(0, exhaustionAmount))",
        "applyBypassingShield(player, source, finalDamage)",
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

    fruit_basket_take_out_text = FRUIT_BASKET_TAKE_OUT_EVENT.read_text(encoding="utf-8")
    for required_reference in (
        "UseBlockCallback.EVENT.register(FruitBasketTakeOutEvent::onUseBlock)",
        "hand != InteractionHand.MAIN_HAND || !player.isSecondaryUseActive()",
        "stack.is(Items.DEBUG_STICK)",
        "stack.is(Items.FIREWORK_ROCKET)",
        '"touhou_little_maid", "smart_slab_has_maid"',
        "stack.getItem() instanceof BlockItem && hitResult.getDirection() != Direction.UP",
        "fruitBasket.takeOut(player)",
    ):
        if required_reference not in fruit_basket_take_out_text:
            errors.append(f"Fruit basket held-item takeout is missing {required_reference}.")
    fruit_basket_block_text = FRUIT_BASKET_BLOCK.read_text(encoding="utf-8")
    if "if (player.isSecondaryUseActive()) {\n                return takeOut(" in fruit_basket_block_text:
        errors.append("FruitBasketBlock still handles unreachable held-item secondary use directly.")

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

    server_entity_load_text = SERVER_ENTITY_LOAD_EVENT.read_text(encoding="utf-8")
    for required_reference in (
        "ServerEntityEvents.ENTITY_LOAD",
        "CAT_LIE_GOAL_PRIORITY = 5",
        "CAT_LIE_SPEED_MODIFIER = 1.1",
        "CAT_LIE_SEARCH_RANGE = 8",
        "CREEPER_MUSTARD_AVOID_GOAL_PRIORITY = 3",
        "CREEPER_MUSTARD_AVOID_DISTANCE = 6.0F",
        "CREEPER_MUSTARD_WALK_SPEED_MODIFIER = 1.0",
        "CREEPER_MUSTARD_SPRINT_SPEED_MODIFIER = 1.2",
        'LEGACY_CAT_LIE_GOAL_TAG = "kaleidoscope_cookery.cat_lie_goal"',
        '"kaleidoscope_cookery.creeper_mustard_avoid_goal"',
        "cat.removeTag(LEGACY_CAT_LIE_GOAL_TAG)",
        "creeper.removeTag(LEGACY_CREEPER_MUSTARD_AVOID_GOAL_TAG)",
        "alreadyInstalled",
    ):
        if required_reference not in server_entity_load_text:
            errors.append(f"Server entity-load AI migration is missing {required_reference}.")

    extra_loot_text = EXTRA_LOOT_TABLE_DROP.read_text(encoding="utf-8")
    for required_reference in (
        '"entities/hoglin"), 2',
        '"entities/pig"), 1',
        '"entities/piglin"), 2',
        '"entities/piglin_brute"), 2',
        '"entities/zoglin"), 2',
        '"entities/zombified_piglin"), 1',
        "DONKEY_MEAT_ROLLS = 2",
        "COOKERY_SEED_DROP_CHANCE = 0.125F",
        "VANILLA_SEED_DROP_CHANCE = 0.02F",
        "getSeed(Items.BEETROOT_SEEDS, VANILLA_SEED_DROP_CHANCE",
        "getSeed(Items.PUMPKIN_SEEDS, VANILLA_SEED_DROP_CHANCE",
        "getSeed(Items.MELON_SEEDS, VANILLA_SEED_DROP_CHANCE",
        ".add(beetroot).add(pumpkin).add(melon)",
        ".add(oil).when(toolMatches)",
        ".add(meat).when(toolMatches)",
    ):
        if required_reference not in extra_loot_text:
            errors.append(f"Extra loot migration is missing {required_reference}.")
    oil_pool = extra_loot_text.split("private static void addOilDrop", 1)[-1].split(
        "private static void addDonkeyMeatDrop", 1
    )[0]
    if "EmptyLootItem" in oil_pool or ".add(empty)" in oil_pool:
        errors.append("Oil loot pools still halve Forge's guaranteed per-roll drop with an empty entry.")
    seed_pool = extra_loot_text.split("private static void addSeedDrop", 1)[-1].split(
        "private static LootPoolSingletonContainer.Builder<?> getSeed", 1
    )[0]
    if "EmptyLootItem" in seed_pool or ".add(empty)" in seed_pool or "SEED_EMPTY_WEIGHT" in extra_loot_text:
        errors.append("Straw-hat seed loot still replaces the three vanilla candidates with an empty entry.")

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

    vigor_effect_text = VIGOR_EFFECT.read_text(encoding="utf-8")
    if "addExhaustion(" in vigor_effect_text:
        errors.append("VigorEffect still creates negative exhaustion during effect ticks.")
    for required_reference in (
        "shouldApplyEffectTickThisTick",
        "applyEffectTick(ServerLevel level, LivingEntity livingEntity, int amplifier)",
        "player.isSprinting()",
        "FoodDataAccessor",
        "kaleidoscopeCookery$setExhaustionLevel(0.0F)",
    ):
        if required_reference not in vigor_effect_text:
            errors.append(f"Vigor effect-tick exhaustion handling is missing {required_reference}.")
    food_data_accessor_text = FOOD_DATA_ACCESSOR.read_text(encoding="utf-8")
    for required_reference in ('@Mixin(FoodData.class)', '@Accessor("exhaustionLevel")'):
        if required_reference not in food_data_accessor_text:
            errors.append(f"Vigor FoodData accessor is missing {required_reference}.")
    if "FoodDataAccessor" not in mixin_data.get("mixins", []):
        errors.append("FoodDataAccessor is not registered as a common mixin.")
    if "ServerPlayerMixin" in mixin_data.get("mixins", []):
        errors.append("Vigor still relies on the weaker ServerPlayer sprint-call redirect.")

    warmth_effect_text = WARMTH_EFFECT.read_text(encoding="utf-8")
    if "BlockPos.betweenClosed(" not in warmth_effect_text:
        errors.append("WarmthEffect does not use a fixed vanilla block scan.")
    if "mutable.offset(" in warmth_effect_text:
        errors.append("WarmthEffect retains a cumulatively mutated scan position.")

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
            "return InteractionResult.CONSUME;",
            "return InteractionResult.SUCCESS_SERVER;",
            "stack.consume(1, player)",
            "USE_CATERPILLAR_FEED_CHICKEN",
        ):
            if required_reference not in chicken_feed_text:
                errors.append(f"Caterpillar chicken feed event is missing {required_reference}.")
        if "stack.shrink(" in chicken_feed_text:
            errors.append("Caterpillar chicken feed event still manually shrinks feed stacks.")
        if "level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME" in chicken_feed_text:
            errors.append("Caterpillar chicken feed event still uses legacy sided swing results.")

    fruit_basket_text = FRUIT_BASKET_BLOCK.read_text(encoding="utf-8")
    for required_reference in (
        "useItemOn",
        "useWithoutItem",
        "!player.hasInfiniteMaterials()",
    ):
        if required_reference not in fruit_basket_text:
            errors.append(f"Fruit basket native interaction is missing {required_reference}.")
    if "if (!level.isClientSide())" not in fruit_basket_text and "if (level.isClientSide())" not in fruit_basket_text:
        errors.append("Fruit basket native interaction is missing an explicit logical-side boundary.")

    for name, path, expected_consumes in (
        ("TeacupBlock", TEACUP_BLOCK, 2),
        ("EmptyCupBlock", EMPTY_CUP_BLOCK, 1),
    ):
        drink_block_text = path.read_text(encoding="utf-8")
        use_item = drink_block_text.split("public @NotNull InteractionResult useItemOn(", 1)[-1].split(
            "public @NotNull InteractionResult useWithoutItem(", 1
        )[0]
        if "ItemStack itemInHand = stack;" not in use_item:
            errors.append(f"{name} does not use the stack supplied to its item interaction entry point.")
        if "player.getItemInHand(hand)" in use_item:
            errors.append(f"{name} still reads the player's hand instead of its supplied interaction stack.")
        if drink_block_text.count("itemInHand.consume(1, player)") < expected_consumes:
            errors.append(f"{name} does not use vanilla ItemStack.consume() for cup stacking.")
        if "itemInHand.shrink(" in drink_block_text:
            errors.append(f"{name} still manually shrinks cup stacks.")

    for name, path, guarded_updates, block_changes, block_destroys in (
        ("TeacupBlock", TEACUP_BLOCK, 5, 5, 2),
        ("EmptyCupBlock", EMPTY_CUP_BLOCK, 3, 3, 1),
    ):
        drink_block_text = path.read_text(encoding="utf-8")
        if drink_block_text.count("if (!level.setBlockAndUpdate(pos,") != guarded_updates:
            errors.append(f"{name} does not guard every cup state update before moving items.")
        if drink_block_text.count("GameEvent.BLOCK_CHANGE") != block_changes:
            errors.append(f"{name} does not emit block-change game events for every retained cup update.")
        if drink_block_text.count("GameEvent.BLOCK_DESTROY") != block_destroys:
            errors.append(f"{name} does not emit block-destroy game events when the last cup is removed.")
        if "GameEvent.Context.of(player, state)" not in drink_block_text:
            errors.append(f"{name} game events do not include the interacting player and prior block state.")

    for name, path, consume_expression, expected_consumes, shrink_expression in (
        ("TableBlock", TABLE_BLOCK, "stack.consume(1, player)", 3, "stack.shrink("),
        ("ChairBlock", CHAIR_BLOCK, "carpetStack.consume(1, player)", 2, "carpetStack.shrink("),
        ("PlateBlock", PLATE_BLOCK, "stack.consume(1, player)", 1, "stack.shrink("),
        ("StackableFoodBlock", STACKABLE_FOOD_BLOCK, "stack.consume(1, player)", 1, "stack.shrink("),
    ):
        decoration_block_text = path.read_text(encoding="utf-8")
        if decoration_block_text.count(consume_expression) < expected_consumes:
            errors.append(f"{name} does not use vanilla ItemStack.consume() for player item consumption.")
        if shrink_expression in decoration_block_text:
            errors.append(f"{name} still manually shrinks player item stacks.")

    for name, path, expected_game_events in (
        ("TableBlock", TABLE_BLOCK, 4),
        ("ChairBlock", CHAIR_BLOCK, 2),
    ):
        furniture_block_text = path.read_text(encoding="utf-8")
        if furniture_block_text.count("GameEvent.BLOCK_CHANGE") != expected_game_events:
            errors.append(f"{name} does not emit block-change game events for every furniture state update.")
        if "GameEvent.Context.of(player, state)" not in furniture_block_text:
            errors.append(f"{name} game events do not include the interacting player and prior block state.")

    chair_block_text = CHAIR_BLOCK.read_text(encoding="utf-8")
    chair_use_item = chair_block_text.split("public InteractionResult useItemOn(", 1)[-1].split(
        "public InteractionResult useWithoutItem(", 1
    )[0]
    if "player.getItemInHand(hand)" in chair_use_item:
        errors.append("ChairBlock bypasses the stack supplied to its item interaction entry point.")
    cook_stool_text = COOK_STOOL_BLOCK.read_text(encoding="utf-8")
    for path, seating_text in ((CHAIR_BLOCK, chair_block_text), (COOK_STOOL_BLOCK, cook_stool_text)):
        if "if (!level.addFreshEntity(entitySit))" not in seating_text:
            errors.append(f"{path.name} does not confirm seat entity creation.")
        if "if (!player.startRiding(entitySit, true, true))" not in seating_text:
            errors.append(f"{path.name} does not handle mounting failure.")
        if "entitySit.discard();" not in seating_text:
            errors.append(f"{path.name} does not discard an unusable seat entity.")

    sit_entity_text = SIT_ENTITY.read_text(encoding="utf-8")
    sit_tick_start = sit_entity_text.find("public void tick()")
    sit_tick_end = sit_entity_text.find("private void checkPassengers()", sit_tick_start)
    sit_tick = sit_entity_text[sit_tick_start:sit_tick_end]
    if "super.tick();" not in sit_tick:
        errors.append("SitEntity does not run the vanilla entity tick lifecycle.")
    if "instanceof ServerLevel" not in sit_tick:
        errors.append("SitEntity passenger cleanup is not guarded by the authoritative server level.")
    if "checkBelowWorld()" in sit_tick:
        errors.append("SitEntity duplicates the vanilla baseTick below-world check.")

    plate_block_text = PLATE_BLOCK.read_text(encoding="utf-8")
    if plate_block_text.count("GameEvent.BLOCK_CHANGE") != 2:
        errors.append("PlateBlock does not emit block-change game events for serving updates.")
    if plate_block_text.count("if (!level.setBlockAndUpdate(pos,") != 2:
        errors.append("PlateBlock does not guard every serving state update before moving items.")
    if "if (!level.destroyBlock(pos, true, player))" not in plate_block_text:
        errors.append("PlateBlock does not confirm empty plate removal.")
    if "GameEvent.BLOCK_DESTROY" not in plate_block_text:
        errors.append("PlateBlock does not emit a block-destroy event when the empty plate is removed.")
    if "GameEvent.Context.of(player, state)" not in plate_block_text:
        errors.append("PlateBlock game events do not include the interacting player and prior block state.")
    if "new ArrayList<>(super.getDrops(state, params))" not in plate_block_text:
        errors.append("PlateBlock bypasses its baseline bowl loot table when adding remaining servings.")

    stackable_food_text = STACKABLE_FOOD_BLOCK.read_text(encoding="utf-8")
    for required_reference in (
        "if (!level.setBlockAndUpdate(pos, state.setValue(COUNT, count + 1)))",
        "if (!level.setBlockAndUpdate(pos, state.setValue(COUNT, count - 1)))",
        "if (!level.removeBlock(pos, false))",
        "GameEvent.BLOCK_CHANGE",
        "GameEvent.BLOCK_DESTROY",
        "GameEvent.Context.of(player, state)",
    ):
        if required_reference not in stackable_food_text:
            errors.append(f"StackableFoodBlock transactional interaction is missing {required_reference}.")
    remove_stackable_index = stackable_food_text.find("level.removeBlock(pos, false)")
    return_stackable_index = stackable_food_text.find("ItemUtils.getItemToLivingEntity(player")
    if remove_stackable_index > return_stackable_index:
        errors.append("StackableFoodBlock returns the last serving before confirming block removal.")

    food_bite_text = FOOD_BITE_BLOCK.read_text(encoding="utf-8")
    if "if (!level.destroyBlock(pos, true, player))" not in food_bite_text:
        errors.append("FoodBiteBlock does not confirm terminal food-block removal.")
    if "GameEvent.BLOCK_DESTROY" not in food_bite_text or "GameEvent.Context.of(player, state)" not in food_bite_text:
        errors.append("FoodBiteBlock does not emit a contextual block-destroy event for terminal removal.")
    bite_state_update = "if (!level.setBlock(pos, state.setValue(bitesProperty, bites + 1), Block.UPDATE_ALL))"
    if bite_state_update not in food_bite_text:
        errors.append("FoodBiteBlock does not confirm bite-state updates before feeding the player.")
    if food_bite_text.find(bite_state_update) > food_bite_text.find("player.getFoodData().eat("):
        errors.append("FoodBiteBlock feeds the player before confirming its bite-state update.")

    two_block_food_text = FOOD_BITE_ONE_BY_TWO_BLOCK.read_text(encoding="utf-8")
    set_placed_start = two_block_food_text.find("public void setPlacedBy(")
    set_placed_end = two_block_food_text.find("protected void createBlockStateDefinition(", set_placed_start)
    set_placed_by = two_block_food_text[set_placed_start:set_placed_end]
    if "level.isClientSide()" in set_placed_by:
        errors.append("FoodBiteOneByTwoBlock suppresses vanilla client-side placement of its paired half.")
    confirmed_pair_removal = "if (level.setBlock(right, airBlockState, Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL))"
    if confirmed_pair_removal not in two_block_food_text:
        errors.append("FoodBiteOneByTwoBlock does not confirm creative paired-half removal.")

    for path in (FOOD_BITE_THREE_BY_THREE_BLOCK, MILLSTONE_BLOCK):
        nine_part_text = path.read_text(encoding="utf-8")
        set_placed_start = nine_part_text.find("public void setPlacedBy(")
        set_placed_end = nine_part_text.find("protected void createBlockStateDefinition(", set_placed_start)
        if "isClientSide()" in nine_part_text[set_placed_start:set_placed_end]:
            errors.append(f"{path.name} suppresses client-side placement of its nine-part structure.")
        if "&& !world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL)" not in nine_part_text:
            errors.append(f"{path.name} does not confirm matching satellite removal.")

    stove_block_text = STOVE_BLOCK.read_text(encoding="utf-8")
    stove_use_item = stove_block_text.split(
        "public @NotNull InteractionResult useItemOn(", 1
    )[-1].split("public void onProjectileHit(", 1)[0]
    if "stack.consume(1, player)" not in stove_use_item:
        errors.append("StoveBlock does not use vanilla ItemStack.consume() for fire charges.")
    if "player.getItemInHand(hand)" in stove_use_item:
        errors.append("StoveBlock does not use the stack supplied to its item interaction entry point.")
    if "stack.shrink(" in stove_use_item:
        errors.append("StoveBlock still manually shrinks fire charges.")
    if stove_block_text.count("GameEvent.BLOCK_CHANGE") != 4:
        errors.append("StoveBlock does not emit a block-change game event for every distinct lit-state update path.")
    if "if (!level.setBlockAndUpdate(pos, state.setValue(LIT, true)))" not in stove_block_text:
        errors.append("StoveBlock consumes ignition items before confirming the lit-state update.")
    if "if (!level.setBlockAndUpdate(pos, state.setValue(LIT, false)))" not in stove_block_text:
        errors.append("StoveBlock mutates extinguishing tools before confirming the lit-state update.")
    if "if (!level.setBlock(hitBlockPos, state.setValue(BlockStateProperties.LIT, true), Block.UPDATE_ALL_IMMEDIATE))" not in stove_block_text:
        errors.append("StoveBlock triggers projectile ignition effects without confirming the state update.")
    for required_reference in (
        "extinguish(level, pos, blockState)",
        "extinguish(level, pos, state)",
        "tickAccess.scheduleTick(pos, this, 1)",
    ):
        if required_reference not in stove_block_text:
            errors.append(f"StoveBlock automatic extinguishing is missing {required_reference}.")
    update_shape_start = stove_block_text.find("BlockState updateShape(")
    update_shape_end = stove_block_text.find("InteractionResult useItemOn(", update_shape_start)
    update_shape_text = stove_block_text[update_shape_start:update_shape_end]
    if "setBlock" in update_shape_text:
        errors.append("StoveBlock still mutates the world directly during updateShape().")
    for required_context in (
        "GameEvent.Context.of(state)",
        "GameEvent.Context.of(player, state)",
        "GameEvent.Context.of(projectile, state)",
    ):
        if required_context not in stove_block_text:
            errors.append(f"StoveBlock game event handling is missing {required_context}.")

    scarecrow_item_text = SCARECROW_ITEM.read_text(encoding="utf-8")
    for required_reference in (
        "if (level instanceof ServerLevel serverLevel)",
        "if (!serverLevel.tryAddFreshEntityWithPassengers(scarecrow))",
        "stack.consume(1, context.getPlayer())",
        "return InteractionResult.SUCCESS;",
    ):
        if required_reference not in scarecrow_item_text:
            errors.append(f"ScarecrowItem placement consumption is missing {required_reference}.")
    if "level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME" in scarecrow_item_text:
        errors.append("ScarecrowItem placement still uses legacy sided interaction results.")
    if "stack.shrink(" in scarecrow_item_text:
        errors.append("ScarecrowItem still manually shrinks the placement stack.")
    confirmed_spawn = scarecrow_item_text.find("if (!serverLevel.tryAddFreshEntityWithPassengers(scarecrow))")
    for side_effect in (
        "level.playSound(",
        "scarecrow.gameEvent(",
        "ModTrigger.EVENT.trigger(",
        "stack.consume(1, context.getPlayer())",
    ):
        if confirmed_spawn > scarecrow_item_text.find(side_effect):
            errors.append(f"ScarecrowItem performs {side_effect} before confirming entity creation.")

    kitchen_shovel_text = KITCHEN_SHOVEL_ITEM.read_text(encoding="utf-8")
    for required_reference in (
        "if (level.isClientSide())",
        "potBlockEntity.takeOutProduct(level, player, stack)",
        "? InteractionResult.SUCCESS",
        ": InteractionResult.FAIL",
    ):
        if required_reference not in kitchen_shovel_text:
            errors.append(f"KitchenShovelItem transactional takeout is missing {required_reference}.")
    if "level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME" in kitchen_shovel_text:
        errors.append("KitchenShovelItem still ignores the authoritative pot takeout result.")

    scarecrow_entity_text = SCARECROW_ENTITY.read_text(encoding="utf-8")
    if "private void releaseShoulderEntity()" not in scarecrow_entity_text:
        errors.append("ScarecrowEntity does not expose a forced shoulder-entity release path.")
    kill_start = scarecrow_entity_text.find("public void kill(ServerLevel level)")
    kill_end = scarecrow_entity_text.find("public boolean isPushable()", kill_start)
    kill_text = scarecrow_entity_text[kill_start:kill_end]
    if "this.releaseShoulderEntity();" not in kill_text:
        errors.append("ScarecrowEntity death still respects the voluntary shoulder release delay.")
    if "this.removeEntitiesOnShoulder();" in kill_text:
        errors.append("ScarecrowEntity death still routes through the delayed shoulder release path.")
    interact_start = scarecrow_entity_text.find("public InteractionResult interact(")
    head_handler_start = scarecrow_entity_text.find("private InteractionResult handleHeadItems(", interact_start)
    hand_swap_start = scarecrow_entity_text.find("private boolean swapHand(", head_handler_start)
    scarecrow_interact = scarecrow_entity_text[interact_start:head_handler_start]
    scarecrow_interaction_handlers = scarecrow_entity_text[head_handler_start:hand_swap_start]
    if "if (player.level().isClientSide()) {\n            return InteractionResult.SUCCESS_SERVER;" not in scarecrow_interact:
        errors.append("ScarecrowEntity client interaction does not defer its swing to the server.")
    if "return InteractionResult.SUCCESS;" in scarecrow_interaction_handlers:
        errors.append("ScarecrowEntity equipment handling still reports a client-sourced success on the server.")
    if scarecrow_interaction_handlers.count("return InteractionResult.SUCCESS_SERVER;") != 8:
        errors.append("ScarecrowEntity equipment success paths do not consistently use SUCCESS_SERVER.")

    raw_dough_text = RAW_DOUGH_ITEM.read_text(encoding="utf-8")
    if "stack.consume(count, entityLiving)" not in raw_dough_text:
        errors.append("RawDoughItem does not use vanilla item consumption.")
    if "stack.setCount(0)" in raw_dough_text:
        errors.append("RawDoughItem still clears the source stack manually.")
    if "worldIn.playSound(null," not in raw_dough_text:
        errors.append("RawDoughItem transformation sound is not server-broadcast.")

    throwable_baozi_text = THROWABLE_BAOZI_ENTITY.read_text(encoding="utf-8")
    on_hit_entity_start = throwable_baozi_text.find("protected void onHitEntity(")
    on_hit_start = throwable_baozi_text.find("protected void onHit(", on_hit_entity_start)
    on_hit_entity = throwable_baozi_text[on_hit_entity_start:on_hit_start]
    if "instanceof ServerLevel serverLevel" not in on_hit_entity:
        errors.append("ThrowableBaoziEntity does not guard entity impact effects behind ServerLevel.")
    for required_reference in (
        "hitEntity.hurtServer(serverLevel",
        "wolf.heal(wolf.getMaxHealth())",
        "serverLevel.broadcastEntityEvent(this, EntityEvent.LOVE_HEARTS)",
    ):
        if required_reference not in on_hit_entity:
            errors.append(f"ThrowableBaoziEntity server impact handling is missing {required_reference}.")
    if "SoundEvents.SNOW_HIT" in on_hit_entity:
        errors.append("ThrowableBaoziEntity still plays a duplicate sound during entity impact dispatch.")
    if throwable_baozi_text.count("SoundEvents.SNOW_HIT") != 1:
        errors.append("ThrowableBaoziEntity should play exactly one impact sound per collision.")

    enamel_basin_text = ENAMEL_BASIN_BLOCK.read_text(encoding="utf-8")
    for required_reference in (
        "public @NotNull InteractionResult useWithoutItem(",
        "return setLid(state, level, pos, player, !state.getValue(HAS_LID));",
    ):
        if required_reference not in enamel_basin_text:
            errors.append(f"EnamelBasinBlock native empty-hand lid interaction is missing {required_reference}.")
    enamel_basin_use_item = enamel_basin_text.split("public @NotNull InteractionResult useItemOn(", 1)[-1].split(
        "public @NotNull InteractionResult useWithoutItem(", 1
    )[0]
    if "mainHandItem.isEmpty()" in enamel_basin_use_item:
        errors.append("EnamelBasinBlock still handles empty-hand lid closing through useItemOn.")
    if "mainHandItem.consume(consumeCount, player)" not in enamel_basin_text:
        errors.append("EnamelBasinBlock does not use vanilla ItemStack.consume() for bulk oil insertion.")
    if "mainHandItem.shrink(" in enamel_basin_text:
        errors.append("EnamelBasinBlock still manually shrinks inserted oil stacks.")
    if enamel_basin_text.count("GameEvent.BLOCK_CHANGE") != 4:
        errors.append("EnamelBasinBlock does not emit a block-change game event for every stateful interaction.")
    if enamel_basin_text.count("if (!level.setBlockAndUpdate(pos,") != 4:
        errors.append("EnamelBasinBlock does not guard every state update before moving items.")
    if "if (!level.destroyBlock(pos, true, player))" not in enamel_basin_text:
        errors.append("EnamelBasinBlock does not confirm empty basin removal.")
    if "GameEvent.BLOCK_DESTROY" not in enamel_basin_text:
        errors.append("EnamelBasinBlock does not emit a block-destroy event when removed by a shovel.")
    if "GameEvent.Context.of(player, state)" not in enamel_basin_text:
        errors.append("EnamelBasinBlock game events do not include the interacting player and prior block state.")

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
    oil_pot_use_item = oil_pot_text.split("public @NotNull InteractionResult useItemOn(", 1)[-1].split(
        "public @NotNull InteractionResult useWithoutItem(", 1
    )[0]
    if "getItemInHand(" in oil_pot_use_item or "getMainHandItem()" in oil_pot_use_item:
        errors.append("OilPotBlock rereads a player hand instead of using the supplied interaction stack.")
    if oil_pot_text.count("if (level.isClientSide())") < 2:
        errors.append("OilPotBlock does not keep both oil transfer predictions client-side.")
    if oil_pot_text.count("if (!oilPot.setOilCount(") != 2:
        errors.append("OilPotBlock does not confirm both oil count updates before moving items.")
    if "level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME" in oil_pot_text:
        errors.append("OilPotBlock still uses legacy sided results that hide failed oil transfers.")
    if oil_pot_text.count("return InteractionResult.CONSUME;") != 2:
        errors.append("OilPotBlock committed transfers do not use the server-side consume result.")
    insert_commit = oil_pot_text.find("if (!oilPot.setOilCount(currentOilCount + addOilCount))")
    extract_commit = oil_pot_text.find("if (!oilPot.setOilCount(currentOilCount - takeCount))")
    if insert_commit > oil_pot_text.find("stack.consume(addOilCount, player)"):
        errors.append("OilPotBlock consumes inserted oil before confirming storage.")
    if extract_commit > oil_pot_text.find(
            "player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.OIL, takeCount))"
    ):
        errors.append("OilPotBlock gives extracted oil before confirming storage removal.")
    if oil_pot_text.count("GameEvent.BLOCK_CHANGE") < 2:
        errors.append("OilPotBlock does not emit block-change events for both oil transfer directions.")

    oil_pot_storage = OIL_POT_STORAGE.read_text(encoding="utf-8")
    common_registry = COMMON_REGISTRY.read_text(encoding="utf-8")
    for required_reference in (
        "implements SingleSlotStorage<ItemVariant>",
        "resource.getItem() != ModItems.OIL",
        "OilPotBlockEntity.MAX_OIL_COUNT - currentCount",
        "this.oilPot.setOilCount(this.pendingCount)",
        "protected void onFinalCommit()",
    ):
        if required_reference not in oil_pot_storage:
            errors.append(f"Oil pot Fabric storage is missing {required_reference}.")
    if "ItemStorage.SIDED.registerForBlockEntity((oilPot, direction) -> oilPot.getItemStorage(), ModBlocks.OIL_POT_BE)" not in common_registry:
        errors.append("Oil pot Fabric item storage is not registered for every side.")

    millstone_block_entity = MILLSTONE_BLOCK_ENTITY.read_text(encoding="utf-8")
    millstone_entity_storage = MILLSTONE_ENTITY_STORAGE.read_text(encoding="utf-8")
    chested_horse_storage = CHESTED_HORSE_STORAGE.read_text(encoding="utf-8")
    for required_reference in (
        "EntityApiLookup<Storage<ItemVariant>, Void>",
        '"millstone_entity_item_storage"',
        "return SOURCE.find(entity, null);",
    ):
        if required_reference not in millstone_entity_storage:
            errors.append(f"Millstone entity storage lookup is missing {required_reference}.")
    if "MillstoneEntityItemStorage.SOURCE.registerFallback" not in common_registry \
            or "new ChestedHorseItemStorage(horse)" not in common_registry:
        errors.append("Chested-horse storage is not registered as the millstone entity lookup fallback.")
    for required_reference in (
        "extends SnapshotParticipant<List<ItemStack>>",
        "this.updateSnapshots(transaction);",
        "AbstractHorse.INVENTORY_SLOT_OFFSET + inventoryIndex",
        "this.slot(slot).set(snapshot.get(slot).copy());",
    ):
        if required_reference not in chested_horse_storage:
            errors.append(f"Chested-horse transactional storage is missing {required_reference}.")
    for required_reference in (
        "MillstoneEntityItemStorage.find(this.bindEntity)",
        "try (Transaction transaction = Transaction.openOuter())",
        "view.extract(resource, maxAmount, transaction)",
        "serverLevel.getEntitiesOfClass(ItemEntity.class",
        "stack.copyWithCount(countCanInsert)",
    ):
        if required_reference not in millstone_block_entity:
            errors.append(f"Millstone automatic input path is missing {required_reference}.")
    if "bindEntity instanceof AbstractChestedHorse" in millstone_block_entity:
        errors.append("Millstone still hard-codes bound-entity extraction to chested horses.")

    pot_block_text = (SRC / "block/kitchen/PotBlock.java").read_text(encoding="utf-8")
    for required_reference in (
        "public InteractionResult useWithoutItem(",
        "pot.removeIngredient(level, player)",
        "pot.takeOutProduct(level, player, ItemStack.EMPTY)",
    ):
        if required_reference not in pot_block_text:
            errors.append(f"PotBlock native empty-hand interaction is missing {required_reference}.")
    pot_use_item = pot_block_text.split("public InteractionResult useItemOn(", 1)[-1].split(
        "public InteractionResult useWithoutItem(", 1
    )[0]
    if "itemInHand.isEmpty()" in pot_use_item:
        errors.append("PotBlock still handles empty-hand ingredient removal through useItemOn.")
    for player_stack_read in ("player.getItemInHand(hand)", "player.getMainHandItem()"):
        if player_stack_read in pot_use_item:
            errors.append(f"PotBlock bypasses its supplied interaction stack: {player_stack_read}.")
    for required_reference in (
        "pot.takeOutProduct(level, player, stack)",
        "pot.onPlaceOil(level, player, stack)",
        "pot.addIngredient(level, player, stack)",
    ):
        if required_reference not in pot_use_item:
            errors.append(f"PotBlock supplied-stack dispatch is missing {required_reference}.")

    item_utils_text = (SRC / "util/ItemUtils.java").read_text(encoding="utf-8")
    if "ItemStackTemplate craftingRemainder = item.getCraftingRemainder();" not in item_utils_text \
            or "if (craftingRemainder != null)" not in item_utils_text:
        errors.append("ItemUtils does not handle the nullable vanilla crafting remainder template.")
    if "getCraftingRemainder().create()" in item_utils_text:
        errors.append("ItemUtils still dereferences nullable crafting remainder templates directly.")

    stockpot_block_text = STOCKPOT_BLOCK.read_text(encoding="utf-8")
    for required_reference in (
        "public @NotNull InteractionResult useWithoutItem(",
        "stockpot.onLitClick(level, player, ItemStack.EMPTY)",
        "stockpot.removeIngredient(level, player)",
        "stockpot.takeOutProduct(level, player, ItemStack.EMPTY)",
    ):
        if required_reference not in stockpot_block_text:
            errors.append(f"StockpotBlock native empty-hand interaction is missing {required_reference}.")
    stockpot_use_item = stockpot_block_text.split("public @NotNull InteractionResult useItemOn(", 1)[-1].split(
        "public @NotNull InteractionResult useWithoutItem(", 1
    )[0]
    if "mainHandItem.isEmpty()" in stockpot_use_item:
        errors.append("StockpotBlock still handles empty-hand ingredient removal through useItemOn.")

    if SICKLE_NETHER_WART_EVENT.exists():
        sickle_nether_wart_text = SICKLE_NETHER_WART_EVENT.read_text(encoding="utf-8")
        for required_reference in (
            "SickleHarvestCallback.EVENT.register(SickleHarvestNetherWartEvent::onSickleHarvest)",
            "if (!player.gameMode.destroyBlock(pos))",
            "level.getBlockState(pos).isAir()",
            "if (level.setBlock(pos, replantedState, Block.UPDATE_ALL))",
            "GameEvent.BLOCK_CHANGE",
            "SickleHarvestCallback.Result.HARVESTED",
            "SickleHarvestCallback.Result.SKIP",
        ):
            if required_reference not in sickle_nether_wart_text:
                errors.append(f"Sickle nether wart event is missing {required_reference}.")
        for duplicate_side_effect in ("LevelEvent.PARTICLES_DESTROY_BLOCK",):
            if duplicate_side_effect in sickle_nether_wart_text:
                errors.append(f"Sickle nether wart event retains duplicate side effect: {duplicate_side_effect}.")
        replant_write_index = sickle_nether_wart_text.find("if (level.setBlock(pos, replantedState, Block.UPDATE_ALL))")
        harvested_return_index = sickle_nether_wart_text.rfind("return true;")
        if harvested_return_index < replant_write_index:
            errors.append("Sickle nether wart harvest skips durability when only replanting fails.")

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
            "GameEvent.BLOCK_CHANGE",
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
        "public boolean replantAfterHarvestIfUnchanged",
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
    if "return level.setBlock(basePos, getReplantedState(DOWN, bottomState), Block.UPDATE_ALL)" not in rice_crop_text:
        errors.append("RiceCropBlock does not report whether all replanted sections were written.")
    if "|| !levelAccessor.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), Block.UPDATE_ALL)" not in rice_crop_text:
        errors.append("RiceCropBlock returns picked-up water without confirming the fluid-state update.")

    base_crop_text = BASE_CROP_BLOCK.read_text(encoding="utf-8")
    for required_reference in (
        "level instanceof ServerLevel serverLevel",
        "dropFromBlockInteractLootTable(",
        "level.getBlockEntity(pos)",
        "this.onUseBreakCrop(state, serverLevel, pos, player)",
        "state.setValue(AGE, ageAfterUse)",
        "if (!level.setBlock(pos, harvestedState, Block.UPDATE_CLIENTS))",
        "GameEvent.BLOCK_CHANGE",
        "GameEvent.Context.of(player, state)",
    ):
        if required_reference not in base_crop_text:
            errors.append(f"BaseCropBlock native interaction harvest is missing {required_reference}.")
    loot_drop_index = base_crop_text.find("dropFromBlockInteractLootTable(")
    reset_crop_index = base_crop_text.find("this.onUseBreakCrop(state, serverLevel, pos, player)")
    if reset_crop_index > loot_drop_index:
        errors.append("BaseCropBlock drops harvest loot before confirming the crop-state reset.")
    if "this.result" in base_crop_text:
        errors.append("BaseCropBlock still retains the legacy manual harvest result supplier.")

    for name, path, delivery, accepted_item in (
        ("StrungMushroomsBlock", STRUNG_MUSHROOMS_BLOCK,
         "ItemUtils.giveItemToPlayer(player, mushrooms", "stack.is(Items.BROWN_MUSHROOM)"),
        ("ChiliRistraBlock", CHILI_RISTRA_BLOCK,
         "ItemUtils.giveItemToPlayer(player, redChili", "stack.is(ModItems.RED_CHILI)"),
    ):
        hanging_harvest = path.read_text(encoding="utf-8")
        use_item = hanging_harvest.split("InteractionResult useItemOn(", 1)[-1].split(
            "InteractionResult useWithoutItem(", 1
        )[0]
        for required_reference in (accepted_item, "return harvest(state, level, pos, player);"):
            if required_reference not in use_item:
                errors.append(f"{name} held-item harvest is missing {required_reference}.")
        if ".isEmpty()" in use_item or "InteractionResult.TRY_WITH_EMPTY_HAND" in use_item:
            errors.append(f"{name} still handles empty-hand harvest through useItemOn.")
        if "public InteractionResult useWithoutItem(" not in hanging_harvest:
            errors.append(f"{name} does not expose native empty-hand harvest handling.")
        state_update = "if (!level.setBlock(pos, updatedState, Block.UPDATE_ALL))"
        if state_update not in hanging_harvest:
            errors.append(f"{name} does not confirm its harvest state update.")
        if hanging_harvest.find(state_update) > hanging_harvest.find(delivery):
            errors.append(f"{name} gives harvest items before confirming its state update.")
        for required_event in ("GameEvent.BLOCK_CHANGE", "GameEvent.BLOCK_DESTROY", "GameEvent.Context.of(player, state)"):
            if required_event not in hanging_harvest:
                errors.append(f"{name} harvest events are missing {required_event}.")

    straw_blocks = STRAW_BLOCKS.read_text(encoding="utf-8")
    confirmed_straw_break = "if (!level.destroyBlock(pos, false))"
    first_straw_drop = "popResource(level, pos, new ItemStack(ModItems.RICE_PANICLE, 5))"
    if confirmed_straw_break not in straw_blocks:
        errors.append("StrawBlocks does not confirm fall-driven block removal.")
    if straw_blocks.find(confirmed_straw_break) > straw_blocks.find(first_straw_drop):
        errors.append("StrawBlocks drops harvest items before confirming fall-driven removal.")
    if "GameEvent.Context.of(entity, state)" not in straw_blocks:
        errors.append("StrawBlocks does not emit a contextual block-destroy event.")

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
