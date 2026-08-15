package com.alrex.parcool.client.sound;

import com.alrex.parcool.common.action.impl.Grapple;
import com.alrex.parcool.common.grapple.GrapplePhase;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrappleSwingSoundInstance extends ActionLoopSoundInstance<Grapple> {
    private static final double FULL_VOLUME_SPEED = 1.4;
    private static final float MAX_VOLUME = 0.7f;

    public GrappleSwingSoundInstance(LocalPlayer player, Grapple grapple) {
        super(player, grapple, SoundEvents.ELYTRA_FLYING);
    }

    @Override
    protected void tickInAlive() {
        if (action.getPhase() != GrapplePhase.ATTACHED) {
            this.volume = 0f;
            return;
        }
        double speed = action.getVelocity().length();
        this.volume = (float) Mth.clamp(speed / FULL_VOLUME_SPEED, 0.0, 1.0) * MAX_VOLUME;
        this.pitch = (float) Mth.clamp(0.7 + speed / FULL_VOLUME_SPEED * 0.5, 0.7, 1.5);
    }
}
