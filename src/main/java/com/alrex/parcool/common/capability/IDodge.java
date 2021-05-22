package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public interface IDodge {
	enum DodgeDirection {Left, Right, Back}

	@OnlyIn(Dist.CLIENT)
	public boolean canDodge(ClientPlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	@Nullable
	public Vector3d getDodgeDirection(ClientPlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	public boolean canContinueDodge(ClientPlayerEntity player);

	public boolean isDodging();

	@Nullable
	public DodgeDirection getDirection();

	public void setDodging(boolean dodging);

	public int getDodgingTime();

	public void updateDodgingTime();

	public int getStaminaConsumption();

	public static IDodge get(PlayerEntity entity) {
		LazyOptional<IDodge> optional = entity.getCapability(Capabilities.DODGE_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}
}
