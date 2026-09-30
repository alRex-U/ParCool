package com.alrex.parcool.common.stamina.handlers;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.payload.StaminaProcessOnServerPayload;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.fabric.PacketDistributor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class HungerStaminaHandler implements IParCoolStaminaHandler {
    private int consumed = 0;

    @Environment(EnvType.CLIENT)
    @Override
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current) {
        return new ReadonlyStamina(false, player.getFoodData().getFoodLevel(), 20);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value) {
        consumed += value;
        return current;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value) {
        return current;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
        if (consumed > 0) {
            PacketDistributor.sendToServer(
                    new StaminaProcessOnServerPayload(StaminaType.HUNGER, consumed));
            consumed = 0;
        }
        return new ReadonlyStamina(
                player.getFoodData().getFoodLevel() < 6, player.getFoodData().getFoodLevel(), 20);
    }

    @Override
    public void processOnServer(Player player, int value) {
        player.causeFoodExhaustion(value / 1000f);
    }
}
