package com.alrex.parcool.extern.feathers;

import com.alrex.parcool.common.capability.IStamina;
import com.elenai.feathers.api.FeathersHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class FeathersStamina implements IStamina {
    private static final int MAX_FEATHERS = 20;

    private final Player player;
    private int old;
    private float fraction = 0;

    public FeathersStamina(Player player) {
        this.player = player;
    }

    @Override
    public int getActualMaxStamina() {
        return MAX_FEATHERS;
    }

    @Override
    public int get() {
        if (player.isLocalPlayer()) {
            return FeathersHelper.getFeathers();
        } else if (player instanceof ServerPlayer serverPlayer) {
            return FeathersHelper.getFeathers(serverPlayer);
        }
        return 1;
    }

    @Override
    public int getOldValue() {
        return old;
    }

    @Override
    public void consume(int value) {
        if (player.isLocalPlayer()) {
            int spentFeathers = value / 100;
            fraction += (value / 100f) - spentFeathers;
            if (fraction >= 1) {
                fraction -= 1;
                FeathersHelper.spendFeathers(spentFeathers + 1);
            } else {
                FeathersHelper.spendFeathers(spentFeathers);
            }
        }
    }

    @Override
    public void recover(int value) {
        fraction -= value / 100f;
        if (fraction < 0) {
            fraction = 0;
        }
    }

    @Override
    public boolean isExhausted() {
        return player.isLocalPlayer() && !FeathersHelper.checkFeathersRemaining();
    }

    @Override
    public void setExhaustion(boolean value) {
    }

    @Override
    public void tick() {
        old = get();
    }

	@Override
	public void set(int value) {
	}

	@Override
	public boolean isImposingExhaustionPenalty() {
		return false;
	}
}
