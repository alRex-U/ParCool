package com.alrex.parcool.common.stamina.handlers;

import com.alrex.parcool.common.attachment.stamina.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import net.minecraft.client.player.LocalPlayer;

public class InfiniteStaminaHandler implements IParCoolStaminaHandler {
    private static final ReadonlyStamina INSTANCE = new ReadonlyStamina(false, 1, 1);

    @Override
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current) {
        return INSTANCE;
    }

    @Override
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value) {
        return INSTANCE;
    }

    @Override
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value) {
        return INSTANCE;
    }
}
