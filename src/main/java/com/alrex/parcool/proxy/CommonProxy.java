package com.alrex.parcool.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.simple.SimpleChannel;

public abstract class CommonProxy {
	public abstract void registerMessages(SimpleChannel instance);

	public void showParCoolGuideScreen(Player playerIn) {
	}

	public void registerModBus(IEventBus bus) {
	}

	public void setup() {
	}

}
