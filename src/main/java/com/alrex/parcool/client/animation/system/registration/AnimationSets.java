package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.client.animation.system.IAnimationController;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class AnimationSets extends BasicRegistry<AnimationSet, AnimationSets.Entry> {
    @Nullable
    private static AnimationSets instance = null;

    public static AnimationSets getInstance() {
        if (instance == null) instance = new AnimationSets();
        return instance;
    }

    public record Entry(
            ID<AnimationSet> id,
            ResourceLocation location,
            Function<AbstractClientPlayer, IAnimationController> controllerSupplier,
            @Nullable Entry parent
    ) {
        public boolean isDescendantOf(ID<AnimationSet> otherId) {
            Entry ancestor = this.parent;
            while (ancestor != null) {
                if (ancestor.id == otherId) return true;
                ancestor = ancestor.parent;
            }
            return false;
        }
    }

    public ID<AnimationSet> register(ResourceLocation location, IAnimationController controller, @Nullable ID<AnimationSet> parent) {
        return register(location, (AbstractClientPlayer p) -> controller, parent);
    }

    public ID<AnimationSet> registerTimeout(ResourceLocation location, int timeoutInTick, @Nullable ID<AnimationSet> parent) {
        return register(
                location,
                (AbstractClientPlayer p) -> {
                    var startTick = p.tickCount;
                    return (IAnimationController) ((workingPlayer) -> (workingPlayer.tickCount - startTick < timeoutInTick));
                },
                parent
        );
    }

    private ID<AnimationSet> register(ResourceLocation location, Function<AbstractClientPlayer, IAnimationController> controllerSupplier, @Nullable ID<AnimationSet> parent) {
        Entry parentEntry;
        if (parent != null) {
            parentEntry = get(parent);
        } else {
            parentEntry = null;
        }
        return registerItem(location, (id) -> new Entry(id, location, controllerSupplier, parentEntry));
    }
}
