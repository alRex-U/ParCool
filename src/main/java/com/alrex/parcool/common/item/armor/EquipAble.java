package com.alrex.parcool.common.item.armor;

import com.alrex.parcool.client.renderer.entity.layers.EquipmentRenderLayer;
import com.alrex.parcool.common.item.DyeAble;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface EquipAble extends Equipable {
    default boolean hasCustomEquipmentColor() {
        return false;
    }

    default int getCustomEquipmentColor(ItemStack stack) {
        if (this instanceof DyeAble dyeAble) {
            return dyeAble.getColor(stack);
        }
        return 0xFFFFFFFF;
    }

    default boolean renderWhenIn(LivingEntity entity, HumanoidArm arm) {
        boolean renderInCustomLayer = false;
        var slot = (arm == entity.getMainArm() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        for (var layer : EquipmentRenderLayer.values()) {
            if (getEquipmentTexture(entity, slot, layer) != null) {
                renderInCustomLayer = true;
                break;
            }
        }
        return renderInCustomLayer;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    ResourceLocation getEquipmentTexture(LivingEntity entity, EquipmentSlot slot, EquipmentRenderLayer layer);
}
