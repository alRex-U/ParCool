package com.alrex.parcool.proxy;

import com.alrex.parcool.common.handlers.PlayerEventHandler;
import com.alrex.parcool.server.command.CommandRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;

public abstract class CommonProxy {
	public abstract void registerMessages(SimpleChannel instance);

	public void init() {
        MinecraftForge.EVENT_BUS.register(PlayerEventHandler.class);
		MinecraftForge.EVENT_BUS.addListener(CommandRegistry::onRegisterCommand);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(CommandRegistry::registerArgumentTypes);
	}

	public void openSkillTreeGui(Player player) {
	}

	public void openGuideGui() {
	}
}
