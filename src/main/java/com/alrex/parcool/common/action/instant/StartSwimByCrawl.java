package com.alrex.parcool.common.action.instant;

import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.InstantAction;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.world.entity.player.Player;

import java.nio.ByteBuffer;

public class StartSwimByCrawl extends InstantAction {
    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        return player.isInWaterOrBubble()
                && !player.isSwimming()
                && KeyRecorder.keyCrawlState.isPressed();
    }

    @Override
    public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
        player.setSprinting(true);
        player.setSwimming(true);
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }
}
