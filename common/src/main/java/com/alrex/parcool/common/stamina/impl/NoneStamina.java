package com.alrex.parcool.common.stamina.impl;

import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class NoneStamina extends AbstractLocalStamina {
    public NoneStamina(Player owner, @Nullable AbstractLocalStamina __) {
        super(owner);
    }

    @Override
    public double max() {
        return 1.;
    }

    @Override
    public double value() {
        return 1.;
    }

    @Override
    public boolean isExhausted() {
        return false;
    }

    @Override
    public void setValue(double value) {
    }

    @Override
    public void consume(double value) {
    }

    @Override
    public void recover(double value) {
    }

    @Override
    public boolean imposePenalty() {
        return false;
    }

    @Override
    public boolean isInfinite() {
        return false;
    }
}
