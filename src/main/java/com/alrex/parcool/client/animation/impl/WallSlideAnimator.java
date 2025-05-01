package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.api.compatibility.Vec3Wrapper;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.WallSlide;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;

public class WallSlideAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerWrapper player, Parkourability parkourability) {
		return !parkourability.get(WallSlide.class).isDoing();
	}

	@Override
	public void animatePost(PlayerWrapper player, Parkourability parkourability, PlayerModelTransformer transformer) {
		Vec3Wrapper wall = parkourability.get(WallSlide.class).getLeanedWallDirection();
		if (wall == null) return;
		Vec3Wrapper bodyVec = player.getVectorYBodyRot();
		Vec3Wrapper vec = new Vec3Wrapper(bodyVec.x(), 0, bodyVec.z()).normalize();

		Vec3Wrapper dividedVec =
				new Vec3Wrapper(
						vec.x() * wall.x() + vec.z() * wall.z(), 0,
						-vec.x() * wall.z() + vec.z() * wall.x()
				).normalize();
		if (dividedVec.z() < 0) {
			transformer
					.rotateRightArm(
							(float) Math.toRadians(-160),
							(float) -Math.toRadians(VectorUtil.toYawDegree(dividedVec) + 90),
							0
					)
					.end();
		} else {
			transformer
					.rotateLeftArm(
							(float) Math.toRadians(-160),
							(float) -Math.toRadians(VectorUtil.toYawDegree(dividedVec) + 90),
							0
					)
					.end();
		}
	}
}
