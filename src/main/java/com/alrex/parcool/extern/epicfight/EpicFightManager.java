package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.common.stamina.handlers.ParCoolStaminaHandler;
import com.alrex.parcool.extern.ModManager;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import javax.annotation.Nullable;

public class EpicFightManager extends ModManager {
    public EpicFightManager() {
        super("epicfight");
    }

    public IParCoolStaminaHandler newStaminaHandlerFor(Player player) {
        if (isUsingEpicFightStamina(Parkourability.get(player))) {
            return new EpicFightStaminaHandler();
        }
        return new ParCoolStaminaHandler();
    }

    @Nullable
    PlayerPatch<?> getPlayerPatch(Player player) {
        if (!isInstalled()) {
            return null;
        }
        return EpicFightCapabilities.getPlayerPatch(player);
    }

    public boolean isUsingEpicFightStamina(Parkourability parkourability) {
        if (!isInstalled()) return false;
        var forcedStamina = parkourability.getServerLimitation().getForcedStamina();
        if (forcedStamina == StaminaType.EPIC_FIGHT) return true;
        return forcedStamina == StaminaType.NONE && parkourability.getClientInfo().getRequestedStamina() == StaminaType.EPIC_FIGHT;
    }
}
