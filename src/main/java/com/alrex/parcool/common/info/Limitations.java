package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.network.SyncLimitationMessage;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;
import java.util.EnumMap;

//This class is mainly for client side.
//server side instance will be used just for individual data store, not be accessed in game
public class Limitations {
	//for client side, whether this instance is synchronized by server
	private boolean haveReceived = false;
	//Whether this limitation is applied
	private boolean enabled = false;
	private final EnumMap<ParCoolConfig.Server.Booleans, Boolean> booleans = new EnumMap<>(ParCoolConfig.Server.Booleans.class);
	private final EnumMap<ParCoolConfig.Server.Integers, Integer> integers = new EnumMap<>(ParCoolConfig.Server.Integers.class);
	private final EnumMap<ParCoolConfig.Server.Doubles, Double> doubles = new EnumMap<>(ParCoolConfig.Server.Doubles.class);
	private final ActionLimitation[] actionLimitations = new ActionLimitation[ActionList.ACTIONS.size()];

	public Limitations() {
		for (int i = 0; i < actionLimitations.length; i++) {
			actionLimitations[i] = new ActionLimitation(true, 0);
		}
	}

	public boolean isReceived() {
		return haveReceived;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isPermitted(Class<? extends Action> action) {
		if (!haveReceived) return false;
		if (!enabled) return true;
		ActionLimitation limitation = actionLimitations[ActionList.getIndexOf(action)];
		if (limitation == null) return false;
		return limitation.isPossible();
	}

	public int getLeastStaminaConsumption(Class<? extends Action> action) {
		if (!haveReceived) return 0;
		if (!enabled) return 0;
		ActionLimitation limitation = actionLimitations[ActionList.getIndexOf(action)];
		if (limitation == null) return 0;
		return limitation.getLeastStaminaConsumption();
	}

	public boolean get(ParCoolConfig.Server.Booleans item) {
		if (!haveReceived) return !item.DefaultValue;
		if (!enabled) return item.DefaultValue;
		Boolean value = booleans.get(item);
		if (value == null) {
			return item.DefaultValue;
		}
		return value;
	}

	public int get(ParCoolConfig.Server.Integers item) {
		if (!haveReceived) return 0;
		if (!enabled) return item.DefaultValue;
		Integer value = integers.get(item);
		if (value == null) {
			return item.DefaultValue;
		}
		return value;
	}

	public double get(ParCoolConfig.Server.Doubles item) {
		if (!haveReceived) return 0;
		if (!enabled) return item.DefaultValue;
		Double value = doubles.get(item);
		if (value == null) {
			return item.DefaultValue;
		}
		return value;
	}

	public boolean isInfiniteStaminaPermitted() {
		return (!enabled ||
				booleans.get(ParCoolConfig.Server.Booleans.AllowInfiniteStamina));
	}

	public void readFromServerConfig() {
		for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
			booleans.put(item, item.get());
		}
		for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
			integers.put(item, item.get());
		}
		for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
			doubles.put(item, item.get());
		}
		for (int i = 0; i < actionLimitations.length; i++) {
			actionLimitations[i] = new ActionLimitation(
					ParCoolConfig.Server.getPermissionOf(ActionList.getByIndex(i)),
					ParCoolConfig.Server.getLeastStaminaConsumptionOf(ActionList.getByIndex(i))
			);
		}
	}

	public void setReceived() {
		haveReceived = true;
	}

	public void writeTo(ByteBuffer buffer) {
		buffer.put((byte) (enabled ? 1 : 0));
		for (ActionLimitation limitation : actionLimitations) {
			buffer.put((byte) (limitation.isPossible() ? 1 : 0))
					.putInt(limitation.getLeastStaminaConsumption());
		}
		for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
			buffer.put((byte) (get(item) ? 1 : 0));
		}
		for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
			buffer.putInt(get(item));
		}
		for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
			buffer.putDouble(get(item));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void readFrom(ByteBuffer buffer) {
		enabled = buffer.get() != 0;
		for (int i = 0; i < actionLimitations.length; i++) {
			actionLimitations[i] = new ActionLimitation(buffer.get() != 0, buffer.getInt());
		}
		for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
			booleans.put(item, buffer.get() != 0);
		}
		for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
			integers.put(item, buffer.getInt());
		}
		for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
			doubles.put(item, buffer.getDouble());
		}
		setReceived();
	}

	public Tag writeTag() {
		CompoundTag nbt = new CompoundTag();
		nbt.putBoolean("limitation_imposed", enabled);
		for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
			nbt.putBoolean(item.Path, get(item));
		}
		for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
			nbt.putInt(item.Path, get(item));
		}
		for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
			nbt.putDouble(item.Path, get(item));
		}

		ListTag limitationList = new ListTag();
		for (int i = 0; i < actionLimitations.length; i++) {
			ActionLimitation limitation = actionLimitations[i];
			if (limitation == null) continue;
			Class<? extends Action> action = ActionList.getByIndex(i);
			CompoundTag actionNbt = new CompoundTag();
			actionNbt.putString("action_name", action.getSimpleName());
			actionNbt.putBoolean("action_permitted", limitation.isPossible());
			actionNbt.putInt("action_stamina_consumption", limitation.getLeastStaminaConsumption());
			limitationList.add(actionNbt);
			nbt.putByte("list_type", limitationList.getElementType());
		}
		nbt.put("actions", limitationList);
		return nbt;
	}

	public void readTag(Tag nbt) {
		if (nbt instanceof CompoundTag) {
			CompoundTag compoundNBT = (CompoundTag) nbt;
			enabled = compoundNBT.getBoolean("limitation_imposed");
			for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
				booleans.put(item, compoundNBT.getBoolean(item.Path));
			}
			for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
				integers.put(item, compoundNBT.getInt(item.Path));
			}
			for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
				doubles.put(item, compoundNBT.getDouble(item.Path));
			}
			for (Tag inbt : compoundNBT.getList("actions", compoundNBT.getByte("list_type"))) {
				if (!(inbt instanceof CompoundTag)) {
					continue;
				}
				CompoundTag actionNbt = (CompoundTag) inbt;
				int i;
				String name = actionNbt.getString("action_name");
				for (i = 0; i < ActionList.ACTIONS.size(); i++) {
					if (name.equals(ActionList.getByIndex(i).getSimpleName())) {
						break;
					}
				}
				if (i == ActionList.ACTIONS.size()) continue;
				actionLimitations[i] = new ActionLimitation(
						actionNbt.getBoolean("action_permitted"),
						actionNbt.getInt("action_stamina_consumption")
				);
			}
		} else {
			throw new IllegalArgumentException("NBT for Limitation, is not CompoundTag");
		}
	}

	public static class Changer {
		public static Changer get(ServerPlayer player) {
			return new Changer(Parkourability.get(player).getActionInfo().getIndividualLimitation(), player);
		}

		final Limitations instance;
		final ServerPlayer player;

		Changer(Limitations limitations, ServerPlayer player) {
			instance = limitations;
			this.player = player;
		}

		public Changer set(ParCoolConfig.Server.Booleans item, boolean value) {
			instance.booleans.put(item, value);
			return this;
		}

		public Changer set(ParCoolConfig.Server.Integers item, int value) {
			instance.integers.put(item, value);
			return this;
		}

		public Changer set(ParCoolConfig.Server.Doubles item, double value) {
			instance.doubles.put(item, value);
			return this;
		}

		public Changer setEnabled(boolean value) {
			instance.enabled = value;
			return this;
		}

		public Changer setPossibilityOf(Class<? extends Action> action, boolean value) {
			int index = ActionList.getIndexOf(action);
			instance.actionLimitations[index] =
					new ActionLimitation(
							value,
							instance.actionLimitations[index].getLeastStaminaConsumption()
					);
			return this;
		}

		public Changer setLeastStaminaConsumption(Class<? extends Action> action, int value) {

			int index = ActionList.getIndexOf(action);
			instance.actionLimitations[index] =
					new ActionLimitation(
							instance.actionLimitations[index].isPossible(),
							value
					);
			return this;
		}

		public Changer setAllDefault() {
			for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
				instance.booleans.put(item, item.DefaultValue);
			}
			for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
				instance.integers.put(item, item.DefaultValue);
			}
			for (int i = 0; i < instance.actionLimitations.length; i++) {
				instance.actionLimitations[i] = new ActionLimitation(true, 0);
			}
			return this;
		}

		public void sync() {
			if (instance == null) return;
			SyncLimitationMessage.sendIndividualLimitation(player);
		}
	}
}
