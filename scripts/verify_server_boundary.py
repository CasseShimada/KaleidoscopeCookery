#!/usr/bin/env python3
"""Check that common/server code does not import client-only classes."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery"
MIXINS = ROOT / "src/main/resources/kaleidoscope_cookery.mixins.json"
MOD_EVENTS = SRC / "init/ModEvents.java"

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

INSTANT_SMELTING_EVENT = SRC / "event/server/effect/InstantSmeltingEffectEvent.java"
LEGACY_INSTANT_SMELTING_MIXIN = SRC / "mixin/BlockMixin.java"
HINDER_EFFECT_EVENT = SRC / "event/server/effect/HinderEffectEvent.java"
VITALITY_EFFECT_EVENT = SRC / "event/server/effect/VitalityEffectEvent.java"
PROJECTILE_DODGE_HANDLER = SRC / "event/server/effect/ProjectileDodgeHandler.java"
PROJECTILE_MIXIN = SRC / "mixin/ProjectileMixin.java"
LEGACY_NEW_EFFECT_EVENTS = SRC / "event/server/effect/NewEffectEvents.java"
WET_FIELD_HOE_EVENT = SRC / "event/interaction/WetFieldHoeUseEvent.java"
CATERPILLAR_CHICKEN_FEED_EVENT = SRC / "event/interaction/CaterpillarChickenFeedEvent.java"
FRUIT_BASKET_BLOCK = SRC / "block/decoration/FruitBasketBlock.java"
SICKLE_NETHER_WART_EVENT = SRC / "event/server/SickleHarvestNetherWartEvent.java"
SICKLE_ITEM = SRC / "item/SickleItem.java"
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
    "compat/jade/",
    "compat/rei/",
    "api/client/",
)

CLIENT_ONLY_PATTERNS = (
    re.compile(r"^\s*import\s+net\.minecraft\.client\.", re.MULTILINE),
    re.compile(r"^\s*import\s+com\.github\.ysbbbbbb\.kaleidoscopecookery\.client\.", re.MULTILINE),
    re.compile(r"^\s*import\s+com\.github\.ysbbbbbb\.kaleidoscopecookery\.api\.client\.", re.MULTILINE),
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

    for path in sorted(SRC.rglob("*.java")):
        if is_client_only_path(path):
            continue
        text = path.read_text(encoding="utf-8")
        for pattern in CLIENT_ONLY_PATTERNS:
            if pattern.search(text):
                errors.append(f"{path.relative_to(ROOT)} contains client-only reference: {pattern.pattern}")

    mixin_data = json.loads(MIXINS.read_text(encoding="utf-8"))
    for mixin in mixin_data.get("mixins", []):
        if mixin.startswith("client."):
            errors.append(f"Client mixin listed in common mixins section: {mixin}")
    for mixin in mixin_data.get("client", []):
        if not mixin.startswith("client."):
            errors.append(f"Non-client mixin listed in client mixins section: {mixin}")

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

    if SICKLE_NETHER_WART_EVENT.exists():
        sickle_nether_wart_text = SICKLE_NETHER_WART_EVENT.read_text(encoding="utf-8")
        for required_reference in (
            "if (!serverPlayer.gameMode.destroyBlock(pos))",
            "level.getBlockState(pos).isAir()",
            "Blocks.NETHER_WART.defaultBlockState()",
            "event.setCostDurability(true)",
            "event.setCanceled(true)",
        ):
            if required_reference not in sickle_nether_wart_text:
                errors.append(f"Sickle nether wart event is missing {required_reference}.")
        for duplicate_side_effect in ("LevelEvent.PARTICLES_DESTROY_BLOCK",):
            if duplicate_side_effect in sickle_nether_wart_text:
                errors.append(f"Sickle nether wart event retains duplicate side effect: {duplicate_side_effect}.")

    sickle_item_text = SICKLE_ITEM.read_text(encoding="utf-8")
    if re.search(
        r"public\s+boolean\s+canDestroyBlock\s*\([^)]*LivingEntity\s+\w+\s*\)\s*\{\s*return\s+true\s*;\s*\}",
        sickle_item_text,
        re.DOTALL,
    ) is None:
        errors.append("SickleItem does not preserve creative-mode block destruction.")
    for required_reference in (
        "!player.hasInfiniteMaterials()",
        "breakCount > 0",
    ):
        if required_reference not in sickle_item_text:
            errors.append(f"SickleItem durability handling is missing {required_reference}.")

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
    print(f"  common mixins: {len(mixin_data.get('mixins', []))}")
    print(f"  client mixins: {len(mixin_data.get('client', []))}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
