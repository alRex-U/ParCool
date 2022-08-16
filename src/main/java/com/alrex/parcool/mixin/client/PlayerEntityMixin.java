package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
		super(p_i48577_1_, p_i48577_2_);
	}

	@Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
	public void onTryToStartFallFlying(CallbackInfoReturnable<Boolean> cir) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability != null && parkourability.getClingToCliff().isCling()) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
}
