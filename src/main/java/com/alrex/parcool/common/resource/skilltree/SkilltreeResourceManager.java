package com.alrex.parcool.common.resource.skilltree;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.skilltree.SkillTree;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SkilltreeResourceManager extends SimpleJsonResourceReloadListener {
    @Nullable
    private static SkilltreeResourceManager INSTANCE = null;

    public static SkilltreeResourceManager getInstance() {
        if (INSTANCE == null) INSTANCE = new SkilltreeResourceManager(ParCool.getActionRegistry());
        return INSTANCE;
    }

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = (new GsonBuilder()).create();
    private final ActionRegistry actionRegistry;
    private List<SkillTree> skillTrees;

    public SkilltreeResourceManager(ActionRegistry actionRegistry) {
        super(GSON, "parcool/skilltree");
        this.actionRegistry = actionRegistry;
    }

    @Override
    protected void apply(@Nonnull Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, @Nonnull ResourceManager manager, @Nonnull ProfilerFiller profilerFiller) {
        var arrayList = new ArrayList<SkillTree>();
        resourceLocationJsonElementMap.forEach((key, json) -> {
            try {
                if (json instanceof JsonObject jsonObject)
                    arrayList.add(SkillTree.fromJson(actionRegistry, jsonObject));
                else throw new JsonSyntaxException("root element is not object");
            } catch (JsonSyntaxException e) {
                LOGGER.error("Parsing error loading parcool skilltree {}: {}", key, e.getMessage());
            }
        });
        arrayList.trimToSize();
        skillTrees = Collections.unmodifiableList(arrayList);
    }

    public List<SkillTree> getSkillTrees() {
        return skillTrees;
    }

    public static void register(AddReloadListenerEvent event) {
        event.addListener(SkilltreeResourceManager.getInstance());
    }
}
