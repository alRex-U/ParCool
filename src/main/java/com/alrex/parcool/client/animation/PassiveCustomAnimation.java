package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class PassiveCustomAnimation {
	private int fallingAnimationTick = 0;
	private static final int FallingStartLine = 14;
	private int flyingAnimationLevelOld = 0;
	private int flyingAnimationLevel = 0;
	private static final int flyingMaxLevel = 20;

	public void tick(PlayerEntity player, Parkourability parkourability) {
		flyingAnimationLevelOld = flyingAnimationLevel;
		if (KeyBindings.isKeyForwardDown() && player.abilities.flying) {
			flyingAnimationLevel++;
			if (flyingAnimationLevel > flyingMaxLevel) {
				flyingAnimationLevel = flyingMaxLevel;
			}
		} else {
			flyingAnimationLevel--;
			if (flyingAnimationLevel < 0) {
				flyingAnimationLevel = 0;
			}
		}
		if (!player.isOnGround()
				&& player.fallDistance > 1
				&& !player.abilities.flying
				&& !player.isFallFlying()
				&& !parkourability.get(ClingToCliff.class).isDoing()
		) {
			fallingAnimationTick++;
		} else {
			fallingAnimationTick = 0;
		}
		/*
		IStamina stamina=IStamina.get(player);
		if (stamina!=null && stamina.isExhausted() && player.getRandom().nextBoolean()){
			spawnSweatParticle(player);
		}
		*/
	}

	public void animate(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		if (fallingAnimationTick >= FallingStartLine
				&& ParCoolConfig.Client.Booleans.EnableFallingAnimation.get()
				&& !parkourability.get(Dive.class).isDoing()
		) {
			animateFalling(parkourability, transformer);
		}
	}

	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
	}

	private void animateFalling(Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (fallingAnimationTick + transformer.getPartialTick() - FallingStartLine) / 14;
		float factor = phase > 1 ? 1 : EasingFunctions.SinInOutBySquare(phase);
		transformer
				.addRotateRightArm(0, 0, (float) Math.toRadians(80 * factor))
				.addRotateLeftArm(0, 0, (float) Math.toRadians(-80 * factor))
				.addRotateRightLeg(0, 0, (float) Math.toRadians(10 * factor))
				.addRotateLeftLeg(0, 0, (float) Math.toRadians(-10 * factor))
				.makeArmsMoveDynamically(factor)
				.makeLegsShakingDynamically(factor)
				.end();
	}

	private void animateCreativeFlying(PlayerEntity player, PlayerModelTransformer transformer) {
		float angle = getAngleCreativeFlying(player, transformer.getPartialTick());
		float factor = getFactorCreativeFlying(transformer.getPartialTick());
		if (flyingAnimationLevel > 0) {
			transformer
					.rotateAdditionallyHeadPitch(-angle)
					.rotateRightArm(
							(float) Math.toRadians(-170 * factor),
							(float) Math.toRadians(90 * factor), 0
					)
					.rotateLeftArm(
							(float) Math.toRadians(-170 * factor),
							(float) Math.toRadians(-90 * factor), 0
					)
					.makeArmsNatural()
					.rotateLeftLeg(0, 0, 0)
					.rotateRightLeg(0, 0, 0)
					.makeLegsLittleMoving()
					.end();
		}
	}

	private void rotateCreativeFlying(PlayerEntity player, PlayerModelRotator rotator) {
		rotator
				.startBasedCenter()
				.rotatePitchFrontward(getAngleCreativeFlying(player, rotator.getPartialTick()))
				.end();
	}

	private void spawnSweatParticle(PlayerEntity player) {
		World level = player.level;
		Random rand = player.getRandom();
		Vector3d particleSpeed = player.getDeltaMovement().scale(0.6);
		level.addParticle(
				ParticleTypes.DRIPPING_WATER,
				player.getX() + player.getBbWidth() * (rand.nextFloat() - 0.5f),
				player.getY() + player.getBbHeight() * rand.nextFloat(),
				player.getZ() + player.getBbWidth() * (rand.nextFloat() - 0.5f),
				particleSpeed.x(),
				particleSpeed.y() - 1,
				particleSpeed.z()
		);
	}
	private float getAngleCreativeFlying(PlayerEntity player, float partial) {
		float xRot = (float) VectorUtil.toPitchDegree(player.getDeltaMovement());
		return (xRot + 90) * getFactorCreativeFlying(partial);
	}

	private float getFactorCreativeFlying(float partial) {
		return EasingFunctions.SinInOutBySquare(MathUtil.lerp(flyingAnimationLevelOld, flyingAnimationLevel, partial) / flyingMaxLevel);
	}
}