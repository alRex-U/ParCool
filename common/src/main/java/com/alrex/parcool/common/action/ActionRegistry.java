package com.alrex.parcool.common.action;

import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.ActionGroup;
import com.alrex.parcool.common.BasicRegistry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

public class ActionRegistry extends BasicRegistry<String, ActionGroup> {
    private Map<ResourceLocation, ActionEntry<?>> registeredEntries = new TreeMap<>();
    @Nullable
    private List<ActionEntry<?>> processingOrder;

    public Map<String, ActionGroup> getRegisteredGroups() {
        return getRegistry();
    }

    public Map<ResourceLocation, ActionEntry<?>> getRegisteredActions() {
        return registeredEntries;
    }

    @Nullable
    public ActionEntry<?> get(ResourceLocation id) {
        var group = getRegisteredGroups().get(id.getNamespace());
        if (group == null) return null;
        for (var actionEntry : group.actions()) {
            if (actionEntry.id().equals(id)) return actionEntry;
        }
        return null;
    }

    public void register(ActionGroup group) {
        register(group.namespace(), group);
        for (var entry : group.actions()) {
            registeredEntries.put(entry.id(), entry);
        }
    }

    @Override
    public void freeze() {
        super.freeze();
        var list = new LinkedList<ActionEntry<?>>();
        var addedActions = new TreeSet<ActionEntry<?>>();
        Deque<ActionEntry<?>> queue = new LinkedList<>();
        for (var group : getRegisteredGroups().entrySet()) {
            queue.addAll(group.getValue().actions());
        }
        while (!queue.isEmpty()) {
            var action = queue.poll();
            if (addedActions.contains(action)) {
                continue;
            }
            if (action.option().beforeProcessedActions().isEmpty()) {
                list.add(action);
                addedActions.add(action);
            } else {
                boolean dependingActionsArePushed = true;
                for (var dependingActions : action.option().beforeProcessedActions()) {
                    if (!addedActions.contains(dependingActions)) {
                        dependingActionsArePushed = false;
                        queue.addFirst(dependingActions);
                    }
                }
                if (dependingActionsArePushed) {
                    list.add(action);
                    addedActions.add(action);
                } else {
                    queue.addLast(action);
                }
            }
        }
        processingOrder = Collections.unmodifiableList(list);
    }

    public List<ActionEntry<?>> getProcessingOrder() {
        if (!isFrozen() || processingOrder == null) {
            throw new IllegalStateException("This ActionRegistry is not frozen. You can not get processing order until action registration are finished");
        }

        return processingOrder;
    }
}
