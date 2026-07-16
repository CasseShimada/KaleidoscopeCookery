#!/usr/bin/env python3
"""Verify client-side renderer, model-layer, particle, and sound wiring."""

from __future__ import annotations

import json
import hashlib
import re
import sys
from pathlib import Path
from typing import Any, Iterable

from resource_roots import iter_resource_files, resource_relative


MOD_ID = "kaleidoscope_cookery"
ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery"
CLIENT_JAVA_ROOT = ROOT / "src/client/java/com/github/ysbbbbbb/kaleidoscopecookery"
CLIENT_ROOT = CLIENT_JAVA_ROOT / "client"
RESOURCES = ROOT / "src/main/resources"
ASSETS = RESOURCES / "assets" / MOD_ID
LEGACY_PACK = RESOURCES / "resourcepacks" / "legacy_resources_pack"
LEGACY_ASSETS = LEGACY_PACK / "assets" / MOD_ID

OPTIONAL_BLOCK_ENTITY_RENDERERS = {
    "OIL_POT_BE",
    # Legacy recipe_block deserialization normalizes instances to RECIPE_BLOCK_BE.
    "FORGE_RECIPE_BLOCK_BE",
}

REQUIRED_HUMANOID_EQUIPMENT_LAYERS = {
    "cookery_farmer": {"humanoid", "humanoid_leggings"},
}

REQUIRED_BASELINE_TEXTURE_HASHES = {
    "textures/block/stone_bricks.png": "24415444c9e4815aad4a3a349ecbfc0e027b96be25ac5b7c273c06f16c4fdd2f",
    "textures/gui/jei/teapot.png": "480c7610a517ea86a3c95431e241829b517516f78d0057b4f588bb413747368a",
}

BASELINE_CUSTOM_CROPS = ("chili", "lettuce", "tomato")
TABLE_WOOD_TYPES = (
    "acacia", "bamboo", "birch", "cherry", "crimson", "dark_oak",
    "jungle", "mangrove", "oak", "spruce", "warped",
)


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def strip_comments(text: str) -> str:
    text = re.sub(r"/\*.*?\*/", "", text, flags=re.DOTALL)
    return re.sub(r"//.*", "", text)


def parse_json(path: Path) -> Any:
    with path.open("r", encoding="utf-8-sig") as handle:
        return json.load(handle)


def walk_strings(value: Any) -> Iterable[str]:
    if isinstance(value, dict):
        for child in value.values():
            yield from walk_strings(child)
    elif isinstance(value, list):
        for child in value:
            yield from walk_strings(child)
    elif isinstance(value, str):
        yield value


def find_class_file(root: Path, class_name: str) -> Path | None:
    matches = list(root.rglob(f"{class_name}.java"))
    if len(matches) == 1:
        return matches[0]
    return None


def collect_string_calls(path: Path, function_name: str) -> dict[str, str]:
    pattern = re.compile(rf'\b{re.escape(function_name)}\(\s*"([a-z0-9_./-]+)"\s*,\s*(\w+)')
    return {const: resource_id for resource_id, const in pattern.findall(read(path))}


def collect_block_entity_declarations() -> set[str]:
    text = strip_comments(read(JAVA_ROOT / "init/ModBlocks.java"))
    return set(re.findall(r"public\s+static\s+final\s+BlockEntityType<\w+>\s+(\w+)\s*=", text))


def collect_registered_entities() -> dict[str, str]:
    mod_entities = strip_comments(read(JAVA_ROOT / "init/ModEntities.java"))
    const_to_type: dict[str, str] = {}
    for entity_class, const in re.findall(
        r"public\s+static\s+final\s+EntityType<(\w+)>\s+(\w+)\s*=\s*EntityType\.Builder",
        mod_entities,
    ):
        const_to_type[const] = entity_class
    return const_to_type


def collect_model_layers() -> tuple[set[str], set[str]]:
    registered: set[str] = set()
    baked: set[str] = set()
    for path in sorted(CLIENT_JAVA_ROOT.rglob("*.java")):
        text = strip_comments(read(path))
        registered.update(re.findall(r"ModelLayerRegistry\.registerModelLayer\(\s*(\w+)\.LAYER_LOCATION", text))
        baked.update(re.findall(r"bakeLayer\(\s*(\w+)\.LAYER_LOCATION\s*\)", text))
    return registered, baked


def collect_mod_texture_refs() -> list[tuple[Path, str]]:
    refs: list[tuple[Path, str]] = []
    pattern = re.compile(
        r'Identifier\.fromNamespaceAndPath\(\s*KaleidoscopeCookery\.MOD_ID\s*,\s*"([^"]+)"\s*\)',
        flags=re.DOTALL,
    )
    for path in sorted(CLIENT_ROOT.rglob("*.java")):
        text = strip_comments(read(path))
        for value in pattern.findall(text):
            if value.startswith("textures/"):
                refs.append((path, value))
    return refs


