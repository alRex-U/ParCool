package com.alrex.parcool.client.renderer.entity.layers;

import com.alrex.parcool.ParCool;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
public class ParCoolModelLayers {
    public static final ModelLayerLocation INNER_EQUIPMENT = new ModelLayerLocation(ResourceLocation.withDefaultNamespace("player"), "parcool.equipment_in");
    public static final ModelLayerLocation INNER_EQUIPMENT_SLIM = new ModelLayerLocation(ResourceLocation.withDefaultNamespace("player_slim"), "parcool.equipment_in");
    public static final ModelLayerLocation OUTER_EQUIPMENT = new ModelLayerLocation(ResourceLocation.withDefaultNamespace("player"), "parcool.equipment_out");
    public static final ModelLayerLocation OUTER_EQUIPMENT_SLIM = new ModelLayerLocation(ParCool.resourceLocation("player_slim"), "parcool.equipment_out");

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        var innerEquipmentDefinition = LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.1f), 0.0F), 64, 32);
        var outerEquipmentDefinition = LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.6f), 0.0F), 64, 32);
        event.registerLayerDefinition(INNER_EQUIPMENT, () -> innerEquipmentDefinition);
        event.registerLayerDefinition(INNER_EQUIPMENT_SLIM, () -> innerEquipmentDefinition);
        event.registerLayerDefinition(OUTER_EQUIPMENT, () -> outerEquipmentDefinition);
        event.registerLayerDefinition(OUTER_EQUIPMENT_SLIM, () -> outerEquipmentDefinition);
    }
}
