package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

public interface IStamina {
	public void allowInfiniteStamina(boolean allowed);

	public boolean isAllowedInfiniteStamina();

	public void setStamina(int newStamina);

	public int getStamina();

	public int getMaxStamina();

	public boolean isExhausted();

	public void setExhausted(boolean exhausted);

	public void consume(int amount);

	public void recover(int amount);

	public void updateRecoveryCoolTime();

	public int getRecoveryCoolTime();

	public static IStamina get(PlayerEntity entity) {
		LazyOptional<IStamina> optional = entity.getCapability(Capabilities.STAMINA_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

}
