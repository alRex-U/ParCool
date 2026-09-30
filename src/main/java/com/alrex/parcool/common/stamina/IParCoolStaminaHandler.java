package com.alrex.parcool.common.stamina;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public interface IParCoolStaminaHandler {
    @Environment(EnvType.CLIENT)
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current);

    @Environment(EnvType.CLIENT)
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value);

    @Environment(EnvType.CLIENT)
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value);

    @Environment(EnvType.CLIENT)
    public default ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
        return current;
    }

    @Environment(EnvType.CLIENT)
    public default boolean shouldShowHUD(LocalPlayer player) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public default boolean shouldImposeExhaustionPenalty(
            LocalPlayer player, ReadonlyStamina current) {
        return true;
    }

    public default void processOnServer(Player player, int value) {}

    public default boolean isExternalStamina() {
        return false;
    }
}
