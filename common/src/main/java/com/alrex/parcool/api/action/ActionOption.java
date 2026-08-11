package com.alrex.parcool.api.action;

import com.alrex.parcool.api.LogicalSide;
import net.minecraft.world.entity.Pose;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class ActionOption {
    public record Value(
            StaminaConsumption defaultCost,
            int learningCost,
            @Nullable ActionEntry<? extends ContinuableAction> parent,
            @Nullable Pose neededPose,
            Set<ActionEntry<? extends Action>> beforeProcessedActions,
            boolean needOnGround,
            boolean needNotOnGround,
            boolean availableInFluid,
            boolean availableNotInFluid,
            boolean availableWithFallFlying,
            LogicalSide triggeredSide
    ) {
    }

    private StaminaConsumption staminaConsumption = StaminaConsumption.ZERO;
    private int learningCost;
    @Nullable
    private ActionEntry<? extends ContinuableAction> parent = null;
    private final TreeSet<ActionEntry<? extends Action>> beforeProcessedActions = new TreeSet<>();
    @Nullable
    private Pose neededPose = Pose.STANDING;
    private boolean availableInFluid = false;
    private boolean availableNotInFluid = true;
    private boolean availableWithFallFlying = false;
    private boolean needOnGround = false;
    private boolean needNotOnGround = false;
    private LogicalSide triggeredSide = LogicalSide.CLIENT;

    public Value build() {
        return new Value(
                staminaConsumption, learningCost, parent, neededPose, beforeProcessedActions, needOnGround, needNotOnGround, availableInFluid, availableNotInFluid, availableWithFallFlying, triggeredSide
        );
    }

    public ActionOption() {
    }

    public ActionOption cost(StaminaConsumption consumption) {
        this.staminaConsumption = consumption;
        return this;
    }

    public ActionOption learningCost(int cost) {
        this.learningCost = cost;
        return this;
    }

    public ActionOption parent(ActionEntry<? extends ContinuableAction> parentAction) {
        this.parent = parentAction;
        this.beforeProcessedActions.add(parentAction);
        return this;
    }

    public ActionOption needPose(@Nullable Pose pose) {
        this.neededPose = pose;
        return this;
    }

    public ActionOption needOnGround(boolean value) {
        this.needOnGround = value;
        return this;
    }

    public ActionOption needNotOnGround(boolean value) {
        this.needNotOnGround = value;
        return this;
    }

    public ActionOption availableInFluid(boolean availableInFluid) {
        this.availableInFluid = availableInFluid;
        return this;
    }

    public ActionOption availableNotInFluid(boolean availableNotInFluid) {
        this.availableNotInFluid = availableNotInFluid;
        return this;
    }

    public ActionOption availableWithFallFlying(boolean availableWithFallFlying) {
        this.availableWithFallFlying = availableWithFallFlying;
        return this;
    }

    public ActionOption triggeredSide(LogicalSide side) {
        this.triggeredSide = side;
        return this;
    }

    @SafeVarargs
    public final ActionOption processedAfter(ActionEntry<? extends Action>... actions) {
        this.beforeProcessedActions.addAll(Arrays.asList(actions));
        return this;
    }
}
