package com.alrex.parcool;

import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.action.impl.Vault;
import net.minecraftforge.common.ForgeConfigSpec;

public class ParCoolConfig {
	private static final ForgeConfigSpec.Builder C_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder S_BUILDER = new ForgeConfigSpec.Builder();

	public static final Client CONFIG_CLIENT = new Client(C_BUILDER);
	public static final Server CONFIG_SERVER = new Server(S_BUILDER);
	public static class Client {
		public final ForgeConfigSpec.BooleanValue canCatLeap;
		public final ForgeConfigSpec.BooleanValue canCrawl;
		public final ForgeConfigSpec.BooleanValue canDodge;
		public final ForgeConfigSpec.BooleanValue canFastRunning;
		public final ForgeConfigSpec.BooleanValue canFrontDodgeByDoubleTap;
		public final ForgeConfigSpec.BooleanValue canClingToCliff;
		public final ForgeConfigSpec.BooleanValue canRoll;
		public final ForgeConfigSpec.BooleanValue canVault;
		public final ForgeConfigSpec.BooleanValue canWallJump;
		public final ForgeConfigSpec.BooleanValue canDive;
		public final ForgeConfigSpec.BooleanValue canFlipping;
		public final ForgeConfigSpec.BooleanValue canBreakfall;
		public final ForgeConfigSpec.BooleanValue canWallSlide;
		public final ForgeConfigSpec.BooleanValue canHorizontalWallRun;
		public final ForgeConfigSpec.BooleanValue infiniteStamina;
		public final ForgeConfigSpec.BooleanValue autoTurningWallJump;
		public final ForgeConfigSpec.BooleanValue disableWallJumpTowardWall;
		public final ForgeConfigSpec.BooleanValue disableCameraRolling;
		public final ForgeConfigSpec.BooleanValue disableCameraFlipping;
		public final ForgeConfigSpec.BooleanValue disableCameraVault;
		public final ForgeConfigSpec.BooleanValue disableCameraHorizontalWallRun;
		public final ForgeConfigSpec.BooleanValue disableCrawlInAir;
		public final ForgeConfigSpec.BooleanValue disableVaultInAir;
		public final ForgeConfigSpec.BooleanValue disableFallingAnimation;
		public final ForgeConfigSpec.BooleanValue disableAnimation;
		public final ForgeConfigSpec.BooleanValue disableFPVAnimation;
		public final ForgeConfigSpec.BooleanValue disableCatLeapAnimation;
		public final ForgeConfigSpec.BooleanValue disableClimbUpAnimation;
		public final ForgeConfigSpec.BooleanValue disableClingToCliffAnimation;
		public final ForgeConfigSpec.BooleanValue disableCrawlAnimation;
		public final ForgeConfigSpec.BooleanValue disableDiveAnimation;
		public final ForgeConfigSpec.BooleanValue disableDodgeAnimation;
		public final ForgeConfigSpec.BooleanValue disableFastRunAnimation;
		public final ForgeConfigSpec.BooleanValue disableFlippingAnimation;
		public final ForgeConfigSpec.BooleanValue disableHorizontalWallRunAnimation;
		public final ForgeConfigSpec.BooleanValue disableVaultAnimation;
		public final ForgeConfigSpec.BooleanValue disableBreakfallAnimation;
		public final ForgeConfigSpec.BooleanValue disableSlidingAnimation;
		public final ForgeConfigSpec.BooleanValue disableWallJumpAnimation;
		public final ForgeConfigSpec.BooleanValue disableWallSlideAnimation;
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
		public final ForgeConfigSpec.IntValue marginHorizontalStaminaHUD;
		public final ForgeConfigSpec.IntValue marginVerticalStaminaHUD;
		public final ForgeConfigSpec.IntValue offsetVerticalLightStaminaHUD;
		public final ForgeConfigSpec.IntValue staminaMax;
		public final ForgeConfigSpec.IntValue staminaConsumptionBreakfall;
		public final ForgeConfigSpec.IntValue staminaConsumptionCatLeap;
		public final ForgeConfigSpec.IntValue staminaConsumptionClingToCliff;
		public final ForgeConfigSpec.IntValue staminaConsumptionClimbUp;
		public final ForgeConfigSpec.IntValue staminaConsumptionDodge;
		public final ForgeConfigSpec.IntValue staminaConsumptionFastRun;
		public final ForgeConfigSpec.IntValue staminaConsumptionFlipping;
		public final ForgeConfigSpec.IntValue staminaConsumptionVault;
		public final ForgeConfigSpec.IntValue staminaConsumptionWallJump;
		public final ForgeConfigSpec.BooleanValue useHungerBarInsteadOfStamina;

