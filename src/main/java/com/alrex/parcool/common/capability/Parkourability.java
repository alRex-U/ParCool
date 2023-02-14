package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ActionPermission;
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

	private final ActionPermission permission = new ActionPermission();
	private final ActionInfo actionInfo = new ActionInfo();

	private final List<Action> actions = ActionList.constructActionsList();
	private final HashMap<Class<? extends Action>, Action> actionsMap;
	private final HashMap<Action, Short> idMap;

	public Parkourability() {
		actionsMap = new HashMap<>((int) (actions.size() * 1.5));
		idMap = new HashMap<>((int) (actions.size() * 1.5));
		for (short i = 0; i < actions.size(); i++) {
			Action action = actions.get(i);
			actionsMap.put(action.getClass(), action);
			idMap.put(action, i);
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
		return idMap.getOrDefault(instance, (short) -1);
	}

	@Nullable
	public Action getActionFromID(short id) {
		if (0 <= id && id < actions.size()) {
			return actions.get(id);
		}
		return null;
	}

	public ActionInfo getActionInfo() {
		return actionInfo;
	}

	public ActionPermission getPermission() {
		return permission;
	}

	public List<Action> getList() {
		return actions;
	}
}
