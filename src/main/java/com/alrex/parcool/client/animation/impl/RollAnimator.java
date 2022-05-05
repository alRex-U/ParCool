package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.MathUtil;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class RollAnimator extends Animator {
	private final Roll.Type type;

	public RollAnimator(Roll.Type type) {
		this.type = type;
	}

	public static float calculateMovementFactor(float progress) {
		return -MathUtil.squaring(progress - 1) + 1;
	}

	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayer player, Parkourability parkourability) {
		Roll roll = parkourability.getRoll();
		if (!roll.isRolling() || type == null) {
			removal = true;
			return;
		}

		float factor = calculateMovementFactor((roll.getRollingTick() + event.getPartialTick()) / (float) roll.getRollMaxTick());

		Vec3 lookVec = player.getLookAngle().yRot((float) Math.PI / 2);
		Vector3f vec = new Vector3f((float) lookVec.x(), 0, (float) lookVec.z());

		if (type == Roll.Type.Back || type == Roll.Type.Front) {
			event.getPoseStack().translate(0, player.getBbHeight() / 2, 0);
			event.getPoseStack().mulPose(vec.rotationDegrees(MathUtil.lerp(0, type == Roll.Type.Front ? 360 : -360, factor)));
			event.getPoseStack().translate(0, -player.getBbHeight() / 2, 0);

			PlayerRenderer renderer = event.getRenderer();
			PlayerModel<AbstractClientPlayer> model = renderer.getModel();

			event.getPoseStack().pushPose();
			{
				PlayerModelTransformer.wrap(player, model, getTick(), event.getPartialTick())
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
								event.getPoseStack(),
								event.getMultiBufferSource(),
								event.getRenderer()
						);
			}
			event.getPoseStack().popPose();
		}
	}
}
