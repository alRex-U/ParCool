package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.capability.stamina.HungerStamina;
import com.alrex.parcool.common.capability.stamina.Stamina;
import com.alrex.parcool.extern.feathers.FeathersManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface IStamina {
	public enum Type {
		Default(Stamina::new), Hunger(HungerStamina::new), Feathers(FeathersManager::newFeathersStaminaFor);

		Type(Function<Player, IStamina> constructor) {
			this.constructor = constructor;
		}

		private final Function<Player, IStamina> constructor;

		public IStamina newInstance(Player player) {
			return constructor.apply(player);
		}
	}
	@Nullable
	public static IStamina get(Player player) {
		LazyOptional<IStamina> optional = player.getCapability(Capabilities.STAMINA_CAPABILITY);
		return optional.orElse(null);
	}

	public int getActualMaxStamina();

	public int get();

	public int getOldValue();

	public void consume(int value);

	public void recover(int value);

	public boolean isExhausted();

	public void setExhaustion(boolean value);

	public void tick();

	public void set(int value);
}
