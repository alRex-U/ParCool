package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class PassiveAnimationProcessor {
    public static void onTick(Player player) {
        if (player instanceof IPlayerAnimatorHolder holder) {
            var animator = holder.getParCoolPlayerAnimator();
            if (!animator.isIdle()) return;
            if (player.getAbilities().flying) {
                animator.start(AnimationRegistries.get().animations().CREATIVE_FLY);
            }
        }
    }
}
