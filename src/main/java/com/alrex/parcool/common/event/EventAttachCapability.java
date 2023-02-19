package com.alrex.parcool.common.event;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EventAttachCapability {

	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity) event.getObject();
		//Parkourability
		{
			Parkourability instance = new Parkourability();
			LazyOptional<Parkourability> optional = LazyOptional.of(() -> instance);
			ICapabilityProvider provider = new ICapabilitySerializable<CompoundNBT>() {
				@Override
				public CompoundNBT serializeNBT() {
					return (CompoundNBT) Capabilities.PARKOURABILITY_CAPABILITY.getStorage().writeNBT(
							Capabilities.PARKOURABILITY_CAPABILITY,
							instance,
							null
					);
				}

				@Override
				public void deserializeNBT(CompoundNBT nbt) {
					Capabilities.PARKOURABILITY_CAPABILITY.getStorage().readNBT(
							Capabilities.PARKOURABILITY_CAPABILITY,
							instance,
							null,
							nbt
					);
				}

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
			IStamina instance = new Stamina(player);
			LazyOptional<IStamina> optional = LazyOptional.of(() -> instance);
			if (player.isLocalPlayer()) {
				instance.setMaxStamina(ParCoolConfig.CONFIG_CLIENT.staminaMax.get());
			}
			ICapabilityProvider provider = new ICapabilitySerializable<CompoundNBT>() {
				@Override
				public CompoundNBT serializeNBT() {
					return (CompoundNBT) Capabilities.STAMINA_CAPABILITY.getStorage().writeNBT(
							Capabilities.STAMINA_CAPABILITY,
							instance,
							null
					);
				}

				@Override
				public void deserializeNBT(CompoundNBT nbt) {
					Capabilities.STAMINA_CAPABILITY.getStorage().readNBT(
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
		if (event.getObject().level.isClientSide) {
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
