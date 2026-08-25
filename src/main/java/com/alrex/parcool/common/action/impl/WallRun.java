package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.animation.system.util.EntityUtil;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.InteractingWallDirection;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class WallRun extends Action implements ActionExtension.JumpListener {
    // Only for local client
    private int tickAfterJump;
    private double runSpeed;

    public WallRun(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.GRAPPLE,
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
        if (wallDirection == null || wallDirection.asVec().dot(new Vec3(lookAngle.x, 0, lookAngle.z).normalize()) <= 1. / Mth.SQRT_OF_TWO)
            return false;

        var gravityAttr = player.getAttribute(Attributes.GRAVITY);
        if (gravityAttr == null) return false;
        var gravity = gravityAttr.getValue();

        var jumpHeight = Mth.square(player.getJumpPower(1.0f)) / (2.1 * gravity);
        var jumpScale = getWallRunHeightScale(player, wallDirection, jumpHeight);
        if (jumpScale <= 0) return false;

        runSpeed = 1.5 * Math.sqrt(jumpHeight * jumpScale * gravity);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        var player = parkourability.player();
        var deltaMove = player.getDeltaMovement();
        player.setDeltaMovement(deltaMove.x, runSpeed, deltaMove.z);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        var player = parkourability.player();
        PlayerAnimator.get((AbstractClientPlayer) player).start(AnimationRegistries.get().animations().WALL_RUN);

        var pos = player.position();
        var lookVec = EntityUtil.getHorizontalLookAngle(player);
        var blockPos = new BlockPos(
                Mth.floor(pos.x + lookVec.x), Mth.floor(pos.y + 0.1), Mth.floor(pos.z + lookVec.z)
        );
        var blockState = player.level().getBlockState(blockPos);
        if (blockState.isAir()) return;
        var random = player.level().random;
        var rotatedLookVec = lookVec.yRot(Mth.HALF_PI);
        for (var i = 0; i < 5; i++) {
            var particleMove = lookVec
                    .scale(random.nextDouble() * 0.4).reverse()
                    .add(rotatedLookVec.scale((random.nextDouble() - 0.5) * 0.4));
            var particlePos = pos
                    .add(lookVec.scale(player.getBbWidth() / 2))
                    .add(rotatedLookVec.scale(player.getBbWidth() * (random.nextDouble() - 0.5)));

            player.level().addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                    particlePos.x, particlePos.y + (random.nextDouble() - 0.5) * 0.3, particlePos.z,
                    particleMove.x, particleMove.y, particleMove.z
            );
        }
        parkourability.player().playSound(ParCoolSoundEvents.WALL_RUN.get());
    }

    @OnlyIn(Dist.CLIENT)
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
        var level = player.level();
        var baseBB = player.getBoundingBox().move(wallVec.x * playerHalfWidth, 0, wallVec.z * playerHalfWidth);
        for (var i = 3; i <= 4; i++) {
            if (level.noCollision(baseBB.move(0, jumpHeight * i, 0))) {
                return i - 2;
            }
        }
        return 0;
    }
}
