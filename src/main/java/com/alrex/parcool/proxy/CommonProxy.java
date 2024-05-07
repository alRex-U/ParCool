package com.alrex.parcool.proxy;

import net.minecraftforge.fml.network.simple.SimpleChannel;

public abstract class CommonProxy {
    public boolean ParCoolIsActive() {
        return true;
    }
	public abstract void registerMessages(SimpleChannel instance);
}
