package com.alrex.parcool.client.sound;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.client.animation.system.util.EntityUtil;
import com.alrex.parcool.common.action.impl.Dive;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiveSoundInstance extends ActionLoopSoundInstance<Dive> {
    public DiveSoundInstance(LocalPlayer player, Dive action) {
        super(player, action, ParCoolSoundEvents.DIVE.get());
    }

    @Override
    protected void tickInAlive() {
        var speed = EntityUtil.getPositionDifference(player).lengthSqr();
        if (speed >= 1.0e-7) {
            this.volume = (float) Mth.clamp(speed / 3.0, 0.0, 1.0);
        } else {
            this.volume = 0.0F;
        }
    }
}
