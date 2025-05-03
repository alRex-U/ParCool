package com.alrex.parcool.utilities;

import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.tags.BlockTags;
import com.alrex.parcool.compatibility.AABBWrapper;
import com.alrex.parcool.compatibility.BlockStateWrapper;
import com.alrex.parcool.compatibility.EntityWrapper;
import com.alrex.parcool.compatibility.LevelWrapper;
import com.alrex.parcool.compatibility.LivingEntityWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import net.minecraft.block.*;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class WorldUtil {

	public static Vec3Wrapper getRunnableWall(LivingEntityWrapper entity, double range) {
		double width = entity.getBbWidth() * 0.4f;
		double wallX = 0;
		double wallZ = 0;
		Vec3Wrapper pos = entity.position();

		AABBWrapper baseBox1 = new AABBWrapper(
				pos.x() - width,
				pos.y(),
				pos.z() - width,
				pos.x() + width,
				pos.y() + entity.getBbHeight() / 1.63,
				pos.z() + width
		);
		AABBWrapper baseBox2 = new AABBWrapper(
				pos.x() - width,
				pos.y() + entity.getBbHeight() / 1.63,
				pos.z() - width,
				pos.x() + width,
				pos.y() + entity.getBbHeight(),
				pos.z() + width
		);

		if (!entity.noCollision(baseBox1.expandTowards(range, 0, 0))
				&& !entity.noCollision(baseBox2.expandTowards(range, 0, 0))
		) {
			wallX++;
		}
		if (!entity.noCollision(baseBox1.expandTowards(-range, 0, 0))
				&& !entity.noCollision(baseBox2.expandTowards(-range, 0, 0))
		) {
			wallX--;
		}
		if (!entity.noCollision(baseBox1.expandTowards(0, 0, range))
				&& !entity.noCollision(baseBox2.expandTowards(0, 0, range))
		) {
			wallZ++;
		}
		if (!entity.noCollision(baseBox1.expandTowards(0, 0, -range))
				&& !entity.noCollision(baseBox1.expandTowards(0, 0, -range))
		) {
			wallZ--;
		}
		if (wallX == 0 && wallZ == 0) return null;

		return new Vec3Wrapper(wallX, 0, wallZ);
	}

	@Nullable
	public static Vec3Wrapper getWall(LivingEntityWrapper entity) {
		return getWall(entity, entity.getBbWidth() * 0.5);
	}
	@Nullable
	public static Vec3Wrapper getWall(LivingEntityWrapper entity, double range) {
		final double width = entity.getBbWidth() * 0.49;
		double wallX = 0;
		double wallZ = 0;
		Vec3Wrapper pos = entity.position();

		AABBWrapper baseBox = new AABBWrapper(
				pos.x() - width,
				pos.y(),
				pos.z() - width,
				pos.x() + width,
				pos.y() + entity.getBbHeight(),
				pos.z() + width
		);

		if (!entity.noCollision(baseBox.expandTowards(range, 0, 0))) {
			wallX++;
		}
		if (!entity.noCollision(baseBox.expandTowards(-range, 0, 0))) {
			wallX--;
		}
		if (!entity.noCollision(baseBox.expandTowards(0, 0, range))) {
			wallZ++;
		}
		if (!entity.noCollision(baseBox.expandTowards(0, 0, -range))) {
			wallZ--;
		}
		if (wallX == 0 && wallZ == 0) return null;

		return new Vec3Wrapper(wallX, 0, wallZ);
	}

	@Nullable
	public static Vec3Wrapper getVaultableStep(LivingEntityWrapper entity) {
		final double d = entity.getBbWidth() * 0.5;
		LevelWrapper world = entity.getLevel();
		double distance = entity.getBbWidth() / 2;
		double baseLine = Math.min(entity.getBbHeight() * 0.86, getWallHeight(entity));
		double stepX = 0;
		double stepZ = 0;
		Vec3Wrapper pos = entity.position();

		AABBWrapper baseBoxBottom = new AABBWrapper(
				pos.x() - d,
				pos.y(),
				pos.z() - d,
				pos.x() + d,
				pos.y() + baseLine,
				pos.z() + d
		);
		AABBWrapper baseBoxTop = new AABBWrapper(
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
		if (stepX == 0 || stepZ == 0) {
			Vec3Wrapper result = new Vec3Wrapper(stepX, 0, stepZ);
			BlockPos target = new BlockPos(entity.position().add(result).add(0, 0.5, 0));
			if (!world.isLoaded(target)) return null;
			BlockStateWrapper state = world.getBlockState(target);
			if (state.getBlock() instanceof StairsBlock) {
				Half half = state.getValue(StairsBlock.HALF);
				if (half != Half.BOTTOM) return result;
				Direction direction = state.getValue(StairsBlock.FACING);
				if (stepZ > 0 && direction == Direction.SOUTH) return null;
				if (stepZ < 0 && direction == Direction.NORTH) return null;
				if (stepX > 0 && direction == Direction.EAST) return null;
				if (stepX < 0 && direction == Direction.WEST) return null;
			}
		}

		return new Vec3Wrapper(stepX, 0, stepZ);
	}

	public static double getWallHeight(LivingEntityWrapper entity, Vec3Wrapper direction, double maxHeight, double accuracy) {
		final double d = entity.getBbWidth() * 0.49;
		direction = direction.normalize();
		LevelWrapper world = entity.getLevel();
		Vec3Wrapper pos = entity.position();
		boolean canReturn = false;
		for (double height = 0; height < maxHeight; height += accuracy) {
			AABBWrapper box = new AABBWrapper(
					pos.x() + d + (direction.x() > 0 ? 1 : 0),
					pos.y() + height,
					pos.z() + d + (direction.z() > 0 ? 1 : 0),
					pos.x() - d + (direction.x() < 0 ? -1 : 0),
					pos.y() + height + accuracy,
					pos.z() - d + (direction.z() < 0 ? -1 : 0)
			);
			if (!world.noCollision(box)) {
				canReturn = true;
			} else {
				if (canReturn) {
					return height;
				}
			}
		}
		return maxHeight;
	}

	public static double getWallHeight(LivingEntityWrapper entity) {
		Vec3Wrapper wall = getWall(entity);
		if (wall == null) return 0;
		LevelWrapper world = entity.getLevel();
		final double accuracy = entity.getBbHeight() / 18; // normally about 0.1
		final double d = entity.getBbWidth() * 0.5;
		int loopNum = (int) Math.round(entity.getBbHeight() / accuracy);
		Vec3Wrapper pos = entity.position();
		boolean canReturn = false;
		for (int i = 0; i < loopNum; i++) {
			AABBWrapper box = new AABBWrapper(
					pos.x() + d + (wall.x() > 0 ? 1 : 0),
					pos.y() + accuracy * i,
					pos.z() + d + (wall.z() > 0 ? 1 : 0),
					pos.x() - d + (wall.x() < 0 ? -1 : 0),
					pos.y() + accuracy * (i + 1),
					pos.z() - d + (wall.z() < 0 ? -1 : 0)
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
	public static HangDown.BarAxis getHangableBars(LivingEntityWrapper entity) {
		final double bbWidth = entity.getBbWidth() / 4;
		final double bbHeight = 0.35;
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		AABBWrapper bb = new AABBWrapper(
				x - bbWidth,
				y + entity.getBbHeight(),
				z - bbWidth,
				x + bbWidth,
				y + entity.getBbHeight() + bbHeight,
				z + bbWidth
		);
		if (entity.noCollision(bb)) return null;
		BlockPos pos = new BlockPos(
				x,
				y + entity.getBbHeight() + 0.4,
				z
		);
		if (!entity.isEveryLoaded(pos)) return null;
		BlockStateWrapper state = entity.getBlockState(pos);
		Block block = state.getBlock();
		HangDown.BarAxis axis = null;
		if (block instanceof RotatedPillarBlock) {
			if (entity.getLevel().isCollisionShapeFullBlock(state, pos)) {
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
			if (entity.getLevel().isCollisionShapeFullBlock(state, pos)) {
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

	public static boolean existsSpaceBelow(LivingEntityWrapper entity) {
		LevelWrapper world = entity.getLevel();
		Vec3Wrapper center = entity.position();
		if (!world.isLoaded(new BlockPos(center))) return false;
		double height = entity.getBbHeight() * 1.5;
		double width = entity.getBbWidth() * 2;
		AABBWrapper boundingBox = new AABBWrapper(
				center.x() - width,
				center.y() - 9,
				center.z() - width,
				center.x() + width,
				center.y() + height,
				center.z() + width
		);
		return world.noCollision(boundingBox);
	}
	public static boolean existsDivableSpace(LivingEntityWrapper entity) {
		LevelWrapper world = entity.getLevel();
		double width = entity.getBbWidth() * 1.5;
		double height = entity.getBbHeight() * 1.5;
		double wideWidth = entity.getBbWidth() * 2;
		Vec3Wrapper center = entity.position();
		if (!world.isLoaded(new BlockPos(center))) return false;
		Vec3Wrapper diveDirection = VectorUtil.fromYawDegree(entity.getYHeadRot());
		for (int i = 0; i < 4; i++) {
			Vec3Wrapper centerPoint = center.add(diveDirection.scale(width * i));
			AABBWrapper box = new AABBWrapper(
					centerPoint.x() - width,
					centerPoint.y() + 0.05,
					centerPoint.z() - width,
					centerPoint.x() + width,
					centerPoint.y() + height,
					centerPoint.z() + width
			);
			if (!world.noCollision(box)) return false;
		}
		center = center.add(diveDirection.scale(4));
		AABBWrapper verticalWideBox = new AABBWrapper(
				center.x() - wideWidth,
				center.y() - 9,
				center.z() - wideWidth,
				center.x() + wideWidth,
				center.y() + height,
				center.z() + wideWidth
		);
		if (world.noCollision(verticalWideBox)) return true;
		BlockPos centerBlockPos = new BlockPos(center.add(0, -0.5, 0));

		// check if water pool exists
		if (!world.isLoaded(centerBlockPos)) return false;
		verticalWideBox = new AABBWrapper(
				center.x() - wideWidth,
				center.y() - 2.9,
				center.z() - wideWidth,
				center.x() + wideWidth,
				center.y() + height,
				center.z() + wideWidth
		);
		int i = 0;
		int waterLevel = -1;
		for (; i < 6; i++) {
			Block block = world.getBlockState(centerBlockPos.below(i)).getBlock();
			if (block == Blocks.AIR) continue;
			if (block == Blocks.WATER) {
				waterLevel = i;
				break;
			}
			return false;
		}
		if (waterLevel == -1) return false;
		boolean filledWithWater = true;
		for (; i < waterLevel + 3; i++) {
			BlockStateWrapper state = world.getBlockState(centerBlockPos.below(i));
			if (state.getBlock() != Blocks.WATER) {
				filledWithWater = false;
				break;
			}
		}
		return filledWithWater && world.noCollision(verticalWideBox);
	}

	@Nullable
	public static Vec3Wrapper getGrabbableWall(LivingEntityWrapper entity) {
		final double d = entity.getBbWidth() * 0.5;
		LevelWrapper world = entity.getLevel();
		double distance = entity.getBbWidth() / 2;
		double baseLine1 = entity.getEyeHeight() + (entity.getBbHeight() - entity.getEyeHeight()) / 2;
		double baseLine2 = entity.getBbHeight() + (entity.getBbHeight() - entity.getEyeHeight()) / 2;
		Vec3Wrapper wall1 = getGrabbableWall(entity, distance, baseLine1);
		if (wall1 != null) return wall1;
		return getGrabbableWall(entity, distance, baseLine2);
	}

	private static Vec3Wrapper getGrabbableWall(LivingEntityWrapper entity, double distance, double baseLine) {
		final double d = entity.getBbWidth() * 0.49;
		LevelWrapper world = entity.getLevel();
		Vec3Wrapper pos = entity.position();
		AABBWrapper baseBoxSide = new AABBWrapper(
				pos.x() - d,
				pos.y() + baseLine - entity.getBbHeight() / 6,
				pos.z() - d,
				pos.x() + d,
				pos.y() + baseLine,
				pos.z() + d
		);
		AABBWrapper baseBoxTop = new AABBWrapper(
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
			BlockPos blockPos1 = entity.getAdjustedBlockPos(
					xDirection,
					baseLine - 0.3,
					0
			);
			BlockPos blockPos2 = entity.getAdjustedBlockPos(
					0,
					baseLine - 0.3,
					zDirection
			);
			if (!entity.isEveryLoaded(blockPos1, blockPos2)) return null;
			slipperiness = entity.getMinSlipperiness(blockPos1, blockPos2);
		} else {
			BlockPos blockPos = entity.getAdjustedBlockPos(
					xDirection,
					baseLine - 0.3,
					zDirection
			);
			if (!entity.isEveryLoaded(blockPos)) return null;
			slipperiness = entity.getMinSlipperiness(blockPos);
		}
		return slipperiness <= 0.9 ? new Vec3Wrapper(xDirection, 0, zDirection) : null;
	}

	private static boolean getHideAbleSpace$isHideAble(LevelWrapper world, Block block, BlockPos pos) {
		return world.isLoaded(pos) && world.getBlockState(pos).is(block) && world.isAir(pos.above());
	}

	@Nullable
	public static Tuple<BlockPos, BlockPos> getHideAbleSpace(EntityWrapper entity, BlockPos base) {
		LevelWrapper world = entity.getLevel();
		if (!world.isLoaded(base)) return null;
		BlockStateWrapper state = world.getBlockState(base);
		Block block = state.getBlock();
		if (!state.isHideAbleBlock()) return null;
		if (!world.isAir(base.above())) {
			if (getHideAbleSpace$isHideAble(world, block, base.above())) {
				return new Tuple<>(base, base.above());
			}
			return null;
		}
		double entityWidth = entity.getBbWidth();
		double entityHeight = entity.getBbHeight();
		if (entityHeight >= 2 || entityWidth >= 1) return null;
		if (entityHeight < 1) return new Tuple<>(base, base);
		Vec3Wrapper lookAngle = entity.getLookAngle();
		if (Math.abs(lookAngle.z()) > Math.abs(lookAngle.x())) {
			if (lookAngle.z() > 0) {
				if (getHideAbleSpace$isHideAble(world, block, base.south())) return new Tuple<>(base, base.south());
				if (lookAngle.x() > 0) {
					if (getHideAbleSpace$isHideAble(world, block, base.east())) return new Tuple<>(base, base.east());
					if (getHideAbleSpace$isHideAble(world, block, base.west())) return new Tuple<>(base, base.west());
				} else {
					if (getHideAbleSpace$isHideAble(world, block, base.west())) return new Tuple<>(base, base.west());
					if (getHideAbleSpace$isHideAble(world, block, base.east())) return new Tuple<>(base, base.east());
				}
				if (getHideAbleSpace$isHideAble(world, block, base.north())) return new Tuple<>(base, base.north());
			} else {
				if (getHideAbleSpace$isHideAble(world, block, base.north())) return new Tuple<>(base, base.north());
				if (lookAngle.x() > 0) {
					if (getHideAbleSpace$isHideAble(world, block, base.east())) return new Tuple<>(base, base.east());
					if (getHideAbleSpace$isHideAble(world, block, base.west())) return new Tuple<>(base, base.west());
				} else {
					if (getHideAbleSpace$isHideAble(world, block, base.west())) return new Tuple<>(base, base.west());
					if (getHideAbleSpace$isHideAble(world, block, base.east())) return new Tuple<>(base, base.east());
				}
				if (getHideAbleSpace$isHideAble(world, block, base.south())) return new Tuple<>(base, base.south());
			}
		} else {
			if (lookAngle.x() > 0) {
				if (getHideAbleSpace$isHideAble(world, block, base.east())) return new Tuple<>(base, base.east());
				if (lookAngle.z() > 0) {
					if (getHideAbleSpace$isHideAble(world, block, base.south())) return new Tuple<>(base, base.south());
					if (getHideAbleSpace$isHideAble(world, block, base.north())) return new Tuple<>(base, base.north());
				} else {
					if (getHideAbleSpace$isHideAble(world, block, base.north())) return new Tuple<>(base, base.north());
					if (getHideAbleSpace$isHideAble(world, block, base.south())) return new Tuple<>(base, base.south());
				}
				if (getHideAbleSpace$isHideAble(world, block, base.west())) return new Tuple<>(base, base.west());
			} else {
				if (getHideAbleSpace$isHideAble(world, block, base.west())) return new Tuple<>(base, base.west());
				if (lookAngle.z() > 0) {
					if (getHideAbleSpace$isHideAble(world, block, base.south())) return new Tuple<>(base, base.south());
					if (getHideAbleSpace$isHideAble(world, block, base.north())) return new Tuple<>(base, base.north());
				} else {
					if (getHideAbleSpace$isHideAble(world, block, base.north())) return new Tuple<>(base, base.north());
					if (getHideAbleSpace$isHideAble(world, block, base.south())) return new Tuple<>(base, base.south());
				}
				if (getHideAbleSpace$isHideAble(world, block, base.east())) return new Tuple<>(base, base.east());
			}
		}
		if (world.getBlockState(base.below()).is(block) && Math.abs(entity.getY() - base.below().getY()) < 0.2) {
			return new Tuple<>(base.below(), base);
		}
		return null;
	}
}
