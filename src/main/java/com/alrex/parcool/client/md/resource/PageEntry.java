package com.alrex.parcool.client.md.resource;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public record PageEntry(String translationKey, ResourceLocation resourceLocation) implements Comparable<PageEntry> {
    @Override
    public int compareTo(@Nonnull PageEntry o) {
        return resourceLocation.compareTo(o.resourceLocation);
    }
}
