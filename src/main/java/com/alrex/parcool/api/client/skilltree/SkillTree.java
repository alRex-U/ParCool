package com.alrex.parcool.api.client.skilltree;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.action.ActionCapabilities;

import javax.annotation.Nullable;
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
            return capabilities.can(actionEntry);
        }
    }
}
