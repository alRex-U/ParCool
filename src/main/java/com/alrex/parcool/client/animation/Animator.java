package com.alrex.parcool.client.animation;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
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

	public abstract void animate(RenderPlayerEvent.Pre event, AbstractClientPlayerEntity player, Parkourability parkourability);
}
