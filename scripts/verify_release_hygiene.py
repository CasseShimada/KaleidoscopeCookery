#!/usr/bin/env python3
"""Check for release-blocking leftovers such as debug code and old platform residue."""

from __future__ import annotations

import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]

TEXT_ROOTS = (
    ROOT / "src/main/java",
    ROOT / "src/client/java",
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


def main() -> int:
    errors: list[str] = []
    errors.extend(validate_forbidden_text())
    errors.extend(validate_forbidden_paths())
    errors.extend(validate_resource_source_sets())

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
