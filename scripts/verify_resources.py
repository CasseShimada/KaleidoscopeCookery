#!/usr/bin/env python3
"""Verify registered cookery content has matching client resources.

This is intentionally lightweight: it mirrors the registration patterns used by
the current Fabric port and checks the resource files that would otherwise fail
late during resource loading.
"""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any, Iterable

from resource_roots import iter_resource_files, resolve_resource


MOD_ID = "kaleidoscope_cookery"
ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery"
RESOURCES = ROOT / "src/main/resources"
ASSETS = RESOURCES / "assets" / MOD_ID

ALLOWED_ITEM_DEFINITION_VARIANTS = {
    "egg",
    "fruit_basket_full",
    "honey",
    "kitchen_shovel_has_oil",
    "kitchen_shovel_no_oil",
    "oil_in_millstone",
    "pot_has_oil",
    "pot_no_oil",
    "raw_dough_0",
    "raw_dough_1",
    "raw_dough_2",
    "raw_dough_3",
    "raw_dough_4",
    "raw_dough_in_millstone",
    "recipe_item_has_recipe",
    "recipe_item_no_recipe",
    "steamer_has_items",
    "steamer_no_items",
    "stockpot_lid_normal",
    "stockpot_lid_using",
    "transmutation_lunch_bag_has_items",
    "transmutation_lunch_bag_no_items",
}

CUSTOM_DROP_BLOCKS = {
    "bamboo_tube_rice",
    "barley_tea",
    "biluochun",
    "empty_cup",
    "flower_tea",
    "millstone",
    "oolong",
    "recipe_block",
    "sakura_fubuki",
    "steamer",
    "teapot",
    "tieguanyin",
}

BASELINE_PLATE_LOOT_BLOCKS = {
    "apple_platter",
    "baozi_plate",
    "berry_platter",
    "chorus_fruit_platter",
    "qingtuan_plate",
    "shengjian_mantou_plate",
    "sticky_candy_plate",
    "sticky_rice_cake_plate",
    "tomato_platter",
    "watermelon_platter",
    "zongzi_plate",
}

REQUIRED_LANGUAGES = {
    "en_us",
    "es_es",
    "ja_jp",
    "ko_kr",
    "lol_us",
    "lzh",
    "pt_br",
    "ru_ru",
    "zh_cn",
    "zh_tw",
}

REQUIRED_PONDER_SCENES = {
    "enamel_basin/enamel_basin_introduction.nbt",
    "millstone/millstone_introduction.nbt",
    "pot/pot_introduction.nbt",
    "shawarma_spit/shawarma_spit_introduction.nbt",
    "steamer/steamer_introduction.nbt",
    "stockpot/stockpot_introduction.nbt",
}


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def parse_json(path: Path) -> Any:
    with path.open("r", encoding="utf-8-sig") as handle:
        return json.load(handle)


def collect_string_calls(path: Path, function_name: str) -> set[str]:
    pattern = re.compile(rf"\b{re.escape(function_name)}\(\s*\"([a-z0-9_./-]+)\"")
    return set(pattern.findall(read(path)))


def collect_registered_items() -> set[str]:
    ids = collect_string_calls(JAVA_ROOT / "init/ModItems.java", "register")
    ids.update(collect_string_calls(JAVA_ROOT / "init/registry/FoodBiteRegistry.java", "registerFoodData"))
    ids.update(collect_string_calls(JAVA_ROOT / "init/registry/PlateRegistry.java", "registerPlateData"))
    ids.update(collect_string_calls(JAVA_ROOT / "init/registry/TeacupRegistry.java", "registerTeacupData"))
    return ids


def collect_registered_blocks() -> set[str]:
    ids = collect_string_calls(JAVA_ROOT / "init/ModBlocks.java", "registerBlock")
    ids.update(collect_string_calls(JAVA_ROOT / "init/registry/FoodBiteRegistry.java", "registerFoodData"))
    ids.update(collect_string_calls(JAVA_ROOT / "init/registry/PlateRegistry.java", "registerPlateData"))
    ids.update(collect_string_calls(JAVA_ROOT / "init/registry/TeacupRegistry.java", "registerTeacupData"))
    return ids


