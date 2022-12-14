package com.alrex.parcool.client.animation;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;

@OnlyIn(Dist.CLIENT)
public abstract class Animator {
	private int tick = 0;

	public final void tick() {
		tick++;
	}

	protected int getTick() {
		return tick;
	}

	public abstract boolean shouldRemoved(PlayerEntity player, Parkourability parkourability);

	/**
	 * @return You should return true if you want to cancel vanilla animation to control all about rendering
	 */
	public boolean animatePre(
			PlayerEntity player,
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
			PlayerEntity player,
			Parkourability parkourability,
			PlayerModelTransformer transformer
	) {
	}

	public void rotate(
			PlayerEntity player,
			Parkourability parkourability,
			PlayerModelRotator rotator
	) {
	}

	public void onCameraSetUp(
			EntityViewRenderEvent.CameraSetup event,
			PlayerEntity clientPlayer,
			Parkourability parkourability
	) {
	}
}
