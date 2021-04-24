package com.alrex.parcool.common.capability;

import com.alrex.parcool.ParCool;
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

public interface IVault {
	@OnlyIn(Dist.CLIENT)
	public boolean canVault(ClientPlayerEntity player);

	public boolean isVaulting();

	public void setVaulting(boolean vaulting);

	public void updateVaultingTime();

	public int getVaultingTime();

	public int getVaultAnimateTime();//Don't "return 0;"

	public static class VaultStorage implements Capability.IStorage<IVault> {
		@Override
		public void readNBT(Capability<IVault> capability, IVault instance, Direction side, INBT nbt) {
		}

		@Nullable
		@Override
		public INBT writeNBT(Capability<IVault> capability, IVault instance, Direction side) {
			return null;
		}
	}

	public static class VaultProvider implements ICapabilityProvider {
		@CapabilityInject(IVault.class)
		public static final Capability<IVault> VAULT_CAPABILITY = null;
		public static final ResourceLocation CAPABILITY_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.parcool.vault");

		private LazyOptional<IVault> instance = LazyOptional.of(VAULT_CAPABILITY::getDefaultInstance);

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return cap == VAULT_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
			return cap == VAULT_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class VaultRegistry {
		@SubscribeEvent
		public static void register(FMLCommonSetupEvent event) {
			CapabilityManager.INSTANCE.register(IVault.class, new IVault.VaultStorage(), Vault::new);
		}
	}
}
