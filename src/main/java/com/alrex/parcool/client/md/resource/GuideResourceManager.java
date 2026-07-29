package com.alrex.parcool.client.md.resource;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.client.md.CompiledMarkdown;
import com.alrex.parcool.client.md.MarkdownParser;
import com.alrex.parcool.client.md.resource.json.PageGroupJson;
import com.alrex.parcool.common.resource.json.ResourceLocationAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class GuideResourceManager extends SimplePreparableReloadListener<GuideResource> {
    private static final ResourceLocation PAGES_LOCATION = new ResourceLocation(ParCool.MOD_ID, "parcool_guide/pages.json");
    private static final Logger LOGGER = LogManager.getLogger();
    private static GuideResourceManager INSTANCE = null;
    private static final TypeToken<List<PageGroupJson>> PAGES_TYPE = new TypeToken<>() {
    };
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter())
            .create();

    public static GuideResourceManager getInstance() {
        if (INSTANCE == null) INSTANCE = new GuideResourceManager();
        return INSTANCE;
    }

    public static ResourceLocation getLocation(ActionEntry<?> action) {
        return new ResourceLocation(
                action.id().getNamespace(),
                "actions/" + action.id().getPath() + ".md"
        );
    }

    private GuideResource resource = GuideResource.empty();

    public GuideResource getResource() {
        return resource;
    }

    public static void register(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(getInstance());
    }

    @Nonnull
    @Override
    protected GuideResource prepare(@Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profilerFiller) {
        var langCode = Minecraft.getInstance().options.languageCode;
        var map = new TreeMap<PageEntry, CompiledMarkdown>();
        var loadingPages = new TreeSet<PageEntry>();
        var groupsEntries = new ArrayList<PageGroupEntry>();
        resourceManager.getResource(PAGES_LOCATION).ifPresent(resource -> {
            try (var reader = resource.openAsReader()) {
                var groups = GSON.<List<PageGroupJson>>fromJson(reader, PAGES_TYPE.getType());
                for (var group : groups) {
                    var pageEntries = new ArrayList<PageEntry>();
                    if (group.pages != null) {
                        for (var page : group.pages) {
                            var pageEntry = new PageEntry(page.title, page.location);
                            pageEntries.add(pageEntry);
                        }
                    }
                    if (group.generate != null) {
                        generatePageEntries(group.generate, pageEntries);
                    }
                    loadingPages.addAll(pageEntries);
                    groupsEntries.add(new PageGroupEntry(
                            group.group,
                            pageEntries.stream().toList()
                    ));
                }
            } catch (IOException e) {
                LOGGER.error("{} at loading guide pages.json, skip all guide resources", e.getClass().getSimpleName());
            }
        });
        for (var pageEntry : loadingPages) {
            var location = pageEntry.resourceLocation();
            var pageLocation = new ResourceLocation(location.getNamespace(), String.format("parcool_guide/%s/%s", langCode, location.getPath()));
            var pageResource = resourceManager.getResource(pageLocation);
            if (pageResource.isEmpty() && !langCode.equals("en_us")) {
                pageLocation = new ResourceLocation(location.getNamespace(), String.format("parcool_guide/en_us/%s", location.getPath()));
                pageResource = resourceManager.getResource(pageLocation);
            }
            ResourceLocation finalPageLocation = pageLocation;
            pageResource.ifPresent(resource -> {
                try (var reader = resource.openAsReader()) {
                    map.put(pageEntry, MarkdownParser.parse(reader));
                } catch (IOException e) {
                    LOGGER.error("{} at loading guide page [{}]", e.getClass().getSimpleName(), finalPageLocation);
                }
            });
        }
        return new GuideResource(groupsEntries.stream().toList(), map);
    }

    private void generatePageEntries(String generate, ArrayList<PageEntry> pageEntries) {
        if (generate.equals("actions")) {
            var registry = ParCool.getActionRegistry();
            for (var action : registry.getRegisteredActions().entrySet()) {
                pageEntries.add(new PageEntry(action.getValue().getTranslationKey(), getLocation(action.getValue())));
            }
        }
    }

    @Override
    protected void apply(@Nonnull GuideResource markdownResource, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profilerFiller) {
        this.resource = markdownResource;
    }
}
