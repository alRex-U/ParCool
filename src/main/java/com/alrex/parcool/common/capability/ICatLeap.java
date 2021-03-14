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

public interface ICatLeap {
    @OnlyIn(Dist.CLIENT)
    public boolean canCatLeap(ClientPlayerEntity player);
    @OnlyIn(Dist.CLIENT)
    public boolean canReadyLeap(ClientPlayerEntity player);
    @OnlyIn(Dist.CLIENT)
    public double getBoostValue(ClientPlayerEntity player);
    public boolean isLeaping();
    public void setLeaping(boolean leaping);
    public boolean isReady();
    public void setReady(boolean ready);
    public void updateReadyTime();
    public int getReadyTime();

    public static class CatLeapStorage implements Capability.IStorage<ICatLeap>{
        @Override
        public void readNBT(Capability<ICatLeap> capability, ICatLeap instance, Direction side, INBT nbt) { }
        @Nullable @Override
        public INBT writeNBT(Capability<ICatLeap> capability, ICatLeap instance, Direction side) {
            return null;
        }
    }

    public static class CatLeapProvider implements ICapabilityProvider {
        @CapabilityInject(ICatLeap.class)
        public static final Capability<ICatLeap> CAT_LEAP_CAPABILITY = null;
        public static final ResourceLocation CAPABILITY_LOCATION=new ResourceLocation(ParCool.MOD_ID,"capability.parcool.catleap");

        private LazyOptional<ICatLeap> instance=LazyOptional.of(CAT_LEAP_CAPABILITY::getDefaultInstance);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == CAT_LEAP_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }
        @Nonnull @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
            return cap == CAT_LEAP_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class CatLeapRegistry{
        @SubscribeEvent
        public static void register(FMLCommonSetupEvent event){
            CapabilityManager.INSTANCE.register(ICatLeap.class,new ICatLeap.CatLeapStorage(),CatLeap::new);
        }
    }
}
