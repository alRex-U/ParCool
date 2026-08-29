package com.alrex.parcool.client.renderer.entity.layers;

import com.alrex.parcool.common.item.armor.EquipAble;
import com.alrex.parcool.extern.AdditionalMods;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ParCoolGeneralEquipmentLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private final A innerLayer;
    private final A outerLayer;

    public ParCoolGeneralEquipmentLayer(RenderLayerParent<T, M> parent, A innerLayer, A outerLayer) {
        super(parent);
        this.innerLayer = innerLayer;
        this.outerLayer = outerLayer;
    }

    @Override
    public void render(
            @Nonnull PoseStack poseStack,
            @Nonnull MultiBufferSource buffer,
            int packedLight,
            @Nonnull T entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (!shouldRenderModel(entity)) return;
        for (var slot : EquipmentSlot.values()) {
            var itemStack = entity.getItemBySlot(slot);
            if (!(itemStack.getItem() instanceof EquipAble equipAble)) continue;
            for (var layer : EquipmentRenderLayer.values()) {
                renderEquipment(poseStack, buffer, equipAble, itemStack, entity, slot, layer, packedLight);
            }
        }
        if (entity instanceof Player player && AdditionalMods.curios().isInstalled()) {
            AdditionalMods.curios().getRenderAbleGeneralEquipments(player).forEach(itemStack -> {
                if (!(itemStack.getItem() instanceof EquipAble equipAble)) return;
                var slot = equipAble.getEquipmentSlot();
                for (var layer : EquipmentRenderLayer.values()) {
                    renderEquipment(poseStack, buffer, equipAble, itemStack, entity, slot, layer, packedLight);
                }
            });
        }
    }

    private void renderEquipment(
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            EquipAble equipAble,
            ItemStack itemStack,
            T entity,
            EquipmentSlot slot,
            EquipmentRenderLayer layer,
            int light
    ) {
        var texture = equipAble.getEquipmentTexture(entity, slot, layer);
        if (texture == null) return;
        var model = switch (layer) {
            case INNER -> innerLayer;
            case OUTER -> outerLayer;
        };
        setPartVisibility(model, slot);
        this.getParentModel().copyPropertiesTo(model);
        if (equipAble.hasCustomEquipmentColor()) {
            var color = equipAble.getCustomEquipmentColor(itemStack);
            renderModel(poseStack, multiBufferSource, light, itemStack.hasFoil(), model, color, texture);
        } else {
            renderModel(poseStack, multiBufferSource, light, itemStack.hasFoil(), model, ~0, texture);
        }
    }

    protected void setPartVisibility(A model, EquipmentSlot slot) {
        model.setAllVisible(false);
        switch (slot) {
            case HEAD:
                model.head.visible = true;
                model.hat.visible = true;
                break;
            case CHEST, MAINHAND, OFFHAND:
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                break;
            case LEGS:
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                break;
            case FEET:
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
        }
    }

    private void renderModel(PoseStack stack, MultiBufferSource bufferSource, int packedLight, boolean hasFoil, Model model, int color, ResourceLocation textureLocation) {
        var vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(textureLocation), hasFoil);
        model.renderToBuffer(stack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
    }

    private boolean shouldRenderModel(Entity entity) {
        return !(entity instanceof Player player) || !AdditionalMods.epicFight().isBattleMode(player);
    }
}
