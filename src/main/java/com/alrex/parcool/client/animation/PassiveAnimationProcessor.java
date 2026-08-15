package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@OnlyIn(Dist.CLIENT)
public class PassiveAnimationProcessor {
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onTick(PlayerTickEvent.Post event) {
        var player = event.getEntity();
        if (player instanceof IPlayerAnimatorHolder holder) {
            var animator = holder.getParCoolPlayerAnimator();
            if (!animator.isIdle()) return;
            if (player.getAbilities().flying) {
                animator.start(AnimationRegistries.get().animations().CREATIVE_FLY);
            }
        }
    }
}
