package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public interface IFastRunning {
	@OnlyIn(Dist.CLIENT)
	public boolean canFastRunning(ClientPlayerEntity player);

	public boolean isFastRunning();

	public void setFastRunning(boolean fastRunning);

	public void updateTime();

	public int getRunningTime();

	public int getNotRunningTime();

	public int getStaminaConsumption();

	public static IFastRunning get(PlayerEntity entity) {
		LazyOptional<IFastRunning> optional = entity.getCapability(Capabilities.FAST_RUNNING_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

}
