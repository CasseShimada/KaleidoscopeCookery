#!/usr/bin/env python3
"""Verify the pinned, optional Farmer's Delight compatibility contract."""

from __future__ import annotations

import json
import re
import sys
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "src/main/resources"
COMPAT = ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/compat/farmersdelight"
GAMETESTS = ROOT / (
    "src/gametest/java/com/github/ysbbbbbb/kaleidoscopecookery/gametest/"
    "KaleidoscopeCookeryGameTests.java"
)
FIXTURE = ROOT / (
    "src/gametest/resources/data/kaleidoscope_cookery_gametest/recipe/"
    "farmers_delight/component_container.json"
)
OVERRIDE_FIXTURE = ROOT / (
    "src/gametest/resources/data/kaleidoscope_cookery_gametest/recipe/"
    "farmers_delight/implicit_override_container.json"
)


def load_json(path: Path) -> dict:
    with path.open("r", encoding="utf-8-sig") as handle:
        return json.load(handle)


def load_properties(path: Path) -> dict[str, str]:
    properties: dict[str, str] = {}
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        properties[key.strip()] = value.strip()
    return properties


def optional_value(path: Path, expected_id: str) -> bool:
    for value in load_json(path).get("values", []):
        if isinstance(value, dict) and value.get("id") == expected_id:
            return value.get("required") is False
    return False


def check_release_jar(errors: list[str], expected_internal_version: str) -> None:
    jars = sorted(
        path for path in (ROOT / "build/libs").glob("kaleidoscopecookery-*.jar")
        if not path.name.endswith("-sources.jar")
    )
    if not jars:
        errors.append("No built release JAR was available for the optional-boundary check.")
        return

    jar = jars[-1]
    with zipfile.ZipFile(jar) as archive:
        names = archive.namelist()
        bundled = [
            name for name in names
            if name.startswith("vectorwing/farmersdelight/")
            or (name.startswith("META-INF/jars/") and "farmers" in name.lower())
        ]
        if bundled:
            errors.append(f"Release JAR bundles Farmer's Delight entries: {bundled[:3]}")
        metadata = json.loads(archive.read("fabric.mod.json"))
        if metadata.get("suggests", {}).get("farmersdelight") != f"={expected_internal_version}":
            errors.append("Built fabric.mod.json does not advertise only the tested internal version.")
        if "farmersdelight" in metadata.get("depends", {}):
            errors.append("Built fabric.mod.json makes Farmer's Delight a hard dependency.")


