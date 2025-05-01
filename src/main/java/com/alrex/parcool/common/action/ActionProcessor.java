package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.compatibility.AbstractClientPlayerWrapper;
import com.alrex.parcool.api.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.api.compatibility.EventBusWrapper;
import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.network.SyncActionStateMessage;
import com.alrex.parcool.common.network.SyncStaminaMessage;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.logging.log4j.Level;

import java.nio.ByteBuffer;
import java.util.List;

public class ActionProcessor {
	private final ByteBuffer bufferOfPostState = ByteBuffer.allocate(128);
	private final ByteBuffer bufferOfPreState = ByteBuffer.allocate(128);
	private final ByteBuffer bufferOfStarting = ByteBuffer.allocate(128);
	private int staminaSyncCoolTimeTick = 0;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onTickInClient(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		if (event.side != LogicalSide.CLIENT) return;
        AbstractClientPlayerWrapper player = AbstractClientPlayerWrapper.get(event);
		Animation animation = Animation.get(player);
		if (animation == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		animation.tick(player, parkourability);
	}

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		PlayerWrapper player = PlayerWrapper.get(event);
		IStamina stamina = IStamina.get(player);
		if (stamina == null) return;
		if (event.phase == TickEvent.Phase.START) {
			stamina.tick();
			return;
		}
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		List<Action> actions = parkourability.getList();
		boolean needSync = event.side == LogicalSide.CLIENT && player.isLocalPlayer();
		SyncActionStateMessage.Encoder builder = SyncActionStateMessage.Encoder.reset();

		int tickCount = player.getTickCount();
		if (needSync && tickCount > 100 && tickCount % 150 == 0 && parkourability.limitationIsNotSynced()) {
			ClientPlayerWrapper clientPlayer = ClientPlayerWrapper.getOrDefault(player);
			if (clientPlayer != null) {
				int trialCount = parkourability.getSynchronizeTrialCount();
				if (trialCount < 5) {
					parkourability.trySyncLimitation(clientPlayer);
					player.displayClientMessage(new TranslationTextComponent("parcool.message.error.limitation.not_synced"), false);
					ParCool.LOGGER.log(Level.WARN, "Detected ParCool Limitation is not synced. Sending synchronization request...");
				} else if (trialCount == 5) {
					player.displayClientMessage(new TranslationTextComponent("parcool.message.error.limitation.not_synced").withStyle(TextFormatting.DARK_RED), false);
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

			action.onTick(player, parkourability, stamina);
			if (event.side == LogicalSide.CLIENT) {
				action.onClientTick(player, parkourability, stamina);
			} else {
				action.onServerTick(player, parkourability, stamina);
			}

			if (player.isLocalPlayer()) {
				if (action.isDoing()) {
					boolean canContinue = parkourability.getActionInfo().can(action.getClass())
							&& !EventBusWrapper.tryToContinueEvent(player, action)
							&& action.canContinue(player, parkourability, stamina);
					if (!canContinue) {
						action.setDoing(false);
						action.onStopInLocalClient(player);
						action.onStop(player);
						EventBusWrapper.stopEvent(player, action);
						builder.appendFinishMsg(parkourability, action);
					}
				} else {
					ParCool.LOGGER.info("Action {} is not doing", action.getClass().getSimpleName());
					bufferOfStarting.clear();
					boolean start = !player.isSpectator()
							&& parkourability.getActionInfo().can(action.getClass())
                            && !EventBusWrapper.tryToStartEvent(player, action)
							&& action.canStart(player, parkourability, stamina, bufferOfStarting);
					bufferOfStarting.flip();
					if (start) {
						action.setDoing(true);
						action.onStart(player, parkourability, bufferOfStarting);
						bufferOfStarting.rewind();
						action.onStartInLocalClient(player, parkourability, stamina, bufferOfStarting);
						bufferOfStarting.rewind();
						EventBusWrapper.startEvent(player, action);
						builder.appendStartData(parkourability, action, bufferOfStarting);
						if (timing == StaminaConsumeTiming.OnStart)
							stamina.consume(parkourability.getActionInfo().getStaminaConsumptionOf(action.getClass()));
					}
				}
			}

			if (action.isDoing()) {
				action.onWorkingTick(player, parkourability, stamina);
				if (event.side == LogicalSide.CLIENT) {
					action.onWorkingTickInClient(player, parkourability, stamina);
					if (player.isLocalPlayer()) {
						action.onWorkingTickInLocalClient(player, parkourability, stamina);
						if (timing == StaminaConsumeTiming.OnWorking)
							stamina.consume(parkourability.getActionInfo().getStaminaConsumptionOf(action.getClass()));
					}
				} else {
					action.onWorkingTickInServer(player, parkourability, stamina);
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
							builder.appendSyncData(parkourability, action, bufferOfPostState);
							break;
						}
					}
				} else {
					bufferOfPostState.rewind();
					builder.appendSyncData(parkourability, action, bufferOfPostState);
				}
			}
		}
		if (needSync) {
			SyncActionStateMessage.sync(player, builder);

			staminaSyncCoolTimeTick++;
			if (!parkourability.limitationIsNotSynced() && (staminaSyncCoolTimeTick > 3 || stamina.wantToConsumeOnServer())) {
				staminaSyncCoolTimeTick = 0;
				SyncStaminaMessage.sync(player);
			}
			if (stamina.isExhausted()) {
				player.setSprinting(false);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		PlayerWrapper clientPlayer = ClientPlayerWrapper.get();
		if (clientPlayer == null) return;
		for (PlayerWrapper player : clientPlayer.getPlayersOnSameLevel()) {
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
	public void onViewRender(EntityViewRenderEvent.CameraSetup event) {
        ClientPlayerWrapper player = ClientPlayerWrapper.get();
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		Animation animation = Animation.get(player);
		if (animation == null) return;
		animation.cameraSetup(event, player, parkourability);
	}
}
