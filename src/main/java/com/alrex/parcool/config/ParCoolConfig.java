package com.alrex.parcool.config;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.AnimatorList;
import com.alrex.parcool.client.gui.ColorTheme;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.impl.HUDType;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.Vault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ParCoolConfig {
	public static final ForgeConfigSpec.Builder S_BUILDER = new ForgeConfigSpec.Builder();

	public static final Server CONFIG_SERVER = new Server(S_BUILDER);

	public static class Client {
		public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
		public static final ForgeConfigSpec BUILT_SPEC;

		public static ForgeConfigSpec.BooleanValue getPossibilityOf(Class<? extends Action> action) {
			return actionPossibilities[ActionList.getIndexOf(action)];
		}

		public static ForgeConfigSpec.BooleanValue canAnimate(Class<? extends Animator> animator) {
			return animatorPossibilities[AnimatorList.getIndex(animator)];
		}

		public static ForgeConfigSpec.IntValue getStaminaConsumptionOf(Class<? extends Action> action) {
			return staminaConsumptions[ActionList.getIndexOf(action)];
		}

		public enum ConfigGroup {
			Animation, CameraAnimation, HUD, Modifier, Control, Stamina, Other
		}

		public interface Item<T> {
			T get();

			//In Client : Set the value to config
			//In Server : Set the value to internal cache (for synchronization with client only)
			void set(T value);

			@OnlyIn(Dist.CLIENT)
			@Nullable
			ForgeConfigSpec.ConfigValue<T> getInternalInstance();

			@OnlyIn(Dist.CLIENT)
			void register(ForgeConfigSpec.Builder builder);

			@OnlyIn(Dist.CLIENT)
			void writeToBuffer(ByteBuffer buffer);

			@OnlyIn(Dist.DEDICATED_SERVER)
			T readFromBuffer(ByteBuffer buffer);
		}

		public enum Booleans implements Item<Boolean> {
			InfiniteStamina(
					ConfigGroup.Stamina,
					"Infinite Stamina (this needs a permission from server, even if it is on single player's game. normally permitted)\nPlease check 'parcool-server.toml' in 'serverconfig' directory",
					"infinite_stamina", false
			),
			InfiniteStaminaWhenCreative(
					ConfigGroup.Stamina, "Infinite Stamina while player is cretive mode",
					"infinite_stamina_if_creative_mode", true
			),
			UseHungerBarInstead(
					ConfigGroup.Stamina, "ParCool consume hanger value instead of stamina",
					"use_hanger_instead", false
			),
			EnableAnimation(
					ConfigGroup.Animation, "Enable custom animations",
					"enable_animation", true
			),
			EnableFallingAnimation(
					ConfigGroup.Animation, "Enable custom animation of falling",
					"enable_falling_animation", true
			),
			EnableFPVAnimation(
					ConfigGroup.CameraAnimation, "Enable first-person-view animations",
					"enable_fov_animation", true
			),
			EnableCameraAnimationOfRolling(
					ConfigGroup.CameraAnimation, "Enable Roll rotation of camera",
					"enable_camera_rotation_flipping", true
			),
			EnableCameraAnimationOfFlipping(
					ConfigGroup.CameraAnimation, "Enable Flipping rotation of camera",
					"enable_camera_rotation_flipping", true
			),
			EnableCameraAnimationOfVault(
					ConfigGroup.CameraAnimation, "Enable Vault animation of camera",
					"enable_camera_animation_vault", false
			),
			EnableCameraAnimationOfHWallRun(
					ConfigGroup.CameraAnimation, "Enable Horizontal-WallRun animation of camera",
					"enable_camera_animation_h-wall-run", true
			),
			EnableCameraAnimationOfHangDown(
					ConfigGroup.CameraAnimation, "Enable Hang animation of camera",
					"enable_camera_animation_hang-down", true
			),
			HideStaminaHUDWhenStaminaIsInfinite(
					ConfigGroup.HUD, null,
					"hide_hud_if_stamina_infinite", true
			),
			EnableDoubleTappingForDodge(
					ConfigGroup.Control, "Enable double-tapping ctrl for Dodge",
					"enable_double_tapping_for_dodge", false
			),
			EnableCrawlInAir(
					ConfigGroup.Control, "Enable Crawl in air",
					"enable_crawl_in_air", true
			),
			EnableVaultInAir(
					ConfigGroup.Control, "Enable Vault in air",
					"enable_vault_in_air", true
			),
			EnableRollWhenCreative(
					ConfigGroup.Control, "Enable Roll when creative mode(experimental)",
					"enable_roll_creative", false
			),
			VaultKeyPressedNeeded(
					ConfigGroup.Control, "Make Vault Need Vault Key Pressed",
					"vault_needs_key_pressed", false
			),
			SubstituteSprintForFastRun(
					ConfigGroup.Control, "enable players to do actions needing Fast-Running by sprint",
					"substitute_sprint", false
			),
			ParCoolIsActive(
					ConfigGroup.Other, "Whether ParCool is active",
					"parcool_activation", true
			);
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
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
			HorizontalMarginOfStaminaHUD(
					ConfigGroup.HUD, "horizontal margin of normal HUD",
					"margin_h_stamina_hud", 3, 0, 100
			),
			VerticalMarginOfStaminaHUD(
					ConfigGroup.HUD, "vertical margin of normal HUD",
					"margin_v_stamina_hud", 3, 0, 100
			),
			WallRunContinuableTick(
					ConfigGroup.Modifier, "How long you can do Horizontal Wall Run",
					"wall-run_continuable_tick", 25, 15, 40
			),
			SlidingContinuableTick(
					ConfigGroup.Modifier, "How long you can do Slide",
					"sliding_continuable_tick", 15, 10, 30
			),
			MaxStamina(
					ConfigGroup.Stamina, null, "max_value_of_stamina",
					2000, 300, 10000
			),
			StaminaRecoveryValue(
					ConfigGroup.Stamina, null, "value_of_stamina_recovery",
					20, 1, 10000
			);
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
			public final int DefaultValue;
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
			FastRunSpeedModifier(
					ConfigGroup.Modifier, "FastRun speed modifier",
					"fast-run_modifier", 2, 0.001, 4
			),
			DodgeSpeedModifier(
					ConfigGroup.Modifier, "Dodge speed modifier",
					"dodge-speed_modifier", 1, 0.5, 1.5
			);
			public final ConfigGroup Group;
			@Nullable
			public final String Comment;
			public final String Path;
			public final double DefaultValue;
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

		private static final ForgeConfigSpec.BooleanValue[] actionPossibilities = new ForgeConfigSpec.BooleanValue[ActionList.ACTIONS.size()];
		private static final ForgeConfigSpec.BooleanValue[] animatorPossibilities = new ForgeConfigSpec.BooleanValue[AnimatorList.ANIMATORS.size()];
		private static final ForgeConfigSpec.IntValue[] staminaConsumptions = new ForgeConfigSpec.IntValue[ActionList.ACTIONS.size()];
		public static final ForgeConfigSpec.EnumValue<HUDType> StaminaHUDType;
		public static final ForgeConfigSpec.EnumValue<Vault.TypeSelectionMode> VaultAnimationMode;
		public static final ForgeConfigSpec.EnumValue<Position.Horizontal> AlignHorizontalStaminaHUD;
		public static final ForgeConfigSpec.EnumValue<Position.Vertical> AlignVerticalStaminaHUD;
		public static final ForgeConfigSpec.EnumValue<ColorTheme> GUIColorTheme;
		public static final ForgeConfigSpec.EnumValue<FastRun.ControlType> FastRunControl;

		private static void register(ForgeConfigSpec.Builder builder, ConfigGroup group) {
			Arrays.stream(Booleans.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
			Arrays.stream(Integers.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
			Arrays.stream(Doubles.values()).filter(x -> x.Group == group).forEach(x -> x.register(builder));
		}

		static {
			ForgeConfigSpec.Builder builder = BUILDER;
			builder.push("Possibility of Actions(Some do not have to work)");
			{
				for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
					actionPossibilities[i] = builder.define("can_" + ActionList.ACTIONS.get(i).getSimpleName(), true);
				}
			}
			builder.pop();
			builder.push("Stamina HUD Configuration");
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
				register(builder, ConfigGroup.Animation);
				register(builder, ConfigGroup.CameraAnimation);
			}
			builder.pop();
			builder.push("Control");
			{
				FastRunControl = builder.comment("Control of FastRun").defineEnum("fast-run_control", FastRun.ControlType.PressKey);
				register(builder, ConfigGroup.Control);
			}
			builder.pop();
			builder.push("Modifier");
			{
				register(builder, ConfigGroup.Modifier);
			}
			builder.pop();
			builder.push("Other Configuration");
			{
				VaultAnimationMode = builder.comment("Vault Animation(Dynamic is to select animation dynamically)").defineEnum("vault_animation_mode", Vault.TypeSelectionMode.Dynamic);
				GUIColorTheme = builder.comment("Color theme of Setting GUI").defineEnum("gui_color_theme", ColorTheme.Blue);
				register(builder, ConfigGroup.Other);
			}
			builder.pop();
			builder.comment("Stamina Section").push("Stamina");
			{
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
				register(builder, ConfigGroup.Stamina);
			}
			builder.pop();
			BUILT_SPEC = BUILDER.build();
		}
	}

	public static class Server {
		private final ForgeConfigSpec.BooleanValue[] actionPermissions = new ForgeConfigSpec.BooleanValue[ActionList.ACTIONS.size()];

		public boolean getPermissionOf(Class<? extends Action> action) {
			return actionPermissions[ActionList.getIndexOf(action)].get();
		}

		private final ForgeConfigSpec.IntValue[] leastStaminaConsumptions = new ForgeConfigSpec.IntValue[ActionList.ACTIONS.size()];

		public int getLeastStaminaConsumptionOf(Class<? extends Action> action) {
			return leastStaminaConsumptions[ActionList.getIndexOf(action)].get();
		}

		public final ForgeConfigSpec.BooleanValue allowInfiniteStamina;
		public final ForgeConfigSpec.IntValue staminaMax;
		public final ForgeConfigSpec.IntValue staminaRecoveryMax;
		public final ForgeConfigSpec.BooleanValue enforced;

		Server(ForgeConfigSpec.Builder builder) {
			enforced = builder.comment("Whether these limitations will be imposed to players").define("limitations_imposed", false);
			builder.push("Action Permissions");
			{
				for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
					actionPermissions[i]
							= builder.define("permit_" + ActionList.ACTIONS.get(i).getSimpleName(), true);
				}
			}
			builder.pop();
			builder.push("Stamina");
			{
				staminaMax = builder.comment("Limitation of max stamina").defineInRange("max_value_of_stamina", 10000, 300, 100000);
				staminaRecoveryMax = builder.comment("Limitation of max stamina recovery").defineInRange("max_value_of_stamina_recovery", 1000, 1, 10000);
				allowInfiniteStamina = builder.comment("Allow Infinite Stamina").define("infinite_stamina", true);
				builder.push("Least Consumption");
				{
					for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
						leastStaminaConsumptions[i]
								= builder.defineInRange(
								"stamina_consumption_of_" + ActionList.ACTIONS.get(i).getSimpleName(),
								ActionList.ACTION_REGISTRIES.get(i).getDefaultStaminaConsumption(),
								0, 10000
						);
					}
				}
			}
			builder.pop();
		}
	}
}
