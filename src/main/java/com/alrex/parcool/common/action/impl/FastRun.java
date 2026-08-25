package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.UUID;

public class FastRun extends ContinuableAction {
    private static final ResourceLocation FAST_RUNNING_MODIFIER_ID = ParCool.resourceLocation("modi.fast_run");
    private static final BehaviorEnforcer.ID ENFORCE_SPRINT_ID = BehaviorEnforcer.newID();

    public FastRun(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.CRAWL,
                ParCoolActions.HANG_ON,
                ParCoolActions.HANG_DOWN,
                ParCoolActions.DIVE,
                ParCoolActions.DODGE
        ));
    }

    @Override
    public boolean canStart() {
        return parkourability.player().isSprinting()
                && !((LocalPlayer) parkourability.player()).isMovingSlowly()
                && Minecraft.getInstance().options.keySprint.isDown();
    }

    @Override
    public boolean canContinue() {
        return canStart()
                && ((LocalPlayer) parkourability.player()).input.hasForwardImpulse();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().FAST_RUN);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        parkourability.getBehaviorEnforcer().sprintMarks.add(ENFORCE_SPRINT_ID, this::isDoing);
    }

    @Override
    public void onTickInServer() {
        var player = parkourability.player();
        var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;
        var modifierAttr = player.getAttribute(ParCoolAttributes.FAST_RUN_SPEED);
        if (modifierAttr == null) return;
        var modifier = attr.getModifier(FAST_RUNNING_MODIFIER_ID);
        if (isDoing()) {
            if (modifier == null) {
                attr.addTransientModifier(new AttributeModifier(
                        FAST_RUNNING_MODIFIER_ID,
                        modifierAttr.getValue(),
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        } else {
            if (modifier != null) {
                attr.removeModifier(FAST_RUNNING_MODIFIER_ID);
            }
        }
    }
}
