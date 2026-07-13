#!/usr/bin/env python3
"""Verify datapack resources that are not covered by recipe/resource checks."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any, Iterable

from resource_roots import iter_resource_files, resolve_resource, resource_relative


MOD_ID = "kaleidoscope_cookery"
ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery"
RESOURCES = ROOT / "src/main/resources"
ASSETS = RESOURCES / "assets" / MOD_ID


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def strip_comments(text: str) -> str:
    text = re.sub(r"/\*.*?\*/", "", text, flags=re.DOTALL)
    return re.sub(r"//.*", "", text)


def parse_json(path: Path) -> Any:
    with path.open("r", encoding="utf-8-sig") as handle:
        return json.load(handle)


def split_id(value: str) -> tuple[str, str] | None:
    if value.startswith("#") or ":" not in value:
        return None
    namespace, path = value.split(":", 1)
    return namespace, path


def walk(value: Any, parent_key: str | None = None) -> Iterable[tuple[str | None, Any]]:
    if isinstance(value, dict):
        for key, child in value.items():
            yield key, child
            yield from walk(child, key)
    elif isinstance(value, list):
        for child in value:
            yield from walk(child, parent_key)


def walk_strings(value: Any, parent_key: str | None = None) -> Iterable[tuple[str | None, str]]:
    for key, child in walk(value, parent_key):
        if isinstance(child, str):
            yield key, child


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


def collect_registered_entities() -> set[str]:
    return set(re.findall(
        r'ResourceKey<EntityType<\?>>\s+\w+_KEY\s*=\s*key\("([a-z0-9_./-]+)"\)',
        read(JAVA_ROOT / "init/ModEntities.java"),
    ))


def collect_registered_pois() -> set[str]:
    return collect_string_calls(JAVA_ROOT / "init/ModPoi.java", "registerPoiType")


def collect_mod_triggers() -> set[str]:
    return set(re.findall(r'register\(\s*"([a-z0-9_./-]+)"', read(JAVA_ROOT / "init/ModTrigger.java")))


def collect_mod_villager_professions() -> set[str]:
    return set(re.findall(r'ResourceKey\.create\(\s*Registries\.VILLAGER_PROFESSION\s*,\s*id\("([a-z0-9_./-]+)"\)', read(JAVA_ROOT / "init/ModVillager.java")))


def collect_loot_codec_ids() -> tuple[set[str], set[str]]:
    class_ids: dict[str, str] = {}
    for path in sorted((JAVA_ROOT / "loot").glob("*.java")):
        text = read(path)
        match = re.search(r'public\s+static\s+final\s+Identifier\s+ID\s*=\s*Identifier\.fromNamespaceAndPath\([^,]+,\s*"([a-z0-9_./-]+)"\)', text)
        if match:
            class_ids[path.stem] = match.group(1)

    mod_loot_types = read(JAVA_ROOT / "init/ModLootTypes.java")
    function_classes = re.findall(r"Registry\.register\(\s*BuiltInRegistries\.LOOT_FUNCTION_TYPE\s*,\s*(\w+)\.ID", mod_loot_types)
    condition_classes = re.findall(r"registerCondition\(\s*(\w+)\.ID", mod_loot_types)
    return (
        {class_ids[class_name] for class_name in function_classes if class_name in class_ids},
        {class_ids[class_name] for class_name in condition_classes if class_name in class_ids},
    )


def iter_data_files(category: str) -> Iterable[Path]:
    for path in iter_resource_files("data", pattern="*.json"):
        parts = resource_relative(path).parts
        if len(parts) >= 4 and parts[2] == category:
            yield path


def tag_path(namespace: str, registry: str, tag_id: str) -> Path:
    return resolve_resource("data", namespace, "tags", registry, f"{tag_id}.json")


def recipe_path(identifier: str) -> Path | None:
    parsed = split_id(identifier)
    if parsed is None:
        return None
    namespace, path = parsed
    return resolve_resource("data", namespace, "recipe", f"{path}.json")


def advancement_path(identifier: str) -> Path | None:
    parsed = split_id(identifier)
    if parsed is None:
        return None
    namespace, path = parsed
    return resolve_resource("data", namespace, "advancement", f"{path}.json")


def texture_path(identifier: str) -> Path | None:
    parsed = split_id(identifier)
    if parsed is None:
        return None
    namespace, path = parsed
    if namespace != MOD_ID:
        return None
    return resolve_resource("assets", namespace, path)


def tag_values(data: Any) -> Iterable[str]:
    values = data.get("values") if isinstance(data, dict) else None
    if not isinstance(values, list):
        return
    for value in values:
        if isinstance(value, str):
            yield value
        elif isinstance(value, dict) and isinstance(value.get("id"), str):
            yield value["id"]


def validate_mod_item_ref(path: Path, value: str, registered_items: set[str], errors: list[str]) -> None:
    parsed = split_id(value)
    if parsed is None:
        return
    namespace, item_id = parsed
    if namespace == MOD_ID and item_id not in registered_items:
        errors.append(f"{path.relative_to(ROOT)} references unregistered item {value}.")


def validate_tag_files(registered_items: set[str], registered_blocks: set[str], registered_entities: set[str],
                       registered_pois: set[str]) -> list[str]:
    errors: list[str] = []
    registries = {
        "item": registered_items,
        "block": registered_blocks,
        "entity_type": registered_entities,
        "point_of_interest_type": registered_pois,
    }

    for path in iter_data_files("tags"):
        rel_parts = resource_relative(path).parts[1:]
        if len(rel_parts) < 4:
            continue
        namespace = rel_parts[0]
        registry = rel_parts[2]
        if registry not in registries and registry not in {"damage_type", "villager_trade"}:
            continue
        data = parse_json(path)
        for value in tag_values(data):
            if value.startswith("#"):
                parsed = split_id(value[1:])
                if parsed is None:
                    continue
                ref_namespace, ref_path = parsed
                if ref_namespace == MOD_ID and not tag_path(ref_namespace, registry, ref_path).exists():
                    errors.append(f"{path.relative_to(ROOT)} references missing tag {value}.")
                continue

            parsed = split_id(value)
            if parsed is None:
                continue
            ref_namespace, ref_path = parsed
            if registry == "villager_trade" and ref_namespace == MOD_ID:
                trade_path = resolve_resource("data", MOD_ID, "villager_trade", f"{ref_path}.json")
                if not trade_path.exists():
                    errors.append(f"{path.relative_to(ROOT)} references missing villager trade {value}.")
            elif registry in registries and ref_namespace == MOD_ID and ref_path not in registries[registry]:
                errors.append(f"{path.relative_to(ROOT)} references unregistered {registry} {value}.")
            elif registry == "damage_type" and ref_namespace == MOD_ID:
                errors.append(f"{path.relative_to(ROOT)} references mod damage type {value}, but no mod damage types are registered.")

        if namespace == MOD_ID and "values" not in data:
            errors.append(f"{path.relative_to(ROOT)} is missing a values array.")

    return errors


def validate_advancements(registered_items: set[str], trigger_ids: set[str]) -> list[str]:
    errors: list[str] = []
    lang_en = parse_json(ASSETS / "lang/en_us.json")
    lang_zh = parse_json(ASSETS / "lang/zh_cn.json")

    for path in iter_data_files("advancement"):
        data = parse_json(path)

        parent = data.get("parent")
        if isinstance(parent, str):
            parsed_parent = split_id(parent)
            if parsed_parent is not None and parsed_parent[0] == MOD_ID:
                parent_path = advancement_path(parent)
                if parent_path is not None and not parent_path.exists():
                    errors.append(f"{path.relative_to(ROOT)} references missing advancement parent {parent}.")

        display = data.get("display")
        if isinstance(display, dict):
            icon = display.get("icon")
            if isinstance(icon, dict) and isinstance(icon.get("id"), str):
                validate_mod_item_ref(path, icon["id"], registered_items, errors)
            background = display.get("background")
            if isinstance(background, str):
                candidate = texture_path(background)
                if candidate is not None and not candidate.exists():
                    errors.append(f"{path.relative_to(ROOT)} references missing advancement background {background}.")

        for key, value in walk_strings(data):
            if key == "trigger":
                parsed_trigger = split_id(value)
                if parsed_trigger is not None and parsed_trigger[0] == MOD_ID and parsed_trigger[1] not in trigger_ids:
                    errors.append(f"{path.relative_to(ROOT)} references unregistered criterion trigger {value}.")
            elif key == "recipe":
                candidate = recipe_path(value)
                if candidate is not None and not candidate.exists():
                    errors.append(f"{path.relative_to(ROOT)} references missing recipe {value}.")
            elif key == "translate" and value.startswith(f"advancements.{MOD_ID}."):
                if value not in lang_en:
                    errors.append(f"{path.relative_to(ROOT)} missing en_us advancement lang key {value}.")
                if value not in lang_zh:
                    errors.append(f"{path.relative_to(ROOT)} missing zh_cn advancement lang key {value}.")

        rewards = data.get("rewards")
        if isinstance(rewards, dict):
            recipes = rewards.get("recipes")
            if isinstance(recipes, list):
                for recipe_id in recipes:
                    if not isinstance(recipe_id, str):
                        continue
                    candidate = recipe_path(recipe_id)
                    if candidate is not None and not candidate.exists():
                        errors.append(f"{path.relative_to(ROOT)} rewards missing recipe {recipe_id}.")

    return errors


def validate_loot_tables(registered_items: set[str], loot_functions: set[str], loot_conditions: set[str]) -> list[str]:
    errors: list[str] = []
    for path in iter_data_files("loot_table"):
        data = parse_json(path)
        for key, value in walk_strings(data):
            if key == "name":
                validate_mod_item_ref(path, value, registered_items, errors)
            elif key == "function":
                parsed = split_id(value)
                if parsed is not None and parsed[0] == MOD_ID and parsed[1] not in loot_functions:
                    errors.append(f"{path.relative_to(ROOT)} references unregistered loot function {value}.")
            elif key == "condition":
                parsed = split_id(value)
                if parsed is not None and parsed[0] == MOD_ID and parsed[1] not in loot_conditions:
                    errors.append(f"{path.relative_to(ROOT)} references unregistered loot condition {value}.")
            elif key in {"items", "item"}:
                if value.startswith("#"):
                    continue
                validate_mod_item_ref(path, value, registered_items, errors)
    return errors


def validate_trades(registered_items: set[str], villager_professions: set[str],
                    loot_functions: set[str]) -> tuple[list[str], int, int]:
    errors: list[str] = []
    trade_sets = list(iter_data_files("trade_set"))
    trade_files = list(iter_data_files("villager_trade"))

    for path in trade_sets:
        rel = resource_relative(path).parts[3:]
        if len(rel) < 2:
            errors.append(f"{path.relative_to(ROOT)} should be under trade_set/<profession>/...")
            continue
        profession = rel[0]
        if profession not in villager_professions:
            errors.append(f"{path.relative_to(ROOT)} uses unregistered villager profession {profession}.")
        data = parse_json(path)
        trades = data.get("trades")
        if not isinstance(trades, str) or not trades.startswith("#"):
            errors.append(f"{path.relative_to(ROOT)} must reference a villager trade tag in trades.")
            continue
        parsed = split_id(trades[1:])
        if parsed is None:
            errors.append(f"{path.relative_to(ROOT)} has malformed trades tag {trades}.")
            continue
        tag_namespace, tag_id = parsed
        if tag_namespace == MOD_ID and not tag_path(tag_namespace, "villager_trade", tag_id).exists():
            errors.append(f"{path.relative_to(ROOT)} references missing trade tag {trades}.")

    for path in trade_files:
        data = parse_json(path)
        for key in ("wants", "gives"):
            stack = data.get(key)
            if not isinstance(stack, dict) or not isinstance(stack.get("id"), str):
                errors.append(f"{path.relative_to(ROOT)} has malformed {key} stack.")
                continue
            validate_mod_item_ref(path, stack["id"], registered_items, errors)
            count = stack.get("count", 1)
            if not isinstance(count, (int, float)) or count <= 0:
                errors.append(f"{path.relative_to(ROOT)} has non-positive {key} count.")
        for key, value in walk_strings(data):
            if key == "function":
                parsed = split_id(value)
                if parsed is not None and parsed[0] == MOD_ID and parsed[1] not in loot_functions:
                    errors.append(f"{path.relative_to(ROOT)} references unregistered trade item modifier {value}.")
            elif key in {"items", "item"} and not value.startswith("#"):
                validate_mod_item_ref(path, value, registered_items, errors)

    return errors, len(trade_sets), len(trade_files)


def validate_millstone_datamap() -> tuple[list[str], int]:
    errors: list[str] = []
    path = resolve_resource("data", MOD_ID, "datamap", "millstone_bindable_data.json")
    data = parse_json(path)
    if not isinstance(data, dict):
        return [f"{path.relative_to(ROOT)} must be an object."], 0
    for entity_id, config in data.items():
        if not isinstance(entity_id, str) or split_id(entity_id) is None:
            errors.append(f"{path.relative_to(ROOT)} has malformed entity id {entity_id!r}.")
        if not isinstance(config, dict):
            errors.append(f"{path.relative_to(ROOT)} entry {entity_id} must be an object.")
            continue
        rot_speed = config.get("rot_speed_tick")
        lift_angle = config.get("lift_angle")
        offset = config.get("offset", [0, 0, 0])
        if not isinstance(rot_speed, int) or rot_speed <= 0:
            errors.append(f"{path.relative_to(ROOT)} entry {entity_id} has invalid rot_speed_tick.")
        if not isinstance(lift_angle, (int, float)):
            errors.append(f"{path.relative_to(ROOT)} entry {entity_id} has invalid lift_angle.")
        if not (isinstance(offset, list) and len(offset) == 3 and all(isinstance(value, (int, float)) for value in offset)):
            errors.append(f"{path.relative_to(ROOT)} entry {entity_id} has invalid offset.")
    return errors, len(data)


def validate_millstone_datamap_reloader() -> list[str]:
    errors: list[str] = []
    listener = strip_comments(read(
        JAVA_ROOT / "datamap/resources/MillstoneBindableDataReloadListener.java"
    ))
    common_registry = strip_comments(read(JAVA_ROOT / "init/registry/CommonRegistry.java"))

    if "extends SimplePreparableReloadListener<Map<EntityType<?>, MillstoneBindableData>>" not in listener:
        errors.append("Millstone datamap does not use the vanilla prepare/apply reload lifecycle.")
    if not re.search(r"static\s+volatile\s+Map<EntityType<\?>,\s*MillstoneBindableData>\s+data", listener):
        errors.append("Millstone datamap is not published as an atomic reload snapshot.")
    if "DATA.clear()" in listener:
        errors.append("Millstone datamap reload still clears live mutable state.")
    if "return Map.copyOf(prepared)" not in listener or "data = prepared" not in listener:
        errors.append("Millstone datamap reload does not publish an immutable prepared snapshot.")
    if "registerReloadListener(MillstoneBindableDataReloadListener.ID" not in common_registry:
        errors.append("Millstone datamap reload listener is not registered through Fabric ResourceLoader.")
    return errors


def validate_village_structures() -> tuple[list[str], int]:
    errors: list[str] = []
    event_text = strip_comments(read(JAVA_ROOT / "event/server/AddVillageStructuresEvent.java"))
    structure_paths = re.findall(r'new\s+VillageKitchen\([^,]+,\s*"([a-z0-9_./-]+)"\s*,', event_text)
    processor_lists = set(re.findall(r'Registries\.PROCESSOR_LIST,\s*Identifier\.fromNamespaceAndPath\([^,]+,\s*"([a-z0-9_./-]+)"\)', event_text))

    for structure_path in structure_paths:
        path = resolve_resource("data", MOD_ID, "structure", f"{structure_path}.nbt")
        if not path.exists():
            errors.append(f"AddVillageStructuresEvent references missing structure {path.relative_to(ROOT)}.")
    for processor_list in processor_lists:
        path = resolve_resource("data", MOD_ID, "worldgen", "processor_list", f"{processor_list}.json")
        if not path.exists():
            errors.append(f"AddVillageStructuresEvent references missing processor list {path.relative_to(ROOT)}.")

    mod_events = read(JAVA_ROOT / "init/ModEvents.java")
    if "AddVillageStructuresEvent.register()" not in mod_events:
        errors.append("AddVillageStructuresEvent is not registered from ModEvents.")

    return errors, len(structure_paths)


def main() -> int:
    registered_items = collect_registered_items()
    registered_blocks = collect_registered_blocks()
    registered_entities = collect_registered_entities()
    registered_pois = collect_registered_pois()
    trigger_ids = collect_mod_triggers()
    villager_professions = collect_mod_villager_professions()
    loot_functions, loot_conditions = collect_loot_codec_ids()

    errors: list[str] = []
    errors.extend(validate_tag_files(registered_items, registered_blocks, registered_entities, registered_pois))
    errors.extend(validate_advancements(registered_items, trigger_ids))
    errors.extend(validate_loot_tables(registered_items, loot_functions, loot_conditions))
    trade_errors, trade_set_count, trade_count = validate_trades(registered_items, villager_professions, loot_functions)
    errors.extend(trade_errors)
    datamap_errors, datamap_entries = validate_millstone_datamap()
    errors.extend(datamap_errors)
    errors.extend(validate_millstone_datamap_reloader())
    village_errors, village_structures = validate_village_structures()
    errors.extend(village_errors)

    if errors:
        print("Datapack verification failed:")
        print("\n".join(errors))
        return 1

    print("Datapack verification passed.")
    print(f"  registered mod triggers: {len(trigger_ids)}")
    print(f"  registered loot functions: {len(loot_functions)}")
    print(f"  registered loot conditions: {len(loot_conditions)}")
    print(f"  villager trade sets: {trade_set_count}")
    print(f"  villager trades: {trade_count}")
    print(f"  millstone datamap entries: {datamap_entries}")
    print(f"  village structures: {village_structures}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
