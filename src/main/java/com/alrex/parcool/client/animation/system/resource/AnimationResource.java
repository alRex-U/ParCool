package com.alrex.parcool.client.animation.system.resource;

import com.alrex.parcool.client.animation.system.data.AnimationComponentGroup;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.data.StaticAnimationComponent;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class AnimationResource {
    private final Map<ResourceLocation, StaticAnimationComponent> componentMap;
    private final Map<ResourceLocation, AnimationComponentGroup> animationGroupMap;
    private final Map<ID<AnimationSet>, List<AnimationSet>> idAnimationSetMap;
    private final Random random = new Random();

    public static AnimationResource empty() {
        return new AnimationResource(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }

    public AnimationResource(
            Map<ResourceLocation, StaticAnimationComponent> componentMap,
            Map<ResourceLocation, AnimationComponentGroup> animationGroupMap,
            Map<ResourceLocation, List<AnimationSet>> animationSetMap
    ) {
        this.componentMap = componentMap;
        this.animationGroupMap = animationGroupMap;
        this.idAnimationSetMap = new TreeMap<>();
        for (var mapEntry : animationSetMap.entrySet()) {
            var entry = AnimationSets.getInstance().get(mapEntry.getKey());
            if (entry != null) {
                this.idAnimationSetMap.put(entry.id(), mapEntry.getValue());
            }
        }
    }

    @Nullable
    public AnimationSet getAnimationSet(ID<AnimationSet> id) {
        var animations = idAnimationSetMap.get(id);
        if (animations == null) return null;
        if (animations.isEmpty()) return null;
        return animations.get(random.nextInt(animations.size()));
    }
}
