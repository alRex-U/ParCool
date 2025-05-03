package com.alrex.parcool.config;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.AnimatorList;
import com.alrex.parcool.client.gui.ColorTheme;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.impl.HUDType;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ParCoolConfig {
	public enum AdvantageousDirection {
		Lower, Higher
	}
	public interface Item<T> {
		T get();

		void set(T value);

		String getPath();

		@Nullable
		ForgeConfigSpec.ConfigValue<T> getInternalInstance();

		void register(ForgeConfigSpec.Builder builder);

		void writeToBuffer(ByteBuffer buffer);

		T readFromBuffer(ByteBuffer buffer);
	}

	public enum ConfigGroup {
		Animation, CameraAnimation, HUD, Modifier, Control, Stamina, Other
	}

	public static class Client {
		public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
		public static final ForgeConfigSpec BUILT_CONFIG;

		public static ForgeConfigSpec.BooleanValue getPossibilityOf(Class<? extends Action> action) {
			return actionPossibilities[ActionList.getIndexOf(action)];
		}

		public static ForgeConfigSpec.BooleanValue canAnimate(Class<? extends Animator> animator) {
			return animatorPossibilities[AnimatorList.getIndex(animator)];
		}

		public static ForgeConfigSpec.IntValue getStaminaConsumptionOf(Class<? extends Action> action) {
			return staminaConsumptions[ActionList.getIndexOf(action)];
		}

		public enum Booleans implements Item<Boolean> {
			InfiniteStamina(
					ConfigGroup.Stamina,
					"Infinite Stamina (this needs a permission from server, even if it is on single player's game. normally permitted)\nPlease check 'parcool-server.toml' in 'serverconfig' directory",
					"infinite_stamina", false
			),
			InfiniteStaminaWhenCreative(
                    ConfigGroup.Stamina, "Infinite Stamina while player is creative mode",
					"infinite_stamina_if_creative_mode", true
			),
			EnableAnimation(
					ConfigGroup.Animation, "Enable custom animations",
					"enable_animation", true
			),
			EnableFallingAnimation(
					ConfigGroup.Animation, "Enable custom animation of falling",
					"enable_falling_animation", true
			),
			EnableLeanAnimationOfFastRun(
					ConfigGroup.Animation, "Enable lean animation while FastRun",
					"enable_lean_animation_fast_run", true
			),
			EnableFPVAnimation(
					ConfigGroup.CameraAnimation, "Enable first-person-view animations",
					"enable_fpv_animation", false
			),
			EnableCameraAnimationOfDodge(
					ConfigGroup.CameraAnimation, "Enable rotation of camera by Dodge",
					"enable_camera_rotation_dodge", false
			),
			EnableCameraAnimationOfBackWallJump(
					ConfigGroup.CameraAnimation, "Enable rotation of camera by Backward Wall-Jump",
					"enable_camera_rotation_back_wall_jump", true
			),
			EnableCameraAnimationOfRolling(
					ConfigGroup.CameraAnimation, "Enable rotation of camera by Roll",
					"enable_camera_rotation_roll", true
			),
			EnableCameraAnimationOfFlipping(
					ConfigGroup.CameraAnimation, "Enable rotation of camera by Flipping",
					"enable_camera_rotation_flipping", true
			),
			EnableCameraAnimationOfVault(
					ConfigGroup.CameraAnimation, "Enable animation of camera by Vault",
					"enable_camera_animation_vault", false
			),
			EnableCameraAnimationOfHWallRun(
					ConfigGroup.CameraAnimation, "Enable animation of camera by Horizontal-WallRun",
					"enable_camera_animation_h-wall-run", true
			),
			EnableCameraAnimationOfHangDown(
					ConfigGroup.CameraAnimation, "Enable animation of camera by Hang-Down",
					"enable_camera_animation_hang-down", true
			),
			HideStaminaHUDWhenStaminaIsInfinite(
					ConfigGroup.HUD, null,
					"hide_hud_if_stamina_infinite", true
			),
            ShowActionStatusBar(
                    ConfigGroup.HUD, "Stamina HUD shows action charge rate, cool time or etc",
                    "show_action_status_bar", true
            ),
			EnableDoubleTappingForDodge(
					ConfigGroup.Control, "Enable double-tapping ctrl for Dodge",
					"enable_double_tapping_for_dodge", false
			),
			EnableWallJumpCooldown(
					ConfigGroup.Control, "Enable cooldown of wall jump",
					"enable_wall_jump_cooldown", true
			),
			EnableCrawlInAir(
					ConfigGroup.Control, "Enable Crawl in air",
					"enable_crawl_in_air", true
			),
			EnableVaultInAir(
					ConfigGroup.Control, "Enable Vault in air",
					"enable_vault_in_air", true
			),
            CanGetOffStepsWhileDodge(
                    ConfigGroup.Control, "Enable getting off steps while doing dodge",
                    "can_get_off_steps_while_dodge", false
			),
			EnableRollWhenCreative(
					ConfigGroup.Control, "Enable Roll when creative mode (experimental)",
					"enable_roll_creative", false
			),
			EnableJustTimeEffectOfBreakfall(
					ConfigGroup.Other, "Enable just timing effect of Breakfall",
					"enable_just_time_effect_breakfall", true
			),
			EnableActionSounds(
					ConfigGroup.Other, "Enable sounds triggered by Action",
					"enable_sounds", true
			),
			EnableActionParticles(
					ConfigGroup.Other, "Enable particles triggered by Action",
					"enable_particles", true
			),
			EnableActionParticlesOfJustTimeBreakfall(
					ConfigGroup.Other, "Enable particles triggered by just-time breakfall",
					"enable_particles_jt_breakfall", true
			),
            Enable3DRenderingForZipline(
                    ConfigGroup.Other, "Enable block like rendering of zipline",
                    "enable_3d_render_zipline", true
            ),
			VaultKeyPressedNeeded(
                    ConfigGroup.Control, "Make Vault need Vault Key Pressed",
					"vault_needs_key_pressed", false
			),
            HideInBlockSneakNeeded(
                    ConfigGroup.Control, "Make HideInBlock need player sneaking",
                    "hideinblock_needs_sneaking", true
            ),
			SubstituteSprintForFastRun(
					ConfigGroup.Control, "enable players to do actions needing Fast-Running by sprint",
					"substitute_sprint", false
			),
            ShowAutoResynchronizationNotification(
                    ConfigGroup.Other, "Notify if auto resynchronization of Limitation is executed",
                    "notify_limitation_auto_resync", true
            ),
			ParCoolIsActive(
					ConfigGroup.Other, "Whether ParCool is active",
					"parcool_activation", true
			);
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
            public final String Translation;
			public final boolean DefaultValue;
			@Nullable
			private ForgeConfigSpec.BooleanValue configInstance = null;

			Booleans(
					ConfigGroup group,
					@Nullable String comment,
					String path,
					boolean defaultValue
			) {
				Group = group;
				Comment = comment;
				Path = path;
				DefaultValue = defaultValue;
                Translation = "parcool.config.c." + path;
			}

			@Override
			public String getPath() {
				return Path;
			}

			@Override
			public void register(ForgeConfigSpec.Builder builder) {
				if (Comment != null) {
					builder.comment(Comment);
				}
                builder.translation(Translation);
				configInstance = builder.define(Path, DefaultValue);
			}

			public Boolean get() {
				if (configInstance == null) return DefaultValue;
				return configInstance.get();
			}

			@Override
			public void set(Boolean value) {
				if (configInstance != null) {
					configInstance.set(value);
				}
			}

			public ForgeConfigSpec.BooleanValue getInternalInstance() {
				return configInstance;
			}

			@Override
			public void writeToBuffer(ByteBuffer buffer) {
				buffer.put((byte) (get() ? 1 : 0));
			}

			@Override
			public Boolean readFromBuffer(ByteBuffer buffer) {
				return buffer.get() != 0;
			}
		}

		public enum Integers implements Item<Integer> {
            AcceptableAngleOfWallJump(
                    ConfigGroup.Control, "acceptable walll angle of wall jump : `0` means you exactly opposite to wall, `180` allow you to wall jump for all angle",
                    "acceptable_angle_wall_jump", 110, 0, 180
            ),
            HorizontalOffsetOfStaminaHUD(
                    ConfigGroup.HUD, "horizontal offset of normal HUD",
                    "offset_h_stamina_hud", 3, 0, 100
            ),
            VerticalOffsetOfStaminaHUD(
                    ConfigGroup.HUD, "vertical offset of normal HUD",
                    "offset_v_stamina_hud", 3, 0, 100
			),
            HorizontalOffsetOfLightStaminaHUD(
                    ConfigGroup.HUD, "horizontal offset of light HUD",
                    "offset_h_light_hud", 0, -100, 100
            ),
            VerticalOffsetOfLightStaminaHUD(
                    ConfigGroup.HUD, "vertical offset of light HUD",
                    "offset_v_light_hud", 0, -100, 100
			),
			WallRunContinuableTick(
					ConfigGroup.Modifier, "How long you can do Horizontal Wall Run",
					"wall-run_continuable_tick", 25, Server.Integers.MaxWallRunContinuableTick.Min, Server.Integers.MaxWallRunContinuableTick.Max
			),
			SlidingContinuableTick(
					ConfigGroup.Modifier, "How long you can do Slide",
					"sliding_continuable_tick", 15, Server.Integers.MaxSlidingContinuableTick.Min, Server.Integers.MaxSlidingContinuableTick.Max
			),
			SuccessiveDodgeCoolTime(
					ConfigGroup.Control, "How long duration of dodge is deal as successive dodge",
					"successive_dodge_cool_time", 30, 0, Integer.MAX_VALUE
			),
			DodgeCoolTime(
					ConfigGroup.Control, "Cool time of Dodge action",
					"dodge_cool_time", Dodge.MAX_TICK, Dodge.MAX_TICK, Integer.MAX_VALUE
			),
			MaxSuccessiveDodgeCount(
					ConfigGroup.Control, "Max number of times of successive Dodge action",
					"successive_dodge_count", 3, 1, Integer.MAX_VALUE
			);
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
			public final int DefaultValue;
            public final String Translation;
			public final int Min;
			public final int Max;
			@Nullable
			private ForgeConfigSpec.IntValue configInstance = null;

			Integers(
					ConfigGroup group,
					@Nullable String comment,
					String path,
					int defaultValue,
					int min,
					int max
			) {
				Group = group;
				Comment = comment;
				Path = path;
				DefaultValue = defaultValue;
				Min = min;
				Max = max;
                Translation = "parcool.config.c." + path;
			}

			@Override
			public String getPath() {
				return Path;
			}

			public void register(ForgeConfigSpec.Builder builder) {
				if (Comment != null) {
					builder.comment(Comment);
				}
                builder.translation(Translation);
				configInstance = builder.defineInRange(Path, DefaultValue, Min, Max);
			}

			@Override
			public Integer get() {
				if (configInstance == null) return DefaultValue;
				return configInstance.get();
			}

			@Override
			public void set(Integer value) {
				if (configInstance != null) {
					configInstance.set(value);
				}
			}

			public ForgeConfigSpec.IntValue getInternalInstance() {
				return configInstance;
			}

			@Override
			public void writeToBuffer(ByteBuffer buffer) {
				buffer.putInt(get());
			}

			@Override
			public Integer readFromBuffer(ByteBuffer buffer) {
				return buffer.getInt();
			}
		}

		public enum Doubles implements Item<Double> {
			FastRunSpeedModifier(
					ConfigGroup.Modifier, "FastRun speed modifier",
					"fast-run_modifier", 2, Server.Doubles.MaxFastRunSpeedModifier.Min, Server.Doubles.MaxFastRunSpeedModifier.Max
			),
			FastSwimSpeedModifier(
					ConfigGroup.Modifier, "FastSwim speed modifier",
					"fast-swim_modifier", 2, Server.Doubles.MaxFastSwimSpeedModifier.Min, Server.Doubles.MaxFastSwimSpeedModifier.Max
			),
			DodgeSpeedModifier(
					ConfigGroup.Modifier, "Dodge speed modifier",
					"dodge-speed_modifier", 1, Server.Doubles.MaxDodgeSpeedModifier.Min, Server.Doubles.MaxDodgeSpeedModifier.Max
			);
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
			public final double DefaultValue;
            public final String Translation;
			public final double Min;
			public final double Max;
			@Nullable
			private ForgeConfigSpec.DoubleValue configInstance = null;

			Doubles(
					ConfigGroup group,
					@Nullable String comment,
					String path,
					double defaultValue,
					double min,
					double max
			) {
				Group = group;
				Comment = comment;
				Path = path;
				DefaultValue = defaultValue;
				Min = min;
				Max = max;
                Translation = "parcool.config.c." + path;
			}

			@Override
			public String getPath() {
				return Path;
			}

			public void register(ForgeConfigSpec.Builder builder) {
				if (Comment != null) {
					builder.comment(Comment);
				}
                builder.translation(Translation);
				configInstance = builder.defineInRange(Path, DefaultValue, Min, Max);
			}

			@Override
			public void writeToBuffer(ByteBuffer buffer) {
				buffer.putDouble(get());
			}

			@Override
			public Double readFromBuffer(ByteBuffer buffer) {
				return buffer.getDouble();
			}

			@Override
			public Double get() {
				if (configInstance == null) return DefaultValue;
				return configInstance.get();
			}

			@Override
			public void set(Double value) {
				if (configInstance != null) {
					configInstance.set(value);
				}
			}

			@OnlyIn(Dist.CLIENT)
			@Nullable
			public ForgeConfigSpec.DoubleValue getInternalInstance() {
				return configInstance;
			}
		}

		private static final ForgeConfigSpec.BooleanValue[] actionPossibilities = new ForgeConfigSpec.BooleanValue[ActionList.ACTIONS.size()];
		private static final ForgeConfigSpec.BooleanValue[] animatorPossibilities = new ForgeConfigSpec.BooleanValue[AnimatorList.ANIMATORS.size()];
		private static final ForgeConfigSpec.IntValue[] staminaConsumptions = new ForgeConfigSpec.IntValue[ActionList.ACTIONS.size()];
		public static final ForgeConfigSpec.EnumValue<HUDType> StaminaHUDType;
		public static final ForgeConfigSpec.EnumValue<Vault.TypeSelectionMode> VaultAnimationMode;
		public static final ForgeConfigSpec.EnumValue<Position.Horizontal> AlignHorizontalStaminaHUD;
		public static final ForgeConfigSpec.EnumValue<Position.Vertical> AlignVerticalStaminaHUD;
		public static final ForgeConfigSpec.EnumValue<ColorTheme> GUIColorTheme;
		public static final ForgeConfigSpec.EnumValue<FastRun.ControlType> FastRunControl;
		public static final ForgeConfigSpec.EnumValue<Crawl.ControlType> CrawlControl;
        public static final ForgeConfigSpec.EnumValue<Flipping.ControlType> FlipControl;
		public static final ForgeConfigSpec.EnumValue<HorizontalWallRun.ControlType> HWallRunControl;
		public static final ForgeConfigSpec.EnumValue<WallJump.ControlType> WallJumpControl;
        public static final ForgeConfigSpec.EnumValue<ClingToCliff.ControlType> ClingToCliffControl;
		public static final ForgeConfigSpec.EnumValue<IStamina.Type> StaminaType;

		private static void register(ForgeConfigSpec.Builder builder, ConfigGroup group) {
			Arrays.stream(Booleans.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
			Arrays.stream(Integers.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
			Arrays.stream(Doubles.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
		}

		static {
			ForgeConfigSpec.Builder builder = BUILDER;
            builder.push("Possibility_of_Actions(Some_do_not_have_to_work)");
			{
				for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
					actionPossibilities[i] = builder.define("can_" + ActionList.ACTIONS.get(i).getSimpleName(), true);
				}
			}
			builder.pop();
            builder.push("Stamina_HUD_Configuration");
			{
				StaminaHUDType = builder.defineEnum("stamina_hud_type", HUDType.Light);
				AlignHorizontalStaminaHUD = builder.comment("horizontal alignment").defineEnum("align_h_s_hud", Position.Horizontal.Right);
				AlignVerticalStaminaHUD = builder.comment("vertical alignment").defineEnum("align_v_s_hud", Position.Vertical.Bottom);
				register(builder, ConfigGroup.HUD);
			}
			builder.pop();
			builder.push("Animations");
			{
				builder.push("Animators");
				{
					for (int i = 0; i < AnimatorList.ANIMATORS.size(); i++) {
						animatorPossibilities[i] = builder.define("enable_" + AnimatorList.ANIMATORS.get(i).getSimpleName(), true);
					}
				}
				builder.pop();
				register(builder, ConfigGroup.Animation);
				register(builder, ConfigGroup.CameraAnimation);
			}
			builder.pop();
			builder.push("Control");
			{
				FastRunControl = builder.comment("Control of Fast Run").defineEnum("fast-run_control", FastRun.ControlType.PressKey);
				CrawlControl = builder.comment("Control of Crawl").defineEnum("crawl_control", Crawl.ControlType.PressKey);
                FlipControl = builder.comment("Control of Flipping").defineEnum("flip_control", Flipping.ControlType.TapMovementAndJump);
				HWallRunControl = builder.comment("Control of Horizontal Wall Run").defineEnum("h-wall-run_control", HorizontalWallRun.ControlType.PressKey);
				WallJumpControl = builder.comment("Control of Wall Jump").defineEnum("wall-jump_control", WallJump.ControlType.PressKey);
                ClingToCliffControl = builder.comment("Control of Cling To Cliff").defineEnum("cling-to-cliff_control", ClingToCliff.ControlType.PressKey);
				register(builder, ConfigGroup.Control);
			}
			builder.pop();
			builder.push("Modifier");
			{
				register(builder, ConfigGroup.Modifier);
			}
			builder.pop();
            builder.push("Other_Configuration");
			{
				VaultAnimationMode = builder.comment("Vault Animation(Dynamic is to select animation dynamically)").defineEnum("vault_animation_mode", Vault.TypeSelectionMode.Dynamic);
				GUIColorTheme = builder.comment("Color theme of Setting GUI").defineEnum("gui_color_theme", ColorTheme.Blue);
				register(builder, ConfigGroup.Other);
			}
			builder.pop();
			builder.push("Stamina");
			{
                StaminaType = builder.defineEnum("used_stamina", IStamina.Type.Default);
                builder.comment("Caution : Max stamina and stamina recovery config is removed because they became attributes.");
                register(builder, ConfigGroup.Stamina);
				builder.push("Consumption");
				{
					for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
						staminaConsumptions[i]
								= builder.defineInRange(
								"stamina_consumption_of_" + ActionList.ACTIONS.get(i).getSimpleName(),
								ActionList.ACTION_REGISTRIES.get(i).getDefaultStaminaConsumption(),
								0, 10000
						);
					}
				}
                builder.pop();
			}
			builder.pop();
			BUILT_CONFIG = builder.build();
		}
	}

	public static class Server {
		public enum Booleans implements Item<Boolean> {
			AllowInfiniteStamina(
					ConfigGroup.Stamina, "Permission of infinite stamina",
					"allow_infinite_stamina", true, true
			),
			AllowDisableWallJumpCooldown(
					ConfigGroup.Control, "Allow disabling cooldown of wall jump",
					"allow_disabling_wall_jump_cooldown", true, true
			),
            DodgeProvideInvulnerableFrame(
                    ConfigGroup.Other, "Enable invulnerable time by Dodge",
                    "enable_dodge_invulnerable_time", true, true
            );
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
			public final boolean DefaultValue;
			public final boolean AdvantageousValue;
			@Nullable
			private ForgeConfigSpec.BooleanValue configInstance = null;

			Booleans(
					ConfigGroup group,
					@Nullable String comment,
					String path,
					boolean defaultValue,
					boolean advantageous
			) {
				Group = group;
				Comment = comment;
				Path = path;
				DefaultValue = defaultValue;
				AdvantageousValue = advantageous;
			}

			@Override
			public String getPath() {
				return Path;
			}

			@Override
			public void register(ForgeConfigSpec.Builder builder) {
				if (Comment != null) {
					builder.comment(Comment);
				}
				configInstance = builder.define(Path, DefaultValue);
			}

			public Boolean get() {
				if (configInstance == null) return DefaultValue;
				return configInstance.get();
			}

			@Override
			public void set(Boolean value) {
				if (configInstance != null) {
					configInstance.set(value);
				}
			}

			public ForgeConfigSpec.BooleanValue getInternalInstance() {
				return configInstance;
			}

			@Override
			public void writeToBuffer(ByteBuffer buffer) {
				buffer.put((byte) (get() ? 1 : 0));
			}

			@Override
			public Boolean readFromBuffer(ByteBuffer buffer) {
				return buffer.get() != 0;
			}
		}

		public enum Integers implements Item<Integer> {
			MaxStaminaLimit(
					ConfigGroup.Stamina, "Limitation of max stamina value",
					"max_stamina_limit", Integer.MAX_VALUE, 300, Integer.MAX_VALUE, AdvantageousDirection.Higher
			),
			MaxStaminaRecovery(
					ConfigGroup.Stamina, "Limitation of max stamina recovery",
					"max_stamina_recovery_limit", Integer.MAX_VALUE, 1, Integer.MAX_VALUE, AdvantageousDirection.Higher
			),
			SuccessiveDodgeCoolTime(
					ConfigGroup.Control, "How long duration of dodge is deal as successive dodge",
					"least_successive_dodge_cool_time", 0, 0, Integer.MAX_VALUE, AdvantageousDirection.Lower
			),
			DodgeCoolTime(
					ConfigGroup.Control, "Cool time of Dodge action",
					"least_dodge_cool_time", Dodge.MAX_TICK, Dodge.MAX_TICK, Integer.MAX_VALUE, AdvantageousDirection.Lower
			),
			MaxSuccessiveDodgeCount(
					ConfigGroup.Control, "Max number of times of successive Dodge action",
					"max_successive_dodge_count", Integer.MAX_VALUE, 1, Integer.MAX_VALUE, AdvantageousDirection.Higher
			),
			MaxWallRunContinuableTick(
					ConfigGroup.Modifier, "How long you can do Horizontal Wall Run",
					"wall-run_continuable_tick", 40, 15, 100, AdvantageousDirection.Higher
			),
			MaxSlidingContinuableTick(
					ConfigGroup.Modifier, "How long you can do Slide",
					"sliding_continuable_tick", 30, 10, 60, AdvantageousDirection.Higher
			);
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
			public final int DefaultValue;
			public final int Min;
			public final int Max;
			public final AdvantageousDirection Advantageous;
			@Nullable
			private ForgeConfigSpec.IntValue configInstance = null;

			Integers(
					ConfigGroup group,
					@Nullable String comment,
					String path,
					int defaultValue,
					int min,
					int max,
					AdvantageousDirection advantageous
			) {
				Group = group;
				Comment = comment;
				Path = path;
				DefaultValue = defaultValue;
				Min = min;
				Max = max;
				Advantageous = advantageous;
			}

			@Override
			public String getPath() {
				return Path;
			}

			public void register(ForgeConfigSpec.Builder builder) {
				if (Comment != null) {
					builder.comment(Comment);
				}
				configInstance = builder.defineInRange(Path, DefaultValue, Min, Max);
			}

			@Override
			public Integer get() {
				if (configInstance == null) return DefaultValue;
				return configInstance.get();
			}

			@Override
			public void set(Integer value) {
				if (configInstance != null) {
					configInstance.set(value);
				}
			}

			public ForgeConfigSpec.IntValue getInternalInstance() {
				return configInstance;
			}

			@Override
			public void writeToBuffer(ByteBuffer buffer) {
				buffer.putInt(get());
			}

			@Override
			public Integer readFromBuffer(ByteBuffer buffer) {
				return buffer.getInt();
			}
		}

		public enum Doubles implements Item<Double> {
			MaxFastRunSpeedModifier(
					ConfigGroup.Modifier, "FastRun speed modifier",
					"max_fast-run_modifier", 2, 0.001, 10, AdvantageousDirection.Higher
			),
			MaxFastSwimSpeedModifier(
					ConfigGroup.Modifier, "FastSwim speed modifier",
					"max_fast-swim_modifier", 2, 0.001, 10, AdvantageousDirection.Higher
			),
			MaxDodgeSpeedModifier(
					ConfigGroup.Modifier, "Dodge speed modifier",
					"max_dodge-speed_modifier", 1, 0.5, 3, AdvantageousDirection.Higher
			);
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
			public final double DefaultValue;
			public final double Min;
			public final double Max;
			public final AdvantageousDirection Advantageous;
			@Nullable
			private ForgeConfigSpec.DoubleValue configInstance = null;

			Doubles(
					ConfigGroup group,
					@Nullable String comment,
					String path,
					double defaultValue,
					double min,
					double max,
					AdvantageousDirection advantageous
			) {
				Group = group;
				Comment = comment;
				Path = path;
				DefaultValue = defaultValue;
				Min = min;
				Max = max;
				Advantageous = advantageous;
			}

			@Override
			public String getPath() {
				return Path;
			}

			public void register(ForgeConfigSpec.Builder builder) {
				if (Comment != null) {
					builder.comment(Comment);
				}
				configInstance = builder.defineInRange(Path, DefaultValue, Min, Max);
			}

			@Override
			public void writeToBuffer(ByteBuffer buffer) {
				buffer.putDouble(get());
			}

			@Override
			public Double readFromBuffer(ByteBuffer buffer) {
				return buffer.getDouble();
			}

			@Override
			public Double get() {
				if (configInstance == null) return DefaultValue;
				return configInstance.get();
			}

			@Override
			public void set(Double value) {
				if (configInstance != null) {
					configInstance.set(value);
				}
			}

			@OnlyIn(Dist.CLIENT)
			@Nullable
			public ForgeConfigSpec.DoubleValue getInternalInstance() {
				return configInstance;
			}
		}

		public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
		public static final ForgeConfigSpec BUILT_CONFIG;
		private static final ForgeConfigSpec.BooleanValue[] actionPermissions = new ForgeConfigSpec.BooleanValue[ActionList.ACTIONS.size()];

		public static boolean getPermissionOf(Class<? extends Action> action) {
			return actionPermissions[ActionList.getIndexOf(action)].get();
		}

		private static final ForgeConfigSpec.IntValue[] leastStaminaConsumptions = new ForgeConfigSpec.IntValue[ActionList.ACTIONS.size()];
		public static final ForgeConfigSpec.BooleanValue LimitationEnabled;

		private static void register(ForgeConfigSpec.Builder builder, ConfigGroup group) {
			Arrays.stream(Server.Booleans.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
			Arrays.stream(Server.Integers.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
			Arrays.stream(Server.Doubles.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
		}

		public static int getLeastStaminaConsumptionOf(Class<? extends Action> action) {
			return leastStaminaConsumptions[ActionList.getIndexOf(action)].get();
		}

		static {
			ForgeConfigSpec.Builder builder = BUILDER;
			builder.push("Limitations");
			{
				LimitationEnabled = builder.comment("Whether these limitations will be imposed to players").define("limitation_imposed", false);
				builder.push("Action_Permissions");
				{
					for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
						actionPermissions[i]
								= builder.define("permit_" + ActionList.ACTIONS.get(i).getSimpleName(), true);
					}
				}
				builder.pop();

				builder.push("Stamina");
				{
					builder.push("Least_Consumption");
					{
						for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
                            leastStaminaConsumptions[i] = builder.defineInRange(
									"stamina_consumption_of_" + ActionList.ACTIONS.get(i).getSimpleName(),
									ActionList.ACTION_REGISTRIES.get(i).getDefaultStaminaConsumption(),
									0, 10000
							);
						}
					}
					builder.pop();
					register(builder, ConfigGroup.Stamina);
				}
				builder.pop();
				builder.push("Control");
				{
					register(builder, ConfigGroup.Control);
				}
				builder.pop();
				builder.push("Modifier");
				{
					register(builder, ConfigGroup.Modifier);
				}
				builder.pop();
			}
			builder.pop();
			BUILT_CONFIG = builder.build();
		}
	}
}
