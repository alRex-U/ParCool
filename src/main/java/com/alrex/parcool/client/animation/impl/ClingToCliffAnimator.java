package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class ClingToCliffAnimator extends Animator {
	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayer player, Parkourability parkourability) {
		ClingToCliff clingToCliff = parkourability.getClingToCliff();
		if (!clingToCliff.isCling()) {
			removal = true;
			return;
		}
		float partial = event.getPartialTick();
		PoseStack stack = event.getPoseStack();
		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayer> model = renderer.getModel();

		stack.pushPose();
		{
			PlayerModelTransformer.wrap(player, model, getTick(), partial)
					.rotateRightArm(
							(float) Math.toRadians(20.0F),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(0.0F)
					)
					.rotateLeftArm(
							(float) Math.toRadians(20.0F),
							(float) -Math.toRadians(player.yBodyRot),
							(float) Math.toRadians(0.0F)
					).render(
							stack,
							event.getMultiBufferSource(),
							renderer
					);
		}
		stack.popPose();
	}
}
