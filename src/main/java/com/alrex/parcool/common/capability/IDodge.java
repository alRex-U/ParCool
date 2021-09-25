package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public interface IDodge {
	enum DodgeDirection {Left, Right, Back, Front}

	@OnlyIn(Dist.CLIENT)
	public boolean canDodge(PlayerEntity player);

	public int getDamageCoolTime();

	public void resetDamageCoolTime();

	public void updateDamageCoolTime();

	public void setDirection(DodgeDirection direction);

	@OnlyIn(Dist.CLIENT)
	@Nullable
	public Vector3d getAndSetDodgeDirection(PlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	public boolean canContinueDodge(PlayerEntity player);

	public int getStaminaConsumptionOfAvoiding(float damage);

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
