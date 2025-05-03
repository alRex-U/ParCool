package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin extends Level {

    protected ClientWorldMixin(WritableLevelData p_204149_, ResourceKey<Level> p_204150_, Holder<DimensionType> p_204151_, Supplier<ProfilerFiller> p_204152_, boolean p_204153_, boolean p_204154_, long p_204155_) {
        super(p_204149_, p_204150_, p_204151_, p_204152_, p_204153_, p_204154_, p_204155_);
    }

    @Inject(method = "setBlocksDirty", at = @At("TAIL"))
    public void onSetBlocksDirty(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        parkourability.get(HideInBlock.class).notifyBlockChanged(pos);
    }
}
