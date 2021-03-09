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

public interface IWallJump {
    public double getJumpPower();
    @OnlyIn(Dist.CLIENT)
    public boolean canWallJump(ClientPlayerEntity player);
    @OnlyIn(Dist.CLIENT)
    public Vector3d getJumpDirection(ClientPlayerEntity player);
    public static class WallJumpStorage implements Capability.IStorage<IWallJump>{
        @Override
        public void readNBT(Capability<IWallJump> capability, IWallJump instance, Direction side, INBT nbt) { }
        @Nullable @Override
        public INBT writeNBT(Capability<IWallJump> capability, IWallJump instance, Direction side) { return null; }
    }

    public static class WallJumpProvider implements ICapabilityProvider {
        @CapabilityInject(IWallJump.class)
        public static final Capability<IWallJump> WALL_JUMP_CAPABILITY = null;
        public static final ResourceLocation CAPABILITY_LOCATION=new ResourceLocation(ParCool.MOD_ID,"parcool.capability.walljump");

        private LazyOptional<IWallJump> instance=LazyOptional.of(WALL_JUMP_CAPABILITY::getDefaultInstance);

        @Nonnull @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == WALL_JUMP_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }
        @Nonnull @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
            return cap == WALL_JUMP_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class WallJumpRegistry{
        @SubscribeEvent
        public static void register(FMLCommonSetupEvent event){
            CapabilityManager.INSTANCE.register(IWallJump.class,new IWallJump.WallJumpStorage(),WallJump::new);
        }
    }
}
