package com.alrex.parcool.client.sound;

import com.alrex.parcool.api.action.ContinuableAction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

@Environment(EnvType.CLIENT)
public abstract class ActionLoopSoundInstance<A extends ContinuableAction> extends AbstractTickableSoundInstance {
    private static final byte MAX_FADEOUT = 6;
    protected final A action;
    protected final LocalPlayer player;
    private byte fadeout;

    public ActionLoopSoundInstance(LocalPlayer player, A action, SoundEvent soundEvent) {
        super(soundEvent, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.action = action;
        this.player = player;
        this.volume = 0.01f;
        this.looping = true;
        this.delay = 0;
        this.fadeout = -1;
    }

    @Override
    public void tick() {
        if (fadeout >= MAX_FADEOUT) {
            stop();
            return;
        }
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        if (fadeout >= 0) {
            fadeout++;
            volume *= 0.9f;
        } else {
            if (action.isDoing()) {
                tickInAlive();
            } else {
                fadeout = 0;
            }
        }
    }

    protected abstract void tickInAlive();
}
