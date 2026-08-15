package com.alrex.parcool.client.renderer;

import com.alrex.parcool.ParCool;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class GrapplingHookItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final ResourceLocation GUI_MODEL = ParCool.resourceLocation("item/grappling_hook_gui");
    public static final ResourceLocation HELD_MODEL = ParCool.resourceLocation("item/grappling_hook_in_hand");

    public GrapplingHookItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(GUI_MODEL);
        event.register(HELD_MODEL);
    }

    @Override
    public void renderByItem(
            @Nonnull ItemStack stack,
            @Nonnull ItemTransforms.TransformType transformType,
            @Nonnull PoseStack poseStack,
            @Nonnull MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean flat = transformType == ItemTransforms.TransformType.GUI
                || transformType == ItemTransforms.TransformType.FIXED
                || transformType == ItemTransforms.TransformType.GROUND;
        BakedModel model = minecraft.getModelManager().getModel(flat ? GUI_MODEL : HELD_MODEL);

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);
        minecraft.getItemRenderer().render(stack, transformType, false, poseStack, buffer, light, overlay, model);
        poseStack.popPose();
    }
}
