package com.alrex.parcool;

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
		public final ForgeConfigSpec.BooleanValue canGrabCliff;
		public final ForgeConfigSpec.BooleanValue canRoll;
		public final ForgeConfigSpec.BooleanValue canVault;
		public final ForgeConfigSpec.BooleanValue canWallJump;
		public final ForgeConfigSpec.BooleanValue infiniteStamina;
		public final ForgeConfigSpec.IntValue maxStamina;
		public final ForgeConfigSpec.BooleanValue ParCoolActivation;
		public final ForgeConfigSpec.BooleanValue hideStaminaHUD;
		public final ForgeConfigSpec.BooleanValue hideStaminaHUDWhileInfinite;

		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("Enable ParCool Actions").push("Possibility of Actions");
			{
				canCatLeap = builder.comment("Possibility to CatLeap").define("canCatLeap", true);
				canCrawl = builder.comment("Possibility to Crawl").define("canCrawl", true);
				canFrontFlip = builder.comment("Possibility to FrontFlip").define("canFrontFlip", true);
				canDodge = builder.comment("Possibility to Dodge").define("canDodge", true);
				canFastRunning = builder.comment("Possibility to FastRunning").define("canFastRunning", true);
				canGrabCliff = builder.comment("Possibility to GrabCliff").define("canGrabCliff", true);
				canRoll = builder.comment("Possibility to Roll").define("canRoll", true);
				canVault = builder.comment("Possibility to Vault").define("canVault", true);
				canWallJump = builder.comment("Possibility to WallJump").define("canWallJump", true);
			}
			builder.pop();
			builder.comment("Values").push("Modify Values");
			{
				maxStamina = builder.comment("Max Value of Stamina").defineInRange("maxStamina", 1000, 10, 5000);
			}
			builder.pop();
			builder.comment("HUD").push("HUD configuration");
			{
				hideStaminaHUD = builder.comment("hide stamina HUD").define("hideStaminaHUD", false);
				hideStaminaHUDWhileInfinite = builder.comment("hide stamina HUD while infinite stamina is enabled").define("hideStaminaHUDWhileInfinite", false);
			}
			builder.pop();
			builder.comment("Others").push("Other configuration");
			{
				infiniteStamina = builder.comment("Infinite Stamina").define("infiniteStamina", false);
			}
			builder.pop();
			builder.comment("About ParCool").push("ParCool");
			{
				ParCoolActivation = builder.comment("ParCool is Active").define("ParCool_Activation", true);
			}
			builder.pop();
		}
	}

	public static class Server {
		public final ForgeConfigSpec.BooleanValue allowInfiniteStamina;

		Server(ForgeConfigSpec.Builder builder) {
			builder.comment("Others").push("Other Configuration");
			{
				allowInfiniteStamina = builder.comment("allow Infinite Stamina").define("infiniteStamina", true);
			}
		}
	}

	public static final ForgeConfigSpec CLIENT_SPEC = C_BUILDER.build();
	public static final ForgeConfigSpec SERVER_SPEC = S_BUILDER.build();
}
