package com.alrex.parcool.proxy;

import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.PassiveAnimationProcessor;
import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.animation.system.event.RegisterAnimationEntryEvent;
import com.alrex.parcool.client.animation.system.handle.AnimationSystemEventHandler;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.resource.AnimationResourceManager;
import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.renderer.entity.layers.ParCoolModelLayers;
import com.alrex.parcool.client.skilltree.ParCoolSkillTrees;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.alrex.parcool.common.handlers.InputHandler;
import com.alrex.parcool.common.network.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void init() {
		super.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(ParCoolKeyBinds::registerAll);
		bus.addListener(HUDRegistry.getInstance()::onSetup);
		bus.addListener(AnimationRegistries::register);
		bus.addListener(ParCoolModelLayers::register);
		bus.addListener(ParCoolTextures::init);
        bus.addListener(AnimationResourceManager::register);
        bus.addListener(GuideResourceManager::register);

		bus = MinecraftForge.EVENT_BUS;
		bus.addListener(ParCoolKeyBinds::tick);
		bus.register(HUDRegistry.getInstance());
		bus.register(InputHandler.class);
        bus.register(AnimationSystemEventHandler.class);
        bus.register(ParCoolSkillTrees.class);
		bus.register(new PassiveAnimationProcessor());

		var registerAnimationEntryEvent = new RegisterAnimationEntryEvent();
		FMLJavaModLoadingContext.get().getModEventBus().post(registerAnimationEntryEvent);
		registerAnimationEntryEvent.finish();
		AnimationSystemConfig.init(AnimationSets.getInstance());
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AnimationSystemConfig.getInstance().getBuiltConfig(), "parcool-animation.toml");
	}

	@Override
	public void registerMessages(SimpleChannel instance) {
		int index = 0;
		instance.messageBuilder(StaminaPacket.class, index++)
				.noResponse()
				.decoder(StaminaPacket.HANDLER::decode)
				.encoder(StaminaPacket.HANDLER::encode)
				.consumerMainThread(StaminaPacket.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(MultiStaminaPacket.class, index++)
				.noResponse()
				.decoder((packet) -> MultiStaminaPacket.decode(MultiStaminaPacket::new, packet))
				.encoder(MultiStaminaPacket::encode)
				.consumerMainThread(MultiStaminaPacket::handleInPhysicalClient)
				.add();
		instance.messageBuilder(ActionStateSetPacket.class, index++)
				.noResponse()
				.decoder(ActionStateSetPacket.HANDLER::decode)
				.encoder(ActionStateSetPacket.HANDLER::encode)
				.consumerMainThread(ActionStateSetPacket.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(MultiActionStateSetPacket.class, index++)
				.noResponse()
				.decoder((packet) -> MultiActionStateSetPacket.decode(MultiActionStateSetPacket::new, packet))
				.encoder(MultiActionStateSetPacket::encode)
				.consumerMainThread(MultiActionStateSetPacket::handleInPhysicalClient)
				.add();
        instance.messageBuilder(ActionCapabilitiesPacket.class, index++)
                .noResponse()
                .decoder(ActionCapabilitiesPacket.HANDLER::decode)
                .encoder(ActionCapabilitiesPacket.HANDLER::encode)
                .consumerMainThread(ActionCapabilitiesPacket.HANDLER::handleInPhysicalClient)
                .add();
		instance.messageBuilder(RequestUnlockActionPacket.class, index++)
				.noResponse()
				.decoder(RequestUnlockActionPacket.HANDLER::decode)
				.encoder(RequestUnlockActionPacket.HANDLER::encode)
				.consumerMainThread(RequestUnlockActionPacket.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(EnableActionPacket.class, index++)
				.noResponse()
				.decoder(EnableActionPacket.HANDLER::decode)
				.encoder(EnableActionPacket.HANDLER::encode)
				.consumerMainThread(EnableActionPacket.HANDLER::handleInPhysicalClient)
				.add();
	}
}
