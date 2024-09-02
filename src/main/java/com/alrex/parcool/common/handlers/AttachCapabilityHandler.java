package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.capability.stamina.OtherStamina;
import com.alrex.parcool.common.capability.storage.StaminaStorage;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AttachCapabilityHandler {

	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player player)) return;
		//Parkourability
		{
            Parkourability instance = new Parkourability();
			LazyOptional<Parkourability> optional = LazyOptional.of(() -> instance);
            ICapabilityProvider provider = new ICapabilityProvider() {
				@Nonnull
				@Override
				public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
					if (cap == Capabilities.PARKOURABILITY_CAPABILITY) {
						return optional.cast();
					}
					return LazyOptional.empty();
				}
			};
			event.addCapability(Capabilities.PARKOURABILITY_LOCATION, provider);
		}
		//Stamina
		{
            IStamina instance;
			if (player.isLocalPlayer()) {
                instance = ParCoolConfig.Client.StaminaType.get().newInstance(player);
            } else {
                instance = new OtherStamina(player);
			}
            LazyOptional<IStamina> optional = LazyOptional.of(() -> instance);
			ICapabilityProvider provider = new ICapabilitySerializable<CompoundTag>() {
				@Override
				public CompoundTag serializeNBT() {
					return (CompoundTag) new StaminaStorage().writeTag(
							Capabilities.STAMINA_CAPABILITY,
                            instance,
							null
					);
				}

				@Override
				public void deserializeNBT(CompoundTag nbt) {
					new StaminaStorage().readTag(
							Capabilities.STAMINA_CAPABILITY,
                            instance,
							null,
							nbt
					);
				}

				@Nonnull
				@Override
				public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
					if (cap == Capabilities.STAMINA_CAPABILITY) {
						return optional.cast();
					}
					return LazyOptional.empty();
				}
			};
			event.addCapability(Capabilities.STAMINA_LOCATION, provider);
		}
        if (event.getObject().level().isClientSide) {
			//Animation
			{
				Animation instance = new Animation();
				LazyOptional<Animation> optional = LazyOptional.of(() -> instance);
				ICapabilityProvider provider = new ICapabilityProvider() {
					@Nonnull
					@Override
					public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
						if (cap == Capabilities.ANIMATION_CAPABILITY) {
							return optional.cast();
						}
						return LazyOptional.empty();
					}
				};
				event.addCapability(Capabilities.ANIMATION_LOCATION, provider);
			}
		}
	}
}
