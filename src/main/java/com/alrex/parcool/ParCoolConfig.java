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
		public final ForgeConfigSpec.BooleanValue vaultNeedKeyPressed;
		public final ForgeConfigSpec.EnumValue<Vault.TypeSelectionMode> vaultAnimationMode;
		public final ForgeConfigSpec.EnumValue<Position.Horizontal> alignHorizontalStaminaHUD;
		public final ForgeConfigSpec.EnumValue<Position.Vertical> alignVerticalStaminaHUD;
		public final ForgeConfigSpec.EnumValue<ColorTheme> guiColorTheme;
		public final ForgeConfigSpec.IntValue marginHorizontalStaminaHUD;
		public final ForgeConfigSpec.IntValue marginVerticalStaminaHUD;
		public final ForgeConfigSpec.IntValue staminaMax;
		public final ForgeConfigSpec.BooleanValue useHungerBarInsteadOfStamina;
		public final ForgeConfigSpec.DoubleValue fastRunningModifier;
		public final ForgeConfigSpec.IntValue wallRunContinuableTick;
		public final ForgeConfigSpec.IntValue slidingContinuableTick;

		Client(ForgeConfigSpec.Builder builder) {
			builder.push("Possibility of Actions(Some do not have to work)");
			{
				for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
					actionPossibilities[i] = builder.define("can" + ActionList.ACTIONS.get(i).getSimpleName(), true);
				}
			}
			builder.pop();
			builder.push("Stamina HUD Configuration");
			{
				hideStaminaHUD = builder.comment("hide stamina HUD when Stamina is infinite").define("hideS_HUD", false);
				useLightHUD = builder.comment("use Light Stamina HUD").define("useLightHUD", true);
				alignHorizontalStaminaHUD = builder.comment("horizontal alignment").defineEnum("align_H_S_HUD", Position.Horizontal.Right);
				alignVerticalStaminaHUD = builder.comment("vertical alignment").defineEnum("align_V_S_HUD", Position.Vertical.Bottom);
				marginHorizontalStaminaHUD = builder.comment("horizontal margin").defineInRange("margin_H_S_HUD", 3, 0, 100);
				marginVerticalStaminaHUD = builder.comment("vertical margin").defineInRange("margin_V_S_HUD", 3, 0, 100);
			}
			builder.pop();
			builder.push("Animations");
			{
				disableFallingAnimation = builder.comment("Disable custom animation of falling").define("disableFallingAnimation", false);
				disableAnimation = builder.comment("Disable custom animations").define("disableAnimation", false);
				disableFPVAnimation = builder.comment("Disable first-person-view animations").define("disableFPVAnimation", false);
				builder.push("Animators");
				{
					for (int i = 0; i < AnimatorList.ANIMATORS.size(); i++) {
						animatorPossibilities[i] = builder.define("enable" + AnimatorList.ANIMATORS.get(i).getSimpleName(), true);
					}
				}
				builder.pop();
				builder.push("Camera");
				{
					disableCameraRolling = builder.comment("Disable Roll rotation of camera").define("disableCameraRotationRolling", false);
					disableCameraFlipping = builder.comment("Disable Flipping rotation of camera").define("disableCameraRotationFlipping", false);
					disableCameraVault = builder.comment("Disable Vault animation of camera").define("disableCameraAnimationVault", true);
					disableCameraHorizontalWallRun = builder.comment("Disable Horizontal-WallRun animation of camera").define("disableCameraAnimationH_WallRun", false);
					disableCameraHang = builder.comment("Disable Hang animation of camera").define("disableCameraAnimationHang", false);
				}
				builder.pop();
			}
			builder.pop();
			builder.push("Modifiers");
			{
				fastRunningModifier = builder.comment("FastRun Speed Modifier").defineInRange("fastRunModifier", 3, 0.001, 4.5);
				dodgeSpeedModifier = builder.comment("Dodge Speed Modifier").defineInRange("dodgeSpeedModifier", 1, 0.5, 1.5);
				wallRunContinuableTick = builder.comment("How long you can do Horizontal Wall Run").defineInRange("wallRunContinuableTick", 25, 15, 40);
				slidingContinuableTick = builder.comment("How long you can do Slide").defineInRange("slidingContinuableTick", 15, 10, 30);
			}
			builder.pop();
			builder.push("Other Configuration");
			{
				disableDoubleTappingForDodge = builder.comment("Disable Double-Tapping For Dodge. Please Use Dodge Key instead").define("disableDoubleTapping", false);
				disableCrawlInAir = builder.comment("Disable Crawl in air (experimental)").define("disableCrawlInAir", true);
				disableVaultInAir = builder.comment("Disable Vault in air (experimental)").define("disableVaultInAir", true);
				enableRollWhenCreative = builder.comment("Enable Roll While player is in creative mode (experimental)").define("enableRollCreative", false);
				vaultNeedKeyPressed = builder.comment("Make Vault Need Vault Key Pressed").define("vaultNeedKeyPressed", false);
				vaultAnimationMode = builder.comment("Vault Animation(Dynamic is to select animation dynamically)").defineEnum("vaultAnimationMode", Vault.TypeSelectionMode.Dynamic);
				replaceSprintWithFastRun = builder.comment("do Fast-Running whenever you do a sprint of vanilla").define("replaceSprintWithFastRun", true);
				substituteSprintForFastRun = builder.comment("enable players to do actions needing Fast-Running by sprint").define("substituteSprint", false);
				guiColorTheme = builder.comment("Color theme of Setting GUI").defineEnum("guiColorTheme", ColorTheme.Blue);
				infiniteStamina = builder
						.comment("Infinite Stamina(this needs a permission from server, even if it is on single player's game)\nPlease check 'parcool-server.toml' in 'serverconfig' directory")
						.define("infiniteStamina", false);
			}
			builder.pop();
			builder.comment("Stamina Section may be affected by Server config").push("Stamina");
			{
				useHungerBarInsteadOfStamina = builder.comment("ParCool consume hanger value instead of stamina").define("useHangerInstead", false);
				staminaMax = builder.defineInRange("MaxValueOfStamina", 2000, 300, 10000);
				builder.push("Consumption");
				{
					for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
						staminaConsumptions[i]
								= builder.defineInRange(
								"staminaConsumptionOf" + ActionList.ACTIONS.get(i).getSimpleName(),
								ActionList.ACTION_REGISTRIES.get(i).getDefaultStaminaConsumption(),
								0, 10000
						);
					}
				}
			}
			builder.pop();
			builder.comment("About ParCool").push("ParCool");
			{
				parCoolActivation = builder.comment("ParCool is Active").define("ParCool_Activation", true);
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
		public final ForgeConfigSpec.BooleanValue enforced;

		Server(ForgeConfigSpec.Builder builder) {
			enforced = builder.comment("Whether these limitations will be imposed to players").define("limitationsImposed", false);
			builder.push("Action Permissions");
			{
				for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
					actionPermissions[i]
							= builder.define("permit" + ActionList.ACTIONS.get(i).getSimpleName(), true);
				}
			}
			builder.pop();
			builder.push("Stamina");
			{
				staminaMax = builder.defineInRange("Max Value of Stamina", 2000, 300, 10000);
				allowInfiniteStamina = builder.comment("allow Infinite Stamina").define("infiniteStamina", true);
				builder.push("Least Consumption");
				{
					for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
						leastStaminaConsumptions[i]
								= builder.defineInRange(
								"staminaConsumptionOf" + ActionList.ACTIONS.get(i).getSimpleName(),
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
