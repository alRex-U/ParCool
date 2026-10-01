package com.alrex.parcool.common.skilltree;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.action.ActionCapabilities;
import com.alrex.parcool.common.action.ActionRegistry;
import com.google.gson.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SkillTree {
    private final Entry<?> root;

    public SkillTree(Entry<?> root) {
        this.root = root;
    }

    public Entry<?> getRoot() {
        return root;
    }

    public static class Entry<T extends Action> {
        private final ActionEntry<T> actionEntry;
        private final List<Entry<?>> children;
        private final List<ActionEntry<?>> dependingActions;
        @Nullable
        private Entry<?> parent;

        public Entry(ActionEntry<T> actionEntry) {
            this(actionEntry, Collections.emptyList(), Collections.emptyList());
        }

        public Entry(ActionEntry<T> actionEntry, List<Entry<?>> children) {
            this(actionEntry, Collections.emptyList(), children);
        }

        public Entry(ActionEntry<T> actionEntry, Entry<?>... children) {
            this(actionEntry, Collections.emptyList(), Arrays.stream(children).toList());
        }

        public Entry(ActionEntry<T> actionEntry, List<ActionEntry<?>> dependingActions, Entry<?>... children) {
            this(actionEntry, dependingActions, Arrays.stream(children).toList());
        }

        public Entry(ActionEntry<T> actionEntry, List<ActionEntry<?>> dependingActions, List<Entry<?>> children) {
            this.actionEntry = actionEntry;
            this.children = children;
            this.dependingActions = dependingActions;
            for (var child : children) {
                if (child.parent != null)
                    throw new IllegalStateException("Single skill entry cannot have multiple parents");
                child.parent = this;
            }
        }

        @Nullable
        public Entry<?> getParent() {
            return parent;
        }

        public List<Entry<?>> getChildren() {
            return children;
        }

        public ActionEntry<T> getActionEntry() {
            return actionEntry;
        }

        public boolean checkDependenciesUnlocked(ActionCapabilities capabilities) {
            if (parent != null && !parent.isUnlocked(capabilities)) return false;
            for (var action : dependingActions) {
                if (!capabilities.can(action)) return false;
            }
            return true;
        }

        public boolean isVisible(ActionCapabilities capabilities) {
            return parent == null || parent.isUnlocked(capabilities) || this.isUnlocked(capabilities);
        }

        public boolean isUnlocked(ActionCapabilities capabilities) {
            return !ParCool.getConfig().server().enableSkillTree.get()
                    || !actionEntry.option().needLearning()
                    || capabilities.can(actionEntry);
        }

        public boolean isEnabled(ActionCapabilities capabilities) {
            return capabilities.can(actionEntry);
        }

        public int getLearningCost() {
            return ParCool.getConfig().server().get(this.actionEntry).learningCost().get();
        }

        private void saveTo(ByteBuf buf) {
            var name = actionEntry.id().toString();
            buf.writeByte(name.length());
            buf.writeCharSequence(name, StandardCharsets.US_ASCII);
            buf.writeByte(children.size());
            for (var child : children) {
                child.saveTo(buf);
            }
        }

        private static Entry<?> readFrom(ActionRegistry registry, ByteBuf buf) {
            var nameLen = buf.readByte();
            var name = ResourceLocation.parse(buf.readCharSequence(nameLen, StandardCharsets.UTF_8).toString());
            var childCount = buf.readByte();
            var children = new ArrayList<Entry<?>>(childCount);
            for (var i = 0; i < childCount; i++) {
                children.add(readFrom(registry, buf));
            }
            return new Entry<>(registry.get(name), children);
        }
    }

    public void saveTo(ByteBuf buf) {
        getRoot().saveTo(buf);
    }

    public static SkillTree readFrom(ActionRegistry registry, ByteBuf buf) {
        return new SkillTree(Entry.readFrom(registry, buf));
    }

    public static SkillTree fromJson(ActionRegistry actionRegistry, JsonElement element) throws JsonSyntaxException {
        return new SkillTree(fromJson$parseElement(actionRegistry, element));
    }

    private static Entry<?> fromJson$parseElement(ActionRegistry actionRegistry, JsonElement element) throws JsonSyntaxException {
        if (element instanceof JsonObject object) {
            if (!(object.get("action") instanceof JsonPrimitive actionElement && actionElement.isString()))
                throw new JsonSyntaxException("action name is not string");
            var actionName = ResourceLocation.tryParse(actionElement.getAsString());
            if (actionName == null)
                throw new JsonSyntaxException("name[" + actionElement.getAsString() + "] is not valid ResourceLocation");
            var action = actionRegistry.get(actionName);
            if (action == null) throw new JsonSyntaxException("action[" + actionName + "] is not registered");
            if (object.get("children") instanceof JsonArray children) {
                var arrayList = new ArrayList<Entry<?>>();
                for (var child : children) {
                    arrayList.add(fromJson$parseElement(actionRegistry, child));
                }
                arrayList.trimToSize();
                return new Entry<>(action, Collections.unmodifiableList(arrayList));
            }
            return new Entry<>(action);
        } else if (element instanceof JsonPrimitive primitive && primitive.isString()) {
            var actionName = ResourceLocation.tryParse(primitive.getAsString());
            if (actionName == null)
                throw new JsonSyntaxException("name[" + primitive.getAsString() + "] is not valid ResourceLocation");
            var action = actionRegistry.get(actionName);
            if (action == null) throw new JsonSyntaxException("action[" + actionName + "] is not registered");
            return new Entry<>(action);
        }
        throw new JsonSyntaxException("element is neither string nor object");
    }
}
