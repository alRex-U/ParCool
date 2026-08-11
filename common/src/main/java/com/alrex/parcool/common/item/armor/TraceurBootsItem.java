package com.alrex.parcool.common.item.armor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.client.renderer.entity.layers.EquipmentRenderLayer;
import com.alrex.parcool.common.item.DyeAble;
import com.alrex.parcool.extern.AdditionalMods;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TraceurBootsItem extends Item implements EquipAble, DyeAble {
    private static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/models/equipment/traceur_boots.png");
    private static final UUID MODIFIER_UUID = UUID.fromString("a2d93ed6-4dba-4fe3-944e-14f9eeff744d");
    private final Multimap<Attribute, AttributeModifier> equipModifier;

    public TraceurBootsItem(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> equipModifiersBuilder = ImmutableMultimap.builder();
        equipModifiersBuilder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MODIFIER_UUID, "Boots movement bonus", 0.05, AttributeModifier.Operation.MULTIPLY_BASE));
        equipModifiersBuilder.put(ParCoolAttributes.MAX_STAMINA.get(), new AttributeModifier(MODIFIER_UUID, "Boots stamina bonus", 0.2, AttributeModifier.Operation.MULTIPLY_BASE));
        equipModifiersBuilder.put(ParCoolAttributes.BREAKFALL_DAMAGE_REDUCTION.get(), new AttributeModifier(MODIFIER_UUID, "Boots breakfall bonus", 0.15, AttributeModifier.Operation.ADDITION));
        equipModifiersBuilder.put(ParCoolAttributes.FAST_RUN_SPEED.get(), new AttributeModifier(MODIFIER_UUID, "Boots fast_run bonus", 0.15, AttributeModifier.Operation.MULTIPLY_BASE));
        equipModifiersBuilder.put(ParCoolAttributes.FAST_SWIM_SPEED.get(), new AttributeModifier(MODIFIER_UUID, "Boots fast_swim bonus", 0.15, AttributeModifier.Operation.MULTIPLY_BASE));
        this.equipModifier = equipModifiersBuilder.build();
    }

    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.FEET;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return getEquipmentSlot(stack) == armorType;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.FEET) {
            return equipModifier;
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag tooltipFlag) {
        DyeAble.appendHoverText(this, stack, level, lines, tooltipFlag);
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    @Override
    public ResourceLocation getEquipmentTexture(LivingEntity entity, EquipmentSlot slot, EquipmentRenderLayer layer) {
        if (slot == EquipmentSlot.FEET && layer == EquipmentRenderLayer.INNER) return TEXTURE_LOCATION;
        return null;
    }

    @Override
    public boolean hasCustomEquipmentColor() {
        return true;
    }

    @Override
    public int getDefaultColor() {
        return 0xE0BD70;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return AdditionalMods.curios().initEquipAbleCapabilities(stack, nbt);
    }
}
