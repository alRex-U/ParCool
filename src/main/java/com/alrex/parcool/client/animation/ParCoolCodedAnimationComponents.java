package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.data.CodedAnimationComponent;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.alrex.parcool.client.animation.system.registration.CodedAnimationComponents;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParCoolCodedAnimationComponents {
    private static Transform lockBody(Player player, Vec3 direction, float partial) {
        var yaw = Mth.wrapDegrees(MathUtil.toYawRadian(direction) + Math.toRadians(Mth.lerp(partial, player.yBodyRotO, player.yBodyRot)));
        return new Transform(Vec3f.ZERO, MathUtil.rotation(Vec3f.YP, (float) yaw));
    }

    public final ID<CodedAnimationComponent> HANG_ON_LOCK_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/hang_on_lock_body",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.BODY) return null;
                var wallVec = Parkourability.get(player).get(ParCoolActions.HANG_ON).getWallVec(partial);
                if (wallVec == null) return null;
                return lockBody(player, wallVec, partial);
            }
    );
    public final ID<CodedAnimationComponent> CLIMB_UP_LOCK_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/climb_up_lock_body",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.BODY) return null;
                var wallVec = Parkourability.get(player).get(ParCoolActions.CLIMB_UP).getWallVec(partial);
                if (wallVec == null) return null;
                return lockBody(player, wallVec, partial);
            }
    );
    public final ID<CodedAnimationComponent> SLIDE_DOWN_LOCK_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/slide_down_lock_body",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.BODY) return null;
                var wallVec = Parkourability.get(player).get(ParCoolActions.SLIDE_DOWN).getWallVec(partial);
                if (wallVec == null) return null;
                return lockBody(player, wallVec, partial);
            }
    );
    public final ID<CodedAnimationComponent> HORIZONTAL_WALL_RUN_LOCK_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/horizontal_wall_run_lock_body",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.BODY) return null;
                var runningDirection = Parkourability.get(player).get(ParCoolActions.HORIZONTAL_WALL_RUN).getRunningDirection(partial);
                if (runningDirection == null) return null;
                return lockBody(player, runningDirection, partial);
            }
    );
    public final ID<CodedAnimationComponent> SLIDE_LOCK_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/slide_lock_body",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.BODY) return null;
                var slidingDirection = Parkourability.get(player).get(ParCoolActions.SLIDE).getSlidingDirection();
                if (slidingDirection == null) return null;
                return lockBody(player, slidingDirection, partial);
            }
    );
    public final ID<CodedAnimationComponent> HIDE_IN_BLOCK_LOCK_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/hide_in_block_lock_body",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.BODY) return null;
                var facingDirection = Parkourability.get(player).get(ParCoolActions.HIDE_IN_BLOCK).getFacingVec();
                if (facingDirection == null) return null;
                return lockBody(player, facingDirection, partial);
            }
    );
    public final ID<CodedAnimationComponent> POLE_CLIMB_LOCK_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/pole_climb_lock_body",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.BODY) return null;
                var vec = Parkourability.get(player).get(ParCoolActions.POLE_CLIMB).getWallVec();
                if (vec == null) return null;
                return lockBody(player, vec, partial);
            }
    );
    public final ID<CodedAnimationComponent> DODGE_LOCK_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/dodge_lock_body",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.BODY) return null;
                var vec = Parkourability.get(player).get(ParCoolActions.DODGE).getFacingVec();
                if (vec == null) return null;
                return lockBody(player, vec, partial);
            }
    );
    public final ID<CodedAnimationComponent> HANG_DOWN_ROTATE_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/hang_down_rotate_body",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.BODY) return null;
                var vec = Parkourability.get(player).get(ParCoolActions.HANG_DOWN).getBodyDirection(partial);
                if (vec == null) return null;
                return lockBody(player, vec, partial);
            }
    );
    public final ID<CodedAnimationComponent> HANG_DOWN_SWING_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/hang_down_swing_body",
            (player, part, progress, partial, mirror) -> {
                switch (part) {
                    case BODY -> {
                        var pitch = Parkourability.get(player).get(ParCoolActions.HANG_DOWN).getRotationAngle(partial);
                        return new Transform(
                                new Vec3f(0, 0.9f - 1.1f * Mth.cos(pitch), -1.1f * Mth.sin(pitch)), MathUtil.rotation(Vec3f.XP, pitch)
                        );
                    }
                    case HEAD -> {
                        var pitch = Parkourability.get(player).get(ParCoolActions.HANG_DOWN).getRotationAngle(partial);
                        return new Transform(Vec3f.ZERO, MathUtil.rotation(Vec3f.XN, Mth.clamp(pitch / 4f, -Mth.PI / 4f, Mth.PI / 4f)));
                    }
                    default -> {
                        return null;
                    }
                }
            }
    );
    public final ID<CodedAnimationComponent> HANG_DOWN_SWING_LIMBS = CodedAnimationComponents.getInstance().register(
            "builtin/hang_down_swing_limbs",
            (player, part, progress, partial, mirror) -> {
                switch (part) {
                    case LEFT_LEG, RIGHT_LEG -> {
                        var angularSpeed = -3f * Parkourability.get(player).get(ParCoolActions.HANG_DOWN).getAngularSpeed(partial);
                        return new Transform(Vec3f.ZERO, MathUtil.rotation(Vec3f.XP, angularSpeed));
                    }
                    default -> {
                        return null;
                    }
                }
            }
    );
    public final ID<CodedAnimationComponent> ZIPLINE_SWING_BODY = CodedAnimationComponents.getInstance().register(
            "builtin/zipline_swing_body",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.BODY && part != AnimatableModelPart.HEAD) return null;
                var action = Parkourability.get(player).get(ParCoolActions.RIDE_ZIPLINE);
                var ziplineOffset = action.getZiplineOffset();
                if (ziplineOffset == null) return null;
                Vec3 rotationAxis = new Vec3(0, 0, 1)
                        .yRot((float) (Math.toRadians(Mth.lerp(partial, player.yBodyRotO, player.yBodyRot)) + MathUtil.toYawRadian(new Vec3(ziplineOffset.x(), 0, ziplineOffset.z())) + Math.PI / 2))
                        .normalize();
                var pitch = action.getBodyAngle(partial);
                switch (part) {
                    case BODY -> {
                        return new Transform(Vec3f.ZERO, MathUtil.rotation(new Vec3f((float) rotationAxis.x, 0, (float) rotationAxis.z), pitch));
                    }
                    case HEAD -> {
                        return new Transform(Vec3f.ZERO, MathUtil.rotation(new Vec3f((float) rotationAxis.x, 0, (float) rotationAxis.z), -0.5f * pitch));
                    }
                    default -> {
                        return null;
                    }
                }
            }
    );
    public final ID<CodedAnimationComponent> ZIPLINE_SWING_LIMBS = CodedAnimationComponents.getInstance().register(
            "builtin/zipline_swing_limbs",
            (player, part, progress, partial, mirror) -> {
                if (part != AnimatableModelPart.LEFT_LEG && part != AnimatableModelPart.RIGHT_LEG) return null;
                var action = Parkourability.get(player).get(ParCoolActions.RIDE_ZIPLINE);
                var ziplineOffset = action.getZiplineOffset();
                if (ziplineOffset == null) return null;
                Vec3 rotationAxis = new Vec3(0, 0, 1)
                        .yRot((float) (Math.toRadians(Mth.lerp(partial, player.yBodyRotO, player.yBodyRot)) + MathUtil.toYawRadian(new Vec3(ziplineOffset.x(), 0, ziplineOffset.z())) + Math.PI / 2))
                        .normalize();
                var pitch = action.getBodyAngle(partial);
                return new Transform(
                        Vec3f.ZERO, MathUtil.rotation(new Vec3f((float) rotationAxis.x, 0, (float) rotationAxis.z), pitch)
                );
            }
    );
    public final ID<CodedAnimationComponent> GRAPPLE_ANIMATE_BASE = CodedAnimationComponents.getInstance().register(
            "builtin/grapple_swing_body",
            (player, part, progress, partial, mirror) -> {
                var action = Parkourability.get(player).get(ParCoolActions.GRAPPLE);
                var acceleration = action.getAcceleration();
                Vec3 rotationAxis = new Vec3(0, 0, 1)
                        .yRot((float) (Math.toRadians(Mth.lerp(partial, player.yBodyRotO, player.yBodyRot)) + MathUtil.toYawRadian(acceleration) + Math.PI / 2))
                        .normalize();
                var pitch = action.getBodyAngle(partial);
                switch (part) {
                    case BODY -> {
                        return new Transform(Vec3f.ZERO, MathUtil.rotation(new Vec3f((float) rotationAxis.x, 0, (float) rotationAxis.z), pitch));
                    }
                    case HEAD -> {
                        return new Transform(Vec3f.ZERO, MathUtil.rotation(new Vec3f((float) rotationAxis.x, 0, (float) rotationAxis.z), -0.5f * pitch));
                    }
                    case RIGHT_ARM, LEFT_ARM -> {
                        Vec3 ropeDirection = action.getRopeDirection();
                        if (ropeDirection == null) return null;
                        var horizontalLen = Math.sqrt(ropeDirection.x * ropeDirection.x + ropeDirection.z * ropeDirection.z);
                        float handTiltScale = part.isMainHandOf(player)
                                ? 0.125f : 0.25f;
                        float yRot = MathUtil.warpRadian((float) (Math.toRadians(Mth.lerp(partial, player.yBodyRotO, player.yBodyRot)) + MathUtil.toYawRadian(new Vec3(-ropeDirection.x(), 0, -ropeDirection.z()))));
                        float xRot = (float) -Math.atan2(ropeDirection.y(), horizontalLen);
                        var reversed = Mth.abs(yRot) > Mth.HALF_PI;
                        var rot = MathUtil.rotation(new Vec3f((float) rotationAxis.x, 0, (float) rotationAxis.z), -pitch);
                        rot.mul(MathUtil.rotation(Vec3f.XP, reversed ? -Mth.PI - xRot : xRot));
                        rot.mul(MathUtil.rotation(Vec3f.YP, (part == AnimatableModelPart.RIGHT_ARM ? Mth.PI : -Mth.PI) * handTiltScale));
                        rot.mul(MathUtil.rotation(Vec3f.XP, Mth.HALF_PI));
                        return new Transform(Vec3f.ZERO, rot);
                    }
                    case LEFT_LEG, RIGHT_LEG -> {
                        return new Transform(Vec3f.ZERO, MathUtil.rotation(new Vec3f((float) rotationAxis.x, 0, (float) rotationAxis.z), 0.5f * pitch));
                    }
                    default -> {
                        return null;
                    }
                }
            }
    );
}
