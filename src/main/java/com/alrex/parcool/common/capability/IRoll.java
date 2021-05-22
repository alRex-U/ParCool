package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public interface IRoll {
	@OnlyIn(Dist.CLIENT)
	public boolean canRollReady(ClientPlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	public boolean canContinueRollReady(ClientPlayerEntity player);

	public boolean isRollReady();

	public boolean isRolling();

	public void setRollReady(boolean ready);

	public void setRolling(boolean rolling);

	public void updateRollingTime();

	public int getRollingTime();

	public int getStaminaConsumption();

	public int getRollAnimateTime();//Don't return 0;

	public static IRoll get(PlayerEntity entity) {
		LazyOptional<IRoll> optional = entity.getCapability(Capabilities.ROLL_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

}
