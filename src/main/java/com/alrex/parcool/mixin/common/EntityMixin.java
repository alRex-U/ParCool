package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin extends CapabilityProvider<Entity> {
    @Shadow
    public boolean noPhysics;

    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "move", at = @At("HEAD"))
    public void onMove(MoverType moverType, Vec3 movement, CallbackInfo ci) {
        if (!(((Object) this) instanceof Player player)) {
            return;
        }
        var parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        if (parkourability.getBehaviorEnforcer().noPhysicsMarks.enforce()) {
            noPhysics = true;
        }
    }
}
