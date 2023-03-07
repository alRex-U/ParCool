# FAQ

Frequently Asked Questions

---

### Q. Is there any plans of a port to fabric?

#### A. No

It will takes much time of port to other API.  
But if someone is willing to do that, I will accept the contribution.

---

### Q. Is 1.12.2 version development now worked on?

#### A. No

I have no plan to port this mod to former version than 1.16.

---

### Q. Does this mod work with [other animation mod]?

#### A. Maybe Yes

*Not Enough Animations*, *Elenai Dodge* and *Better Combat* are asked frequently.  
This mod's animation system is less prone to cause conflict with other mods.
Actually some players have reported that these mod's work well with this mod.

First try to install it.

---

### Q. Why I can't place blocks fast anymore by consecutively pressing right click?
#### A. Re-bind some keybindings may solve this.

ParCool use Right Click as some keybindings for some acitons.
When some keybindings are attached to same key of placing blocks, your placing pace will get slow.
This seems a problem of vanilla Minecraft, not of ParCool.
I recommend to use center button of your mouse.

---

## Since ver-3.0.0.0

### Q. Action Limitations by configs and commands don't work

#### A. Check whether the limitations are enabled.

ParCool's limitation features are enabled only when they are enabled by configs or commands explicitly.

##### Server-wide Limitation (serverconfig)

```text
limitations_imposed = true
```

##### Individual Limitation (mc command)

```text
parcool limitations enable <players>
```