def walk_json_strings(value: Any, parent_key: str | None = None) -> Iterable[tuple[str | None, str]]:
    if isinstance(value, dict):
        for key, child in value.items():
            yield from walk_json_strings(child, key)
    elif isinstance(value, list):
        for child in value:
            yield from walk_json_strings(child, parent_key)
    elif isinstance(value, str):
        yield parent_key, value


def walk_texture_strings(value: Any) -> Iterable[str]:
    if isinstance(value, dict):
        textures = value.get("textures")
        if isinstance(textures, dict):
            for _, texture in walk_json_strings(textures):
                yield texture
        for child in value.values():
            yield from walk_texture_strings(child)
    elif isinstance(value, list):
        for child in value:
            yield from walk_texture_strings(child)


def split_id(value: str) -> tuple[str, str] | None:
    if value.startswith("#"):
        return None
    if ":" not in value:
        return None
    namespace, path = value.split(":", 1)
    return namespace, path


def model_path(identifier: str) -> Path | None:
    parsed = split_id(identifier)
    if parsed is None:
        return None
    namespace, path = parsed
    if namespace != MOD_ID:
        return None
    return RESOURCES / "assets" / namespace / "models" / f"{path}.json"


def texture_path(identifier: str) -> Path | None:
    parsed = split_id(identifier)
    if parsed is None:
        return None
    namespace, path = parsed
    if namespace != MOD_ID:
        return None
    return RESOURCES / "assets" / namespace / "textures" / f"{path}.png"


def sound_path(identifier: str) -> Path | None:
    parsed = split_id(identifier)
    if parsed is None:
        return None
    namespace, path = parsed
    if namespace != MOD_ID:
        return None
    return RESOURCES / "assets" / namespace / "sounds" / f"{path}.ogg"


def report_missing(kind: str, missing: Iterable[str], errors: list[str]) -> None:
    missing_list = sorted(set(missing))
    if not missing_list:
        return
    errors.append(f"{kind}: {len(missing_list)} missing")
    for item in missing_list[:50]:
        errors.append(f"  - {item}")
    if len(missing_list) > 50:
        errors.append(f"  ... {len(missing_list) - 50} more")


def validate_registry_id_constants() -> list[str]:
    errors: list[str] = []
    registries = (
        (JAVA_ROOT / "init/registry/PlateRegistry.java", "registerPlateData"),
        (JAVA_ROOT / "init/registry/TeacupRegistry.java", "registerTeacupData"),
    )
    for path, register_method in registries:
        text = read(path)
        if re.search(r"public\s+static\s+(?!final\b)Identifier\s+\w+\s*(?:=|;)", text):
            errors.append(f"{path.relative_to(ROOT)} exposes mutable registry identifiers.")

        registered_ids = collect_string_calls(path, register_method)
        constant_pattern = re.compile(
            rf"public\s+static\s+final\s+Identifier\s+\w+\s*=\s*"
            rf"{re.escape(register_method)}\(\s*\"([a-z0-9_./-]+)\""
        )
        constant_ids = set(constant_pattern.findall(text))
        if constant_ids != registered_ids:
            errors.append(
                f"{path.relative_to(ROOT)} does not bind every registered id to a static final constant."
            )

    food_registry = JAVA_ROOT / "init/registry/FoodBiteRegistry.java"
    food_text = read(food_registry)
    if re.search(r"public\s+static\s+(?!final\b)Identifier\s+\w+\s*(?:=|;)", food_text):
        errors.append(f"{food_registry.relative_to(ROOT)} exposes mutable registry identifiers.")
    declared_constants = set(re.findall(
        r"public\s+static\s+final\s+Identifier\s+(\w+)\s*;",
        food_text,
    ))
    assigned_constants = set(re.findall(
        r"\b(\w+)\s*=\s*registerFoodData\(\s*\"[a-z0-9_./-]+\"",
        food_text,
    ))
    if declared_constants != assigned_constants:
        errors.append(
            f"{food_registry.relative_to(ROOT)} does not assign every registry id constant exactly once."
        )
    return errors


