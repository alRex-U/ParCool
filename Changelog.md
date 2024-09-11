# Changelogs

## ver-3.3.0.1

### Translation

- Russian (ru_ru) (Modified)
  - by Quarkrus

### Bugfix

- Some configs are generated on Server
- Charge Jump can be triggered even when it is disabled

---

## ver-3.3.0.0

### New Features

- Action : Charge Jump
- API : Action Event
- API : Animation Event
  - Addon for animation compatibility with other mods is planned

### Modified

- Catleap
  - Animation modified
- Fast Run
  - Animation modified
- Vault
  - Animation modified
- Dodge
  - Possible to go through narrow space up to about 1.5 block (1.20.x)
- Cling To Cliff
  - Cancelable by sneaking
- Hang Down
  - Cancelable by sneaking
- Fast Swim
  - Animation modified
  - Fast Swim speed modifier added
- Slide
  - Animation modified
  - Possible to look around
  - Add particle effect
- Horizontal Wall Run
  - Auto is selectable as one of controls in config
- Pole Climbing
  - More appropriate ladder detection
- HUD
  - HUDs show charge rate and action cool down

### Translation

- Korean(South Korea) (ko_kr) (Added)
  - by Maxwell12

### Bugfix

- Players can use some actions during crawling
- Players can start dive during riding objects
- Players can flip during roll and dodge
- Crawl stops when release button even in narrow space
- Horizontal Wall Run is not affected by speed attributes
- Limitations:AllowInfiniteStamina is not applied in client
- Sound subtitles missing
- etc

---

## ver-3.2.0.0

### New Features

- Action : Fast Swim
- Animation : Particles are added for some animations
- Sound : New sounds for its own Resource Pack
  - by *SquARzY*
- Keybind : Enabling / Disabling ParCool
  - by *Kasualix*
- API : Attributes
- API : Effects
- API : SoundEvents

### Modified

- ParCool Logo
- Fast Run
  - Animation was modified
- Dodge
  - Players don't get off blocks while doing dodge(configurable)
  - Animation was modified
- Flipping
  - Possible to trigger Flipping by pressing jump and forward or backward movement key
  - Which control is used is selectable
- Wall Slide
  - Consume more damage by default
  - Take damage while declining falling speed
- Wall Jump
  - The height of jump changes depend on camera angle
- Horizontal Wall Run
  - Easier to perform sequentially
- Dive
  - Activation conditions was relaxed
  - Possible to start in air by pressing jump key long time
  - Impossible to jump while performing
  - New Animation for Diving into water
- Roll
  - Animation was modified
- Stamina
  - Max value and recovery speed was changed into attributes
  - Recovery speed in air was decreased

### Translation

- Turkish (tr_tr) (Added)
  - by *furkenisnice*
- Spanish (es_es) (Modified)
  - by *elAltrex*
- English (en_us) (Modified)
  - by *EternalAdministrator*

### Bugfix

- Catleap can be triggerd while breakfall
- Catleap height is constant regardless of jump height
- Sliding speed is constant regardless of movement speed
- PlayerModelRotator does not apply completion to animations
- It is impossible to create instance of Stamina API

---

## ver-3.1.0.6

### Translation

- Russian (ru_ru) *(Added)*
  - by *[ Master Xort ]*
- Chinese (zh_cn) *(Modified)*
  - by *njt-233*

### Bugfix

- Global Limitation is not synchronized correctly
- There is a spelling mistake in the translation
  - contributed by *Marc-Antoine Sauvé*

---

## ver-3.1.0.4

### New Features

- Supports [Paragliders](https://www.curseforge.com/minecraft/mc-mods/paragliders) Mod as one of stamina systems (only
  on 1.20.1)

### Bugfix

- Players cannot read in-game guide book of Patchouli books on 1.20.1
- [#194](https://github.com/alRex-U/ParCool/issues/194)
- [#197](https://github.com/alRex-U/ParCool/issues/197)

---

## ver-3.1.0.0

- Date : 2023 Aug.

### New Features

- Action : Climb Poles
- Action : Wall Run
- Action : Skydiving
- Actions play sound
- Backward Wall Jump
- Just-time Breakfall
- Some New Configs
- Some Limitation items
- Get Limitation Command
- Stamina API

### Modified

- Dive goes swimming motion directly
- FastRun action is toggleable
- Dodge Animation
- Vault Animation
- FastRun Animation
- SafetyTap Animation
- Make Slide impossible to be triggered in air
- Some Configs are renamed
- Some Limitations are renamed

### Bugfix

- ParCool cannot work on plugin installed server.
- Solid Mobs Incompatibility
- ParCool disable ladder animation of NotEnoughAnimations
- ParCool disable ladder detection of other mods.
- Stamina are not infinite when players are creative.
- Some actions are able to used when riding entity
- Fast-run animation stops and does not restart
- Vault is triggered by stairs
- Sliding on ice provides over speed

---
