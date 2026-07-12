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
    print("  legacy block entity ids: recipe_book -> RECIPE_BLOCK_BE")
    return 0


if __name__ == "__main__":
    sys.exit(main())
