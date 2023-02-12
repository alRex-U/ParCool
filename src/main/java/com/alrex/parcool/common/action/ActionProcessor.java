package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.SyncActionStateMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.nio.ByteBuffer;
import java.util.List;

public class ActionProcessor {
	public final ByteBuffer bufferOfPostState = ByteBuffer.allocate(128);
	public final ByteBuffer bufferOfPreState = ByteBuffer.allocate(128);
	public final ByteBuffer bufferOfStarting = ByteBuffer.allocate(128);

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onTickInClient(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		if (event.side != LogicalSide.CLIENT) return;
		PlayerEntity player = event.player;
		Animation animation = Animation.get(player);
		if (animation == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		animation.tick(player, parkourability);
	}

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		PlayerEntity player = event.player;
		Parkourability parkourability = Parkourability.get(player);
		Stamina stamina = Stamina.get(player);
		if (parkourability == null || stamina == null) return;
		List<Action> actions = parkourability.getList();
		stamina.onTick(parkourability.getActionInfo());
		boolean needSync = event.side == LogicalSide.CLIENT && player.isLocalPlayer();
		SyncActionStateMessage.Encoder builder = SyncActionStateMessage.Encoder.reset();

		for (Action action : actions) {
			if (needSync) {
				bufferOfPreState.clear();
				action.saveSynchronizedState(bufferOfPreState);
				bufferOfPreState.flip();
			}
			if (action.isDoing()) {
				action.setDoingTick(action.getDoingTick() + 1);
				action.setNotDoingTick(0);
			} else {
				action.setDoingTick(0);
				action.setNotDoingTick(action.getNotDoingTick() + 1);
			}

			action.onTick(player, parkourability, stamina);
			if (event.side == LogicalSide.CLIENT) {
				action.onClientTick(player, parkourability, stamina);
			} else {
				action.onServerTick(player, parkourability, stamina);
			}

			if (player.isLocalPlayer()) {
				if (action.isDoing()) {
					boolean canContinue = action.canContinue(player, parkourability, stamina);
					if (!canContinue) {
						action.setDoing(false);
						action.onStopInLocalClient(player);
						action.onStop(player);
						builder.appendFinishMsg(parkourability, action);
					}
				} else {
					bufferOfStarting.clear();
					boolean start = action.canStart(player, parkourability, stamina, bufferOfStarting);
					bufferOfStarting.flip();
					if (start) {
						action.setDoing(true);
						action.onStartInLocalClient(player, parkourability, stamina, bufferOfStarting);
						bufferOfStarting.rewind();
						action.onStart(player, parkourability);
						builder.appendStartData(parkourability, action, bufferOfStarting);
					}
				}
			}

			if (action.isDoing()) {
				action.onWorkingTick(player, parkourability, stamina);
				if (event.side == LogicalSide.CLIENT) {
					action.onWorkingTickInClient(player, parkourability, stamina);
					if (player.isLocalPlayer()) {
						action.onWorkingTickInLocalClient(player, parkourability, stamina);
					}
				} else {
					action.onWorkingTickInServer(player, parkourability, stamina);
				}
			}

			if (needSync) {
				bufferOfPostState.clear();
				action.saveSynchronizedState(bufferOfPostState);
				bufferOfPostState.flip();

				while (bufferOfPreState.hasRemaining()) {
					if (bufferOfPostState.get() != bufferOfPreState.get()) {
						bufferOfPostState.rewind();
						builder.appendSyncData(parkourability, action, bufferOfPostState);
						break;
					}
				}
			}
		}
		if (needSync) {
			SyncActionStateMessage.sync(player, builder);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		List<Action> actions = parkourability.getList();
		for (Action action : actions) {
			action.onRenderTick(event, player, parkourability);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onViewRender(EntityViewRenderEvent.CameraSetup event) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		Animation animation = Animation.get(player);
		if (animation == null) return;
		animation.cameraSetup(event, player, parkourability);
	}
}
