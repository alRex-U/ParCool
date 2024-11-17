package com.alrex.parcool.common.action;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.client.animation.Animation;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.network.payload.ActionStatePayload;
import com.alrex.parcool.common.stamina.LocalStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class ActionProcessor {
	private final ByteBuffer bufferOfPostState = ByteBuffer.allocate(128);
	private final ByteBuffer bufferOfPreState = ByteBuffer.allocate(128);
	private final ByteBuffer bufferOfStarting = ByteBuffer.allocate(128);
	private int staminaSyncCoolTimeTick = 0;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onTickInClient(PlayerTickEvent.Post event) {
		if (!event.getEntity().level().isClientSide()) return;
		AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();
		Animation animation = Animation.get(player);
		if (animation == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		animation.tick(player, parkourability);
	}

	@SubscribeEvent
	public void onTick(PlayerTickEvent.Pre event) {
		Player player = event.getEntity();
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		List<Action> actions = parkourability.getList();
		boolean needSync = player.level().isClientSide() && player.isLocalPlayer();
		if (needSync) {
			var stamina = LocalStamina.get();
			if (stamina == null || !stamina.isAvailable()) return;
		}
		LinkedList<ActionStatePayload.Entry> syncStates = new LinkedList<>();

		parkourability.getAdditionalProperties().onTick(player, parkourability);
		for (Action action : actions) {
			StaminaConsumeTiming timing = action.getStaminaConsumeTiming();
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

			action.onTick(player, parkourability);
			if (player.level().isClientSide()) {
				action.onClientTick(player, parkourability);
			} else {
				action.onServerTick(player, parkourability);
			}

			if (player.isLocalPlayer()) {
				if (action.isDoing()) {
					boolean canContinue = parkourability.getActionInfo().can(action.getClass())
							&& !player.getData(Attachments.STAMINA).isExhausted()
							&& !NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToContinueEvent(player, action)).isCanceled()
							&& action.canContinue(player, parkourability);
					if (!canContinue) {
						action.setDoing(false);
						action.onStopInLocalClient(player);
						action.onStop(player);
						NeoForge.EVENT_BUS.post(new ParCoolActionEvent.StopEvent(player, action));
						syncStates.addLast(new ActionStatePayload.Entry(
								action.getClass(),
								ActionStatePayload.Entry.Type.Finish,
								new byte[0]
						));
					}
				} else {
					bufferOfStarting.clear();
					boolean start = parkourability.getActionInfo().can(action.getClass())
							&& !player.getData(Attachments.STAMINA).isExhausted()
							&& !NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStartEvent(player, action)).isCanceled()
							&& action.canStart(player, parkourability, bufferOfStarting);
					bufferOfStarting.flip();
					if (start) {
						action.setDoing(true);
						action.onStartInLocalClient(player, parkourability, bufferOfStarting);
						bufferOfStarting.rewind();
						action.onStart(player, parkourability);
						NeoForge.EVENT_BUS.post(new ParCoolActionEvent.StartEvent(player, action));
						var data = new byte[bufferOfStarting.remaining()];
						bufferOfStarting.get(data);
						syncStates.addLast(new ActionStatePayload.Entry(
								action.getClass(),
								ActionStatePayload.Entry.Type.Start,
								data
						));
						if (timing == StaminaConsumeTiming.OnStart) {
							var stamina = LocalStamina.get();
							if (stamina != null) {
								stamina.consume(parkourability.getActionInfo().getStaminaConsumptionOf(action.getClass()));
							}
						}
					}
				}
			}

			if (action.isDoing()) {
				action.onWorkingTick(player, parkourability);
				if (player.level().isClientSide()) {
					action.onWorkingTickInClient(player, parkourability);
					if (player.isLocalPlayer()) {
						action.onWorkingTickInLocalClient(player, parkourability);
						if (timing == StaminaConsumeTiming.OnWorking) {
							var stamina = LocalStamina.get();
							if (stamina != null) {
								stamina.consume(parkourability.getActionInfo().getStaminaConsumptionOf(action.getClass()));
							}
						}
					}
				} else {
					action.onWorkingTickInServer(player, parkourability);
				}
			}

			if (needSync) {
				bufferOfPostState.clear();
				action.saveSynchronizedState(bufferOfPostState);
				bufferOfPostState.flip();

				if (bufferOfPostState.limit() == bufferOfPreState.limit()) {
					while (bufferOfPreState.hasRemaining()) {
						if (bufferOfPostState.get() != bufferOfPreState.get()) {
							bufferOfPostState.rewind();
							var data = new byte[bufferOfPostState.remaining()];
							bufferOfPostState.get(data);
							syncStates.addLast(new ActionStatePayload.Entry(
									action.getClass(),
									ActionStatePayload.Entry.Type.Normal,
									data
							));
							break;
						}
					}
				} else {
					bufferOfPostState.rewind();
					var data = new byte[bufferOfPostState.remaining()];
					bufferOfPostState.get(data);
					syncStates.addLast(new ActionStatePayload.Entry(
							action.getClass(),
							ActionStatePayload.Entry.Type.Normal,
							data
					));
				}
			}
		}
		if (needSync) {
			PacketDistributor.sendToServer(new ActionStatePayload(player.getUUID(), syncStates));
			var stamina = LocalStamina.get();
			if (stamina != null) {
				staminaSyncCoolTimeTick++;
				if (staminaSyncCoolTimeTick > 5) {
					staminaSyncCoolTimeTick = 0;
					stamina.sync((LocalPlayer) player);
				}
				if (stamina.isExhausted()) {
					player.setSprinting(false);
				}
				stamina.onTick();
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onRenderTick(RenderFrameEvent.Pre event) {
		Player clientPlayer = Minecraft.getInstance().player;
		if (clientPlayer == null) return;
		for (Player player : clientPlayer.getCommandSenderWorld().players()) {
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			List<Action> actions = parkourability.getList();
			for (Action action : actions) {
				action.onRenderTick(event, player, parkourability);
			}
			Animation animation = Animation.get(player);
			if (animation == null) return;
			animation.onRenderTick(event, player, parkourability);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onViewRender(ViewportEvent.ComputeCameraAngles event) {
        LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		Animation animation = Animation.get(player);
		if (animation == null) return;
		animation.cameraSetup(event, player, parkourability);
	}
}
