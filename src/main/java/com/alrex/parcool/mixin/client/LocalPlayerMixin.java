package com.alrex.parcool.mixin.client;


import com.alrex.parcool.common.attachment.Attachments;
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

	public LocalPlayerMixin(ClientLevel p_250460_, GameProfile p_249912_) {
		super(p_250460_, p_249912_);
	}

	@Inject(method = "Lnet/minecraft/client/player/LocalPlayer;aiStep()V", at = @At("TAIL"))
	public void onAiStep(CallbackInfo ci) {
		LocalPlayer player = (LocalPlayer) (Object) this;
		var stamina = player.getData(Attachments.STAMINA);
		if (stamina == null) return;
		if (stamina.isExhausted()) {
			player.setSprinting(false);
		}
	}
}
