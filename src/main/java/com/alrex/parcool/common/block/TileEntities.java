package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TileEntities {
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ParCool.MOD_ID);
    public static final RegistryObject<BlockEntityType<ZiplineHookTileEntity>> ZIPLINE_HOOK = REGISTER.register(
            "zipline_hook",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ZiplineHookTileEntity(TileEntities.ZIPLINE_HOOK.get(), pos, state),
                    Blocks.WOODEN_ZIPLINE_HOOK.get(),
                    Blocks.IRON_ZIPLINE_HOOK.get()
            ).build(null)
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
