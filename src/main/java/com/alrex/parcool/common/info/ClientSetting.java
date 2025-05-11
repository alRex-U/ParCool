package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.EnumMap;

public abstract class ClientSetting {
    public static final StreamCodec<ByteBuf, ClientSetting> STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> value.writeTo(buffer),
            ClientSetting::readFrom
    );

    private static class Default extends ClientSetting {
        @Override
        public boolean getPossibilityOf(Class<? extends Action> action) {
            return false;
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return Integer.MAX_VALUE;
        }

        @Override
        public StaminaType getRequestedStamina() {
            return StaminaType.NONE;
        }

        @Override
        public Boolean get(ParCoolConfig.Client.Booleans item) {
            return item.DefaultValue;
        }

        @Override
        public Integer get(ParCoolConfig.Client.Integers item) {
            return item.DefaultValue;
        }

        @Override
        public Double get(ParCoolConfig.Client.Doubles item) {
            return item.DefaultValue;
        }
    }

    private static class Remote extends ClientSetting {
        private final boolean[] actionPossibilities = new boolean[Actions.LIST.size()];
        private final int[] staminaConsumptions = new int[Actions.LIST.size()];
        private final EnumMap<ParCoolConfig.Client.Booleans, Boolean> booleans = new EnumMap<>(ParCoolConfig.Client.Booleans.class);
        private final EnumMap<ParCoolConfig.Client.Integers, Integer> integers = new EnumMap<>(ParCoolConfig.Client.Integers.class);
        private final EnumMap<ParCoolConfig.Client.Doubles, Double> doubles = new EnumMap<>(ParCoolConfig.Client.Doubles.class);
        private StaminaType requestedStamina = null;

        public Remote() {
            Arrays.fill(actionPossibilities, true);
            Arrays.fill(staminaConsumptions, 0);
            for (ParCoolConfig.Client.Booleans item : ParCoolConfig.Client.Booleans.values()) {
                booleans.put(item, item.DefaultValue);
            }
            for (ParCoolConfig.Client.Integers item : ParCoolConfig.Client.Integers.values()) {
                integers.put(item, item.DefaultValue);
            }
            for (ParCoolConfig.Client.Doubles item : ParCoolConfig.Client.Doubles.values()) {
                doubles.put(item, item.DefaultValue);
            }
        }

        @Override
        public boolean getPossibilityOf(Class<? extends Action> action) {
            return actionPossibilities[Actions.getIndexOf(action)];
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return staminaConsumptions[Actions.getIndexOf(action)];
        }

        @Override
        public StaminaType getRequestedStamina() {
            return requestedStamina;
        }

        @Override
        public Boolean get(ParCoolConfig.Client.Booleans item) {
            return booleans.get(item);
        }

        @Override
        public Integer get(ParCoolConfig.Client.Integers item) {
            return integers.get(item);
        }

        @Override
        public Double get(ParCoolConfig.Client.Doubles item) {
            return doubles.get(item);
        }

    }

    public static final ClientSetting UNSYNCED_INSTANCE = new Default();

    public abstract boolean getPossibilityOf(Class<? extends Action> action);

    public abstract int getStaminaConsumptionOf(Class<? extends Action> action);

    public abstract StaminaType getRequestedStamina();

    public abstract Boolean get(ParCoolConfig.Client.Booleans item);

    public abstract Integer get(ParCoolConfig.Client.Integers item);

    public abstract Double get(ParCoolConfig.Client.Doubles item);

    @OnlyIn(Dist.CLIENT)
    public static ClientSetting readFromLocalConfig() {
        var configInstance = ParCoolConfig.Client.getInstance();
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; i++) {
            instance.actionPossibilities[i] = instance.getPossibilityOf(Actions.LIST.get(i));
            instance.staminaConsumptions[i] = instance.getStaminaConsumptionOf(Actions.LIST.get(i));
        }
        for (ParCoolConfig.Client.Booleans item : ParCoolConfig.Client.Booleans.values()) {
            instance.booleans.put(item, item.get());
        }
        for (ParCoolConfig.Client.Integers item : ParCoolConfig.Client.Integers.values()) {
            instance.integers.put(item, item.get());
        }
        for (ParCoolConfig.Client.Doubles item : ParCoolConfig.Client.Doubles.values()) {
            instance.doubles.put(item, item.get());
        }
        instance.requestedStamina = configInstance.StaminaType.get();
        return instance;
    }

    public void writeTo(ByteBuf buffer) {
        for (Class<? extends Action> action : Actions.LIST) {
            buffer.writeByte((byte) (getPossibilityOf(action) ? 1 : 0));
            buffer.writeInt(getStaminaConsumptionOf(action));
        }
        for (ParCoolConfig.Client.Booleans item : ParCoolConfig.Client.Booleans.values()) {
            buffer.writeByte((byte) (get(item) ? 1 : 0));
        }
        for (ParCoolConfig.Client.Integers item : ParCoolConfig.Client.Integers.values()) {
            buffer.writeInt(get(item));
        }
        for (ParCoolConfig.Client.Doubles item : ParCoolConfig.Client.Doubles.values()) {
            buffer.writeDouble(get(item));
        }
        buffer.writeByte(getRequestedStamina().ordinal());
    }

    public static ClientSetting readFrom(ByteBuf buffer) {
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; i++) {
            instance.actionPossibilities[i] = buffer.readByte() != 0;
            instance.staminaConsumptions[i] = buffer.readInt();
        }
        for (ParCoolConfig.Client.Booleans item : ParCoolConfig.Client.Booleans.values()) {
            instance.booleans.put(item, item.readFromBuffer(buffer));
        }
        for (ParCoolConfig.Client.Integers item : ParCoolConfig.Client.Integers.values()) {
            instance.integers.put(item, item.readFromBuffer(buffer));
        }
        for (ParCoolConfig.Client.Doubles item : ParCoolConfig.Client.Doubles.values()) {
            instance.doubles.put(item, item.readFromBuffer(buffer));
        }
        instance.requestedStamina = StaminaType.values()[buffer.readByte()];
        return instance;
    }

}
