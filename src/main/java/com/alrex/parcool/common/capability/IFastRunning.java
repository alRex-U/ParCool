package com.alrex.parcool.common.capability;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.impl.FastRunning;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IFastRunning {
	//only in Client
	public boolean canFastRunning(ClientPlayerEntity player);

	public boolean isFastRunning();

	public void setFastRunning(boolean fastRunning);

	public void updateTime();

	public int getRunningTime();

	public int getNotRunningTime();

	public int getStaminaConsumption();

	public static class FastRunningStorage implements Capability.IStorage<IFastRunning> {
		@Override
		public void readNBT(Capability<IFastRunning> capability, IFastRunning instance, Direction side, INBT nbt) {
		}

		@Nullable
		@Override
		public INBT writeNBT(Capability<IFastRunning> capability, IFastRunning instance, Direction side) {
			return null;
		}
	}

	public static IFastRunning get(PlayerEntity entity) {
		LazyOptional<IFastRunning> optional = entity.getCapability(FastRunningProvider.FAST_RUNNING_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

	public static class FastRunningProvider implements ICapabilityProvider {
		@CapabilityInject(IFastRunning.class)
		public static final Capability<IFastRunning> FAST_RUNNING_CAPABILITY = null;
		public static final ResourceLocation CAPABILITY_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.parcool.fastrunning");

		private LazyOptional<IFastRunning> instance = LazyOptional.of(FAST_RUNNING_CAPABILITY::getDefaultInstance);

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return cap == FAST_RUNNING_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
			return cap == FAST_RUNNING_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}
	}

	public static class FastRunningRegistry {
		@SubscribeEvent
		public static void register(FMLCommonSetupEvent event) {
			CapabilityManager.INSTANCE.register(IFastRunning.class, new FastRunningStorage(), FastRunning::new);
		}
	}

}
