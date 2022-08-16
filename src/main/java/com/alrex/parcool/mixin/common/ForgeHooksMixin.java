package com.alrex.parcool.mixin.common;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public abstract class ForgeHooksMixin {
	@Inject(method = "isLivingOnLadder", at = @At("HEAD"), cancellable = true)
	private static void onIsLivingOnLadder(BlockState state, World world, BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		boolean isSpectator = (entity instanceof PlayerEntity && ((PlayerEntity) entity).isSpectator());
		if (isSpectator) {
			cir.setReturnValue(false);
			return;
		}
		if (!ForgeConfig.SERVER.fullBoundingBoxLadders.get()) {
			cir.setReturnValue(isLadder(state, world, pos, entity));
		} else {
			AxisAlignedBB bb = entity.getBoundingBox();
			int mX = MathHelper.floor(bb.minX);
			int mY = MathHelper.floor(bb.minY);
			int mZ = MathHelper.floor(bb.minZ);
			for (int y2 = mY; y2 < bb.maxY; y2++) {
				for (int x2 = mX; x2 < bb.maxX; x2++) {
					for (int z2 = mZ; z2 < bb.maxZ; z2++) {
						BlockPos tmp = new BlockPos(x2, y2, z2);
						state = world.getBlockState(tmp);
						if (isLadder(state, world, tmp, entity)) {
							cir.setReturnValue(true);
							return;
						}
					}
				}
			}
			cir.setReturnValue(false);
		}
	}

	private static boolean isLadder(BlockState state, World world, BlockPos pos, LivingEntity entity) {
		Block block = state.getBlockState().getBlock();
		if (block instanceof FenceBlock || block instanceof PaneBlock) {
			int count = 0;
			if (state.getValue(FourWayBlock.NORTH)) count++;
			if (state.getValue(FourWayBlock.SOUTH)) count++;
			if (state.getValue(FourWayBlock.EAST)) count++;
			if (state.getValue(FourWayBlock.WEST)) count++;
			return count <= 0;
		} else if (block instanceof RotatedPillarBlock) {
			return state.getValue(RotatedPillarBlock.AXIS).isVertical();
		} else {
			return block.isLadder(state.getBlockState(), world, pos, entity);
		}
	}
}
