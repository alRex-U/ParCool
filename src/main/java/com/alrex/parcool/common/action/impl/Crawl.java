package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Pose;

import java.util.List;

public class Crawl extends ContinuableAction {
    private static final BehaviorEnforcer.ID CANCEL_SPRINT_ID = BehaviorEnforcer.newID();

    public Crawl(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.DIVE,
                ParCoolActions.HORIZONTAL_WALL_RUN,
                ParCoolActions.HANG_ON,
                ParCoolActions.DODGE,
                ParCoolActions.HIDE_IN_BLOCK
        ));
    }

    @Override
    public boolean canStart() {
        if (parkourability.player().getForcedPose() != null) return false;
        if (ParCoolKeyBinds.CRAWL.key().isDown()) return true;
        return parkourability.player().hasPose(Pose.SWIMMING);
    }

    @Override
    public boolean canContinue() {
        if (ParCoolKeyBinds.CRAWL.key().isDown()) return true;
        return !parkourability.player().canEnterPose(Pose.CROUCHING) && parkourability.player().hasPose(Pose.SWIMMING);
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().CRAWL);
    }

    @Override
    public void onStartInLocalClient() {
        parkourability.getBehaviorEnforcer().addMarkerEnforcingNoSprint(CANCEL_SPRINT_ID, this::isDoing);
        if (parkourability.get(ParCoolActions.FAST_RUN).isDoing()) {
            parkourability.request(ParCoolActions.SLIDE, new Slide.RequestContext());
        }
    }

    @Override
    public void onStart() {
        parkourability.player().setForcedPose(Pose.SWIMMING);
    }

    @Override
    public void onStop() {
        if (parkourability.player().getForcedPose() == Pose.SWIMMING) {
            parkourability.player().setForcedPose(null);
        }
    }
}