def validate_plate_loot_tables() -> list[str]:
    errors: list[str] = []
    for block_id in sorted(BASELINE_PLATE_LOOT_BLOCKS):
        path = resolve_resource("data", MOD_ID, "loot_table", "blocks", f"{block_id}.json")
        if not path.exists():
            errors.append(f"Forge plate loot table missing: {block_id}")
            continue
        data = parse_json(path)
        pools = data.get("pools")
        if not isinstance(pools, list) or len(pools) != 1:
            errors.append(f"{path.relative_to(ROOT)} must contain exactly one bowl loot pool.")
            continue
        pool = pools[0]
        entries = pool.get("entries") if isinstance(pool, dict) else None
        conditions = pool.get("conditions") if isinstance(pool, dict) else None
        entry_names = [entry.get("name") for entry in entries if isinstance(entry, dict)] \
            if isinstance(entries, list) else []
        condition_ids = [condition.get("condition") for condition in conditions if isinstance(condition, dict)] \
            if isinstance(conditions, list) else []
        if entry_names != ["minecraft:bowl"]:
            errors.append(f"{path.relative_to(ROOT)} does not restore the single Forge bowl drop.")
        if condition_ids != ["minecraft:survives_explosion"]:
            errors.append(f"{path.relative_to(ROOT)} does not restore survives_explosion.")
    return errors


