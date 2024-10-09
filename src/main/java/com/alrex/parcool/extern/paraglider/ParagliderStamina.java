package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.forge.capability.PlayerMovementProvider;
import tictim.paraglider.impl.movement.PlayerMovement;

public class ParagliderStamina implements IStamina {
	public ParagliderStamina(Player player) {
		this.player = player;
	}

	final Player player;
	int old;
	private int consumedBuffer = 0;

	private Stamina getInternalInstance() {
		return Stamina.get(player);
	}

	@Override
	public int getActualMaxStamina() {
		return getInternalInstance().stamina();
	}

	@Override
	public int get() {
		return getInternalInstance().stamina();
	}

	@Override
	public int getOldValue() {
		return old;
	}

	@Override
	public void consume(int value) {}

	@Override
	public void recover(int value) {
	}

	@Override
	public boolean isExhausted() {
		return getInternalInstance().isDepleted();
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
	public boolean wantToConsumeOnServer() {
		return consumedBuffer != 0;
	}

	@Override
	public int getRequestedValueConsumedOnServer() {
		int neededValue = consumedBuffer;
		consumedBuffer = 0;
		return neededValue;
	}

	public static void consumeOnServer(ServerPlayer player, int value) {
		Stamina stamina = Stamina.get(player);
		stamina.takeStamina(value, false, false);
	}
}
