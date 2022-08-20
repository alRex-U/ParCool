package com.alrex.parcool.common.info;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.utilities.BufferUtil;

import java.nio.ByteBuffer;

import static com.alrex.parcool.ParCoolConfig.CONFIG_CLIENT;
import static com.alrex.parcool.ParCoolConfig.CONFIG_SERVER;
import static java.lang.Math.max;

public class ActionInfo {
	private boolean received = false;
	private boolean allowedInfiniteStamina = false;
	private int staminaMax;
	private int staminaConsumptionBreakfall = 0;
	private int staminaConsumptionCatLeap = 0;
	private int staminaConsumptionClingToCliff = 0;
	private int staminaConsumptionClimbUp = 0;
	private int staminaConsumptionDodge = 0;
	private int staminaConsumptionFastRun = 0;
	private int staminaConsumptionFlipping = 0;
	private int staminaConsumptionVault = 0;
	private int staminaConsumptionWallJump = 0;

	public int getStaminaConsumptionBreakfall() {
		return max(CONFIG_CLIENT.staminaConsumptionBreakfall.get(), staminaConsumptionBreakfall);
	}

	public int getStaminaConsumptionFlipping() {
		return max(CONFIG_CLIENT.staminaConsumptionFlipping.get(), staminaConsumptionFlipping);
	}

	public int getStaminaConsumptionVault() {
		return max(CONFIG_CLIENT.staminaConsumptionVault.get(), staminaConsumptionVault);
	}

	public int getStaminaConsumptionCatLeap() {
		return max(CONFIG_CLIENT.staminaConsumptionCatLeap.get(), staminaConsumptionCatLeap);
	}

	public int getStaminaConsumptionClingToCliff() {
		return max(CONFIG_CLIENT.staminaConsumptionClingToCliff.get(), staminaConsumptionClingToCliff);
	}

	public int getStaminaConsumptionClimbUp() {
		return max(CONFIG_CLIENT.staminaConsumptionClimbUp.get(), staminaConsumptionClimbUp);
	}

	public int getStaminaConsumptionDodge() {
		return max(CONFIG_CLIENT.staminaConsumptionDodge.get(), staminaConsumptionDodge);
	}

	public int getStaminaConsumptionFastRun() {
		return max(CONFIG_CLIENT.staminaConsumptionFastRun.get(), staminaConsumptionFastRun);
	}

	public int getStaminaConsumptionWallJump() {
		return max(CONFIG_CLIENT.staminaConsumptionWallJump.get(), staminaConsumptionWallJump);
	}

	public int getMaxStamina() {
		return max(CONFIG_CLIENT.staminaMax.get(), staminaMax);
	}

	public double getCatLeapPower() {
		return 0.49;
	}

	public int getDodgeCoolTick() {
		return 10;
	}

	public int getMaxSlidingTick() {
		return 15;
	}

	public boolean isStaminaInfinite() {
		return ParCoolConfig.CONFIG_CLIENT.infiniteStamina.get() && allowedInfiniteStamina;
	}

	public static void encode(ByteBuffer buffer) {
		buffer.putInt(CONFIG_SERVER.staminaConsumptionBreakfall.get());
		buffer.putInt(CONFIG_SERVER.staminaConsumptionCatLeap.get());
		buffer.putInt(CONFIG_SERVER.staminaConsumptionClimbUp.get());
		buffer.putInt(CONFIG_SERVER.staminaConsumptionDodge.get());
		buffer.putInt(CONFIG_SERVER.staminaConsumptionClingToCliff.get());
		buffer.putInt(CONFIG_SERVER.staminaConsumptionFastRun.get());
		buffer.putInt(CONFIG_SERVER.staminaConsumptionFlipping.get());
		buffer.putInt(CONFIG_SERVER.staminaConsumptionVault.get());
		buffer.putInt(CONFIG_SERVER.staminaConsumptionWallJump.get());
		buffer.putInt(CONFIG_SERVER.staminaMax.get());
		BufferUtil.wrap(buffer).putBoolean(CONFIG_SERVER.allowInfiniteStamina.get());
	}

	public void decode(ByteBuffer buffer) {
		staminaConsumptionBreakfall = buffer.getInt();
		staminaConsumptionCatLeap = buffer.getInt();
		staminaConsumptionClimbUp = buffer.getInt();
		staminaConsumptionDodge = buffer.getInt();
		staminaConsumptionClingToCliff = buffer.getInt();
		staminaConsumptionFastRun = buffer.getInt();
		staminaConsumptionFlipping = buffer.getInt();
		staminaConsumptionVault = buffer.getInt();
		staminaConsumptionWallJump = buffer.getInt();
		staminaMax = buffer.getInt();
		allowedInfiniteStamina = BufferUtil.getBoolean(buffer);
	}

	public void receiveServerPermissions(ByteBuffer buffer) {
		received = true;
		decode(buffer);
	}
}
