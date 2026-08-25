package com.alrex.parcool.common.item.armor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.client.renderer.entity.layers.EquipmentRenderLayer;
import com.alrex.parcool.common.item.DyeAble;
import com.alrex.parcool.extern.AdditionalMods;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class TraceurGlovesItem extends Item implements EquipAble, DyeAble {
    private static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/models/equipment/traceur_gloves.png");
    private static final ResourceLocation MODIFIER_ID = ParCool.resourceLocation("traceur_glove");
    private final ItemAttributeModifiers equipModifier;

    public TraceurGlovesItem(Properties properties) {
        super(properties);
        var modifiersBuilder = ItemAttributeModifiers.builder();
        var slideDownAttr = new AttributeModifier(MODIFIER_ID, 0.1, AttributeModifier.Operation.ADD_VALUE);
        modifiersBuilder.add(ParCoolAttributes.SLIDE_DOWN_DECELERATION, slideDownAttr, EquipmentSlotGroup.MAINHAND);
        modifiersBuilder.add(ParCoolAttributes.SLIDE_DOWN_DECELERATION, slideDownAttr, EquipmentSlotGroup.OFFHAND);
        modifiersBuilder.add(ParCoolAttributes.SLIDE_DOWN_DECELERATION, slideDownAttr, EquipmentSlotGroup.CHEST);

        modifiersBuilder.add(ParCoolAttributes.MAX_STAMINA, new AttributeModifier(MODIFIER_ID, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.CHEST);
        modifiersBuilder.add(Attributes.ATTACK_SPEED, new AttributeModifier(MODIFIER_ID, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.CHEST);
        equipModifier = modifiersBuilder.build();
    }

    @Nonnull
    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return getEquipmentSlot();
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
        lines.add(Component.translatable("parcool.gui.text.glove.tooltip").withStyle(ChatFormatting.GRAY));
        DyeAble.appendHoverText(this, stack, context, lines, tooltipFlag);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            @Nullable
            public HumanoidModel.ArmPose getArmPose(@Nonnull LivingEntity entityLiving, @Nonnull InteractionHand hand, @Nonnull ItemStack itemStack) {
                if (shouldRenderAsEquipment(entityLiving, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND)) {
                    return HumanoidModel.ArmPose.EMPTY;
                }
                return null;
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean shouldRenderAsEquipment(LivingEntity entity, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            var chestItem = entity.getItemBySlot(EquipmentSlot.CHEST);
            if (chestItem.getItem() instanceof TraceurGlovesItem) return false;
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    public ResourceLocation getEquipmentTexture(LivingEntity entity, EquipmentSlot slot, EquipmentRenderLayer layer) {
        if (!shouldRenderAsEquipment(entity, slot)) return null;
        return layer == EquipmentRenderLayer.INNER ? TEXTURE_LOCATION : null;
    }

    public static boolean isEquipped(LivingEntity entity) {
        for (var slot : new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
            var item = entity.getItemBySlot(slot).getItem();
            if (item instanceof TraceurGlovesItem) return true;
        }
        return false;
    }

    @Override
    public boolean hasCustomEquipmentColor() {
        return true;
    }

    @Override
    public int getDefaultColor() {
        return 0xFFDD93;
    }

    /*
    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return AdditionalMods.curios().initEquipAbleCapabilities(stack, nbt);
    }
     */
}
