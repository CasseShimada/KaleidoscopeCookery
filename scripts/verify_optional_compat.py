#!/usr/bin/env python3
"""Verify the reproducible contracts for the three optional compatibility layers."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
MOD_ID = "kaleidoscope_cookery"
RESOURCES = ROOT / "src" / "main" / "resources"
LANG = RESOURCES / "assets" / MOD_ID / "lang"

WTHIT_FEATURES = {
    "fruit_basket", "kitchenware_rack", "table", "pot", "stockpot", "steamer",
    "shawarma_spit", "chopping_board", "enamel_basin", "food_bite_block",
    "oil_pot", "millstone", "recipe_block",
}


def load(path: Path) -> dict:
    with path.open("r", encoding="utf-8-sig") as handle:
        return json.load(handle)


def values(path: Path) -> set[str]:
    return set(load(path).get("values", []))


def main() -> int:
    errors: list[str] = []
    carry_black = values(RESOURCES / "data/carryon/tags/block/block_blacklist.json")
    carry_white = values(RESOURCES / "data/carryon/tags/block/block_whitelist.json")
    relocation = values(RESOURCES / "data/c/tags/block/relocation_not_supported.json")
    diggus_include = values(RESOURCES / "data/diggusmaximus/tags/block/included_blocks.json")
    diggus_exclude = values(RESOURCES / "data/diggusmaximus/tags/block/excluded_blocks.json")

    if relocation != {"#carryon:block_blacklist"}:
        errors.append("c:relocation_not_supported must hard-deny exactly the audited Carry On blacklist.")
    expected_diggus_safe = {f"{MOD_ID}:oil_block", f"{MOD_ID}:straw_block"}
    if diggus_include != expected_diggus_safe:
        errors.append("Diggus Maximus include tag must contain only inert oil and straw blocks.")
    expected_diggus_exclude = {"#carryon:block_whitelist"} | (carry_black - expected_diggus_safe)
    if diggus_exclude != expected_diggus_exclude:
        errors.append("Diggus Maximus exclude tag no longer covers every audited non-inert Cookery block.")
    if carry_black & carry_white:
        errors.append("Carry On blacklist and whitelist overlap.")
    if len(carry_white) != 31 or len(carry_black) != 85:
        errors.append(f"Carry On audit changed unexpectedly ({len(carry_white)} safe/{len(carry_black)} unsafe).")
    group_dir = RESOURCES / "data/diggusmaximus/tags/block/groups"
    if group_dir.exists() and any(group_dir.rglob("*.json")):
        errors.append("Cookery blocks must not be grouped as equivalent Diggus Maximus variants.")

    plugins = load(RESOURCES / "waila_plugins.json")
    expected_plugin = {
        "entrypoints": {
            "common": "com.github.ysbbbbbb.kaleidoscopecookery.compat.wthit.ModWthitCommonPlugin",
            "client": "com.github.ysbbbbbb.kaleidoscopecookery.compat.wthit.ModWthitClientPlugin",
        },
        "side": "*",
        "required": [],
        "defaultEnabled": True,
    }
    if plugins != {f"{MOD_ID}:overlay": expected_plugin}:
        errors.append("waila_plugins.json is not the WTHIT 20 common/client entrypoint contract.")

    fabric = load(RESOURCES / "fabric.mod.json")
    suggests = fabric.get("suggests", {})
    for mod in ("wthit", "carryon", "diggusmaximus"):
        if mod not in suggests:
            errors.append(f"fabric.mod.json does not keep {mod} optional under suggests.")
        if mod in fabric.get("depends", {}):
            errors.append(f"fabric.mod.json incorrectly makes {mod} a hard dependency.")

    required_lang = {
        "overlay.kaleidoscope_cookery.chopping_board.cut_count",
        "overlay.kaleidoscope_cookery.enamel_basin.oil_count",
        "overlay.kaleidoscope_cookery.shawarma_spit.cook_time",
        *(f"config.waila.plugin_{MOD_ID}.{feature}" for feature in WTHIT_FEATURES),
    }
    for path in sorted(LANG.glob("*.json")):
        data = load(path)
        missing = sorted(required_lang - data.keys())
        if missing:
            errors.append(f"{path.name} is missing WTHIT/neutral translations: {missing}")
    for legacy in (
        "jade.kaleidoscope_cookery.chopping_board.cut_count",
        "jade.kaleidoscope_cookery.enamel_basin.oil_count",
    ):
        if legacy not in load(LANG / "en_us.json"):
            errors.append(f"Legacy Jade translation alias was removed: {legacy}")

    build = (ROOT / "build.gradle").read_text(encoding="utf-8")
    properties = (ROOT / "gradle.properties").read_text(encoding="utf-8")
    if 'compileOnly "com.github.casseshimada.wthit:wthit:${project.wthit_version}:api"' not in build:
        errors.append("WTHIT API is not a remote compileOnly dependency.")
    if "verifyWthitApiArtifact" not in build or "wthit_api_sha256=" not in properties:
        errors.append("WTHIT release API artifact is not checksum-pinned.")
    if re.search(r"[A-Za-z]:\\\\Users\\\\", build):
        errors.append("build.gradle contains a machine-local absolute path.")

    java_roots = [ROOT / "src/main/java", ROOT / "src/client/java", ROOT / "src/gametest/java"]
    forbidden = (
        "tschipp.carryon.common.",
        "PickupHandler",
        "PlacementHandler",
        "CarryOnDataManager",
        "net.kyrptonaught.diggusmaximus.ExcavationServiceAccess",
        "net.kyrptonaught.diggusmaximus.Excavate",
    )
    for java_root in java_roots:
        for path in java_root.rglob("*.java"):
            text = path.read_text(encoding="utf-8")
            for token in forbidden:
                if token in text:
                    errors.append(f"{path.relative_to(ROOT)} references forbidden third-party internals: {token}")
    common_wthit = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/compat/wthit"
    for path in common_wthit.glob("*.java"):
        if "net.minecraft.client" in path.read_text(encoding="utf-8"):
            errors.append(f"Dedicated-server WTHIT source imports a client class: {path.name}")

    if errors:
        print("Optional compatibility verification failed:")
        print("\n".join(f"  - {error}" for error in errors))
        return 1
    print("Optional compatibility verification passed.")
    print("  Carry On: 31 carryable / 85 hard-denied blocks")
    print("  Diggus Maximus: 2 included / 114 excluded blocks")
    print("  WTHIT: 13 feature options, split common/client entrypoints, pinned API artifact")
    print(f"  localized languages: {len(list(LANG.glob('*.json')))}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
