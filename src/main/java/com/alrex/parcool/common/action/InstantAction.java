package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

public abstract class InstantAction extends Action {
    @Override
    public final boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        return false;
    }

    @Override
    public final void onWorkingTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
    }

    @Override
    public final void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
    }

    @Override
    public final void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
    }

    @Override
    public final void onWorkingTickInServer(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
    }

    @Override
    public final void onStop(PlayerEntity player) {
    }

    @Override
    public final void onStopInServer(PlayerEntity player) {
    }

    @Override
    public final void onStopInOtherClient(PlayerEntity player) {
    }

    @Override
    public final void onStopInLocalClient(PlayerEntity player) {
    }
}
