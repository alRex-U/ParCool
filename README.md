# ParCool — Fabric 1.21.1

![ParCool](parcool_logo.png)

ParCool adds parkour movements to Minecraft: climbing, rolls, wall jumps and faster running.
This Fabric 1.21.1 fork is based on [ParCool by alRex-U](https://github.com/alRex-U/ParCool).
Blockfield maintains the fork, including fixes to sprint cancellation when stamina is exhausted.

## Build and checks

Requires JDK 21 (`JAVA_HOME`), Python 3.12+, Node.js 22 and Just 1.57.0.
Tools are cached inside the project; the commands work on Linux and Windows.

```sh
just setup
just check
just build
```

`just format` applies formatting. The mod JAR is written to `build/libs/`.

## Releases

After committing to `main`, run `scripts/bump-fork.sh parcool` from
[blockfield-client](https://github.com/Blockfield/blockfield-client). It creates a
`bfN` tag, waits for the build and pins the released JAR. Passing an existing `bfN`
as the second argument only updates the pin. Do not change the mod version by hand.
Shared mods also need the corresponding server pin and a coordinated server/client release.

[ParCool Guide](docs/parcool-guide-on-web-v3.1.0.0/Introduction.md) describes the
features available to mod developers and server operators.
License: [GNU LGPL v3](LICENSE).
