package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.payload.ActionStatePayload;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.Level;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class ActionProcessor {
	private static final ResourceLocation STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID =
			ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "exhausted.speed");

	private static final AttributeModifier STAMINA_DEPLETED_SLOWNESS_MODIFIER = new AttributeModifier(
			STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID,
			-0.05,
			AttributeModifier.Operation.ADD_VALUE
	);

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
		List<Action> actions = parkourability.getList();
		boolean needSync = player.level().isClientSide() && player.isLocalPlayer();
		if (needSync) {
			var stamina = LocalStamina.get((LocalPlayer) player);
			if (!stamina.isAvailable()) return;
		}
		LinkedList<ActionStatePayload.Entry> syncStates = new LinkedList<>();

		if (needSync && player.tickCount > 100 && player.tickCount % 150 == 0 && parkourability.limitationIsNotSynced()) {
			if (player instanceof LocalPlayer) {
				int trialCount = parkourability.getSynchronizeTrialCount();
				if (trialCount < 5) {
					parkourability.trySyncLimitation((LocalPlayer) player, parkourability);
					if (ParCoolConfig.Client.Booleans.ShowAutoResynchronizationNotification.get()) {
						player.displayClientMessage(Component.translatable("parcool.message.error.limitation.not_synced"), false);
					}
					ParCool.LOGGER.log(Level.WARN, "Detected ParCool Limitation is not synced. Sending synchronization request...");
				} else if (trialCount == 5) {
					parkourability.incrementSynchronizeTrialCount();
					player.displayClientMessage(Component.translatable("parcool.message.error.limitation.fail_sync").withStyle(ChatFormatting.DARK_RED), false);
					ParCool.LOGGER.log(Level.ERROR, "Failed to synchronize ParCool Limitation. Please report to developer");
				}
			}
		}

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
					boolean start = !player.isSpectator()
							&& parkourability.getActionInfo().can(action.getClass())
							&& !player.getData(Attachments.STAMINA).isExhausted()
							&& !NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStartEvent(player, action)).isCanceled()
							&& action.canStart(player, parkourability, bufferOfStarting);
					bufferOfStarting.flip();
					if (start) {
						action.setDoing(true);
						action.onStart(player, parkourability, bufferOfStarting);
						bufferOfStarting.rewind();
						action.onStartInLocalClient(player, parkourability, bufferOfStarting);
						bufferOfStarting.rewind();
						NeoForge.EVENT_BUS.post(new ParCoolActionEvent.StartEvent(player, action));
						var data = new byte[bufferOfStarting.remaining()];
						bufferOfStarting.get(data);
						syncStates.addLast(new ActionStatePayload.Entry(
								action.getClass(),
								ActionStatePayload.Entry.Type.Start,
								data
						));
						if (timing == StaminaConsumeTiming.OnStart && player instanceof LocalPlayer localPlayer) {
							var stamina = LocalStamina.get(localPlayer);
							stamina.consume(localPlayer, parkourability.getActionInfo().getStaminaConsumptionOf(action.getClass()));
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
						if (timing == StaminaConsumeTiming.OnWorking && player instanceof LocalPlayer localPlayer) {
							var stamina = LocalStamina.get(localPlayer);
							stamina.consume(localPlayer, parkourability.getActionInfo().getStaminaConsumptionOf(action.getClass()));
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
			if (!parkourability.limitationIsNotSynced() && player instanceof LocalPlayer localPlayer) {
				var stamina = LocalStamina.get(localPlayer);
				staminaSyncCoolTimeTick++;
				if (staminaSyncCoolTimeTick > 5) {
					staminaSyncCoolTimeTick = 0;
					stamina.sync(localPlayer);
				}
				stamina.onTick(localPlayer);
			}
		}
		if (!player.level().isClientSide()) {
			var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
			if (attr != null) {
				ReadonlyStamina readonlyStamina = player.getData(Attachments.STAMINA);
				if (readonlyStamina.isExhausted()) {
					player.setSprinting(false);
					if (!attr.hasModifier(STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID)) {
						attr.addTransientModifier(STAMINA_DEPLETED_SLOWNESS_MODIFIER);
					}
				} else {
					attr.removeModifier(STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID);
				}
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
