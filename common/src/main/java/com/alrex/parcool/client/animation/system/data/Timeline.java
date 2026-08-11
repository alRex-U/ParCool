package com.alrex.parcool.client.animation.system.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.TreeMap;

@Environment(EnvType.CLIENT)
public class Timeline {
    private final TreeMap<Float, Transition> transitions = new TreeMap<>();

    public Timeline(Iterable<Transition> transitions) {
        for (var transition : transitions) {
            this.transitions.put(transition.getStart().time(), transition);
        }
    }

    public float getValue(float tick) {
        Float tickF = tick;
        var current = transitions.floorEntry(tickF);
        if (current == null) {
            return transitions.firstEntry().getValue().getStart().value();
        }
        var next = transitions.higherEntry(tickF);

        return current.getValue().getValueAt(tick, next == null ? null : next.getValue());
    }
}
