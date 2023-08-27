package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;
import java.util.EnumMap;

public class ClientInformation {
	private boolean synced = false;
	private final boolean[] actionPossibilities = new boolean[ActionList.ACTIONS.size()];
	private final int[] staminaConsumptions = new int[ActionList.ACTIONS.size()];
	private final EnumMap<ParCoolConfig.Client.Booleans, Boolean> booleans = new EnumMap<>(ParCoolConfig.Client.Booleans.class);
	private final EnumMap<ParCoolConfig.Client.Integers, Integer> integers = new EnumMap<>(ParCoolConfig.Client.Integers.class);
	private final EnumMap<ParCoolConfig.Client.Doubles, Double> doubles = new EnumMap<>(ParCoolConfig.Client.Doubles.class);


	public boolean getPossibilityOf(Class<? extends Action> action) {
		if (!synced) return false;
		return actionPossibilities[ActionList.getIndexOf(action)];
	}

	public int getStaminaConsumptionOf(Class<? extends Action> action) {
		if (!synced) return 0;
		return staminaConsumptions[ActionList.getIndexOf(action)];
	}

	public Boolean get(ParCoolConfig.Client.Booleans item) {
		if (!synced) return item.DefaultValue;
		Boolean value = booleans.get(item);
		return value == null ? item.DefaultValue : value;
	}

	public Integer get(ParCoolConfig.Client.Integers item) {
		if (!synced) return item.DefaultValue;
		Integer value = integers.get(item);
		return value == null ? item.DefaultValue : value;
	}

	public Double get(ParCoolConfig.Client.Doubles item) {
		if (!synced) return item.DefaultValue;
		Double value = doubles.get(item);
		return value == null ? item.DefaultValue : value;
	}

	@OnlyIn(Dist.CLIENT)
	public void readFromLocalConfig() {
		for (int i = 0; i < actionPossibilities.length; i++) {
			actionPossibilities[i] = ParCoolConfig.Client.getPossibilityOf(ActionList.ACTIONS.get(i)).get();
			staminaConsumptions[i] = ParCoolConfig.Client.getStaminaConsumptionOf(ActionList.ACTIONS.get(i)).get();
		}
		for (ParCoolConfig.Client.Booleans item : ParCoolConfig.Client.Booleans.values()) {
			booleans.put(item, item.get());
		}
		for (ParCoolConfig.Client.Integers item : ParCoolConfig.Client.Integers.values()) {
			integers.put(item, item.get());
		}
		for (ParCoolConfig.Client.Doubles item : ParCoolConfig.Client.Doubles.values()) {
			doubles.put(item, item.get());
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void writeTo(ByteBuffer buffer) {
		for (boolean possibility : actionPossibilities) {
			buffer.put((byte) (possibility ? 1 : 0));
		}
		for (int staminaConsumption : staminaConsumptions) {
			buffer.putInt(staminaConsumption);
		}
		for (ParCoolConfig.Client.Booleans item : ParCoolConfig.Client.Booleans.values()) {
			item.writeToBuffer(buffer);
		}
		for (ParCoolConfig.Client.Integers item : ParCoolConfig.Client.Integers.values()) {
			item.writeToBuffer(buffer);
		}
		for (ParCoolConfig.Client.Doubles item : ParCoolConfig.Client.Doubles.values()) {
			item.writeToBuffer(buffer);
		}
	}

	public void readFrom(ByteBuffer buffer) {
		for (int i = 0; i < actionPossibilities.length; i++) {
			actionPossibilities[i] = buffer.get() != 0;
		}
		for (int i = 0; i < staminaConsumptions.length; i++) {
			staminaConsumptions[i] = buffer.getInt();
		}
		for (ParCoolConfig.Client.Booleans item : ParCoolConfig.Client.Booleans.values()) {
			booleans.put(item, item.readFromBuffer(buffer));
		}
		for (ParCoolConfig.Client.Integers item : ParCoolConfig.Client.Integers.values()) {
			integers.put(item, item.readFromBuffer(buffer));
		}
		for (ParCoolConfig.Client.Doubles item : ParCoolConfig.Client.Doubles.values()) {
			doubles.put(item, item.readFromBuffer(buffer));
		}
	}

	public void setSynced(boolean status) {
		synced = status;
	}
}
