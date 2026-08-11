package com.alrex.parcool.client.animation.system.registration;

public class IDProvider<T> {
    private int lastNumber = -1;

    public ID<T> newID() {
        return new ID(++lastNumber);
    }
}
