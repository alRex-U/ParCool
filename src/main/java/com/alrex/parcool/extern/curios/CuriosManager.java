package com.alrex.parcool.extern.curios;

import com.alrex.parcool.common.item.armor.EquipAble;
import com.alrex.parcool.extern.ModManager;
import com.alrex.parcool.extern.curios.capability.EquipAbleCuriosWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class CuriosManager extends ModManager {
    public CuriosManager() {
        super("curios");
    }

    public @Nullable ICapabilityProvider initEquipAbleCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (!isInstalled()) return null;
        if (!(stack.getItem() instanceof EquipAble)) return null;
        return new EquipAbleCuriosWrapper(stack);
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
