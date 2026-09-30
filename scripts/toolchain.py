"""Pinned project-local quality tools and reproducible build Python."""

import hashlib
import os
import platform
import shutil
import subprocess
import sys
import tarfile
import tempfile
import urllib.request
import zipfile
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CACHE = ROOT / ".cache" / "quality"
PYTHON_ASSETS = {
    "linux-arm64": [
        "https://github.com/astral-sh/python-build-standalone/releases/download/20260924/cpython-3.12.14%2B20260924-aarch64-unknown-linux-gnu-install_only.tar.gz",
        "c0574c9c9fec561eb192ad510e4e79aaad039929b3531b3bb873e2f630fb14a9",
    ],
    "linux-amd64": [
        "https://github.com/astral-sh/python-build-standalone/releases/download/20260924/cpython-3.12.14%2B20260924-x86_64-unknown-linux-gnu-install_only.tar.gz",
        "5eae8cf79dd47fc2496a4fccc892936be831ce7a84d984b2299dfb1cdb592682",
    ],
    "windows-arm64": [
        "https://github.com/astral-sh/python-build-standalone/releases/download/20260924/cpython-3.12.14%2B20260924-aarch64-pc-windows-msvc-install_only.tar.gz",
        "394b3bf95c45e5b02e02fe9b201e51df11dbff612db5b26406c6db484581317d",
    ],
    "windows-amd64": [
        "https://github.com/astral-sh/python-build-standalone/releases/download/20260924/cpython-3.12.14%2B20260924-x86_64-pc-windows-msvc-install_only.tar.gz",
        "c5303174bc29f5205decf6721ac549d4eb41c448f9b8c46cbc562d00348865bb",
    ],
}
BINARY_ASSETS = {
    "actionlint-linux-amd64": [
        "https://github.com/rhysd/actionlint/releases/download/v1.7.12/actionlint_1.7.12_linux_amd64.tar.gz",
        "8aca8db96f1b94770f1b0d72b6dddcb1ebb8123cb3712530b08cc387b349a3d8",
    ],
    "actionlint-linux-arm64": [
        "https://github.com/rhysd/actionlint/releases/download/v1.7.12/actionlint_1.7.12_linux_arm64.tar.gz",
        "325e971b6ba9bfa504672e29be93c24981eeb1c07576d730e9f7c8805afff0c6",
    ],
    "actionlint-windows-amd64": [
        "https://github.com/rhysd/actionlint/releases/download/v1.7.12/actionlint_1.7.12_windows_amd64.zip",
        "6e7241b51e6817ea6a047693d8e6fed13b31819c9a0dd6c5a726e1592d22f6e9",
    ],
    "actionlint-windows-arm64": [
        "https://github.com/rhysd/actionlint/releases/download/v1.7.12/actionlint_1.7.12_windows_arm64.zip",
        "cadcf7ea4efe3a68728893813643cebe1185e5b1d4be5b96245f65c9a4d5ea41",
    ],
    "shfmt-linux-amd64": [
        "https://github.com/mvdan/sh/releases/download/v3.14.1/shfmt_v3.14.1_linux_amd64",
        "76e77641faa025814b77f153b29796b8e6fa2fca03e0c76a691608b86c7ea7bf",
    ],
    "shfmt-linux-arm64": [
        "https://github.com/mvdan/sh/releases/download/v3.14.1/shfmt_v3.14.1_linux_arm64",
        "5f2db09dae91fca848f7adbdd014632e921a383863a2ad7e0450ad3aba0c6489",
    ],
    "shfmt-windows-amd64": [
        "https://github.com/mvdan/sh/releases/download/v3.14.1/shfmt_v3.14.1_windows_amd64.exe",
        "13629ce28442ca80b6b5a819f7574ab39e1c28c6e26734ca816c9714e04851df",
    ],
    "shellcheck-linux-arm64": [
        "https://github.com/koalaman/shellcheck/releases/download/v0.11.0/shellcheck-v0.11.0.linux.aarch64.tar.gz",
        "68a8133197a50beb8803f8d42f9908d1af1c5540d4bb05fdfca8c1fa47decefc",
    ],
    "shellcheck-linux-amd64": [
        "https://github.com/koalaman/shellcheck/releases/download/v0.11.0/shellcheck-v0.11.0.linux.x86_64.tar.gz",
        "b7af85e41cc99489dcc21d66c6d5f3685138f06d34651e6d34b42ec6d54fe6f6",
    ],
    "shellcheck-windows-amd64": [
        "https://github.com/koalaman/shellcheck/releases/download/v0.11.0/shellcheck-v0.11.0.zip",
        "8a4e35ab0b331c85d73567b12f2a444df187f483e5079ceffa6bda1faa2e740e",
    ],
    "just-windows-arm64": [
        "https://github.com/casey/just/releases/download/1.57.0/just-1.57.0-aarch64-pc-windows-msvc.zip",
        "14ff33bbac8d2f07deda30cd3bafe7be083213cdb1c4dae7a55567d375cc17f1",
    ],
    "just-linux-arm64": [
        "https://github.com/casey/just/releases/download/1.57.0/just-1.57.0-aarch64-unknown-linux-musl.tar.gz",
        "f225044a81adea6e0b3a8b9370aaf374e6af76c8735ae263ac993df55fd137ec",
    ],
    "just-windows-amd64": [
        "https://github.com/casey/just/releases/download/1.57.0/just-1.57.0-x86_64-pc-windows-msvc.zip",
        "4c7391d17cb1d17b758b52004ee6411372b8a13ff37c3c9b9031625cb6026e09",
    ],
    "just-linux-amd64": [
        "https://github.com/casey/just/releases/download/1.57.0/just-1.57.0-x86_64-unknown-linux-musl.tar.gz",
        "45b548094283cb9739af8f13273b8cddeee869f5b4ef2bb631b1f311cb566155",
    ],
}


