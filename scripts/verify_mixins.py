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
        if name == "ServerPlayerMixin":
            if "checkMovementStatistics" not in text or "ordinal = 3" not in text:
                errors.append("ServerPlayerMixin does not target the vanilla sprint exhaustion call.")
            if "@Redirect" not in text:
                errors.append("ServerPlayerMixin does not isolate sprint exhaustion at its call site.")

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
