package com.alrex.parcool.extern.feathers;

import com.alrex.parcool.common.capability.IStamina;
import com.elenai.feathers.api.FeathersHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class FeathersStamina implements IStamina {
	private static final int MAX_FEATHERS = 20;

	private final Player player;
	private int old;

	public FeathersStamina(Player player) {
		this.player = player;
	}

	@Override
	public int getMaxStamina() {
		return MAX_FEATHERS;
	}

	@Override
	public int getActualMaxStamina() {
		return MAX_FEATHERS;
	}

	@Override
	public void setMaxStamina(int value) {
	}

	@Override
	public int get() {
		if (player.isLocalPlayer()) {
			return FeathersHelper.getFeathers();
		} else if (player instanceof ServerPlayer serverPlayer) {
			FeathersHelper.getFeathers(serverPlayer);
		}
		return getMaxStamina();
	}

	@Override
	public int getOldValue() {
		return old;
	}

	@Override
	public void consume(int value) {
		if (player.isLocalPlayer()) {
			FeathersHelper.spendFeathers(Math.round(value / 100f));
		}
	}

	@Override
	public void recover(int value) {
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
}