def child_env():
    env = os.environ.copy()
    for key in (
        "APPIMAGE",
        "__MISE_SHIM",
        "__MISE_DIFF",
        "PYTHONHOME",
        "PYTHONPATH",
        "JAVA_TOOL_OPTIONS",
        "_JAVA_OPTIONS",
        "JDK_JAVA_OPTIONS",
        "CLASSPATH",
    ):
        env.pop(key, None)
    env["PYTHONUTF8"] = "1"
    env["PYTHONIOENCODING"] = "utf-8"
    sdk = Path(env.get("JAVA_HOME", "")) / "bin"
    env["PATH"] = os.pathsep.join(
        [
            str(CACHE / "bin"),
            *([str(sdk)] if env.get("JAVA_HOME") and sdk.is_dir() else []),
            *(
                entry
                for entry in env.get("PATH", "").split(os.pathsep)
                if not (
                    Path(entry).name.lower() == "shims"
                    and Path(entry).parent.name.lower() == "mise"
                )
            ),
        ]
    )
    return env


def native(name):
    executable = shutil.which(str(name), path=child_env()["PATH"])
    if executable is None:
        raise RuntimeError(
            f"Missing native {name}; install the repository's documented SDK prerequisites"
        )
    return executable


def call(*args, capture=False):
    executable = native(args[0])
    return subprocess.run(
        [executable, *map(str, args[1:])],
        check=True,
        env=child_env(),
        text=True,
        stdout=subprocess.PIPE if capture else sys.stderr,
        shell=os.name == "nt" and Path(executable).suffix.lower() in (".cmd", ".bat"),
    ).stdout


def host():
    system = platform.system().lower()
    machine = platform.machine().lower()
    arch = {
        "amd64": "amd64",
        "x86_64": "amd64",
        "arm64": "arm64",
        "aarch64": "arm64",
    }.get(machine)
    if system not in ("linux", "windows") or arch is None:
        raise RuntimeError(f"Unsupported platform: {system}/{machine}")
    return system, arch


def download(name, url, checksum):
    target = CACHE / name
    if target.is_file():
        data = target.read_bytes()
    else:
        with urllib.request.urlopen(url, timeout=60) as response:
            data = response.read()
    if hashlib.sha256(data).hexdigest() != checksum:
        raise RuntimeError(f"Tool checksum mismatch: {name}")
    if not target.is_file():
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_bytes(data)
    return target


def python_version(python):
    return call(
        python,
        "-I",
        "-X",
        "utf8",
        "-c",
        "import sys,zlib; print(sys.version_info[:3],zlib.ZLIB_VERSION,zlib.ZLIB_RUNTIME_VERSION)",
        capture=True,
    ).strip()


