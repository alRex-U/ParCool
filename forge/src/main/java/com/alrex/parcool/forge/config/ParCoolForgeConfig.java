package com.alrex.parcool.forge.config;

import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.stamina.HUDType;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import com.alrex.parcool.common.stamina.StaminaTypes;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.TreeMap;

public class ParCoolForgeConfig extends ParCoolConfig {
    public ParCoolForgeConfig(ActionRegistry actionRegistry, StaminaTypeRegistry staminaTypeRegistry) {
        super(new ForgeClient(), new ForgeServer(actionRegistry, staminaTypeRegistry));
    }

    private record ConfigAdaptor<T>(ForgeConfigSpec.ConfigValue<T> config) implements ConfigItem<T> {
        @Override
        public T get() {
            return config.get();
        }

        @Override
        public void set(T value) {
            config.set(value);
        }
    }

    public void register(ModLoadingContext loadingContext) {
        loadingContext.registerConfig(ModConfig.Type.CLIENT, ((ForgeClient) client()).builtConfig);
        loadingContext.registerConfig(ModConfig.Type.SERVER, ((ForgeServer) server()).builtConfig);
    }

    private static <T> ConfigItem<T> adapt(ForgeConfigSpec.ConfigValue<T> value) {
        return new ConfigAdaptor<>(value);
    }

    public static class ForgeClient implements Client {
        private final ForgeConfigSpec builtConfig;
        private final StaminaHud staminaHud;
        private final Sound sound;

        @Override
        public StaminaHud staminaHud() {
            return staminaHud;
        }

        @Override
        public Sound sound() {
            return sound;
        }

        public ForgeClient() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            builder.push("HUD");
            {
                staminaHud = new StaminaHud(
                        adapt(builder.defineEnum("stamina_hud_type", HUDType.Light)),
                        adapt(builder.comment("horizontal alignment").defineEnum("hud_align_h_s", Position.Horizontal.Right)),
                        adapt(builder.comment("vertical alignment").defineEnum("hud_align_v_s", Position.Vertical.Bottom)),
                        adapt(builder.define("show_always", false)),
                        adapt(builder.define("hide_automatically", true)),
                        adapt(builder.defineInRange("hud_offset_h", 0, -100, 100)),
                        adapt(builder.defineInRange("hud_offset_v", 0, -100, 100))
                );
            }
            builder.pop();
            builder.push("Sound");
            {
                sound = new Sound(adapt(builder.define("enable_sounds", true)));
            }
            builder.pop();
            builtConfig = builder.build();
        }
    }

    public static class ForgeServer implements Server {
        private final ForgeConfigSpec builtConfig;
        private final System system;
        private final TreeMap<String, TreeMap<ActionEntry<?>, ActionValue>> actionMap;

        @Override
        public ResourceLocation getStaminaTypeID() {
            var id = ResourceLocation.tryParse(staminaType.get());
            return id != null ? id : StaminaTypes.PARCOOL_STAMINA.id();
        }

        public final ForgeConfigSpec.ConfigValue<String> staminaType;

        public ForgeServer(ActionRegistry actionRegistry, StaminaTypeRegistry staminaTypeRegistry) {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            actionMap = new TreeMap<>();

            builder.push("Action");
            for (var group : actionRegistry.getRegisteredGroups().entrySet()) {
                builder.push(group.getKey());
                var inGroupMap = new TreeMap<ActionEntry<?>, ActionValue>();
                for (var action : group.getValue().actions()) {
                    builder.push(action.id().getPath());
                    inGroupMap.put(
                            action,
                            new ActionValue(
                                    adapt(builder.define("available", true)),
                                    adapt(builder.defineInRange("cost_start", action.option().defaultCost().onStart(), 0, Short.MAX_VALUE)),
                                    adapt(builder.defineInRange("cost_working", action.option().defaultCost().onWorking(), 0, Short.MAX_VALUE)),
                                    adapt(builder.defineInRange("cost_finish", action.option().defaultCost().onFinish(), 0, Short.MAX_VALUE)),
                                    adapt(builder.defineInRange("learn_cost", action.option().learningCost(), 0, Short.MAX_VALUE))
                            )
                    );
                    builder.pop();
                }
                actionMap.put(group.getKey(), inGroupMap);
                builder.pop();
            }
            builder.pop();

            builder.push("Game");
            {
                system = new System(
                        adapt(builder.define("damage_without_glove", true)),
                        adapt(builder.define("enable_skill_tree", true))
                );
            }
            builder.pop();
            builder.push("Stamina");
            {
                var registeredItems = staminaTypeRegistry.getEntries();
                var list = new String[registeredItems.size() + 1];
                list[0] = "Available Types :";
                int i = 0;
                for (var type : registeredItems) {
                    list[++i] = "- [" + type.id() + "]";
                }
                builder.comment(list);
                staminaType = builder.define("stamina_type", StaminaTypes.PARCOOL_STAMINA.id().toString());
            }
            builder.pop();
            builtConfig = builder.build();
        }

        @Override
        public ActionValue get(ActionEntry<?> entry) {
            return actionMap.get(entry.id().getNamespace()).get(entry);
        }

        @Override
        public System system() {
            return system;
        }
    }
}
