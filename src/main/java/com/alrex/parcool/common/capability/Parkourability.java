package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.action.AdditionalProperties;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.info.ActionInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class Parkourability {
	@Nullable
	public static Parkourability get(PlayerEntity player) {
		LazyOptional<Parkourability> optional = player.getCapability(Capabilities.PARKOURABILITY_CAPABILITY);
		return optional.orElse(null);
	}

	private final ActionInfo info = new ActionInfo();
	private final AdditionalProperties properties = new AdditionalProperties();

	private final List<Action> actions = ActionList.constructActionsList();
	private final HashMap<Class<? extends Action>, Action> actionsMap;

	public Parkourability() {
		actionsMap = new HashMap<>((int) (actions.size() * 1.5));
		for (short i = 0; i < actions.size(); i++) {
			Action action = actions.get(i);
			actionsMap.put(action.getClass(), action);
		}
	}

	public <T extends Action> T get(Class<T> action) {
		T value = (T) actionsMap.getOrDefault(action, null);
		if (value == null) {
			throw new IllegalArgumentException("The Action instance is not registered:" + action.getSimpleName());
		}
		return value;
	}

	public short getActionID(Action instance) {
		return ActionList.getIndexOf(instance.getClass());
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

	public ActionInfo getActionInfo() {
		return info;
	}

	public List<Action> getList() {
		return actions;
	}
}