def main() -> int:
    errors: list[str] = []
    errors.extend(validate_registry_id_constants())
    errors.extend(validate_plate_loot_tables())

    json_errors: list[str] = []
    for path in iter_resource_files(pattern="*.json"):
        try:
            parse_json(path)
        except Exception as exc:  # noqa: BLE001 - print path and parser error.
            json_errors.append(f"{path.relative_to(ROOT)}: {exc}")
    report_missing("invalid json", json_errors, errors)

    item_ids = collect_registered_items()
    block_ids = collect_registered_blocks()
    effect_ids = collect_string_calls(JAVA_ROOT / "init/ModEffects.java", "register")

    item_defs = {path.stem for path in (ASSETS / "items").glob("*.json")}
    blockstates = {path.stem for path in (ASSETS / "blockstates").glob("*.json")}
    block_loot_tables = {
        path.stem
        for path in iter_resource_files("data", MOD_ID, "loot_table", "blocks", pattern="*.json")
    }
    effect_textures = {path.stem for path in (ASSETS / "textures/mob_effect").glob("*.png")}
    languages = {path.stem for path in (ASSETS / "lang").glob("*.json")}
    ponder_root = ASSETS / "ponder"
    ponder_scenes = {
        path.relative_to(ponder_root).as_posix()
        for path in ponder_root.rglob("*.nbt")
    } if ponder_root.exists() else set()

    report_missing(
        "registered items without item definitions",
        (f"{MOD_ID}:{item_id}" for item_id in item_ids - item_defs),
        errors,
    )
    report_missing(
        "registered blocks without blockstates",
        (f"{MOD_ID}:{block_id}" for block_id in block_ids - blockstates),
        errors,
    )
    report_missing(
        "unregistered item definitions",
        (f"{MOD_ID}:{item_id}" for item_id in item_defs - item_ids - ALLOWED_ITEM_DEFINITION_VARIANTS),
        errors,
    )
    report_missing(
        "unregistered blockstates",
        (f"{MOD_ID}:{block_id}" for block_id in blockstates - block_ids),
        errors,
    )
    report_missing(
        "registered blocks without loot table or custom drop whitelist",
        (f"{MOD_ID}:{block_id}" for block_id in block_ids - block_loot_tables - CUSTOM_DROP_BLOCKS),
        errors,
    )
    report_missing(
        "loot tables for unregistered blocks",
        (f"{MOD_ID}:{block_id}" for block_id in block_loot_tables - block_ids),
        errors,
    )
    report_missing(
        "custom drop whitelist entries for unregistered blocks",
        (f"{MOD_ID}:{block_id}" for block_id in CUSTOM_DROP_BLOCKS - block_ids),
        errors,
    )
    report_missing(
        "custom drop whitelist entries that now also have loot tables",
        (f"{MOD_ID}:{block_id}" for block_id in CUSTOM_DROP_BLOCKS & block_loot_tables),
        errors,
    )
    report_missing(
        "registered effects without mob-effect textures",
        (f"{MOD_ID}:{effect_id}" for effect_id in effect_ids - effect_textures),
        errors,
    )
    report_missing(
        "mob-effect textures without registered effects",
        (f"{MOD_ID}:{effect_id}" for effect_id in effect_textures - effect_ids),
        errors,
    )
    report_missing("baseline languages", REQUIRED_LANGUAGES - languages, errors)
    report_missing("baseline Ponder scenes in the main pack", REQUIRED_PONDER_SCENES - ponder_scenes, errors)

    lang_en = parse_json(ASSETS / "lang/en_us.json")
    lang_zh = parse_json(ASSETS / "lang/zh_cn.json")
    for language, lang in (("en_us", lang_en), ("zh_cn", lang_zh)):
        report_missing(
            f"{language} missing block lang keys",
            (f"block.{MOD_ID}.{block_id}" for block_id in block_ids if f"block.{MOD_ID}.{block_id}" not in lang),
            errors,
        )
        report_missing(
            f"{language} missing item/block lang keys",
            (
                f"item.{MOD_ID}.{item_id}"
                for item_id in item_ids
                if f"item.{MOD_ID}.{item_id}" not in lang and f"block.{MOD_ID}.{item_id}" not in lang
            ),
            errors,
        )

    missing_models: list[str] = []
    missing_textures: list[str] = []
    for path in (
        list((ASSETS / "items").glob("*.json"))
        + list((ASSETS / "blockstates").glob("*.json"))
        + list((ASSETS / "models").rglob("*.json"))
    ):
        data = parse_json(path)
        for key, value in walk_json_strings(data):
            if key in {"model", "parent"}:
                candidate = model_path(value)
                if candidate is not None and not candidate.exists():
                    missing_models.append(f"{path.relative_to(ROOT)} -> {value}")
        for value in walk_texture_strings(data):
            candidate = texture_path(value)
            if candidate is not None and not candidate.exists():
                missing_textures.append(f"{path.relative_to(ROOT)} -> {value}")

    report_missing("missing local model references", missing_models, errors)
    report_missing("missing local texture references", missing_textures, errors)

    sounds_file = ASSETS / "sounds.json"
    if sounds_file.exists():
        missing_sounds: list[str] = []
        sounds = parse_json(sounds_file)
        for key, value in walk_json_strings(sounds):
            if key != "sounds":
                continue
            candidate = sound_path(value)
            if candidate is not None and not candidate.exists():
                missing_sounds.append(f"{sounds_file.relative_to(ROOT)} -> {value}")
        report_missing("missing local sound references", missing_sounds, errors)

    if errors:
        print("Resource verification failed:")
        print("\n".join(errors))
        return 1

    print("Resource verification passed.")
    print(f"  registered items: {len(item_ids)}")
    print(f"  registered blocks: {len(block_ids)}")
    print(f"  item definitions: {len(item_defs)}")
    print(f"  blockstates: {len(blockstates)}")
    print(f"  block loot tables: {len(block_loot_tables)}")
    print(f"  custom drop blocks: {len(CUSTOM_DROP_BLOCKS)}")
    print(f"  mob effects/textures: {len(effect_ids)}/{len(effect_textures)}")
    print(f"  baseline languages: {len(REQUIRED_LANGUAGES)}")
    print(f"  main-pack Ponder scenes: {len(ponder_scenes)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
