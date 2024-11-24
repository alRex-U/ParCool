package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.api.stamina.Stamina;

public class ParagliderStamina implements IStamina {
	public ParagliderStamina(Player player) {
		this.player = player;
	}

	final Player player;
	int old;

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
}
