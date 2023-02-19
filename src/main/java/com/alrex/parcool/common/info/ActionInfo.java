package com.alrex.parcool.common.info;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.network.LimitationByServerMessage;
import net.minecraft.nbt.INBT;

public class ActionInfo {
	private final LimitationByServer serverLimitation = new LimitationByServer();
	private final LimitationByServer individualLimitation = new LimitationByServer();

	public LimitationByServer getIndividualLimitation() {
		return individualLimitation;
	}

	public LimitationByServer getServerLimitation() {
		return serverLimitation;
	}

	public boolean can(Class<? extends Action> action) {
		return ParCool.isActive()
				&& ParCoolConfig.CONFIG_CLIENT.getPossibilityOf(action).get()
				&& serverLimitation.isPermitted(action)
				&& individualLimitation.isPermitted(action);
	}

	public int getStaminaConsumptionOf(Class<? extends Action> action) {
		int value = ParCoolConfig.CONFIG_CLIENT.getStaminaConsumptionOf(action).get();
		value = Math.max(value, serverLimitation.getLeastStaminaConsumption(action));
		value = Math.max(value, individualLimitation.getLeastStaminaConsumption(action));
		return value;
	}

	public int getMaxStaminaLimitation() {
		return Math.min(serverLimitation.getMaxStaminaLimitation(), individualLimitation.getMaxStaminaLimitation());
	}

	public boolean isInfiniteStaminaPermitted() {
		return serverLimitation.isInfiniteStaminaPermitted()
				&& individualLimitation.isInfiniteStaminaPermitted();
	}

	public void readNBT(INBT inbt) {
		individualLimitation.readNBT(inbt);
	}

	public INBT writeNBT() {
		return individualLimitation.writeNBT();
	}

	public void receiveLimitation(LimitationByServerMessage msg) {
		serverLimitation.receive(msg);
	}

	public void receiveIndividualLimitation(LimitationByServerMessage msg) {
		individualLimitation.receive(msg);
	}
}
