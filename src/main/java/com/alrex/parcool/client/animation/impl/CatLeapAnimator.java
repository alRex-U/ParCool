package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.RenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderPlayerEvent;

import static com.alrex.parcool.utilities.MathUtil.lerp;

public class CatLeapAnimator extends Animator {
	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayerEntity player, Parkourability parkourability) {
		CatLeap catLeap = parkourability.getCatLeap();
		if (!catLeap.isLeaping()) {
			removal = true;
			return;
		}
		PlayerEntity mainPlayer = Minecraft.getInstance().player;
		if (mainPlayer == null) return;
		float partial = event.getPartialRenderTick();
		MatrixStack stack = event.getMatrixStack();
		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayerEntity> model = renderer.getModel();
		stack.pushPose();
		{
			float factor = (catLeap.getLeapingTick() + event.getPartialRenderTick()) / 30f;
			if (factor > 1) factor = 1f;
			Vector3d posOffset = RenderUtil.getPlayerOffset(mainPlayer, player, partial);
			stack.translate(posOffset.x(), posOffset.y(), posOffset.z());
			PlayerModelTransformer.wrap(player, model, getTick(), event.getPartialRenderTick())
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
							event.getMatrixStack(),
							event.getBuffers(),
							event.getRenderer()
					);
		}
		stack.popPose();
	}
}
