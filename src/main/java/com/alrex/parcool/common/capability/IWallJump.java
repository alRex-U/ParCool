package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public interface IWallJump {
	public double getJumpPower();

	@OnlyIn(Dist.CLIENT)
	public boolean canWallJump(ClientPlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	public Vector3d getJumpDirection(ClientPlayerEntity player);

	public int getStaminaConsumption();

	public static IWallJump get(PlayerEntity entity) {
		LazyOptional<IWallJump> optional = entity.getCapability(Capabilities.WALL_JUMP_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

}
