package com.alrex.parcool.api.stamina;

import net.minecraft.resources.ResourceLocation;

public record StaminaTypeEntry<T extends AbstractLocalStamina>(ResourceLocation id, String name,
                                                               IStaminaProvider<T> provider) {
}
