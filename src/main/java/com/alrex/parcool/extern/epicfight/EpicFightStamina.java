package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.stamina.ParCoolStamina;
import com.alrex.parcool.extern.AdditionalMods;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EpicFightStamina implements IStamina {
    private final Player player;
    private float consumeBuffer = 0;
    private final ParCoolStamina parcoolStamina;

    public EpicFightStamina(Player player) {
        this.player = player;
        parcoolStamina = new ParCoolStamina(player);
    }

    @Override
    public int getActualMaxStamina() {
        if (AdditionalMods.epicFight().isBattleMode(player)) {
            PlayerPatch<?> patch = AdditionalMods.epicFight().getPlayerPatch(player);
            if (patch == null) return 0;
            return (int) patch.getMaxStamina();
        } else {
            return parcoolStamina.getActualMaxStamina();
        }
    }

    @Override
    public int get() {
        if (AdditionalMods.epicFight().isBattleMode(player)) {
            PlayerPatch<?> patch = AdditionalMods.epicFight().getPlayerPatch(player);
            if (patch == null) return 0;
            return (int) patch.getStamina();
        } else {
            return parcoolStamina.get();
        }
    }

    @Override
    public int getOldValue() {
        if (AdditionalMods.epicFight().isBattleMode(player)) {
            return get();
        } else {
            return parcoolStamina.getOldValue();
        }
    }

    @Override
    public void consume(int value) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        if (isExhausted()
                || parkourability.getActionInfo().isStaminaInfinite(player.isSpectator() || player.isCreative())
                || player.hasEffect(Effects.INEXHAUSTIBLE.get())
        ) return;
        if (AdditionalMods.epicFight().isBattleMode(player)) {
            consumeBuffer += value / 60f;
        } else {
            parcoolStamina.consume(value);
        }
    }

    @Override
    public void recover(int value) {
        if (!AdditionalMods.epicFight().isBattleMode(player)) {
            parcoolStamina.recover(value);
        }
    }

    @Override
    public boolean isExhausted() {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return false;
        if (parkourability.getActionInfo().isStaminaInfinite(player.isSpectator() || player.isCreative())
                || player.hasEffect(Effects.INEXHAUSTIBLE.get())
        ) return false;
        if (AdditionalMods.epicFight().isBattleMode(player)) {
            PlayerPatch<?> patch = AdditionalMods.epicFight().getPlayerPatch(player);
            if (patch == null) return false;
            return patch.getStamina() < 0.1f;
        }
        return parcoolStamina.isExhausted();
    }

    @Override
    public void setExhaustion(boolean value) {
        if (!AdditionalMods.epicFight().isBattleMode(player)) {
            parcoolStamina.setExhaustion(value);
        }
    }

    @Override
    public void tick() {
        if (!AdditionalMods.epicFight().isBattleMode(player)) {
            parcoolStamina.tick();
        }
    }

    @Override
    public void updateOldValue() {
        if (!AdditionalMods.epicFight().isBattleMode(player)) {
            parcoolStamina.updateOldValue();
        }
    }

    @Override
    public void set(int value) {
        if (!AdditionalMods.epicFight().isBattleMode(player)) {
            parcoolStamina.set(value);
        }
    }

    @Override
    public boolean wantToConsumeOnServer() {
        return AdditionalMods.epicFight().isBattleMode(player) && consumeBuffer != 0f;
    }

    @Override
    public int getRequestedValueConsumedOnServer() {
        int neededValue = (int) (consumeBuffer * 10000f);
        consumeBuffer = 0f;
        return neededValue;
    }

    @Override
    public boolean isImposingExhaustionPenalty() {
        return !AdditionalMods.epicFight().isBattleMode(player) && parcoolStamina.isImposingExhaustionPenalty();
    }

    public static void consumeOnServer(ServerPlayer player, int value) {
        PlayerPatch<?> patch = AdditionalMods.epicFight().getPlayerPatch(player);
        if (patch == null) return;
        patch.resetActionTick();
        patch.setStamina(patch.getStamina() - value / 10000f);
    }
}