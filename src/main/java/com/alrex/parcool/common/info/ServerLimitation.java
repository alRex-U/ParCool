package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.limitation.Limitation;
import com.alrex.parcool.server.limitation.Limitations;
import net.minecraft.server.level.ServerPlayer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;

public abstract class ServerLimitation {
    private static class Default extends ServerLimitation {
        @Override
        public boolean isPermitted(Class<? extends Action> action) {
            return false;
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return Integer.MAX_VALUE;
        }

        @Override
        public Boolean get(ParCoolConfig.Server.Booleans item) {
            return !item.AdvantageousValue;
        }

        @Override
        public Integer get(ParCoolConfig.Server.Integers item) {
            return item.Advantageous == ParCoolConfig.AdvantageousDirection.Lower ? item.Max : item.Min;
        }

        @Override
        public Double get(ParCoolConfig.Server.Doubles item) {
            return item.Advantageous == ParCoolConfig.AdvantageousDirection.Lower ? item.Max : item.Min;
        }

        @Override
        public boolean isSynced() {
            return false;
        }
    }

    private static class Remote extends ServerLimitation {
        private final boolean[] actionPossibilities = new boolean[ActionList.ACTIONS.size()];
        private final int[] leastStaminaConsumptions = new int[ActionList.ACTIONS.size()];
        private final EnumMap<ParCoolConfig.Server.Booleans, Boolean> booleans = new EnumMap<>(ParCoolConfig.Server.Booleans.class);
        private final EnumMap<ParCoolConfig.Server.Integers, Integer> integers = new EnumMap<>(ParCoolConfig.Server.Integers.class);
        private final EnumMap<ParCoolConfig.Server.Doubles, Double> doubles = new EnumMap<>(ParCoolConfig.Server.Doubles.class);

        public Remote() {
            Arrays.fill(actionPossibilities, true);
            Arrays.fill(leastStaminaConsumptions, 0);
            for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
                booleans.put(item, item.AdvantageousValue);
            }
            for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
                integers.put(item, item.Advantageous == ParCoolConfig.AdvantageousDirection.Higher ? item.Max : item.Min);
            }
            for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
                doubles.put(item, item.Advantageous == ParCoolConfig.AdvantageousDirection.Higher ? item.Max : item.Min);
            }
        }

        @Override
        public boolean isPermitted(Class<? extends Action> action) {
            return actionPossibilities[ActionList.getIndexOf(action)];
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return leastStaminaConsumptions[ActionList.getIndexOf(action)];
        }

        @Override
        public Boolean get(ParCoolConfig.Server.Booleans item) {
            return booleans.get(item);
        }

        @Override
        public Integer get(ParCoolConfig.Server.Integers item) {
            return integers.get(item);
        }

        @Override
        public Double get(ParCoolConfig.Server.Doubles item) {
            return doubles.get(item);
        }

        @Override
        public boolean isSynced() {
            return true;
        }

        void apply(Limitation limitation) {
            for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
                if (this.actionPossibilities[i]) {
                    this.actionPossibilities[i] = limitation.isPermitted(ActionList.ACTIONS.get(i));
                }
                this.leastStaminaConsumptions[i] = Math.max(
                        this.leastStaminaConsumptions[i],
                        limitation.getLeastStaminaConsumption(ActionList.ACTIONS.get(i))
                );
            }
            for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
                if (this.booleans.get(item) == item.AdvantageousValue) {
                    this.booleans.put(item, limitation.get(item));
                }
            }
            for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
                this.integers.put(
                        item,
                        item.Advantageous == ParCoolConfig.AdvantageousDirection.Higher ?
                                Math.min(limitation.get(item), this.integers.get(item)) :
                                Math.max(limitation.get(item), this.integers.get(item))
                );
            }
            for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
                this.doubles.put(
                        item,
                        item.Advantageous == ParCoolConfig.AdvantageousDirection.Higher ?
                                Math.min(limitation.get(item), this.doubles.get(item)) :
                                Math.max(limitation.get(item), this.doubles.get(item))
                );
            }
        }
    }

    public static final ServerLimitation UNSYNCED_INSTANCE = new Default();

    public abstract boolean isPermitted(Class<? extends Action> action);

    public abstract int getStaminaConsumptionOf(Class<? extends Action> action);

    public abstract Boolean get(ParCoolConfig.Server.Booleans item);

    public abstract Integer get(ParCoolConfig.Server.Integers item);

    public abstract Double get(ParCoolConfig.Server.Doubles item);

    public abstract boolean isSynced();

    public static ServerLimitation get(ServerPlayer player) {
        Collection<Limitation> limitations = Limitations.getLimitationsOf(player.getUUID());

        Remote instance = new Remote();
        if (Limitations.getGlobalLimitation().isEnabled()) {
            instance.apply(Limitations.getGlobalLimitation());
        }
        for (Limitation limitation : limitations) {
            if (limitation.isEnabled()) {
                instance.apply(limitation);
            }
        }
        return instance;
    }

    public void writeTo(ByteBuffer buffer) {
        for (Class<? extends Action> action : ActionList.ACTIONS) {
            buffer.put((byte) (isPermitted(action) ? 1 : 0));
            buffer.putInt(getStaminaConsumptionOf(action));
        }
        for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
            buffer.put((byte) (get(item) ? 1 : 0));
        }
        for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
            buffer.putInt(get(item));
        }
        for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
            buffer.putDouble(get(item));
        }
    }

    public static ServerLimitation readFrom(ByteBuffer buffer) {
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; i++) {
            instance.actionPossibilities[i] = buffer.get() != 0;
            instance.leastStaminaConsumptions[i] = buffer.getInt();
        }
        for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
            instance.booleans.put(item, item.readFromBuffer(buffer));
        }
        for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
            instance.integers.put(item, item.readFromBuffer(buffer));
        }
        for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
            instance.doubles.put(item, item.readFromBuffer(buffer));
        }
        return instance;
    }
}
