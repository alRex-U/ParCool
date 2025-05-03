package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;

public class HangAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerWrapper player, Parkourability parkourability) {
		return !parkourability.get(HangDown.class).isDoing();
	}
	@Override
	public void animatePost(PlayerWrapper player, Parkourability parkourability, PlayerModelTransformer transformer) {
		HangDown hangDown = parkourability.get(HangDown.class);
		HangDown.BarAxis axis = hangDown.getHangingBarAxis();
		if (axis == null) return;
		boolean orthogonal = hangDown.isOrthogonalToBar();
        transformer
                .translateRightArm(0.3f, -2.0f, 0)
                .translateLeftArm(-0.3f, -2.0f, 0);
		if (orthogonal) {
			float zAngle = (float) Math.toRadians(10 + 20 * Math.sin(24 * hangDown.getArmSwingAmount()));
			transformer
					.rotateRightArm(
							(float) Math.PI,
							0,
							-zAngle
					)
					.rotateLeftArm(
							(float) Math.PI,
							0,
							zAngle
					);
		} else {
			float xAngle = (float) Math.toRadians(180 + 25 * Math.sin(24 * hangDown.getArmSwingAmount()));
			transformer
					.rotateRightArm(
							xAngle,
							0,
							(float) Math.toRadians(15)
					)
					.rotateLeftArm(
							-xAngle,
							0,
							-(float) Math.toRadians(15)
					);
		}
		transformer
				.rotateRightLeg(0, 0, 0)
				.rotateLeftLeg(0, 0, 0)
				.makeLegsLittleMoving()
				.end();
	}

	private float getRotateAngle(HangDown hangDown, double partialTick) {
		return (float) (-hangDown.getBodySwingAngleFactor() * 40 * Math.sin((hangDown.getDoingTick() + partialTick) / 10 * Math.PI));
	}

	@Override
    public void rotatePost(PlayerWrapper player, Parkourability parkourability, PlayerModelRotator rotator) {
		HangDown hangDown = parkourability.get(HangDown.class);
		rotator.startBasedTop()
				.rotatePitchFrontward(getRotateAngle(hangDown, rotator.getPartialTick()))
				.end();
    }

	@Override
	public void onCameraSetUp(EntityViewRenderEvent.CameraSetup event, PlayerWrapper clientPlayer, Parkourability parkourability) {
		if (!clientPlayer.isLocalPlayer() ||
				!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
				!ParCoolConfig.Client.Booleans.EnableCameraAnimationOfHangDown.get()
		) return;
		HangDown hangDown = parkourability.get(HangDown.class);
		event.setPitch(clientPlayer.getViewXRot((float) event.getRenderPartialTicks()) + getRotateAngle(hangDown, event.getRenderPartialTicks()));
	}
}
