package com.alrex.parcool.client.md;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public class MarkdownResource {
    public static MarkdownResource empty() {
        return new MarkdownResource(Collections.emptyMap());
    }

    private final Map<ResourceLocation, CompiledMarkdown> data;

    public MarkdownResource(Map<ResourceLocation, CompiledMarkdown> data) {
        this.data = data;
    }

    @Nullable
    public CompiledMarkdown get(ResourceLocation location) {
        return data.get(location);
    }
}
