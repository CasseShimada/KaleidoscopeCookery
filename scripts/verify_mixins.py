#!/usr/bin/env python3
"""Verify Mixin and access widener wiring."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery"
CLIENT_JAVA_ROOT = ROOT / "src/client/java/com/github/ysbbbbbb/kaleidoscopecookery"
MIXIN_ROOT = JAVA_ROOT / "mixin"
CLIENT_MIXIN_ROOT = CLIENT_JAVA_ROOT / "mixin"
RESOURCES = ROOT / "src/main/resources"
CLIENT_RESOURCES = ROOT / "src/client/resources"
MIXINS_JSON = RESOURCES / "kaleidoscope_cookery.mixins.json"
CLIENT_MIXINS_JSON = CLIENT_RESOURCES / "kaleidoscope_cookery.client.mixins.json"
ACCESS_WIDENER = RESOURCES / "kaleidoscope_cookery.accesswidener"
FABRIC_MOD_JSON = RESOURCES / "fabric.mod.json"
BUILD_GRADLE = ROOT / "build.gradle"
ADD_VILLAGE_STRUCTURES_EVENT = JAVA_ROOT / "event/server/AddVillageStructuresEvent.java"
FLOUR_ITEM = JAVA_ROOT / "item/FlourItem.java"
TUNDRA_STRIDER_EFFECT = JAVA_ROOT / "effect/TundraStriderEffect.java"
PROJECTILE_DODGE_HANDLER = JAVA_ROOT / "event/server/effect/ProjectileDodgeHandler.java"
CUSTOM_ARM_POSE = CLIENT_JAVA_ROOT / "client/animation/CustomArmPose.java"
PRESERVATION_EVENT = JAVA_ROOT / "event/server/effect/PreservationEvent.java"
FARMER_ARMOR_EVENT = JAVA_ROOT / "event/server/effect/FarmerArmorEffectEvent.java"
TRASH_CAN_TARGETING = JAVA_ROOT / "util/TrashCanTargeting.java"
TRASH_CAN_RENDER_STATE = CLIENT_JAVA_ROOT / "client/render/TrashCanRenderState.java"
MOD_EVENTS = JAVA_ROOT / "init/ModEvents.java"
PLAYER_MIXIN = MIXIN_ROOT / "PlayerMixin.java"
GIVE_GIFT_TO_HERO_MIXIN = MIXIN_ROOT / "GiveGiftToHeroMixin.java"
LEGACY_PLAYER_DATA_COMPAT = JAVA_ROOT / "util/LegacyPlayerDataCompat.java"
FLATULENCE_EFFECT = JAVA_ROOT / "effect/FlatulenceEffect.java"

MIXIN_PACKAGE = "com.github.ysbbbbbb.kaleidoscopecookery.mixin"
MIXIN_CONFIG_NAME = "kaleidoscope_cookery.mixins.json"
CLIENT_MIXIN_CONFIG_NAME = "kaleidoscope_cookery.client.mixins.json"
ACCESS_WIDENER_NAME = "kaleidoscope_cookery.accesswidener"

CLIENT_ONLY_PATTERNS = (
    re.compile(r"^\s*import\s+net\.minecraft\.client\.", re.MULTILINE),
    re.compile(r"^\s*import\s+com\.github\.ysbbbbbb\.kaleidoscopecookery\.client\.", re.MULTILINE),
    re.compile(r"^\s*import\s+com\.github\.ysbbbbbb\.kaleidoscopecookery\.api\.client\.", re.MULTILINE),
    re.compile(r"\bEnvType\.CLIENT\b"),
    re.compile(r"@Environment\s*\(\s*EnvType\.CLIENT\s*\)"),
)

EXPECTED_ACCESS_WIDENER_ENTRIES = {
    (
        "accessible",
        "field",
        "net/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool",
        "templates",
        "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
    ),
    (
        "accessible",
        "field",
        "net/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool",
        "rawTemplates",
        "Ljava/util/List;",
    ),
    (
        "mutable",
        "field",
        "net/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool",
        "rawTemplates",
        "Ljava/util/List;",
    ),
}


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def strip_comments(text: str) -> str:
    text = re.sub(r"/\*.*?\*/", "", text, flags=re.DOTALL)
    return re.sub(r"//.*", "", text)


def parse_json(path: Path) -> Any:
    with path.open("r", encoding="utf-8-sig") as handle:
        return json.load(handle)


def parse_target_java_version() -> int | None:
    match = re.search(r"\btargetJavaVersion\s*=\s*(\d+)\b", read(BUILD_GRADLE))
    if match is None:
        return None
    return int(match.group(1))


def java_package(path: Path) -> str | None:
    match = re.search(r"^\s*package\s+([a-zA-Z0-9_.]+)\s*;", read(path), flags=re.MULTILINE)
    return match.group(1) if match else None


def mixin_name_from_path(path: Path, root: Path) -> str:
    return path.relative_to(root).with_suffix("").as_posix().replace("/", ".")


def mixin_path_from_name(name: str) -> Path:
    if name.startswith("client."):
        return CLIENT_MIXIN_ROOT / Path(*name.split(".")).with_suffix(".java")
    return MIXIN_ROOT / Path(*name.split(".")).with_suffix(".java")


def expected_package_for_mixin(name: str) -> str:
    parts = name.split(".")[:-1]
    if not parts:
        return MIXIN_PACKAGE
    return f"{MIXIN_PACKAGE}.{'.'.join(parts)}"


def parse_mixin_list(
        data: dict[str, Any], section: str, config_name: str, errors: list[str]
) -> list[str]:
    value = data.get(section, [])
    if not isinstance(value, list):
        errors.append(f"{config_name} section {section!r} must be a list.")
        return []

    mixins: list[str] = []
    for entry in value:
        if not isinstance(entry, str):
            errors.append(f"{config_name} section {section!r} contains non-string entry {entry!r}.")
            continue
        if not re.fullmatch(r"[A-Za-z_$][A-Za-z0-9_$]*(?:\.[A-Za-z_$][A-Za-z0-9_$]*)*", entry):
            errors.append(f"{config_name} has malformed mixin class name: {entry!r}.")
            continue
        mixins.append(entry)
    return mixins


def validate_mixin_config() -> tuple[list[str], int, int]:
    errors: list[str] = []
    data = parse_json(MIXINS_JSON)
    client_data = parse_json(CLIENT_MIXINS_JSON)

    for config_name, config_data in (
            (MIXIN_CONFIG_NAME, data),
            (CLIENT_MIXIN_CONFIG_NAME, client_data),
    ):
        if config_data.get("required") is not True:
            errors.append(f"{config_name} should keep required=true so remap drift fails fast.")
        if config_data.get("minVersion") != "0.8":
            errors.append(f"{config_name} should keep minVersion=\"0.8\".")
        if config_data.get("package") != MIXIN_PACKAGE:
            errors.append(f"{config_name} package should be {MIXIN_PACKAGE}.")
        if config_data.get("injectors", {}).get("defaultRequire") != 1:
            errors.append(f"{config_name} should keep injectors.defaultRequire=1.")

    target_java = parse_target_java_version()
    if target_java is None:
        errors.append("build.gradle does not declare targetJavaVersion.")
    else:
        expected_compatibility = f"JAVA_{target_java}"
        for config_name, config_data in (
                (MIXIN_CONFIG_NAME, data),
                (CLIENT_MIXIN_CONFIG_NAME, client_data),
        ):
            if config_data.get("compatibilityLevel") != expected_compatibility:
                errors.append(
                    f"{config_name} compatibilityLevel should be {expected_compatibility} "
                    f"to match build.gradle targetJavaVersion={target_java}."
                )

    fabric_mod = parse_json(FABRIC_MOD_JSON)
    mixin_configs = fabric_mod.get("mixins", [])
    if MIXIN_CONFIG_NAME not in mixin_configs:
        errors.append(f"fabric.mod.json does not reference {MIXIN_CONFIG_NAME}.")
    client_config_entries = [
        entry for entry in mixin_configs
        if isinstance(entry, dict) and entry.get("config") == CLIENT_MIXIN_CONFIG_NAME
    ]
    if client_config_entries != [{"config": CLIENT_MIXIN_CONFIG_NAME, "environment": "client"}]:
        errors.append(
            f"fabric.mod.json should reference {CLIENT_MIXIN_CONFIG_NAME} only in the client environment."
        )

    common_mixins = parse_mixin_list(data, "mixins", MIXIN_CONFIG_NAME, errors)
    client_mixins = parse_mixin_list(client_data, "client", CLIENT_MIXIN_CONFIG_NAME, errors)
    if data.get("client"):
        errors.append(f"{MIXIN_CONFIG_NAME} still contains client mixins.")
    if client_data.get("mixins"):
        errors.append(f"{CLIENT_MIXIN_CONFIG_NAME} contains common mixins.")
    listed_mixins = common_mixins + client_mixins

    duplicates = sorted({name for name in listed_mixins if listed_mixins.count(name) > 1})
    if duplicates:
        errors.append(f"Mixin configurations contain duplicate entries: {duplicates}")

    for name in common_mixins:
        if name.startswith("client."):
            errors.append(f"Client mixin listed in common mixins section: {name}")
    for name in client_mixins:
        if not name.startswith("client."):
            errors.append(f"Non-client mixin listed in client mixins section: {name}")

    source_mixins = {
        mixin_name_from_path(path, MIXIN_ROOT): path
        for path in sorted(MIXIN_ROOT.rglob("*.java"))
    }
    source_mixins.update({
        mixin_name_from_path(path, CLIENT_MIXIN_ROOT): path
        for path in sorted(CLIENT_MIXIN_ROOT.rglob("*.java"))
    })
    listed_set = set(listed_mixins)
    missing_files = sorted(listed_set - set(source_mixins))
    unlisted_files = sorted(set(source_mixins) - listed_set)
    if missing_files:
        errors.append(f"Mixin configurations reference missing mixin classes: {missing_files}")
    if unlisted_files:
        errors.append(f"Mixin source files are not listed in a Mixin config: {unlisted_files}")

    for name in sorted(listed_set & set(source_mixins)):
        path = mixin_path_from_name(name)
        text = read(path)
        package = java_package(path)
        expected_package = expected_package_for_mixin(name)
        if package != expected_package:
            errors.append(f"{path.relative_to(ROOT)} package should be {expected_package}, found {package}.")
        if "@Mixin" not in text:
            errors.append(f"{path.relative_to(ROOT)} is listed as a mixin but has no @Mixin annotation.")
        if "@Overwrite" in text:
            errors.append(f"{path.relative_to(ROOT)} uses @Overwrite; prefer an explicit review before release.")

        if name in common_mixins:
            for pattern in CLIENT_ONLY_PATTERNS:
                if pattern.search(text):
                    errors.append(f"{path.relative_to(ROOT)} common mixin contains client-only reference: {pattern.pattern}")
        if name in client_mixins and not path.relative_to(CLIENT_MIXIN_ROOT).as_posix().startswith("client/"):
            errors.append(f"{path.relative_to(ROOT)} is a client mixin but is not under mixin/client/.")
        if name == "MobBucketItemMixin":
            if "BlockPos.betweenClosed(" not in text:
                errors.append("MobBucketItemMixin does not use a fixed vanilla block scan.")
            if "mutable.offset(" in text:
                errors.append("MobBucketItemMixin retains a cumulatively mutated scan position.")
            if "wrapAsHolder(this.type).is(TagMod.RICE_GROWTH_BOOSTER)" not in text:
                errors.append("MobBucketItemMixin no longer filters bucket entities through rice_growth_booster.")
        if name == "PistonStructureResolverMixin":
            if '@Inject(method = "isSticky"' in text or "setReturnValue(true)" in text:
                errors.append("PistonStructureResolverMixin incorrectly makes the Forge-nonsticky oil block sticky.")
            if text.count("is(ModBlocks.OIL_BLOCK)") < 2 or "setReturnValue(false)" not in text:
                errors.append("PistonStructureResolverMixin does not reject oil-block adhesion symmetrically.")
        if name == "FoodDataAccessor":
            if "@Mixin(FoodData.class)" not in text or '@Accessor("exhaustionLevel")' not in text:
                errors.append("FoodDataAccessor does not expose only the exhaustion level required by Vigor.")
        if name == "FarmBlockMixin" and "entity != null" not in text:
            errors.append("FarmBlockMixin also cancels entity=null farmland dehydration, unlike Forge trample events.")
        if name == "ItemEntityMixin":
            if '@Inject(method = "tick", at = @At("TAIL"))' not in text:
                errors.append("ItemEntityMixin no longer checks flour after the vanilla item tick.")
            if "this.tickCount % 10 == 0" not in text or "FlourItem.hydrateIfInWater" not in text:
                errors.append("ItemEntityMixin no longer preserves the Forge ten-tick flour hydration cadence.")
        if name == "FallingBlockEntityMixin":
            if "FallingBlockEntity;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;" not in text:
                errors.append("FallingBlockEntityMixin no longer targets the 26.2 server-side falling-block drop call.")
            if "cancellable = true" not in text or "ci.cancel()" not in text:
                errors.append("FallingBlockEntityMixin does not replace the vanilla steamer item drop.")
            if "self.discard()" not in text:
                errors.append("FallingBlockEntityMixin leaks the entity when cancelling the 26.2 timeout drop path.")
            if "dropFallingSteamerAsItem" not in text or "self.blockData" not in text:
                errors.append("FallingBlockEntityMixin no longer preserves falling steamer block-entity data.")
        if name == "LivingEntityMixin":
            if '@Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)' not in text:
                errors.append("LivingEntityMixin no longer intercepts the tundra-strider speed factor at method entry.")
            if "TundraStriderEffect.getBlockSpeedFactor(entity).ifPresent(cir::setReturnValue)" not in text:
                errors.append("LivingEntityMixin no longer delegates the baseline tundra-strider speed formula.")
            for required in (
                    '@Inject(method = "completeUsingItem", at = @At("HEAD"))',
                    "getUseItem().copy()",
                    "LivingEntity;stopUsingItem()V",
                    "PreservationEvent.onItemUseFinished",
                    '@Inject(method = "tick", at = @At("TAIL"))',
                    "FarmerArmorEffectEvent.onLivingTick",
            ):
                if required not in text:
                    errors.append(f"LivingEntityMixin lost an entity-event contract: {required}")
        if name == "MobMixin":
            if '@Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)' not in text:
                errors.append("MobMixin no longer intercepts target assignment before vanilla mutation.")
            if "TrashCanTargeting.isHidingInTrashCan(target)" not in text or "ci.cancel()" not in text:
                errors.append("MobMixin no longer rejects players riding trash-can seats.")
        if name == "PowderSnowBlockMixin":
            if '@Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)' not in text:
                errors.append("PowderSnowBlockMixin no longer intercepts the vanilla powder-snow walk check.")
            if "TundraStriderEffect.canWalkOnPowderSnow(entity)" not in text or "cir.setReturnValue(true)" not in text:
                errors.append("PowderSnowBlockMixin no longer grants the tundra-strider powder-snow exception.")
        if name == "ProjectileMixin":
            if "hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)" not in text:
                errors.append("ProjectileMixin no longer targets the 26.2 projectile impact method.")
            if "EntityHitResult" not in text or "ProjectileDodgeHandler.dodgeProjectile(living)" not in text:
                errors.append("ProjectileMixin no longer limits dodge handling to living entity impacts.")
            if "cir.setReturnValue(ProjectileDeflection.NONE)" not in text:
                errors.append("ProjectileMixin no longer skips the original entity impact after a successful dodge.")

    flour_text = read(FLOUR_ITEM)
    if "itemEntity.isInWater()" not in flour_text:
        errors.append("FlourItem no longer requires the dropped stack to be in water.")
    if "new ItemStack(ModItems.RAW_DOUGH, stack.getCount())" not in flour_text:
        errors.append("FlourItem no longer preserves stack count when hydrating into raw dough.")

    tundra_text = read(TUNDRA_STRIDER_EFFECT)
    for required in (
            "entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.TUNDRA_STRIDER)",
            "entity.getBlockPosBelowThatAffectsMyMovement()",
            "blockState.is(TagMod.TUNDRA_STRIDER_SPEED_BLOCKS)",
            "1.1f + Math.max(1 - friction, 0) * 0.5f",
    ):
        if required not in tundra_text:
            errors.append(f"TundraStriderEffect lost baseline behavior: {required}")

    dodge_text = read(PROJECTILE_DODGE_HANDLER)
    for required in (
            "DODGE_DURATION_COST = 200",
            "TELEPORT_RANGE = 3.0",
            "TELEPORT_ATTEMPTS = 16",
            "effect.isInfiniteDuration()",
            "effect.getAmplifier()",
            "effect.isAmbient()",
            "effect.isVisible()",
            "effect.showIcon()",
    ):
        if required not in dodge_text:
            errors.append(f"ProjectileDodgeHandler lost baseline behavior: {required}")

    arm_mixin_text = read(CLIENT_MIXIN_ROOT / "client/HumanoidModelMixin.java")
    if arm_mixin_text.count('at = @At("TAIL")') != 2:
        errors.append("HumanoidModelMixin should apply both lift poses after vanilla arm posing.")
    for required in (
            "state.rightHandItemStack.getItem() instanceof LiftBlockItem",
            "CustomArmPose.applyLiftPose(this.rightArm, HumanoidArm.RIGHT)",
            "state.leftHandItemStack.getItem() instanceof LiftBlockItem",
            "CustomArmPose.applyLiftPose(this.leftArm, HumanoidArm.LEFT)",
    ):
        if required not in arm_mixin_text:
            errors.append(f"HumanoidModelMixin lost lift-pose behavior: {required}")
    arm_pose_text = read(CUSTOM_ARM_POSE)
    for required in (
            "arm.xRot = -Mth.PI;",
            "arm.zRot = -Mth.PI * 0.025f;",
            "arm.xRot = -Mth.PI * 0.5f;",
            "arm.zRot = Mth.PI * 0.025f;",
    ):
        if required not in arm_pose_text:
            errors.append(f"CustomArmPose lost a Forge lift angle: {required}")

    preservation_text = read(PRESERVATION_EVENT)
    for required in (
            "stack.has(DataComponents.FOOD)",
            "stack.get(DataComponents.CONSUMABLE)",
            "ApplyStatusEffectsConsumeEffect",
            "entity.removeEffect(effect)",
    ):
        if required not in preservation_text:
            errors.append(f"Preservation completion handling lost baseline behavior: {required}")
    if "UseItemCallback" in preservation_text or "PRESERVATION_FOOD" in preservation_text:
        errors.append("Preservation still runs at item-use start or narrows Forge's all-food behavior to a tag.")

    farmer_text = read(FARMER_ARMOR_EVENT)
    for required in (
            "LivingEntity entity",
            "entity.tickCount % ARMOR_CHECK_INTERVAL_TICKS",
            "entity.isInWater()",
            "DOLPHINS_GRACE_REFRESH_TICKS = 25",
            "entity.getItemBySlot(slot).is(TagMod.FARMER_ARMOR)",
    ):
        if required not in farmer_text:
            errors.append(f"Farmer armor handling lost baseline behavior: {required}")
    if "ServerTickEvents" in farmer_text or "ServerPlayer" in farmer_text or "server.getTickCount()" in farmer_text:
        errors.append("Farmer armor still uses a player-only global server clock.")

    mod_events_text = read(MOD_EVENTS)
    for stale_registration in (
            "PreservationEvent.register();",
            "FarmerArmorEffectEvent.register();",
            "TrashCanHideEvent.register();",
    ):
        if stale_registration in mod_events_text:
            errors.append(f"ModEvents retains superseded event registration: {stale_registration}")
    if (JAVA_ROOT / "event/server/TrashCanHideEvent.java").exists():
        errors.append("The incomplete 32-block per-tick TrashCanHideEvent still exists.")

    targeting_text = read(TRASH_CAN_TARGETING)
    for required in (
            "entity.getVehicle() instanceof SitEntity sitEntity",
            "sitEntity.getSitType() == SitEntity.TRASH_CAN",
            "clearTargetsAroundBlock",
    ):
        if required not in targeting_text:
            errors.append(f"Trash-can targeting lost baseline behavior: {required}")
    if "clearTargetsAroundPlayer" in targeting_text:
        errors.append("TrashCanTargeting still exposes the superseded per-tick player scan.")

    player_mixin_text = read(PLAYER_MIXIN)
    for required in (
            "@Mixin(Player.class)",
            '@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))',
            "LegacyPlayerDataCompat.loadFlatulenceStartingPosition((Player) (Object) this, input)",
    ):
        if required not in player_mixin_text:
            errors.append(f"PlayerMixin lost legacy player-data migration wiring: {required}")
    gift_mixin_text = read(GIVE_GIFT_TO_HERO_MIXIN)
    for required in (
            "@Mixin(GiveGiftToHero.class)",
            '@Inject(method = "<clinit>", at = @At("TAIL"))',
            ".putAll(GIFTS)",
            ".put(ModVillager.CHEF_KEY, ModLootTables.CHEF_GIFT)",
    ):
        if required not in gift_mixin_text:
            errors.append(f"Chef hero-gift mapping Mixin is missing: {required}")
    legacy_player_data_text = read(LEGACY_PLAYER_DATA_COMPAT)
    for required in (
            'FORGE_DATA = "ForgeData"',
            'FLATULENCE_START = "FlatulenceEffectStartingPosition"',
            "player.hasEffect(FLATULENCE)",
            'input.getInt("X")',
            'input.getInt("Y")',
            'input.getInt("Z")',
            "player.setAttached(FLATULENCE_EFFECT_STARTING_POSITION, position)",
    ):
        if required not in legacy_player_data_text:
            errors.append(f"Legacy flatulence player-data migration is missing: {required}")
    flatulence_effect_text = read(FLATULENCE_EFFECT)
    if "Vec3.atLowerCornerOf(player.blockPosition())" not in flatulence_effect_text:
        errors.append("FlatulenceEffect no longer records the Forge block-position starting point.")

    camera_text = read(CLIENT_MIXIN_ROOT / "client/CameraMixin.java")
    for required in (
            '@Inject(method = "update", at = @At("TAIL"))',
            "getCameraType().isFirstPerson()",
            "TrashCanTargeting.isHidingInTrashCan(player)",
            "this.setRotation(this.yRot(), 0.0F)",
    ):
        if required not in camera_text:
            errors.append(f"CameraMixin lost trash-can camera behavior: {required}")

    avatar_text = read(CLIENT_MIXIN_ROOT / "client/AvatarRendererMixin.java")
    for required in (
            "extractRenderState(Lnet/minecraft/world/entity/Avatar;",
            "((FabricRenderState) state).setData(",
            "TrashCanTargeting.isHidingInTrashCan(avatar)",
    ):
        if required not in avatar_text:
            errors.append(f"AvatarRendererMixin lost trash-can render-state extraction: {required}")

    living_renderer_text = read(CLIENT_MIXIN_ROOT / "client/LivingEntityRendererMixin.java")
    for required in (
            "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;",
            'at = @At("HEAD")',
            "cancellable = true",
            "getDataOrDefault(TrashCanRenderState.HIDDEN, false)",
            "ci.cancel()",
    ):
        if required not in living_renderer_text:
            errors.append(f"LivingEntityRendererMixin lost player hiding behavior: {required}")

    render_state_text = read(TRASH_CAN_RENDER_STATE)
    if "RenderStateDataKey<Boolean> HIDDEN" not in render_state_text:
        errors.append("TrashCanRenderState does not define a typed hidden-player render-state key.")

    return errors, len(common_mixins), len(client_mixins)


def parse_access_widener_entries(path: Path, errors: list[str]) -> set[tuple[str, ...]]:
    lines = path.read_text(encoding="utf-8-sig").splitlines()
    non_empty_lines = [line.strip() for line in lines if line.strip() and not line.strip().startswith("#")]
    if not non_empty_lines:
        errors.append(f"{ACCESS_WIDENER_NAME} is empty.")
        return set()
    if non_empty_lines[0] != "accessWidener v2 official":
        errors.append(f"{ACCESS_WIDENER_NAME} must start with 'accessWidener v2 official'.")

    entries: set[tuple[str, ...]] = set()
    seen: set[tuple[str, ...]] = set()
    for index, line in enumerate(non_empty_lines[1:], start=2):
        parts = tuple(line.split())
        if len(parts) != 5:
            errors.append(f"{ACCESS_WIDENER_NAME}:{index} has malformed entry: {line}")
            continue
        access, target_type, owner, member, descriptor = parts
        if access not in {"accessible", "mutable"}:
            errors.append(f"{ACCESS_WIDENER_NAME}:{index} has unsupported access type {access!r}.")
        if target_type != "field":
            errors.append(f"{ACCESS_WIDENER_NAME}:{index} should only widen fields in this project.")
        if not owner.startswith("net/minecraft/"):
            errors.append(f"{ACCESS_WIDENER_NAME}:{index} widens non-Minecraft owner {owner!r}.")
        if not descriptor:
            errors.append(f"{ACCESS_WIDENER_NAME}:{index} has an empty descriptor.")
        if parts in seen:
            errors.append(f"{ACCESS_WIDENER_NAME}:{index} duplicates an existing entry.")
        seen.add(parts)
        entries.add(parts)
    return entries


def validate_access_widener() -> tuple[list[str], int]:
    errors: list[str] = []
    fabric_mod = parse_json(FABRIC_MOD_JSON)
    if fabric_mod.get("accessWidener") != ACCESS_WIDENER_NAME:
        errors.append(f"fabric.mod.json accessWidener should be {ACCESS_WIDENER_NAME}.")

    build_text = read(BUILD_GRADLE)
    match = re.search(r'accessWidenerPath\s*=\s*file\("([^"]+)"\)', build_text)
    if match is None:
        errors.append("build.gradle does not configure loom.accessWidenerPath.")
    else:
        configured_path = ROOT / match.group(1)
        if configured_path.resolve() != ACCESS_WIDENER.resolve():
            errors.append(f"build.gradle accessWidenerPath points to {match.group(1)}, expected src/main/resources/{ACCESS_WIDENER_NAME}.")

    entries = parse_access_widener_entries(ACCESS_WIDENER, errors)
    missing_entries = sorted(EXPECTED_ACCESS_WIDENER_ENTRIES - entries)
    extra_entries = sorted(entries - EXPECTED_ACCESS_WIDENER_ENTRIES)
    if missing_entries:
        errors.append(f"{ACCESS_WIDENER_NAME} is missing required entries: {missing_entries}")
    if extra_entries:
        errors.append(f"{ACCESS_WIDENER_NAME} contains unreviewed extra entries: {extra_entries}")

    if not ADD_VILLAGE_STRUCTURES_EVENT.exists():
        errors.append("AddVillageStructuresEvent is missing; access widener entries may be stale.")
    else:
        event_text = strip_comments(read(ADD_VILLAGE_STRUCTURES_EVENT))
        if "pool.templates.add" not in event_text:
            errors.append("StructureTemplatePool.templates is widened but AddVillageStructuresEvent does not add to pool.templates.")
        if "pool.rawTemplates" not in event_text:
            errors.append("StructureTemplatePool.rawTemplates is widened but AddVillageStructuresEvent does not read rawTemplates.")
        if re.search(r"\bpool\.rawTemplates\s*=", event_text) is None:
            errors.append("StructureTemplatePool.rawTemplates is mutable but AddVillageStructuresEvent does not assign rawTemplates.")
        if "ServerLifecycleEvents.SERVER_STARTING.register" not in event_text:
            errors.append("Village kitchens are not injected at the Fabric equivalent of ServerAboutToStartEvent.")
        if event_text.count("KITCHEN_WEIGHT") < 6 or "private static final int KITCHEN_WEIGHT = 4" not in event_text:
            errors.append("Village kitchen pool weights no longer match the Forge baseline value of four.")

    return errors, len(entries)


def main() -> int:
    errors: list[str] = []
    mixin_errors, common_mixin_count, client_mixin_count = validate_mixin_config()
    aw_errors, access_widener_entry_count = validate_access_widener()
    errors.extend(mixin_errors)
    errors.extend(aw_errors)

    if errors:
        print("Mixin/access widener verification failed:")
        print("\n".join(errors))
        return 1

    print("Mixin/access widener verification passed.")
    print(f"  common mixins: {common_mixin_count}")
    print(f"  client mixins: {client_mixin_count}")
    print(f"  access widener entries: {access_widener_entry_count}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
