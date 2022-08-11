package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public abstract class Action {
	public abstract void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina);

	@OnlyIn(Dist.CLIENT)
	public abstract void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina);

	@OnlyIn(Dist.CLIENT)
	public abstract void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability);

	public abstract void restoreState(ByteBuffer buffer);

	/**
	 * save state needed to be synchronized
	 */
	public abstract void saveState(ByteBuffer buffer);
}
