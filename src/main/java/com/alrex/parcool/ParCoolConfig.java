package com.alrex.parcool;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.AnimatorList;
import com.alrex.parcool.client.gui.ColorTheme;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.action.impl.Vault;
import net.minecraftforge.common.ForgeConfigSpec;

public class ParCoolConfig {
	private static final ForgeConfigSpec.Builder C_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder S_BUILDER = new ForgeConfigSpec.Builder();

	public static final Client CONFIG_CLIENT = new Client(C_BUILDER);

	public static final Server CONFIG_SERVER = new Server(S_BUILDER);

	public static class Client {
		public ForgeConfigSpec.BooleanValue getPossibilityOf(Class<? extends Action> action) {
			return actionPossibilities[ActionList.getIndexOf(action)];
		}

		public ForgeConfigSpec.BooleanValue canAnimate(Class<? extends Animator> animator) {
			return animatorPossibilities[AnimatorList.getIndex(animator)];
		}

		public ForgeConfigSpec.IntValue getStaminaConsumptionOf(Class<? extends Action> action) {
			return staminaConsumptions[ActionList.getIndexOf(action)];
		}

		private final ForgeConfigSpec.BooleanValue[] actionPossibilities = new ForgeConfigSpec.BooleanValue[ActionList.ACTIONS.size()];
		private final ForgeConfigSpec.BooleanValue[] animatorPossibilities = new ForgeConfigSpec.BooleanValue[AnimatorList.ANIMATORS.size()];
		private final ForgeConfigSpec.IntValue[] staminaConsumptions = new ForgeConfigSpec.IntValue[ActionList.ACTIONS.size()];
		public final ForgeConfigSpec.BooleanValue infiniteStamina;
		public final ForgeConfigSpec.BooleanValue disableCameraRolling;
		public final ForgeConfigSpec.BooleanValue disableCameraFlipping;
		public final ForgeConfigSpec.BooleanValue disableCameraHang;
		public final ForgeConfigSpec.BooleanValue disableCameraVault;
		public final ForgeConfigSpec.BooleanValue disableCameraHorizontalWallRun;
		public final ForgeConfigSpec.BooleanValue disableCrawlInAir;
		public final ForgeConfigSpec.BooleanValue disableVaultInAir;
		public final ForgeConfigSpec.BooleanValue disableFallingAnimation;
		public final ForgeConfigSpec.BooleanValue disableAnimation;
		public final ForgeConfigSpec.BooleanValue disableFPVAnimation;
		public final ForgeConfigSpec.BooleanValue enableRollWhenCreative;
		public final ForgeConfigSpec.BooleanValue disableDoubleTappingForDodge;
		public final ForgeConfigSpec.BooleanValue substituteSprintForFastRun;
		public final ForgeConfigSpec.BooleanValue replaceSprintWithFastRun;
		public final ForgeConfigSpec.DoubleValue dodgeSpeedModifier;
		public final ForgeConfigSpec.BooleanValue parCoolActivation;
		public final ForgeConfigSpec.BooleanValue hideStaminaHUD;
		public final ForgeConfigSpec.BooleanValue useLightHUD;
		public final ForgeConfigSpec.BooleanValue useFeathers;
		public final ForgeConfigSpec.BooleanValue vaultNeedKeyPressed;
		public final ForgeConfigSpec.EnumValue<Vault.TypeSelectionMode> vaultAnimationMode;
		public final ForgeConfigSpec.EnumValue<Position.Horizontal> alignHorizontalStaminaHUD;
		public final ForgeConfigSpec.EnumValue<Position.Vertical> alignVerticalStaminaHUD;
		public final ForgeConfigSpec.EnumValue<ColorTheme> guiColorTheme;
		public final ForgeConfigSpec.IntValue marginHorizontalStaminaHUD;
		public final ForgeConfigSpec.IntValue marginVerticalStaminaHUD;
		public final ForgeConfigSpec.IntValue staminaMax;
		public final ForgeConfigSpec.IntValue staminaRecovery;
		public final ForgeConfigSpec.BooleanValue useHungerBarInsteadOfStamina;
		public final ForgeConfigSpec.DoubleValue fastRunningModifier;
		public final ForgeConfigSpec.IntValue wallRunContinuableTick;
		public final ForgeConfigSpec.IntValue slidingContinuableTick;