def collect_client_init_calls() -> set[str]:
    text = strip_comments(read(CLIENT_JAVA_ROOT / "KaleidoscopeCookeryClient.java"))
    calls = set(re.findall(r"\b(\w+)\.(?:init|register)\(\s*\)", text))
    if "ClientRegistry.init()" in text:
        calls.add("ClientRegistry")
    return calls


def collect_equipment_assets() -> set[str]:
    text = strip_comments(read(JAVA_ROOT / "init/ModArmorMaterials.java"))
    return set(re.findall(
        r'ResourceKey\.create\(\s*EquipmentAssets\.ROOT_ID\s*,\s*id\("([a-z0-9_./-]+)"\)\s*\)',
        text,
    ))


def validate_renderers() -> list[str]:
    errors: list[str] = []
    client_init_calls = collect_client_init_calls()
    expected_client_calls = {
        "ClientRegistry",
        "LegacyResourcePack",
        "ModModelLoading",
        "ModClientTooltip",
        "ModEntitiesRender",
        "ModParticleFactoryRegistry",
    }
    missing_client_calls = expected_client_calls - client_init_calls
    if missing_client_calls:
        errors.append(f"KaleidoscopeCookeryClient is missing client init calls: {sorted(missing_client_calls)}")

    client_registry = strip_comments(read(CLIENT_ROOT / "init/ClientRegistry.java"))
    mod_entities_render = strip_comments(read(CLIENT_ROOT / "init/ModEntitiesRender.java"))

    entity_types = collect_registered_entities()
    for const, entity_class in sorted(entity_types.items()):
        if f"EntityRenderers.register(ModEntities.{const}" not in mod_entities_render:
            errors.append(f"Entity renderer missing for ModEntities.{const} ({entity_class}).")
        entity_file = find_class_file(JAVA_ROOT / "entity", entity_class)
        if entity_file is None:
            errors.append(f"Registered entity class not found: {entity_class}.")
        elif f"TYPE = ModEntities.{const}" not in strip_comments(read(entity_file)):
            errors.append(f"{entity_class}.TYPE does not preserve the ModEntities.{const} compatibility bridge.")

    block_entity_consts = collect_block_entity_declarations()
    rendered_block_entities = set(re.findall(r"BlockEntityRenderers\.register\(\s*ModBlocks\.(\w+)\s*,\s*(\w+)::new", client_registry))
    rendered_consts = {const for const, _ in rendered_block_entities}
    unknown_consts = rendered_consts - block_entity_consts
    if unknown_consts:
        errors.append(f"Block entity renderers reference unknown block entity types: {sorted(unknown_consts)}")
    missing_custom_renderers = block_entity_consts - rendered_consts - OPTIONAL_BLOCK_ENTITY_RENDERERS
    if missing_custom_renderers:
        errors.append(f"Block entity renderers missing or not whitelisted: {sorted(missing_custom_renderers)}")

    for _, renderer_class in sorted(rendered_block_entities):
        if find_class_file(CLIENT_ROOT / "render/block", renderer_class) is None:
            errors.append(f"Block entity renderer class not found: {renderer_class}.")

    return errors


def validate_model_layers() -> list[str]:
    errors: list[str] = []
    registered, baked = collect_model_layers()
    missing = baked - registered
    if missing:
        errors.append(f"Model layers baked but not registered: {sorted(missing)}")
    for model_class in sorted(registered | baked):
        model_file = find_class_file(CLIENT_ROOT / "model", model_class)
        if model_file is None:
            errors.append(f"Model layer class not found: {model_class}.")
        else:
            model_text = strip_comments(read(model_file))
            if "LAYER_LOCATION" not in model_text:
                errors.append(f"{model_class} does not declare LAYER_LOCATION.")
    return errors


def validate_resource_reloaders() -> list[str]:
    errors: list[str] = []
    listener = strip_comments(read(CLIENT_ROOT / "resources/ItemRenderReplacerReloadListener.java"))
    client_registry = strip_comments(read(CLIENT_ROOT / "init/ClientRegistry.java"))

    if "extends SimplePreparableReloadListener<ItemRenderReplacer>" not in listener:
        errors.append("Item render replacements do not use the vanilla prepare/apply reload lifecycle.")
    if not re.search(r"static\s+volatile\s+ItemRenderReplacer\s+replacer", listener):
        errors.append("Item render replacements are not published as an atomic reload snapshot.")
    if "REPLACER.clear()" in listener:
        errors.append("Item render replacement reload still clears live mutable state.")
    if "replacer = prepared" not in listener:
        errors.append("Prepared item render replacements are not applied as one snapshot.")
    if "registerReloadListener(ItemRenderReplacerReloadListener.ID" not in client_registry:
        errors.append("Item render replacement reload listener is not registered through Fabric ResourceLoader.")
    return errors


