package com.alrex.parcool.api.stamina;

import com.alrex.parcool.api.ParCoolMobEffects;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractLocalStamina implements IReadableStamina {
    public AbstractLocalStamina(Player owner) {
        this.owner = owner;
    }

    protected final Player owner;

    public abstract void setValue(double value);

    public abstract void consume(double value);

    public abstract void recover(double value);

    public boolean isInfinite() {
        return owner.isCreative() || owner.isSpectator() || owner.hasEffect(ParCoolMobEffects.INEXHAUSTIBLE.get());
    }

    public void tick() {
    }

    public boolean imposePenalty() {
        return isExhausted();
    }

    public boolean showHud() {
        return false;
    }
}
