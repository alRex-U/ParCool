package com.alrex.parcool.common.action;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public enum InteractingWallDirection {
    XP(1, 0, false, false),
    XN(-1, 0, false, false),
    ZP(0, 1, false, false),
    ZN(0, -1, false, false),

    XP_ZP_INDENTATION(1, 1, true, false),
    XP_ZN_INDENTATION(1, -1, true, false),
    XN_ZP_INDENTATION(-1, 1, true, false),
    XN_ZN_INDENTATION(-1, -1, true, false),

    XP_ZP_PROTRUSION(1, 1, true, true),
    XP_ZN_PROTRUSION(1, -1, true, true),
    XN_ZP_PROTRUSION(-1, 1, true, true),
    XN_ZN_PROTRUSION(-1, -1, true, true);
    private final short signX;
    private final short signZ;
    private final boolean oblique;
    private final boolean protrusion;
    private final Vec3 normalizedVec;

    InteractingWallDirection(int signX, int signZ, boolean oblique, boolean protrusion) {
        this.signX = (short) signX;
        this.signZ = (short) signZ;
        this.oblique = oblique;
        this.protrusion = protrusion;
        this.normalizedVec = oblique
                ? new Vec3(signX, 0, signZ).scale(1 / Mth.sqrt(2))
                : new Vec3(signX, 0, signZ);
    }

    public Vec3 asVec() {
        return normalizedVec;
    }

    public short getSignX() {
        return signX;
    }

    public short getSignZ() {
        return signZ;
    }

    public boolean isOblique() {
        return oblique;
    }

    public boolean isProtrusion() {
        return protrusion;
    }

    public boolean alongToAxis() {
        return !oblique && !protrusion;
    }

    @Nullable
    public static InteractingWallDirection get(int signX, int signZ, boolean protrusion) {
        for (var direction : InteractingWallDirection.values()) {
            if (direction.signX == signX && direction.signZ == signZ && direction.protrusion == protrusion) {
                return direction;
            }
        }
        return null;
    }

    @Nullable
    public static InteractingWallDirection getAdjacentWall(Player player) {
        var playerBB = player.getBoundingBox();
        double xRange = playerBB.getXsize() * 0.25, zRange = playerBB.getZsize() * 0.25;
        return getAdjacentWall(player, playerBB, xRange, zRange);
    }

    @Nullable
    public static InteractingWallDirection getAdjacentWall(Player player, double xRange, double zRange) {
        var playerBB = player.getBoundingBox();
        return getAdjacentWall(player, playerBB, xRange, zRange);
    }

    @Nullable
    private static InteractingWallDirection getAdjacentWall(Player player, AABB playerBB, double xRange, double zRange) {
        var level = player.level;
        short signX = 0, signZ = 0;
        boolean protrusion = false;

        if (!level.noCollision(player, playerBB.expandTowards(xRange, 0, 0))) {
            signX++;
        }
        if (!level.noCollision(player, playerBB.expandTowards(-xRange, 0, 0))) {
            signX--;
        }
        if (!level.noCollision(player, playerBB.expandTowards(0, 0, zRange))) {
            signZ++;
        }
        if (!level.noCollision(player, playerBB.expandTowards(0, 0, -zRange))) {
            signZ--;
        }

        var direction = InteractingWallDirection.get(signX, signZ, protrusion);
        if (direction == null) {
            protrusion = true;
            signX = 0;
            signZ = 0;
            if (!level.noCollision(player, playerBB.expandTowards(xRange, 0, zRange))) {
                signX++;
                signZ++;
            }
            if (!level.noCollision(player, playerBB.expandTowards(-xRange, 0, -zRange))) {
                signX--;
                signZ--;
            }
            direction = InteractingWallDirection.get(signX, signZ, protrusion);
            if (direction == null) {
                signX = 0;
                signZ = 0;
                if (!level.noCollision(player, playerBB.expandTowards(xRange, 0, -zRange))) {
                    signX++;
                    signZ--;
                }
                if (!level.noCollision(player, playerBB.expandTowards(-xRange, 0, zRange))) {
                    signX--;
                    signZ++;
                }
                direction = InteractingWallDirection.get(signX, signZ, protrusion);
            }
        }
        return direction;
    }
}
