package com.alrex.parcool.common.stamina.impl;

import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class HungerStamina extends AbstractLocalStamina {
    public HungerStamina(Player owner, @Nullable AbstractLocalStamina __) {
        super(owner);
    }

    @Override
    public void setValue(double value) {
    }

    @Override
    public void consume(double value) {
        //TODO
        if (isInfinite()) return;
    }

    @Override
    public void recover(double value) {
    }

    @Override
    public double max() {
        return 20;
    }

    @Override
    public double value() {
        return owner.getFoodData().getFoodLevel();
    }

    @Override
    public boolean isExhausted() {
        return value() <= 3;
    }

    @Override
    public boolean imposePenalty() {
        return false;
    }
}
