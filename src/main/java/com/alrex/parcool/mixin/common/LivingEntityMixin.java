package com.alrex.parcool.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
		super(p_i48580_1_, p_i48580_2_);
	}

	@Inject(method = "Lnet/minecraft/world/entity/LivingEntity;onClimbable()Z", at = @At("HEAD"), cancellable = true)
	public void onClimbable(CallbackInfoReturnable<Boolean> cir) {
		cir.cancel();
		if (this.isSpectator()) {
			cir.setReturnValue(false);
		} else {
			LivingEntity entity = (LivingEntity) (Object) this;
			BlockPos blockpos = this.blockPosition();
			BlockState blockstate = this.getFeetBlockState();
			cir.setReturnValue(isLivingOnLadder(blockstate, entity.level, blockpos, entity));
		}
	}

	public boolean isLivingOnLadder(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull LivingEntity entity) {
		boolean isSpectator = (entity instanceof Player && ((Player) entity).isSpectator());
		if (isSpectator) return false;
		if (!ForgeConfig.SERVER.fullBoundingBoxLadders.get()) {
			return isLadder(state, world, pos, entity);
		} else {
			AABB bb = entity.getBoundingBox();
			int mX = Mth.floor(bb.minX);
			int mY = Mth.floor(bb.minY);
			int mZ = Mth.floor(bb.minZ);
			for (int y2 = mY; y2 < bb.maxY; y2++) {
				for (int x2 = mX; x2 < bb.maxX; x2++) {
					for (int z2 = mZ; z2 < bb.maxZ; z2++) {
						BlockPos tmp = new BlockPos(x2, y2, z2);
						state = world.getBlockState(tmp);
						if (isLadder(state, world, tmp, entity)) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}

	private boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
		Block block = state.getBlock();
		if (block instanceof FenceBlock) {
			int count = 0;
			if (state.getValue(CrossCollisionBlock.NORTH)) count++;
			if (state.getValue(CrossCollisionBlock.SOUTH)) count++;
			if (state.getValue(CrossCollisionBlock.EAST)) count++;
			if (state.getValue(CrossCollisionBlock.WEST)) count++;
			return count <= 0;
		} else if (block instanceof RotatedPillarBlock) {
			return state.getValue(RotatedPillarBlock.AXIS).isVertical();
		} else if (block instanceof EndRodBlock) {
			Direction direction = state.getValue(DirectionalBlock.FACING);
			return direction == Direction.UP || direction == Direction.DOWN;
		} else {
			return block.isLadder(state, world, pos, entity);
		}
	}
}
