package com.alrex.parcool.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrappleTipModel {
    public static final int TEXTURE_WIDTH = 32;
    public static final int TEXTURE_HEIGHT = 32;
    private static final String PART_NAME = "tip";

    private final ModelPart tip;

    public GrappleTipModel(ModelPart root) {
        this.tip = root.getChild(PART_NAME);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild(
                PART_NAME,
                CubeListBuilder.create()
                        .texOffs(24, 0).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F)
                        .texOffs(0, 6).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 0.0F)
                        .texOffs(12, 0).addBox(-3.0F, -6.0F, 3.0F, 6.0F, 6.0F, 0.0F)
                        .texOffs(-6, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 0.0F, 6.0F)
                        .texOffs(12, 0).mirror().addBox(-3.0F, -6.0F, -3.0F, 0.0F, 6.0F, 6.0F).mirror(false)
                        .texOffs(12, 0).addBox(3.0F, -6.0F, -3.0F, 0.0F, 6.0F, 6.0F),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay) {
        tip.render(poseStack, consumer, light, overlay);
    }
}
