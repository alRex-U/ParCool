package com.alrex.parcool.common.capability;

import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.api.compatibility.ServerPlayerWrapper;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.capability.stamina.HungerStamina;
import com.alrex.parcool.common.capability.stamina.Stamina;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface IStamina {
	public enum Type {
		Default(Stamina.class, Stamina::new, null),
		Hunger(HungerStamina.class, HungerStamina::new, HungerStamina::consumeOnServer);

		Type(Class<? extends IStamina> clazz, Function<PlayerWrapper, IStamina> constructor, BiConsumer<ServerPlayerWrapper, Integer> serverStaminaHandler) {
			this.constructor = constructor;
			this.clazz = clazz;
			this.serverStaminaHandler = serverStaminaHandler;
		}

		private final Function<PlayerWrapper, IStamina> constructor;
		private final Class<? extends IStamina> clazz;
		@Nullable
		private final BiConsumer<ServerPlayerWrapper, Integer> serverStaminaHandler;

		public IStamina newInstance(PlayerWrapper player) {
			return constructor.apply(player);
		}

		public void handleConsumeOnServer(ServerPlayerWrapper player, int value) {
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
	public static IStamina get(PlayerWrapper player) {
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
