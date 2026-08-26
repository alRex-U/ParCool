package com.alrex.parcool.extern.curios.capability;

import com.alrex.parcool.common.item.armor.EquipAble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class EquipAbleCuriosWrapper implements ICurio {

    private final ItemStack itemStack;
    private final Multimap<Holder<Attribute>, AttributeModifier> modifiers;
    public EquipAbleCuriosWrapper(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.modifiers = HashMultimap.create();
        if (itemStack.getItem() instanceof EquipAble equipAble) {
            for (var attrEntry : itemStack.getAttributeModifiers().modifiers()) {
                if (attrEntry.slot().test(equipAble.getEquipmentSlot())) {
                    modifiers.put(attrEntry.attribute(), attrEntry.modifier());
                }
            }
        }
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id) {
        return modifiers;
    }

    @Override
    public ItemStack getStack() {
        return itemStack;
    }
}
