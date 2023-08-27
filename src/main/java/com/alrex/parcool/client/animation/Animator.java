package com.alrex.parcool.client.animation;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;

;

public abstract class Animator {
	private int tick = 0;

	public void tick() {
		tick++;
	}

	protected int getTick() {
		return tick;
	}

	public abstract boolean shouldRemoved(Player player, Parkourability parkourability);

	/**
	 * @return You should return true if you want to cancel vanilla animation to control all about rendering
	 */
	public boolean animatePre(
			Player player,
			Parkourability parkourability,
			PlayerModelTransformer transformer
	) {
		return false;
	}

	/**
	 * Called after vanilla animation is done
	 * You can utilize this to use partially vanilla animation
	 */
	public void animatePost(
			Player player,
			Parkourability parkourability,
			PlayerModelTransformer transformer
	) {
	}

	public void rotate(
			Player player,
			Parkourability parkourability,
			PlayerModelRotator rotator
	) {
	}

	public void onCameraSetUp(
			ViewportEvent.ComputeCameraAngles event,
			Player clientPlayer,
			Parkourability parkourability
	) {
	}

	public void onRenderTick(
			TickEvent.RenderTickEvent event,
			Player player,
			Parkourability parkourability
	) {
	}
}
