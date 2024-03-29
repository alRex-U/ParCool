package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import javax.annotation.Nullable;

public class EpicFightManager {
    private static boolean modInstalled = false;

    public static boolean isEpicFightInstalled() {
        return modInstalled;
    }

    public static void init() {
        @Nullable
        var mod = ModList.get().getModFileById("epicfight");
        modInstalled = mod != null;
    }

    public static IStamina newEpicFightStaminaFor(Player player) {
        if (!modInstalled) return IStamina.Type.Default.newInstance(player);
        return new EpicFightStamina(player);
    }

    @Nullable
    static PlayerPatch<?> getPlayerPatch(Player player) {
        if (!isEpicFightInstalled()) {
            return null;
        }
        return EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
    }

    public static boolean isBattleMode(Player player) {
        PlayerPatch<?> patch = getPlayerPatch(player);
        if (patch == null) return false;
        return patch.isBattleMode();
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isUsingEpicFightStamina(Player player) {
        return IStamina.get(player) instanceof EpicFightStamina && isBattleMode(player);
    }
}