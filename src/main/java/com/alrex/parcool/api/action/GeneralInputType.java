package com.alrex.parcool.api.action;

import com.alrex.parcool.client.input.ParCoolKeyBinds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum GeneralInputType {
    NONE, INSTANT, CONTINUOUS;

    public enum Instant {
        PRESS_KEY, RELEASE_KEY;

        public Input newInput() {
            return switch (this) {
                case PRESS_KEY -> new PressKeyInput();
                case RELEASE_KEY -> new ReleaseKeyInput();
            };
        }
    }

    public enum Continuation {
        DOWN_KEY, TOGGLE, ALWAYS;

        public Input newInput() {
            return switch (this) {
                case DOWN_KEY -> new DownKeyInput();
                case TOGGLE -> new ToggleKeyInput();
                default -> new AlwaysInput();
            };
        }
    }

    public interface Input {
        boolean isActive();

        @OnlyIn(Dist.CLIENT)
        default void tick(ParCoolKeyBinds.IStateProvider stateProvider) {
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static abstract class AbstractInputHandle implements Input {
        protected boolean current;

        @Override
        public boolean isActive() {
            return current;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class PressKeyInput extends AbstractInputHandle {
        @Override
        public void tick(ParCoolKeyBinds.IStateProvider stateProvider) {
            current = stateProvider.state().isJustPressed();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class ReleaseKeyInput extends AbstractInputHandle {
        @Override
        public void tick(ParCoolKeyBinds.IStateProvider stateProvider) {
            current = stateProvider.state().isJustReleased();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class DownKeyInput extends AbstractInputHandle {
        @Override
        public void tick(ParCoolKeyBinds.IStateProvider stateProvider) {
            current = stateProvider.state().isDown();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class ToggleKeyInput extends AbstractInputHandle {
        @Override
        public void tick(ParCoolKeyBinds.IStateProvider stateProvider) {
            if (stateProvider.state().isJustPressed()) current = !current;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class AlwaysInput implements Input {
        @Override
        public boolean isActive() {
            return true;
        }
    }
}
