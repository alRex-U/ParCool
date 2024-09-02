package com.alrex.parcool.proxy;

import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.network.*;
import com.alrex.parcool.common.registries.EventBusForgeRegistry;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.SimpleChannel;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public boolean ParCoolIsActive() {
        return ParCoolConfig.Client.Booleans.ParCoolIsActive.get();
    }

	@Override
	public void init() {
		EventBusForgeRegistry.registerClient(MinecraftForge.EVENT_BUS);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(KeyBindings::register);
		bus.addListener(HUDRegistry.getInstance()::onSetup);
	}

	@Override
	public void registerMessages(SimpleChannel instance) {
		instance
				.messageBuilder(StartBreakfallMessage.class)
				.encoder(StartBreakfallMessage::encode)
				.decoder(StartBreakfallMessage::decode)
				.consumerNetworkThread(StartBreakfallMessage::handleClient)
				.add();
		instance
				.messageBuilder(SyncStaminaMessage.class)
				.encoder(SyncStaminaMessage::encode)
				.decoder(SyncStaminaMessage::decode)
				.consumerNetworkThread(SyncStaminaMessage::handleClient)
				.add();
		instance
				.messageBuilder(SyncLimitationMessage.class)
				.encoder(SyncLimitationMessage::encode)
				.decoder(SyncLimitationMessage::decode)
				.consumerNetworkThread(SyncLimitationMessage::handle)
				.add();
		instance
				.messageBuilder(SyncActionStateMessage.class)
				.encoder(SyncActionStateMessage::encode)
				.decoder(SyncActionStateMessage::decode)
				.consumerNetworkThread(SyncActionStateMessage::handleClient)
				.add();
		instance
				.messageBuilder(SyncClientInformationMessage.class)
				.encoder(SyncClientInformationMessage::encode)
				.decoder(SyncClientInformationMessage::decode)
				.consumerNetworkThread(SyncClientInformationMessage::handleClient)
				.add();
	}
}
