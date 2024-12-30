package com.alrex.parcool.mixin.common;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.ClimbPoles;
import com.alrex.parcool.common.action.impl.ClimbUp;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
		super(p_i48580_1_, p_i48580_2_);
	}

	@Inject(method = "net.minecraft.world.entity.LivingEntity.onClimbable", at = @At("HEAD"), cancellable = true)
	public void onClimbable(CallbackInfoReturnable<Boolean> cir) {
		if (this.isSpectator()) {
			cir.setReturnValue(false);
		} else {
			LivingEntity entity = (LivingEntity) (Object) this;
			if (!(entity instanceof Player)) {
				return;
			}
			Player player = (Player) entity;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) {
				return;
			}
			if (!parkourability.getActionInfo().can(ClimbPoles.class)
					|| MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStartEvent(player, parkourability.get(ClimbPoles.class)))
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
			boolean onLadder = parCool$isLivingOnCustomLadder(blockstate, entity.level(), blockpos, entity);
			if (onLadder) {
				cir.setReturnValue(true);
			}
		}
	}

	@Unique
	public boolean parCool$isLivingOnCustomLadder(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull LivingEntity entity) {
		boolean isSpectator = (entity instanceof Player && entity.isSpectator());
		if (isSpectator) return false;
		if (!ForgeConfig.SERVER.fullBoundingBoxLadders.get()) {
			return parCool$isCustomLadder(state, world, pos, entity);
		} else {
			AABB bb = entity.getBoundingBox();
			int mX = Mth.floor(bb.minX);
			int mY = Mth.floor(bb.minY);
			int mZ = Mth.floor(bb.minZ);
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
	private boolean parCool$isCustomLadder(BlockState state, Level world, BlockPos pos, LivingEntity entity) {
		Block block = state.getBlock();
		if (block instanceof CrossCollisionBlock) {
			int zCount = 0;
			int xCount = 0;
			if (state.getValue(CrossCollisionBlock.NORTH)) zCount++;
			if (state.getValue(CrossCollisionBlock.SOUTH)) zCount++;
			if (state.getValue(CrossCollisionBlock.EAST)) xCount++;
			if (state.getValue(CrossCollisionBlock.WEST)) xCount++;
			boolean stacked = world.isLoaded(pos.above()) && world.getBlockState(pos.above()).getBlock() instanceof CrossCollisionBlock;
			if (!stacked && world.isLoaded(pos.below()) && world.getBlockState(pos.below()).getBlock() instanceof CrossCollisionBlock)
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
