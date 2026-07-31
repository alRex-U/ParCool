package com.alrex.parcool.client.animation.system;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IPlayerAnimatorHolder {
    PlayerAnimator getParCoolPlayerAnimator();
}
