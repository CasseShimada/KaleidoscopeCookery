#!/usr/bin/env python3
"""Verify pack.mcmeta against the configured Minecraft version metadata."""

from __future__ import annotations

import json
import sys
from pathlib import Path
from zipfile import BadZipFile, ZipFile


ROOT = Path(__file__).resolve().parents[1]
GRADLE_PROPERTIES = ROOT / "gradle.properties"
PACK_METADATA = ROOT / "src/main/resources/pack.mcmeta"
LOOM_CACHE = Path.home() / ".gradle/caches/fabric-loom"


def read_properties(path: Path) -> dict[str, str]:
    properties: dict[str, str] = {}
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        properties[key.strip()] = value.strip()
    return properties


def find_minecraft_jar(version: str) -> Path | None:
    version_cache = LOOM_CACHE / version
    for name in ("minecraft-client.jar", "minecraft-merged.jar", "minecraft-common.jar"):
        candidate = version_cache / name
        if candidate.is_file():
            return candidate
    return None


def read_version_metadata(jar_path: Path) -> dict[str, object]:
    with ZipFile(jar_path) as jar:
        with jar.open("version.json") as handle:
            return json.load(handle)


def main() -> int:
    errors: list[str] = []
    properties = read_properties(GRADLE_PROPERTIES)
    minecraft_version = properties.get("minecraft_version")
    if not minecraft_version:
        errors.append("gradle.properties does not define minecraft_version.")
        minecraft_version = "unknown"

    jar_path = find_minecraft_jar(minecraft_version)
    if jar_path is None:
        errors.append(
            f"Minecraft {minecraft_version} Loom cache is missing; run the Gradle build before this verifier."
        )
        version_metadata: dict[str, object] = {}
    else:
        try:
            version_metadata = read_version_metadata(jar_path)
        except (BadZipFile, KeyError, json.JSONDecodeError) as exc:
            errors.append(f"Failed to read {jar_path}/version.json: {exc}")
            version_metadata = {}

    pack_version = version_metadata.get("pack_version")
    if not isinstance(pack_version, dict):
        errors.append("Minecraft version.json does not contain pack_version metadata.")
        resource_major = data_major = None
    else:
        resource_major = pack_version.get("resource_major")
        data_major = pack_version.get("data_major")
        if not isinstance(resource_major, int) or not isinstance(data_major, int):
            errors.append("Minecraft version.json has invalid resource/data pack major versions.")

    try:
        pack_metadata = json.loads(PACK_METADATA.read_text(encoding="utf-8-sig"))
        pack = pack_metadata["pack"]
        min_format = pack["min_format"]
        max_format = pack["max_format"]
    except (OSError, KeyError, TypeError, json.JSONDecodeError) as exc:
        errors.append(f"Failed to read pack.mcmeta: {exc}")
        min_format = max_format = None

    if isinstance(resource_major, int) and isinstance(data_major, int):
        expected_min = min(resource_major, data_major)
        expected_max = max(resource_major, data_major)
        if min_format != expected_min or max_format != expected_max:
            errors.append(
                "pack.mcmeta format range does not match Minecraft "
                f"{minecraft_version}: expected {expected_min}..{expected_max}, "
                f"found {min_format}..{max_format}."
            )

    if errors:
        print("Pack metadata verification failed:")
        print("\n".join(errors))
        return 1

    print("Pack metadata verification passed.")
    print(f"  minecraft: {minecraft_version}")
    print(f"  resource pack major: {resource_major}")
    print(f"  data pack major: {data_major}")
    print(f"  declared range: {min_format}..{max_format}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
