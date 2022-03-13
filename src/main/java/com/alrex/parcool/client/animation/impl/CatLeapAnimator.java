package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.client.event.RenderPlayerEvent;

import static com.alrex.parcool.utilities.MathUtil.lerp;

public class CatLeapAnimator extends Animator {
	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayer player, Parkourability parkourability) {
		CatLeap catLeap = parkourability.getCatLeap();
		if (!catLeap.isLeaping()) {
			removal = true;
			return;
		}
		float partial = event.getPartialTick();
		PoseStack stack = event.getPoseStack();
		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayer> model = renderer.getModel();
		stack.pushPose();
		{
			float factor = (catLeap.getLeapingTick() + event.getPartialTick()) / 30f;
			if (factor > 1) factor = 1f;
			PlayerModelTransformer.wrap(player, model, getTick(), event.getPartialTick())
					.rotateLeftArm(
							(float) Math.toRadians(lerp(20f, 180f, factor)),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(0)
					)
					.rotateRightArm(
							(float) Math.toRadians(lerp(20f, 180f, factor)),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(0)
					)
					.rotateLeftLeg(
							(float) Math.toRadians(lerp(120f, 170f, factor)),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(0F)
					)
					.rotateRightLeg(
							(float) Math.toRadians(lerp(240f, 190f, factor)),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(0F)
					)
					.render(
							event.getPoseStack(),
							event.getMultiBufferSource(),
							event.getRenderer()
					);
		}
		stack.popPose();
	}
}
