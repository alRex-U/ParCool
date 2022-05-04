package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.mojang.math.Vector3f;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class DodgeAnimator extends Animator {
	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayer player, Parkourability parkourability) {
		Dodge dodge = parkourability.getDodge();
		if (!dodge.isDodging() || !dodge.isFlipping()) {
			removal = true;
			return;
		}

		if (dodge.isDodging() && dodge.getDodgeDirection() == Dodge.DodgeDirections.Back) {
			Vec3 lookVec = player.getLookAngle().yRot((float) Math.PI / 2);
			Vector3f vec = new Vector3f((float) lookVec.x(), 0, (float) lookVec.z());

			event.getPoseStack().translate(0, player.getBbHeight() / 2, 0);
			event.getPoseStack().mulPose(vec.rotationDegrees((dodge.getDodgingTick() + event.getPartialTick()) * -30));
			event.getPoseStack().translate(0, -player.getBbHeight() / 2, 0);
		} else if (dodge.isDodging() && dodge.getDodgeDirection() == Dodge.DodgeDirections.Front) {
			Vec3 lookVecRight = player.getLookAngle().yRot((float) Math.PI / 2);
			Vector3f vec = new Vector3f((float) lookVecRight.x(), 0, (float) lookVecRight.z());

			event.getPoseStack().translate(0, player.getBbHeight() / 2, 0);
			event.getPoseStack().mulPose(vec.rotationDegrees((dodge.getDodgingTick() + event.getPartialTick()) * 30));
			event.getPoseStack().translate(0, -player.getBbHeight() / 2, 0);
		}
	}
}
