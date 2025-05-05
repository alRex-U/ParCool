package com.alrex.parcool.common.stamina;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IParCoolStaminaHandler {
    @OnlyIn(Dist.CLIENT)
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current);

    @OnlyIn(Dist.CLIENT)
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value);

    @OnlyIn(Dist.CLIENT)
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value);

    @OnlyIn(Dist.CLIENT)
    public default ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
        return current;
    }

    public default void processOnServer(Player player, int value) {
    }
}
