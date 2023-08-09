package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.action.impl.ClimbPoles;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	public abstract BlockState getFeetBlockState();

	@Shadow
	public abstract void readAdditionalSaveData(CompoundNBT p_70037_1_);

	public LivingEntityMixin(EntityType<?> p_i48580_1_, World p_i48580_2_) {
		super(p_i48580_1_, p_i48580_2_);
	}

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;onClimbable()Z", at = @At("HEAD"), cancellable = true)
	public void onClimbable(CallbackInfoReturnable<Boolean> cir) {
		if (this.isSpectator()) {
			cir.cancel();
			cir.setReturnValue(false);
		} else {
			LivingEntity entity = (LivingEntity) (Object) this;
			if (!(entity instanceof PlayerEntity)) {
				return;
			}
			PlayerEntity player = (PlayerEntity) entity;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) {
				return;
			}
			if (!parkourability.getClientInfo().getPossibilityOf(ClimbPoles.class)) {
				return;
			}
			BlockPos blockpos = this.blockPosition();
			BlockState blockstate = this.getFeetBlockState();
			boolean onLadder = isLivingOnCustomLadder(blockstate, entity.level, blockpos, entity);
			if (onLadder) {
				cir.cancel();
				cir.setReturnValue(true);
			}
		}
	}

	public boolean isLivingOnCustomLadder(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull LivingEntity entity) {
		boolean isSpectator = (entity instanceof PlayerEntity && entity.isSpectator());
		if (isSpectator) return false;
		if (!ForgeConfig.SERVER.fullBoundingBoxLadders.get()) {
			return isCustomLadder(state, world, pos, entity);
		} else {
			AxisAlignedBB bb = entity.getBoundingBox();
			int mX = MathHelper.floor(bb.minX);
			int mY = MathHelper.floor(bb.minY);
			int mZ = MathHelper.floor(bb.minZ);
			for (int y2 = mY; y2 < bb.maxY; y2++) {
				for (int x2 = mX; x2 < bb.maxX; x2++) {
					for (int z2 = mZ; z2 < bb.maxZ; z2++) {
						BlockPos tmp = new BlockPos(x2, y2, z2);
						if (!world.isLoaded(pos)) {
							return false;
						}
						state = world.getBlockState(tmp);
						if (isCustomLadder(state, world, tmp, entity)) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}

	private boolean isCustomLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		Block block = state.getBlockState().getBlock();
		if (block instanceof FourWayBlock) {
			int zCount = 0;
			int xCount = 0;
			if (state.getValue(FourWayBlock.NORTH)) zCount++;
			if (state.getValue(FourWayBlock.SOUTH)) zCount++;
			if (state.getValue(FourWayBlock.EAST)) xCount++;
			if (state.getValue(FourWayBlock.WEST)) xCount++;
			return (zCount + xCount <= 1) || (zCount == 1 && xCount == 1);
		} else if (block instanceof RotatedPillarBlock) {
			return !state.isCollisionShapeFullBlock(world, pos) && state.getValue(RotatedPillarBlock.AXIS).isVertical();
		} else if (block instanceof DirectionalBlock) {
			Direction direction = state.getValue(DirectionalBlock.FACING);
			return !state.isCollisionShapeFullBlock(world, pos) && (direction == Direction.UP || direction == Direction.DOWN);
		}
		return false;
	}
}
