package com.alrex.parcool.extern.feathers;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.stamina.ParCoolStamina;
import com.alrex.parcool.extern.ModManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

public class FeathersManager extends ModManager {
    public FeathersManager() {
        super("feathers");
    }

    @Override
    public void initInClient() {
        if (isInstalled()) {
            MinecraftForge.EVENT_BUS.register(EventConsumerForFeathers.class);
        }
    }

    public IStamina newFeathersStaminaFor(Player player) {
        if (!isInstalled()) return new ParCoolStamina(player);
		return new FeathersStamina(player);
	}

    @OnlyIn(Dist.CLIENT)
    public boolean isUsingFeathers(Player player) {
        return isInstalled() && IStamina.get(player) instanceof FeathersStamina;
    }
}
