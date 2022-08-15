package com.alrex.parcool;

import com.alrex.parcool.client.hud.Position;
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
		public final ForgeConfigSpec.BooleanValue canFrontFlip;
		public final ForgeConfigSpec.BooleanValue canClingToCliff;
		public final ForgeConfigSpec.BooleanValue canRoll;
		public final ForgeConfigSpec.BooleanValue canVault;
		public final ForgeConfigSpec.BooleanValue canWallJump;
		public final ForgeConfigSpec.BooleanValue canDive;
		public final ForgeConfigSpec.BooleanValue canFlipping;
		public final ForgeConfigSpec.BooleanValue canBreakfall;
		public final ForgeConfigSpec.BooleanValue infiniteStamina;
		public final ForgeConfigSpec.BooleanValue autoTurningWallJump;
		public final ForgeConfigSpec.BooleanValue disableWallJumpTowardWall;
		public final ForgeConfigSpec.BooleanValue disableCameraRolling;
		public final ForgeConfigSpec.BooleanValue disableCameraFlipping;
		public final ForgeConfigSpec.BooleanValue disableDoubleTappingForDodge;
		public final ForgeConfigSpec.BooleanValue substituteSprintForFastRun;
		public final ForgeConfigSpec.BooleanValue replaceSprintWithFastRun;
		public final ForgeConfigSpec.BooleanValue creativeFlyingLikeSuperMan;
		public final ForgeConfigSpec.DoubleValue fastRunningModifier;
		public final ForgeConfigSpec.BooleanValue parCoolActivation;
		public final ForgeConfigSpec.BooleanValue hideStaminaHUD;
		public final ForgeConfigSpec.BooleanValue useLightHUD;
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

		Client(ForgeConfigSpec.Builder builder) {
			builder.push("Possibility of Actions");
			{
				canCatLeap = builder.define("canCatLeap", true);
				canCrawl = builder.define("canCrawl", true);
				canFrontFlip = builder.define("canFrontFlip", true);
				canDodge = builder.define("canDodge", true);
				canFastRunning = builder.define("canFastRunning", true);
				canClingToCliff = builder.define("canClingToCliff", true);
				canRoll = builder.define("canRoll", true);
				canVault = builder.define("canVault", true);
				canWallJump = builder.define("canWallJump", true);
				canDive = builder.define("canDive", true);
				canFlipping = builder.define("canFlipping", true);
				canBreakfall = builder.define("canBreakFall", true);
			}
			builder.pop();
			builder.push("Modifier Values");
			{
				fastRunningModifier = builder.comment("FastRun Speed Modifier").defineInRange("fastRunModifier", 3, 0.001, 3);
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
			builder.push("Other Configuration");
			{
				autoTurningWallJump = builder.comment("Auto turning forward when WallJump").define("autoTurningWallJump", false);
				disableWallJumpTowardWall = builder.comment("Disable WallJump toward a wall").define("disableWallJumpTowardWall", false);
				disableCameraRolling = builder.comment("Disable Roll rotation of camera").define("disableCameraRotationRolling", false);
				disableCameraFlipping = builder.comment("Disable Flipping rotation of camera").define("disableCameraRotationFlipping", false);
				disableDoubleTappingForDodge = builder.comment("Disable Double-Tapping For Dodge. Please Use Dodge Key instead").define("disableDoubleTapping", false);
				replaceSprintWithFastRun = builder.comment("Replace vanilla sprint with Fast-Running").define("replaceSprintWithFastRun", true);
				substituteSprintForFastRun = builder.comment("Substitute a sprint of vanilla for the FastRunning").define("substituteSprint", false);
				creativeFlyingLikeSuperMan = builder.comment("Can creative-fly like super-man(experimental)").define("creativeFlyLikeSuperMan", false);
				infiniteStamina = builder
						.comment("Infinite Stamina(this needs a permission from server, even if it is on single player's game)\nPlease check 'parcool-server.toml' in 'serverconfig' directory")
						.define("infiniteStamina", false);
			}
			builder.pop();
			builder.comment("Stamina Section is affected by Server config").push("Stamina");
			{
				staminaMax = builder.defineInRange("Max Value of Stamina", 2000, 300, 10000);
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

		Server(ForgeConfigSpec.Builder builder) {
			builder.push("Action Permissions");
			{
				allowCatLeap = builder.comment("allow CatLeap").define("allowCatLeap", true);
				allowCrawl = builder.comment("allow Crawl").define("allowCrawl", true);
				allowDodge = builder.comment("allow Dodge").define("allowDodge", true);
				allowFastRunning = builder.comment("allow FastRunning").define("allowFastRunning", true);
				allowClingToCliff = builder.comment("allow ClingToCliff").define("allowClingToCliff", true);
				allowRoll = builder.comment("allow Roll").define("allowRoll", true);
				allowVault = builder.comment("allow Vault").define("allowVault", true);
				allowWallJump = builder.comment("allow WallJump").define("allowWallJump", true);
				allowBreakfall = builder.comment("allow Breakfall").define("allowBreakfall", true);
				allowFlipping = builder.comment("allow Flipping").define("allowFlipping", true);
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
