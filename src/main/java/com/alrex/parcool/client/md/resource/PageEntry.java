package com.alrex.parcool.client.md.resource;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public record PageEntry(String translationKey, ResourceLocation resourceLocation) implements Comparable<PageEntry> {
    @Override
    public int compareTo(@Nonnull PageEntry o) {
        return resourceLocation.compareTo(o.resourceLocation);
    }
}
