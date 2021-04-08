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

public interface IRoll {
    @OnlyIn(Dist.CLIENT)
    public boolean canRollReady(ClientPlayerEntity player);
    @OnlyIn(Dist.CLIENT)
    public boolean canContinueRollReady(ClientPlayerEntity player);
    public boolean isRollReady();
    public boolean isRolling();
    public void setRollReady(boolean ready);
    public void setRolling(boolean rolling);
    public void updateRollingTime();
    public int getRollingTime();
    public int getStaminaConsumption();
    public int getRollAnimateTime();//Don't return 0;

    public static class RollStorage implements Capability.IStorage<IRoll>{
        @Override
        public void readNBT(Capability<IRoll> capability, IRoll instance, Direction side, INBT nbt) { }
        @Nullable
        @Override
        public INBT writeNBT(Capability<IRoll> capability, IRoll instance, Direction side) {
            return null;
        }
    }

    public static class RollProvider implements ICapabilityProvider {
        @CapabilityInject(IRoll.class)
        public static final Capability<IRoll> ROLL_CAPABILITY = null;
        public static final ResourceLocation CAPABILITY_LOCATION=new ResourceLocation(ParCool.MOD_ID,"capability.parcool.roll");

        private LazyOptional<IRoll> instance=LazyOptional.of(ROLL_CAPABILITY::getDefaultInstance);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == ROLL_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }
        @Nonnull @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
            return cap == ROLL_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RollRegistry{
        @SubscribeEvent
        public static void register(FMLCommonSetupEvent event){
            CapabilityManager.INSTANCE.register(IRoll.class,new IRoll.RollStorage(),Roll::new);
        }
    }
}
