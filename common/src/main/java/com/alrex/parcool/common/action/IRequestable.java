package com.alrex.parcool.common.action;

public interface IRequestable<Context> {
    default boolean requestCanStart(Object context) {
        if (context == null) return false;
        return canStart((Context) context);
    }

    boolean canStart(Context context);
}
