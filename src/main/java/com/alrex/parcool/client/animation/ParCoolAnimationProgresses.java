package com.alrex.parcool.client.animation;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.AnimationProgress;
import com.alrex.parcool.client.animation.system.registration.AnimationProgresses;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParCoolAnimationProgresses {
    public ID<AnimationProgress> DIVE_PROGRESS = AnimationProgresses.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "builtin/dive"),
            (player, partialTick) -> Parkourability.get(player).get(ParCoolActions.DIVE).getAnimationProgress(partialTick)
    );
    public ID<AnimationProgress> JUMP_CHARGING_PROGRESS = AnimationProgresses.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "builtin/jump_charging"),
            (player, partialTick) -> Parkourability.get(player).get(ParCoolActions.CHARGE_JUMP).getChargeProgress(partialTick)
    );
}
