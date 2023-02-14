package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.WallSlide;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class WallSlideAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.get(WallSlide.class).isDoing();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		Vector3d wall = parkourability.get(WallSlide.class).getLeanedWallDirection();
		if (wall == null) return;
		Vector3d bodyVec = VectorUtil.fromYawDegree(player.yBodyRot);
		Vector3d vec = new Vector3d(bodyVec.x(), 0, bodyVec.z()).normalize();

		Vector3d dividedVec =
				new Vector3d(
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
