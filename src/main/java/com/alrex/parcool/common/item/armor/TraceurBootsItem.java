package com.alrex.parcool.common.item.armor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.client.renderer.entity.layers.EquipmentRenderLayer;
import com.alrex.parcool.common.item.DyeAble;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TraceurBootsItem extends Item implements EquipAble, DyeAble {
    private static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/models/equipment/traceur_boots.png");
    private static final ResourceLocation MODIFIER_ID = ParCool.resourceLocation("traceur_boots");
    private final ItemAttributeModifiers equipModifier;

    public TraceurBootsItem(Properties properties) {
        super(properties);
        var equipModifiersBuilder = ItemAttributeModifiers.builder();
        equipModifiersBuilder.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(MODIFIER_ID, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.FEET);
        equipModifiersBuilder.add(ParCoolAttributes.MAX_STAMINA, new AttributeModifier(MODIFIER_ID, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.FEET);
        equipModifiersBuilder.add(ParCoolAttributes.BREAKFALL_DAMAGE_REDUCTION, new AttributeModifier(MODIFIER_ID, 0.15, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET);
        equipModifiersBuilder.add(ParCoolAttributes.FAST_RUN_SPEED, new AttributeModifier(MODIFIER_ID, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.FEET);
        equipModifiersBuilder.add(ParCoolAttributes.FAST_SWIM_SPEED, new AttributeModifier(MODIFIER_ID, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.FEET);
        this.equipModifier = equipModifiersBuilder.build();
    }

    @Nonnull
    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.FEET;
    }

    @Override
    public EquipmentSlot getEquipmentSlot(@Nonnull ItemStack stack) {
        return getEquipmentSlot();
    }

    @Override
    public boolean canEquip(@Nonnull ItemStack stack, @Nonnull EquipmentSlot armorType, @Nonnull LivingEntity entity) {
        return getEquipmentSlot(stack) == armorType;
    }

    @Nonnull
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(@Nonnull ItemStack stack) {
        return equipModifier;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        DyeAble.appendHoverText(this, stack, context, lines, tooltipFlag);
    }

    @OnlyIn(Dist.CLIENT)
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

    /*
    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return AdditionalMods.curios().initEquipAbleCapabilities(stack, nbt);
    }
     */
}
