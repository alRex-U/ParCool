package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntities {
    private static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ParCool.MOD_ID);
    public static final RegistryObject<TileEntityType<ZiplineHookTileEntity>> ZIPLINE_HOOK = REGISTER.register(
            "zipline_hook",
            () -> TileEntityType.Builder.of(
                    () -> new ZiplineHookTileEntity(TileEntities.ZIPLINE_HOOK.get()),
                    Blocks.ZIPLINE_HOOK.get()
            ).build(null)
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
