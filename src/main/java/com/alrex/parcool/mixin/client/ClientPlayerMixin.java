package com.alrex.parcool.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerMixin extends AbstractClientPlayer {

	private boolean oldSprinting = false;

	public ClientPlayerMixin(ClientLevel p_234112_, GameProfile p_234113_, @Nullable ProfilePublicKey p_234114_) {
		super(p_234112_, p_234113_, p_234114_);
	}

	@Inject(method = "aiStep", at = @At("HEAD"))
	public void onAiStep(CallbackInfo ci) {

		LocalPlayer player = (LocalPlayer) (Object) this;
		if (player.isLocalPlayer()) {
			boolean flag = !player.input.hasForwardImpulse() || !((float) player.getFoodData().getFoodLevel() > 6.0F || this.getAbilities().mayfly);
			boolean flag1 = flag || this.isInWater() && !this.isUnderWater();
			if (oldSprinting && !flag1) {
				player.setSprinting(true);
			}
			oldSprinting = player.isSprinting();
		}
	}
}
