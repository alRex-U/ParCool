package com.alrex.parcool.mixin.client;

import com.alrex.parcool.ParCoolConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerMixin extends AbstractClientPlayer {
	public ClientPlayerMixin(ClientLevel p_i50991_1_, GameProfile p_i50991_2_) {
		super(p_i50991_1_, p_i50991_2_);
	}

	private boolean oldSprinting = false;

	@Inject(method = "aiStep", at = @At("HEAD"))
	public void onAiStep(CallbackInfo ci) {

		LocalPlayer player = (LocalPlayer) (Object) this;
		if (player.isLocalPlayer() && ParCoolConfig.CONFIG_CLIENT.continueSprintWhenColliding.get()) {
			boolean flag = !player.input.hasForwardImpulse() || !((float) player.getFoodData().getFoodLevel() > 6.0F || this.getAbilities().mayfly);
			boolean flag1 = flag || this.isInWater() && !this.isUnderWater();
			if (oldSprinting && !flag1) {
				player.setSprinting(true);
			}
			oldSprinting = player.isSprinting();
		}
	}
}
