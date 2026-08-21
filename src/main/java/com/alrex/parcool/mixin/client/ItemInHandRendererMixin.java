package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.item.armor.EquipAble;
import com.alrex.parcool.common.item.misc.GrapplingHookItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow
    protected abstract void renderPlayerArm(PoseStack p_109347_, MultiBufferSource p_109348_, int p_109349_, float p_109350_, float p_109351_, HumanoidArm p_109352_);

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void onRenderItemInRenderArmWithItem(AbstractClientPlayer player, float p_109373_, float p_109374_, InteractionHand hand, float p_109376_, ItemStack stack, float p_109378_, PoseStack poseStack, MultiBufferSource bufferSource, int p_109381_, CallbackInfo ci) {
        var arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean showBareArm = stack.getItem() instanceof EquipAble equipAble
                ? equipAble.renderWhenIn(player, arm)
                : stack.getItem() instanceof GrapplingHookItem && GrapplingHookItem.isDeployed(player);
        if (showBareArm) {
            poseStack.popPose();
            this.renderPlayerArm(poseStack, bufferSource, p_109381_, p_109378_, p_109376_, arm);
            poseStack.pushPose();
        }
    }
}
