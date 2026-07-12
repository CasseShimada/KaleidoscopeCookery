"""Resolve resources across the handwritten and generated source trees."""

from __future__ import annotations

from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
RESOURCE_ROOTS = (
    ROOT / "src/main/resources",
    ROOT / "src/main/generated",
)


def resolve_resource(*parts: str) -> Path:
    relative = Path(*parts)
    matches = [root / relative for root in RESOURCE_ROOTS if (root / relative).exists()]
    if len(matches) > 1:
        locations = ", ".join(str(path.relative_to(ROOT)) for path in matches)
        raise RuntimeError(f"Duplicate resource {relative.as_posix()}: {locations}")
    return matches[0] if matches else RESOURCE_ROOTS[0] / relative


def iter_resource_files(*parts: str, pattern: str = "*") -> list[Path]:
    files: dict[Path, Path] = {}
    for root in RESOURCE_ROOTS:
        directory = root.joinpath(*parts)
        if not directory.exists():
            continue
        for path in directory.rglob(pattern):
            if not path.is_file():
                continue
            relative = path.relative_to(root)
            if relative in files:
                locations = ", ".join(
                    str(candidate.relative_to(ROOT)) for candidate in (files[relative], path)
                )
                raise RuntimeError(f"Duplicate resource {relative.as_posix()}: {locations}")
            files[relative] = path
    return [files[relative] for relative in sorted(files)]


def resource_relative(path: Path) -> Path:
    for root in RESOURCE_ROOTS:
        if path.is_relative_to(root):
            return path.relative_to(root)
    raise ValueError(f"Path is outside configured resource roots: {path}")
