> **Blockfield:** этот форк попадает в пак так: закоммитить в `main`, затем в `blockfield-modpack` выполнить `scripts/bump-fork.sh parcool`. Скрипт сам ставит тег `bfN`, ждёт сборку и закрепляет релиз. Версию руками не менять.

![ParCool_Logo](./parcool_logo.png)

# ParCool MOD

**Welcome to this project!**

_ParCool_ is a mod of Minecraft, for more _Cool_ Actions like _Parkour_.\
It's inspired by [SmartMoving](https://www.curseforge.com/minecraft/mc-mods/smart-moving). That was a very great mod.

Players can do more actions such as...

- Grabbing Cliffs
- Running Faster
- Roll
- Backflip
- WallJump
- CatLeap\
  etc

If it made you traceurs or traceuses ; parkour practitioners, I couldn't be happier!!

This project is always ready to accept your contribution.

### For Developers

This mod provides some features for mod developers, server-hosts and mod-packers.
Please read [ParCool Guide](docs/parcool-guide-on-web-v3.1.0.0/Introduction.md).

_ParCool_ is licensed with\
**GNU LESSER GENERAL PUBLIC LICENSE Version 3**.

## Developer checks

Install Python 3.12+, Node.js 22 and Just 1.57.0, native JDK 21 (`JAVA_HOME`) on Linux or Windows. Quality tools stay in the project cache.

```sh
just setup
just check
just format
```

`just --list` lists supported build and application commands.
