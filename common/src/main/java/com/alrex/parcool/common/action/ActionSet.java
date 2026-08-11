package com.alrex.parcool.common.action;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.util.Tuple;

import java.util.*;

public class ActionSet implements Iterable<Action> {
    private final TreeMap<String, List<Action>> actions = new TreeMap<>();
    private final List<Action> iterationList;
    private final List<? extends ActionExtension.Handler<? extends ActionExtension>> extHandlers;

    public ActionSet(Parkourability parkourability, ActionRegistry registry) {
        for (var group : registry.getRegisteredGroups().entrySet()) {
            actions.put(group.getKey(), group.getValue().actions().stream().map(it -> (Action) it.createInstance(parkourability)).toList());
        }
        var extListenerLists = new ArrayList<Tuple<Class<? extends ActionExtension>, LinkedList<ActionExtension>>>();
        for (var extType : ActionExtension.EXTENSIONS) {
            extListenerLists.add(new Tuple<>(extType, new LinkedList<>()));
        }
        var listForIteration = new ArrayList<Action>();
        for (var entry : registry.getProcessingOrder()) {
            var action = get(entry);
            listForIteration.add(action);

            for (var extListenerList : extListenerLists) {
                if (extListenerList.getA().isAssignableFrom(action.getClass())) {
                    extListenerList.getB().add((ActionExtension) action);
                }
            }
        }
        extHandlers = extListenerLists.stream().map(it -> createHandler(it.getA(), it.getB())).toList();
        iterationList = Collections.unmodifiableList(listForIteration);
    }

    private <T extends ActionExtension> ActionExtension.Handler<T> createHandler(Class<T> clazz, LinkedList<?> list) {
        return new ActionExtension.Handler<>(clazz, list.stream().map(clazz::cast).toList());
    }

    public <T extends Action> T get(ActionEntry<T> entry) {
        return (T) actions.get(entry.id().getNamespace()).get(entry.index());
    }

    public <T extends ActionExtension> Iterable<? extends T> getExtensionListeners(Class<T> clazz) {
        for (var extHandler : extHandlers) {
            if (extHandler.match(clazz)) {
                return (List<? extends T>) extHandler.getListeners();
            }
        }
        throw new IllegalStateException(String.format("Extension class [%s] is not registered", clazz));
    }


    @Override
    public Iterator<Action> iterator() {
        return iterationList.iterator();
    }
}