		Client(ForgeConfigSpec.Builder builder) {
			builder.push("Possibility of Actions(Some do not have to work)");
			{
				for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
					actionPossibilities[i] = builder.define("can_" + ActionList.ACTIONS.get(i).getSimpleName(), true);
				}
			}
			builder.pop();
			builder.push("Stamina HUD Configuration");
			{
				hideStaminaHUD = builder.comment("hide stamina HUD when Stamina is infinite").define("hide_s_hud", false);
				useLightHUD = builder.comment("use Light Stamina HUD").define("use_light_hud", true);
				alignHorizontalStaminaHUD = builder.comment("horizontal alignment").defineEnum("align_h_s_hud", Position.Horizontal.Right);
				alignVerticalStaminaHUD = builder.comment("vertical alignment").defineEnum("align_v_s_hud", Position.Vertical.Bottom);
				marginHorizontalStaminaHUD = builder.comment("horizontal margin").defineInRange("margin_h_s_hud", 3, 0, 100);
				marginVerticalStaminaHUD = builder.comment("vertical margin").defineInRange("margin_v_s_hud", 3, 0, 100);
			}
			builder.pop();
			builder.push("Animations");
			{
				disableFallingAnimation = builder.comment("Disable custom animation of falling").define("disable_falling_animation", false);
				disableAnimation = builder.comment("Disable custom animations").define("disable_animation", false);
				disableFPVAnimation = builder.comment("Disable first-person-view animations").define("disable_FPV_animation", false);
				builder.push("Animators");
				{
					for (int i = 0; i < AnimatorList.ANIMATORS.size(); i++) {
						animatorPossibilities[i] = builder.define("enable_" + AnimatorList.ANIMATORS.get(i).getSimpleName(), true);
					}
				}
				builder.pop();
				builder.push("Camera");
				{
					disableCameraRolling = builder.comment("Disable Roll rotation of camera").define("disable_camera_rotation_rolling", false);
					disableCameraFlipping = builder.comment("Disable Flipping rotation of camera").define("disable_camera_rotation_flipping", false);
					disableCameraVault = builder.comment("Disable Vault animation of camera").define("disable_camera_animation_vault", true);
					disableCameraHorizontalWallRun = builder.comment("Disable Horizontal-WallRun animation of camera").define("disable_camera_animation_h-wall-run", false);
					disableCameraHang = builder.comment("Disable Hang animation of camera").define("disable_camera_animation_hang-down", false);
				}
				builder.pop();
			}
			builder.pop();
			builder.push("Modifiers");
			{
				fastRunningModifier = builder.comment("FastRun Speed Modifier").defineInRange("fast-run_modifier", 2, 0.001, 4);
				dodgeSpeedModifier = builder.comment("Dodge Speed Modifier").defineInRange("dodge-speed_modifier", 1, 0.5, 1.5);
				wallRunContinuableTick = builder.comment("How long you can do Horizontal Wall Run").defineInRange("wall-run_continuable_tick", 25, 15, 40);
				slidingContinuableTick = builder.comment("How long you can do Slide").defineInRange("sliding_continuable_tick", 15, 10, 30);
			}
			builder.pop();
			builder.push("Other Configuration");
			{
				disableDoubleTappingForDodge = builder.comment("Disable Double-Tapping For Dodge. Please Use Dodge Key instead").define("disable_double_tapping", false);
				disableCrawlInAir = builder.comment("Disable Crawl in air (experimental)").define("disable_crawl_in_air", false);
				disableVaultInAir = builder.comment("Disable Vault in air (experimental)").define("disable_vault_in_air", false);
				enableRollWhenCreative = builder.comment("Enable Roll While player is in creative mode (experimental)").define("enable_roll_creative", false);
				vaultNeedKeyPressed = builder.comment("Make Vault Need Vault Key Pressed").define("vaultNeedKeyPressed", false);
				vaultAnimationMode = builder.comment("Vault Animation(Dynamic is to select animation dynamically)").defineEnum("vault_animation_mode", Vault.TypeSelectionMode.Dynamic);
				replaceSprintWithFastRun = builder.comment("do Fast-Running whenever you do a sprint of vanilla").define("replace_sprint_with_fast-run", true);
				substituteSprintForFastRun = builder.comment("enable players to do actions needing Fast-Running by sprint").define("substitute_sprint", false);
				guiColorTheme = builder.comment("Color theme of Setting GUI").defineEnum("gui_color_theme", ColorTheme.Blue);
				infiniteStamina = builder
						.comment("Infinite Stamina(this needs a permission from server, even if it is on single player's game. normally permitted)\nPlease check 'parcool-server.toml' in 'serverconfig' directory")
						.define("infinite_stamina", false);
			}
			builder.pop();
			builder.comment("Stamina Section (may be affected by Server config)").push("Stamina");
			{
				useHungerBarInsteadOfStamina = builder.comment("ParCool consume hanger value instead of stamina").define("use_hanger_instead", false);
				useFeathers = builder.comment("ParCool use Feathers mod as stamina system, if it installed(experimental)\n(https://www.curseforge.com/minecraft/mc-mods/feathers)").define("use_Feathers", true);
				staminaMax = builder.defineInRange("max_value_of_stamina", 2000, 300, 100000);
				staminaRecovery = builder.defineInRange("value_of_stamina_recovery", 20, 1, 10000);
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
			}
			builder.pop();
			builder.comment("About ParCool").push("ParCool");
			{
				parCoolActivation = builder.comment("ParCool is Active").define("parcool_activation", true);
			}
			builder.pop();
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

	public static final ForgeConfigSpec CLIENT_SPEC = C_BUILDER.build();
	public static final ForgeConfigSpec SERVER_SPEC = S_BUILDER.build();
}
