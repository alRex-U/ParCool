package com.alrex.parcool.common.tags;

import com.alrex.parcool.ParCool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTags {
    public static final TagKey<Block> HIDE_ABLE = net.minecraft.tags.BlockTags.create(new ResourceLocation(ParCool.MOD_ID, "hide_able"));
    public static final TagKey<Block> POLE_CLIMBABLE = net.minecraft.tags.BlockTags.create(new ResourceLocation(ParCool.MOD_ID, "pole_climbable"));
}
