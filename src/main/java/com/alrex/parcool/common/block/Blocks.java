package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.zipline.IronZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.WoodenZiplineHookBlock;
import com.alrex.parcool.fabric.DeferredHolder;
import com.alrex.parcool.fabric.DeferredRegister;
import com.alrex.parcool.fabric.IEventBus;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class Blocks {
    private static final DeferredRegister<Block> REGISTER =
            DeferredRegister.create(Registries.BLOCK, ParCool.MOD_ID);
    public static final DeferredHolder<Block, Block> WOODEN_ZIPLINE_HOOK =
            REGISTER.register(
                    "wooden_zipline_hook",
                    () ->
                            new WoodenZiplineHookBlock(
                                    BlockBehaviour.Properties.of()
                                            .mapColor(MapColor.WOOD)
                                            .strength(1.0f, 3.0f)
                                            .sound(SoundType.WOOD)
                                            .pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> IRON_ZIPLINE_HOOK =
            REGISTER.register(
                    "iron_zipline_hook",
                    () ->
                            new IronZiplineHookBlock(
                                    BlockBehaviour.Properties.of()
                                            .mapColor(MapColor.METAL)
                                            .strength(1.0f, 3.0f)
                                            .noCollission()
                                            .sound(SoundType.CHAIN)
                                            .pushReaction(PushReaction.DESTROY)));

    public static void registerAll(IEventBus bus) {
        REGISTER.register(bus);
    }
}
