package com.alrex.parcool.client.renderer.entity.layers;

import com.alrex.parcool.common.item.armor.EquipAble;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

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
            @Nonnull MultiBufferSource multiBufferSource,
            int i, @Nonnull T entity, float v, float v1, float v2, float v3, float v4, float v5
    ) {
        for (var slot : EquipmentSlot.values()) {
            var itemStack = entity.getItemBySlot(slot);
            var item = itemStack.getItem();
            if (!(item instanceof EquipAble equipAble)) continue;
            for (var layer : EquipmentRenderLayer.values()) {
                var texture = equipAble.getEquipmentTexture(entity, slot, layer);
                if (texture == null) continue;
                var model = switch (layer) {
                    case INNER -> innerLayer;
                    case OUTER -> outerLayer;
                };
                setPartVisibility(model, slot);
                this.getParentModel().copyPropertiesTo(model);
                if (equipAble.hasCustomEquipmentColor()) {
                    var color = equipAble.getCustomEquipmentColor(itemStack);
                    int r = (color & 0xFF0000) >> 16;
                    int g = (color & 0x00FF00) >> 8;
                    int b = (color & 0x0000FF);
                    renderModel(poseStack, multiBufferSource, i, itemStack.hasFoil(), model, r / 255f, g / 255f, b / 255f, texture);
                } else {
                    renderModel(poseStack, multiBufferSource, i, itemStack.hasFoil(), model, 1f, 1f, 1f, texture);
                }
            }
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

    private void renderModel(PoseStack stack, MultiBufferSource bufferSource, int p_117109_, boolean hasFoil, Model model, float r, float g, float b, ResourceLocation textureLocation) {
        var vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(textureLocation), false, hasFoil);
        model.renderToBuffer(stack, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
    }
}
