package com.alrex.parcool.mixin.client;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.impl.Animation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
	@Shadow
	@Final
	private boolean slim;
	@Shadow
	@Final
	public ModelPart jacket;
	@Shadow
	@Final
	public ModelPart rightPants;
	@Shadow
	@Final
	public ModelPart rightSleeve;
	@Shadow
	@Final
	public ModelPart leftPants;
	@Shadow
	@Final
	public ModelPart leftSleeve;

	@Shadow
	@Final
	private ModelPart ear;
	private PlayerModelTransformer transformer = null;

	public PlayerModelMixin(ModelPart p_170677_) {
		super(p_170677_);
	}

	public PlayerModelMixin(ModelPart p_170679_, Function<ResourceLocation, RenderType> p_170680_) {
		super(p_170679_, p_170680_);
	}

	@Inject(method = "Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
	protected void onSetupAnimHead(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (!(entity instanceof Player)) return;
		PlayerModel model = (PlayerModel) (Object) this;
		Player player = (Player) entity;
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

	@Inject(method = "Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
	protected void onSetupAnimTail(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (!(entity instanceof Player)) return;
		Player player = (Player) entity;
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
