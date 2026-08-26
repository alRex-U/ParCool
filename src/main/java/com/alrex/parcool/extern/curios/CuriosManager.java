package com.alrex.parcool.extern.curios;

import com.alrex.parcool.common.item.armor.EquipAble;
import com.alrex.parcool.extern.ModManager;
import com.alrex.parcool.extern.curios.capability.ParCoolCuriosCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.stream.Stream;

public class CuriosManager extends ModManager {
    public CuriosManager() {
        super("curios");
    }

    @Override
    public void init() {
        super.init();
        if (isInstalled()) {
            ModLoadingContext.get().getActiveContainer().getEventBus().register(ParCoolCuriosCapabilities.class);
        }
    }

    public Stream<ItemStack> getGeneralEquipments(Player player) {
        if (!isInstalled()) return Stream.empty();
        return CuriosApi.getCuriosInventory(player)
                .map(it -> it.findCurios(curio -> curio.getItem() instanceof EquipAble).stream().map(SlotResult::stack))
                .orElseGet(Stream::empty);
    }

    public boolean isEquipped(Player player, Item item) {
        if (!isInstalled()) return false;
        return CuriosApi.getCuriosInventory(player).map(it -> it.isEquipped(item)).orElse(Boolean.FALSE);
    }
}
