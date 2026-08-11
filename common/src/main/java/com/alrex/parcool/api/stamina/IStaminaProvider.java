package com.alrex.parcool.api.stamina;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface IStaminaProvider<T extends AbstractLocalStamina> {
    T newInstance(Player owner, @Nullable AbstractLocalStamina old);
}
