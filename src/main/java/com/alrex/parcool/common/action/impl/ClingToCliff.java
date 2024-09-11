package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.Animation;
import com.alrex.parcool.client.animation.impl.ClingToCliffAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
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

public class ClingToCliff extends Action {
	private float armSwingAmount = 0;
	private FacingDirection facingDirection = FacingDirection.ToWall;
	@Nullable
	private Vec3 clingWallDirection = null;

	public float getArmSwingAmount() {
		return armSwingAmount;
	}

	@Override
	public void onWorkingTick(Player player, Parkourability parkourability) {
		player.fallDistance = 0;
	}

	public FacingDirection getFacingDirection() {
		return facingDirection;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
		boolean value = (player.getDeltaMovement().y() < 0.2
                && !player.isShiftKeyDown()
				&& !parkourability.get(HorizontalWallRun.class).isDoing()
				&& KeyBindings.getKeyGrabWall().isDown()
		);
		if (!value) return false;
		Vec3 wallVec = WorldUtil.getGrabbableWall(player);
		if (wallVec == null) return false;
		startInfo.putDouble(wallVec.x())
				.putDouble(wallVec.z());
		//Check whether player is facing to wall
		return 0.5 < wallVec.normalize().dot(player.getLookAngle().multiply(1, 0, 1).normalize());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(Player player, Parkourability parkourability) {
		return (!player.isShiftKeyDown()
				&& parkourability.getActionInfo().can(ClingToCliff.class)
				&& KeyBindings.getKeyGrabWall().isDown()
				&& !parkourability.get(HorizontalWallRun.class).isDoing()
				&& !parkourability.get(ClimbUp.class).isDoing()
				&& WorldUtil.getGrabbableWall(player) != null
		);
	}

    @Override
    public void onStart(Player player, Parkourability parkourability) {
        armSwingAmount = 0;
    }

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		clingWallDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		facingDirection = FacingDirection.ToWall;
		armSwingAmount = 0;
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.CLING_TO_CLIFF.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new ClingToCliffAnimator());
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		clingWallDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		facingDirection = FacingDirection.ToWall;
		armSwingAmount = 0;
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.CLING_TO_CLIFF.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new ClingToCliffAnimator());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        armSwingAmount += (float) player.getDeltaMovement().multiply(1, 0, 1).lengthSqr();
		if (KeyBindings.getKeyLeft().isDown() && KeyBindings.getKeyRight().isDown()) {
			player.setDeltaMovement(0, 0, 0);
		} else {
			if (clingWallDirection != null && facingDirection == FacingDirection.ToWall) {
				Vec3 vec = clingWallDirection.yRot((float) (Math.PI / 2)).normalize().scale(0.1);
				if (KeyBindings.getKeyLeft().isDown()) player.setDeltaMovement(vec);
				else if (KeyBindings.getKeyRight().isDown()) player.setDeltaMovement(vec.reverse());
				else player.setDeltaMovement(0, 0, 0);
			} else {
				player.setDeltaMovement(0, 0, 0);
			}
		}
	}

	@Override
	public void onWorkingTickInClient(Player player, Parkourability parkourability) {
		clingWallDirection = WorldUtil.getGrabbableWall(player);
		if (clingWallDirection == null) return;
		clingWallDirection = clingWallDirection.normalize();
		Vec3 lookingAngle = player.getLookAngle().multiply(1, 0, 1).normalize();
		Vec3 angle =
				new Vec3(
						clingWallDirection.x() * lookingAngle.x() + clingWallDirection.z() * lookingAngle.z(), 0,
						-clingWallDirection.x() * lookingAngle.z() + clingWallDirection.z() * lookingAngle.x()
				).normalize();
		if (angle.x() > 0.342) {
			facingDirection = FacingDirection.ToWall;
		} else if (angle.z() < 0) {
			facingDirection = FacingDirection.RightAgainstWall;
		} else {
			facingDirection = FacingDirection.LeftAgainstWall;
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
		if (isDoing() && clingWallDirection != null) {
			switch (facingDirection) {
				case ToWall:
					player.setYBodyRot((float) VectorUtil.toYawDegree(clingWallDirection));
					break;
				case RightAgainstWall:
                    player.yBodyRotO = player.yBodyRot = (float) VectorUtil.toYawDegree(clingWallDirection.yRot((float) (-Math.PI / 2)));
					break;
				case LeftAgainstWall:
                    player.yBodyRotO = player.yBodyRot = (float) VectorUtil.toYawDegree(clingWallDirection.yRot((float) (Math.PI / 2)));
			}
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnWorking;
	}

	public enum FacingDirection {
		ToWall, RightAgainstWall, LeftAgainstWall
	}
}
