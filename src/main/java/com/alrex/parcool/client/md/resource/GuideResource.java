package com.alrex.parcool.client.md.resource;

import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.client.md.CompiledMarkdown;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@OnlyIn(Dist.CLIENT)
public class GuideResource {
    public static GuideResource empty() {
        return new GuideResource(Collections.emptyList(), Collections.emptyMap());
    }

    private final Map<PageEntry, CompiledMarkdown> data;
    private final Map<ResourceLocation, CompiledMarkdown> locationToContent;
    private final List<PageGroupEntry> groups;

    public GuideResource(List<PageGroupEntry> pages, Map<PageEntry, CompiledMarkdown> data) {
        this.data = data;
        this.groups = pages;
        locationToContent = new TreeMap<>();
        for (var dataEntry : data.entrySet()) {
            locationToContent.put(dataEntry.getKey().resourceLocation(), dataEntry.getValue());
        }
    }

    public List<PageGroupEntry> getPages() {
        return groups;
    }

    @Nullable
    public CompiledMarkdown get(PageEntry location) {
        return data.get(location);
    }

    @Nullable
    public CompiledMarkdown get(ResourceLocation location) {
        return locationToContent.get(location);
    }

    @Nullable
    public CompiledMarkdown get(ActionEntry<?> action) {
        return locationToContent.get(GuideResourceManager.getLocation(action));
    }
}
