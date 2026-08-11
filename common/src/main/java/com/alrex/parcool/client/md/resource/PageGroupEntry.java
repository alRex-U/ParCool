package com.alrex.parcool.client.md.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public record PageGroupEntry(String translationKey, List<PageEntry> content) {
}
