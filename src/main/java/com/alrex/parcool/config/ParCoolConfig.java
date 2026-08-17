package com.alrex.parcool.config;

import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.stamina.HUDType;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import com.alrex.parcool.common.stamina.StaminaTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.TreeMap;

public class ParCoolConfig {
	public ParCoolConfig(ActionRegistry actionRegistry, StaminaTypeRegistry staminaTypeRegistry) {
		this.client = new Client();
		this.server = new Server(actionRegistry, staminaTypeRegistry);
	}

	public void register(ModLoadingContext loadingContext) {
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
			ForgeConfigSpec.BooleanValue permit,
			ForgeConfigSpec.IntValue costOnStart,
			ForgeConfigSpec.IntValue costOnWorking,
			ForgeConfigSpec.IntValue costOnFinish,
			ForgeConfigSpec.IntValue learningCost
	) {
	}

	public static class Client {
		private final ForgeConfigSpec builtConfig;

		public record StaminaHud(
				ForgeConfigSpec.EnumValue<HUDType> type,
				ForgeConfigSpec.EnumValue<Position.Horizontal> alignHorizontal,
				ForgeConfigSpec.EnumValue<Position.Vertical> alignVertical,
				ForgeConfigSpec.BooleanValue showAlways,
				ForgeConfigSpec.BooleanValue hideAutomatically,
				ForgeConfigSpec.IntValue offsetHorizontal,
				ForgeConfigSpec.IntValue offsetVertical
		) {
		}

		public record GrapplingHookView(
				ForgeConfigSpec.DoubleValue fovIntensity,
				ForgeConfigSpec.DoubleValue cameraRollIntensity,
				ForgeConfigSpec.DoubleValue ropeSag
		) {
		}

		public final ForgeConfigSpec.BooleanValue enableActionSounds;
		public final ForgeConfigSpec.BooleanValue parcoolIsActive;
		public final ForgeConfigSpec.BooleanValue showTargetIndicator;
		public final ForgeConfigSpec.IntValue targetIndicatorSize;
		public final ForgeConfigSpec.BooleanValue debugRope;
		public final StaminaHud staminaHud;
		public final GrapplingHookView grapplingHook;


