package com.alrex.parcool.api;


import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Stamina {
    public static Stamina get(Player player) {
		return new Stamina(player);
	}

	private final Player player;

	private Stamina(Player player) {
		this.player = player;
	}

	public int getMaxValue() {
		return player.getData(Attachments.STAMINA).max();
	}

	public int getValue() {
		return player.getData(Attachments.STAMINA).value();
	}

	public boolean isExhausted() {
		return player.getData(Attachments.STAMINA).isExhausted();
	}

	@OnlyIn(Dist.CLIENT)
	public void consume(int value) {
		if (!(player instanceof LocalPlayer localPlayer)) return;
		var stamina = LocalStamina.get(localPlayer);
		stamina.consume(localPlayer, value);
	}

	@OnlyIn(Dist.CLIENT)
	public void recover(int value) {
		if (!(player instanceof LocalPlayer localPlayer)) return;
		var stamina = LocalStamina.get(localPlayer);
		stamina.recover(localPlayer, value);
	}
}
