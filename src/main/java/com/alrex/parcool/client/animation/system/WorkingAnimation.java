package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.data.AnimationComponentGroup;
import com.alrex.parcool.client.animation.system.data.IAnimationComponent;
import com.alrex.parcool.client.animation.system.data.Transform;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class WorkingAnimation implements IWorkingAnimation {
    public record Component(
            IAnimationComponent component,
            @Nullable IBlendingFactor blendingFactor,
            AnimationProgress progress,
            boolean mirror
    ) {
    }

    private final List<Component> components;
    private int tick = 0;
    private final int duration;
    private final boolean loop;
    private final boolean infinite;
    private boolean finished = false;

    public WorkingAnimation(AnimationComponentGroup group) {
        components = group.components().stream().map(it -> new Component(it.component(), it.blendingFactor() != null ? it.blendingFactor().get() : null, it.progressSupplier().get(), it.mirror())).toList();
        duration = group.duration();
        loop = group.loops();
        infinite = group.infinite();
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean loops() {
        return loop;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void reset() {
        tick = 0;
        finished = false;
        for (var component : components) {
            component.progress().reset();
        }
    }

    @Override
    public void tick(AbstractClientPlayer player) {
        tick++;
        if (!infinite && tick >= getDuration()) {
            if (loops()) {
                tick = 0;
                for (var component : components) {
                    component.progress().reset();
                }
            } else {
                finished = true;
                tick = getDuration();
            }
            return;
        }
        for (var component : components) {
            component.progress().tick(player);
            if (component.blendingFactor != null) component.blendingFactor.tick(player);
        }
    }

    @Override
    public ModelTransform getTransformation(AbstractClientPlayer player, float partialTick, boolean allMirroring) {
        var map = new EnumMap<AnimatableModelPart, Transform>(AnimatableModelPart.class);
        for (var component : components) {
            var blendingValue = component.blendingFactor != null ? component.blendingFactor.getFactor(player, partialTick) : 1f;
            var method = component.blendingFactor != null ? component.blendingFactor.getBlendMethod() : BlendMethod.ADD;
            var mirror = component.mirror ^ allMirroring;

            for (var modelPart : AnimatableModelPart.values()) {

                if (blendingValue < 1e-5) continue;

                var transform = map.get(modelPart);
                if (transform == null) transform = Transform.NO_TRANSFORMATION;
                var componentTransform = component.component.getTransform(player, modelPart, component.progress.getProgress(player, partialTick), partialTick, mirror);
                if (componentTransform != null) {
                    componentTransform = new Transform(
                            componentTransform.translation(),
                            componentTransform.rotation().normalize(new Quaternionf())
                    );
                    switch (method) {
                        case ADD -> map.put(modelPart, transform.append(componentTransform, blendingValue, true));
                        case SET -> map.put(modelPart, transform.morph(componentTransform, blendingValue, true));
                    }
                }
            }
        }
        for (var modelPart : AnimatableModelPart.values()) {
            var transform = map.get(modelPart);
            if (transform == null) continue;
            if (transform == Transform.NO_TRANSFORMATION) {
                map.remove(modelPart);
            }
        }
        return new ModelTransform(map);
    }
}
