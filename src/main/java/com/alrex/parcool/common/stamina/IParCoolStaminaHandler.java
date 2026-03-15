package com.alrex.parcool.common.stamina;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public interface IParCoolStaminaHandler {
    //@OnlyIn(Dist.CLIENT)
    public ReadonlyStamina initializeStamina(Player player, ReadonlyStamina current);

    //@OnlyIn(Dist.CLIENT)
    public ReadonlyStamina consume(Player player, ReadonlyStamina current, int value);

    //@OnlyIn(Dist.CLIENT)
    public ReadonlyStamina recover(Player player, ReadonlyStamina current, int value);

    //@OnlyIn(Dist.CLIENT)
    public default ReadonlyStamina onTick(Player player, ReadonlyStamina current) {
        return current;
    }

    //@OnlyIn(Dist.CLIENT)
    public default boolean shouldShowHUD(Player player) {
        return false;
    }

    //@OnlyIn(Dist.CLIENT)
    public default boolean shouldImposeExhaustionPenalty(LocalPlayer player, ReadonlyStamina current) {
        return true;
    }

    public default void processOnServer(Player player, int value) {
    }

    public default boolean isExternalStamina() {
        return false;
    }
}
