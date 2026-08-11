package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public abstract class BasicRegistry<T, E> {
    private final IDProvider<T> idProvider = new IDProvider<>();
    private final TreeMap<ID<T>, E> registry = new TreeMap<>();
    private final TreeMap<ResourceLocation, ID<T>> nameToId = new TreeMap<>();
    private boolean frozen = false;

    public Map<ID<T>, E> getRegistry() {
        return registry;
    }

    public ID<T> registerItem(String subName, Function<ID<T>, E> entryFunc) {
        var name = ParCool.resourceLocation(subName);
        return registerItem(name, entryFunc);
    }

    public ID<T> registerItem(ResourceLocation name, Function<ID<T>, E> entryFunc) {
        var id = idProvider.newID();
        register(name, id, entryFunc.apply(id));
        return id;
    }

    public ID<T> registerItem(String subName, E entry) {
        var name = ParCool.resourceLocation(subName);
        return registerItem(name, entry);
    }

    public ID<T> registerItem(ResourceLocation name, E entry) {
        var id = idProvider.newID();
        register(name, id, entry);
        return id;
    }

    private void register(ResourceLocation name, ID<T> id, E entry) {
        if (frozen) {
            throw new IllegalStateException("Registering item to frozen registry");
        }
        registry.put(id, entry);
        nameToId.put(name, id);
    }

    @Nullable
    public ID<T> getID(ResourceLocation name) {
        return nameToId.get(name);
    }

    @Nullable
    public E get(ResourceLocation name) {
        var id = getID(name);
        if (id == null) return null;
        return registry.get(id);
    }

    public E get(ID<T> id) {
        return registry.get(id);
    }

    public void freeze() {
        frozen = true;
    }

    public boolean isFrozen() {
        return frozen;
    }
}
