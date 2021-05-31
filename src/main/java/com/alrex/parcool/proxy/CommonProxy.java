package com.alrex.parcool.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public abstract class CommonProxy {
	public abstract void registerMessages(SimpleChannel instance);

	public void showParCoolGuideScreen(PlayerEntity playerIn) {
	}

	;
}
