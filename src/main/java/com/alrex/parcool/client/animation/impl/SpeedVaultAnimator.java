package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;

import static com.alrex.parcool.utilities.MathUtil.lerp;
import static com.alrex.parcool.utilities.MathUtil.squaring;

public class SpeedVaultAnimator extends Animator {
	public static final int MAX_TIME = 11;

	private final Vault.Type type;

	public SpeedVaultAnimator(Vault.Type type) {
		this.type = type;
	}

	@Override
	public void animate(RenderPlayerEvent.Pre event, AbstractClientPlayer player, Parkourability parkourability) {
		Vault vault = parkourability.getVault();
		if (getTick() >= MAX_TIME) {
			removal = true;
			return;
		}
		float partial = event.getPartialTick();
		PoseStack stack = event.getPoseStack();
		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayer> model = renderer.getModel();

		float phase = (getTick() + partial) / MAX_TIME;
		float factor = -squaring(((getTick() + partial) - MAX_TIME / 2f) / (MAX_TIME / 2f)) + 1;

		Vec3 lookVec = player.getLookAngle();
		Vector3f vec = new Vector3f((float) lookVec.x(), 0, (float) lookVec.z());
		Vec3 rightVec = new Vec3(vec.x(), 0, vec.z()).yRot((float) -Math.PI / 2).normalize().scale(1.4 * factor);
		event.getPoseStack().translate(0, player.getBbHeight() / 2, 0);
		stack.mulPose(vec.rotationDegrees(factor * 70 * (type == Vault.Type.Right ? -1 : 1)));
		event.getPoseStack().translate(0, -player.getBbHeight() / 2 - 0.2 * factor, 0);
		stack.pushPose();
		{
			switch (type) {
				case Right:
					PlayerModelTransformer
							.wrap(player, model, getTick(), partial)
							.rotateLeftArm(
									(float) Math.toRadians(180 - factor * 70),
									(float) -Math.toRadians(player.yBodyRot + lerp(-35, -145, phase)),
									(float) Math.toRadians(0)
							)
							.render(stack, event.getMultiBufferSource(), renderer);
					break;

				case Left:
					PlayerModelTransformer
							.wrap(player, model, getTick(), partial)
							.rotateRightArm(
									(float) Math.toRadians(180 + factor * 70),
									(float) -Math.toRadians(player.yBodyRot + lerp(-35, -145, phase)),
									(float) Math.toRadians(0)
							)
							.render(stack, event.getMultiBufferSource(), renderer);
					break;
			}
		}
		stack.popPose();
	}
}
