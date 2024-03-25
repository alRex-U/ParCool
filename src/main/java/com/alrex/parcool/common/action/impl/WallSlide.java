package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.WallSlideAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.damage.DamageSources;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

;

public class WallSlide extends Action {
	private Vec3 leanedWallDirection = null;
	private byte particleSpawnCoolTime = 0;
	private double startYSpeed = 0;
	private int damageCount = 0, takenDamageCount = 0;
	private byte damageCoolTime = 0;

	@Nullable
	public Vec3 getLeanedWallDirection() {
		return leanedWallDirection;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		startInfo.putDouble(Math.abs(player.getDeltaMovement().y()));
		return canContinue(player, parkourability, stamina);
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		Vec3 wall = WorldUtil.getWall(player);
		return (wall != null
				&& !player.isOnGround()
				&& parkourability.getActionInfo().can(WallSlide.class)
				&& !parkourability.get(FastRun.class).isDoing()
				&& !parkourability.get(Dodge.class).isDoing()
				&& !player.getAbilities().flying
				&& player.getDeltaMovement().y <= 0
				&& KeyBindings.getKeyWallSlide().isDown()
				&& !stamina.isExhausted()
				&& !parkourability.get(Dive.class).isDoing()
				&& !parkourability.get(ClingToCliff.class).isDoing()
				&& parkourability.get(ClingToCliff.class).getNotDoingTick() > 12
		);
	}

    @Override
    public void onStart(Player player, Parkourability parkourability) {
        particleSpawnCoolTime = 0;
    }

	@Override
	public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startData) {
		startYSpeed = startData.getDouble();
		damageCount = (int) (5.5 * (startYSpeed - 1.) / player.getBbHeight());
		takenDamageCount = 0;
		damageCoolTime = 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInClient(Player player, Parkourability parkourability, IStamina stamina) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new WallSlideAnimator());
		}
        particleSpawnCoolTime--;
        if (particleSpawnCoolTime <= 0) {
            particleSpawnCoolTime = 2;
            spawnSlideParticle(player);
        }
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnWorking;
	}

	@Override
	public void onWorkingTick(Player player, Parkourability parkourability, IStamina stamina) {
		leanedWallDirection = WorldUtil.getWall(player);
		if (leanedWallDirection != null) {
			BlockPos leanedBlock = new BlockPos(
					player.getX() + leanedWallDirection.x,
					player.getBoundingBox().minY + player.getBbHeight() * 0.75,
					player.getZ() + leanedWallDirection.z
			);
			if (!player.level.isLoaded(leanedBlock)) return;
			float slipperiness = player.level.getBlockState(leanedBlock).getFriction(player.level, leanedBlock, player);
			slipperiness = (float) Math.sqrt(slipperiness);
			player.fallDistance *= slipperiness;
			player.setDeltaMovement(player.getDeltaMovement().multiply(0.8, slipperiness, 0.8));
		}
	}

	@Override
	public void onWorkingTickInServer(Player player, Parkourability parkourability, IStamina stamina) {
		if (damageCoolTime <= 0 && damageCount > takenDamageCount++) {
			int invulnerableTime = player.invulnerableTime; // bypass invulnerableTime
			damageCoolTime = 1;
			player.invulnerableTime = 0;
			player.hurt(DamageSources.WALL_SLIDE, 0.3f);
			player.invulnerableTime = invulnerableTime;
		} else {
			damageCoolTime--;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnSlideParticle(Player player) {
		if (leanedWallDirection == null) return;
		if (player.getRandom().nextBoolean()) return;
		Level level = player.level;
		Vec3 pos = player.position();
		BlockPos leanedBlock = new BlockPos(
				pos.add(leanedWallDirection.x(), player.getBbHeight() * 0.25, leanedWallDirection.z())
		);
		if (!level.isLoaded(leanedBlock)) return;
		float width = player.getBbWidth();
		BlockState blockstate = level.getBlockState(leanedBlock);

        Vec3 normalizedWallVec = leanedWallDirection.normalize();
        Vec3 orthogonalToWallVec = normalizedWallVec.yRot((float) (Math.PI / 2));
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            Vec3 particlePos = new Vec3(
                    pos.x() + (normalizedWallVec.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5D)) * width,
                    pos.y() + player.getBbHeight() - 0.2D + 0.3 * player.getRandom().nextDouble(),
                    pos.z() + (normalizedWallVec.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5D)) * width
            );
            Vec3 particleSpeed = normalizedWallVec
                    .reverse()
                    .yRot((float) (Math.PI * 0.1 * (player.getRandom().nextDouble() - 0.5)))
                    .scale(0.05)
                    .add(0, -0.5 - player.getRandom().nextDouble(), 0);
            level.addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(leanedBlock),
                    particlePos.x(),
                    particlePos.y(),
                    particlePos.z(),
                    particleSpeed.x(),
                    particleSpeed.y(),
                    particleSpeed.z()
            );
        }
    }
}