def validate_client_initialization() -> list[str]:
    errors: list[str] = []
    legacy_registry = strip_comments(read(CLIENT_ROOT / "resources/LegacyResourcePack.java"))
    for required in (
        '"legacy_resources_pack"',
        "ResourceLoader.registerBuiltinPack(",
        "PackActivationType.NORMAL",
        'Component.translatable("pack.kaleidoscope_cookery.legacy_resources_pack.title")',
    ):
        if required not in legacy_registry:
            errors.append(f"Legacy resource-pack registration lost baseline behavior: {required}")

    if not LEGACY_PACK.is_dir():
        errors.append("The built-in legacy resource pack directory is missing.")
    else:
        legacy_assets = list((LEGACY_PACK / "assets").rglob("*"))
        legacy_asset_count = sum(path.is_file() for path in legacy_assets)
        if legacy_asset_count < 1400:
            errors.append(
                f"The built-in legacy resource pack has only {legacy_asset_count} assets; baseline has 1400."
            )
        pack_meta = parse_json(LEGACY_PACK / "pack.mcmeta").get("pack", {})
        if pack_meta.get("min_format") != 88 or pack_meta.get("max_format") != 107:
            errors.append("The legacy resource pack does not declare the Minecraft 26.2 resource format range 88..107.")
        description = pack_meta.get("description")
        if not isinstance(description, dict) or description.get("translate") != (
            "pack.kaleidoscope_cookery.legacy_resources_pack.desc"
        ):
            errors.append("The legacy resource pack description is not localized through the baseline key.")

    lang_en = parse_json(ASSETS / "lang/en_us.json")
    lang_zh = parse_json(ASSETS / "lang/zh_cn.json")
    for key in (
        "pack.kaleidoscope_cookery.legacy_resources_pack.title",
        "pack.kaleidoscope_cookery.legacy_resources_pack.desc",
    ):
        if key not in lang_en or key not in lang_zh:
            errors.append(f"Legacy resource-pack translation is missing: {key}")

    pot_overlay = strip_comments(read(CLIENT_ROOT / "event/PotOverlayEvent.java"))
    trash_overlay = strip_comments(read(CLIENT_ROOT / "event/TrashCanOverlayEvent.java"))
    for name, text in (("pot", pot_overlay), ("trash can", trash_overlay)):
        if "attachElementAfter(VanillaHudElements.CROSSHAIR" not in text:
            errors.append(f"The {name} HUD is not registered immediately above the crosshair as in Forge.")
    for required in (
        "kaleidoscopeCookery$getOverlayMessageTime() > 0",
        "y -= 12",
    ):
        if required not in pot_overlay:
            errors.append(f"Pot HUD lost action-bar message avoidance: {required}")

    hud_accessor = strip_comments(read(CLIENT_JAVA_ROOT / "mixin/client/HudAccessor.java"))
    if '@Accessor("overlayMessageTime")' not in hud_accessor:
        errors.append("HudAccessor does not expose the 26.2 overlay-message timer read-only.")
    return errors


