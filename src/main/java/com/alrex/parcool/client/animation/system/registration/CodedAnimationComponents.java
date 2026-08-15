package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.data.CodedAnimationComponent;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.alrex.parcool.client.animation.system.util.EntityUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class CodedAnimationComponents extends BasicRegistry<CodedAnimationComponent, CodedAnimationComponents.RegistrationEntry> {
    public record RegistrationEntry(ResourceLocation name, CodedAnimationComponent component) {
    }

    private CodedAnimationComponents() {
    }

    @Nullable
    private static CodedAnimationComponents INSTANCE = null;

    public static CodedAnimationComponents getInstance() {
        if (INSTANCE == null) INSTANCE = new CodedAnimationComponents();
        return INSTANCE;
    }

    public ID<CodedAnimationComponent> register(String subName, CodedAnimationComponent component) {
        var name = ParCool.resourceLocation(subName);
        return register(name, component);
    }

    public ID<CodedAnimationComponent> register(ResourceLocation name, CodedAnimationComponent component) {
        return registerItem(name, new RegistrationEntry(name, component));
    }

    public final ID<CodedAnimationComponent> LOCK_HEAD_ROTATION = register("builtin/lock_head_front", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.HEAD) return null;
        var q = MathUtil.rotation(
                Vec3f.YP,
                (float) Math.toRadians(Mth.wrapDegrees(Mth.lerp(partial, player.yBodyRotO, player.yBodyRot) - Mth.lerp(partial, player.yHeadRotO, player.yHeadRot)))
        );
        q.mul(MathUtil.rotation(
                Vec3f.XP,
                (float) Math.toRadians(Mth.wrapDegrees(-Mth.lerp(partial, player.xRotO, player.getXRot())))
        ));
        return new Transform(Vec3f.ZERO, q);
    });
    public final ID<CodedAnimationComponent> LOCK_BODY_ROTATION = register("builtin/lock_body", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.BODY) return null;
        var q = MathUtil.rotation(
                Vec3f.YP,
                (float) Math.toRadians(Mth.wrapDegrees(Mth.lerp(partial, player.yBodyRotO, player.yBodyRot) - Mth.lerp(partial, player.yHeadRotO, player.yHeadRot)))
        );
        return new Transform(Vec3f.ZERO, q);
    });

    private static Transform getBobTransform(float progress) {
        var zRot = Mth.cos(progress * 0.09f) * 0.025f + 0.05f;
        var xRot = Mth.sin(progress * 0.067f) * 0.025f;
        return Transform.fromRotationParams(xRot, 0f, zRot);
    }

    public final ID<CodedAnimationComponent> BOB_LEFT_ARM = register("builtin/bob_left_arm", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.LEFT_ARM) return null;
        var transform = getBobTransform(progress);
        return mirror ? transform : transform.mirror();
    });
    public final ID<CodedAnimationComponent> BOB_RIGHT_ARM = register("builtin/bob_right_arm", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.RIGHT_ARM) return null;
        var transform = getBobTransform(progress);
        return mirror ? transform.mirror() : transform;
    });
    public final ID<CodedAnimationComponent> BOB_LEFT_LEG = register("builtin/bob_left_leg", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.LEFT_LEG) return null;
        var transform = getBobTransform(progress);
        return mirror ? transform : transform.mirror();
    });
    public final ID<CodedAnimationComponent> BOB_RIGHT_LEG = register("builtin/bob_right_leg", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.RIGHT_LEG) return null;
        var transform = getBobTransform(progress);
        return mirror ? transform.mirror() : transform;
    });
    public final ID<CodedAnimationComponent> ROTATE_BODY_SWIM = register("builtin/swim_rotate_body", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.BODY) return null;
        var swimRot = !player.isInWater() && !player.isInFluidType((fluidType, height) -> player.canSwimInFluidType(fluidType)) ? -90.0f : -90.0f - player.getXRot();
        return new Transform(
                new Vec3f(0, -0.6f, 0),
                MathUtil.rotation(
                        Vec3f.XP,
                        Mth.lerp(player.getSwimAmount(partial), 0.0F, (float) Math.toRadians(swimRot))
                )
        );
    });
    public final ID<CodedAnimationComponent> ROTATE_BODY_SWIM_RELATIVE = register("builtin/swim_rotate_body_relative", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.BODY) return null;
        var swimRot = !player.isInWater() && !player.isInFluidType((fluidType, height) -> player.canSwimInFluidType(fluidType)) ? 0f : -player.getXRot();
        return new Transform(
                new Vec3f(0, -0.6f, 0),
                MathUtil.rotation(
                        Vec3f.XP,
                        Mth.lerp(player.getSwimAmount(partial), 0.0F, (float) Math.toRadians(swimRot))
                )
        );
    });
    public final ID<CodedAnimationComponent> ROTATE_BODY_TO_MOVE_PITCH = register("builtin/rotate_body_to_move_pitch", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.BODY) return null;
        var posDiff = EntityUtil.getPositionDifference(player);
        var horizontalLen = Math.sqrt(posDiff.x * posDiff.x + posDiff.z * posDiff.z);

        return new Transform(Vec3f.ZERO,
                MathUtil.rotation(
                        Vec3f.XP,
                        (float) Math.atan2(posDiff.y(), horizontalLen) - Mth.HALF_PI
                )
        );
    });

    private static Transform getShakeTransform(float progress) {
        var zRot = Mth.cos(progress * 0.56f) * 0.6f + 0.05f;
        var xRot = Mth.sin(progress * 0.56f) * 0.6f;
        return Transform.fromRotationParams(xRot, 0f, zRot);
    }

    public final ID<CodedAnimationComponent> SHAKE_LEFT_ARM = register("builtin/shake_left_arm", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.LEFT_ARM) return null;
        var transform = getShakeTransform(progress);
        return mirror ? transform.mirror() : transform;
    });
    public final ID<CodedAnimationComponent> SHAKE_RIGHT_ARM = register("builtin/shake_right_arm", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.RIGHT_ARM) return null;
        var transform = getShakeTransform(progress);
        return mirror ? transform.mirror() : transform;
    });
    public final ID<CodedAnimationComponent> SHAKE_LEFT_LEG = register("builtin/shake_left_leg", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.LEFT_LEG) return null;
        var transform = getShakeTransform(progress);
        return mirror ? transform.mirror() : transform;
    });
    public final ID<CodedAnimationComponent> SHAKE_RIGHT_LEG = register("builtin/shake_right_leg", (player, part, progress, partial, mirror) -> {
        if (part != AnimatableModelPart.RIGHT_LEG) return null;
        var transform = getShakeTransform(progress);
        return mirror ? transform.mirror() : transform;
    });
}
