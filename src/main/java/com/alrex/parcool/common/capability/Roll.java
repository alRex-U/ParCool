package com.alrex.parcool.common.capability;

import com.alrex.parcool.client.input.KeyBindings;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Roll implements IRoll{
    private boolean rollReady=false;
    private boolean rolling=false;
    private int rollingTime=0;

    @Override
    public boolean canContinueRollReady(ClientPlayerEntity player) {
        return rollReady && KeyBindings.getKeyRoll().isKeyDown() && !rolling && !player.isInWaterOrBubbleColumn();
    }

    @Override
    public boolean canRollReady(ClientPlayerEntity player) {
        return !player.isOnGround() && KeyBindings.getKeyRoll().isKeyDown() && !player.isInWaterOrBubbleColumn();
    }

    @Override
    public boolean isRollReady() { return rollReady; }
    @Override
    public boolean isRolling() { return rolling; }

    @Override
    public void setRollReady(boolean rollReady) { this.rollReady = rollReady; }
    @Override
    public void setRolling(boolean rolling) { this.rolling = rolling; if (!rolling)rollingTime=0; }

    @Override
    public void updateRollingTime() {
        if (rolling){
            rollingTime++;
            if (rollingTime>getRollAnimateTime()){
                setRolling(false);
            }
        } else rollingTime=0;
    }

    @Override
    public int getRollingTime() { return rollingTime; }

    @Override
    public int getStaminaConsumption() { return 50; }

    @Override
    public int getRollAnimateTime() { return 10; }

}