def validate_legacy_models() -> list[str]:
    errors: list[str] = []
    model_root = LEGACY_ASSETS / "models"
    if not model_root.is_dir():
        return ["The legacy resource pack has no Cookery model directory."]

    for path in sorted(model_root.rglob("*.json")):
        model = parse_json(path)
        if not isinstance(model, dict):
            errors.append(f"Legacy model is not a JSON object: {path.relative_to(ROOT)}")
            continue
        if model.get("loader") == "forge:separate_transforms":
            errors.append(f"Legacy model still uses the unsupported Forge transform loader: {path.relative_to(ROOT)}")
        textures = model.get("textures")
        if isinstance(textures, dict) and "particle" not in textures and not isinstance(model.get("parent"), str):
            errors.append(f"Standalone legacy model has no particle texture: {path.relative_to(ROOT)}")

    expected_selectors = {
        "cold_cut_ham_slices": (
            {"gui", "fixed"},
            "kaleidoscope_cookery:item/cold_cut_ham_slices",
            "kaleidoscope_cookery:item/cold_cut_ham_slices_block",
            "kaleidoscope_cookery:item/cold_cut_ham_slices_gui",
        ),
        "fruit_basket": (
            {"gui", "ground", "fixed"},
            "kaleidoscope_cookery:item/fruit_basket",
            "kaleidoscope_cookery:item/fruit_basket_full",
            "kaleidoscope_cookery:item/fruit_basket",
        ),
        "teapot": (
            {"gui", "ground", "fixed"},
            "kaleidoscope_cookery:item/teapot",
            "kaleidoscope_cookery:item/teapot_3d",
            "kaleidoscope_cookery:item/teapot",
        ),
    }
    for item_id, (contexts, flat_model, fallback_model, flat_texture) in expected_selectors.items():
        definition_path = LEGACY_ASSETS / "items" / f"{item_id}.json"
        flat_model_path = model_root / "item" / f"{item_id}.json"
        if not definition_path.is_file() or not flat_model_path.is_file():
            errors.append(f"Legacy display-context model is incomplete for {item_id}.")
            continue

        definition = parse_json(definition_path).get("model", {})
        cases = definition.get("cases", [])
        if (
            definition.get("type") != "minecraft:select"
            or definition.get("property") != "minecraft:display_context"
            or len(cases) != 1
        ):
            errors.append(f"Legacy {item_id} does not use one 26.2 display-context selector.")
            continue
        when = cases[0].get("when", [])
        actual_contexts = {when} if isinstance(when, str) else set(when)
        if actual_contexts != contexts:
            errors.append(f"Legacy {item_id} contexts differ from Forge: {sorted(actual_contexts)}")
        if cases[0].get("model", {}).get("model") != flat_model:
            errors.append(f"Legacy {item_id} selector does not use its 2D model.")
        if definition.get("fallback", {}).get("model") != fallback_model:
            errors.append(f"Legacy {item_id} selector does not preserve its 3D fallback.")

        flat_json = parse_json(flat_model_path)
        if (
            flat_json.get("parent") != "minecraft:item/generated"
            or flat_json.get("textures", {}).get("layer0") != flat_texture
        ):
            errors.append(f"Legacy {item_id} 2D model does not preserve its Forge perspective texture.")
    return errors


def validate_baseline_model_migrations() -> list[str]:
    errors: list[str] = []
    cross_path = ASSETS / "models/block/cross.json"
    legacy_cross_path = LEGACY_ASSETS / "models/block/cross.json"
    if not cross_path.is_file() or not legacy_cross_path.is_file():
        errors.append("The baseline lowered cross model is missing from the main or legacy resource pack.")
    else:
        cross_model = parse_json(cross_path)
        if cross_model != parse_json(legacy_cross_path):
            errors.append("The main-pack cross model differs from the exact Forge baseline copy.")
        elements = cross_model.get("elements", []) if isinstance(cross_model, dict) else []
        y_bounds = {
            (element.get("from", [None, None])[1], element.get("to", [None, None])[1])
            for element in elements
            if isinstance(element, dict)
        }
        if len(elements) != 2 or y_bounds != {(-1, 15)}:
            errors.append("The baseline cross model no longer lowers crops one pixel to meet farmland.")

    for crop_id in BASELINE_CUSTOM_CROPS:
        for stage in range(8):
            path = ASSETS / "models/block/crop" / crop_id / f"stage{stage}.json"
            if not path.is_file():
                errors.append(f"Missing baseline {crop_id} crop model: {path.relative_to(ROOT)}")
                continue
            if parse_json(path).get("parent") != f"{MOD_ID}:block/cross":
                errors.append(f"{path.relative_to(ROOT)} no longer uses the lowered baseline cross model.")

    for stage in range(8):
        for section in ("down", "middle", "up"):
            path = ASSETS / "models/block/crop/rice" / f"stage{stage}_{section}.json"
            if not path.is_file():
                errors.append(f"Missing baseline rice crop model: {path.relative_to(ROOT)}")
                continue
            if parse_json(path).get("parent") != "minecraft:block/cross":
                errors.append(f"{path.relative_to(ROOT)} must retain its Forge-baseline vanilla cross parent.")

    position_models = {0: "single", 1: "left", 2: "middle", 3: "right"}
    for wood in TABLE_WOOD_TYPES:
        path = ASSETS / "blockstates" / f"table_{wood}.json"
        if not path.is_file():
            errors.append(f"Missing table blockstate: {path.relative_to(ROOT)}")
            continue

        expected_variants: dict[str, dict[str, str | int]] = {}
        for axis in ("x", "z"):
            for has_carpet in ("false", "true"):
                for position, model_part in position_models.items():
                    for waterlogged in ("false", "true"):
                        key = (
                            f"axis={axis},has_carpet={has_carpet},"
                            f"position={position},waterlogged={waterlogged}"
                        )
                        variant: dict[str, str | int] = {
                            "model": f"{MOD_ID}:block/table/{wood}_{model_part}",
                        }
                        if position != 0:
                            variant["y"] = 180 if axis == "x" else 270
                        expected_variants[key] = variant

        actual_variants = parse_json(path).get("variants")
        if actual_variants != expected_variants:
            errors.append(
                f"{path.relative_to(ROOT)} no longer preserves the 32-state 26.2 rotation replacement "
                "for the Forge table models."
            )
    return errors


