package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.AnimatableProperty;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.mojang.math.Quaternion;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumMap;

@OnlyIn(Dist.CLIENT)
public record StaticAnimationComponent(
        EnumMap<AnimatableModelPart, EnumMap<AnimatableProperty, Timeline>> animationCurves,
        int duration
) implements IAnimationComponent {

    @Override
    @Nullable
    public Transform getTransform(AbstractClientPlayer __, AnimatableModelPart part, float progress, float ___, boolean mirror) {
        var curves = animationCurves.get(mirror ? part.getMirrorPart() : part);
        if (curves == null) return null;
        var translation = new float[3];
        for (int i = 0; i < 3; i++) {
            var property = AnimatableProperty.TRANSLATIONS.get(i);
            var curve = curves.get(property);
            translation[i] = curve != null
                    ? curve.getValue(progress)
                    : property.getDefaultValue();
        }
        var rotation = new float[4];
        rotation[0] = 1f;
        for (int i = 0; i < 4; i++) {
            var property = AnimatableProperty.ROTATIONS.get(i);
            var curve = curves.get(property);
            rotation[i] = curve != null
                    ? curve.getValue(progress)
                    : property.getDefaultValue();
        }

        var transform = new Transform(
                new Vec3f(translation[0], translation[1], translation[2]),
                new Quaternion(rotation[1], rotation[2], rotation[3], rotation[0])
        );
        return mirror ? transform.mirror() : transform;
    }
}
