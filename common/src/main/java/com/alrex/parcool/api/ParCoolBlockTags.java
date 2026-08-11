package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ParCoolBlockTags {
    public static final TagKey<Block> HIDE_ABLE = TagKey.create(Registry.BLOCK_REGISTRY, ParCool.resourceLocation("hide_able"));
    public static final TagKey<Block> POLE_CLIMBABLE = TagKey.create(Registry.BLOCK_REGISTRY, ParCool.resourceLocation("pole_climbable"));
}
