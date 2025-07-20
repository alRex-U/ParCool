package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.world.entity.player.Player;

public abstract class InstantAction extends Action {
    @Override
    public final boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
        return false;
    }

    @Override
    public final void onWorkingTick(Player player, Parkourability parkourability, IStamina stamina) {
    }

    @Override
    public final void onWorkingTickInClient(Player player, Parkourability parkourability, IStamina stamina) {
    }

    @Override
    public final void onWorkingTickInLocalClient(Player player, Parkourability parkourability, IStamina stamina) {
    }

    @Override
    public final void onWorkingTickInServer(Player player, Parkourability parkourability, IStamina stamina) {
    }

    @Override
    public final void onStop(Player player) {
    }

    @Override
    public final void onStopInServer(Player player) {
    }

    @Override
    public final void onStopInOtherClient(Player player) {
    }

    @Override
    public final void onStopInLocalClient(Player player) {
    }
}
