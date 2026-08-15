package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.extern.ModManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModLoadingContext;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import javax.annotation.Nullable;

public class EpicFightManager extends ModManager {
    public EpicFightManager() {
        super("epicfight");
    }

    @Override
    public void init() {
        super.init();
        if (!isInstalled()) return;
        ModLoadingContext.get().getActiveContainer().getEventBus().register(new EpicFightModLoadEventHandler());
    }

    @Nullable
    PlayerPatch<?> getPlayerPatch(Player player) {
        if (!isInstalled()) return null;

        return EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
    }

    public boolean isBattleMode(Player player) {
        if (!isInstalled()) return false;
        PlayerPatch<?> patch = getPlayerPatch(player);
        if (patch == null) return false;
        return patch.isEpicFightMode();
    }
}
