package com.alrex.parcool.client.renderer.entity.layers;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.renderer.entity.GrappleTipModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
public class ParCoolModelLayers {
    public static final ModelLayerLocation INNER_EQUIPMENT = new ModelLayerLocation(ResourceLocation.withDefaultNamespace("player"), "parcool.equipment_in");
    public static final ModelLayerLocation INNER_EQUIPMENT_SLIM = new ModelLayerLocation(ResourceLocation.withDefaultNamespace("player_slim"), "parcool.equipment_in");
    public static final ModelLayerLocation OUTER_EQUIPMENT = new ModelLayerLocation(ResourceLocation.withDefaultNamespace("player"), "parcool.equipment_out");
    public static final ModelLayerLocation OUTER_EQUIPMENT_SLIM = new ModelLayerLocation(ResourceLocation.withDefaultNamespace("player_slim"), "parcool.equipment_out");
    public static final ModelLayerLocation GRAPPLE_TIP = new ModelLayerLocation(ParCool.resourceLocation("grapple_tip"), "main");

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        var innerEquipmentDefinition = LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.1f), 0.0F), 64, 32);
        var outerEquipmentDefinition = LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.6f), 0.0F), 64, 32);
        event.registerLayerDefinition(INNER_EQUIPMENT, () -> innerEquipmentDefinition);
        event.registerLayerDefinition(INNER_EQUIPMENT_SLIM, () -> innerEquipmentDefinition);
        event.registerLayerDefinition(OUTER_EQUIPMENT, () -> outerEquipmentDefinition);
        event.registerLayerDefinition(OUTER_EQUIPMENT_SLIM, () -> outerEquipmentDefinition);
        event.registerLayerDefinition(GRAPPLE_TIP, GrappleTipModel::createLayer);
    }
}
