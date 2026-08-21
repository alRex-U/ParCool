package com.alrex.parcool.client.animation.system;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public enum AnimatableModelPart {
    BODY, HEAD, RIGHT_ARM, LEFT_ARM, RIGHT_LEG, LEFT_LEG;
    private static final List<AnimatableModelPart> MIRROR;

    static {
        MIRROR = List.of(
                BODY, HEAD, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG
        );
    }

    public boolean isMainHandOf(Player player) {
        return switch (player.getMainArm()) {
            case RIGHT -> this == RIGHT_ARM;
            case LEFT -> this == LEFT_ARM;
            default -> false;
        };
    }
    public AnimatableModelPart getMirrorPart() {
        return MIRROR.get(this.ordinal());
    }
}
