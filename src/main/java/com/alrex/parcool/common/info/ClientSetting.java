package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.EnumMap;

public abstract class ClientSetting {

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

        @Override
        public IStamina.Type getStaminaType() {
            return IStamina.Type.Default;
        }
    }

    private static class Remote extends ClientSetting {
        private final boolean[] actionPossibilities = new boolean[ActionList.ACTIONS.size()];
        private final int[] staminaConsumptions = new int[ActionList.ACTIONS.size()];
        private final EnumMap<ParCoolConfig.Client.Booleans, Boolean> booleans = new EnumMap<>(ParCoolConfig.Client.Booleans.class);
        private final EnumMap<ParCoolConfig.Client.Integers, Integer> integers = new EnumMap<>(ParCoolConfig.Client.Integers.class);
        private final EnumMap<ParCoolConfig.Client.Doubles, Double> doubles = new EnumMap<>(ParCoolConfig.Client.Doubles.class);
        private IStamina.Type staminaType = IStamina.Type.Default;

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
            return actionPossibilities[ActionList.getIndexOf(action)];
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return staminaConsumptions[ActionList.getIndexOf(action)];
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

        @Override
        public IStamina.Type getStaminaType() {
            return staminaType;
        }
    }

    public static final ClientSetting UNSYNCED_INSTANCE = new Default();

    public abstract boolean getPossibilityOf(Class<? extends Action> action);

    public abstract int getStaminaConsumptionOf(Class<? extends Action> action);

    public abstract Boolean get(ParCoolConfig.Client.Booleans item);

    public abstract Integer get(ParCoolConfig.Client.Integers item);

    public abstract Double get(ParCoolConfig.Client.Doubles item);

    public abstract IStamina.Type getStaminaType();

    @OnlyIn(Dist.CLIENT)
    public static ClientSetting readFromLocalConfig() {
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; i++) {
            instance.actionPossibilities[i] = ParCoolConfig.Client.getPossibilityOf(ActionList.ACTIONS.get(i)).get();
            instance.staminaConsumptions[i] = ParCoolConfig.Client.getStaminaConsumptionOf(ActionList.ACTIONS.get(i)).get();
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
        instance.staminaType = ParCoolConfig.Client.StaminaType.get();
        return instance;
    }

    public void writeTo(ByteBuffer buffer) {
        for (Class<? extends Action> action : ActionList.ACTIONS) {
            buffer.put((byte) (getPossibilityOf(action) ? 1 : 0));
            buffer.putInt(getStaminaConsumptionOf(action));
        }
        for (ParCoolConfig.Client.Booleans item : ParCoolConfig.Client.Booleans.values()) {
            buffer.put((byte) (get(item) ? 1 : 0));
        }
        for (ParCoolConfig.Client.Integers item : ParCoolConfig.Client.Integers.values()) {
            buffer.putInt(get(item));
        }
        for (ParCoolConfig.Client.Doubles item : ParCoolConfig.Client.Doubles.values()) {
            buffer.putDouble(get(item));
        }
        buffer.putInt(getStaminaType().ordinal());
    }

    public static ClientSetting readFrom(ByteBuffer buffer) {
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; i++) {
            instance.actionPossibilities[i] = buffer.get() != 0;
            instance.staminaConsumptions[i] = buffer.getInt();
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
        instance.staminaType = IStamina.Type.values()[buffer.getInt()];
        return instance;
    }

}
