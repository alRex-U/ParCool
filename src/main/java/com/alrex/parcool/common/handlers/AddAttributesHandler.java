package com.alrex.parcool.common.handlers;

import com.alrex.parcool.api.Attributes;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public class AddAttributesHandler {
    public static void addPlayerAttributes(AttributeSupplier.Builder builder) {
        builder.add(Attributes.MAX_STAMINA);
        builder.add(Attributes.STAMINA_RECOVERY);
    }
}
