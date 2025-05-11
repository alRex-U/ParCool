package com.alrex.parcool.common.attachment.common;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.action.AdditionalProperties;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.info.ServerLimitation;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class Parkourability {
	public static Parkourability get(Player player) {
		return player.getData(Attachments.PARKOURABILITY);
	}

    private final ActionInfo info;
	private final AdditionalProperties properties = new AdditionalProperties();
	private final BehaviorEnforcer enforcer = new BehaviorEnforcer();
	private final List<Action> actions = Actions.constructActionsList();
	private final HashMap<Class<? extends Action>, Action> actionsMap;
	private int synchronizeTrialCount = 0;

	public Parkourability() {
		actionsMap = new HashMap<>((int) (actions.size() * 1.5));
        for (Action action : actions) {
			actionsMap.put(action.getClass(), action);
		}
        info = new ActionInfo();
	}

	public <T extends Action> T get(Class<T> action) {
		T value = (T) actionsMap.getOrDefault(action, null);
		if (value == null) {
			throw new IllegalArgumentException("The Action instance is not registered:" + action.getSimpleName());
		}
		return value;
	}

	public short getActionID(Action instance) {
		return Actions.getIndexOf(instance.getClass());
	}

	@Nullable
	public Action getActionFromID(short id) {
		if (0 <= id && id < actions.size()) {
			return actions.get(id);
		}
		return null;
	}

	public AdditionalProperties getAdditionalProperties() {
		return properties;
	}

	public BehaviorEnforcer getBehaviorEnforcer() {
		return enforcer;
    }

	public ActionInfo getActionInfo() {
		return info;
	}

    public ClientSetting getClientInfo() {
        return info.getClientSetting();
	}

    public ServerLimitation getServerLimitation() {
        return info.getServerLimitation();
    }

	public List<Action> getList() {
		return actions;
	}

	public void CopyFrom(Parkourability original) {
        getActionInfo().setClientSetting(original.getActionInfo().getClientSetting());
        getActionInfo().setServerLimitation(original.getActionInfo().getServerLimitation());
	}

	public boolean isDoingNothing() {
		return actions.stream().noneMatch(Action::isDoing);
	}

	@OnlyIn(Dist.CLIENT)
	public void trySyncLimitation(LocalPlayer player, Parkourability parkourability) {
		synchronizeTrialCount++;
		PacketDistributor.sendToServer(new ClientInformationPayload(player.getUUID(), true, parkourability.getClientInfo()));
	}

	@OnlyIn(Dist.CLIENT)
	public int getSynchronizeTrialCount() {
		return synchronizeTrialCount;
	}

	public void incrementSynchronizeTrialCount() {
		synchronizeTrialCount++;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean limitationIsNotSynced() {
		return !getServerLimitation().isSynced();
	}

	@SafeVarargs
	public final Boolean isDoingAny(Class<? extends Action>... actions) {
		for (Class<? extends Action> action : actions) {
			if (get(action).isDoing()) {
				return true;
			}
		}

		return false;
	}
}
