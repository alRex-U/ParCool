package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class WallSlideAnimator extends Animator {
	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayer player, Parkourability parkourability) {

		if (!parkourability.getWallSlide().isSliding()) {
			removal = true;
			return;
		}
		float partial = event.getPartialTick();
		PoseStack stack = event.getPoseStack();
		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayer> model = renderer.getModel();
		Vec3 wall = WorldUtil.getWall(player);
		if (wall == null) {
			removal = true;
			return;
		}
		Vec3 lookVec = player.getLookAngle();
		Vec3 vec = new Vec3(lookVec.x(), 0, lookVec.z()).normalize();

		Vec3 dividedVec =
				new Vec3(
						vec.x() * wall.x() + vec.z() * wall.z(), 0,
						-vec.x() * wall.z() + vec.z() * wall.x()
				).normalize();
		stack.pushPose();
		{
			PlayerModelTransformer transformer = PlayerModelTransformer.wrap(player, model, getTick(), partial);
			if (dividedVec.z() < 0.09) {
				transformer.rotateRightArm(
						(float) Math.toRadians(20.0F),
						-(float) Math.toRadians(VectorUtil.toYawDegree(wall)),
						0
				);
			}
			if (-0.09 < dividedVec.z()) {
				transformer.rotateLeftArm(
						(float) Math.toRadians(20.0F),
						-(float) Math.toRadians(VectorUtil.toYawDegree(wall)),
						0
				);
			}
			transformer.render(stack, event.getMultiBufferSource(), renderer);
		}
		stack.popPose();
	}
}
