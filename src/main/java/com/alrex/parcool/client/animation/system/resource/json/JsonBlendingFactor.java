package com.alrex.parcool.client.animation.system.resource.json;

import com.alrex.parcool.client.animation.system.BlendMethod;
import com.alrex.parcool.client.animation.system.resource.Argument;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class JsonBlendingFactor {
    private ResourceLocation name;
    @Nullable
    private Argument args;
    @Nullable
    private BlendMethod method;

    public BlendMethod getBlendMethod() {
        if (method == null) return BlendMethod.ADD;
        return method;
    }

    public Argument getArgs() {
        if (args == null) return Argument.EMPTY;
        return args;
    }

    public ResourceLocation getName() {
        return name;
    }
}
