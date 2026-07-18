package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.IRequestable;
import net.minecraft.client.player.AbstractClientPlayer;

import javax.annotation.Nullable;

public class Castaway extends ContinuableAction implements IRequestable<Castaway.RequestContext> {
    private static final int MAX_TICK = 14;

    @Nullable
    private HangOn.HangState hangState;

    public Castaway(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
    }

    @Override
    public boolean canContinue() {
        return getDoingTick() <= MAX_TICK;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public boolean canStart(RequestContext requestContext) {
        this.hangState = requestContext.hangState;
        var player = parkourability.player();
        return player.level.noCollision(player.getBoundingBox().deflate(0.1).expandTowards(0, -2, 0));
    }

    @Override
    public void onStartInLocalClient() {
        if (hangState == null) return;
        var player = parkourability.player();
        var deltaMove = player.getDeltaMovement();
        var reversedHangVec = hangState.direction().asVec().reverse();
        player.setDeltaMovement(deltaMove.x + reversedHangVec.x * 0.15, 0.3, deltaMove.z + reversedHangVec.z * 0.15);
    }

    @Override
    public void onStart() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().CASTAWAY);
    }

    public record RequestContext(HangOn.HangState hangState) {
    }
}
