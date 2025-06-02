package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.stamina.ParCoolStamina;
import com.alrex.parcool.extern.ModManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import javax.annotation.Nullable;

public class EpicFightManager extends ModManager {

    public EpicFightManager() {
        super("epicfight");
    }

    public IStamina newFeathersStaminaFor(Player player) {
        if (!isInstalled()) return IStamina.Type.Default.newInstance(player);
        return new EpicFightStamina(player);
    }

    @Nullable
    PlayerPatch<?> getPlayerPatch(Player player) {
        if (!isInstalled()) {
            return null;
        }
        return EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
    }

    public boolean isEpicFightMode(Player player) {
        PlayerPatch<?> patch = getPlayerPatch(player);
        if (patch == null) return false;
        return patch.isEpicFightMode();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean canShowStaminaHUD(Player player) {
        var stamina = IStamina.get(player);
        if (stamina instanceof ParCoolStamina) return true;
        return stamina instanceof EpicFightStamina && !isEpicFightMode(player);
    }
}