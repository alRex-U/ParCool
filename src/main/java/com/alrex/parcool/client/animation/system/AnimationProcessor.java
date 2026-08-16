package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.client.animation.system.resource.AnimationResourceManager;
import com.mojang.math.Quaternion;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;

@OnlyIn(Dist.CLIENT)
public class AnimationProcessor {
    private record WorkingAnimationEntry(AnimationSets.Entry registration, WorkingAnimationSet animator) {
    }

    private final AbstractClientPlayer owner;

    public AnimationProcessor(AbstractClientPlayer owner) {
        this.owner = owner;
    }

    public AbstractClientPlayer getOwner() {
        return owner;
    }

    private final ArrayList<WorkingAnimationEntry> animators = new ArrayList<>();

    public void tick() {
        var finished = new LinkedList<WorkingAnimationEntry>();
        for (var working : animators) {
            working.animator.tick(owner);
            if (working.animator.isFinished()) {
                finished.addFirst(working);
            }
        }
        for (var finishedAnim : finished) {
            remove(finishedAnim);
        }
    }

    public void start(ID<AnimationSet> id, boolean mirror) {
        var entry = getWorkingAnimator(id);
        if (entry != null) {
            remove(entry);
        }

        startIfNotWorking(id, mirror);
    }

    public void startIfNotWorking(ID<AnimationSet> id, boolean mirror) {
        if (isWorking(id)) return;

        var animEntry = AnimationSets.getInstance().get(id);
        if (animEntry == null) return;
        var newAnimationSet = AnimationResourceManager.getInstance().getResource().getAnimationSet(id);
        if (newAnimationSet == null) return;
        if (animEntry.parent() != null) {
            startIfNotWorking(animEntry.parent().id(), mirror);
        }
        animators.add(new WorkingAnimationEntry(animEntry, new WorkingAnimationSet(newAnimationSet, animEntry.controllerSupplier().apply(owner), mirror)));
    }

    private void remove(WorkingAnimationEntry entry) {
        int i = animators.size();
        while ((--i) >= 0) {
            var animation = animators.get(i);
            if (animation == entry) {
                animators.remove(i);
                break;
            }
            if (animation.registration.isDescendantOf(entry.registration.id())) {
                animators.remove(i);
            }
        }
    }
    public void stop(ID<AnimationSet> id) {
        var animator = getWorkingAnimator(id);
        if (animator != null) {
            animator.animator().startForceFinishing();
        }
    }

    public void stopImmediately(ID<AnimationSet> id) {
        int i = animators.size();
        while ((--i) >= 0) {
            var animation = animators.get(i);
            if (animation.registration.id() == id) {
                animators.remove(i);
                break;
            }
            if (animation.registration.isDescendantOf(id)) {
                animators.remove(i);
            }
        }
    }

    public boolean isIdle() {
        return animators.isEmpty();
    }

    @Nullable
    private WorkingAnimationEntry getWorkingAnimator(ID<AnimationSet> id) {
        for (var animator : animators) {
            if (animator.registration.id() == id) return animator;
        }
        return null;
    }

    @Nullable
    public BlendingModelTransform getTransformation(boolean firstPersonView, float partial) {
        if (animators.isEmpty()) return null;
        var factors = new float[10];
        int i;
        var maxBlendFactor = 0f;
        for (i = 0; i < 10 && i < animators.size(); i++) {
            factors[i] = animators.get((animators.size() - 1) - i).animator().getCurrentBlendFactor(firstPersonView, partial);
            if (maxBlendFactor < factors[i]) maxBlendFactor = factors[i];
            if (factors[i] >= 0.99) {
                maxBlendFactor = factors[i] = 1;
                i++;
                break;
            }
        }
        i--;
        var maxI = i;
        var transform = ModelTransform.NO_TRANSFORMATION;
        Vec3f cameraRotation = null;
        float vanillaBlend = 1f;
        do {
            var animator = animators.get((animators.size() - 1) - i).animator;
            var thisAnimatorTransform = animator.getTransform(owner, partial);
            if (thisAnimatorTransform == null) continue;
            var cameraScale = animator.getCameraAnimationScale();
            if (firstPersonView && cameraScale != null && AnimationSystemConfig.getInstance().enableCameraAnimation.get()) {
                var bodyTrans = thisAnimatorTransform.transforms().get(AnimatableModelPart.BODY);
                if (bodyTrans != null) {
                    if (cameraRotation == null) cameraRotation = Vec3f.ZERO;
                    var bodyQ = bodyTrans.rotation();
                    var rot = new Quaternion(
                            cameraScale.x() * bodyQ.i(),
                            cameraScale.y() * bodyQ.j(),
                            cameraScale.z() * bodyQ.k(),
                            bodyQ.r()
                    );
                    rot.normalize();
                    var bodyRot = MathUtil.toCameraRotation(rot);
                    cameraRotation = new Vec3f(
                            Mth.lerp(factors[i], cameraRotation.x(), bodyRot.x()),
                            Mth.lerp(factors[i], cameraRotation.y(), bodyRot.y()),
                            Mth.lerp(factors[i], cameraRotation.z(), bodyRot.z())
                    );
                }
            }
            if (i == maxI) {
                factors[i] = 1f; // First animation is applied without blending
                vanillaBlend = animator.getBlendFactorOverVanilla(owner, partial);
            }
            transform = transform.morph(thisAnimatorTransform, factors[i]);
        } while ((--i) >= 0);
        return BlendingModelTransform.from(transform, maxBlendFactor * vanillaBlend, cameraRotation);
    }

    private boolean isWorking(ID<AnimationSet> id) {
        return getWorkingAnimator(id) != null;
    }
}
