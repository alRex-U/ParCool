package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.network.LimitationByServerMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

//This class is mainly for client side.
//server side instance will be used just for individual data store, not be accessed in game
public class LimitationByServer {
	//for client side, whether this instance is synchronized by server
	private boolean haveReceived = false;
	//Whether this limitation is applied
	private boolean enforced = false;
	private int maxStaminaLimitation = Integer.MAX_VALUE;
	private boolean infiniteStaminaPermitted = true;
	private final ActionLimitation[] list = new ActionLimitation[ActionList.ACTIONS.size()];

	public LimitationByServer() {
		for (int i = 0; i < list.length; i++) {
			list[i] = new ActionLimitation(true, 0);
		}
	}

	public boolean isPermitted(Class<? extends Action> action) {
		if (!haveReceived) return false;
		if (!enforced) return true;
		ActionLimitation limitation = list[ActionList.getIndexOf(action)];
		if (limitation == null) return false;
		return limitation.isPossible();
	}

	public int getLeastStaminaConsumption(Class<? extends Action> action) {
		if (!haveReceived) return 0;
		if (!enforced) return 0;
		ActionLimitation limitation = list[ActionList.getIndexOf(action)];
		if (limitation == null) return 0;
		return limitation.getLeastStaminaConsumption();
	}

	public int getMaxStaminaLimitation() {
		if (!enforced) return Integer.MAX_VALUE;
		return maxStaminaLimitation;
	}

	public boolean isInfiniteStaminaPermitted() {
		return (!enforced || infiniteStaminaPermitted);
	}

	public void writeSyncData(LimitationByServerMessage msg) {
		msg.setEnforced(enforced);
		msg.setMaxStaminaLimitation(maxStaminaLimitation);
		msg.setPermissionOfInfiniteStamina(infiniteStaminaPermitted);
		ActionLimitation[] limitations = msg.getLimitations();
		for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
			limitations[i] = new ActionLimitation(
					list[i].isPossible(),
					list[i].getLeastStaminaConsumption()
			);
		}
	}

	public INBT writeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("enforced", enforced);
		nbt.putInt("max_stamina", maxStaminaLimitation);
		nbt.putBoolean("infinite_stamina_permitted", infiniteStaminaPermitted);
		ListNBT limitationList = new ListNBT();
		for (int i = 0; i < list.length; i++) {
			ActionLimitation limitation = list[i];
			if (limitation == null) continue;
			Class<? extends Action> action = ActionList.getByIndex(i);
			CompoundNBT actionNbt = new CompoundNBT();
			actionNbt.putString("action_name", action.getSimpleName());
			actionNbt.putBoolean("action_permitted", limitation.isPossible());
			actionNbt.putInt("action_stamina_consumption", limitation.getLeastStaminaConsumption());
			limitationList.add(actionNbt);
			nbt.putByte("list_type", limitationList.getElementType());
		}
		nbt.put("actions", limitationList);
		return nbt;
	}

	public void readNBT(INBT nbt) {
		if (nbt instanceof CompoundNBT) {
			CompoundNBT compoundNBT = (CompoundNBT) nbt;
			enforced = compoundNBT.getBoolean("enforced");
			maxStaminaLimitation = compoundNBT.getInt("max_stamina");
			infiniteStaminaPermitted = compoundNBT.getBoolean("infinite_stamina_permitted");
			for (INBT inbt : compoundNBT.getList("actions", compoundNBT.getByte("list_type"))) {
				if (!(inbt instanceof CompoundNBT)) {
					continue;
				}
				CompoundNBT actionNbt = (CompoundNBT) inbt;
				int i;
				String name = actionNbt.getString("action_name");
				for (i = 0; i < ActionList.ACTIONS.size(); i++) {
					if (name.equals(ActionList.getByIndex(i).getSimpleName())) {
						break;
					}
				}
				if (i == ActionList.ACTIONS.size()) continue;
				list[i] = new ActionLimitation(
						actionNbt.getBoolean("action_permitted"),
						actionNbt.getInt("action_stamina_consumption")
				);
			}
		} else {
			throw new IllegalArgumentException("NBT for LimitationByServer, is not CompoundNBT");
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void receive(LimitationByServerMessage msg) {
		haveReceived = true;
		enforced = msg.isEnforced();
		maxStaminaLimitation = msg.getMaxStaminaLimitation();
		infiniteStaminaPermitted = msg.getPermissionOfInfiniteStamina();
		for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
			list[i] = msg.getLimitations()[i];
		}
	}

	public static class IndividualLimitationChanger {
		@Nullable
		LimitationByServer instance = null;
		ServerPlayerEntity player;

		public IndividualLimitationChanger(ServerPlayerEntity player) {
			Parkourability parkourability = Parkourability.get(player);
			this.player = player;
			if (parkourability != null) {
				instance = parkourability.getActionInfo().getIndividualLimitation();
			}
		}

		public IndividualLimitationChanger setDefault() {
			if (instance == null) return this;
			instance.maxStaminaLimitation = Integer.MAX_VALUE;
			instance.enforced = false;
			instance.infiniteStaminaPermitted = true;
			for (int i = 0; i < instance.list.length; i++) {
				instance.list[i] = new ActionLimitation(true, 0);
			}
			return this;
		}

		public IndividualLimitationChanger setMaxStaminaLimitation(int value) {
			if (instance == null) return this;
			instance.maxStaminaLimitation = value;
			return this;
		}

		public IndividualLimitationChanger setEnforced(boolean value) {
			if (instance == null) return this;
			instance.enforced = value;
			return this;
		}

		public IndividualLimitationChanger setInfiniteStaminaPermission(boolean value) {
			if (instance == null) return this;
			instance.infiniteStaminaPermitted = value;
			return this;
		}

		public IndividualLimitationChanger setPossibilityOf(Class<? extends Action> action, boolean value) {
			if (instance == null) return this;
			ActionLimitation limitation = instance.list[ActionList.getIndexOf(action)];
			instance.list[ActionList.getIndexOf(action)] = new ActionLimitation(value, limitation.getLeastStaminaConsumption());
			return this;
		}

		public IndividualLimitationChanger setStaminaConsumptionOf(Class<? extends Action> action, int value) {
			if (instance == null) return this;
			ActionLimitation limitation = instance.list[ActionList.getIndexOf(action)];
			instance.list[ActionList.getIndexOf(action)] = new ActionLimitation(limitation.isPossible(), value);
			return this;
		}

		public void sync() {
			if (instance == null) return;
			LimitationByServerMessage.sendIndividualLimitation(player);
		}
	}
}
