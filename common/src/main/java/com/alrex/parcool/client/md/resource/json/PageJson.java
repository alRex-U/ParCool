package com.alrex.parcool.client.md.resource.json;

import com.alrex.parcool.ParCool;
import net.minecraft.resources.ResourceLocation;

public class PageJson {
    private static final ResourceLocation DEFAULT = ParCool.resourceLocation("not_found");
    public String title = "parcool.guide.page.not_found";
    public ResourceLocation location = DEFAULT;
}
