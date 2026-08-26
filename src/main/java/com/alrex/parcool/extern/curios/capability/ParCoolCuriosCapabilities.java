package com.alrex.parcool.extern.curios.capability;

import com.alrex.parcool.common.item.ParCoolItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosCapability;

public class ParCoolCuriosCapabilities {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                CuriosCapability.ITEM,
                (itemStack, v) -> new EquipAbleCuriosWrapper(itemStack),
                ParCoolItems.TRACEUR_GLOVES.get(),
                ParCoolItems.TRACEUR_BOOTS.get()
        );
    }
}
