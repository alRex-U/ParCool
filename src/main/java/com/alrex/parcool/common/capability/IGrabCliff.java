package com.alrex.parcool.common.capability;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.impl.GrabCliff;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IGrabCliff {
	@OnlyIn(Dist.CLIENT)
	public boolean canGrabCliff(ClientPlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	public boolean canJumpOnCliff(ClientPlayerEntity player);

	public boolean isGrabbing();

	public void setGrabbing(boolean grabbing);

	public void updateTime();

	public int getGrabbingTime();

	public int getNotGrabbingTime();

	public int getStaminaConsumptionGrab();

	public int getStaminaConsumptionClimbUp();

	public static class GrabCliffStorage implements Capability.IStorage<IGrabCliff> {
		@Override
		public void readNBT(Capability<IGrabCliff> capability, IGrabCliff instance, Direction side, INBT nbt) {
		}

		@Nullable
		@Override
		public INBT writeNBT(Capability<IGrabCliff> capability, IGrabCliff instance, Direction side) {
			return null;
		}
	}

	public static class GrabCliffProvider implements ICapabilityProvider {
		@CapabilityInject(IGrabCliff.class)
		public static final Capability<IGrabCliff> GRAB_CLIFF_CAPABILITY = null;
		public static final ResourceLocation CAPABILITY_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.parcool.grabcliff");

		private LazyOptional<IGrabCliff> instance = LazyOptional.of(GRAB_CLIFF_CAPABILITY::getDefaultInstance);

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return cap == GRAB_CLIFF_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
			return cap == GRAB_CLIFF_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class GrabCliffRegistry {
		@SubscribeEvent
		public static void register(FMLCommonSetupEvent event) {
			CapabilityManager.INSTANCE.register(IGrabCliff.class, new IGrabCliff.GrabCliffStorage(), GrabCliff::new);
		}
	}
}
