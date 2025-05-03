package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.CrawlAnimator;
import com.alrex.parcool.client.animation.impl.SlidingAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.BlockStateWrapper;
import com.alrex.parcool.compatibility.LevelWrapper;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.block.BlockRenderType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class Slide extends Action {
	private static final BehaviorEnforcer.ID ID_JUMP_CANCEL = BehaviorEnforcer.newID();
	private Vec3Wrapper slidingVec = null;

	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vec3Wrapper lookingVec = player.getLookAngle().multiply(1, 0, 1).normalize();
		startInfo.putDouble(lookingVec.x()).putDouble(lookingVec.z());
		return (!stamina.isExhausted()
				&& KeyRecorder.keyCrawlState.isPressed()
				&& player.isOnGround()
				&& !parkourability.get(Roll.class).isDoing()
				&& !parkourability.get(Tap.class).isDoing()
				&& parkourability.get(Crawl.class).isDoing()
				&& !player.isInWaterOrBubble()
				&& parkourability.get(FastRun.class).getDashTick(parkourability.getAdditionalProperties()) > 5
		);
	}

	@Override
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
        int maxSlidingTick = Math.min(
                parkourability.getActionInfo().getClientSetting().get(ParCoolConfig.Client.Integers.SlidingContinuableTick),
                parkourability.getActionInfo().getServerLimitation().get(ParCoolConfig.Server.Integers.MaxSlidingContinuableTick)
        );
		return getDoingTick() < maxSlidingTick
				&& parkourability.get(Crawl.class).isDoing();
	}

	@Override
	public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		slidingVec = new Vec3Wrapper(startData.getDouble(), 0, startData.getDouble());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.SLIDE.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new SlidingAnimator());
		}
		parkourability.getBehaviorEnforcer().addMarkerCancellingJump(ID_JUMP_CANCEL, this::isDoing);
	}

	@Override
	public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
		slidingVec = new Vec3Wrapper(startData.getDouble(), 0, startData.getDouble());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.SLIDE.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new SlidingAnimator());
		}
	}

	@Override
	public void onWorkingTickInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		if (slidingVec != null) {
			ModifiableAttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
			double speedScale = 0.45;
			if (attr != null) {
				speedScale = attr.getValue() * 4.5;
			}
			Vec3Wrapper vec = slidingVec.scale(speedScale);
			player.setDeltaMovement((player.isOnGround() ? vec : vec.scale(0.6)).add(0, player.getDeltaMovement().y(), 0));
		}
	}

	@Override
	public void onWorkingTickInClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		spawnSlidingParticle(player);
	}

	@Override
	public void onStopInLocalClient(PlayerWrapper player) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new CrawlAnimator());
		}
	}

	@Override
	public void onStopInOtherClient(PlayerWrapper player) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new CrawlAnimator());
		}
	}

	@Nullable
	public Vec3Wrapper getSlidingVector() {
		return slidingVec;
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnSlidingParticle(PlayerWrapper player) {
		if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get()) return;
		LevelWrapper level = player.getLevel();
		Vec3Wrapper pos = player.position();
		BlockStateWrapper feetBlock = player.getBelowBlockState();
		float width = player.getBbWidth();
		Vec3Wrapper direction = getSlidingVector();
		if (direction == null) return;

		if (feetBlock.getRenderShape() != BlockRenderType.INVISIBLE) {
			Vec3Wrapper particlePos = new Vec3Wrapper(
					pos.x() + (player.getRandom().nextDouble() - 0.5D) * width,
					pos.y() + 0.01D + 0.2 * player.getRandom().nextDouble(),
					pos.z() + (player.getRandom().nextDouble() - 0.5D) * width
			);
			Vec3Wrapper particleSpeed = direction
					.reverse()
					.scale(2.5 + 5 * player.getRandom().nextDouble())
					.add(0, 1.5, 0);
			level.addParticle(
					feetBlock.getBlockParticleData(ParticleTypes.BLOCK, new BlockPos(player.position().add(0, -0.5, 0))),
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
