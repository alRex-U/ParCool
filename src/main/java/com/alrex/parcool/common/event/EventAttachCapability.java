package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.provider.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventAttachCapability {

	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof PlayerEntity)) return;
		event.addCapability(CrawlProvider.CAPABILITY_LOCATION, new CrawlProvider());
		event.addCapability(FastRunningProvider.CAPABILITY_LOCATION, new FastRunningProvider());
		event.addCapability(StaminaProvider.CAPABILITY_LOCATION, new StaminaProvider());
		event.addCapability(WallJumpProvider.CAPABILITY_LOCATION, new WallJumpProvider());
		event.addCapability(CatLeapProvider.CAPABILITY_LOCATION, new CatLeapProvider());
		event.addCapability(GrabCliffProvider.CAPABILITY_LOCATION, new GrabCliffProvider());
		event.addCapability(VaultProvider.CAPABILITY_LOCATION, new VaultProvider());
		event.addCapability(DodgeProvider.CAPABILITY_LOCATION, new DodgeProvider());
		event.addCapability(RollProvider.CAPABILITY_LOCATION, new RollProvider());
	}
}
