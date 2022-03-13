package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;

import static com.alrex.parcool.utilities.MathUtil.lerp;
import static com.alrex.parcool.utilities.MathUtil.squaring;

public class KongVaultAnimator extends Animator {
	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayer player, Parkourability parkourability) {
		Vault vault = parkourability.getVault();
		if (getTick() >= SpeedVaultAnimator.MAX_TIME) {
			removal = true;
			return;
		}
		float partial = event.getPartialTick();
		PoseStack stack = event.getPoseStack();
		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayer> model = renderer.getModel();

		float phase = (getTick() + partial) / SpeedVaultAnimator.MAX_TIME;
		float factor = -squaring(((getTick() + partial) - SpeedVaultAnimator.MAX_TIME / 2f) / (SpeedVaultAnimator.MAX_TIME / 2f)) + 1;

		Vec3 lookVec = player.getLookAngle().yRot((float) Math.PI / 2);
		Vector3f vec = new Vector3f((float) lookVec.x(), 0, (float) lookVec.z());

		stack.translate(0, player.getBbHeight() / 2, 0);
		stack.mulPose(vec.rotationDegrees(factor * 95));
		stack.translate(0, -player.getBbHeight() / 2, 0);
		stack.pushPose();
		{
			PlayerModelTransformer
					.wrap(player, model, getTick(), partial)
					.rotateLeftArm(
							(float) Math.toRadians(lerp(30f, 210f, phase)),
							(float) -Math.toRadians(player.yBodyRot - lerp(0, 25, phase)),
							(float) Math.toRadians(0)
					)
					.rotateRightArm(
							(float) Math.toRadians(lerp(40f, 190f, phase)),
							(float) -Math.toRadians(player.yBodyRot + lerp(0, 25, phase)),
							(float) Math.toRadians(0)
					)
					.rotateLeftLeg(
							(float) Math.toRadians(MathUtil.lerp(180f, 220f, factor)),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(0F)
					)
					.rotateRightLeg(
							(float) Math.toRadians(MathUtil.lerp(160f, 215f, factor)),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(0F)
					)
					.render(stack, event.getMultiBufferSource(), renderer);
		}
		stack.popPose();
	}
}
