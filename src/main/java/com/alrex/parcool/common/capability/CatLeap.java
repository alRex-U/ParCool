package com.alrex.parcool.common.capability;

import com.alrex.parcool.client.input.KeyBindings;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

public class CatLeap implements ICatLeap {
	private boolean leaping = false;
	private boolean ready = false;
	private int readyTime = 0;

	@Override
	public boolean canCatLeap(ClientPlayerEntity player) {
		IStamina stamina;
		{
			LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
			if (!staminaOptional.isPresent()) return false;
			stamina = staminaOptional.resolve().get();
		}
		return player.isOnGround() && !stamina.isExhausted() && ready && readyTime < 10 && !KeyBindings.getKeySneak().isKeyDown();
	}

	@Override
	public boolean canReadyLeap(ClientPlayerEntity player) {
		IFastRunning fastRunning;
		{
			LazyOptional<IFastRunning> fastRunningOptional = player.getCapability(IFastRunning.FastRunningProvider.FAST_RUNNING_CAPABILITY);
			if (!fastRunningOptional.isPresent()) return false;
			fastRunning = fastRunningOptional.resolve().get();
		}
		return (fastRunning.getNotRunningTime() < 10 && KeyBindings.getKeySneak().isKeyDown()) || (ready && KeyBindings.getKeySneak().isKeyDown() && readyTime < 10);
	}

	@Override
	public double getBoostValue(ClientPlayerEntity player) {
		return 0.49;
	}

	@Override
	public boolean isLeaping() {
		return leaping;
	}

	@Override
	public void setLeaping(boolean leaping) {
		this.leaping = leaping;
	}

	@Override
	public int getReadyTime() {
		return readyTime;
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	@Override
	public void updateReadyTime() {
		if (ready) readyTime++;
		else readyTime = 0;
	}

	@Override
	public int getStaminaConsumption() {
		return 200;
	}
}
