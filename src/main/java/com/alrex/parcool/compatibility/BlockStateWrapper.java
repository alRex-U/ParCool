package com.alrex.parcool.compatibility;

import com.alrex.parcool.common.tags.BlockTags;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockStateWrapper {
    public static final Minecraft mc = Minecraft.getInstance();
    private final BlockState block;

    public BlockStateWrapper(BlockState block) {
        this.block = block;
    }
    
    public float getFriction(IWorldReader world, BlockPos pos, Entity entity) {
        return block.getSlipperiness(world, pos, entity);
    }

    public BlockState getInstance() {
        return block;
    }

    public SoundType getSoundType() {
        return block.getSoundType();
    }

    public Block getBlock() {
        return block.getBlock();
    }

    public boolean isCollisionShapeFullBlock(World world, BlockPos pos) {
        return block.isCollisionShapeFullBlock(world, pos);
    }

    public <T extends Comparable<T>> T getValue(Property<T> axis) {
        return block.getValue(axis);
    }

    public Direction getValue(DirectionProperty facing) {
        return block.getValue(facing);
    }

    public BlockState getBlockState() {
        return block.getBlockState();
    }

    public static BlockStateWrapper get(BlockState stateBase) {
        return new BlockStateWrapper(stateBase);
    }

    public boolean is(Block block2) {
        return block.getBlock() == block2;
    }

    public boolean isAir() {
        return block.isAir();
    }

    public BlockRenderType getRenderShape() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRenderShape'");
    }

    public boolean isHideAbleBlock() {
		return block.getBlock().getTags().contains(BlockTags.HIDE_ABLE);
	}

    public IParticleData getBlockParticleData(ParticleType<BlockParticleData> block2, BlockPos blockpos) {
        return new BlockParticleData(ParticleTypes.BLOCK, block).setPos(blockpos);
    }

    public void destroyParticle(BlockPos pos) {
        mc.particleEngine.destroy(pos, block);
    }
}
