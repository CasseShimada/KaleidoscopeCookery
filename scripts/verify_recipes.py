#!/usr/bin/env python3
"""Verify recipe serializers, recipe types, and recipe JSON agree."""

from __future__ import annotations

import json
import re
import sys
from collections import Counter
from pathlib import Path
from typing import Any, Iterable

from resource_roots import RESOURCE_ROOTS, iter_resource_files, resource_relative


MOD_ID = "kaleidoscope_cookery"
ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery"
RESOURCES = ROOT / "src/main/resources"
MOD_RECIPES = JAVA_ROOT / "init/ModRecipes.java"
MOD_EVENTS = JAVA_ROOT / "init/ModEvents.java"
MILLSTONE_BLOCK_ENTITY = JAVA_ROOT / "blockentity/kitchen/MillstoneBlockEntity.java"
MILLSTONE_TAKE_ITEM_CALLBACK = JAVA_ROOT / "api/event/MillstoneTakeItemCallback.java"
RECIPE_ROOT = JAVA_ROOT / "crafting/recipe"
SERIALIZER_ROOT = JAVA_ROOT / "crafting/serializer"
RECIPE_EVENT_FILES = {
    "MillstoneSpecialRecipeEvent": JAVA_ROOT / "event/recipe/MillstoneSpecialRecipeEvent.java",
    "MillstoneSpecialFinishEvent": JAVA_ROOT / "event/recipe/MillstoneSpecialFinishEvent.java",
    "SpecialRecipeItemEvent": JAVA_ROOT / "event/SpecialRecipeItemEvent.java",
}

# CustomRecipe serializers identify vanilla crafting recipes directly and do
# not need a custom RecipeType registration.
SERIALIZER_ONLY_RECIPE_IDS = {
    "rice_bowl",
}


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


def collect_string_calls(path: Path, function_name: str) -> set[str]:
    pattern = re.compile(rf"\b{re.escape(function_name)}\(\s*\"([a-z0-9_./-]+)\"")
    return set(pattern.findall(read(path)))


def collect_registered_items() -> set[str]:
    ids = collect_string_calls(JAVA_ROOT / "init/ModItems.java", "register")
    ids.update(collect_string_calls(JAVA_ROOT / "init/registry/FoodBiteRegistry.java", "registerFoodData"))
    ids.update(collect_string_calls(JAVA_ROOT / "init/registry/PlateRegistry.java", "registerPlateData"))
    ids.update(collect_string_calls(JAVA_ROOT / "init/registry/TeacupRegistry.java", "registerTeacupData"))
    return ids


def collect_serializer_declarations(text: str) -> dict[str, dict[str, str]]:
    pattern = re.compile(
        r"public\s+static\s+final\s+RecipeSerializer<(?P<recipe_class>\w+)>\s+"
        r"(?P<const>\w+_SERIALIZER)\s*=\s*new\s+RecipeSerializer<>\("
        r"\s*(?P<serializer_class>\w+)\.CODEC\s*,\s*(?P=serializer_class)\.STREAM_CODEC\s*\)",
        flags=re.DOTALL,
    )
    return {
        match.group("const"): {
            "recipe_class": match.group("recipe_class"),
            "serializer_class": match.group("serializer_class"),
        }
        for match in pattern.finditer(text)
    }


def collect_recipe_type_declarations(text: str) -> dict[str, dict[str, str]]:
    pattern = re.compile(
        r"public\s+static\s+final\s+RecipeType<(?P<recipe_class>\w+)>\s+"
        r"(?P<const>\w+_RECIPE)\s*=\s*simple\(id\(\"(?P<id>[a-z0-9_./-]+)\"\)\)",
        flags=re.DOTALL,
    )
    return {
        match.group("const"): {
            "recipe_class": match.group("recipe_class"),
            "id": match.group("id"),
        }
        for match in pattern.finditer(text)
    }


