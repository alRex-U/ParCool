package com.alrex.parcool.client.particle;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

//only in Client
public class ParticleProvider {
	public static void spawnEffectActivateParCool(AbstractClientPlayerEntity player) {
		ClientWorld world = player.clientLevel;
		Vector3d pos = player.position();
		final double x = pos.x();
		final double y = pos.y() + 0.1;
		final double z = pos.z();

		Vector3d motion = player.getDeltaMovement();
		Vector3d vec = new Vector3d(0, 0, 0.3);
		for (int i = 0; i < 16; i++) {
			Vector3d direction = vec.yRot((float) (Math.PI / 8 * i)).add(motion);
			world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, direction.x(), 0, direction.z());
		}
	}

	public static void spawnEffectAvoidDamage(AbstractClientPlayerEntity player) {
		ClientWorld world = player.clientLevel;
		Vector3d vec = player.position();
		final double x = vec.x();
		final double y = vec.y() + player.getBbHeight() / 2;
		final double z = vec.z();
		final BasicParticleType particleType = ParticleTypes.END_ROD;
		Vector3d motion = player.getDeltaMovement();

		world.addParticle(particleType, x, y, z, motion.x(), 0.2 + motion.y(), motion.z());
		Vector3d vec1 = new Vector3d(0.141421, 0.141421, 0);
		Vector3d vec2 = new Vector3d(0.2, 0, 0);
		Vector3d vec3 = new Vector3d(0.141421, -0.141421, 0);
		for (int i = 0; i < 8; i++) {
			vec1 = vec1.yRot((float) (Math.PI / 4));
			vec2 = vec2.yRot((float) (Math.PI / 4));
			vec3 = vec3.yRot((float) (Math.PI / 4));
			world.addParticle(particleType, x, y, z, vec1.x() + motion.x(), vec1.y() + motion.y(), vec1.z() + motion.z());
			world.addParticle(particleType, x, y, z, vec2.x() + motion.x(), vec2.y() + motion.y(), vec2.z() + motion.z());
			world.addParticle(particleType, x, y, z, vec3.x() + motion.x(), vec3.y() + motion.y(), vec3.z() + motion.z());
		}
		world.addParticle(particleType, x, y, z, motion.x(), -0.2 + motion.y(), motion.z());
	}

	public static void spawnEffectSweat(AbstractClientPlayerEntity player) {
		ClientWorld world = player.clientLevel;
		Random random = player.getRandom();
		Vector3d vec = player.position();
		final double x = vec.x();
		final double y = vec.y();
		final double z = vec.z();
		world.addParticle(
				ParticleTypes.DRIPPING_WATER,
				x + random.nextInt(10) / 10d - 0.5,
				y + random.nextInt(20) / 10d,
				z + random.nextInt(10) / 10d - 0.5,
				0, 0, 0
		);
	}
}
