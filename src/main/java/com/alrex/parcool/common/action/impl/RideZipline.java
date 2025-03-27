package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.utilities.ZiplineUtil;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class RideZipline extends Action {
    @Nullable
    private ZiplineRopeEntity ridingZipline;

    @Override
    public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
        if (KeyBindings.getKeyBindRideZipline().isDown()
                && !player.isOnGround()
                && !player.isInWater()
                && !parkourability.get(Dive.class).isDoing()
                && !parkourability.get(Vault.class).isDoing()
                && !parkourability.get(HangDown.class).isDoing()
                && !parkourability.get(Flipping.class).isDoing()
                && !parkourability.get(HorizontalWallRun.class).isDoing()
                && !parkourability.get(VerticalWallRun.class).isDoing()
        ) {
            ZiplineRopeEntity ropeEntity = ZiplineUtil.getHangableZipline(player.level, player);
            if (ropeEntity == null) return false;
            ridingZipline = ropeEntity;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        return KeyBindings.getKeyBindRideZipline().isDown();
    }

    @Override
    public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        player.setDeltaMovement(0, 0, 0);
    }

    @Override
    public void onStop(PlayerEntity player) {
        ridingZipline = null;
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }
}
