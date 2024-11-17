package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.Animation;
import com.alrex.parcool.client.animation.impl.HangAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class HangDown extends Action {
	public enum BarAxis {
		X, Z
	}

	private double bodySwingAngleFactor = 0;
	private float armSwingAmount = 0;
	private boolean orthogonalToBar = false;

	public float getArmSwingAmount() {
		return armSwingAmount;
	}

	public double getBodySwingAngleFactor() {
		return bodySwingAngleFactor;
	}

	public boolean isOrthogonalToBar() {
		return orthogonalToBar;
	}

	@Nullable
	public BarAxis getHangingBarAxis() {
		return hangingBarAxis;
	}

	private BarAxis hangingBarAxis = null;

	@OnlyIn(Dist.CLIENT)
	@Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
		startInfo.putDouble(Math.max(-1, Math.min(1, 3 * player.getLookAngle().multiply(1, 0, 1).normalize().dot(player.getDeltaMovement()))));
		return (Math.abs(player.getDeltaMovement().y) < 0.2
				&& KeyBindings.getKeyHangDown().isDown()
				&& !parkourability.get(JumpFromBar.class).isDoing()
				&& !parkourability.get(ClingToCliff.class).isDoing()
				&& WorldUtil.getHangableBars(player) != null
				&& (KeyBindings.getKeyHangDown().getKey().equals(KeyBindings.getKeySneak().getKey()) || !player.isShiftKeyDown())
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return (!player.getData(Attachments.STAMINA).isExhausted()
				&& KeyBindings.getKeyHangDown().isDown()
				&& parkourability.getActionInfo().can(HangDown.class)
				&& !parkourability.get(JumpFromBar.class).isDoing()
				&& !parkourability.get(ClingToCliff.class).isDoing()
				&& WorldUtil.getHangableBars(player) != null
		);
	}

	private void setup(Player player, ByteBuffer startData) {
		armSwingAmount = 0;
		bodySwingAngleFactor = startData.getDouble();
		hangingBarAxis = WorldUtil.getHangableBars(player);
		Vec3 bodyVec = VectorUtil.fromYawDegree(player.yBodyRot);
		orthogonalToBar = (hangingBarAxis == BarAxis.X && Math.abs(bodyVec.x) < Math.abs(bodyVec.z))
				|| (hangingBarAxis == BarAxis.Z && Math.abs(bodyVec.z) < Math.abs(bodyVec.x));
		player.setDeltaMovement(0, 0, 0);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new HangAnimator());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		setup(player, startData);
		if (!KeyBindings.getKeyHangDown().getKey().equals(KeyBindings.getKeySneak().getKey())) {
			parkourability.getCancelMarks().addMarkerCancellingSneak(this::isDoing);
		}
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get()) {
			player.playSound(SoundEvents.HANG_DOWN.get(), 1.0f, 1.0f);
		}
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		setup(player, startData);
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get()) {
            player.playSound(SoundEvents.HANG_DOWN.get(), 1.0f, 1.0f);
        }
	}

	@OnlyIn(Dist.CLIENT)
	@Override
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
		Vec3 bodyVec = VectorUtil.fromYawDegree(player.yBodyRot);
		final double speed = 0.1;
		double xSpeed = 0, zSpeed = 0;
		if (orthogonalToBar) {
			if (hangingBarAxis == BarAxis.X) {
				xSpeed = (bodyVec.z > 0 ? 1 : -1) * speed;
			} else {
				zSpeed = (bodyVec.x > 0 ? 1 : -1) * speed;
			}
			if (KeyBindings.getKeyLeft().isDown()) player.setDeltaMovement(xSpeed, 0, -zSpeed);
			else if (KeyBindings.getKeyRight().isDown()) player.setDeltaMovement(-xSpeed, 0, zSpeed);
			else player.setDeltaMovement(0, 0, 0);
		} else {
			if (hangingBarAxis == BarAxis.X) {
				xSpeed = (bodyVec.x > 0 ? 1 : -1) * speed;
			} else {
				zSpeed = (bodyVec.z > 0 ? 1 : -1) * speed;
			}
			if (KeyBindings.getKeyForward().isDown()) player.setDeltaMovement(xSpeed, 0, zSpeed);
			else if (KeyBindings.getKeyBack().isDown()) player.setDeltaMovement(-xSpeed, 0, -zSpeed);
			else player.setDeltaMovement(0, 0, 0);
		}
        armSwingAmount += (float) player.getDeltaMovement().multiply(1, 0, 1).lengthSqr();
	}

	@Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
		hangingBarAxis = WorldUtil.getHangableBars(player);
		Vec3 bodyVec = VectorUtil.fromYawDegree(player.yBodyRot);
		orthogonalToBar =
				(hangingBarAxis == BarAxis.X && Math.abs(bodyVec.x) < Math.abs(bodyVec.z))
						|| (hangingBarAxis == BarAxis.Z && Math.abs(bodyVec.z) < Math.abs(bodyVec.x));
		if (orthogonalToBar) {
			bodySwingAngleFactor /= 1.05;
		} else {
			bodySwingAngleFactor /= 1.5;
		}
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
		buffer.putFloat(armSwingAmount);
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
		armSwingAmount = buffer.getFloat();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
		if (isDoing()) {
			if (hangingBarAxis == null) return;
			Vec3 bodyVec = VectorUtil.fromYawDegree(player.yBodyRot).normalize();
			Vec3 lookVec = player.getLookAngle();
			Vec3 idealLookVec;
			if (Math.abs(lookVec.x) > Math.abs(lookVec.z)) {
				idealLookVec = new Vec3(lookVec.x > 0 ? 1 : -1, 0, 0);
			} else {
				idealLookVec = new Vec3(0, 0, lookVec.z > 0 ? 1 : -1);
			}
			double differenceAngle = Math.acos(bodyVec.dot(idealLookVec));
			differenceAngle /= 4;
			player.setYBodyRot((float) VectorUtil.toYawDegree(idealLookVec.yRot((float) differenceAngle)));
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnWorking;
	}
}
