package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.capability.IStamina;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
	public LocalPlayerMixin(ClientLevel p_i50991_1_, GameProfile p_i50991_2_) {
		super(p_i50991_1_, p_i50991_2_);
	}

	@Inject(method = "Lnet/minecraft/client/player/LocalPlayer;aiStep()V", at = @At("TAIL"))
	public void onAiStep(CallbackInfo ci) {
		LocalPlayer player = (LocalPlayer) (Object) this;
		IStamina stamina = IStamina.get(player);
		if (stamina == null) return;
		if (stamina.isExhausted()) {
			player.setSprinting(false);
		}
	}
}