def validate_data_driven_models() -> list[str]:
    errors: list[str] = []
    expected_item_model_markers = {
        "kitchen_shovel": {"minecraft:condition", "kaleidoscope_cookery:kitchen_shovel_has_oil"},
        "stockpot_lid": {"minecraft:condition", "minecraft:using_item"},
        "oil_pot": {
            "minecraft:condition",
            "kaleidoscope_cookery:oil_pot_oil_count",
            "kaleidoscope_cookery:oil_pot_count",
        },
        "raw_dough": {"minecraft:range_dispatch", "minecraft:use_duration"},
        "recipe_item": {"minecraft:condition", "kaleidoscope_cookery:recipe_record"},
        "transmutation_lunch_bag": {
            "minecraft:condition",
            "kaleidoscope_cookery:transmutation_lunch_bag_items",
        },
        "steamer": {"minecraft:condition", "minecraft:block_entity_data"},
        "honey": {"minecraft:model", "kaleidoscope_cookery:item/honey"},
        "egg": {"minecraft:model", "kaleidoscope_cookery:item/egg"},
        "oil_in_millstone": {"minecraft:model", "kaleidoscope_cookery:item/oil_in_millstone"},
    }
    for item_id, required_markers in expected_item_model_markers.items():
        path = ASSETS / "items" / f"{item_id}.json"
        if not path.exists():
            errors.append(f"Missing 26.2 data-driven item model: {path.relative_to(ROOT)}")
            continue
        markers = set(walk_strings(parse_json(path)))
        missing = required_markers - markers
        if missing:
            errors.append(f"Item model {item_id} is missing baseline replacements: {sorted(missing)}")

    render_type_models = []
    for path in (ASSETS / "models/block").rglob("*.json"):
        model = parse_json(path)
        if isinstance(model, dict) and "render_type" in model:
            render_type_models.append(path)
    if len(render_type_models) < 357:
        errors.append(
            f"Only {len(render_type_models)} block models declare render_type; Forge baseline has 357."
        )
    if (CLIENT_ROOT / "init/ModBlockRenderLayerMap.java").exists():
        errors.append("The obsolete no-op ModBlockRenderLayerMap placeholder still exists.")
    return errors


def validate_animation_clocks() -> list[str]:
    errors: list[str] = []
    for path in sorted((CLIENT_ROOT / "render").rglob("*.java")):
        if "System.currentTimeMillis()" in strip_comments(read(path)):
            errors.append(f"{path.relative_to(ROOT)} uses the system wall clock for rendering.")
    return errors


def validate_stable_render_seeds() -> list[str]:
    errors: list[str] = []
    millstone_renderer = read(CLIENT_ROOT / "render/block/MillstoneBlockEntityRender.java")
    if "state.randomSeed = millstone.getBlockPos().asLong();" not in millstone_renderer:
        errors.append("Millstone renderer does not derive item layout from the stable block position.")
    if "millstone.hashCode()" in millstone_renderer:
        errors.append("Millstone renderer still seeds item layout from object identity.")
    stockpot_renderer = read(CLIENT_ROOT / "render/block/StockpotBlockEntityRender.java")
    if "ItemStack.hashItemAndComponents(stack)" not in stockpot_renderer or "stack.hashCode()" in stockpot_renderer:
        errors.append("Stockpot renderer does not derive item layout from stable item components.")
    mob_soup_renderer = read(CLIENT_ROOT / "render/soupbase/MobSoupBaseRender.java")
    if "stockpot.getBlockPos().asLong()" not in mob_soup_renderer or "renderEntity.hashCode()" in mob_soup_renderer:
        errors.append("Mob soup renderer still derives animation phase from entity object identity.")
    return errors


def validate_entity_render_snapshots() -> list[str]:
    errors: list[str] = []
    scarecrow = read(JAVA_ROOT / "entity/ScarecrowEntity.java")
    renderer = read(CLIENT_ROOT / "render/entity/ScarecrowRender.java")
    if "public long lastHit" in scarecrow:
        errors.append("ScarecrowEntity still exposes mutable hit animation state.")
    if "scarecrow.getLastHitTime()" not in renderer or "scarecrow.lastHit" in renderer:
        errors.append("Scarecrow renderer does not read hit timing through the entity snapshot API.")
    return errors


