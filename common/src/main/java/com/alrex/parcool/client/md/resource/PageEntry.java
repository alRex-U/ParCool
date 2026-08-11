package com.alrex.parcool.client.md.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public record PageEntry(String translationKey, ResourceLocation resourceLocation) implements Comparable<PageEntry> {
    @Override
    public int compareTo(PageEntry o) {
        return resourceLocation.compareTo(o.resourceLocation);
    }
}
