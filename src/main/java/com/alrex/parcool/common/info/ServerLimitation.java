package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.limitation.Limitation;
import com.alrex.parcool.server.limitation.Limitations;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;

public abstract class ServerLimitation {
    public static final StreamCodec<ByteBuf, ServerLimitation> STREAM_CODEC = StreamCodec.of(
            (buf, limitation) -> limitation.writeTo(buf),
            ServerLimitation::readFrom
    );
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
        public StaminaType getForcedStamina() {
            return StaminaType.NONE;
        }

        @Override
        public boolean isSynced() {
            return false;
        }
    }

    private static class Remote extends ServerLimitation {
        private final boolean[] actionPossibilities = new boolean[Actions.LIST.size()];
        private final int[] leastStaminaConsumptions = new int[Actions.LIST.size()];
        private final EnumMap<ParCoolConfig.Server.Booleans, Boolean> booleans = new EnumMap<>(ParCoolConfig.Server.Booleans.class);
        private final EnumMap<ParCoolConfig.Server.Integers, Integer> integers = new EnumMap<>(ParCoolConfig.Server.Integers.class);
        private final EnumMap<ParCoolConfig.Server.Doubles, Double> doubles = new EnumMap<>(ParCoolConfig.Server.Doubles.class);
        private StaminaType forcedStamina = StaminaType.NONE;

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
            return actionPossibilities[Actions.getIndexOf(action)];
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return leastStaminaConsumptions[Actions.getIndexOf(action)];
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
        public StaminaType getForcedStamina() {
            return forcedStamina;
        }

        @Override
        public boolean isSynced() {
            return true;
        }

        void apply(Limitation limitation) {
            for (int i = 0; i < Actions.LIST.size(); i++) {
                if (this.actionPossibilities[i]) {
                    this.actionPossibilities[i] = limitation.isPermitted(Actions.LIST.get(i));
                }
                this.leastStaminaConsumptions[i] = Math.max(
                        this.leastStaminaConsumptions[i],
                        limitation.getLeastStaminaConsumption(Actions.LIST.get(i))
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
            forcedStamina = limitation.getForcedStamina();
        }
    }

    public static final ServerLimitation UNSYNCED_INSTANCE = new Default();

    public abstract boolean isPermitted(Class<? extends Action> action);

    public abstract int getStaminaConsumptionOf(Class<? extends Action> action);

    public abstract Boolean get(ParCoolConfig.Server.Booleans item);

    public abstract Integer get(ParCoolConfig.Server.Integers item);

    public abstract Double get(ParCoolConfig.Server.Doubles item);

    public abstract StaminaType getForcedStamina();

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

    public void writeTo(ByteBuf buffer) {
        for (Class<? extends Action> action : Actions.LIST) {
            buffer.writeByte((byte) (isPermitted(action) ? 1 : 0));
            buffer.writeInt(getStaminaConsumptionOf(action));
        }
        for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
            buffer.writeByte((byte) (get(item) ? 1 : 0));
        }
        for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
            buffer.writeInt(get(item));
        }
        for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
            buffer.writeDouble(get(item));
        }
        buffer.writeByte(getForcedStamina().ordinal());
    }

    public static ServerLimitation readFrom(ByteBuf buffer) {
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; i++) {
            instance.actionPossibilities[i] = buffer.readByte() != 0;
            instance.leastStaminaConsumptions[i] = buffer.readInt();
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
        instance.forcedStamina = StaminaType.values()[buffer.readByte()];
        return instance;
    }
}
