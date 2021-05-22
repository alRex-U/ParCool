package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public interface IVault {
	@OnlyIn(Dist.CLIENT)
	public boolean canVault(ClientPlayerEntity player);

	public boolean isVaulting();

	public void setVaulting(boolean vaulting);

	public void updateVaultingTime();

	public int getVaultingTime();

	public int getVaultAnimateTime();//Don't "return 0;"

	public static IVault get(PlayerEntity entity) {
		LazyOptional<IVault> optional = entity.getCapability(Capabilities.VAULT_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

}
