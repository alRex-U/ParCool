package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public interface ICatLeap {
	@OnlyIn(Dist.CLIENT)
	public boolean canCatLeap(ClientPlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	public boolean canReadyLeap(ClientPlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	public double getBoostValue(ClientPlayerEntity player);

	public boolean isLeaping();

	public void setLeaping(boolean leaping);

	public boolean isReady();

	public void setReady(boolean ready);

	public void updateReadyTime();

	public int getReadyTime();

	public int getStaminaConsumption();

	@Nullable
	public static ICatLeap get(PlayerEntity entity) {
		LazyOptional<ICatLeap> optional = entity.getCapability(Capabilities.CAT_LEAP_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}
}
