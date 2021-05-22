package com.alrex.parcool.common.event;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.client.particle.ParticleProvider;
import com.alrex.parcool.common.capability.*;
import com.alrex.parcool.common.network.*;
import com.alrex.parcool.constants.TranslateKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

@OnlyIn(Dist.CLIENT)
public class EventActivateParCool {
	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START && event.side != LogicalSide.CLIENT) return;
		if (KeyRecorder.keyActivateParCoolState.isPressed()) {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			IStamina stamina = IStamina.get(player);
			if (stamina == null) return;

			if (stamina.isExhausted()) {
				player.sendStatusMessage(new TranslationTextComponent(TranslateKeys.WARNING_ACTIVATION_EXHAUSTED), false);
				return;
			}
			boolean active = !ParCool.isActive();
			boolean can = active ? activate() : inactivate();
			if (!can) return;
			ParCool.setActivation(active);
			player.sendStatusMessage(new TranslationTextComponent(active ? TranslateKeys.MESSAGE_ACTIVATION_ACTIVE : TranslateKeys.MESSAGE_ACTIVATION_INACTIVE), false);
		}
	}

	public static boolean activate() {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return false;
		ParticleProvider.spawnEffectActivateParCool(player);
		return true;
	}

	public static boolean inactivate() {
		ClientPlayerEntity player = Minecraft.getInstance().player;

		ICatLeap catLeap = ICatLeap.get(player);
		ICrawl crawl = ICrawl.get(player);
		IFastRunning fastRunning = IFastRunning.get(player);
		IGrabCliff grabCliff = IGrabCliff.get(player);
		IVault vault = IVault.get(player);
		IDodge dodge = IDodge.get(player);
		IWallJump wallJump = IWallJump.get(player);
		if (
				catLeap == null ||
						crawl == null ||
						fastRunning == null ||
						grabCliff == null ||
						vault == null ||
						dodge == null ||
						wallJump == null
		) return false;

		catLeap.setReady(false);
		catLeap.setLeaping(false);
		crawl.setCrawling(false);
		crawl.setSliding(false);
		fastRunning.setFastRunning(false);
		grabCliff.setGrabbing(false);
		vault.setVaulting(false);
		dodge.setDodging(false);

		SyncCatLeapMessage.sync(player);
		SyncCrawlMessage.sync(player);
		SyncGrabCliffMessage.sync(player);
		SyncFastRunningMessage.sync(player);
		SyncDodgeMessage.sync(player);
		return true;
	}
}
