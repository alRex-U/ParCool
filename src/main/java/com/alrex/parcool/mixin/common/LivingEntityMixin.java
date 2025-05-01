package com.alrex.parcool.mixin.common;

import com.alrex.parcool.api.compatibility.EventBusWrapper;
import com.alrex.parcool.api.compatibility.LivingEntityWrapper;
import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.ClimbPoles;
import com.alrex.parcool.common.action.impl.ClimbUp;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Shadow
    public abstract void releaseUsingItem();

	public LivingEntityMixin(EntityType<?> p_i48580_1_, World p_i48580_2_) {
		super(p_i48580_1_, p_i48580_2_);
	}

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;onClimbable()Z", at = @At("HEAD"), cancellable = true)
	public void onClimbable(CallbackInfoReturnable<Boolean> cir) {
		if (this.isSpectator()) {
			cir.setReturnValue(false);
		} else {
			LivingEntityWrapper entity = LivingEntityWrapper.get(this);
			PlayerWrapper player = PlayerWrapper.getOrDefault(entity);
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) {
				return;
			}
			if (!parkourability.getActionInfo().can(ClimbPoles.class)
					|| EventBusWrapper.tryToStartEvent(player, parkourability.get(ClimbPoles.class))
			) {
				return;
            }
            if (parkourability.get(ClimbUp.class).isDoing()) {
                return;
			}
			ChargeJump chargeJump = parkourability.get(ChargeJump.class);
			if (chargeJump.isDoing() || chargeJump.isCharging()) {
				return;
			}
			BlockPos blockpos = this.blockPosition();
			BlockState blockstate = this.getFeetBlockState();
			boolean onLadder = parCool$isLivingOnCustomLadder(blockstate, entity.getLevel(), blockpos, entity);
			if (onLadder) {
				cir.setReturnValue(true);
			}
		}
	}

	@Unique
	public boolean parCool$isLivingOnCustomLadder(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull LivingEntityWrapper entity) {
		boolean isSpectator = PlayerWrapper.is(entity) && entity.isSpectator();
		if (isSpectator) return false;
		if (!ForgeConfig.SERVER.fullBoundingBoxLadders.get()) {
			return parCool$isCustomLadder(state, world, pos, entity);
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
						if (parCool$isCustomLadder(state, world, tmp, entity)) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}

	@Unique
	private boolean parCool$isCustomLadder(BlockState state, World world, BlockPos pos, LivingEntityWrapper entity) {
		Block block = state.getBlockState().getBlock();
		if (block instanceof FourWayBlock) {
			int zCount = 0;
			int xCount = 0;
			if (state.getValue(FourWayBlock.NORTH)) zCount++;
			if (state.getValue(FourWayBlock.SOUTH)) zCount++;
			if (state.getValue(FourWayBlock.EAST)) xCount++;
			if (state.getValue(FourWayBlock.WEST)) xCount++;
			boolean stacked = world.isLoaded(pos.above()) && world.getBlockState(pos.above()).getBlock() instanceof FourWayBlock;
			if (!stacked && world.isLoaded(pos.below()) && world.getBlockState(pos.below()).getBlock() instanceof FourWayBlock)
				stacked = true;

			return ((zCount + xCount <= 1) || (zCount == 1 && xCount == 1)) && stacked;
		} else if (block instanceof RotatedPillarBlock) {
			boolean stacked = world.isLoaded(pos.above()) && world.getBlockState(pos.above()).getBlock() instanceof RotatedPillarBlock;
			if (!stacked && world.isLoaded(pos.below()) && world.getBlockState(pos.below()).getBlock() instanceof RotatedPillarBlock)
				stacked = true;
			return !state.isCollisionShapeFullBlock(world, pos) && state.getValue(RotatedPillarBlock.AXIS).isVertical();
		} else if (block instanceof DirectionalBlock) {
			Direction direction = state.getValue(DirectionalBlock.FACING);
			return !state.isCollisionShapeFullBlock(world, pos) && (direction == Direction.UP || direction == Direction.DOWN);
		}
		return false;
	}
}
