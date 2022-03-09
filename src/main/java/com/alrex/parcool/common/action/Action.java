package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public abstract class Action {
	public abstract void onTick(Player player, Parkourability parkourability, Stamina stamina);

	@OnlyIn(Dist.CLIENT)
	public abstract void onClientTick(Player player, Parkourability parkourability, Stamina stamina);

	@OnlyIn(Dist.CLIENT)
	public abstract void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability);

	public abstract boolean needSynchronization(ByteBuffer savedInstanceState);

	public abstract void sendSynchronization(Player player);

	public abstract void synchronize(Object message);

	public abstract void saveState(ByteBuffer buffer);
}