		Client(ForgeConfigSpec.Builder builder) {
			builder.push("Possibility of Actions");
			{
				canCatLeap = builder.define("canCatLeap", true);
				canCrawl = builder.define("canCrawl", true);
				canFrontDodgeByDoubleTap = builder.comment("Possibility to Frontward-Dodge By double tapping a button").define("canFrontDodgeByDoubleTapping", true);
				canDodge = builder.define("canDodge", true);
				canFastRunning = builder.define("canFastRunning", true);
				canClingToCliff = builder.define("canClingToCliff", true);
				canRoll = builder.define("canRoll", true);
				canVault = builder.define("canVault", true);
				canWallJump = builder.define("canWallJump", true);
				canDive = builder.define("canDive", true);
				canFlipping = builder.define("canFlipping", true);
				canBreakfall = builder.define("canBreakFall", true);
				canWallSlide = builder.define("canWallSlide", true);
				canHorizontalWallRun = builder.define("canHorizontalWallRun", true);
			}
			builder.pop();
			builder.push("Modifier Values");
			{
				dodgeSpeedModifier = builder.comment("Dodge Speed Modifier").defineInRange("dodgeSpeedModifier", 0.4, 0.2, 0.52);
			}
			builder.pop();
			builder.push("Stamina HUD Configuration");
			{
				hideStaminaHUD = builder.comment("hide stamina HUD when Stamina is infinite").define("hideS_HUD", false);
				useLightHUD = builder.comment("use Light Stamina HUD").define("useLightHUD", false);
				alignHorizontalStaminaHUD = builder.comment("horizontal alignment").defineEnum("align_H_S_HUD", Position.Horizontal.Right);
				alignVerticalStaminaHUD = builder.comment("vertical alignment").defineEnum("align_V_S_HUD", Position.Vertical.Bottom);
				marginHorizontalStaminaHUD = builder.comment("horizontal margin").defineInRange("margin_H_S_HUD", 3, 0, 100);
				marginVerticalStaminaHUD = builder.comment("vertical margin").defineInRange("margin_V_S_HUD", 3, 0, 100);
				offsetVerticalLightStaminaHUD = builder.comment("vertical offset of light stamina HUD").defineInRange("offset_V_LS_HUD", 0, -50, 50);
			}
			builder.pop();
			builder.push("Animations");
			{
				disableFallingAnimation = builder.comment("Disable custom animation of falling").define("disableFallingAnimation", false);
				disableAnimation = builder.comment("Disable custom animations").define("disableAnimation", false);
				disableFPVAnimation = builder.comment("Disable first-person-view animations").define("disableFPVAnimation", false);
				disableCatLeapAnimation = builder.define("disableCatLeapAnimation", false);
				disableClimbUpAnimation = builder.define("disableClimbUpAnimation", false);
				disableClingToCliffAnimation = builder.define("disableClingToCliffAnimation", false);
				disableCrawlAnimation = builder.define("disableCrawlAnimation", false);
				disableDiveAnimation = builder.define("disableDiveAnimation", false);
				disableDodgeAnimation = builder.define("disableDodgeAnimation", false);
				disableFastRunAnimation = builder.define("disableFastRunAnimation", false);
				disableFlippingAnimation = builder.define("disableFlippingAnimation", false);
				disableHorizontalWallRunAnimation = builder.define("disableHorizontalWallRunAnimation", false);
				disableVaultAnimation = builder.define("disableVaultAnimation", false);
				disableBreakfallAnimation = builder.define("disableBreakfallAnimation", false);
				disableSlidingAnimation = builder.define("disableSlidingAnimation", false);
				disableWallJumpAnimation = builder.define("disableWallJumpAnimation", false);
				disableWallSlideAnimation = builder.define("disableWallSlideAnimation", false);
				disableCameraRolling = builder.comment("Disable Roll rotation of camera").define("disableCameraRotationRolling", false);
				disableCameraFlipping = builder.comment("Disable Flipping rotation of camera").define("disableCameraRotationFlipping", false);
				disableCameraVault = builder.comment("Disable Vault animation of camera").define("disableCameraAnimationVault", true);
				disableCameraHorizontalWallRun = builder.comment("Disable Horizontal-WallRun animation of camera").define("disableCameraAnimationH_WallRun", false);
			}
			builder.pop();
			builder.push("Other Configuration");
			{
				autoTurningWallJump = builder.comment("Auto turning forward when WallJump").define("autoTurningWallJump", false);
				disableWallJumpTowardWall = builder.comment("Disable WallJump toward a wall").define("disableWallJumpTowardWall", false);
				disableDoubleTappingForDodge = builder.comment("Disable Double-Tapping For Dodge. Please Use Dodge Key instead").define("disableDoubleTapping", false);
				disableCrawlInAir = builder.comment("Disable Crawl in air (experimental)").define("disableCrawlInAir", true);
				disableVaultInAir = builder.comment("Disable Vault in air (experimental)").define("disableVaultInAir", true);
				enableRollWhenCreative = builder.comment("Enable Roll While player is in creative mode (experimental)").define("enableRollCreative", false);
				vaultNeedKeyPressed = builder.comment("Make Vault Need Vault Key Pressed").define("vaultNeedKeyPressed", false);
				vaultAnimationMode = builder.comment("Vault Animation(Dynamic is to select animation dynamically)").defineEnum("vaultAnimationMode", Vault.TypeSelectionMode.Dynamic);
				replaceSprintWithFastRun = builder.comment("do Fast-Running whenever you do a sprint of vanilla").define("replaceSprintWithFastRun", true);
				substituteSprintForFastRun = builder.comment("enable players to do actions needing Fast-Running by sprint").define("substituteSprint", false);
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
					staminaConsumptionBreakfall = builder.defineInRange("Breakfall", 100, 0, 10000);
					staminaConsumptionCatLeap = builder.defineInRange("CatLeap", 150, 0, 10000);
					staminaConsumptionClingToCliff = builder.defineInRange("ClingToCliff", 2, 0, 10000);
					staminaConsumptionClimbUp = builder.defineInRange("ClimbUp", 150, 0, 10000);
					staminaConsumptionDodge = builder.defineInRange("Dodge", 80, 0, 10000);
					staminaConsumptionFastRun = builder.defineInRange("FastRunning", 2, 0, 10000);
					staminaConsumptionFlipping = builder.defineInRange("Flipping", 80, 0, 10000);
					staminaConsumptionVault = builder.defineInRange("Vault", 50, 0, 10000);
					staminaConsumptionWallJump = builder.defineInRange("WallJump", 120, 0, 10000);
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
		public final ForgeConfigSpec.BooleanValue allowInfiniteStamina;
		public final ForgeConfigSpec.BooleanValue allowCatLeap;
		public final ForgeConfigSpec.BooleanValue allowCrawl;
		public final ForgeConfigSpec.BooleanValue allowDodge;
		public final ForgeConfigSpec.BooleanValue allowFastRunning;
		public final ForgeConfigSpec.BooleanValue allowClingToCliff;
		public final ForgeConfigSpec.BooleanValue allowRoll;
		public final ForgeConfigSpec.BooleanValue allowVault;
		public final ForgeConfigSpec.BooleanValue allowWallJump;
		public final ForgeConfigSpec.BooleanValue allowBreakfall;
		public final ForgeConfigSpec.BooleanValue allowFlipping;
		public final ForgeConfigSpec.BooleanValue allowWallSlide;
		public final ForgeConfigSpec.BooleanValue allowHorizontalWallRun;
		public final ForgeConfigSpec.IntValue staminaMax;
		public final ForgeConfigSpec.IntValue staminaConsumptionBreakfall;
		public final ForgeConfigSpec.IntValue staminaConsumptionCatLeap;
		public final ForgeConfigSpec.IntValue staminaConsumptionClingToCliff;
		public final ForgeConfigSpec.IntValue staminaConsumptionClimbUp;
		public final ForgeConfigSpec.IntValue staminaConsumptionDodge;
		public final ForgeConfigSpec.IntValue staminaConsumptionFastRun;
		public final ForgeConfigSpec.IntValue staminaConsumptionFlipping;
		public final ForgeConfigSpec.IntValue staminaConsumptionVault;
		public final ForgeConfigSpec.IntValue staminaConsumptionWallJump;
		public final ForgeConfigSpec.DoubleValue fastRunningModifier;

		Server(ForgeConfigSpec.Builder builder) {
			builder.push("Action Permissions");
			{
				allowCatLeap = builder.define("allowCatLeap", true);
				allowCrawl = builder.define("allowCrawl", true);
				allowDodge = builder.define("allowDodge", true);
				allowFastRunning = builder.define("allowFastRunning", true);
				allowClingToCliff = builder.define("allowClingToCliff", true);
				allowRoll = builder.define("allowRoll", true);
				allowVault = builder.define("allowVault", true);
				allowWallJump = builder.define("allowWallJump", true);
				allowBreakfall = builder.define("allowBreakfall", true);
				allowFlipping = builder.define("allowFlipping", true);
				allowWallSlide = builder.define("allowWallSlide", true);
				allowHorizontalWallRun = builder.define("allowHorizontalWallRun", true);
			}
			builder.pop();
			builder.push("modifiers");
			{
				fastRunningModifier = builder.comment("FastRun Speed Modifier(Planned to be made to be changeable by each client player?)").defineInRange("fastRunModifier", 3, 0.001, 4.5);
			}
			builder.pop();
			builder.push("Stamina");
			{
				staminaMax = builder.defineInRange("Max Value of Stamina", 2000, 300, 10000);
				allowInfiniteStamina = builder.comment("allow Infinite Stamina").define("infiniteStamina", true);
				builder.push("Consumption");
				{
					staminaConsumptionBreakfall = builder.defineInRange("Breakfall", 100, 0, 10000);
					staminaConsumptionCatLeap = builder.defineInRange("CatLeap", 150, 0, 10000);
					staminaConsumptionClingToCliff = builder.defineInRange("ClingToCliff", 2, 0, 10000);
					staminaConsumptionClimbUp = builder.defineInRange("ClimbUp", 150, 0, 10000);
					staminaConsumptionDodge = builder.defineInRange("Dodge", 80, 0, 10000);
					staminaConsumptionFastRun = builder.defineInRange("FastRunning", 2, 0, 10000);
					staminaConsumptionFlipping = builder.defineInRange("Flipping", 80, 0, 10000);
					staminaConsumptionVault = builder.defineInRange("Vault", 50, 0, 10000);
					staminaConsumptionWallJump = builder.defineInRange("WallJump", 120, 0, 10000);
				}
			}
			builder.pop();
		}
	}

	public static final ForgeConfigSpec CLIENT_SPEC = C_BUILDER.build();
	public static final ForgeConfigSpec SERVER_SPEC = S_BUILDER.build();
}
