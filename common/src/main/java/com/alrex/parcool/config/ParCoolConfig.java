package com.alrex.parcool.config;

import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.stamina.HUDType;
import net.minecraft.resources.ResourceLocation;

public class ParCoolConfig {
    public interface ConfigItem<T> {
        T get();

        void set(T value);
    }

    public ParCoolConfig(Client client, Server server) {
        this.client = client;
        this.server = server;
    }

    private final Client client;
    private final Server server;

    public Client client() {
        return client;
    }

    public Server server() {
        return server;
    }

    public record ActionValue(
            ConfigItem<Boolean> permit,
            ConfigItem<Integer> costOnStart,
            ConfigItem<Integer> costOnWorking,
            ConfigItem<Integer> costOnFinish,
            ConfigItem<Integer> learningCost
    ) {
    }

    public interface Client {
        StaminaHud staminaHud();

        Sound sound();

        record StaminaHud(
                ConfigItem<HUDType> type,
                ConfigItem<Position.Horizontal> alignHorizontal,
                ConfigItem<Position.Vertical> alignVertical,
                ConfigItem<Boolean> showAlways,
                ConfigItem<Boolean> hideAutomatically,
                ConfigItem<Integer> offsetHorizontal,
                ConfigItem<Integer> offsetVertical
        ) {
        }

        record Sound(
                ConfigItem<Boolean> enableActionSounds
        ) {
        }
    }

    public interface Server {
        ActionValue get(ActionEntry<?> entry);

        System system();

        ResourceLocation getStaminaTypeID();

        public record System(
                ConfigItem<Boolean> damageWithoutGlove,
                ConfigItem<Boolean> enableSkillTree
        ) {
        }
    }
}
