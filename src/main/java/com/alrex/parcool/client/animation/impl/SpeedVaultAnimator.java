package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.capability.Parkourability;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderPlayerEvent;

import static com.alrex.parcool.utilities.MathUtil.lerp;
import static com.alrex.parcool.utilities.MathUtil.squaring;

public class SpeedVaultAnimator extends Animator {
	private static final int MAX_TIME = 11;

	public enum Type {Right, Left}

	private Type type;

	public SpeedVaultAnimator(Type type) {
		this.type = type;
	}

	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayerEntity player, Parkourability parkourability) {
		Vault vault = parkourability.getVault();
		if (getTick() >= MAX_TIME) {
			removal = true;
			return;
		}
		PlayerEntity mainPlayer = Minecraft.getInstance().player;
		if (mainPlayer == null) return;
		float partial = event.getPartialRenderTick();
		MatrixStack stack = event.getMatrixStack();
		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayerEntity> model = renderer.getEntityModel();

		float phase = (getTick() + partial) / MAX_TIME;
		float factor = -squaring(((getTick() + partial) - MAX_TIME / 2f) / (MAX_TIME / 2f)) + 1;

		Vector3d lookVec = player.getLookVec();
		Vector3f vec = new Vector3f((float) lookVec.getX(), 0, (float) lookVec.getZ());
		Vector3d rightVec = new Vector3d(vec.getX(), 0, vec.getZ()).rotateYaw((float) -Math.PI / 2).normalize().scale(1.4 * factor);
		event.getMatrixStack().translate(0, player.getHeight() / 2, 0);
		stack.rotate(vec.rotationDegrees(factor * 70 * (type == Type.Right ? -1 : 1)));
		event.getMatrixStack().translate(0, -player.getHeight() / 2 - 0.2 * factor, 0);
		stack.push();
		{
			switch (type) {
				case Right:
					PlayerModelTransformer
							.wrap(player, model, getTick(), partial)
							.rotateLeftArm(
									(float) Math.toRadians(180 - factor * 70),
									(float) -Math.toRadians(player.renderYawOffset + lerp(-35, -145, phase)),
									(float) Math.toRadians(0)
							)
							.render(stack, event.getBuffers(), renderer);
					break;

				case Left:
					PlayerModelTransformer
							.wrap(player, model, getTick(), partial)
							.rotateRightArm(
									(float) Math.toRadians(180 + factor * 70),
									(float) -Math.toRadians(player.renderYawOffset + lerp(-35, -145, phase)),
									(float) Math.toRadians(0)
							)
							.render(stack, event.getBuffers(), renderer);
					break;
			}
		}
		stack.pop();
	}
}
