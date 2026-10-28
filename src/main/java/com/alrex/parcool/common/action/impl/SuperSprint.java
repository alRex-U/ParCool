package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;

// Super sprint is internal action to detect whether some actions needing FastRun are possible to be triggered
public class SuperSprint extends ContinuableAction {
    public SuperSprint(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
    }

    public boolean checkActionsEnabledWithSprint() {
        return ParCool.getConfig().client().substituteSprintForFastRun.get();
    }

    @Override
    public boolean canContinue() {
        if (checkActionsEnabledWithSprint()) {
            return parkourability.player().isSprinting();
        } else {
            return parkourability.get(ParCoolActions.FAST_RUN).isDoing();
        }
    }

    @Override
    public boolean canStart() {
        return canContinue();
    }
}
