package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class TileEntities {
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ParCool.MOD_ID);
    public static final Supplier<BlockEntityType<ZiplineHookTileEntity>> ZIPLINE_HOOK = REGISTER.register(
            "zipline_hook",
            () -> new BlockEntityType<>(
                    (BlockPos pos, BlockState state) -> new ZiplineHookTileEntity(TileEntities.ZIPLINE_HOOK.get(), pos, state),
                    Set.of(Blocks.WOODEN_ZIPLINE_HOOK.get(), Blocks.IRON_ZIPLINE_HOOK.get())
            )
    );

    public static void registerAll(IEventBus bus) {
        REGISTER.register(bus);
    }
}
