package com.alrex.parcool.api.action;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

public record ActionGroup(String namespace, List<ActionEntry<? extends Action>> actions) {
    public static class Builder {
        private final String namespace;
        private final LinkedList<ActionEntry<? extends Action>> registered = new LinkedList<>();

        public String getNamespace() {
            return namespace;
        }

        public Builder(String namespace) {
            this.namespace = namespace;
        }

        public <T extends Action> ActionEntry<T> add(String name, Class<T> clazz, ActionEntry.ActionConstructor<T> factory) {
            var entry = new ActionEntry<>((short) registered.size(), new ResourceLocation(namespace, name), clazz, factory, new ActionOption());
            registered.addLast(entry);
            return entry;
        }

        public <T extends Action> ActionEntry<T> add(String name, Class<T> clazz, ActionEntry.ActionConstructor<T> factory, ActionOption option) {
            var entry = new ActionEntry<>((short) registered.size(), new ResourceLocation(namespace, name), clazz, factory, option);
            registered.addLast(entry);
            return entry;
        }

        public ActionGroup build() {
            return new ActionGroup(namespace, registered.stream().toList());
        }
    }
}
