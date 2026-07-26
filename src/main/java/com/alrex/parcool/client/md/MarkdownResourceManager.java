package com.alrex.parcool.client.md;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.TreeMap;

public class MarkdownResourceManager extends SimplePreparableReloadListener<MarkdownResource> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static MarkdownResourceManager INSTANCE = null;

    public static MarkdownResourceManager getInstance() {
        if (INSTANCE == null) INSTANCE = new MarkdownResourceManager();
        return INSTANCE;
    }

    private MarkdownResource resource = MarkdownResource.empty();

    public MarkdownResource getResource() {
        return resource;
    }

    public static void register(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(getInstance());
    }

    @Nonnull
    @Override
    protected MarkdownResource prepare(@Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profilerFiller) {
        var map = new TreeMap<ResourceLocation, CompiledMarkdown>();
        for (var resourceEntry : resourceManager.listResources("parcool_guide", location -> location.getPath().endsWith(".md")).entrySet()) {
            try (var reader = resourceEntry.getValue().openAsReader()) {
                map.put(resourceEntry.getKey(), MarkdownParser.parse(reader));
            } catch (IOException e) {
                LOGGER.error("{} at loading guide {}", e.getClass().getSimpleName(), resourceEntry.getKey());
            }
        }
        return new MarkdownResource(map);
    }

    @Override
    protected void apply(@Nonnull MarkdownResource markdownResource, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profilerFiller) {
        this.resource = markdownResource;
    }
}
