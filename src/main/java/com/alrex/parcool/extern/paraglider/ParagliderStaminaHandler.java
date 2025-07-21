package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import tictim.paraglider.api.stamina.Stamina;

public class ParagliderStaminaHandler implements IParCoolStaminaHandler {
    private Stamina getInternalInstance(Player player) {
        return Stamina.get(player);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current) {
        var stamina = getInternalInstance(player);
        return new ReadonlyStamina(false, (int) stamina.stamina(), (int) Math.ceil(stamina.maxStamina()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value) {
        return current;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value) {
        return current;
    }

    @Override
    public ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
        var stamina = getInternalInstance(player);
        return new ReadonlyStamina(
                stamina.isDepleted(),
                (int) stamina.stamina(),
                (int) Math.ceil(stamina.maxStamina())
        );
    }

    @Override
    public boolean isExternalStamina() {
        return true;
    }
}
