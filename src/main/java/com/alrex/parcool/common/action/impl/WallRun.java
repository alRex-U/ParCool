package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.InteractingWallDirection;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

public class WallRun extends Action implements ActionExtension.JumpListener {
    // Only for local client
    private int tickAfterJump;
    private double runSpeed;

    public WallRun(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.CRAWL,
                ParCoolActions.HANG_ON,
                ParCoolActions.SLIDE_DOWN,
                ParCoolActions.HORIZONTAL_WALL_RUN,
                ParCoolActions.VAULT
        ));
    }

    @Override
    public boolean canStart() {
        var tickSinceStarted = getTickSinceStarted();
        if (0 <= tickSinceStarted && tickSinceStarted < 20) return false;
        if (!ParCoolKeyBinds.JUMP.state().isDown() || tickAfterJump < 4 || 12 < tickAfterJump) return false;

        var player = parkourability.player();
        var lookAngle = player.getLookAngle();
        if (lookAngle.y <= 0. || !parkourability.get(ParCoolActions.FAST_RUN).isDoing()) return false;

        if (!player.horizontalCollision) return false;
        var wallDirection = parkourability.getAdditionalProperties().getDefaultWallInteraction();
        if (wallDirection == null || wallDirection.asVec().dot(lookAngle) <= 1. / Mth.SQRT_OF_TWO) return false;

        var gravityAttr = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        if (gravityAttr == null) return false;
        var gravity = gravityAttr.getValue();

        var jumpHeight = Mth.square(player.getJumpPower() + player.getJumpBoostPower()) / (2.1 * gravity);
        var jumpScale = getWallRunHeightScale(player, wallDirection, jumpHeight);
        if (jumpScale <= 0) return false;

        runSpeed = 1.5 * Math.sqrt(jumpHeight * jumpScale * gravity);

        return true;
    }

    @Override
    public void onStartInLocalClient() {
        var player = parkourability.player();
        var deltaMove = player.getDeltaMovement();
        player.setDeltaMovement(deltaMove.x, runSpeed, deltaMove.z);
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().WALL_RUN);
    }

    @Override
    public void onTickInLocalClient() {
        tickAfterJump++;
    }

    @Override
    public void onJump() {
        tickAfterJump = 0;
    }

    private static int getWallRunHeightScale(Player player, InteractingWallDirection wallDirection, double jumpHeight) {
        var wallVec = wallDirection.asVec();
        var playerHalfWidth = player.getBbWidth() / 2.;
        var level = player.level;
        var baseBB = player.getBoundingBox().move(wallVec.x * playerHalfWidth, 0, wallVec.z * playerHalfWidth);
        for (var i = 3; i <= 4; i++) {
            if (level.noCollision(baseBB.move(0, jumpHeight * i, 0))) {
                return i - 2;
            }
        }
        return 0;
    }
}
