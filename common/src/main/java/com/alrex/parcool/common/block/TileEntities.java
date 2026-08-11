package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolBlocks;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TileEntities {
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ParCool.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY);
    public static final RegistrySupplier<BlockEntityType<ZiplineHookTileEntity>> ZIPLINE_HOOK = REGISTER.register(
            "zipline_hook",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ZiplineHookTileEntity(TileEntities.ZIPLINE_HOOK.get(), pos, state),
                    ParCoolBlocks.WOODEN_ZIPLINE_HOOK.get(),
                    ParCoolBlocks.IRON_ZIPLINE_HOOK.get()
            ).build(null)
    );

    public static void register() {
        REGISTER.register();
    }
}
