package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.zipline.IronZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.WoodenZiplineHookBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Blocks {
    private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, ParCool.MOD_ID);
    public static final RegistryObject<Block> WOODEN_ZIPLINE_HOOK = REGISTER.register(
            "wooden_zipline_hook",
            () -> new WoodenZiplineHookBlock(AbstractBlock.Properties
                    .of(Material.WOOD)
                    .strength(1.0f, 3.0f)
                    .sound(SoundType.WOOD)
            )
    );
    public static final RegistryObject<Block> IRON_ZIPLINE_HOOK = REGISTER.register(
            "iron_zipline_hook",
            () -> new IronZiplineHookBlock(AbstractBlock.Properties
                    .of(Material.METAL)
                    .strength(1.0f, 3.0f)
                    .sound(SoundType.CHAIN)
            )
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

}
