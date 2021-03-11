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

public interface IStamina {
    public void setStamina(int newStamina);
    public int getStamina();
    public int getMaxStamina();
    public boolean isExhausted();
    public void setExhausted(boolean exhausted);
    public void consume(int amount);
    public void recover(int amount);
    public void updateRecoveryCoolTime();
    public int getRecoveryCoolTime();

    public static class StaminaStorage implements Capability.IStorage<IStamina>{
        private static final String STAMINA="stamina";
        private static final String EXHAUSTED="exhausted";
        @Override
        public void readNBT(Capability<IStamina> capability, IStamina instance, Direction side, INBT nbt) {
            CompoundNBT compoundNBT = (CompoundNBT) nbt;
            instance.setStamina(compoundNBT.getInt(STAMINA));
            instance.setExhausted(compoundNBT.getBoolean(EXHAUSTED));
        }
        @Nullable @Override
        public INBT writeNBT(Capability<IStamina> capability, IStamina instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt(STAMINA, instance.getStamina());
            nbt.putBoolean(EXHAUSTED, instance.isExhausted());
            return nbt;
        }
    }

    public static class StaminaProvider implements ICapabilityProvider{
        @CapabilityInject(IStamina.class)
        public static final Capability<IStamina> STAMINA_CAPABILITY = null;
        public static final ResourceLocation CAPABILITY_LOCATION=new ResourceLocation(ParCool.MOD_ID,"parcool.capability.stamina");

        private LazyOptional<IStamina> instance=LazyOptional.of(STAMINA_CAPABILITY::getDefaultInstance);

        @Nonnull @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == STAMINA_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }
        @Nonnull @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
            return cap == STAMINA_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class StaminaRegistry{
        @SubscribeEvent
        public static void register(FMLCommonSetupEvent event){
            CapabilityManager.INSTANCE.register(IStamina.class,new StaminaStorage(),Stamina::new);
        }
    }
}
