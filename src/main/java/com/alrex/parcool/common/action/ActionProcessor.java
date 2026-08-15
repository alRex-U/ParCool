package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.network.ActionCapabilitiesPacket;
import com.alrex.parcool.common.network.ActionStatePacket;
import com.alrex.parcool.common.network.ActionStateSetPacket;
import com.alrex.parcool.common.stamina.StaminaSynchronizationDepot;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class ActionProcessor {
	private final StaminaSynchronizationDepot serverStaminaDepot = new StaminaSynchronizationDepot();
	private final ActionSynchronizationDepot serverActionDepot = new ActionSynchronizationDepot();

	public StaminaSynchronizationDepot getStaminaSyncDepot() {
		return serverStaminaDepot;
	}

	public ActionSynchronizationDepot getActionSyncDepot() {
		return serverActionDepot;
	}

	@SubscribeEvent
    public void onTickLevel(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) return;
		getStaminaSyncDepot().tick();
		getActionSyncDepot().tick();
	}

	@SubscribeEvent
    public void onTickPlayer(PlayerTickEvent.Post event) {
        var player = event.getEntity();
		var parkourability = Parkourability.get(player);
		Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData = new TreeMap<>();
        var side = player.level().isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER;
        if (side.isClient()) {
			onTick$doPreprocessInClient(parkourability);
		} else {
			onTick$doPreprocessInServer(parkourability);
		}

        parkourability.getAdditionalProperties().onTick();
		for (Action action : parkourability.getActions()) {
            NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Tick.Pre(player, action));
            processAction(parkourability, side, action, synchronizedData);
            NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Tick.Post(player, action));
		}
		if (!synchronizedData.isEmpty()) {
            onTick$sendActionSyncPacket(parkourability, side, synchronizedData);
		}
        if (side.isClient()) {
			onTick$sendStaminaSyncPacketInLocal(parkourability);
		}
		if (player instanceof ServerPlayer serverPlayer) {
			if (parkourability.getCapabilities().isDirty()) {
				parkourability.getCapabilities().sync(serverPlayer, ActionCapabilitiesPacket.Target.CAPABILITY);
			}
			if (parkourability.getEnabledActions().isDirty()) {
				parkourability.getEnabledActions().sync(serverPlayer, ActionCapabilitiesPacket.Target.ENABLED_ACTIONS);
			}
		}

		parkourability.finishTicking();
	}

	private void onTick$doPreprocessInServer(Parkourability parkourability) {
	}

	@OnlyIn(Dist.CLIENT)
	private void onTick$doPreprocessInClient(Parkourability parkourability) {
		if (parkourability.getStamina() instanceof AbstractLocalStamina stamina) {
			stamina.tick();
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void onTick$sendStaminaSyncPacketInLocal(Parkourability parkourability) {
		if ((parkourability.player().tickCount & 0b11) == 0 && parkourability.getStamina() instanceof AbstractLocalStamina localStamina) {
			localStamina.sync();
		}
	}

	private void onTick$sendActionSyncPacket(Parkourability parkourability, LogicalSide side, Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData) {
		var list = new LinkedList<ActionStatePacket>();
		for (var entry : synchronizedData.entrySet()) {
			if (entry.getValue().isEmpty()) continue;
			list.add(new ActionStatePacket(entry.getKey(), entry.getValue()));
		}
		if (list.isEmpty()) return;

		var packet = side == LogicalSide.CLIENT
				? ActionStateSetPacket.fromClient(parkourability.player().getUUID())
				: ActionStateSetPacket.fromServer(parkourability.player().getUUID());
		for (var subPacket : list) {
			packet.add(subPacket);
		}
		if (side.isClient()) {
            PacketDistributor.sendToServer(packet);
		} else {
			getActionSyncDepot().requestSync(packet);
		}
	}

	private void processAction(Parkourability parkourability, LogicalSide logicalSide, Action action, Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData) {
		var player = parkourability.player();
		var triggeredSide = action.getEntry().option().triggeredSide();
		boolean needSync = (triggeredSide.isClient() && player.isLocalPlayer())
				|| (triggeredSide.isServer() && logicalSide.isServer());
		action.tick();
		ActionStatePacket.Type type = ActionStatePacket.Type.DATA;
		if (needSync) {
			if (action instanceof ContinuableAction continuableAction && continuableAction.isDoing()) {
				if (!(continuableAction.isPossibleToContinue())) {
                    NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(parkourability.player(), continuableAction));
					continuableAction.finish();
                    NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(parkourability.player(), continuableAction));
					type = ActionStatePacket.Type.FINISH;
				}
			} else {
				if (action.isReadyToStart()) {
                    NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(parkourability.player(), action));
					action.start();
                    NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(parkourability.player(), action));
					type = ActionStatePacket.Type.START;
				}
			}
		}
		if (action instanceof ContinuableAction continuableAction && continuableAction.isDoing()) {
			continuableAction.tickOnWorking();
		}
		var data = action.getSynchronizedData().packToEntry(type, action.getEntry());
		if (data != null) {
			var list = synchronizedData.computeIfAbsent(data.entry().id().getNamespace(), __ -> new LinkedList<>());
			list.add(data);
		}
	}
}
