#!/usr/bin/env python3
"""Verify block entity registration, storage, and item-data wiring.

The current port intentionally keeps a legacy ``recipe_book`` block entity id
for old saves. This script records that compatibility rule and checks the rest
of the block entity graph stays aligned.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery"
MOD_BLOCKS = JAVA_ROOT / "init/ModBlocks.java"
BLOCK_ENTITY_ROOT = JAVA_ROOT / "blockentity"
BLOCK_ROOT = JAVA_ROOT / "block"
BASE_BLOCK_ENTITY = BLOCK_ENTITY_ROOT / "BaseBlockEntity.java"

EXPLICIT_CLIENT_SYNC_BLOCK_ENTITIES = (
    BLOCK_ENTITY_ROOT / "decoration/ChairBlockEntity.java",
    BLOCK_ENTITY_ROOT / "decoration/FruitBasketBlockEntity.java",
    BLOCK_ENTITY_ROOT / "decoration/OilPotBlockEntity.java",
    BLOCK_ENTITY_ROOT / "decoration/RecipeBlockEntity.java",
    BLOCK_ENTITY_ROOT / "decoration/TableBlockEntity.java",
    BLOCK_ENTITY_ROOT / "kitchen/ChoppingBoardBlockEntity.java",
    BLOCK_ENTITY_ROOT / "kitchen/KitchenwareRacksBlockEntity.java",
    BLOCK_ENTITY_ROOT / "kitchen/MillstoneBlockEntity.java",
    BLOCK_ENTITY_ROOT / "kitchen/PotBlockEntity.java",
    BLOCK_ENTITY_ROOT / "kitchen/ShawarmaSpitBlockEntity.java",
    BLOCK_ENTITY_ROOT / "kitchen/SteamerBlockEntity.java",
    BLOCK_ENTITY_ROOT / "kitchen/StockpotBlockEntity.java",
    BLOCK_ENTITY_ROOT / "kitchen/TeapotBlockEntity.java",
    BLOCK_ENTITY_ROOT / "misc/TrashCanBlockEntity.java",
)

SNAPSHOT_CONTAINER_GETTERS = {
    BLOCK_ENTITY_ROOT / "decoration/FruitBasketBlockEntity.java": "return copyStacks(this.items.items);",
    BLOCK_ENTITY_ROOT / "decoration/TableBlockEntity.java": "return copyStacks(this.items);",
    BLOCK_ENTITY_ROOT / "kitchen/PotBlockEntity.java": "return copyStacks(this.inputs);",
    BLOCK_ENTITY_ROOT / "kitchen/SteamerBlockEntity.java": "return copyStacks(this.items);",
    BLOCK_ENTITY_ROOT / "kitchen/StockpotBlockEntity.java": "return copyStacks(this.inputs);",
}

SNAPSHOT_STATE_RETURNS = {
    BLOCK_ENTITY_ROOT / "kitchen/ChoppingBoardBlockEntity.java": (
        "return this.currentCutStack.copy();",
    ),
    BLOCK_ENTITY_ROOT / "kitchen/KitchenwareRacksBlockEntity.java": (
        "return this.itemLeft.copy();",
        "return this.itemRight.copy();",
    ),
    BLOCK_ENTITY_ROOT / "kitchen/MillstoneBlockEntity.java": (
        "return this.input.copy();",
        "return this.output.copy();",
    ),
    BLOCK_ENTITY_ROOT / "kitchen/PotBlockEntity.java": (
        "return this.result.copy();",
    ),
    BLOCK_ENTITY_ROOT / "kitchen/SteamerBlockEntity.java": (
        "return this.cookingProgress.clone();",
        "return this.cookingTime.clone();",
    ),
    BLOCK_ENTITY_ROOT / "kitchen/StockpotBlockEntity.java": (
        "return this.result.copy();",
    ),
    BLOCK_ENTITY_ROOT / "kitchen/ShawarmaSpitBlockEntity.java": (
        "return (this.cookingItem.isEmpty() ? this.cookedItem : this.cookingItem).copy();",
    ),
    BLOCK_ENTITY_ROOT / "kitchen/TeapotBlockEntity.java": (
        "return this.input.copy();",
        "return this.result.copy();",
    ),
}

EXPECTED_BLOCK_ENTITY_IDS = {
    "POT_BE": "pot",
    "STOCKPOT_BE": "stockpot",
    "FRUIT_BASKET_BE": "fruit_basket",
    "CHOPPING_BOARD_BE": "chopping_board",
    "KITCHENWARE_RACKS_BE": "kitchenware_racks",
    "SHAWARMA_SPIT_BE": "shawarma_spit",
    "STEAMER_BE": "steamer",
    "TEAPOT_BE": "teapot",
    "TRASH_CAN_BE": "trash_can",
    "MILLSTONE_BE": "millstone",
    # Legacy id retained for existing world save compatibility.
    "RECIPE_BLOCK_BE": "recipe_book",
    "OIL_POT_BE": "oil_pot",
    "FOOD_BITE_THREE_BY_THREE_BE": "food_bite_three_by_three",
    "CHAIR_BE": "chair",
    "TABLE_BE": "table",
}

STATELESS_BLOCK_ENTITIES = {
    "FoodBiteThreeByThreeBlockEntity",
}

EXPECTED_BLOCK_ENTITY_DATA_REFERENCES = {
    "ModBlocks.STEAMER_BE",
    "ModBlocks.TEAPOT_BE",
    "this.getType(",
    "this.getType()",
    "type",
}


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def strip_comments(text: str) -> str:
    text = re.sub(r"/\*.*?\*/", "", text, flags=re.DOTALL)
    return re.sub(r"//.*", "", text)


def find_class_file(root: Path, class_name: str) -> Path | None:
    matches = list(root.rglob(f"{class_name}.java"))
    if len(matches) == 1:
        return matches[0]
    return None


def collect_block_declarations(mod_blocks: str) -> dict[str, str]:
    pattern = re.compile(
        r"public\s+static\s+final\s+Block\s+(\w+)\s*=\s*new\s+(\w+)\s*\(",
        flags=re.MULTILINE,
    )
    return {block_const: block_class for block_const, block_class in pattern.findall(mod_blocks)}


def collect_registered_blocks(mod_blocks: str) -> dict[str, str]:
    pattern = re.compile(r'registerBlock\(\s*"([a-z0-9_./-]+)"\s*,\s*(\w+)\s*\)')
    return {block_const: block_id for block_id, block_const in pattern.findall(mod_blocks)}


def collect_block_entity_declarations(mod_blocks: str) -> dict[str, dict[str, object]]:
    pattern = re.compile(
        r"public\s+static\s+final\s+BlockEntityType<(?P<class>\w+)>\s+"
        r"(?P<const>\w+)\s*=\s*FabricBlockEntityTypeBuilder\.create\(\s*"
        r"(?P<ctor>\w+)::new\s*,(?P<blocks>.*?)\)\.build\(\)",
        flags=re.DOTALL,
    )
    declarations: dict[str, dict[str, object]] = {}
    for match in pattern.finditer(mod_blocks):
        block_args = match.group("blocks")
        declarations[match.group("const")] = {
            "class": match.group("class"),
            "ctor": match.group("ctor"),
            "blocks": re.findall(r"\b[A-Z][A-Z0-9_]+\b", block_args),
        }
    return declarations


def collect_block_entity_registrations(mod_blocks: str) -> dict[str, str]:
    pattern = re.compile(r'registerBlockEntity\(\s*"([a-z0-9_./-]+)"\s*,\s*(\w+)\s*\)')
    return {block_entity_const: path for path, block_entity_const in pattern.findall(mod_blocks)}


def method_exists(text: str, method: str) -> bool:
    return re.search(rf"\b{re.escape(method)}\s*\(", text) is not None


def main() -> int:
    errors: list[str] = []
    mod_blocks = strip_comments(read(MOD_BLOCKS))
    block_declarations = collect_block_declarations(mod_blocks)
    registered_blocks = collect_registered_blocks(mod_blocks)
    block_entity_declarations = collect_block_entity_declarations(mod_blocks)
    block_entity_registrations = collect_block_entity_registrations(mod_blocks)

    base_block_entity = read(BASE_BLOCK_ENTITY)
    if "void refresh()" in base_block_entity:
        errors.append("BaseBlockEntity still exposes the legacy combined refresh path.")
    if "protected final void setChangedAndSync()" not in base_block_entity:
        errors.append("BaseBlockEntity is missing an explicit dirty-and-client-sync path.")
    if "Block.UPDATE_CLIENTS" not in base_block_entity:
        errors.append("BaseBlockEntity client synchronization still requires neighbor updates.")
    if "copy.set(i, stacks.get(i).copy());" not in base_block_entity:
        errors.append("BaseBlockEntity does not deep-copy item stacks for public inventory snapshots.")
    for path, expected_return in SNAPSHOT_CONTAINER_GETTERS.items():
        if expected_return not in read(path):
            errors.append(f"{path.name} exposes mutable block entity inventory state.")
    for path, expected_returns in SNAPSHOT_STATE_RETURNS.items():
        block_entity_text = read(path)
        for expected_return in expected_returns:
            if expected_return not in block_entity_text:
                errors.append(f"{path.name} exposes mutable block entity state: {expected_return}")
    for path in EXPLICIT_CLIENT_SYNC_BLOCK_ENTITIES:
        block_entity_text = read(path)
        if "this.refresh();" in block_entity_text:
            errors.append(f"{path.name} still uses the legacy combined refresh path.")
        if "this.setChangedAndSync();" not in block_entity_text:
            errors.append(f"{path.name} does not use the explicit dirty-and-client-sync path.")
    oil_pot_block_entity = read(BLOCK_ENTITY_ROOT / "decoration/OilPotBlockEntity.java")
    if "updateNeighbourForOutputSignal" not in oil_pot_block_entity:
        errors.append("OilPotBlockEntity does not notify comparators after oil count changes.")
    if "public boolean setOilCount(" not in oil_pot_block_entity:
        errors.append("OilPotBlockEntity oil mutations do not report whether the world-state update succeeded.")
    oil_pot_load = oil_pot_block_entity.split("public void loadAdditional", 1)[-1].split("public int getOilCount", 1)[0]
    if "setBlock(" in oil_pot_load:
        errors.append("OilPotBlockEntity mutates the world while loading serialized data.")
    oil_pot_block = read(BLOCK_ROOT / "kitchen/OilPotBlock.java")
    insert_commit = "if (!oilPot.setOilCount(currentOilCount + addOilCount))"
    extract_commit = "if (!oilPot.setOilCount(currentOilCount - takeCount))"
    if insert_commit not in oil_pot_block:
        errors.append("OilPotBlock consumes oil without confirming the storage mutation.")
    if extract_commit not in oil_pot_block:
        errors.append("OilPotBlock gives oil before confirming the storage mutation.")
    if oil_pot_block.find(insert_commit) > oil_pot_block.find("stack.consume(addOilCount, player)"):
        errors.append("OilPotBlock consumes oil before committing it to storage.")
    if oil_pot_block.find(extract_commit) > oil_pot_block.find(
            "player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.OIL, takeCount))"
    ):
        errors.append("OilPotBlock gives oil before committing its removal from storage.")
    kitchenware_racks_block = read(BLOCK_ROOT / "kitchen/KitchenwareRacksBlock.java")
    for required_reference in (
        "public @NotNull InteractionResult useWithoutItem(",
        "racks.onClick(player, stack, isLeft)",
        "racks.onClick(player, ItemStack.EMPTY, isLeft)",
    ):
        if required_reference not in kitchenware_racks_block:
            errors.append(f"KitchenwareRacksBlock native interaction split is missing {required_reference}.")
    if "mainHandItem.isEmpty()" in kitchenware_racks_block:
        errors.append("KitchenwareRacksBlock still handles unreachable empty-hand takeout in useItemOn.")
    transmutation_lunch_bag = read(JAVA_ROOT / "item/TransmutationLunchBagItem.java")
    if "ItemStackContainer.wrap(fruitBasket.getItems())" in transmutation_lunch_bag:
        errors.append("TransmutationLunchBagItem still mutates the fruit basket inventory directly.")
    if "fruitBasket.refresh()" in transmutation_lunch_bag:
        errors.append("TransmutationLunchBagItem still calls the legacy block entity refresh API.")
    fruit_basket = read(BLOCK_ENTITY_ROOT / "decoration/FruitBasketBlockEntity.java")
    fruit_basket_block = read(BLOCK_ROOT / "decoration/FruitBasketBlock.java")
    if "public boolean putOn(" not in fruit_basket or "public boolean takeOut(" not in fruit_basket:
        errors.append("FruitBasketBlockEntity does not report whether inventory interactions changed state.")
    fruit_basket_use_item = fruit_basket_block.split("public @NotNull InteractionResult useItemOn(", 1)[-1].split(
        "public @NotNull InteractionResult useWithoutItem(", 1
    )[0]
    if "stack.isEmpty()" in fruit_basket_use_item or "this.useWithoutItem(" in fruit_basket_use_item:
        errors.append("FruitBasketBlock still manually forwards empty stacks from useItemOn.")
    if "fruitBasket.putOn(stack, !player.hasInfiniteMaterials())" not in fruit_basket_use_item:
        errors.append("FruitBasketBlock does not use the stack supplied to its item interaction entry point.")
    fruit_basket_use_without_item = fruit_basket_block.split(
        "public @NotNull InteractionResult useWithoutItem(", 1
    )[-1].split("private static InteractionResult takeOut(", 1)[0]
    if "InteractionResult.TRY_WITH_EMPTY_HAND" in fruit_basket_use_without_item:
        errors.append("FruitBasketBlock recursively requests empty-hand dispatch from useWithoutItem.")
    if fruit_basket_block.count("level.gameEvent(GameEvent.BLOCK_CHANGE, pos") < 2:
        errors.append("FruitBasketBlock does not emit block-change events for successful inventory interactions.")
    kitchenware_racks = read(BLOCK_ENTITY_ROOT / "kitchen/KitchenwareRacksBlockEntity.java")
    if "if (this.level == null || this.level.isClientSide())" not in kitchenware_racks:
        errors.append("KitchenwareRacksBlockEntity mutates detached or client-side state.")
    if kitchenware_racks.count("this.level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition") < 2:
        errors.append("KitchenwareRacksBlockEntity does not emit block-change events for rack mutations.")
    chopping_board = read(BLOCK_ENTITY_ROOT / "kitchen/ChoppingBoardBlockEntity.java")
    if chopping_board.count("level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition") < 4:
        errors.append("ChoppingBoardBlockEntity does not emit block-change events for all board mutations.")
    if "ItemStack returned = this.currentCutStack.copy();\n            this.resetBoardData();" not in chopping_board:
        errors.append("ChoppingBoardBlockEntity returns ingredients before clearing the board.")
    chopping_board_block = read(BLOCK_ROOT / "kitchen/ChoppingBoardBlock.java")
    chopping_board_use_item = chopping_board_block.split(
        "public @NotNull InteractionResult useItemOn(", 1
    )[-1].split("public @NotNull InteractionResult useWithoutItem(", 1)[0]
    if "ItemStack itemInHand = stack;" not in chopping_board_use_item:
        errors.append("ChoppingBoardBlock does not use the stack supplied to its item interaction entry point.")
    chopping_board_use_without_item = chopping_board_block.split(
        "public @NotNull InteractionResult useWithoutItem(", 1
    )[-1].split("public BlockEntity newBlockEntity(", 1)[0]
    if "InteractionResult.TRY_WITH_EMPTY_HAND" in chopping_board_use_without_item:
        errors.append("ChoppingBoardBlock recursively requests empty-hand dispatch from useWithoutItem.")
    shawarma_spit = read(BLOCK_ENTITY_ROOT / "kitchen/ShawarmaSpitBlockEntity.java")
    if "cookTime--;\n            this.setChanged();" not in shawarma_spit:
        errors.append("ShawarmaSpitBlockEntity does not mark cooking progress dirty.")
    if "public ItemStack cookingItem" in shawarma_spit or "public ItemStack cookedItem" in shawarma_spit or "public int cookTime" in shawarma_spit:
        errors.append("ShawarmaSpitBlockEntity still exposes mutable cooking state.")
    shawarma_block = read(BLOCK_ROOT / "kitchen/ShawarmaSpitBlock.java")
    set_placed_start = shawarma_block.find("public void setPlacedBy(")
    set_placed_end = shawarma_block.find("public @NotNull FluidState getFluidState(", set_placed_start)
    set_placed_by = shawarma_block[set_placed_start:set_placed_end]
    if "level.getFluidState(upperPos)" not in set_placed_by:
        errors.append("ShawarmaSpitBlock does not copy waterlogging from the upper placement position.")
    if "level.isClientSide()" in set_placed_by:
        errors.append("ShawarmaSpitBlock suppresses vanilla client-side placement of its upper half.")
    destroy_start = shawarma_block.find("public @NotNull BlockState playerWillDestroy(")
    destroy_end = shawarma_block.find("private static BlockPos getStoragePos(", destroy_start)
    player_will_destroy = shawarma_block[destroy_start:destroy_end]
    confirmed_removal = "if (level.setBlock(below, airBlockState, Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL))"
    if confirmed_removal not in player_will_destroy:
        errors.append("ShawarmaSpitBlock does not confirm creative lower-half removal.")
    if player_will_destroy.find("popResource(level, below, storedItem)") < player_will_destroy.find(confirmed_removal):
        errors.append("ShawarmaSpitBlock drops stored food before confirming creative lower-half removal.")
    shawarma_renderer = read(ROOT / "src/client/java/com/github/ysbbbbbb/kaleidoscopecookery/client/render/block/ShawarmaSpitBlockEntityRender.java")
    shawarma_jade = read(ROOT / "src/client/java/com/github/ysbbbbbb/kaleidoscopecookery/compat/jade/block/ShawarmaSpitComponentProvider.java")
    if any("shawarmaSpit.cookingItem" in text or "shawarmaSpit.cookedItem" in text for text in (shawarma_block, shawarma_renderer, shawarma_jade)):
        errors.append("Shawarma spit consumers still access mutable item fields directly.")
    steamer = read(BLOCK_ENTITY_ROOT / "kitchen/SteamerBlockEntity.java")
    if "stack.isEmpty() || steamer.cookingTime[i] < 0" not in steamer:
        errors.append("SteamerBlockEntity still reprocesses completed cooking slots.")
    if "Block.UPDATE_ALL" in steamer:
        errors.append("SteamerBlockEntity still broadcasts neighbor updates for inventory changes.")
    if "if (!level.removeBlock(this.getBlockPos(), false))" not in steamer:
        errors.append("SteamerBlockEntity returns an empty layer before confirming block removal.")
    if "if (!level.setBlockAndUpdate(this.getBlockPos(), blockState.setValue(SteamerBlock.HALF, true)))" not in steamer:
        errors.append("SteamerBlockEntity returns a stacked layer before confirming the half-state update.")
    if steamer.count("GameEvent.BLOCK_CHANGE") < 3 or "GameEvent.BLOCK_DESTROY" not in steamer:
        errors.append("SteamerBlockEntity does not emit game events for food and layer mutations.")
    steamer_block = read(BLOCK_ROOT / "kitchen/SteamerBlock.java")
    steamer_use_item = steamer_block.split("public @NotNull InteractionResult useItemOn(", 1)[-1].split(
        "public @NotNull InteractionResult useWithoutItem(", 1
    )[0]
    if "ItemStack itemInHand = stack;" not in steamer_use_item:
        errors.append("SteamerBlock does not use the stack supplied to its item interaction entry point.")
    if "itemInHand.isEmpty()" in steamer_use_item or "this.useWithoutItem(" in steamer_use_item:
        errors.append("SteamerBlock still manually forwards empty stacks from useItemOn.")
    steamer_use_without_item = steamer_block.split("public @NotNull InteractionResult useWithoutItem(", 1)[-1].split(
        "private static boolean canInteractWithOpenLayer(", 1
    )[0]
    if "InteractionResult.TRY_WITH_EMPTY_HAND" in steamer_use_without_item:
        errors.append("SteamerBlock recursively requests empty-hand dispatch from useWithoutItem.")
    if "if (!level.setBlockAndUpdate(pos, state.setValue(HAS_LID, !hasLid)))" not in steamer_block:
        errors.append("SteamerBlock does not confirm lid state changes.")
    if "level.gameEvent(GameEvent.BLOCK_CHANGE, pos" not in steamer_block:
        errors.append("SteamerBlock does not emit block-change events for lid interactions.")
    trash_can = read(BLOCK_ENTITY_ROOT / "misc/TrashCanBlockEntity.java")
    if "if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof ItemEntity itemEntity))" not in trash_can:
        errors.append("TrashCanBlockEntity still mutates absorbed item entities on the client.")
    if "level.blockEvent" not in trash_can or "boolean triggerEvent" not in trash_can:
        errors.append("TrashCanBlockEntity does not synchronize animations through block events.")
    for required_reference in (
        "public boolean putItem(",
        "public boolean withdrawItem(",
    ):
        if required_reference not in trash_can:
            errors.append(f"TrashCanBlockEntity mutation result contract is missing {required_reference}.")
    if trash_can.count("if (!(this.level instanceof ServerLevel)") != 2:
        errors.append("TrashCanBlockEntity manual inventory mutations are not server-authoritative.")
    chair_block = read(BLOCK_ROOT / "decoration/ChairBlock.java")
    table_block = read(BLOCK_ROOT / "decoration/TableBlock.java")
    if ".refresh()" in chair_block or ".refresh()" in table_block:
        errors.append("Furniture blocks still call the legacy block entity refresh API.")
    if "tableItems.set(" in table_block:
        errors.append("TableBlock still mutates the table block entity inventory directly.")
    if "public InteractionResult useWithoutItem(" not in table_block:
        errors.append("TableBlock does not route empty-hand takeout through the native interaction entry point.")
    if "boolean handEmpty" in table_block:
        errors.append("TableBlock still handles unreachable empty-hand takeout in useItemOn.")
    table_block_entity = read(BLOCK_ENTITY_ROOT / "decoration/TableBlockEntity.java")
    if table_block_entity.count("this.level == null || this.level.isClientSide()") != 2:
        errors.append("TableBlockEntity inventory mutations are not server-authoritative.")
    if table_block.count("setBlockAndUpdate(pos, state.setValue(HAS_CARPET, true))") != 1:
        errors.append("TableBlock does not reserve block-state updates for initial carpet placement.")
    if chair_block.count("setBlockAndUpdate(pos, state.setValue(HAS_CARPET, true))") != 1:
        errors.append("ChairBlock does not reserve block-state updates for initial carpet placement.")
    if "if (!level.setBlockAndUpdate(pos, state.setValue(HAS_CARPET, true)))" not in table_block:
        errors.append("TableBlock does not confirm initial carpet placement.")
    if "if (!level.setBlockAndUpdate(pos, state.setValue(HAS_CARPET, true)))" not in chair_block:
        errors.append("ChairBlock does not confirm initial carpet placement.")
    recipe_block = read(BLOCK_ROOT / "misc/RecipeBlock.java")
    if "getItems()" in recipe_block:
        errors.append("RecipeBlock still accesses the recipe block entity container directly.")
    recipe_block_entity = read(BLOCK_ENTITY_ROOT / "decoration/RecipeBlockEntity.java")
    if "ItemStackContainer" in recipe_block_entity:
        errors.append("RecipeBlockEntity still wraps its single display item in a mutable container.")
    teapot = read(BLOCK_ENTITY_ROOT / "kitchen/TeapotBlockEntity.java")
    if teapot.count("this.setChanged();") < 2:
        errors.append("TeapotBlockEntity does not persist both cooking progress phases.")
    if "if (!level.removeBlock(worldPosition, false))" not in teapot:
        errors.append("TeapotBlockEntity returns its drops before confirming block removal.")
    if "level.gameEvent(GameEvent.BLOCK_DESTROY, worldPosition" not in teapot:
        errors.append("TeapotBlockEntity does not emit a block-destroy event when taken.")
    if teapot.count("level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition") < 4:
        errors.append("TeapotBlockEntity does not emit block-change events for all content mutations.")
    millstone = read(BLOCK_ENTITY_ROOT / "kitchen/MillstoneBlockEntity.java")
    if "this.progress--;\n            this.setChanged();" not in millstone:
        errors.append("MillstoneBlockEntity does not persist grinding progress each tick.")
    if "this.progress % 10 == 0) {\n                this.syncToClient();" not in millstone:
        errors.append("MillstoneBlockEntity does not retain its periodic client progress updates.")
    if "Mth.positiveModulo(this.cacheRot + gameTime * degPerTick, 360.0)" not in millstone:
        errors.append("MillstoneBlockEntity does not normalize its world-time rotation phase.")
    if "this.rotSpeedTick = Math.max(data.rotSpeedTick(), 1);\n        this.cacheRot = getRotationOffset" not in millstone:
        errors.append("MillstoneBlockEntity does not preserve rotation when changing bindable speeds.")
    millstone_block = read(BLOCK_ROOT / "kitchen/MillstoneBlock.java")
    millstone_use_item = millstone_block.split(
        "public @NotNull InteractionResult useItemOn(", 1
    )[-1].split("public @NotNull InteractionResult useWithoutItem(", 1)[0]
    if "ItemStack mainHandItem = stack;" not in millstone_use_item:
        errors.append("MillstoneBlock does not use the stack supplied to its item interaction entry point.")
    millstone_use_without_item = millstone_block.split(
        "public @NotNull InteractionResult useWithoutItem(", 1
    )[-1].split("public void stepOn(", 1)[0]
    if "InteractionResult.TRY_WITH_EMPTY_HAND" in millstone_use_without_item:
        errors.append("MillstoneBlock recursively requests empty-hand dispatch from useWithoutItem.")
    stockpot = read(BLOCK_ENTITY_ROOT / "kitchen/StockpotBlockEntity.java")
    if "private void setLidItem(ItemStack lidItem)" not in stockpot:
        errors.append("StockpotBlockEntity still exposes direct lid item mutation.")
    if "this.lidItem = lidItem.copyWithCount(1);\n        this.setChangedAndSync();" not in stockpot:
        errors.append("StockpotBlockEntity does not own lid item copying, persistence, and synchronization.")
    if stockpot.count("if (!level.setBlockAndUpdate(worldPosition, updatedState))") < 2:
        errors.append("StockpotBlockEntity transfers lid items before confirming block-state updates.")
    if stockpot.count("level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition") < 8:
        errors.append("StockpotBlockEntity does not emit block-change events for all interactive mutations.")
    pot = read(BLOCK_ENTITY_ROOT / "kitchen/PotBlockEntity.java")
    if "if (!level.setBlockAndUpdate(worldPosition, updatedState))" not in pot:
        errors.append("PotBlockEntity consumes oil without confirming the block-state update.")
    if pot.count("level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition") < 4:
        errors.append("PotBlockEntity does not emit block-change events for all content mutations.")
    if pot.count("this.inputs.set(i,") > pot.count("this.setChangedAndSync();"):
        errors.append("PotBlockEntity input mutations can outnumber explicit dirty-and-sync paths.")

    declared_consts = set(block_entity_declarations)
    expected_consts = set(EXPECTED_BLOCK_ENTITY_IDS)
    missing_declarations = expected_consts - declared_consts
    unexpected_declarations = declared_consts - expected_consts
    if missing_declarations:
        errors.append(f"Missing expected block entity declarations: {sorted(missing_declarations)}")
    if unexpected_declarations:
        errors.append(f"Unexpected block entity declarations need audit entries: {sorted(unexpected_declarations)}")

    registered_consts = set(block_entity_registrations)
    missing_registrations = declared_consts - registered_consts
    extra_registrations = registered_consts - declared_consts
    if missing_registrations:
        errors.append(f"Block entity declarations not registered: {sorted(missing_registrations)}")
    if extra_registrations:
        errors.append(f"Unknown block entity registrations: {sorted(extra_registrations)}")

    block_classes_checked: set[str] = set()
    stateful_count = 0
    stateless_count = 0

    for const, declaration in sorted(block_entity_declarations.items()):
        entity_class = str(declaration["class"])
        ctor_class = str(declaration["ctor"])
        target_blocks = list(declaration["blocks"])

        if entity_class != ctor_class:
            errors.append(f"{const} declares {entity_class} but uses constructor {ctor_class}.")

        expected_id = EXPECTED_BLOCK_ENTITY_IDS.get(const)
        actual_id = block_entity_registrations.get(const)
        if expected_id is not None and actual_id != expected_id:
            errors.append(f"{const} registered as {actual_id!r}, expected {expected_id!r}.")

        entity_file = find_class_file(BLOCK_ENTITY_ROOT, entity_class)
        if entity_file is None:
            errors.append(f"{const} class file not found or ambiguous: {entity_class}.")
            continue

        entity_text = strip_comments(read(entity_file))
        super_pattern = rf"super\s*\(\s*ModBlocks\.{re.escape(const)}\s*,"
        if not re.search(super_pattern, entity_text):
            errors.append(f"{entity_class} constructor does not call super(ModBlocks.{const}, ...).")

        has_save = method_exists(entity_text, "saveAdditional")
        has_load = method_exists(entity_text, "loadAdditional")
        if entity_class in STATELESS_BLOCK_ENTITIES:
            stateless_count += 1
            if has_save or has_load:
                errors.append(f"{entity_class} is marked stateless but declares save/load methods.")
        else:
            stateful_count += 1
            if not has_save or not has_load:
                errors.append(f"{entity_class} should declare both saveAdditional and loadAdditional.")
            if has_save and "super.saveAdditional" not in entity_text:
                errors.append(f"{entity_class}.saveAdditional does not call super.saveAdditional.")
            if has_load and "super.loadAdditional" not in entity_text:
                errors.append(f"{entity_class}.loadAdditional does not call super.loadAdditional.")

        if not target_blocks:
            errors.append(f"{const} has no target blocks in FabricBlockEntityTypeBuilder.")
        for block_const in target_blocks:
            block_class = block_declarations.get(block_const)
            block_id = registered_blocks.get(block_const)
            if block_class is None:
                errors.append(f"{const} references undeclared block constant {block_const}.")
                continue
            if block_id is None:
                errors.append(f"{const} references block {block_const}, but that block is not registered.")
            if block_class in block_classes_checked:
                continue
            block_file = find_class_file(BLOCK_ROOT, block_class)
            if block_file is None:
                errors.append(f"{block_const} block class not found or ambiguous: {block_class}.")
                continue
            block_text = strip_comments(read(block_file))
            if "newBlockEntity" not in block_text:
                errors.append(f"{block_class} does not override newBlockEntity.")
            if f"new {entity_class}(" not in block_text:
                errors.append(f"{block_class}.newBlockEntity does not create {entity_class}.")
            if "getTicker" in block_text and f"ModBlocks.{const}" not in block_text:
                errors.append(f"{block_class}.getTicker does not guard against ModBlocks.{const}.")
            block_classes_checked.add(block_class)

    block_entity_data_errors: list[str] = []
    for path in sorted(JAVA_ROOT.rglob("*.java")):
        text = strip_comments(read(path))
        for match in re.finditer(r"BlockItem\.setBlockEntityData\([^,]+,\s*([^,\n)]+)", text):
            type_reference = match.group(1).strip()
            if type_reference not in EXPECTED_BLOCK_ENTITY_DATA_REFERENCES:
                block_entity_data_errors.append(f"{path.relative_to(ROOT)} -> {type_reference}")
        for match in re.finditer(r"TypedEntityData\.of\(\s*([^,\n)]+)", text):
            type_reference = match.group(1).strip()
            if type_reference not in EXPECTED_BLOCK_ENTITY_DATA_REFERENCES:
                block_entity_data_errors.append(f"{path.relative_to(ROOT)} -> {type_reference}")
    if block_entity_data_errors:
        errors.append("Unexpected block entity data type references:")
        errors.extend(f"  - {entry}" for entry in block_entity_data_errors)

    if errors:
        print("Block entity verification failed:")
        print("\n".join(errors))
        return 1

    print("Block entity verification passed.")
    print(f"  block entity types: {len(block_entity_declarations)}")
    print(f"  stateful block entities: {stateful_count}")
    print(f"  stateless block entities: {stateless_count}")
    print(f"  block classes checked: {len(block_classes_checked)}")
    print(f"  inventory snapshot getters: {len(SNAPSHOT_CONTAINER_GETTERS)}")
    print(f"  item/array snapshot getters: {sum(map(len, SNAPSHOT_STATE_RETURNS.values()))}")
    print("  legacy block entity ids: recipe_book -> RECIPE_BLOCK_BE")
    return 0


if __name__ == "__main__":
    sys.exit(main())
