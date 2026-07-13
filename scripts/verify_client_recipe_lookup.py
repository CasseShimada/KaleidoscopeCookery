#!/usr/bin/env python3
"""Verify client recipe integrations use synchronized vanilla recipe state."""

from __future__ import annotations

import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
CLIENT_ROOT = ROOT / "src/client/java/com/github/ysbbbbbb/kaleidoscopecookery"
LOOKUP = CLIENT_ROOT / "client/util/ClientRecipeLookup.java"
LEGACY_LOADER = CLIENT_ROOT / "client/util/RecipeJsonLoader.java"
JEI_CATEGORIES = CLIENT_ROOT / "compat/jei/category"
REI_CATEGORIES = CLIENT_ROOT / "compat/rei/category"
RICE_BOWL_MAKER = CLIENT_ROOT / "client/util/RiceBowlRecipeMaker.java"


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def main() -> int:
    errors: list[str] = []

    if LEGACY_LOADER.exists():
        errors.append("Legacy RecipeJsonLoader still exists.")
    if not LOOKUP.exists():
        errors.append("ClientRecipeLookup is missing.")
    else:
        lookup = read(LOOKUP)
        if "level.recipeAccess().getSynchronizedRecipes().recipes()" not in lookup:
            errors.append("Client recipe lookup does not use synchronized RecipeAccess state.")
        for forbidden in (
            "FabricLoader",
            "IntegratedServer",
            "RecipeManager",
            "ResourceManager",
            "Files.walk",
            "JsonParser",
        ):
            if forbidden in lookup:
                errors.append(f"Client recipe lookup still uses legacy fallback API: {forbidden}")

    category_sources = [read(path) for path in sorted(JEI_CATEGORIES.glob("*RecipeCategory.java"))]
    lookup_users = [source for source in category_sources if "ClientRecipeLookup.getRecipes(" in source]
    if len(lookup_users) != 8:
        errors.append(f"Expected 8 JEI categories to use ClientRecipeLookup, found {len(lookup_users)}.")
    if any("RecipeJsonLoader" in source for source in category_sources):
        errors.append("A JEI category still references RecipeJsonLoader.")

    rei_sources = [read(path) for path in sorted(REI_CATEGORIES.glob("*RecipeCategory.java"))]
    rei_lookup_users = [source for source in rei_sources if "ClientRecipeLookup.getRecipes(" in source]
    if len(rei_lookup_users) != 5:
        errors.append(f"Expected 5 REI categories to use ClientRecipeLookup, found {len(rei_lookup_users)}.")
    for forbidden in ("getSynchronizedRecipes()", "RecipeAccess"):
        if any(forbidden in source for source in rei_sources):
            errors.append(f"A REI category still performs its own synchronized recipe lookup: {forbidden}")

    rice_bowl_maker = read(RICE_BOWL_MAKER)
    if "ClientRecipeLookup.getRecipes(ModRecipes.RICE_BOWL_SERIALIZER)" not in rice_bowl_maker:
        errors.append("Rice bowl JEI recipes do not use the synchronized client recipe lookup.")

    if errors:
        print("Client recipe lookup verification failed:")
        print("\n".join(errors))
        return 1

    print("Client recipe lookup verification passed.")
    print("  JEI recipe categories: 8")
    print("  REI recipe categories: 5")
    print("  serializer-only recipe adapters: 1")
    return 0


if __name__ == "__main__":
    sys.exit(main())
