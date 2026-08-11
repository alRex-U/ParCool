package com.alrex.parcool.common.action;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class AdditionalProperties {
    public AdditionalProperties(Player player) {
        this.player = player;
    }

    public static class StatusTick {
        private int duration;
        private int lastDuration;
        private boolean doing;

        private void update(boolean doing) {
            if (this.doing == doing) {
                duration++;
            } else {
                lastDuration = duration;
                duration = 0;
            }
            this.doing = doing;
        }

        public int durationDoing() {
            return doing ? duration : 0;
        }

        public int durationNotDoing() {
            return !doing ? duration : 0;
        }

        public int lastDurationDoing() {
            return !doing ? lastDuration : 0;
        }

        public int lastDurationNotDoing() {
            return doing ? lastDuration : 0;
        }
    }

    private final Player player;
    private int tickAfterLastJump = 0;
    private final StatusTick sprint = new StatusTick();
    private final StatusTick sneak = new StatusTick();
    private final StatusTick onGround = new StatusTick();
    private final StatusTick flying = new StatusTick();
    private final StatusTick inWater = new StatusTick();
    private boolean wallDirectionCached = false;
    @Nullable
    private InteractingWallDirection cachedDefaultWallDirection = null;

    public void onJump() {
        tickAfterLastJump = 0;
    }

    public void onTick() {
        tickAfterLastJump++;
        sprint.update(player.isSprinting());
        sneak.update(player.isShiftKeyDown());
        onGround.update(player.isOnGround());
        flying.update(player.getAbilities().flying);
        inWater.update(player.isInWater());
        wallDirectionCached = false;
    }

    public StatusTick getSprintDurations() {
        return sprint;
    }

    public StatusTick getSneakDurations() {
        return sneak;
    }

    public StatusTick getOnGroundDurations() {
        return onGround;
    }

    public StatusTick getFlyingDurations() {
        return flying;
    }

    public StatusTick getInWaterDurations() {
        return inWater;
    }

    @Nullable
    public InteractingWallDirection getDefaultWallInteraction() {
        if (!wallDirectionCached) {
            cachedDefaultWallDirection = InteractingWallDirection.getAdjacentWall(player);
            wallDirectionCached = true;
        }
        return cachedDefaultWallDirection;
    }
}