def validate_textures() -> list[str]:
    errors: list[str] = []
    for path, texture in collect_mod_texture_refs():
        texture_path = ASSETS / texture
        if not texture_path.exists():
            errors.append(f"{path.relative_to(ROOT)} references missing texture {texture}.")

    for relative_path, expected_hash in REQUIRED_BASELINE_TEXTURE_HASHES.items():
        texture_path = ASSETS / relative_path
        if not texture_path.exists():
            errors.append(f"Missing Forge baseline texture: {texture_path.relative_to(ROOT)}")
            continue
        actual_hash = hashlib.sha256(texture_path.read_bytes()).hexdigest()
        if actual_hash != expected_hash:
            errors.append(
                f"Forge baseline texture changed: {texture_path.relative_to(ROOT)} "
                f"has SHA-256 {actual_hash}."
            )

    professions = set(re.findall(
        r'ResourceKey\.create\(\s*Registries\.VILLAGER_PROFESSION\s*,\s*id\("([a-z0-9_./-]+)"\)',
        read(JAVA_ROOT / "init/ModVillager.java"),
    ))
    for profession in sorted(professions):
        villager_texture = ASSETS / "textures" / "entity" / "villager" / "profession" / f"{profession}.png"
        zombie_texture = ASSETS / "textures" / "entity" / "zombie_villager" / "profession" / f"{profession}.png"
        if not villager_texture.exists():
            errors.append(f"Missing villager profession texture: {villager_texture.relative_to(ROOT)}")
        if not zombie_texture.exists():
            errors.append(f"Missing zombie villager profession texture: {zombie_texture.relative_to(ROOT)}")
    return errors


def validate_equipment_assets() -> list[str]:
    errors: list[str] = []
    equipment_assets = collect_equipment_assets()

    for asset_id in sorted(equipment_assets):
        asset_file = ASSETS / "equipment" / f"{asset_id}.json"
        if not asset_file.exists():
            errors.append(f"Equipment asset {asset_id} is missing {asset_file.relative_to(ROOT)}.")
            continue

        asset_json = parse_json(asset_file)
        layers = asset_json.get("layers") if isinstance(asset_json, dict) else None
        if not isinstance(layers, dict):
            errors.append(f"{asset_file.relative_to(ROOT)} must define an equipment layers object.")
            continue

        required_layers = REQUIRED_HUMANOID_EQUIPMENT_LAYERS.get(asset_id, set())
        missing_layers = required_layers - set(layers)
        if missing_layers:
            errors.append(
                f"{asset_file.relative_to(ROOT)} is missing equipment layers: {sorted(missing_layers)}"
            )

        for layer_type, entries in sorted(layers.items()):
            if not isinstance(entries, list) or not entries:
                errors.append(f"{asset_file.relative_to(ROOT)} layer {layer_type} must be a non-empty array.")
                continue
            for index, entry in enumerate(entries):
                if not isinstance(entry, dict):
                    errors.append(
                        f"{asset_file.relative_to(ROOT)} layer {layer_type}[{index}] must be an object."
                    )
                    continue
                texture = entry.get("texture")
                if not isinstance(texture, str) or not texture:
                    errors.append(
                        f"{asset_file.relative_to(ROOT)} layer {layer_type}[{index}] has no texture."
                    )
                    continue
                namespace, texture_name = texture.split(":", 1) if ":" in texture else ("minecraft", texture)
                if namespace != MOD_ID:
                    continue
                texture_path = (
                    RESOURCES
                    / "assets"
                    / namespace
                    / "textures"
                    / "entity"
                    / "equipment"
                    / layer_type
                    / f"{texture_name}.png"
                )
                if not texture_path.exists():
                    errors.append(
                        f"{asset_file.relative_to(ROOT)} references missing equipment texture "
                        f"{texture_path.relative_to(ROOT)}."
                    )
    return errors


def validate_particles() -> list[str]:
    errors: list[str] = []
    mod_particles = strip_comments(read(JAVA_ROOT / "init/ModParticles.java"))
    particles = collect_string_calls(JAVA_ROOT / "init/ModParticles.java", "register")
    factory_text = strip_comments(read(CLIENT_ROOT / "init/ModParticleFactoryRegistry.java"))
    factory_consts = set(re.findall(r"ParticleProviderRegistry\.getInstance\(\)\.register\(\s*ModParticles\.(\w+)", factory_text))

    if "class ModParticleType" in mod_particles:
        errors.append("ModParticles still reimplements FabricParticleTypes.complex.")
    if not re.search(
        r"FabricParticleTypes\.complex\(\s*false\s*,\s*StockpotParticleOptions\.CODEC\s*,\s*"
        r"StockpotParticleOptions\.STREAM_CODEC\s*\)",
        mod_particles,
    ):
        errors.append("Stockpot particle type does not use the Fabric complex particle factory.")

    missing_factories = set(particles) - factory_consts
    extra_factories = factory_consts - set(particles)
    if missing_factories:
        errors.append(f"Particles registered without client factories: {sorted(missing_factories)}")
    if extra_factories:
        errors.append(f"Particle factories reference unknown particle constants: {sorted(extra_factories)}")

    for const, particle_id in sorted(particles.items()):
        particle_file = ASSETS / "particles" / f"{particle_id}.json"
        if not particle_file.exists():
            errors.append(f"Particle {const} is missing {particle_file.relative_to(ROOT)}.")
            continue
        particle_json = parse_json(particle_file)
        for value in walk_strings(particle_json):
            if ":" not in value:
                continue
            namespace, path = value.split(":", 1)
            if namespace == MOD_ID:
                texture_path = ASSETS / "textures" / "particle" / f"{path}.png"
                if not texture_path.exists():
                    errors.append(f"{particle_file.relative_to(ROOT)} references missing particle texture {value}.")
    return errors


