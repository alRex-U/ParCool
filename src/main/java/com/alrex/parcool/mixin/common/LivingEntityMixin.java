package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, IForgeLivingEntity {
    @Shadow
    @Nullable
    public abstract AttributeInstance getAttribute(Attribute p_21052_);

    @Shadow
    @Final
    private static UUID SPEED_MODIFIER_SPRINTING_UUID;

    @Shadow
    @Final
    private static AttributeModifier SPEED_MODIFIER_SPRINTING;

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    public void onSetSprinting(boolean sprint, CallbackInfo ci) {
        if (!(((Object) this) instanceof Player player)) {
            return;
        }
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability.getBehaviorEnforcer().noSprintMarks.enforce() || parkourability.getStamina().isExhausted()) {
            super.setSprinting(sprint = false);
            ci.cancel();
        } else if (parkourability.getBehaviorEnforcer().sprintMarks.enforce()) {
            super.setSprinting(sprint = true);
            ci.cancel();
        }
        if (ci.isCancelled()) {
            var attributeinstance = getAttribute(Attributes.MOVEMENT_SPEED);
            if (attributeinstance.getModifier(SPEED_MODIFIER_SPRINTING_UUID) != null) {
                attributeinstance.removeModifier(SPEED_MODIFIER_SPRINTING);
            }
            if (sprint) attributeinstance.addTransientModifier(SPEED_MODIFIER_SPRINTING);
        }
    }
}
