package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(EntityType<? extends LivingEntity> p_i48577_1_, Level p_i48577_2_) {
		super(p_i48577_1_, p_i48577_2_);
	}

	@Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
	public void onTryToStartFallFlying(CallbackInfoReturnable<Boolean> cir) {
		Player player = (Player) (Object) this;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability != null && parkourability.getClingToCliff().isCling()) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}

	@Inject(method = "travel", at = @At("TAIL"))
	public void onTravel(Vec3 p_213352_1_, CallbackInfo ci) {
	}
}
