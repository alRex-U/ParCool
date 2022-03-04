package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class DodgeAnimator extends Animator {
	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayerEntity player, Parkourability parkourability) {
		Dodge dodge = parkourability.getDodge();
		if (!dodge.isDodging()) {
			removal = true;
			return;
		}

		if (dodge.isDodging() && dodge.getDodgeDirection() == Dodge.DodgeDirections.Back) {
			Vector3d lookVec = player.getLookAngle().yRot((float) Math.PI / 2);
			Vector3f vec = new Vector3f((float) lookVec.x(), 0, (float) lookVec.z());

			event.getMatrixStack().translate(0, player.getBbHeight() / 2, 0);
			event.getMatrixStack().mulPose(vec.rotationDegrees((dodge.getDodgingTick() + event.getPartialRenderTick()) * -30));
			event.getMatrixStack().translate(0, -player.getBbHeight() / 2, 0);
		} else if (dodge.isDodging() && dodge.getDodgeDirection() == Dodge.DodgeDirections.Front) {
			Vector3d lookVecRight = player.getLookAngle().yRot((float) Math.PI / 2);
			Vector3f vec = new Vector3f((float) lookVecRight.x(), 0, (float) lookVecRight.z());

			event.getMatrixStack().translate(0, player.getBbHeight() / 2, 0);
			event.getMatrixStack().mulPose(vec.rotationDegrees((dodge.getDodgingTick() + event.getPartialRenderTick()) * 30));
			event.getMatrixStack().translate(0, -player.getBbHeight() / 2, 0);
		}
	}
}
