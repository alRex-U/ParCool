# Mod API

- This content is for mod developers.

ParCool provides API for adding support of ParCool.

## Installation

First import ParCool into your project in some way.  
Normally I recommend to use [Curse Maven](https://cursemaven.com/).

Add these sentences to your gradle buildscripts.

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

## Stamina API

### Get Instance

```java
import com.alrex.parcool.api.Stamina;

/*-------In your code-------*/
Stamina instance = Stamina.get(player);
```

`Stamina.get(PlayerEntity)` returns `@Nullable Stamina`, so don't forget null-check.
This method returns *null* when initialization is not done yet.

#### Accessors

##### `int getValue()`

It returns current value.

###### Caution

This method returns accurate value when called for a local player on Client side,
but if called for other players in multi-player game or on server side, whether the value is accurate is not guaranteed
because of its synchronization system.

---

##### `int getMaxValue()`

It returns max value of stamina.

---

##### `boolean isExhausted`

It returns whether the stamina is in exhausted state.

---

##### `@OnlyIn(Dist.Client) void setValue(int value)`

It sets current value.

###### Caution

This method can be called only for a local player on client side

---

##### `@OnlyIn(Dist.Client) void consume(int value)`

It consumes stamina by passed value.

###### Caution

This method can be called only for a local player on client side

---

##### `@OnlyIn(Dist.Client) void recover(int value)`

It recovers stamina by passed value.

###### Caution

This method can be called only for a local player on client side

---

## Limitations API

There is a dedicated page.
Please read it.

- <[Limitation Document](./limitations.md)>

---

In addition to these, several other classes marked as APIs.

Please check package `com.alrex.parcool.api.*`