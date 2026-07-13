#!/usr/bin/env python3
"""Verify client-side renderer, model-layer, particle, and sound wiring."""

from __future__ import annotations

import json
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

OPTIONAL_BLOCK_ENTITY_RENDERERS = {
    "OIL_POT_BE",
}

REQUIRED_HUMANOID_EQUIPMENT_LAYERS = {
    "cookery_farmer": {"humanoid", "humanoid_leggings"},
}


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
    for path in sorted(CLIENT_ROOT.rglob("*.java")):
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
        "ModModelLoading",
        "ModClientTooltip",
        "ModEntitiesRender",
        "ModParticleFactoryRegistry",
        "ModBlockRenderLayerMap",
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
    return errors


def validate_textures() -> list[str]:
    errors: list[str] = []
    for path, texture in collect_mod_texture_refs():
        texture_path = ASSETS / texture
        if not texture_path.exists():
            errors.append(f"{path.relative_to(ROOT)} references missing texture {texture}.")

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


def main() -> int:
    errors: list[str] = []
    errors.extend(validate_renderers())
    errors.extend(validate_model_layers())
    errors.extend(validate_resource_reloaders())
    errors.extend(validate_animation_clocks())
    errors.extend(validate_stable_render_seeds())
    errors.extend(validate_textures())
    errors.extend(validate_equipment_assets())
    errors.extend(validate_particles())
    errors.extend(validate_sounds())
    errors.extend(validate_item_tag_translations())

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

    print("Client asset verification passed.")
    print(f"  entity renderers: {entity_count}")
    print(f"  block entity renderers: {block_entity_render_count}")
    print(f"  model layers registered: {len(model_layers)}")
    print(f"  model layers baked: {len(baked_layers)}")
    print(f"  equipment assets: {len(equipment_assets)}")
    print(f"  particles: {len(particles)}")
    print(f"  sound events: {len(sounds)}")
    print(f"  translated item tags: {len(item_tag_translations)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
