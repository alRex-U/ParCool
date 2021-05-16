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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventActivateParCool {
	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		if (KeyRecorder.keyActivateParCoolState.isPressed()) {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			IStamina stamina;
			{
				LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
				if (!staminaOptional.isPresent()) return;
				stamina = staminaOptional.orElseThrow(NullPointerException::new);
			}
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

		ICatLeap catLeap;
		ICrawl crawl;
		IFastRunning fastRunning;
		IGrabCliff grabCliff;
		IVault vault;
		IDodge dodge;
		IWallJump wallJump;
		{
			LazyOptional<IDodge> dodgeOptional = player.getCapability(IDodge.DodgeProvider.DODGE_CAPABILITY);
			LazyOptional<ICatLeap> catLeapOptional = player.getCapability(ICatLeap.CatLeapProvider.CAT_LEAP_CAPABILITY);
			LazyOptional<ICrawl> crawlOptional = player.getCapability(ICrawl.CrawlProvider.CRAWL_CAPABILITY);
			LazyOptional<IFastRunning> fastRunningOptional = player.getCapability(IFastRunning.FastRunningProvider.FAST_RUNNING_CAPABILITY);
			LazyOptional<IGrabCliff> grabCliffOptional = player.getCapability(IGrabCliff.GrabCliffProvider.GRAB_CLIFF_CAPABILITY);
			LazyOptional<IVault> vaultOptional = player.getCapability(IVault.VaultProvider.VAULT_CAPABILITY);
			LazyOptional<IWallJump> wallJumpOptional = player.getCapability(IWallJump.WallJumpProvider.WALL_JUMP_CAPABILITY);
			if (!dodgeOptional.isPresent() || !catLeapOptional.isPresent() || !crawlOptional.isPresent() || !fastRunningOptional.isPresent() || !grabCliffOptional.isPresent() || !vaultOptional.isPresent() || !wallJumpOptional.isPresent())
				return false;
			catLeap = catLeapOptional.orElseThrow(NullPointerException::new);
			crawl = crawlOptional.orElseThrow(NullPointerException::new);
			fastRunning = fastRunningOptional.orElseThrow(NullPointerException::new);
			grabCliff = grabCliffOptional.orElseThrow(NullPointerException::new);
			vault = vaultOptional.orElseThrow(NullPointerException::new);
			wallJump = wallJumpOptional.orElseThrow(NullPointerException::new);
			dodge = dodgeOptional.orElseThrow(NullPointerException::new);
		}
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