		public Client() {
			ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
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
			builder.push("GrapplingHook");
			{
				grapplingHook = new GrapplingHookView(
						builder.comment("how much speed widens the view. 0 disables")
								.defineInRange("fov_intensity", 0.6, 0.0, 1.0),
						builder.comment("how far the camera leans towards the rope. 0 disables")
								.defineInRange("camera_roll_intensity", 0.6, 0.0, 1.0),
						builder.comment("how much a loose rope sags. 0 for straight")
								.defineInRange("rope_sag", 1.0, 0.0, 2.0)
				);
				showTargetIndicator = builder.comment("show a marker where the hook would land")
						.define("show_target_indicator", true);
				targetIndicatorSize = builder.comment("size of that marker in pixels")
						.defineInRange("target_indicator_size", 11, 3, 64);
				debugRope = builder.comment("log it when the rope ends up inside a block")
						.define("debug_rope", false);
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
		public record GrapplingHook(
				ForgeConfigSpec.DoubleValue maxRange,
				ForgeConfigSpec.DoubleValue hookTravelSpeed,
				ForgeConfigSpec.DoubleValue minRopeLength,
				ForgeConfigSpec.DoubleValue reelOutSpeed,
				ForgeConfigSpec.DoubleValue swingControlForce,
				ForgeConfigSpec.DoubleValue swingAssist,
				ForgeConfigSpec.DoubleValue swingDamping,
				ForgeConfigSpec.DoubleValue airResistance,
				ForgeConfigSpec.DoubleValue maxSpeed,
				ForgeConfigSpec.DoubleValue ropeDrag,
				ForgeConfigSpec.DoubleValue ropeCompliance,
				ForgeConfigSpec.DoubleValue releaseBoost,
				ForgeConfigSpec.IntValue momentumKeepTicks,
				ForgeConfigSpec.DoubleValue momentumDrag,
				ForgeConfigSpec.IntValue aimAssistAngle,
				ForgeConfigSpec.IntValue physicsSubsteps,
				ForgeConfigSpec.BooleanValue allowRopeWrapping,
				ForgeConfigSpec.IntValue maxRopeBends,
				ForgeConfigSpec.DoubleValue maxTension,
				ForgeConfigSpec.DoubleValue pullStrength,
				ForgeConfigSpec.DoubleValue pullSpeedLimit
		) {
		}

		private final ForgeConfigSpec builtConfig;
		private final TreeMap<String, TreeMap<ActionEntry<?>, ActionValue>> actionMap;
        public final ForgeConfigSpec.BooleanValue damageWithoutGlove;
        public final ForgeConfigSpec.BooleanValue enableSkillTree;
		public final GrapplingHook grapplingHook;

		public final ResourceLocation getStaminaTypeID() {
			var id = ResourceLocation.tryParse(staminaType.get());
			return id != null ? id : StaminaTypes.PARCOOL_STAMINA.id();
		}

		public final ForgeConfigSpec.ConfigValue<String> staminaType;

		public ActionValue get(ActionEntry<?> entry) {
			return actionMap.get(entry.id().getNamespace()).get(entry);
		}

		public Server(ActionRegistry actionRegistry, StaminaTypeRegistry staminaTypeRegistry) {
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
			builder.push("GrapplingHook");
			{
				grapplingHook = new GrapplingHook(
						builder.comment("max reach in blocks")
								.defineInRange("max_range", 48.0, 8.0, 128.0),
						builder.comment("how fast the thrown hook flies")
								.defineInRange("hook_travel_speed", 4.0, 1.0, 24.0),
						builder.comment("shortest the rope can get")
								.defineInRange("min_rope_length", 1.5, 0.5, 8.0),
						builder.comment("rope let out per tick while sneaking")
								.defineInRange("reel_out_speed", 0.35, 0.0, 1.0),
						builder.comment("steering force while swinging. gravity is 0.08")
								.defineInRange("swing_control_force", 0.012, 0.0, 0.16),
						builder.comment("how much steering pumps the swing. 0 for a plain pendulum")
								.defineInRange("swing_assist", 0.35, 0.0, 2.0),
						builder.comment("raise this if a swing wobbles for too long")
								.defineInRange("swing_damping", 0.015, 0.0, 0.3),
						builder.comment("drag at speed. raise for a heavier swing")
								.defineInRange("air_resistance", 0.013, 0.0, 0.1),
						builder.comment("speed limit while swinging")
								.defineInRange("max_speed", 1.6, 0.5, 5.0),
						builder.comment("flat speed kept per tick")
								.defineInRange("rope_drag", 0.997, 0.9, 1.0),
						builder.comment("how much the rope stretches. 0 for none")
								.defineInRange("rope_compliance", 0.0005, 0.0, 0.05),
						builder.comment("upward boost when letting go with jump. a jump is 0.42")
								.defineInRange("release_boost", 0.36, 0.0, 1.5),
						builder.comment("ticks of momentum kept after letting go. 0 disables")
								.defineInRange("momentum_keep_ticks", 30, 0, 200),
						builder.comment("speed kept per tick during that. vanilla air is 0.91")
								.defineInRange("momentum_drag", 0.98, 0.9, 1.0),
						builder.comment("aim assist cone in degrees. 0 disables")
								.defineInRange("aim_assist_angle", 14, 0, 45),
						builder.comment("physics steps per tick")
								.defineInRange("physics_substeps", 6, 1, 12),
						builder.comment("let the rope bend around corners")
								.define("allow_rope_wrapping", true),
						builder.comment("max corners one rope can wrap around")
								.defineInRange("max_rope_bends", 40, 0, 64),
						builder.comment("load before the hook tears off. 0 never breaks")
								.defineInRange("max_tension", 26.0, 0.0, 200.0),
						builder.comment("pull force while holding use. gravity is 0.08")
								.defineInRange("pull_strength", 0.12, 0.0, 2.0),
						builder.comment("fastest the rope reels you in, in blocks per tick")
								.defineInRange("pull_speed_limit", 0.75, 0.05, 4.0)
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
	}
}
