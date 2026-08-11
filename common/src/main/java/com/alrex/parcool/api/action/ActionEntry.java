package com.alrex.parcool.api.action;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ActionEntry<T extends Action> implements Comparable<ActionEntry<?>> {
    private final short index;
    private final ResourceLocation id;
    private final Class<T> clazz;
    private final ActionConstructor<T> factory;
    private final ActionOption.Value option;
    private final ArrayList<ActionEntry<? extends Action>> children = new ArrayList<>();
    private final String translationKey;

    public ActionEntry(
            short index,
            ResourceLocation id,
            Class<T> clazz,
            ActionConstructor<T> factory,
            ActionOption option
    ) {
        this.index = index;
        this.id = id;
        this.clazz = clazz;
        this.factory = factory;
        this.option = option.build();
        if (this.option.parent() != null) {
            this.option.parent().children.add(this);
        }
        this.translationKey = "parcool.action." + id.getNamespace() + "." + id.getPath();
    }

    public short index() {
        return index;
    }

    public ResourceLocation id() {
        return id;
    }

    @Nullable
    public ActionEntry<? extends ContinuableAction> parent() {
        return option.parent();
    }

    public Iterable<ActionEntry<? extends Action>> children() {
        return children;
    }

    public ActionOption.Value option() {
        return option;
    }

    public T createInstance(Parkourability parkourability) {
        return factory.construct(parkourability, this);
    }

    @Override
    public int compareTo(ActionEntry<?> o) {
        var strComp = this.id.getNamespace().compareTo(o.id.getNamespace());
        if (strComp != 0) return strComp;
        return Short.compare(this.index, o.index);
    }

    public interface ActionConstructor<T extends Action> {
        T construct(Parkourability parkourability, ActionEntry<T> entry);
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
