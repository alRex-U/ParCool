package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class RollAnimator extends Animator {
	public static float calculateMovementFactor(float progress) {
		return -MathUtil.squaring(progress - 1) + 1;
	}

	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayerEntity player, Parkourability parkourability) {
		Roll roll = parkourability.getRoll();
		if (!roll.isRolling()) {
			removal = true;
			return;
		}

		float factor = calculateMovementFactor((roll.getRollingTick() + event.getPartialRenderTick()) / (float) roll.getRollMaxTick());

		Vector3d lookVec = player.getLookAngle().yRot((float) Math.PI / 2);
		Vector3f vec = new Vector3f((float) lookVec.x(), 0, (float) lookVec.z());

		event.getMatrixStack().translate(0, player.getBbHeight() / 2, 0);
		event.getMatrixStack().mulPose(vec.rotationDegrees(MathUtil.lerp(0, 360, factor)));
		event.getMatrixStack().translate(0, -player.getBbHeight() / 2, 0);

		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayerEntity> model = renderer.getModel();

		event.getMatrixStack().pushPose();
		{
			PlayerModelTransformer.wrap(player, model, getTick(), event.getPartialRenderTick())
					.rotateLeftArm(
							(float) Math.toRadians(MathUtil.lerp(110f, 180f, factor)),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(MathUtil.lerp(-20f, 0f, factor))
					)
					.rotateRightArm(
							(float) Math.toRadians(MathUtil.lerp(110f, 180f, factor)),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(MathUtil.lerp(20f, 0f, factor))
					)
					.rotateLeftLeg(
							(float) Math.toRadians(MathUtil.lerp(80f, 170f, factor)),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(0F)
					)
					.rotateRightLeg(
							(float) Math.toRadians(MathUtil.lerp(90f, 190f, factor)),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(0F)
					)
					.render(
							event.getMatrixStack(),
							event.getBuffers(),
							event.getRenderer()
					);
		}
		event.getMatrixStack().popPose();
	}
}
