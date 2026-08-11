package com.alrex.parcool.client.renderer.entity.layers;

import com.alrex.parcool.ParCool;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

@Environment(EnvType.CLIENT)
public class ParCoolModelLayers {
    public static final ModelLayerLocation INNER_EQUIPMENT = new ModelLayerLocation(ParCool.resourceLocation("equipment"), "equipment");
    public static final ModelLayerLocation INNER_EQUIPMENT_SLIM = new ModelLayerLocation(ParCool.resourceLocation("equipment"), "equipment");
    public static final ModelLayerLocation OUTER_EQUIPMENT = new ModelLayerLocation(ParCool.resourceLocation("equipment"), "equipment");
    public static final ModelLayerLocation OUTER_EQUIPMENT_SLIM = new ModelLayerLocation(ParCool.resourceLocation("equipment"), "equipment");

    public static void register() {
        var innerEquipmentDefinition = LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.6f), 0.0F), 64, 32);
        var outerEquipmentDefinition = LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.1f), 0.0F), 64, 32);
        EntityModelLayerRegistry.register(INNER_EQUIPMENT, () -> innerEquipmentDefinition);
        EntityModelLayerRegistry.register(INNER_EQUIPMENT_SLIM, () -> innerEquipmentDefinition);
        EntityModelLayerRegistry.register(OUTER_EQUIPMENT, () -> outerEquipmentDefinition);
        EntityModelLayerRegistry.register(OUTER_EQUIPMENT_SLIM, () -> outerEquipmentDefinition);
    }
}
