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
		public final ForgeConfigSpec.BooleanValue infiniteStamina;
		public final ForgeConfigSpec.BooleanValue autoTurningWallJump;
		public final ForgeConfigSpec.BooleanValue disableWallJumpTowardWall;
		public final ForgeConfigSpec.BooleanValue disableCameraRolling;
		public final ForgeConfigSpec.BooleanValue disableCameraDodge;
		public final ForgeConfigSpec.BooleanValue substituteSprintForFastRun;
		public final ForgeConfigSpec.DoubleValue fastRunningModifier;
		public final ForgeConfigSpec.BooleanValue parCoolActivation;
		public final ForgeConfigSpec.BooleanValue hideStaminaHUD;
		public final ForgeConfigSpec.BooleanValue useLightHUD;
		public final ForgeConfigSpec.EnumValue<Position.Horizontal> alignHorizontalStaminaHUD;
		public final ForgeConfigSpec.EnumValue<Position.Vertical> alignVerticalStaminaHUD;
		public final ForgeConfigSpec.IntValue marginHorizontalStaminaHUD;
		public final ForgeConfigSpec.IntValue marginVerticalStaminaHUD;
		public final ForgeConfigSpec.IntValue offsetVerticalLightStaminaHUD;

		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("Enable ParCool Actions").push("Possibility of Actions");
			{
				canCatLeap = builder.comment("Possibility to CatLeap").define("canCatLeap", true);
				canCrawl = builder.comment("Possibility to Crawl").define("canCrawl", true);
				canFrontFlip = builder.comment("Possibility to FrontFlip").define("canFrontFlip", true);
				canDodge = builder.comment("Possibility to Dodge").define("canDodge", true);
				canFastRunning = builder.comment("Possibility to FastRunning").define("canFastRunning", true);
				canClingToCliff = builder.comment("Possibility to ClingToCliff").define("canClingToCliff", true);
				canRoll = builder.comment("Possibility to Roll").define("canRoll", true);
				canVault = builder.comment("Possibility to Vault").define("canVault", true);
				canWallJump = builder.comment("Possibility to WallJump").define("canWallJump", true);
			}
			builder.pop();
			builder.comment("Values").push("Modifier Values");
			{
				fastRunningModifier = builder.comment("FastRun Speed Modifier").defineInRange("fastRunModifier", 5, 0.001, 5);
			}
			builder.pop();
			builder.comment("HUD").push("Stamina HUD configuration");
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
			builder.comment("Others").push("Other configuration");
			{
				autoTurningWallJump = builder.comment("Auto turning forward when WallJump").define("autoTurningWallJump", false);
				disableWallJumpTowardWall = builder.comment("Disable WallJump toward a wall").define("disableWallJumpTowardWall", false);
				disableCameraRolling = builder.comment("Disable Roll rotation of camera").define("disableCameraRotationRolling", false);
				disableCameraDodge = builder.comment("Disable Dodge rotation of camera").define("disableCameraRotationDodge", false);
				substituteSprintForFastRun = builder.comment("Substitute a sprint of vanilla for the FastRunning").define("substituteSprint", false);
				infiniteStamina = builder
						.comment("Infinite Stamina(this needs a permission from server, even if it is on single player's game)\nPlease check 'parcool-server.toml' in 'serverconfig' directory")
						.define("infiniteStamina", false);
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

		Server(ForgeConfigSpec.Builder builder) {
			builder.comment("Action Permissions").push("Permissions");
			{
				allowCatLeap = builder.comment("allow CatLeap").define("allowCatLeap", true);
				allowCrawl = builder.comment("allow Crawl").define("allowCrawl", true);
				allowDodge = builder.comment("allow Dodge").define("allowDodge", true);
				allowFastRunning = builder.comment("allow FastRunning").define("allowFastRunning", true);
				allowClingToCliff = builder.comment("allow ClingToCliff").define("allowClingToCliff", true);
				allowRoll = builder.comment("allow Roll").define("allowRoll", true);
				allowVault = builder.comment("allow Vault").define("allowVault", true);
				allowWallJump = builder.comment("allow WallJump").define("allow WallJump", true);
			}
			builder.pop();
			builder.comment("Others").push("Other Configuration");
			{
				allowInfiniteStamina = builder.comment("allow Infinite Stamina").define("infiniteStamina", false);
			}
			builder.pop();
		}
	}

	public static final ForgeConfigSpec CLIENT_SPEC = C_BUILDER.build();
	public static final ForgeConfigSpec SERVER_SPEC = S_BUILDER.build();
}
