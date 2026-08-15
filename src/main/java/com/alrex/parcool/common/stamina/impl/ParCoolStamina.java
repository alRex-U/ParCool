package com.alrex.parcool.common.stamina.impl;

import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class ParCoolStamina extends AbstractLocalStamina {
    private double max;
    private double value = 2000;
    private boolean exhausted = false;
    private int recoverCooldown = 0;

    public ParCoolStamina(Player owner, @Nullable AbstractLocalStamina old) {
        super(owner);
        updateMax();
        if (old != null) {
            setValue((int) (max * old.value() / old.max()));
            exhausted = old.isExhausted();
        }
    }

    private void updateMax() {
        var staminaAttr = owner.getAttribute(ParCoolAttributes.MAX_STAMINA.get());
        if (staminaAttr == null) return;
        this.max = staminaAttr.getValue();
        if (value > max) {
            value = max;
        }
    }

    @Override
    public double max() {
        return max;
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    public boolean isExhausted() {
        return exhausted;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
        setDirty();
        if (this.value < 0) this.value = 0;
        if (this.value > max) this.value = max;
    }

    @Override
    public void consume(double value) {
        if (exhausted) return;
        if (isInfinite()) return;

        this.value -= value;
        setDirty();
        if (this.value <= 0) {
            this.value = 0;
            exhausted = true;
        }
        recoverCooldown = 30;
    }

    @Override
    public void recover(double value) {
        this.value += value;
        setDirty();
        if (this.value > max) {
            this.value = max;
            exhausted = false;
        }
    }

    @Override
    public void tick() {
        recoverCooldown--;
        updateMax();
        var recoverAttr = owner.getAttribute(ParCoolAttributes.STAMINA_RECOVERY.get());
        if (recoverAttr == null) return;
        var recoverValue = recoverAttr.getValue();
        if (isInfinite()) {
            exhausted = false;
            recover(recoverValue * 5.);
        } else if (recoverCooldown <= 0) {
            recover(owner.onGround() ? recoverValue : recoverValue * 0.4);
        }
    }

    @Override
    public boolean showHud() {
        return true;
    }
}
