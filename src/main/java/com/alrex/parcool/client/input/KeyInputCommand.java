package com.alrex.parcool.client.input;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class KeyInputCommand {
    private boolean active = false;
    private byte currentProgress = 0;
    private int tickInCurrentProgress = 0;
    private final List<InputComponent> components;

    private KeyInputCommand(List<InputComponent> components) {
        this.components = components;
    }

    public void reset() {
        currentProgress = 0;
        tickInCurrentProgress = 0;
    }

    public void tick() {
        active = false;
        if (currentProgress >= components.size()) currentProgress = 0;
        var comp = components.get(currentProgress);
        if (comp.tickAndCheckActive()) {
            if (currentProgress < components.size() - 1) {
                active = true;
            } else {
                currentProgress++;
            }
        }
        tickInCurrentProgress++;
        if (currentProgress > 0 && tickInCurrentProgress >= comp.getTimeout()) {
            reset();
        }
    }

    public boolean isActive() {
        return active;
    }

    public static class Builder {
        private final LinkedList<InputComponent> compList = new LinkedList<>();

        private static Builder start(InputComponent component) {
            var builder = new Builder();
            builder.compList.add(component);
            return builder;
        }

        public Builder andThen(InputComponent component) {
            compList.add(component);
            return this;
        }

        public KeyInputCommand build() {
            return new KeyInputCommand(compList.stream().toList());
        }
    }
}
