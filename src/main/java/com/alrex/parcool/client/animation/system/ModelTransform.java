package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.data.Transform;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.EnumMap;

@OnlyIn(Dist.CLIENT)
public record ModelTransform(EnumMap<AnimatableModelPart, Transform> transforms) {
    public static final ModelTransform NO_TRANSFORMATION;

    static {
        var map = new EnumMap<AnimatableModelPart, Transform>(AnimatableModelPart.class);
        for (var part : AnimatableModelPart.values()) {
            map.put(part, Transform.NO_TRANSFORMATION);
        }
        NO_TRANSFORMATION = new ModelTransform(map);
    }

    public ModelTransform mirror() {
        var newMap = new EnumMap<AnimatableModelPart, Transform>(AnimatableModelPart.class);
        var headTransform = transforms.get(AnimatableModelPart.HEAD);
        if (headTransform != null) {
            newMap.put(AnimatableModelPart.HEAD, headTransform.mirror());
        }
        var bodyTransform = transforms.get(AnimatableModelPart.BODY);
        if (bodyTransform != null) {
            newMap.put(AnimatableModelPart.BODY, bodyTransform.mirror());
        }
        var rArmTransform = transforms.get(AnimatableModelPart.RIGHT_ARM);
        var lArmTransform = transforms.get(AnimatableModelPart.LEFT_ARM);
        if (lArmTransform != null) newMap.put(AnimatableModelPart.RIGHT_ARM, lArmTransform.mirror());
        if (rArmTransform != null) newMap.put(AnimatableModelPart.LEFT_ARM, rArmTransform.mirror());
        var rLegTransform = transforms.get(AnimatableModelPart.RIGHT_LEG);
        var lLegTransform = transforms.get(AnimatableModelPart.LEFT_LEG);
        if (lLegTransform != null) newMap.put(AnimatableModelPart.RIGHT_LEG, lLegTransform.mirror());
        if (rLegTransform != null) newMap.put(AnimatableModelPart.LEFT_LEG, rLegTransform.mirror());
        return new ModelTransform(newMap);
    }

    public ModelTransform morph(ModelTransform to, float t) {
        if (t <= 1e-4) return this;
        if (t >= 0.9999) return to;
        var newMap = new EnumMap<AnimatableModelPart, Transform>(AnimatableModelPart.class);
        for (var part : AnimatableModelPart.values()) {
            var fromTransform = transforms.get(part);
            var toTransform = to.transforms.get(part);
            if (toTransform == null) {
                if (fromTransform != null) {
                    newMap.put(part, fromTransform);
                }
                continue;
            } else {
                if (fromTransform == null) {
                    newMap.put(part, toTransform);
                    continue;
                }
            }
            newMap.put(part, fromTransform.morph(toTransform, t, part == AnimatableModelPart.BODY));
        }
        return new ModelTransform(newMap);
    }
}
