package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.capability.stamina.HungerStamina;
import com.alrex.parcool.common.capability.stamina.Stamina;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface IStamina {
	public enum Type {
		Default(Stamina.class, Stamina::new, null),
		Hunger(HungerStamina.class, HungerStamina::new, HungerStamina::consumeOnServer);

		Type(Class<? extends IStamina> clazz, Function<PlayerEntity, IStamina> constructor, BiConsumer<ServerPlayerEntity, Integer> serverStaminaHandler) {
			this.constructor = constructor;
			this.clazz = clazz;
			this.serverStaminaHandler = serverStaminaHandler;
		}

		private final Function<PlayerEntity, IStamina> constructor;
		private final Class<? extends IStamina> clazz;
		@Nullable
		private final BiConsumer<ServerPlayerEntity, Integer> serverStaminaHandler;

		public IStamina newInstance(PlayerEntity player) {
			return constructor.apply(player);
		}

		public void handleConsumeOnServer(ServerPlayerEntity player, int value) {
			if (this.serverStaminaHandler != null) {
				serverStaminaHandler.accept(player, value);
			}
		}

		public static Type getFromInstance(IStamina stamina) {
			for (Type type : Type.values()) {
				if (type.clazz.isAssignableFrom(stamina.getClass())) {
					return type;
				}
			}
			return null;
		}
	}
	@Nullable
	public static IStamina get(PlayerEntity player) {
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

	public default void updateOldValue() {
	}

	public default boolean wantToConsumeOnServer() {
		return false;
	}

	public default int getRequestedValueConsumedOnServer() {
		return 0;
	}

}
