package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.network.ActionStatePacket;
import com.alrex.parcool.common.network.ActionStateSetPacket;
import com.alrex.parcool.common.stamina.StaminaSynchronizationDepot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;

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
	public void onTickLevel(TickEvent.LevelTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		if (event.side.isClient()) return;
		getStaminaSyncDepot().tick();
		getActionSyncDepot().tick();
	}

	@SubscribeEvent
	public void onTickPlayer(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;

		var player = event.player;
		var parkourability = Parkourability.get(player);
		Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData = new TreeMap<>();
		if (event.side.isClient()) {
			onTick$doPreprocessInClient(parkourability);
		} else {
			onTick$doPreprocessInServer(parkourability);
		}

        parkourability.getAdditionalProperties().onTick();
		for (Action action : parkourability.getActions()) {
			MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Tick.Pre(player, action));
			processAction(parkourability, event.side, action, synchronizedData);
			MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Tick.Post(player, action));
		}
		if (!synchronizedData.isEmpty()) {
			onTick$sendSyncPacket(parkourability, event.side, synchronizedData);
		}
        if (player instanceof ServerPlayer serverPlayer && parkourability.getCapabilities().isDirty()) {
            parkourability.getCapabilities().sync(serverPlayer);
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

	private void onTick$sendSyncPacket(Parkourability parkourability, LogicalSide side, Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData) {
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
			ParCool.CONNECTION.send(PacketDistributor.SERVER.noArg(), packet);
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
					MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(parkourability.player(), continuableAction));
					continuableAction.finish();
					MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(parkourability.player(), continuableAction));
					type = ActionStatePacket.Type.FINISH;
				}
			} else {
				if (action.isReadyToStart()) {
					MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(parkourability.player(), action));
					action.start();
					MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(parkourability.player(), action));
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
