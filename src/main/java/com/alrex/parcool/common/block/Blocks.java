package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.zipline.IronZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.WoodenZiplineHookBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Blocks {
    private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, ParCool.MOD_ID);
    public static final RegistryObject<Block> WOODEN_ZIPLINE_HOOK = REGISTER.register(
            "wooden_zipline_hook",
            () -> new WoodenZiplineHookBlock(BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f, 3.0f)
                    .sound(SoundType.WOOD)
            )
    );
    public static final RegistryObject<Block> IRON_ZIPLINE_HOOK = REGISTER.register(
            "iron_zipline_hook",
            () -> new IronZiplineHookBlock(BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.METAL)
                    .strength(1.0f, 3.0f)
                    .noCollission()
                    .sound(SoundType.CHAIN)
            )
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

}
