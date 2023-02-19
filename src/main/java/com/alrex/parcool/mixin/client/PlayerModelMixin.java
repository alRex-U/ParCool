package com.alrex.parcool.mixin.client;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Animation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends BipedModel<T> {
	@Shadow
	@Final
	private boolean slim;
	@Shadow
	@Final
	public ModelRenderer jacket;
	@Shadow
	@Final
	public ModelRenderer rightPants;
	@Shadow
	@Final
	public ModelRenderer rightSleeve;
	@Shadow
	@Final
	public ModelRenderer leftPants;
	@Shadow
	@Final
	public ModelRenderer leftSleeve;

	@Shadow
	@Final
	private ModelRenderer ear;
	private PlayerModelTransformer transformer = null;

	public PlayerModelMixin(float p_i1148_1_) {
		super(p_i1148_1_);
	}

	@Inject(method = "Lnet/minecraft/client/renderer/entity/model/PlayerModel;setupAnim(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
	protected void onSetupAnimHead(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (!(entity instanceof PlayerEntity)) return;
		PlayerModel model = (PlayerModel) (Object) this;
		PlayerEntity player = (PlayerEntity) entity;
		if (player.isLocalPlayer()
				&& Minecraft.getInstance().options.getCameraType().isFirstPerson()
				&& ParCoolConfig.CONFIG_CLIENT.disableFPVAnimation.get()
		) return;

		transformer = new PlayerModelTransformer(
				player,
				model,
				slim,
				ageInTicks,
				limbSwing,
				limbSwingAmount,
				netHeadYaw,
				headPitch
		);
		transformer.reset();

		Animation animation = Animation.get(player);
		if (animation == null) return;

		boolean shouldCancel = animation.animatePre(player, transformer);
		transformer.copyFromBodyToWear();
		if (shouldCancel) {
			transformer = null;
			info.cancel();
		}
	}

	@Inject(method = "Lnet/minecraft/client/renderer/entity/model/PlayerModel;setupAnim(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
	protected void onSetupAnimTail(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (!(entity instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity) entity;
		if (player.isLocalPlayer()
				&& Minecraft.getInstance().options.getCameraType().isFirstPerson()
				&& ParCoolConfig.CONFIG_CLIENT.disableFPVAnimation.get()
		) return;

		Animation animation = Animation.get(player);
		if (animation == null) {
			transformer = null;
			return;
		}

		if (transformer != null) {
			animation.animatePost(player, transformer);
			transformer.copyFromBodyToWear();
			transformer = null;
		}
	}

}
