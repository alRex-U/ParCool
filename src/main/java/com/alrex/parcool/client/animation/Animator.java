package com.alrex.parcool.client.animation;

import com.alrex.parcool.common.action.Parkourability;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public abstract class Animator {
	private int tick = 0;

    public void tick(Player player) {
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

    public boolean rotatePre(
            Player player,
            Parkourability parkourability,
            PlayerModelRotator rotator
    ) {
        return false;
    }

    public void rotatePost(
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
            RenderFrameEvent event,
			Player player,
			Parkourability parkourability
	) {
	}

}
