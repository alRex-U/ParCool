package com.alrex.parcool.api.unstable;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.limitation.Limitations;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public abstract class Limitation {
    public static Limitation get(ServerPlayer player, ID limitationID) {
        return new NormalLimitation(player, Limitations.createLimitationOf(player.getUUID(), limitationID.convert()));
    }

    public static Limitation getIndividual(ServerPlayer player) {
        return new NormalLimitation(player, Limitations.createLimitationOf(player.getUUID(), Limitations.INDIVIDUAL_ID));
    }

    public static Limitation getGlobal(MinecraftServer server) {
        return new GlobalLimitation(server);
    }

    public static class ID {
        public final String group;
        public final String name;

        public ID(String group, String name) {
            this.group = group;
            this.name = name;
        }

        public String getGroup() {
            return group;
        }

        public String getName() {
            return name;
        }

        com.alrex.parcool.server.limitation.Limitation.ID convert() {
            return new com.alrex.parcool.server.limitation.Limitation.ID(group, name);
        }

        @Override
        public String toString() {
            return this.group + ":" + this.name;
        }
    }

    public Limitation set(ParCoolConfig.Server.Booleans item, boolean value) {
        instance.set(item, value);
        return this;
    }

    public Limitation set(ParCoolConfig.Server.Integers item, int value) {
        instance.set(item, value);
        return this;
    }

    public Limitation set(ParCoolConfig.Server.Doubles item, double value) {
        instance.set(item, value);
        return this;
    }

    public Limitation setDefault() {
        instance.setAllDefault();
        return this;
    }

    public Limitation permit(Class<? extends Action> action, boolean permission) {
        instance.setPossibilityOf(action, permission);
        return this;
    }

    public Limitation setLeastStaminaConsumption(Class<? extends Action> action, int value) {
        instance.setLeastStaminaConsumption(action, value);
        return this;
    }

    public Limitation enable() {
        instance.setEnabled(true);
        return this;
    }

    public Limitation disable() {
        instance.setEnabled(false);
        return this;
    }

    public boolean get(ParCoolConfig.Server.Booleans item) {
        return instance.get(item);
    }

    public int get(ParCoolConfig.Server.Integers item) {
        return instance.get(item);
    }

    public double get(ParCoolConfig.Server.Doubles item) {
        return instance.get(item);
    }

    public boolean isPermitted(Class<? extends Action> action) {
        return instance.isPermitted(action);
    }

    public int getLeastStaminaConsumption(Class<? extends Action> action) {
        return instance.getLeastStaminaConsumption(action);
    }

    public boolean isEnabled() {
        return instance.isEnabled();
    }

    public abstract void apply();

    public static boolean delete(ID limitationID) {
        return Limitations.delete(limitationID.convert());
    }

    private static class NormalLimitation extends Limitation {

        private NormalLimitation(ServerPlayer player, com.alrex.parcool.server.limitation.Limitation instance) {
            super(instance);
            this.player = player;
        }

        private final ServerPlayer player;

        @Override
        public void apply() {
            Limitations.update(player);
        }
    }

    private static class GlobalLimitation extends Limitation {
        private final MinecraftServer server;

        private GlobalLimitation(MinecraftServer server) {
            super(Limitations.getGlobalLimitation());
            this.server = server;
        }

        @Override
        public void apply() {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                Limitations.update(player);
            }
        }
    }

    private final com.alrex.parcool.server.limitation.Limitation instance;

    private Limitation(com.alrex.parcool.server.limitation.Limitation instance) {
        this.instance = instance;
    }
}
