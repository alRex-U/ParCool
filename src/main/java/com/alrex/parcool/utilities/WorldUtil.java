package com.alrex.parcool.utilities;

import com.alrex.parcool.common.action.impl.HangDown;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WorldUtil {

	public static Vector3d getRunnableWall(LivingEntity entity, double range) {
		double width = entity.getBbWidth() / 2;
		double wallX = 0;
		double wallZ = 0;
		Vector3d pos = entity.position();

		AxisAlignedBB baseBox = new AxisAlignedBB(
				pos.x() - width,
				pos.y(),
				pos.z() - width,
				pos.x() + width,
				pos.y() + entity.getBbHeight(),
				pos.z() + width
		);

		if (!entity.level.noCollision(baseBox.expandTowards(range, 0, 0))) {
			wallX++;
		}
		if (!entity.level.noCollision(baseBox.expandTowards(-range, 0, 0))) {
			wallX--;
		}
		if (!entity.level.noCollision(baseBox.expandTowards(0, 0, range))) {
			wallZ++;
		}
		if (!entity.level.noCollision(baseBox.expandTowards(0, 0, -range))) {
			wallZ--;
		}
		if (wallX == 0 && wallZ == 0) return null;

		return new Vector3d(wallX, 0, wallZ);
	}

	@Nullable
	public static Vector3d getWall(LivingEntity entity) {
		double range = entity.getBbWidth() / 2;
		final double width = entity.getBbWidth() * 0.5;
		double wallX = 0;
		double wallZ = 0;
		Vector3d pos = entity.position();

		AxisAlignedBB baseBox = new AxisAlignedBB(
				pos.x() - width,
				pos.y(),
				pos.z() - width,
				pos.x() + width,
				pos.y() + entity.getBbHeight(),
				pos.z() + width
		);

		if (!entity.level.noCollision(baseBox.expandTowards(range, 0, 0))) {
			wallX++;
		}
		if (!entity.level.noCollision(baseBox.expandTowards(-range, 0, 0))) {
			wallX--;
		}
		if (!entity.level.noCollision(baseBox.expandTowards(0, 0, range))) {
			wallZ++;
		}
		if (!entity.level.noCollision(baseBox.expandTowards(0, 0, -range))) {
			wallZ--;
		}
		if (wallX == 0 && wallZ == 0) return null;

		return new Vector3d(wallX, 0, wallZ);
	}

	@Nullable
	public static Vector3d getVaultableStep(LivingEntity entity) {
		final double d = entity.getBbWidth() * 0.5;
		World world = entity.level;
		double distance = entity.getBbWidth() / 2;
		double baseLine = Math.min(entity.getBbHeight() * 0.86, getWallHeight(entity));
		double stepX = 0;
		double stepZ = 0;
		Vector3d pos = entity.position();

		AxisAlignedBB baseBoxBottom = new AxisAlignedBB(
				pos.x() - d,
				pos.y(),
				pos.z() - d,
				pos.x() + d,
				pos.y() + baseLine,
				pos.z() + d
		);
		AxisAlignedBB baseBoxTop = new AxisAlignedBB(
				pos.x() - d,
				pos.y() + baseLine,
				pos.z() - d,
				pos.x() + d,
				pos.y() + baseLine + entity.getBbHeight(),
				pos.z() + d
		);
		if (!world.noCollision(baseBoxBottom.expandTowards(distance, 0, 0)) && world.noCollision(baseBoxTop.expandTowards((distance + 1.8), 0, 0))) {
			stepX++;
		}
		if (!world.noCollision(baseBoxBottom.expandTowards(-distance, 0, 0)) && world.noCollision(baseBoxTop.expandTowards(-(distance + 1.8), 0, 0))) {
			stepX--;
		}
		if (!world.noCollision(baseBoxBottom.expandTowards(0, 0, distance)) && world.noCollision(baseBoxTop.expandTowards(0, 0, (distance + 1.8)))) {
			stepZ++;
		}
		if (!world.noCollision(baseBoxBottom.expandTowards(0, 0, -distance)) && world.noCollision(baseBoxTop.expandTowards(0, 0, -(distance + 1.8)))) {
			stepZ--;
		}
		if (stepX == 0 && stepZ == 0) return null;

		return new Vector3d(stepX, 0, stepZ);
	}

	public static double getWallHeight(LivingEntity entity) {
		Vector3d wall = getWall(entity);
		if (wall == null) return 0;
		World world = entity.level;
		final double accuracy = entity.getBbHeight() / 18; // normally about 0.1
		final double d = entity.getBbWidth() * 0.5;
		int loopNum = (int) Math.round(entity.getBbHeight() / accuracy);
		Vector3d pos = entity.position();
		double x1 = pos.x() + d + (wall.x() > 0 ? 1 : 0);
		double y1 = pos.y();
		double z1 = pos.z() + d + (wall.z() > 0 ? 1 : 0);
		double x2 = pos.x() - d + (wall.x() < 0 ? -1 : 0);
		double z2 = pos.z() - d + (wall.z() < 0 ? -1 : 0);
		boolean canReturn = false;
		for (int i = 0; i < loopNum; i++) {
			AxisAlignedBB box = new AxisAlignedBB(
					x1, y1 + accuracy * i, z1, x2, y1 + accuracy * (i + 1), z2
			);

			if (!world.noCollision(box)) {
				canReturn = true;
			} else {
				if (canReturn) return accuracy * i;
			}
		}
		return entity.getBbHeight();
	}

	@Nullable
	public static HangDown.BarAxis getHangableBars(LivingEntity entity) {
		final double bbWidth = entity.getBbWidth() / 4;
		final double bbHeight = 0.35;
		AxisAlignedBB bb = new AxisAlignedBB(
				entity.getX() - bbWidth,
				entity.getY() + entity.getBbHeight(),
				entity.getZ() - bbWidth,
				entity.getX() + bbWidth,
				entity.getY() + entity.getBbHeight() + bbHeight,
				entity.getZ() + bbWidth
		);
		if (entity.level.noCollision(bb)) return null;
		BlockPos pos = new BlockPos(
				entity.getX(),
				entity.getY() + entity.getBbHeight() + 0.4,
				entity.getZ()
		);
		if (!entity.level.isLoaded(pos)) return null;
		BlockState state = entity.level.getBlockState(pos);
		Block block = state.getBlock();
		HangDown.BarAxis axis = null;
		if (block instanceof RotatedPillarBlock) {
			if (state.isCollisionShapeFullBlock(entity.level, pos)) {
				return null;
			}
			Direction.Axis pillarAxis = state.getValue(RotatedPillarBlock.AXIS);
			switch (pillarAxis) {
				case X:
					axis = HangDown.BarAxis.X;
					break;
				case Z:
					axis = HangDown.BarAxis.Z;
					break;
			}
		} else if (block instanceof DirectionalBlock) {
			if (state.isCollisionShapeFullBlock(entity.level, pos)) {
				return null;
			}
			Direction direction = state.getValue(DirectionalBlock.FACING);
			switch (direction) {
				case EAST:
				case WEST:
					axis = HangDown.BarAxis.X;
					break;
				case NORTH:
				case SOUTH:
					axis = HangDown.BarAxis.Z;
			}
		} else if (block instanceof FourWayBlock) {
			int zCount = 0;
			int xCount = 0;
			if (state.getValue(FourWayBlock.NORTH)) zCount++;
			if (state.getValue(FourWayBlock.SOUTH)) zCount++;
			if (state.getValue(FourWayBlock.EAST)) xCount++;
			if (state.getValue(FourWayBlock.WEST)) xCount++;
			if (zCount > 0 && xCount == 0) axis = HangDown.BarAxis.Z;
			if (xCount > 0 && zCount == 0) axis = HangDown.BarAxis.X;
		} else if (block instanceof WallBlock) {
			int zCount = 0;
			int xCount = 0;
			if (state.getValue(WallBlock.NORTH_WALL) != WallHeight.NONE) zCount++;
			if (state.getValue(WallBlock.SOUTH_WALL) != WallHeight.NONE) zCount++;
			if (state.getValue(WallBlock.EAST_WALL) != WallHeight.NONE) xCount++;
			if (state.getValue(WallBlock.WEST_WALL) != WallHeight.NONE) xCount++;
			if (zCount > 0 && xCount == 0) axis = HangDown.BarAxis.Z;
			if (xCount > 0 && zCount == 0) axis = HangDown.BarAxis.X;
		}

		return axis;
	}

	public static boolean existsDivableSpace(LivingEntity entity) {
		World world = entity.level;
		Vector3d lookAngle = entity.getLookAngle();
		Vector3d center = entity.position().add(new Vector3d(lookAngle.x(), 0, lookAngle.z()).normalize().multiply(3, 0, 3));
		if (!world.isLoaded(new BlockPos(center))) {
			return false;
		}
		BlockPos centerPos = new BlockPos(center);
		final int neededSpaceHeight = 9;
		boolean hasSpace = true;
		for (int i = 0; i < neededSpaceHeight; i++) {
			hasSpace = !world.getBlockState(centerPos).getMaterial().blocksMotion();
			hasSpace = hasSpace && world.isLoaded(centerPos.west()) && !world.getBlockState(centerPos.west()).getMaterial().blocksMotion();
			hasSpace = hasSpace && world.isLoaded(centerPos.east()) && !world.getBlockState(centerPos.east()).getMaterial().blocksMotion();
			hasSpace = hasSpace && world.isLoaded(centerPos.north()) && !world.getBlockState(centerPos.north()).getMaterial().blocksMotion();
			hasSpace = hasSpace && world.isLoaded(centerPos.south()) && !world.getBlockState(centerPos.south()).getMaterial().blocksMotion();
			if (!hasSpace) break;
			centerPos = centerPos.below();
		}
		if (!hasSpace) return false;
		center = entity.position().add(new Vector3d(lookAngle.x(), 0, lookAngle.z()).normalize().multiply(5, 0, 5));
		centerPos = new BlockPos(center);
		for (int i = 0; i < neededSpaceHeight; i++) {
			hasSpace = !world.getBlockState(centerPos).getMaterial().blocksMotion();
			hasSpace = hasSpace && world.isLoaded(centerPos.west()) && !world.getBlockState(centerPos.west()).getMaterial().blocksMotion();
			hasSpace = hasSpace && world.isLoaded(centerPos.east()) && !world.getBlockState(centerPos.east()).getMaterial().blocksMotion();
			hasSpace = hasSpace && world.isLoaded(centerPos.north()) && !world.getBlockState(centerPos.north()).getMaterial().blocksMotion();
			hasSpace = hasSpace && world.isLoaded(centerPos.south()) && !world.getBlockState(centerPos.south()).getMaterial().blocksMotion();
			if (!hasSpace) break;
			centerPos = centerPos.below();
		}
		return hasSpace;
	}

	@Nullable
	public static Vector3d getGrabbableWall(LivingEntity entity) {
		final double d = entity.getBbWidth() * 0.5;
		World world = entity.level;
		double distance = entity.getBbWidth() / 2;
		double baseLine1 = entity.getEyeHeight() + (entity.getBbHeight() - entity.getEyeHeight()) / 2;
		double baseLine2 = entity.getBbHeight() + (entity.getBbHeight() - entity.getEyeHeight()) / 2;
		Vector3d wall1 = getGrabbableWall(entity, distance, baseLine1);
		if (wall1 != null) return wall1;
		return getGrabbableWall(entity, distance, baseLine2);
	}

	private static Vector3d getGrabbableWall(LivingEntity entity, double distance, double baseLine) {
		final double d = entity.getBbWidth() * 0.49;
		World world = entity.level;
		Vector3d pos = entity.position();
		AxisAlignedBB baseBoxSide = new AxisAlignedBB(
				pos.x() - d,
				pos.y() + baseLine - entity.getBbHeight() / 6,
				pos.z() - d,
				pos.x() + d,
				pos.y() + baseLine,
				pos.z() + d
		);
		AxisAlignedBB baseBoxTop = new AxisAlignedBB(
				pos.x() - d,
				pos.y() + baseLine,
				pos.z() - d,
				pos.x() + d,
				pos.y() + entity.getBbHeight(),
				pos.z() + d
		);
		int xDirection = 0;
		int zDirection = 0;

		if (!world.noCollision(baseBoxSide.expandTowards(distance, 0, 0)) && world.noCollision(baseBoxTop.expandTowards(distance, 0, 0)))
			xDirection++;
		if (!world.noCollision(baseBoxSide.expandTowards(-distance, 0, 0)) && world.noCollision(baseBoxTop.expandTowards(-distance, 0, 0)))
			xDirection--;
		if (!world.noCollision(baseBoxSide.expandTowards(0, 0, distance)) && world.noCollision(baseBoxTop.expandTowards(0, 0, distance)))
			zDirection++;
		if (!world.noCollision(baseBoxSide.expandTowards(0, 0, -distance)) && world.noCollision(baseBoxTop.expandTowards(0, 0, -distance)))
			zDirection--;
		if (xDirection == 0 && zDirection == 0) {
			return null;
		}
		float slipperiness;
		if (xDirection != 0 && zDirection != 0) {
			BlockPos blockPos1 = new BlockPos(
					entity.getX() + xDirection,
					entity.getBoundingBox().minY + baseLine - 0.3,
					entity.getZ()
			);
			BlockPos blockPos2 = new BlockPos(
					entity.getX(),
					entity.getBoundingBox().minY + baseLine - 0.3,
					entity.getZ() + zDirection
			);
			if (!entity.level.isLoaded(blockPos1)) return null;
			if (!entity.level.isLoaded(blockPos2)) return null;
			slipperiness = Math.min(
					entity.level.getBlockState(blockPos1).getSlipperiness(entity.level, blockPos1, entity),
					entity.level.getBlockState(blockPos2).getSlipperiness(entity.level, blockPos2, entity)
			);
		} else {
			BlockPos blockPos = new BlockPos(
					entity.getX() + xDirection,
					entity.getBoundingBox().minY + baseLine - 0.3,
					entity.getZ() + zDirection
			);
			if (!entity.level.isLoaded(blockPos)) return null;
			slipperiness = entity.level.getBlockState(blockPos).getSlipperiness(entity.level, blockPos, entity);
		}
		return slipperiness <= 0.9 ? new Vector3d(xDirection, 0, zDirection) : null;
	}
}
