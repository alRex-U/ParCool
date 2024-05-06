# Limitations

---

Limitation is a feature for setting limit of players parkour skills.

This document is only for versions newer than v3.2.1.0.
If you want to see old document, please read [this](../../parcool-gude-on-web-v3.1.0.0/for_developer/limitations.md).

---

## Three Types of Limitations

There are three types of limitations.

1. **Server-wide Limitations**
2. **Individual Limitations**
3. **Custom Limitations**

## Caution

To impose limitations to players, first you need to enable them.

### Example

#### Server-wide Limitation (serverconfig)

```text
limitations_imposed = true
```

#### Individual Limitation (mc command)

```text
parcool limitations enable individual of <players>
```

#### Custom Limitation (mc command)

If your limitation id is `youprojectname:example`

```text
parcool limitations enable yourprojectname:example of <players>
```

---

## Server-wide Limitations

*Server-wide Limitations* is applied to all players by server-configuration file.

Please check serverconfig folder in folders of each world.

---

## Individual and Custom Limitations

*Individual Limitations* and *Custom Limitations* can be applied to each player by in-game commands.

### Example

#### Disable Each Actions

```
parcool limitations set <your limitation id> of <players> possibility <action> false
```

#### Disable Infinite Stamina

```
parcool limitation set <your limitation id> of  <players> boolean allow_infinite_stamina false
```

#### Set max stamina limitation

```
parcool limitations set <your limitation id> of <players> integer max_stamina_limit <value>
```

#### Set least stamina consumption of each action

```
parcool limitations set <your limitation id> of <players> least_stamina_consumption <action> <value>
```

#### Set values to default

```text
parcool limitations set <your limitation id> of <players> to_default
```

### From your java code

As for *Custom Limitations*, you can create and control from your code.

```groovy
repositories {
    maven {
        url "https://cursemaven.com"
    }
}
```

```groovy
dependencies {
    implementation fg.deobf("curse.maven:parcool-482378:${fileid /*Edit here*/}")
}
```

#### Caution

- This feature is marked as **Unstable**. This feature accesses ParCool internal classes directly, so I cannot guarantee
  that this operation will work across some versions as same.
- This feature is permitted only in **Server side**.

#### Example

```java
// in 1.16.5

import com.alrex.parcool.api.unstable.Limitation;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.common.action.impl.FastRun;

public class YouCode {
  public static void controlLimitation(ServerPlayer target) {
        Limitation.get(player, new Limitation.ID("yourmodid", "example"))
                // enable limitation "yourmodid:example"
                .enable()
                // prohibit infinite stamina
                .set(ParCoolConfig.Server.Booleans.AllowInfiniteStamina, false)
                // prohibit FastRun action
                .permit(FastRun.class, false)
                // sync (don't forget to call this)
                .apply();
    }
}
```
