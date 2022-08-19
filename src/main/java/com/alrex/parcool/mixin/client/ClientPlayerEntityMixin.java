package com.alrex.parcool.mixin.client;

import com.alrex.parcool.ParCoolConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
	public ClientPlayerEntityMixin(ClientWorld p_i50991_1_, GameProfile p_i50991_2_) {
		super(p_i50991_1_, p_i50991_2_);
	}

	private boolean oldSprinting = false;

	@Inject(method = "aiStep", at = @At("HEAD"))
	public void onAiStep(CallbackInfo ci) {
		ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
		if (player.isLocalPlayer() && ParCoolConfig.CONFIG_CLIENT.continueSprintWhenColliding.get()) {
			boolean flag = !player.input.hasForwardImpulse() || !((float) player.getFoodData().getFoodLevel() > 6.0F || this.abilities.mayfly);
			boolean flag1 = flag || this.isInWater() && !this.isUnderWater();
			if (oldSprinting && !flag1) {
				player.setSprinting(true);
			}
			oldSprinting = player.isSprinting();
		}
	}
}
