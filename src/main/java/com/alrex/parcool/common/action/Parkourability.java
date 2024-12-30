package com.alrex.parcool.common.action;

import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.info.ServerLimitation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public class Parkourability {
	public static class Registry {
		private static final TreeMap<UUID, Parkourability> mapClient = new TreeMap<>();
		private static final TreeMap<UUID, Parkourability> mapServer = new TreeMap<>();

		@Nullable
		private static Parkourability get(Player player) {
			return player.level().isClientSide() ? mapClient.get(player.getUUID()) : mapServer.get(player.getUUID());
		}

		public static void setupInClient(UUID id) {
			mapClient.putIfAbsent(id, new Parkourability());
		}

		public static void setupInServer(UUID id) {
			mapServer.putIfAbsent(id, new Parkourability());
		}

		public static void unloadInClient(UUID id) {
			mapClient.remove(id);
		}

		public static void unloadInServer(UUID id) {
			mapServer.remove(id);
		}
	}
	@Nullable
	public static Parkourability get(Player player) {
		return Registry.get(player);
	}

    private final ActionInfo info;
	private final AdditionalProperties properties = new AdditionalProperties();
    private final CancelMarks cancelMarks = new CancelMarks();
	private final List<Action> actions = Actions.constructActionsList();
	private final HashMap<Class<? extends Action>, Action> actionsMap;

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

    public CancelMarks getCancelMarks() {
        return cancelMarks;
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
}