def main() -> int:
    errors: list[str] = []
    properties = load_properties(ROOT / "gradle.properties")
    version = properties.get("farmers_delight_version", "")
    sha256 = properties.get("farmers_delight_sha256", "")
    if not re.fullmatch(r"26\.2-\d+\.\d+\.\d+", version):
        errors.append(f"farmers_delight_version is not a pinned Minecraft 26.2 release: {version!r}")
    if not re.fullmatch(r"[0-9a-f]{64}", sha256):
        errors.append("farmers_delight_sha256 is missing or malformed.")
    expected_internal_version = f"{version}+refabricated"

    build = (ROOT / "build.gradle").read_text(encoding="utf-8")
    coordinate = 'maven.modrinth:farmers-delight-refabricated:${project.farmers_delight_version}'
    if build.count(coordinate) != 3:
        errors.append("All two opt-in runtimes and the audit configuration must use the version property.")
    if re.search(r"farmers-delight-refabricated:(?!\$\{project\.farmers_delight_version\})[0-9]", build):
        errors.append("build.gradle contains a second hard-coded Farmer's Delight version.")
    if any("farmers-delight-refabricated" in line and "compileOnly" in line for line in build.splitlines()):
        errors.append("The full Farmer's Delight mod JAR must not be placed on a compileOnly classpath.")
    for contract in (
        "farmersDelightCompatArtifact",
        "verifyFarmersDelightCompatArtifact",
        "project.farmers_delight_sha256",
        'transitive = false',
        'dependsOn(tasks.named("verifyFarmersDelightCompatArtifact"))',
    ):
        if contract not in build:
            errors.append(f"build.gradle lost the pinned artifact contract: {contract}")

    metadata = load_json(RESOURCES / "fabric.mod.json")
    if metadata.get("suggests", {}).get("farmersdelight") != "=${farmers_delight_mod_version}":
        errors.append("Source fabric.mod.json must suggest exactly the processed tested version.")
    if "farmersdelight" in metadata.get("depends", {}):
        errors.append("Source fabric.mod.json makes Farmer's Delight mandatory.")

    compat_sources = {path.name: path.read_text(encoding="utf-8") for path in COMPAT.glob("*.java")}
    boundary = compat_sources.get("FarmersDelightCompat.java", "")
    adapter = compat_sources.get("CookingPotCompat.java", "")
    if "vectorwing.farmersdelight" in boundary or "import vectorwing" in adapter:
        errors.append("The common optional boundary has a bytecode-level Farmer's Delight type reference.")
    for forbidden in ("Proxy.newProxyInstance", "RecipeWrapper", "ItemHandler", "catch (Exception ignored)"):
        if any(forbidden in source for source in compat_sources.values()):
            errors.append(f"The strict adapter regressed to a fragile/silent construct: {forbidden}")
    for accessor in (
        '"input"', '"result"', '"container"', '"containerOverride"',
        '"getExperience"', '"getCookTime"', '"category"',
    ):
        if accessor not in adapter:
            errors.append(f"The version-fixed target API binding is missing {accessor}.")
    for behavior in (
        "isModLoaded(ID)", "ModDependency.Kind.SUGGESTS", "declared.matches(",
        "appendStockpotRecipes", "existingIds.add", "findMatchingRecipe",
    ):
        if behavior not in boundary:
            errors.append(f"The optional boundary lost required behavior: {behavior}")

    jei = (ROOT / (
        "src/client/java/com/github/ysbbbbbb/kaleidoscopecookery/compat/jei/category/"
        "StockpotRecipeCategory.java"
    )).read_text(encoding="utf-8")
    rei = (ROOT / (
        "src/client/java/com/github/ysbbbbbb/kaleidoscopecookery/compat/rei/category/"
        "ReiStockpotRecipeCategory.java"
    )).read_text(encoding="utf-8")
    if "FarmersDelightCompat.appendStockpotRecipes" not in jei:
        errors.append("JEI no longer receives converted Farmer's Delight recipes.")
    if "FarmersDelightCompat.appendStockpotRecipes" not in rei:
        errors.append("REI no longer receives converted Farmer's Delight recipes.")

    optional_tags = {
        RESOURCES / "data/c/tags/item/vegetables.json": "#c:foods/vegetable",
        RESOURCES / "data/c/tags/item/cooked_rice.json": "farmersdelight:cooked_rice",
        RESOURCES / "data/c/tags/item/foods/cooked_rice.json": "farmersdelight:cooked_rice",
        RESOURCES / "data/kaleidoscope_cookery/tags/item/kitchen_knife.json":
            "#farmersdelight:tools/knives",
        RESOURCES / "data/kaleidoscope_cookery/tags/block/rice_plantable.json":
            "farmersdelight:rich_soil_farmland",
    }
    for path, entry in optional_tags.items():
        if not optional_value(path, entry):
            errors.append(f"{path.relative_to(ROOT)} lost optional tag entry {entry}.")
    modern_vegetables = set(load_json(
        RESOURCES / "data/c/tags/item/foods/vegetable.json"
    ).get("values", []))
    expected_vegetables = {
        "#c:vegetables/chilipepper", "#c:vegetables/tomato", "#c:vegetables/lettuce",
    }
    if modern_vegetables != expected_vegetables:
        errors.append("c:foods/vegetable no longer exposes all three Cookery vegetable tags.")
    if set(load_json(RESOURCES / "data/c/tags/item/foods/tomato.json").get("values", [])) \
            != {"#c:crops/tomato"}:
        errors.append("c:foods/tomato no longer exposes Cookery tomatoes through the crop tag.")
    modern_knives = set(load_json(
        RESOURCES / "data/c/tags/item/tools/knife.json"
    ).get("values", []))
    expected_knives = {
        "kaleidoscope_cookery:iron_kitchen_knife",
        "kaleidoscope_cookery:gold_kitchen_knife",
        "kaleidoscope_cookery:diamond_kitchen_knife",
        "kaleidoscope_cookery:netherite_kitchen_knife",
    }
    if modern_knives != expected_knives:
        errors.append("c:tools/knife no longer exposes all four Cookery knives.")
    english = load_json(RESOURCES / "assets/kaleidoscope_cookery/lang/en_us.json")
    for translation in (
        "tag.item.c.foods.tomato",
        "tag.item.c.foods.vegetable",
        "tag.item.c.tools.knife",
    ):
        if translation not in english:
            errors.append(f"The modern cross-mod tag lacks an English display name: {translation}")

    fixture = load_json(FIXTURE)
    conditions = fixture.get("fabric:load_conditions", [])
    if conditions != [{"condition": "fabric:all_mods_loaded", "values": ["farmersdelight"]}]:
        errors.append("The target-only GameTest recipe is not conditionally loaded.")
    ingredients = fixture.get("ingredients", [])
    if len(ingredients) != 3 or len(set(ingredients)) == len(ingredients):
        errors.append("The target-only GameTest recipe no longer tests a duplicate ingredient.")
    if fixture.get("result", {}).get("count") != 2 \
            or "components" not in fixture.get("result", {}) \
            or fixture.get("container", {}).get("id") != "minecraft:glass_bottle" \
            or fixture.get("cookingtime") != 37:
        errors.append("The target-only GameTest recipe lost count/components/container/time coverage.")
    override_fixture = load_json(OVERRIDE_FIXTURE)
    if override_fixture.get("fabric:load_conditions") != conditions \
            or override_fixture.get("result", {}).get("id") != "minecraft:experience_bottle" \
            or "container" in override_fixture \
            or override_fixture.get("cookingtime") != 19:
        errors.append("The target remainder-override GameTest fixture lost its implicit-container contract.")

    tests = GAMETESTS.read_text(encoding="utf-8")
    for coverage in (
        "farmersDelightCookingRecipeCompatUsesTargetApiWhenInstalled",
        "farmersDelightRecipeActuallyCompletesInStockpot",
        "farmersDelightRecipesSurviveServerDataPackReload",
        "Set.of(1, 2, 3, 4, 5, 6)",
        "firstAppendCount",
        "26.2-3.6.8+refabricated",
    ):
        if coverage not in tests:
            errors.append(f"Farmer's Delight GameTest coverage is missing: {coverage}")

    workflow = (ROOT / ".github/workflows/gradle-publish-26.2.yml").read_text(encoding="utf-8")
    if "-PfarmersDelightCompatSmoke=true runGameTest" not in workflow:
        errors.append("CI does not execute the pinned installed-mod GameTest path.")

    check_release_jar(errors, expected_internal_version)

    if errors:
        print("Farmer's Delight compatibility verification failed:")
        print("\n".join(f"  - {error}" for error in errors))
        return 1
    print("Farmer's Delight compatibility verification passed.")
    print(f"  target: {expected_internal_version}")
    print(f"  artifact SHA-256: {sha256}")
    print("  boundary: optional exact-version reflection, no bundled target classes")
    print("  coverage: absent/installed, 1..6 inputs, reload, real cooking, JEI/REI, cross-tags")
    return 0


if __name__ == "__main__":
    sys.exit(main())
