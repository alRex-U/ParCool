package com.alrex.parcool.client.animation.system;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IPlayerAnimatorHolder {
    PlayerAnimator getParCoolPlayerAnimator();
}
