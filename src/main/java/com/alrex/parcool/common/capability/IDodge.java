package com.alrex.parcool.common.capability;

import com.alrex.parcool.ParCool;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
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

public interface IDodge {
	enum DodgeDirection {Left, Right, Back}

	@OnlyIn(Dist.CLIENT)
	public boolean canDodge(ClientPlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	@Nullable
	public Vector3d getDodgeDirection(ClientPlayerEntity player);

	@OnlyIn(Dist.CLIENT)
	public boolean canContinueDodge(ClientPlayerEntity player);

	public boolean isDodging();

	@Nullable
	public DodgeDirection getDirection();

	public void setDodging(boolean dodging);

	public int getDodgingTime();

	public void updateDodgingTime();

	public int getStaminaConsumption();

	public static class DodgeStorage implements Capability.IStorage<IDodge> {
		@Override
		public void readNBT(Capability<IDodge> capability, IDodge instance, Direction side, INBT nbt) {
		}

		@Nullable
		@Override
		public INBT writeNBT(Capability<IDodge> capability, IDodge instance, Direction side) {
			return null;
		}
	}

	public static class DodgeProvider implements ICapabilityProvider {
		@CapabilityInject(IDodge.class)
		public static final Capability<IDodge> DODGE_CAPABILITY = null;
		public static final ResourceLocation CAPABILITY_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.parcool.dodge");

		private LazyOptional<IDodge> instance = LazyOptional.of(DODGE_CAPABILITY::getDefaultInstance);

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return cap == DODGE_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
			return cap == DODGE_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class DodgeRegistry {
		@SubscribeEvent
		public static void register(FMLCommonSetupEvent event) {
			CapabilityManager.INSTANCE.register(IDodge.class, new IDodge.DodgeStorage(), Dodge::new);
		}
	}
}
