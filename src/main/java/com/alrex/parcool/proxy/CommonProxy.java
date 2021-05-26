package com.alrex.parcool.proxy;

import net.minecraftforge.fml.network.simple.SimpleChannel;

public abstract class CommonProxy {
	public abstract void registerMessages(SimpleChannel instance);
}
