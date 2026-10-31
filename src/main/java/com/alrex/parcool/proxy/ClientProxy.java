package com.alrex.parcool.proxy;

import com.alrex.parcool.client.GrappleCameraHandler;
import com.alrex.parcool.client.GrappleTargetOverlay;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.PassiveAnimationProcessor;
import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.animation.system.event.RegisterAnimationEntryEvent;
import com.alrex.parcool.client.animation.system.handle.AnimationSystemEventHandler;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.resource.AnimationResourceManager;
import com.alrex.parcool.client.gui.screen.ParCoolGuideScreen;
import com.alrex.parcool.client.gui.screen.ParCoolSettingScreen;
import com.alrex.parcool.client.gui.screen.ParCoolSkillTreeScreen;
import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.renderer.GrappleRopeRenderer;
import com.alrex.parcool.client.renderer.GrapplingHookItemRenderer;
import com.alrex.parcool.client.renderer.entity.layers.ParCoolModelLayers;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.handlers.InputHandler;
import com.alrex.parcool.common.item.ParCoolItems;
import com.alrex.parcool.common.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
        bus.addListener(GrapplingHookItemRenderer::registerModels);

		bus = MinecraftForge.EVENT_BUS;
		bus.addListener(ParCoolKeyBinds::tick);
		bus.addListener(HUDRegistry.getInstance()::onTick);
		bus.register(InputHandler.class);
        bus.register(AnimationSystemEventHandler.class);
        bus.register(GrappleCameraHandler.class);
        bus.register(GrappleRopeRenderer.class);
        bus.register(GrappleTargetOverlay.class);
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
		instance.messageBuilder(StaminaPacket.CtoS.class, index++)
				.noResponse()
				.decoder(StaminaPacket.CtoS.HANDLER::decode)
				.encoder(StaminaPacket.CtoS.HANDLER::encode)
				.consumerMainThread(StaminaPacket.CtoS.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(StaminaPacket.StoC.class, index++)
				.noResponse()
				.decoder(StaminaPacket.StoC.HANDLER::decode)
				.encoder(StaminaPacket.StoC.HANDLER::encode)
				.consumerMainThread(StaminaPacket.StoC.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(MultiStaminaPacket.class, index++)
				.noResponse()
				.decoder(MultiStaminaPacket.HANDLER::decode)
				.encoder(MultiStaminaPacket.HANDLER::encode)
				.consumerMainThread(MultiStaminaPacket.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(PlayerActionPacket.CtoS.class, index++)
				.noResponse()
				.decoder(PlayerActionPacket.CtoS.HANDLER::decode)
				.encoder(PlayerActionPacket.CtoS.HANDLER::encode)
				.consumerMainThread(PlayerActionPacket.CtoS.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(PlayerActionPacket.StoC.class, index++)
				.noResponse()
				.decoder(PlayerActionPacket.StoC.HANDLER::decode)
				.encoder(PlayerActionPacket.StoC.HANDLER::encode)
				.consumerMainThread(PlayerActionPacket.StoC.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(MultiPlayerActionPacket.class, index++)
				.noResponse()
				.decoder(MultiPlayerActionPacket.HANDLER::decode)
				.encoder(MultiPlayerActionPacket.HANDLER::encode)
				.consumerMainThread(MultiPlayerActionPacket.HANDLER::handleInPhysicalClient)
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
		instance.messageBuilder(EnableActionPacket.CtoS.class, index++)
				.noResponse()
				.decoder(EnableActionPacket.CtoS.HANDLER::decode)
				.encoder(EnableActionPacket.CtoS.HANDLER::encode)
				.consumerMainThread(EnableActionPacket.CtoS.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(EnableActionPacket.StoC.class, index++)
				.noResponse()
				.decoder(EnableActionPacket.StoC.HANDLER::decode)
				.encoder(EnableActionPacket.StoC.HANDLER::encode)
				.consumerMainThread(EnableActionPacket.StoC.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(ChangeActivationPacket.CtoS.class, index++)
				.noResponse()
				.decoder(ChangeActivationPacket.CtoS.HANDLER::decode)
				.encoder(ChangeActivationPacket.CtoS.HANDLER::encode)
				.consumerMainThread(ChangeActivationPacket.CtoS.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(ChangeActivationPacket.StoC.class, index++)
				.noResponse()
				.decoder(ChangeActivationPacket.StoC.HANDLER::decode)
				.encoder(ChangeActivationPacket.StoC.HANDLER::encode)
				.consumerMainThread(ChangeActivationPacket.StoC.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(SkilltreePacket.class, index++)
				.noResponse()
				.decoder(SkilltreePacket.HANDLER::decode)
				.encoder(SkilltreePacket.HANDLER::encode)
				.consumerMainThread(SkilltreePacket.HANDLER::handleInPhysicalClient)
				.add();
	}

    @Override
	public void openSettingGui(Player player) {
		Minecraft.getInstance().setScreen(new ParCoolSettingScreen(Parkourability.get(player)));
    }

	@Override
	public void openSkillTreeGui(Player player) {
		var parkourability = Parkourability.get(player);
		Minecraft.getInstance().setScreen(new ParCoolSkillTreeScreen(
				parkourability,
				parkourability.getSkillTrees(),
				player.getInventory().contains(new ItemStack(ParCoolItems.PARCOOL_GUIDE.get()))
		));
	}

	@Override
	public void openGuideGui() {
		Minecraft.getInstance().setScreen(new ParCoolGuideScreen(new ResourceLocation("parcool", "welcome.md")));
	}
}
