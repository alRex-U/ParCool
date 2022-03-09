package com.alrex.parcool.client.animation;

import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;

@OnlyIn(Dist.CLIENT)
public abstract class Animator {
	private int tick = 0;
	protected boolean removal = false;

	public final void tick() {
		tick++;
	}

	protected int getTick() {
		return tick;
	}

	public boolean isRemoved() {
		return removal;
	}

	public abstract void animate(RenderPlayerEvent.Pre event, AbstractClientPlayer player, Parkourability parkourability);
}
