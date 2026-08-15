package com.alrex.parcool.client.md.resource;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public record PageGroupEntry(String translationKey, List<PageEntry> content) {
}
