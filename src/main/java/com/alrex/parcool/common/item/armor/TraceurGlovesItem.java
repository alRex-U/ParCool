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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class TraceurGlovesItem extends Item implements EquipAble, DyeAble {
    private static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/models/equipment/traceur_gloves.png");
    private static final UUID MODIFIER_UUID = UUID.fromString("f757de68-b2f5-4b41-af69-438ae46d15dc");
    private final Multimap<Attribute, AttributeModifier> equipModifier;
    private final Multimap<Attribute, AttributeModifier> inHandModifier;

    public TraceurGlovesItem(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> chestModifiersBuilder = ImmutableMultimap.builder();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> inHandModifiersBuilder = ImmutableMultimap.builder();
        var slideDownAttr = new AttributeModifier(MODIFIER_UUID, "Glove Slide down deceleration", 0.1, AttributeModifier.Operation.ADDITION);
        chestModifiersBuilder.put(ParCoolAttributes.SLIDE_DOWN_DECELERATION.get(), slideDownAttr);
        inHandModifiersBuilder.put(ParCoolAttributes.SLIDE_DOWN_DECELERATION.get(), slideDownAttr);

        chestModifiersBuilder.put(ParCoolAttributes.MAX_STAMINA.get(), new AttributeModifier(MODIFIER_UUID, "Glove stamina bonus", 0.2, AttributeModifier.Operation.MULTIPLY_BASE));
        chestModifiersBuilder.put(Attributes.ATTACK_SPEED, new AttributeModifier(MODIFIER_UUID, "Glove attack speed", 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        this.equipModifier = chestModifiersBuilder.build();
        this.inHandModifier = inHandModifiersBuilder.build();
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
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return getEquipmentSlot(stack) == armorType;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return switch (slot) {
            case CHEST -> equipModifier;
            case MAINHAND, OFFHAND -> inHandModifier;
            default -> super.getAttributeModifiers(slot, stack);
        };
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return super.getArmorTexture(stack, entity, slot, type);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> lines, TooltipFlag tooltipFlag) {
        lines.add(Component.translatable("parcool.gui.text.glove.tooltip").withStyle(ChatFormatting.GRAY));
        DyeAble.appendHoverText(this, stack, level, lines, tooltipFlag);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            @Nullable
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
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

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return AdditionalMods.curios().initEquipAbleCapabilities(stack, nbt);
    }
}