def canonical_python():
    if sys.version_info < (3, 12):
        raise RuntimeError("Install native Python 3.12 or later to run project tools")
    if sys.version_info[:3] == (3, 12, 14) and (
        zlib.ZLIB_VERSION,
        zlib.ZLIB_RUNTIME_VERSION,
    ) == ("1.3.2", "1.3.2"):
        return Path(sys.executable)
    key = "-".join(host())
    directory = CACHE / ("python-3.12.14-" + key)
    relative = Path("python") / ("python.exe" if os.name == "nt" else "bin/python3")
    python = directory / relative
    if not python.is_file():
        archive = download("python-3.12.14-" + key + ".tar.gz", *PYTHON_ASSETS[key])
        with tempfile.TemporaryDirectory(dir=CACHE) as temporary:
            staging = Path(temporary) / "install"
            staging.mkdir()
            with tarfile.open(archive) as source:
                source.extractall(staging, filter="data")
            version = python_version(staging / relative)
            if version != "(3, 12, 14) 1.3.2 1.3.2":
                raise RuntimeError(f"Canonical Python compressor mismatch: {version}")
            staging.rename(directory)
    version = python_version(python)
    if version != "(3, 12, 14) 1.3.2 1.3.2":
        raise RuntimeError(f"Canonical Python compressor mismatch: {version}")
    return python


def tool(name):
    if name == "ruff":
        directory = CACHE / "venv"
        python = directory / ("Scripts/python.exe" if os.name == "nt" else "bin/python")
        if not python.is_file():
            call(canonical_python(), "-I", "-m", "venv", "--clear", directory)
        probe = subprocess.run(
            [str(python), "-I", "-m", "ruff", "--version"],
            env=child_env(),
            capture_output=True,
            text=True,
        )
        if probe.returncode or probe.stdout.strip() != "ruff 0.15.7":
            call(
                python,
                "-I",
                "-m",
                "pip",
                "--isolated",
                "install",
                "--disable-pip-version-check",
                "ruff==0.15.7",
            )
        return directory / ("Scripts/ruff.exe" if os.name == "nt" else "bin/ruff")
    if name in ("prettier", "pnpm"):
        version = {"prettier": "3.8.3", "pnpm": "11.18.0"}[name]
        directory = CACHE / "npm"
        binary = (
            directory
            / "node_modules"
            / ".bin"
            / (name + ".cmd" if os.name == "nt" else name)
        )
        if not binary.is_file():
            call(
                "npm",
                "install",
                "--prefix",
                directory,
                "--no-audit",
                "--no-fund",
                "--ignore-scripts",
                "--package-lock=false",
                "--save-exact",
                name + "@" + version,
            )
        return binary
    system, arch = host()
    # ShellCheck/shfmt publish amd64 Windows binaries, supported by Windows 11 ARM64 emulation.
    if system == "windows" and name in ("shellcheck", "shfmt"):
        arch = "amd64"
    url, checksum = BINARY_ASSETS[name + "-" + system + "-" + arch]
    archive = download(name + "-" + Path(url).name, url, checksum)
    filename = name + (".exe" if system == "windows" else "")
    if url.endswith(".zip"):
        with zipfile.ZipFile(archive) as source:
            member = next(n for n in source.namelist() if Path(n).name == filename)
            data = source.read(member)
    elif url.endswith(".tar.gz"):
        with tarfile.open(archive) as source:
            member = next(
                m for m in source.getmembers() if Path(m.name).name == filename
            )
            data = source.extractfile(member).read()
    else:
        data = archive.read_bytes()
    binary = CACHE / "bin" / filename
    binary.parent.mkdir(parents=True, exist_ok=True)
    if not binary.is_file() or binary.read_bytes() != data:
        binary.write_bytes(data)
    binary.chmod(0o755)
    return binary


if __name__ == "__main__":
    if sys.argv[1:] == ["--ci"]:
        tool("just")
        python = canonical_python()
        with Path(os.environ["GITHUB_PATH"]).open(
            "a", encoding="utf-8", newline="\n"
        ) as output:
            output.write(str(CACHE / "bin") + "\n" + str(python.parent) + "\n")
    else:
        call(canonical_python(), "-I", "-X", "utf8", *sys.argv[1:])