def validate_sounds() -> list[str]:
    errors: list[str] = []
    sounds = collect_string_calls(JAVA_ROOT / "init/ModSounds.java", "register")
    sounds_json = parse_json(ASSETS / "sounds.json")
    sound_ids = set(sounds.values())
    sound_json_ids = set(sounds_json)
    missing_json = sound_ids - sound_json_ids
    extra_json = sound_json_ids - sound_ids
    if missing_json:
        errors.append(f"Sound events registered without sounds.json entries: {sorted(missing_json)}")
    if extra_json:
        errors.append(f"sounds.json entries are not registered as sound events: {sorted(extra_json)}")

    lang_en = parse_json(ASSETS / "lang/en_us.json")
    lang_zh = parse_json(ASSETS / "lang/zh_cn.json")
    for sound_id, sound_data in sorted(sounds_json.items()):
        if not isinstance(sound_data, dict):
            errors.append(f"sounds.json entry {sound_id} must be an object.")
            continue
        subtitle = sound_data.get("subtitle")
        if isinstance(subtitle, str):
            if subtitle not in lang_en:
                errors.append(f"sounds.json subtitle missing in en_us: {subtitle}")
            if subtitle not in lang_zh:
                errors.append(f"sounds.json subtitle missing in zh_cn: {subtitle}")
        entries = sound_data.get("sounds")
        if not isinstance(entries, list) or not entries:
            errors.append(f"sounds.json entry {sound_id} has no sounds array.")
            continue
        for entry in entries:
            sound_ref = entry.get("name") if isinstance(entry, dict) else entry
            if not isinstance(sound_ref, str) or ":" not in sound_ref:
                continue
            namespace, sound_path = sound_ref.split(":", 1)
            if namespace == MOD_ID:
                ogg = ASSETS / "sounds" / f"{sound_path}.ogg"
                if not ogg.exists():
                    errors.append(f"sounds.json entry {sound_id} references missing sound {sound_ref}.")
    return errors


def collect_item_tag_translation_keys() -> set[str]:
    keys: set[str] = set()
    for path in iter_resource_files("data", pattern="*.json"):
        relative = resource_relative(path)
        parts = relative.parts
        if len(parts) < 5 or parts[2:4] != ("tags", "item"):
            continue

        namespace = parts[1]
        if namespace == "minecraft":
            continue

        tag_path = Path(*parts[4:]).with_suffix("").as_posix().replace("/", ".")
        keys.add(f"tag.item.{namespace}.{tag_path}")
    return keys


def validate_item_tag_translations() -> list[str]:
    lang_en = parse_json(ASSETS / "lang/en_us.json")
    missing = collect_item_tag_translation_keys() - set(lang_en)
    if missing:
        return [f"Item tags missing en_us translations: {sorted(missing)}"]
    return []


def collect_jade_config_translation_keys() -> set[str]:
    plugin = read(JAVA_ROOT / "compat/jade/ModPlugin.java")
    provider_ids = set(re.findall(
        r'public static final Identifier \w+\s*=\s*Identifier\.fromNamespaceAndPath\('
        r'KaleidoscopeCookery\.MOD_ID,\s*"([a-z0-9_./-]+)"\)',
        plugin,
    ))
    return {f"config.jade.plugin_{MOD_ID}.{provider_id}" for provider_id in provider_ids}


def validate_jade_config_translations() -> list[str]:
    required = collect_jade_config_translation_keys()
    errors: list[str] = []
    for language in ("en_us", "zh_cn"):
        translations = parse_json(ASSETS / "lang" / f"{language}.json")
        missing = required - set(translations)
        if missing:
            errors.append(f"Jade provider config keys missing in {language}: {sorted(missing)}")
    return errors


def method_body(text: str, signature: str) -> str:
    start = text.find(signature)
    if start < 0:
        return ""
    brace = text.find("{", start)
    depth = 0
    for index in range(brace, len(text)):
        if text[index] == "{":
            depth += 1
        elif text[index] == "}":
            depth -= 1
            if depth == 0:
                return text[brace + 1:index]
    return ""


