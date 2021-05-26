package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public interface IGrabCliff {
	@OnlyIn(Dist.CLIENT)
	public boolean canGrabCliff(PlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	public boolean canJumpOnCliff(PlayerEntity player);

	public boolean isGrabbing();

	public void setGrabbing(boolean grabbing);

	public void updateTime();

	public int getGrabbingTime();

	public int getNotGrabbingTime();

	public int getStaminaConsumptionGrab();

	public int getStaminaConsumptionClimbUp();

	public static IGrabCliff get(PlayerEntity entity) {
		LazyOptional<IGrabCliff> optional = entity.getCapability(Capabilities.GRAB_CLIFF_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

}
