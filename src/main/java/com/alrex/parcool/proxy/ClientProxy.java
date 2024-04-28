package com.alrex.parcool.proxy;

import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.network.*;
import com.alrex.parcool.common.registries.EventBusForgeRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		EventBusForgeRegistry.registerClient(MinecraftForge.EVENT_BUS);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(KeyBindings::register);
		bus.addListener(HUDRegistry.getInstance()::onSetup);

	}

	@Override
	public void registerMessages(SimpleChannel instance) {
		instance.registerMessage(
				3,
				StartBreakfallMessage.class,
				StartBreakfallMessage::encode,
				StartBreakfallMessage::decode,
				StartBreakfallMessage::handleClient
		);
		instance.registerMessage(
				10,
				SyncStaminaMessage.class,
				SyncStaminaMessage::encode,
				SyncStaminaMessage::decode,
				SyncStaminaMessage::handleClient
		);
		instance.registerMessage(
				12,
				SyncLimitationMessage.class,
				SyncLimitationMessage::encode,
				SyncLimitationMessage::decode,
				SyncLimitationMessage::handle
		);
		instance.registerMessage(
				15,
				SyncActionStateMessage.class,
				SyncActionStateMessage::encode,
				SyncActionStateMessage::decode,
				SyncActionStateMessage::handleClient
		);
		instance.registerMessage(
				17,
				SyncClientInformationMessage.class,
				SyncClientInformationMessage::encode,
				SyncClientInformationMessage::decode,
				SyncClientInformationMessage::handleClient
		);
	}
}
