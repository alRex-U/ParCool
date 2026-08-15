package com.alrex.parcool.api.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.IRequestable;
import com.alrex.parcool.common.network.ActionStatePacket;
import com.alrex.parcool.common.network.ActionStateSetPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

public abstract class Action {
	public Action(Parkourability parkourability, ActionEntry<? extends Action> entry) {
		this.entry = entry;
		this.parkourability = parkourability;
		exclusiveActions = null;
	}

	public Action(Parkourability parkourability, ActionEntry<? extends Action> entry, Collection<ActionEntry<? extends ContinuableAction>> exclusiveActions) {
		this.entry = entry;
		this.parkourability = parkourability;
		this.exclusiveActions = exclusiveActions;
	}

	protected final Parkourability parkourability;
	protected final ActionEntry<? extends Action> entry;
	@Nullable
	protected final Collection<ActionEntry<? extends ContinuableAction>> exclusiveActions;
	private int tickSinceStarted = -1;

	public int getTickSinceStarted() {
		return tickSinceStarted;
	}

	public ActionEntry<? extends Action> getEntry() {
		return entry;
	}

	public SynchronizedDataHolder getSynchronizedData() {
		return SynchronizedDataHolder.empty();
	}

	public void tick() {
		if (tickSinceStarted >= 0) {
			tickSinceStarted++;
		}
		onTick();
		if (parkourability.player().level().isClientSide) {
			onTickInClient();
			if (parkourability.player().isLocalPlayer()) {
				onTickInLocalClient();
			} else {
				onTickInOtherClient();
			}
		} else {
			onTickInServer();
		}
	}

	public void start() {
		tickSinceStarted = 0;

		onStart();
		if (parkourability.player().isLocalPlayer()) {
			onStartInClient();
			onStartInLocalClient();
			takeCost(StaminaConsumption.Type.START);
		} else {
			if (parkourability.player().level().isClientSide()) {
				onStartInClient();
				onStartInOtherClient();
			} else {
				onStartInServer();
			}
		}
	}

	/// <strong>Warning</strong> : This method use internal system directly, without waiting system lifecycle.
	/// Therefore, the synchronization packet is not bundled with other packet, this cause increase of number of sent packet.
	/// And this can cause problems if used without being careful.
	///
	/// Please use <code>canStart</code> method as long as possible
	public void startExplicitly() {
		start();
		var player = parkourability.player();
		var clientSide = player.level().isClientSide();
        var packet = clientSide
				? ActionStateSetPacket.fromClient(parkourability.player().getUUID())
				: ActionStateSetPacket.fromServer(parkourability.player().getUUID());
		packet.add(new ActionStatePacket(
				entry.id().getNamespace(),
				Collections.singletonList(getSynchronizedData().packToEntry(ActionStatePacket.Type.START, entry))
		));
        if (clientSide) {
            PacketDistributor.sendToServer(packet);
        } else {
            PacketDistributor.sendToAllPlayers(packet);
        }
	}

	protected final boolean isPossible() {
		var player = parkourability.player();
		if (parkourability.player().isSpectator() || (parkourability.getStamina().isExhausted())) return false;
		var option = entry.option();
        if ((option.neededPose() != null && option.neededPose() != player.getPose())
                || (!option.availableInFluid() && player.isInFluidType())
				|| (!option.availableNotInFluid() && !player.isInFluidType())
				|| (!option.availableWithFallFlying() && player.isFallFlying())
				|| (option.needOnGround() && !player.onGround())
				|| (option.needNotOnGround() && player.onGround())
				|| player.getAbilities().flying
				|| !parkourability.permit(entry)
		) {
			return false;
		}
		var parent = entry.parent();
		if (parent != null && !parkourability.get(parent).isDoing()) {
			return false;
		}
		if (exclusiveActions != null) {
			for (var exclusiveAction : exclusiveActions) {
				if (parkourability.get(exclusiveAction).isDoing()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isReadyToStart() {
		if (!isPossible()) return false;
        if (NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(parkourability.player(), this)).isCanceled())
			return false;
		return (this instanceof IRequestable<?> requestable)
				? parkourability.canStartByRequest(requestable)
				: canStart();
	}

	protected void takeCost(StaminaConsumption.Type type) {
		if (parkourability.getStamina() instanceof AbstractLocalStamina stamina) {
			var cost = parkourability.getCost(entry, type);
			if (cost != 0) stamina.consume(cost);
		}
	}

	public abstract boolean canStart();

	public void onStart() {
	}

	public void onStartInServer() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInClient() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInOtherClient() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInLocalClient() {
	}

	public void onTick() {
	}

	public void onTickInServer() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onTickInClient() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onTickInLocalClient() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onTickInOtherClient() {
	}

	@OnlyIn(Dist.CLIENT)
	public boolean wantsToShowStatusBar() {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public float getStatusValue() {
		return 0;
	}
}
