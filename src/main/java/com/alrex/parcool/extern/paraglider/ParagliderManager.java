package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.common.stamina.handlers.ParCoolStaminaHandler;
import com.alrex.parcool.extern.ModManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import tictim.paraglider.api.ParagliderItemCapability;

import javax.annotation.Nullable;

public class ParagliderManager extends ModManager {

    public ParagliderManager() {
        super("paraglider");
        NeoForge.EVENT_BUS.register(EventConsumerForParaglider.class);
    }

    @Nullable
    public IParCoolStaminaHandler newParagliderStaminaHandlerFor(Player player) {
        if (isUsingParagliderStamina(Parkourability.get(player))) return new ParagliderStaminaHandler();
        return new ParCoolStaminaHandler();
    }

    public boolean isUsingParagliderStamina(Parkourability parkourability) {
        if (!isInstalled()) return false;
        var forcedStamina = parkourability.getServerLimitation().getForcedStamina();
        if (forcedStamina == StaminaType.PARAGLIDER) return true;
        return forcedStamina == StaminaType.NONE && parkourability.getClientInfo().getRequestedStamina() == StaminaType.PARAGLIDER;
    }

    public boolean isFallingWithParaglider(Player player) {
        if (isInstalled()) {
            for (var item : new ItemStack[]{player.getMainHandItem(), player.getOffhandItem()}) {
                var cap = item.getCapability(ParagliderItemCapability.CAPABILITY);
                if (cap == null) cap = ParagliderItemCapability.defaultImpl();
                if (cap.isParagliding(item)) {
                    return true;
                }
            }
        }
        return false;
    }
}