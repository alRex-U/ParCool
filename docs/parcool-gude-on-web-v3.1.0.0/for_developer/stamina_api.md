# Stamina API

- This content is for mod developers.
- This feature is experimental. Please report if you find problems.

ParCool provides API for adding support of ParCool stamina.

## Installation

First import ParCool into your project in some way.  
Normally I recommend to use [Curse Maven](https://cursemaven.com/).

Add these sentences.

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

## Get Instance

```java
import com.alrex.parcool.api.Stamina;

/*-------In your code-------*/
Stamina instance = Stamina.get(player);
```

`Stamina.get(Player)` returns `@Nullable Stamina`, so don't forget null-check.
This method returns *null* when initialization is not done yet.

### Accessors

#### `int getValue()`

It returns current value.

##### Caution

This method returns accurate value when called for a local player on Client side,
but if called for other players in multi-player game or on server side, whether the value is accurate is not guaranteed
because of its synchronization system.

---

#### `int getMaxValue()`

It returns max value of stamina.

---

#### `boolean isExhausted`

It returns whether the stamina is in exhausted state.

---

#### `@OnlyIn(Dist.Client) void setValue(int value)`

It sets current value.

##### Caution

This method can be called only for a local player on client side

---

#### `@OnlyIn(Dist.Client) void consume(int value)`

It consumes stamina by passed value.

##### Caution

This method can be called only for a local player on client side

---

#### `@OnlyIn(Dist.Client) void recover(int value)`

It recovers stamina by passed value.

##### Caution

This method can be called only for a local player on client side
