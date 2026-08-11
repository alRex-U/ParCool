package com.alrex.parcool.common.action;

import net.minecraft.core.BlockPos;

import java.util.List;

public interface ActionExtension {
    interface JumpListener extends ActionExtension {
        void onJump();
    }

    interface LandListener extends ActionExtension {
        void onLand(LivingFallEvent event);
    }

    interface AttackedListener extends ActionExtension {
        void onAttacked(LivingAttackEvent event);
    }

    interface VisibilityListener extends ActionExtension {
        void onUpdateVisibility(LivingEvent.LivingVisibilityEvent event);
    }

    interface LeaveFromWallListener extends ActionExtension {
        void onLeaveFromWall();
    }

    interface BlockChangedInClientListener extends ActionExtension {
        void onChangeBlock(BlockPos pos);
    }

    public static final List<Class<? extends ActionExtension>> EXTENSIONS = List.of(
            VisibilityListener.class,
            LandListener.class,
            JumpListener.class,
            AttackedListener.class,
            LeaveFromWallListener.class,
            BlockChangedInClientListener.class
    );

    final class Handler<T extends ActionExtension> {
        private final List<T> listeners;
        private final Class<T> listenerClass;

        public Handler(Class<T> listenerClass, List<T> listeners) {
            this.listenerClass = listenerClass;
            this.listeners = listeners.stream().toList();
        }

        public boolean match(Class<?> clazz) {
            return clazz == listenerClass;
        }

        public List<T> getListeners() {
            return listeners;
        }
    }
}
