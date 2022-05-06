package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.capability.Parkourability;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class ClingToCliffAnimator extends Animator {
	private int oldTick = 0;
	private int movementValue = 0;

	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayerEntity player, Parkourability parkourability) {
		ClingToCliff clingToCliff = parkourability.getClingToCliff();
		if (!clingToCliff.isCling()) {
			removal = true;
			return;
		}
		float partial = event.getPartialRenderTick();
		MatrixStack stack = event.getMatrixStack();
		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayerEntity> model = renderer.getModel();

		boolean usePartialTick = false;
		if (KeyBindings.getKeyRight().isDown() ^ KeyBindings.getKeyLeft().isDown()) {
			if (getTick() != oldTick) {
				oldTick = getTick();
				usePartialTick = true;
				movementValue += 1;
			}
		}
		double movement = Math.sin(Math.toRadians((movementValue + (usePartialTick ? 0 : partial)) * 15)) * 45;

		stack.pushPose();
		{
			PlayerModelTransformer.wrap(player, model, getTick(), partial)
					.rotateRightArm(
							(float) Math.toRadians(20.0F),
							(float) -Math.toRadians(player.yBodyRot),
							0
					)
					.rotateLeftArm(
							(float) Math.toRadians(20.0F),
							(float) -Math.toRadians(player.yBodyRot),
							0
					).render(
							stack,
							event.getBuffers(),
							renderer
					);
		}
		stack.popPose();
	}
}
