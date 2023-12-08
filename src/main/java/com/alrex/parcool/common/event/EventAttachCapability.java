package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.common.capability.storage.ParkourabilityStorage;
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

public class EventAttachCapability {

	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof Player)) return;
		Player player = (Player) event.getObject();
		//Parkourability
		{
			Parkourability instance = new Parkourability(player);
			LazyOptional<Parkourability> optional = LazyOptional.of(() -> instance);
			ICapabilityProvider provider = new ICapabilitySerializable<CompoundTag>() {
				@Override
				public CompoundTag serializeNBT() {
					return (CompoundTag) new ParkourabilityStorage().writeTag(
							Capabilities.PARKOURABILITY_CAPABILITY,
							instance,
							null
					);
				}

				@Override
				public void deserializeNBT(CompoundTag nbt) {
					new ParkourabilityStorage().readTag(
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
			IStamina instance = null;
			/*if (player.isLocalPlayer() && ParagliderManager.isUsingParaglider()) {
				instance = ParagliderManager.newParagliderStaminaFor(player);
			}*/
			instance = new Stamina(player);
			final IStamina finalInstance = instance;
			LazyOptional<IStamina> optional = LazyOptional.of(() -> finalInstance);
			if (player.isLocalPlayer()) {
				instance.setMaxStamina(ParCoolConfig.Client.Integers.MaxStamina.get());
			}
			ICapabilityProvider provider = new ICapabilitySerializable<CompoundTag>() {
				@Override
				public CompoundTag serializeNBT() {
					return (CompoundTag) new StaminaStorage().writeTag(
							Capabilities.STAMINA_CAPABILITY,
							finalInstance,
							null
					);
				}

				@Override
				public void deserializeNBT(CompoundTag nbt) {
					new StaminaStorage().readTag(
							Capabilities.STAMINA_CAPABILITY,
							finalInstance,
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
		if (event.getObject().getCommandSenderWorld().isClientSide) {
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
