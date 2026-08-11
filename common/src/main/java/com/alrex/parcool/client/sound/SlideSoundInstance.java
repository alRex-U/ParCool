package com.alrex.parcool.client.sound;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.client.animation.system.util.EntityUtil;
import com.alrex.parcool.common.action.impl.Slide;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class SlideSoundInstance extends ActionLoopSoundInstance<Slide> {
    public SlideSoundInstance(LocalPlayer player, Slide action) {
        super(player, action, ParCoolSoundEvents.SLIDE.get());
    }

    @Override
    protected void tickInAlive() {
        var speed = EntityUtil.getPositionDifference(player).lengthSqr();
        if (speed >= 1.0e-7) {
            this.volume = (float) Mth.clamp(speed * 1.5, 0.4, 1.0);
        } else {
            this.volume = 0.0F;
        }
    }
}
