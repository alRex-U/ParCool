package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.zipline.IronZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.WoodenZiplineHookBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Blocks {
    private static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(ParCool.MOD_ID);
    public static final Supplier<Block> WOODEN_ZIPLINE_HOOK = REGISTER.register(
            "wooden_zipline_hook",
            (name) -> new WoodenZiplineHookBlock(BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .setId(ResourceKey.create(Registries.BLOCK, name))
            )
    );
    public static final DeferredHolder<Block, Block> IRON_ZIPLINE_HOOK = REGISTER.register(
            "iron_zipline_hook",
            (name) -> new IronZiplineHookBlock(BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.METAL)
                    .strength(1.0f, 3.0f)
                    .noCollission()
                    .sound(SoundType.CHAIN)
                    .setId(ResourceKey.create(Registries.BLOCK, name))
            )
    );

    public static void registerAll(IEventBus bus) {
        REGISTER.register(bus);
    }

}
