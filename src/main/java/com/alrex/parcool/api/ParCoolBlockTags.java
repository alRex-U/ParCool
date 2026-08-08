package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ParCoolBlockTags {
    public static final TagKey<Block> HIDE_ABLE = net.minecraft.tags.BlockTags.create(ParCool.resourceLocation("hide_able"));
    public static final TagKey<Block> POLE_CLIMBABLE = net.minecraft.tags.BlockTags.create(ParCool.resourceLocation("pole_climbable"));
}
