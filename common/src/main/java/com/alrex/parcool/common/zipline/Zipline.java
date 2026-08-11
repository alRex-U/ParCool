package com.alrex.parcool.common.zipline;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public record Zipline(ZiplineShape shape, ZiplineInfo info, BlockPos start, BlockPos end,
                      boolean powered) implements Comparable<Zipline> {

    public static final double MAXIMUM_HORIZONTAL_DISTANCE = 192.;
    public static final double MAXIMUM_VERTICAL_DISTANCE = MAXIMUM_HORIZONTAL_DISTANCE * 0.51;

    @Environment(EnvType.CLIENT)
    @Nullable
    public static Zipline getHangAbleZipline(ClientLevel level, Player player) {
        if (!(level instanceof ILoadedZiplineHolderProvider provider)) return null;
        double catchRange = player.getBbWidth();
        double yDeltaMovement = player.getDeltaMovement().y();
        double yDistanceScale = Mth.clamp(0.7 / (Math.abs(yDeltaMovement) + 0.7), 0.4d, 1d);
        var grabPos = player.position().add(0, player.getBbHeight() * 1.11, 0);
        for (var zipline : provider.getZiplineHolder().getLivingZiplines()) {
            var shape = zipline.shape();
            if (shape.isPossiblyHangAble(grabPos)) {
                double distSqr = shape.getSquaredDistanceApproximately(grabPos, yDistanceScale);
                if (distSqr < catchRange * catchRange) {
                    return zipline;
                }
            }
        }

        return null;
    }

    @Override
    public int compareTo(Zipline o) {
        var startDiff = start.compareTo(o.start);
        if (startDiff != 0) return startDiff;
        var endDiff = end.compareTo(o.end);
        if (endDiff != 0) return endDiff;
        return Integer.compare(info.type().ordinal(), o.info.type().ordinal());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Zipline zipline && compareTo(zipline) == 0;
    }
}
