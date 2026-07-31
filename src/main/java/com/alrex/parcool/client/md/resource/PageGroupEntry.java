package com.alrex.parcool.client.md.resource;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public record PageGroupEntry(String translationKey, List<PageEntry> content) {
}
