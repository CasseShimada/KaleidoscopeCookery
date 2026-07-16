#!/usr/bin/env python3
"""Check for release-blocking leftovers such as debug code and old platform residue."""

from __future__ import annotations

import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
GENERAL_CONFIG = (
    ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/config/GeneralConfig.java"
)
REGISTRY_HOLDER_FILES = (
    ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/init/ModEffects.java",
    ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/init/ModTrigger.java",
)

TEXT_ROOTS = (
    ROOT / "src/main/java",
    ROOT / "src/client/java",
    ROOT / "src/datagen/java",
    ROOT / "src/datagen/resources",
    ROOT / "src/main/generated",
    ROOT / "src/main/resources",
)
TEXT_FILES = (
    ROOT / "build.gradle",
    ROOT / "settings.gradle",
    ROOT / "gradle.properties",
    ROOT / ".github/workflows/gradle-publish-26.2.yml",
)
TEXT_SUFFIXES = {
    ".accesswidener",
    ".gradle",
    ".java",
    ".json",
    ".mcmeta",
    ".properties",
    ".txt",
    ".yaml",
    ".yml",
}

FORBIDDEN_PATTERNS = (
    ("temporary marker", re.compile(r"\b(?:TODO|FIXME|XXX|HACK)\b")),
    ("console output", re.compile(r"\bSystem\.(?:out|err)\.")),
    ("stack trace print", re.compile(r"\bprintStackTrace\s*\(")),
    ("debug/trace logger", re.compile(r"\bLOGGER\.(?:debug|trace)\s*\(")),
    ("Forge API residue", re.compile(r"\b(?:net\.minecraftforge|net\.neoforged|NeoForge|ForgeRegistries|DeferredRegister|IEventBus)\b")),
)

FORBIDDEN_PATHS = (
    ROOT / "src/generated/resources",
    ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/util/neo",
    ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/client/event/ModModelEvent.java",
    ROOT / "src/client/java/com/github/ysbbbbbb/kaleidoscopecookery/client/event/ModModelEvent.java",
    ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/init/ModPredicateRegistry.java",
    ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/util/RecipeMatcher.java",
    ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/datagen/tag/TagItem.java",
    ROOT / "src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/datagen",
    ROOT / ".github/workflows/gradle-publish-1.21.1.yml",
)


def iter_text_files() -> list[Path]:
    files: set[Path] = set()
    for root in TEXT_ROOTS:
        if root.exists():
            files.update(path for path in root.rglob("*") if path.is_file() and path.suffix.lower() in TEXT_SUFFIXES)
    files.update(path for path in TEXT_FILES if path.exists())
    return sorted(files)


def validate_forbidden_text() -> list[str]:
    errors: list[str] = []
    for path in iter_text_files():
        text = path.read_text(encoding="utf-8-sig")
        for label, pattern in FORBIDDEN_PATTERNS:
            for match in pattern.finditer(text):
                line = text.count("\n", 0, match.start()) + 1
                errors.append(f"{path.relative_to(ROOT)}:{line} contains {label}: {match.group(0)!r}")
    return errors


def validate_forbidden_paths() -> list[str]:
    errors: list[str] = []
    for path in FORBIDDEN_PATHS:
        if path.is_file():
            errors.append(f"Forbidden release residue file exists: {path.relative_to(ROOT)}")
        elif path.is_dir() and any(child.is_file() for child in path.rglob("*")):
            errors.append(f"Forbidden release residue directory contains files: {path.relative_to(ROOT)}")
    return errors


def validate_resource_source_sets() -> list[str]:
    handwritten = ROOT / "src/main/resources"
    generated = ROOT / "src/main/generated"
    errors: list[str] = []
    generated_files = [path for path in generated.rglob("*") if path.is_file()]
    if not generated_files:
        errors.append("Generated resource source set is empty: src/main/generated")
        return errors
    for path in generated_files:
        relative = path.relative_to(generated)
        duplicate = handwritten / relative
        if duplicate.exists():
            errors.append(
                f"Generated resource is duplicated in src/main/resources: {relative.as_posix()}"
            )
    return errors


def validate_configuration_boundary() -> list[str]:
    errors: list[str] = []
    config_text = GENERAL_CONFIG.read_text(encoding="utf-8")
    for field in (
        "satiatedShieldAbsorbEnabled",
        "satiatedShieldAbsorbExcessDamage",
    ):
        if re.search(rf"public\s+boolean\s+{field}\s*(?:=|;)", config_text):
            errors.append(f"GeneralConfig exposes mutable configuration field: {field}")
        if f"public boolean {field}()" not in config_text:
            errors.append(f"GeneralConfig is missing read accessor: {field}()")

    build_text = (ROOT / "build.gradle").read_text(encoding="utf-8")
    properties_text = (ROOT / "gradle.properties").read_text(encoding="utf-8")
    client_smoke = re.search(
        r'if\s*\(project\.findProperty\("clientCompatSmoke"\)\s*==\s*"true"\)\s*\{.*?^\s*\}',
        build_text,
        flags=re.DOTALL | re.MULTILINE,
    )
    production_build_text = build_text
    if client_smoke is not None:
        smoke_block = client_smoke.group(0)
        if not re.search(r'runtimeOnly\s+["\']maven\.modrinth:cloth-config:', smoke_block):
            errors.append("Client compatibility smoke no longer scopes Cloth Config as runtime-only.")
        production_build_text = build_text[:client_smoke.start()] + build_text[client_smoke.end():]

    if "cloth-config" in production_build_text or "cloth_config_version" in properties_text:
        errors.append("Unused Cloth Config dependency remains in the build configuration.")
    return errors


def validate_registry_holders() -> list[str]:
    errors: list[str] = []
    for path in REGISTRY_HOLDER_FILES:
        text = path.read_text(encoding="utf-8")
        if re.search(r"public\s+static\s+(?!final\b)[^;=]+\s+[A-Z][A-Z0-9_]*\s*(?:=|;)", text):
            errors.append(f"Registry holder exposes mutable content: {path.relative_to(ROOT)}")
    return errors


def main() -> int:
    errors: list[str] = []
    errors.extend(validate_forbidden_text())
    errors.extend(validate_forbidden_paths())
    errors.extend(validate_resource_source_sets())
    errors.extend(validate_configuration_boundary())
    errors.extend(validate_registry_holders())

    if errors:
        print("Release hygiene verification failed:")
        print("\n".join(errors))
        return 1

    print("Release hygiene verification passed.")
    print(f"  text files scanned: {len(iter_text_files())}")
    print(f"  forbidden paths checked: {len(FORBIDDEN_PATHS)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
