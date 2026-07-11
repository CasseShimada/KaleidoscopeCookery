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
    "VitalityEffectEvent": SRC / "event/server/effect/VitalityEffectEvent.java",
}

LEGACY_SERVER_EVENT_PATHS = (
    SRC / "event/ArmorEffectHandler.java",
    SRC / "event/effect/FlatulenceServerEvent.java",
    SRC / "event/effect/HinderEvent.java",
    SRC / "event/effect/PreservationEvent.java",
    SRC / "event/effect/SatiatedShieldEvent.java",
    SRC / "event/effect/VitalityEvent.java",
)

INSTANT_SMELTING_EVENT = SRC / "event/server/effect/InstantSmeltingEffectEvent.java"
LEGACY_INSTANT_SMELTING_MIXIN = SRC / "mixin/BlockMixin.java"
HINDER_EFFECT_EVENT = SRC / "event/server/effect/HinderEffectEvent.java"
VITALITY_EFFECT_EVENT = SRC / "event/server/effect/VitalityEffectEvent.java"
PROJECTILE_DODGE_HANDLER = SRC / "event/server/effect/ProjectileDodgeHandler.java"
PROJECTILE_MIXIN = SRC / "mixin/ProjectileMixin.java"
LEGACY_NEW_EFFECT_EVENTS = SRC / "event/server/effect/NewEffectEvents.java"

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
    for event_class, path in SERVER_EVENT_REGISTRATIONS.items():
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

    if errors:
        print("Server boundary verification failed:")
        print("\n".join(errors))
        return 1

    print("Server boundary verification passed.")
    print("  common/server sources contain no direct client-only imports")
    print(f"  migrated server events: {len(SERVER_EVENT_REGISTRATIONS)}")
    print(f"  legacy server event paths checked: {len(LEGACY_SERVER_EVENT_PATHS)}")
    print(f"  common mixins: {len(mixin_data.get('mixins', []))}")
    print(f"  client mixins: {len(mixin_data.get('client', []))}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
