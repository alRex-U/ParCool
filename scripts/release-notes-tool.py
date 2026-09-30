"""Минимальная проверка release-notes для форков (bfN): формат + версия."""

import re
import sys
from pathlib import Path

REQUIRED_SECTIONS = (
    "Кратко",
    "Для игроков",
    "Технические изменения",
    "Обновлённые компоненты",
    "Совместимость и необходимые действия",
    "Известные проблемы",
    "Источники и ограничения полноты",
)


def check(path, repository=None, version=None):
    errors = []
    try:
        text = Path(path).read_text(encoding="utf-8")
    except OSError as exc:
        return [f"не читается: {exc}"]
    if not text.startswith("---\n"):
        return ["нет YAML-заголовка"]
    end = text.find("\n---\n", 4)
    if end < 0:
        return ["YAML-заголовок не закрыт"]
    meta = {}
    for line in text[4:end].split("\n"):
        if not line.strip() or line.strip().startswith("#"):
            continue
        m = re.fullmatch(r"([A-Za-z_]+):\s*(.*)", line.strip())
        if not m:
            return [f"заголовок: ожидается ключ: значение, got {line!r}"]
        v = m.group(2).strip()
        meta[m.group(1)] = (
            v[1:-1] if len(v) >= 2 and v[0] == v[-1] and v[0] in "\"'" else v
        )
    for k in (
        "schema_version",
        "repository",
        "version",
        "previous_version",
        "date",
        "kind",
        "backfilled",
    ):
        if k not in meta:
            errors.append(f"заголовок: отсутствует ключ `{k}`")
    if errors:
        return errors
    if meta.get("schema_version") != "1":
        errors.append("schema_version должен быть 1")
    if repository and meta.get("repository") != repository:
        errors.append(f"repository={meta['repository']!r}, ожидается {repository!r}")
    if version and meta.get("version") != version:
        errors.append(f"version={meta['version']!r}, ожидается {version!r}")
    if meta.get("kind") != "mod":
        errors.append("kind для форков всегда mod")
    if not re.fullmatch(r"bf\d+", meta.get("version", "")):
        errors.append("version форка: ожидается bfN")
    pv = meta.get("previous_version", "")
    if pv not in ("null",) and not re.fullmatch(r"bf\d+", pv):
        errors.append("previous_version: bfN или null")
    if not re.fullmatch(r"\d{4}-\d{2}-\d{2}", meta.get("date", "")):
        errors.append("date: YYYY-MM-DD")
    if meta.get("backfilled") not in ("true", "false"):
        errors.append("backfilled: true/false")
    body = text.split("\n---\n", 1)[1]
    for s in REQUIRED_SECTIONS:
        if not re.search(rf"(?m)^##\s+{re.escape(s)}\s*$", body):
            errors.append(f"раздел отсутствует: `## {s}`")
    return errors


if __name__ == "__main__":
    import argparse

    p = argparse.ArgumentParser()
    p.add_argument("file")
    p.add_argument("--repository")
    p.add_argument("--version")
    a = p.parse_args()
    errs = check(a.file, a.repository, a.version)
    for e in errs:
        print(f"ERROR: {e}")
    sys.exit(1 if errs else 0)
