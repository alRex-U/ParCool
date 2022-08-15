package com.alrex.parcool.mixin;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyBindings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	@Shadow
	@Final
	public PlayerAbilities abilities;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
		super(p_i48577_1_, p_i48577_2_);
	}

	@Inject(method = "travel", at = @At("TAIL"))
	public void onTravel(Vector3d p_213352_1_, CallbackInfo ci) {
		if (abilities.flying && ParCoolConfig.CONFIG_CLIENT.creativeFlyingLikeSuperMan.get()) {
			if (KeyBindings.getKeyForward().isDown()) {
				setDeltaMovement(getLookAngle().normalize().scale(getDeltaMovement().multiply(1, 1.65, 1).length()));
			}
		}
	}
}
