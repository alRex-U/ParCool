package com.alrex.parcool.client.animation;

import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;

public abstract class Animator {
	private int tick = 0;

	public void tick(PlayerWrapper player) {
		tick++;
	}

	protected int getTick() {
		return tick;
	}

	public abstract boolean shouldRemoved(PlayerWrapper player, Parkourability parkourability);

	/**
	 * @return You should return true if you want to cancel vanilla animation to control all about rendering
	 */
	public boolean animatePre(
			PlayerWrapper player,
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
			PlayerWrapper player,
			Parkourability parkourability,
			PlayerModelTransformer transformer
	) {
	}

	public boolean rotatePre(
			PlayerWrapper player,
			Parkourability parkourability,
			PlayerModelRotator rotator
	) {
		return false;
	}

	public void rotatePost(
			PlayerWrapper player,
			Parkourability parkourability,
			PlayerModelRotator rotator
	) {
	}

	public void onCameraSetUp(
			EntityViewRenderEvent.CameraSetup event,
			PlayerWrapper clientPlayer,
			Parkourability parkourability
	) {
	}

	public void onRenderTick(
			TickEvent.RenderTickEvent event,
			PlayerWrapper player,
			Parkourability parkourability
	) {
	}

}
