package com.alrex.parcool.extern.curios.capability;

import com.alrex.parcool.common.item.armor.EquipAble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.UUID;

public class EquipAbleCuriosWrapper implements ICurio, ICapabilityProvider {
    private final LazyOptional<ICurio> holder = LazyOptional.of(() -> this);
    private final ItemStack itemStack;
    private final Multimap<Attribute, AttributeModifier> modifiers;
    public EquipAbleCuriosWrapper(ItemStack itemStack) {
        this.itemStack = itemStack;
        if (itemStack.getItem() instanceof EquipAble equipAble) {
            this.modifiers = itemStack.getAttributeModifiers(equipAble.getEquipmentSlot());
        } else {
            this.modifiers = HashMultimap.create();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        return CuriosCapability.ITEM.orEmpty(capability, holder);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
        return modifiers;
    }

    @Override
    public ItemStack getStack() {
        return itemStack;
    }
}
