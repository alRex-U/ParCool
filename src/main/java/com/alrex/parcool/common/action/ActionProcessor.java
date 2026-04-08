package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.network.payload.ActionStatePayload;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.BufferUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class ActionProcessor {
    private static final Identifier STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "exhausted.speed");

	private static final AttributeModifier STAMINA_DEPLETED_SLOWNESS_MODIFIER = new AttributeModifier(
			STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID,
			-0.05,
			AttributeModifier.Operation.ADD_VALUE
	);

	private static final ByteBuffer bufferOfPostState = ByteBuffer.allocate(128);
	private static final ByteBuffer bufferOfPreState = ByteBuffer.allocate(128);
	private static final ByteBuffer bufferOfStarting = ByteBuffer.allocate(128);
	private static int staminaSyncCoolTimeTick = 5;

	@SubscribeEvent
	public void onTick(PlayerTickEvent.Post event) {
		var player = event.getEntity();
		Parkourability parkourability = Parkourability.get(player);

		boolean inClient = player.level().isClientSide();
		boolean inServer = !inClient;

		onTick$doPreprocess(event);
		if (inClient) {
			ClientActionProcessor.onTick$doPreprocessInClient(event, parkourability);
		} else {
			onTick$doPreprocessInServer(event);
		}

		List<Action> actions = parkourability.getList();
		boolean needSync = player.isLocalPlayer();

		if (needSync) {
			ClientActionProcessor.onTick$checkLimitationSynchronization(player, parkourability);
		}

		parkourability.getAdditionalProperties().onTick(player, parkourability);
		LinkedList<ActionStatePayload.Entry> syncStates = new LinkedList<>();
		for (Action action : actions) {
			NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Tick.Pre(player, action));
			processAction(player, parkourability, syncStates, inClient, action);
			NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Tick.Post(player, action));
		}
		if (needSync && !syncStates.isEmpty()) {
            ClientActionProcessor.onTick$sendSynchronizationPacket(player, syncStates);
		}

		if (inClient) ClientActionProcessor.onTick$doPostProcessInClient(event, parkourability);
	}

	private void onTick$doPreprocess(PlayerTickEvent event) {
	}

	private void onTick$doPreprocessInServer(PlayerTickEvent event) {

	}

	private void processAction(Player player, Parkourability parkourability, LinkedList<ActionStatePayload.Entry> syncStates, boolean inClientSide, Action action) {
		boolean needSync = player.isLocalPlayer();

		if (needSync) {
			saveSynchronizationState(action, bufferOfPreState);
		}
		action.tick();

		action.onTick(player, parkourability);
		if (inClientSide) {
			action.onClientTick(player, parkourability);
		} else {
			action.onServerTick(player, parkourability);
		}

		if (needSync) {
			ClientActionProcessor.checkAndChangeActionState(player, parkourability, action, syncStates);
		}

		if (action.isDoing()) {
			action.onWorkingTick(player, parkourability);
			if (inClientSide) {
				action.onWorkingTickInClient(player, parkourability);
				if (needSync) {
					action.onWorkingTickInLocalClient(player, parkourability);
					if (action.getStaminaConsumeTiming() == StaminaConsumeTiming.OnWorking) {
						ClientActionProcessor.consumeStamina(player, parkourability.getActionInfo().getStaminaConsumptionOf(action.getClass()));
					}
				} else {
					action.onWorkingTickInOtherClient(player, parkourability);
				}
			} else {
				action.onWorkingTickInServer(player, parkourability);
			}
		}

		if (needSync) {
			saveSynchronizationState(action, bufferOfPostState);

			if (!BufferUtil.haveSameContents(bufferOfPreState, bufferOfPostState)) {
				bufferOfPostState.rewind();
				var data = new byte[bufferOfPostState.remaining()];
				bufferOfPostState.get(data);
				syncStates.addLast(new ActionStatePayload.Entry(
						action.getClass(),
						ActionStatePayload.Entry.Type.Normal,
						data
				));
				bufferOfPreState.clear();
				bufferOfPostState.clear();
			}
		}
	}

	private void saveSynchronizationState(Action action, ByteBuffer buffer) {
		buffer.clear();
		action.saveSynchronizedState(buffer);
		buffer.flip();
	}

	public static class ClientActionProcessor {
		private static void consumeStamina(Player player, int value) {
			if (player instanceof LocalPlayer localPlayer) {
				LocalStamina.get(localPlayer).consume(localPlayer, value);
			}
		}

        private static void onTick$doPreprocessInClient(PlayerTickEvent event, Parkourability parkourability) {
            if (!(event.getEntity() instanceof AbstractClientPlayer clientPlayer)) return;
            Animation animation = Animation.get(clientPlayer);
            animation.tick(clientPlayer, parkourability);
        }

        private static void onTick$doPostProcessInClient(PlayerTickEvent event, Parkourability parkourability) {
            if (!(event.getEntity() instanceof LocalPlayer player)) return;
            if (!parkourability.limitationIsNotSynced()) {
                var stamina = LocalStamina.get(player);
                stamina.onTick(player);
                if (--staminaSyncCoolTimeTick > 5) {
                    stamina.sync(player);
                    staminaSyncCoolTimeTick = 0;
                }
            }
            var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr != null) {
                if (LocalStamina.get(player).imposeExhaustionPenalty(player) && parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.EnableStaminaExhaustionPenalty)) {
                    player.setSprinting(false);
                    if (!attr.hasModifier(STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID)) {
                        attr.addTransientModifier(STAMINA_DEPLETED_SLOWNESS_MODIFIER);
                    }
                } else {
                    attr.removeModifier(STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID);
                }
            }
        }

        private static void onTick$checkLimitationSynchronization(Player player, Parkourability parkourability) {
            if (player.isLocalPlayer() && player.tickCount > 127 && player.tickCount % 256 == 0 && parkourability.limitationIsNotSynced()) {
                if (player instanceof LocalPlayer localPlayer) {
                    int trialCount = parkourability.getSynchronizeTrialCount();
                    if (trialCount < 5) {
                        parkourability.trySyncLimitation(localPlayer, parkourability);
                        if (ParCoolConfig.Client.Booleans.ShowAutoResynchronizationNotification.get()) {
                            player.displayClientMessage(Component.translatable("parcool.message.error.limitation.not_synced"), false);
                        }
                        ParCool.LOGGER.warn("Detected ParCool Limitation is not synced. Sending synchronization request...");
                    } else if (trialCount == 5) {
                        parkourability.incrementSynchronizeTrialCount();
                        player.displayClientMessage(Component.translatable("parcool.message.error.limitation.fail_sync").withStyle(ChatFormatting.DARK_RED), false);
                        ParCool.LOGGER.error("Failed to synchronize ParCool Limitation. There may be problems about server connection. Please report to the developer after checking connection");
                    }
                }
            }
        }

        private static void onTick$sendSynchronizationPacket(Player player, List<ActionStatePayload.Entry> syncStates) {
            ClientPacketDistributor.sendToServer(new ActionStatePayload(player.getUUID(), syncStates));
        }

        private static void checkAndChangeActionState(Player player, Parkourability parkourability, Action action, LinkedList<ActionStatePayload.Entry> syncStates) {
            if (!(player instanceof LocalPlayer localPlayer)) return;
            if (action.isDoing()) {
                boolean canContinue = parkourability.getActionInfo().can(action.getClass())
                    && !player.getData(Attachments.STAMINA).isExhausted()
                    && !NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToContinueEvent(player, action)).isCanceled()
                    && !NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToContinue(player, action)).isCanceled()
                    && action.canContinue(player, parkourability);
                if (!canContinue) {
                    NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(player, action));
                    action.finish(player);
                    NeoForge.EVENT_BUS.post(new ParCoolActionEvent.StopEvent(player, action));
                    NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(player, action));
                    syncStates.addLast(new ActionStatePayload.Entry(action.getClass(), ActionStatePayload.Entry.Type.Finish, new byte[0]));
                }
            } else {
                bufferOfStarting.clear();
                boolean start = !player.isSpectator()
                    && !player.getData(Attachments.STAMINA).isExhausted()
                    && parkourability.getActionInfo().can(action.getClass())
                    && !NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStartEvent(player, action)).isCanceled()
                    && !NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(player, action)).isCanceled()
                    && action.canStart(player, parkourability, bufferOfStarting);
                bufferOfStarting.flip();
                if (start) {
                    NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(player, action));
                    action.start(player, parkourability, bufferOfStarting);
                    NeoForge.EVENT_BUS.post(new ParCoolActionEvent.StartEvent(player, action));
                    NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(player, action));
                    if (action.getStaminaConsumeTiming() == StaminaConsumeTiming.OnStart) {
                        consumeStamina(localPlayer, parkourability.getActionInfo().getStaminaConsumptionOf(action.getClass()));
                    }
                    var data = new byte[bufferOfStarting.remaining()];
                    bufferOfStarting.get(data);
                    syncStates.addLast(new ActionStatePayload.Entry(
                        action.getClass(),
                        ActionStatePayload.Entry.Type.Start,
                        data
                    ));
                }
            }
        }

		@SubscribeEvent
		public void onRenderTick(RenderFrameEvent.Pre event) {
			Player clientPlayer = Minecraft.getInstance().player;
			if (clientPlayer == null) return;
			for (Player player : clientPlayer.level().players()) {
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
}
