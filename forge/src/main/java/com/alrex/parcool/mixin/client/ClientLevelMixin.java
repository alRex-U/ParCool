package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.zipline.ILoadedZiplineHolderProvider;
import com.alrex.parcool.common.zipline.LoadedZiplineHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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
public abstract class ClientLevelMixin extends Level implements ILoadedZiplineHolderProvider {
    private LoadedZiplineHolder parcool$ziplineHolder = new LoadedZiplineHolder();

    protected ClientLevelMixin(WritableLevelData p_220352_, ResourceKey<Level> p_220353_, Holder<DimensionType> p_220354_, Supplier<ProfilerFiller> p_220355_, boolean p_220356_, boolean p_220357_, long p_220358_, int p_220359_) {
        super(p_220352_, p_220353_, p_220354_, p_220355_, p_220356_, p_220357_, p_220358_, p_220359_);
    }

    @Override
    public LoadedZiplineHolder getZiplineHolder() {
        return parcool$ziplineHolder;
    }

    @Inject(method = "tickEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;tickBlockEntities()V"))
    public void onTickEntities(CallbackInfo ci) {
        parcool$ziplineHolder.clear();
    }

    @Inject(method = "setBlocksDirty", at = @At("TAIL"))
    public void onSetBlocksDirty(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var parkourability = Parkourability.get(player);
        for (var listener : parkourability.getActions().getExtensionListeners(ActionExtension.BlockChangedInClientListener.class)) {
            listener.onChangeBlock(pos);
        }
    }
}