def collect_registrations(text: str, function_name: str) -> dict[str, str]:
    pattern = re.compile(rf'{re.escape(function_name)}\(\s*"([a-z0-9_./-]+)"\s*,\s*(\w+)\s*\)')
    return {const: recipe_id for recipe_id, const in pattern.findall(text)}


def iter_recipe_files() -> Iterable[Path]:
    for path in iter_resource_files("data", pattern="*.json"):
        parts = resource_relative(path).parts
        if len(parts) >= 4 and parts[2] == "recipe":
            yield path


def collect_result_ids(value: Any) -> Iterable[str]:
    if isinstance(value, dict):
        result = value.get("result")
        if isinstance(result, str):
            yield result
        elif isinstance(result, dict):
            item_id = result.get("id")
            if isinstance(item_id, str):
                yield item_id
        for child in value.values():
            yield from collect_result_ids(child)
    elif isinstance(value, list):
        for child in value:
            yield from collect_result_ids(child)


def collect_legacy_recipe_dirs() -> list[Path]:
    legacy_dirs: list[Path] = []
    for root in RESOURCE_ROOTS:
        for path in (root / "data").rglob("recipes"):
            if path.parent.name == "advancement":
                continue
            legacy_dirs.append(path)
    return legacy_dirs


def main() -> int:
    errors: list[str] = []
    mod_recipes = strip_comments(read(MOD_RECIPES))
    mod_events = strip_comments(read(MOD_EVENTS))
    serializers = collect_serializer_declarations(mod_recipes)
    recipe_types = collect_recipe_type_declarations(mod_recipes)
    serializer_registrations = collect_registrations(mod_recipes, "registerSerializer")
    type_registrations = collect_registrations(mod_recipes, "registerType")
    registered_items = collect_registered_items()

    for event_class, path in RECIPE_EVENT_FILES.items():
        event_text = strip_comments(read(path))
        if re.search(r"public\s+static\s+void\s+register\s*\(\s*\)", event_text) is None:
            errors.append(f"{event_class} does not expose a no-argument register() entrypoint.")
        if f"{event_class}.register();" not in mod_events:
            errors.append(f"ModEvents does not register {event_class}.")
        if re.search(r"public\s+static\s+void\s+(?:onMillstoneTakeItem|onCheckItemEvent|onDeductItemEvent)\s*\(\s*\)", event_text):
            errors.append(f"{event_class} still exposes a legacy action-named registration method.")

    millstone_special_event = strip_comments(read(RECIPE_EVENT_FILES["MillstoneSpecialRecipeEvent"]))
    millstone_block_entity = strip_comments(read(MILLSTONE_BLOCK_ENTITY))
    if not MILLSTONE_TAKE_ITEM_CALLBACK.exists():
        errors.append("Millstone take-item callback is missing.")
    else:
        millstone_callback = strip_comments(read(MILLSTONE_TAKE_ITEM_CALLBACK))
        for required_reference in (
            "Event<MillstoneTakeItemCallback> EVENT = EventFactory.createArrayBacked(",
            "PASS(false, false)",
            "FAILURE(true, false)",
            "SUCCESS(true, true)",
        ):
            if required_reference not in millstone_callback:
                errors.append(f"Millstone take-item callback is missing {required_reference}.")
    for required_reference in (
        "MillstoneTakeItemCallback.EVENT.register(MillstoneSpecialRecipeEvent::onTakeItem)",
        "MillstoneTakeItemCallback.Result.PASS",
        "MillstoneTakeItemCallback.Result.FAILURE",
        "MillstoneTakeItemCallback.Result.SUCCESS",
    ):
        if required_reference not in millstone_special_event:
            errors.append(f"Millstone special extraction is missing {required_reference}.")
    for required_reference in (
        "MillstoneTakeItemCallback.EVENT.invoker().takeItem(user, heldItem, this)",
        "callbackResult.handled()",
        "callbackResult.succeeds()",
    ):
        if required_reference not in millstone_block_entity:
            errors.append(f"Millstone block entity callback handling is missing {required_reference}.")
    for legacy_reference in ("MILLSTONE_TAKE_ITEM", "MillstoneTakeItemEvent", "IActionCancelable"):
        if legacy_reference in mod_events or legacy_reference in millstone_special_event or legacy_reference in millstone_block_entity:
            errors.append(f"Millstone extraction still uses legacy mutable event state: {legacy_reference}.")

    creative_container_guard = re.search(
        r"if\s*\(\s*!user\.hasInfiniteMaterials\(\)\s*\)\s*\{.*?"
        r"ItemUtils\.getContainerItem\(heldItem\.split\(1\)\)",
        millstone_special_event,
        flags=re.DOTALL,
    )
    if creative_container_guard is None:
        errors.append("Millstone raw dough handling consumes a container in creative mode.")
    if "user.hasInfiniteMaterials() ? heldItem.copyWithCount(1) : heldItem.split(1)" not in millstone_special_event:
        errors.append("Millstone oil pot handling does not preserve the creative-mode held stack.")

    declared_serializers = set(serializers)
    registered_serializer_consts = set(serializer_registrations)
    if declared_serializers != registered_serializer_consts:
        errors.append(
            "Recipe serializer declarations and registrations differ: "
            f"declared_only={sorted(declared_serializers - registered_serializer_consts)} "
            f"registered_only={sorted(registered_serializer_consts - declared_serializers)}"
        )

    declared_types = set(recipe_types)
    registered_type_consts = set(type_registrations)
    if declared_types != registered_type_consts:
        errors.append(
            "Recipe type declarations and registrations differ: "
            f"declared_only={sorted(declared_types - registered_type_consts)} "
            f"registered_only={sorted(registered_type_consts - declared_types)}"
        )

    serializer_ids = {const: recipe_id for const, recipe_id in serializer_registrations.items()}
    type_ids = {const: recipe_id for const, recipe_id in type_registrations.items()}
    serializer_ids_by_path = {recipe_id: const for const, recipe_id in serializer_ids.items()}
    type_ids_by_path = {recipe_id: const for const, recipe_id in type_ids.items()}

    for recipe_id, serializer_const in sorted(serializer_ids_by_path.items()):
        if recipe_id in SERIALIZER_ONLY_RECIPE_IDS:
            if recipe_id in type_ids_by_path:
                errors.append(f"{recipe_id} is marked serializer-only but has a RecipeType registration.")
            continue
        if recipe_id not in type_ids_by_path:
            errors.append(f"{serializer_const} ({recipe_id}) has no matching RecipeType registration.")

    for recipe_id, type_const in sorted(type_ids_by_path.items()):
        if recipe_id not in serializer_ids_by_path:
            errors.append(f"{type_const} ({recipe_id}) has no matching RecipeSerializer registration.")

    for const, declaration in sorted(serializers.items()):
        recipe_class = declaration["recipe_class"]
        serializer_class = declaration["serializer_class"]
        recipe_id = serializer_ids.get(const)

        recipe_file = RECIPE_ROOT / f"{recipe_class}.java"
        serializer_file = SERIALIZER_ROOT / f"{serializer_class}.java"
        if not recipe_file.exists():
            errors.append(f"Recipe class file missing: {recipe_class}.")
            continue
        if not serializer_file.exists():
            errors.append(f"Serializer class file missing: {serializer_class}.")
            continue

        recipe_text = strip_comments(read(recipe_file))
        serializer_text = strip_comments(read(serializer_file))
        if f"return ModRecipes.{const}" not in recipe_text:
            errors.append(f"{recipe_class}.getSerializer does not return ModRecipes.{const}.")
        if "public static final MapCodec" not in serializer_text or "public static final StreamCodec" not in serializer_text:
            errors.append(f"{serializer_class} must expose both CODEC and STREAM_CODEC.")

        if recipe_id in SERIALIZER_ONLY_RECIPE_IDS:
            if "extends CustomRecipe" not in recipe_text:
                errors.append(f"{recipe_class} is serializer-only but does not extend CustomRecipe.")
            continue

        type_const = type_ids_by_path.get(recipe_id or "")
        if type_const is None:
            continue
        if f"return ModRecipes.{type_const}" not in recipe_text:
            errors.append(f"{recipe_class}.getType does not return ModRecipes.{type_const}.")

    custom_type_counts: Counter[str] = Counter()
    missing_type_files: list[str] = []
    bad_custom_types: list[str] = []
    bad_results: list[str] = []
    migrated_soup_base_ids: list[str] = []
    recipe_files = list(iter_recipe_files())

    for path in recipe_files:
        data = parse_json(path)
        recipe_type = data.get("type")
        if not isinstance(recipe_type, str):
            missing_type_files.append(str(path.relative_to(ROOT)))
            continue

        parsed_type = split_id(recipe_type)
        if parsed_type is None:
            bad_custom_types.append(f"{path.relative_to(ROOT)} -> malformed type {recipe_type!r}")
            continue

        namespace, type_path = parsed_type
        if namespace == MOD_ID:
            custom_type_counts[type_path] += 1
            if type_path not in serializer_ids_by_path:
                bad_custom_types.append(f"{path.relative_to(ROOT)} -> unregistered custom type {recipe_type}")

        soup_base = data.get("soup_base")
        if soup_base in {"minecraft:water_bucket", "minecraft:lava_bucket"}:
            migrated_soup_base_ids.append(f"{path.relative_to(ROOT)} -> {soup_base}")

        for result_id in collect_result_ids(data):
            parsed_result = split_id(result_id)
            if parsed_result is None:
                continue
            result_namespace, result_path = parsed_result
            if result_namespace == MOD_ID and result_path not in registered_items:
                bad_results.append(f"{path.relative_to(ROOT)} -> result {result_id}")

    if missing_type_files:
        errors.append("Recipe files missing top-level type:")
        errors.extend(f"  - {entry}" for entry in missing_type_files)
    if bad_custom_types:
        errors.append("Recipe files with invalid custom types:")
        errors.extend(f"  - {entry}" for entry in bad_custom_types)
    if bad_results:
        errors.append("Recipe files with unregistered mod result items:")
        errors.extend(f"  - {entry}" for entry in bad_results[:50])
        if len(bad_results) > 50:
            errors.append(f"  ... {len(bad_results) - 50} more")
    if migrated_soup_base_ids:
        errors.append("Stockpot recipes use migrated bucket item IDs instead of stable fluid soup-base IDs:")
        errors.extend(f"  - {entry}" for entry in migrated_soup_base_ids)

    missing_custom_recipe_json = sorted(set(serializer_ids_by_path) - set(custom_type_counts))
    if missing_custom_recipe_json:
        errors.append(f"Registered custom recipe serializers with no recipe JSON: {missing_custom_recipe_json}")

    legacy_dirs = collect_legacy_recipe_dirs()
    if legacy_dirs:
        errors.append("Legacy data/*/recipes directories found outside advancements:")
        errors.extend(f"  - {path.relative_to(ROOT)}" for path in legacy_dirs)

    if errors:
        print("Recipe verification failed:")
        print("\n".join(errors))
        return 1

    print("Recipe verification passed.")
    print(f"  custom serializers: {len(serializer_ids_by_path)}")
    print(f"  custom recipe types: {len(type_ids_by_path)}")
    print(f"  serializer-only recipes: {len(SERIALIZER_ONLY_RECIPE_IDS)}")
    print(f"  recipe files: {len(recipe_files)}")
    print("  custom recipe JSON:")
    for recipe_id, count in sorted(custom_type_counts.items()):
        print(f"  - {MOD_ID}:{recipe_id}: {count}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
