package com.alrex.parcool.common;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.StaminaConsumption;
import com.alrex.parcool.api.stamina.IReadableStamina;
import com.alrex.parcool.common.action.*;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import com.alrex.parcool.common.stamina.StaminaTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.TreeMap;

public class Parkourability {
	public static Parkourability get(Player player) {
		if (player instanceof IParkourabilityHolder holder) {
			return holder.getParkourability();
		}
		return null;
	}

	private final AdditionalProperties properties;
	private final BehaviorEnforcer enforcer = new BehaviorEnforcer();
	private final ActionSet actions;
	private final TreeMap<ActionEntry<?>, Object> requestedContexts = new TreeMap<>();
	private final Player player;
	private final ActionCapabilities capabilities;
	private final ActionCapabilities enabledActionStates;
	private IReadableStamina stamina;

	public Parkourability(Player player, ActionRegistry registry) {
		this.player = player;
		this.actions = new ActionSet(this, registry);
		this.properties = new AdditionalProperties(player);
		this.capabilities = new ActionCapabilities(registry, false);
		this.enabledActionStates = new ActionCapabilities(registry, true);
		if (player.isLocalPlayer()) {
			var staminaProvider = ParCool.getStaminaTypeRegistry().getRegistry().get(ParCool.getConfig().server().getStaminaTypeID());
			if (staminaProvider == null) {
				staminaProvider = ParCool.getStaminaTypeRegistry().getRegistry().get(StaminaTypes.PARCOOL_STAMINA.id());
			}
			this.stamina = staminaProvider.provider().newInstance(player, null);
		} else {
			this.stamina = ReadonlyStamina.DEFAULT;
		}
	}

	public <T extends Action> T get(ActionEntry<T> entry) {
		return actions.get(entry);
	}

	public ActionSet getActions() {
		return actions;
	}

	public Player player() {
		return player;
	}

	public AdditionalProperties getAdditionalProperties() {
		return properties;
	}

	public BehaviorEnforcer getBehaviorEnforcer() {
		return enforcer;
	}

	public IReadableStamina getStamina() {
		return stamina;
	}

    public ActionCapabilities getCapabilities() {
        return capabilities;
    }

	public ActionCapabilities getEnabledActions() {
		return enabledActionStates;
	}

	public void updateStaminaInRemote(ReadonlyStamina newStamina) {
		if (stamina instanceof ReadonlyStamina) {
			stamina = newStamina;
		}
	}

    public void updateActionCapability(ActionCapabilities capabilities) {
		this.capabilities.copyFrom(capabilities);
	}

	public void updateEnabledActions(ActionCapabilities capabilities) {
		this.enabledActionStates.copyFrom(capabilities);
	}

	public boolean permit(ActionEntry<?> actionEntry) {
        var config = ParCool.getConfig().server();
		return config.get(actionEntry).permit().get() && (!config.enableSkillTree.get() || capabilities.can(actionEntry)) && enabledActionStates.can(actionEntry);
	}

	/// Request the action start.
	/// All requests are cleared on tick finishing, so this must be called before the action is ticked
	public <Context, RequestableAction extends Action & IRequestable<Context>> void request(ActionEntry<RequestableAction> actionEntry, @Nonnull Context context) {
		requestedContexts.put(actionEntry, context);
	}

	public <Context, RequestableAction extends Action & IRequestable<Context>> Context getRequest(ActionEntry<RequestableAction> actionEntry) {
		return (Context) requestedContexts.remove(actionEntry);
	}

	public boolean canStartByRequest(IRequestable<?> requestable) {
		if (!(requestable instanceof Action action)) return false;
		var context = requestedContexts.get(action.getEntry());
		return context != null ? requestable.requestCanStart(context) : action.canStart();
	}

	public void finishTicking() {
		requestedContexts.clear();
	}

	public double getCost(ActionEntry<?> entry, StaminaConsumption.Type type) {
		var config = ParCool.getConfig().server().get(entry);
		return switch (type) {
			case START -> config.costOnStart().get();
			case WORKING -> config.costOnWorking().get();
			case FINISH -> config.costOnFinish().get();
		};
	}

    public void copyFrom(Parkourability original) {
		this.capabilities.copyFrom(original.capabilities);
		this.enabledActionStates.copyFrom(original.enabledActionStates);
    }

    public CompoundTag saveToTag() {
        var tag = new CompoundTag();
        tag.put("caps", capabilities.saveToTag());
		tag.put("enabled", enabledActionStates.saveToTag());
        return tag;
    }

    public void readFrom(CompoundTag tag) {
        if (tag.get("caps") instanceof CompoundTag compoundCapTag) capabilities.readFromTag(compoundCapTag);
		if (tag.get("enabled") instanceof CompoundTag compoundCapTag) enabledActionStates.readFromTag(compoundCapTag);
    }
}
