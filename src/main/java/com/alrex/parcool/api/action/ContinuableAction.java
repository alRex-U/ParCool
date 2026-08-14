package com.alrex.parcool.api.action;

import com.alrex.parcool.common.Parkourability;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;

public abstract class ContinuableAction extends Action {
    public ContinuableAction(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
    }

    public ContinuableAction(Parkourability parkourability, ActionEntry<? extends Action> entry, Collection<ActionEntry<? extends ContinuableAction>> exclusiveActions) {
        super(parkourability, entry, exclusiveActions);
    }

    private boolean doing = false;
    private int doingTick = 0;
    private int notDoingTick = 0;

    public boolean isDoing() {
        return doing;
    }

    public int getDoingTick() {
        return doingTick;
    }

    public int getNotDoingTick() {
        return notDoingTick;
    }

    @Override
    public void tick() {
        if (doing) {
            doingTick++;
            notDoingTick = 0;
        } else {
            notDoingTick++;
            doingTick = 0;
        }
        super.tick();
    }

    public void tickOnWorking() {
        onWorkingTick();
        if (parkourability.player().level().isClientSide) {
            onWorkingTickInClient();
            if (parkourability.player().isLocalPlayer()) {
                onWorkingTickInLocalClient();
            } else {
                onWorkingTickInOtherClient();
            }
        } else {
            onWorkingTickInServer();
        }

        takeCost(StaminaConsumption.Type.WORKING);
    }

    @Override
    public void start() {
        if (doing) return;
        doing = true;
        super.start();
    }

    public void finish() {
        if (!doing) return;
        for (var child : entry.children()) {
            if (parkourability.get(child) instanceof ContinuableAction continuableAction) {
                continuableAction.finish();
            }
        }
        doing = false;
        if (parkourability.player().isLocalPlayer()) {
            takeCost(StaminaConsumption.Type.FINISH);
            onStopInLocalClient();
            onStopInClient();
        } else {
            if (parkourability.player().level().isClientSide()) {
                onStopInOtherClient();
                onStopInClient();
            } else {
                onStopInServer();
            }
        }
        onStop();
    }

    public final boolean isPossibleToContinue() {
        if (!isPossible()) return false;
        if (MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.TryToContinue(parkourability.player(), this)))
            return false;
        return canContinue();
    }

    @Override
    public boolean isReadyToStart() {
        return getNotDoingTick() >= 3 && super.isReadyToStart();
    }

    public abstract boolean canContinue();

    public void onStop() {
    }

    public void onStopInServer() {
    }

    @OnlyIn(Dist.CLIENT)
    public void onStopInClient() {
    }

    @OnlyIn(Dist.CLIENT)
    public void onStopInOtherClient() {
    }

    @OnlyIn(Dist.CLIENT)
    public void onStopInLocalClient() {
    }

    public void onWorkingTick() {
    }

    public void onWorkingTickInServer() {
    }

    @OnlyIn(Dist.CLIENT)
    public void onWorkingTickInClient() {
    }

    @OnlyIn(Dist.CLIENT)
    public void onWorkingTickInOtherClient() {
    }

    @OnlyIn(Dist.CLIENT)
    public void onWorkingTickInLocalClient() {
    }
}
