package com.alrex.parcool.client.sound;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.client.animation.system.util.EntityUtil;
import com.alrex.parcool.common.action.impl.SlideDown;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlideDownSoundInstance extends ActionLoopSoundInstance<SlideDown> {
    public SlideDownSoundInstance(LocalPlayer player, SlideDown action) {
        super(player, action, ParCoolSoundEvents.SLIDE_DOWN.get());
    }

    @Override
    protected void tickInAlive() {
        var speed = EntityUtil.getPositionDifference(player).lengthSqr();
        if (speed >= 1.0e-7) {
            this.volume = (float) Mth.clamp(speed / 2.0, 0.25, 1.0);
        } else {
            this.volume = 0.0F;
        }
    }
}
