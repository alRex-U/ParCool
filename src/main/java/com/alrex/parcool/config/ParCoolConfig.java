package com.alrex.parcool.config;

import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.stamina.HUDType;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import com.alrex.parcool.common.stamina.StaminaTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.TreeMap;

public class ParCoolConfig {
	public ParCoolConfig(ActionRegistry actionRegistry, StaminaTypeRegistry staminaTypeRegistry) {
		this.client = new Client();
		this.server = new Server(actionRegistry, staminaTypeRegistry);
	}

    public void register(ModContainer loadingContext) {
		loadingContext.registerConfig(ModConfig.Type.CLIENT, client.builtConfig);
		loadingContext.registerConfig(ModConfig.Type.SERVER, server.builtConfig);
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
            ModConfigSpec.BooleanValue permit,
            ModConfigSpec.IntValue costOnStart,
            ModConfigSpec.IntValue costOnWorking,
            ModConfigSpec.IntValue costOnFinish,
            ModConfigSpec.IntValue learningCost
	) {
	}

	public static class Client {
        private final ModConfigSpec builtConfig;

		public record StaminaHud(
                ModConfigSpec.EnumValue<HUDType> type,
                ModConfigSpec.EnumValue<Position.Horizontal> alignHorizontal,
                ModConfigSpec.EnumValue<Position.Vertical> alignVertical,
                ModConfigSpec.BooleanValue showAlways,
                ModConfigSpec.BooleanValue hideAutomatically,
                ModConfigSpec.IntValue offsetHorizontal,
                ModConfigSpec.IntValue offsetVertical
		) {
		}

        public final ModConfigSpec.BooleanValue enableActionSounds;
        public final ModConfigSpec.BooleanValue parcoolIsActive;
		public final StaminaHud staminaHud;


		public Client() {
            var builder = new ModConfigSpec.Builder();
			builder.push("HUD");
			{
				staminaHud = new StaminaHud(
						builder.defineEnum("stamina_hud_type", HUDType.Light),
						builder.comment("horizontal alignment").defineEnum("hud_align_h_s", Position.Horizontal.Right),
						builder.comment("vertical alignment").defineEnum("hud_align_v_s", Position.Vertical.Bottom),
						builder.define("show_always", false),
						builder.define("hide_automatically", true),
						builder.defineInRange("hud_offset_h", 0, -100, 100),
						builder.defineInRange("hud_offset_v", 0, -100, 100)
				);
			}
			builder.pop();
			builder.push("Other");
			{
				enableActionSounds = builder.define("enable_sounds", true);
				parcoolIsActive = builder.define("parcool_is_active", true);
			}
			builder.pop();
			builtConfig = builder.build();
		}
	}

	public static class Server {
        private final ModConfigSpec builtConfig;
		private final TreeMap<String, TreeMap<ActionEntry<?>, ActionValue>> actionMap;
        public final ModConfigSpec.BooleanValue damageWithoutGlove;
        public final ModConfigSpec.BooleanValue enableSkillTree;

		public final ResourceLocation getStaminaTypeID() {
			var id = ResourceLocation.tryParse(staminaType.get());
			return id != null ? id : StaminaTypes.PARCOOL_STAMINA.id();
		}

        public final ModConfigSpec.ConfigValue<String> staminaType;

		public ActionValue get(ActionEntry<?> entry) {
			return actionMap.get(entry.id().getNamespace()).get(entry);
		}

		public Server(ActionRegistry actionRegistry, StaminaTypeRegistry staminaTypeRegistry) {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

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
									builder.define("available", true),
									builder.defineInRange("cost_start", action.option().defaultCost().onStart(), 0, Short.MAX_VALUE),
									builder.defineInRange("cost_working", action.option().defaultCost().onWorking(), 0, Short.MAX_VALUE),
									builder.defineInRange("cost_finish", action.option().defaultCost().onFinish(), 0, Short.MAX_VALUE),
									builder.defineInRange("learn_cost", action.option().learningCost(), 0, Short.MAX_VALUE)
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
                enableSkillTree = builder.define("enable_skill_tree", true);
                damageWithoutGlove = builder.define("damage_without_glove", true);
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
	}
}
