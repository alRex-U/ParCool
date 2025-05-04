# Limitations

---

**Limitation** is new feature added by ver-3.0.0.0.  
This enable server-hosts or mod-packers to control each player's possibility of each action and some config value.

---

## Two Types of Limitations

There are two types of limitations.

1. **Server-wide Limitations**
2. **Individual Limitations**

## Caution

To impose limitations to players, first you need to enable them.

### Example

#### Server-wide Limitation (serverconfig)

```text
limitations_imposed = true
```

#### Individual Limitation (mc command)

```text
parcool limitations enable <players>
```

---

## Server-wide Limitations

*Server-wide Limitations* is applied to all players by server-configuration file.

Please check serverconfig folder in folders of each world.

---

## Individual Limitations

*Individual Limitations* can be applied to each player by in-game commands.

### Example

#### Enabling

```text
parcool limitations enable <players>
```

#### Disable Each Actions

```
parcool limitations set <players> possibility <action> false
```

#### Set max stamina limitation

```text
parcool limitations set <players> max_stamina <value>
```

#### Set least stamina consumption of each action

```text
parcool limitations set <players> least_stamina_consumption <action> <value>
```

#### Set values to default

```text
parcool limitations set <players> to_default
```
