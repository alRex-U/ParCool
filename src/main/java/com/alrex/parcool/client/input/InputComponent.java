package com.alrex.parcool.client.input;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public abstract class InputComponent {
    protected final ParCoolKeyBinds.Input input;
    protected final int timeout;

    public int getTimeout() {
        return timeout;
    }

    public InputComponent(ParCoolKeyBinds.Input input, int timeout) {
        this.input = input;
        this.timeout = timeout;
    }

    protected abstract boolean tickAndCheckActive();

    public static InputComponent pressTwice(ParCoolKeyBinds.Input input, int timeout) {
        return new DoubleTapComponent(input, timeout);
    }

    private static class PredicateInputComponent extends InputComponent {
        private final Predicate<ParCoolKeyBinds.Input> condition;

        public PredicateInputComponent(ParCoolKeyBinds.Input input, int timeout, Predicate<ParCoolKeyBinds.Input> condition) {
            super(input, timeout);
            this.condition = condition;
        }

        @Override
        protected boolean tickAndCheckActive() {
            return condition.test(input);
        }
    }

    private static class DoubleTapComponent extends InputComponent {
        private byte tickFromPressed;

        public DoubleTapComponent(ParCoolKeyBinds.Input input, int timeout) {
            super(input, timeout);
        }

        @Override
        protected boolean tickAndCheckActive() {
            if (tickFromPressed < 127) tickFromPressed++;
            if (input.state().isJustPressed()) {
                if (tickFromPressed < 5) {
                    tickFromPressed = 0;
                    return true;
                }
                tickFromPressed = 0;
            }
            return false;
        }
    }
}
