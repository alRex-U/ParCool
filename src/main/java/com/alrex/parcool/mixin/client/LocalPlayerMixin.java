package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

	public LocalPlayerMixin(ClientLevel p_234112_, GameProfile p_234113_, @Nullable ProfilePublicKey p_234114_) {
		super(p_234112_, p_234113_, p_234114_);
	}

	@Inject(method = "isShiftKeyDown", at = @At("HEAD"), cancellable = true)
	public void onIsShiftKeyDown(CallbackInfoReturnable<Boolean> cir) {
		Parkourability parkourability = Parkourability.get((Player) (Object) this);

		if (parkourability == null) return;
		if (parkourability.getCancelMarks().cancelSneak()) {
			cir.setReturnValue(false);
		}
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