def validate_creative_tabs() -> list[str]:
    errors: list[str] = []
    text = read(JAVA_ROOT / "init/ModCreativeTabs.java")
    register_body = method_body(text, "public static void registerTabs()")
    main_body = method_body(text, "private static void addMainTabItems")
    food_body = method_body(text, "private static void addFoodTabItems")
    dynamic_food_body = method_body(text, "private static void addFoodBiteItems")

    if register_body.find("COOKERY_FOOD_TAB") > register_body.find("COOKERY_MAIN_TAB"):
        errors.append("Creative tabs no longer place the food tab before the main tab as Forge did.")
    if "ModItems.EMPTY_CUP" in main_body:
        errors.append("Empty cup leaked into the main creative tab instead of the food tab tea section.")

    tool_order = [
        "ModItems.KITCHEN_SHOVEL", "ModItems.SICKLE", "ModItems.GOLD_KITCHEN_KNIFE",
        "ModItems.IRON_KITCHEN_KNIFE", "ModItems.DIAMOND_KITCHEN_KNIFE",
        "ModItems.NETHERITE_KITCHEN_KNIFE",
    ]
    positions = [main_body.find(value) for value in tool_order]
    if any(position < 0 for position in positions) or positions != sorted(positions):
        errors.append("Main creative tab does not preserve the Forge tool order.")

    tea_order = [
        "addFoodBiteItems(output)", "PlateRegistry.ids()", "ModItems.EMPTY_CUP", "TeacupRegistry.ids()",
    ]
    positions = [food_body.find(value) for value in tea_order]
    if any(position < 0 for position in positions) or positions != sorted(positions):
        errors.append("Food creative tab does not preserve food, plate, empty cup, and tea ordering.")
    if (
        "itemId.equals(FoodBiteRegistry.DOUGH_DROP_SOUP)" not in dynamic_food_body
        or dynamic_food_body.find("ModItems.COLD_CUT_HAM_SLICES")
        > dynamic_food_body.find("output.accept(requiredItem(itemId))")
    ):
        errors.append("Cold-cut ham is not inserted immediately before dough-drop soup.")
    return errors


def main() -> int:
    errors: list[str] = []
    errors.extend(validate_renderers())
    errors.extend(validate_model_layers())
    errors.extend(validate_resource_reloaders())
    errors.extend(validate_client_initialization())
    errors.extend(validate_legacy_models())
    errors.extend(validate_baseline_model_migrations())
    errors.extend(validate_data_driven_models())
    errors.extend(validate_animation_clocks())
    errors.extend(validate_stable_render_seeds())
    errors.extend(validate_entity_render_snapshots())
    errors.extend(validate_textures())
    errors.extend(validate_equipment_assets())
    errors.extend(validate_particles())
    errors.extend(validate_sounds())
    errors.extend(validate_item_tag_translations())
    errors.extend(validate_jade_config_translations())
    errors.extend(validate_creative_tabs())

    if errors:
        print("Client asset verification failed:")
        print("\n".join(errors))
        return 1

    entity_count = len(collect_registered_entities())
    block_entity_render_count = len(re.findall(
        r"BlockEntityRenderers\.register\(",
        read(CLIENT_ROOT / "init/ClientRegistry.java"),
    ))
    model_layers, baked_layers = collect_model_layers()
    particles = collect_string_calls(JAVA_ROOT / "init/ModParticles.java", "register")
    sounds = collect_string_calls(JAVA_ROOT / "init/ModSounds.java", "register")
    equipment_assets = collect_equipment_assets()
    item_tag_translations = collect_item_tag_translation_keys()
    jade_config_translations = collect_jade_config_translation_keys()
    legacy_asset_count = sum(path.is_file() for path in (LEGACY_PACK / "assets").rglob("*"))
    legacy_model_count = sum(path.is_file() for path in (LEGACY_ASSETS / "models").rglob("*.json"))
    render_type_count = sum(
        isinstance(parse_json(path), dict) and "render_type" in parse_json(path)
        for path in (ASSETS / "models/block").rglob("*.json")
    )

    print("Client asset verification passed.")
    print(f"  entity renderers: {entity_count}")
    print(f"  block entity renderers: {block_entity_render_count}")
    print(f"  model layers registered: {len(model_layers)}")
    print(f"  model layers baked: {len(baked_layers)}")
    print(f"  equipment assets: {len(equipment_assets)}")
    print(f"  particles: {len(particles)}")
    print(f"  sound events: {len(sounds)}")
    print(f"  translated item tags: {len(item_tag_translations)}")
    print(f"  translated Jade provider configs: {len(jade_config_translations)}")
    print(f"  legacy resource-pack assets: {legacy_asset_count}")
    print(f"  legacy resource-pack models: {legacy_model_count}")
    print(f"  data-driven block render types: {render_type_count}")
    print(f"  lowered baseline crop models: {len(BASELINE_CUSTOM_CROPS) * 8}")
    print(f"  table blockstate variants: {len(TABLE_WOOD_TYPES) * 32}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
