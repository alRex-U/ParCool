package com.alrex.parcool.common.capability;

import com.alrex.parcool.ParCool;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IJumpBoost {
    public boolean canJumpBoost();

    public static class JumpBoostStorage implements Capability.IStorage<IJumpBoost>{
        @Override
        public void readNBT(Capability<IJumpBoost> capability, IJumpBoost instance, Direction side, INBT nbt) { }
        @Nullable @Override
        public INBT writeNBT(Capability<IJumpBoost> capability, IJumpBoost instance, Direction side) { return null; }
    }

    public static class JumpBoostProvider implements ICapabilityProvider {
        @CapabilityInject(IJumpBoost.class)
        public static final Capability<IJumpBoost> JUMP_BOOST_CAPABILITY = null;
        public static final ResourceLocation CAPABILITY_LOCATION=new ResourceLocation(ParCool.MOD_ID,"parcool.capability.jumpboost");

        private LazyOptional<IJumpBoost> instance=LazyOptional.of(JUMP_BOOST_CAPABILITY::getDefaultInstance);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == JUMP_BOOST_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }
        @Nonnull @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
            return cap == JUMP_BOOST_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class JumpBoostRegistry{
        @SubscribeEvent
        public static void register(FMLCommonSetupEvent event){
            CapabilityManager.INSTANCE.register(IJumpBoost.class,new IJumpBoost.JumpBoostStorage(),JumpBoost::new);
        }
    }
}
