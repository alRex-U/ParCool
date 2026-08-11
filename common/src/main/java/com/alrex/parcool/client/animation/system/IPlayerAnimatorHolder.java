package com.alrex.parcool.client.animation.system;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IPlayerAnimatorHolder {
    PlayerAnimator getParCoolPlayerAnimator();
}
