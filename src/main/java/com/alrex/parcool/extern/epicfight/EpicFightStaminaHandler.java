package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.payload.StaminaProcessOnServerPayload;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.extern.AdditionalMods;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import javax.annotation.Nullable;

public class EpicFightStaminaHandler implements IParCoolStaminaHandler {
    private float consumed = 0;

    private ReadonlyStamina readCurrentStamina(Player player, @Nullable ReadonlyStamina current) {
        var patch = AdditionalMods.epicFight().getPlayerPatch(player);
        if (patch == null) {
            if (current == null) return ReadonlyStamina.createDefault();
            else return current;
        }

        return new ReadonlyStamina(
                !patch.hasStamina(1),
                (int) patch.getStamina(),
                (int) patch.getMaxStamina()
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current) {
        return readCurrentStamina(player, current);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value) {
        consumed += value / 60f;
        return current;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value) {
        consumed -= value / 60f;
        return current;
    }

    @Override
    public ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
        if (consumed != 0) {
            PacketDistributor.sendToServer(new StaminaProcessOnServerPayload(StaminaType.EPIC_FIGHT, (int) (consumed * 2048)));
            consumed = 0;
        }
        return readCurrentStamina(player, current);
    }

    @Override
    public boolean shouldImposeExhaustionPenalty(LocalPlayer player, ReadonlyStamina current) {
        return false;
    }

    @Override
    public void processOnServer(Player player, int value) {
        float consumedValue = value / 2048f;
        PlayerPatch<?> patch = AdditionalMods.epicFight().getPlayerPatch(player);
        if (patch == null) return;
        patch.resetActionTick();
        patch.setStamina(patch.getStamina() - consumedValue);
    }

    @Override
    public boolean isExternalStamina() {
        return true;
    }
}
