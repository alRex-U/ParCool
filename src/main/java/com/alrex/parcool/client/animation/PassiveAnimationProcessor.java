package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class PassiveAnimationProcessor {
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        var player = event.player;
        if (player instanceof IPlayerAnimatorHolder holder) {
            var animator = holder.getParCoolPlayerAnimator();
            if (!animator.isIdle()) return;
            if (player.getAbilities().flying) {
                animator.start(AnimationRegistries.get().animations().CREATIVE_FLY);
            }
        }
    }
}
