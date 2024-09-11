package com.alrex.parcool.api;


import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.stamina.ReadonlyStamina;
import com.alrex.parcool.common.stamina.LocalStamina;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Stamina {
    public static Stamina get(Player player) {
		return new Stamina(player.getData(Attachments.STAMINA), player.isLocalPlayer());
	}

	private final ReadonlyStamina staminaInstance;
	private final boolean isLocalPlayer;

	private Stamina(ReadonlyStamina staminaInstance, boolean local) {
		this.staminaInstance = staminaInstance;
		isLocalPlayer = local;
	}

	public int getMaxValue() {
		return staminaInstance.max();
	}

	public int getValue() {
		return staminaInstance.value();
	}

	public boolean isExhausted() {
		return staminaInstance.isExhausted();
	}

	@OnlyIn(Dist.CLIENT)
	public void consume(int value) {
		if (!isLocalPlayer) return;
		var stamina = LocalStamina.get();
		if (stamina == null) return;
		stamina.consume(value);
	}

	@OnlyIn(Dist.CLIENT)
	public void recover(int value) {
		if (!isLocalPlayer) return;
		var stamina = LocalStamina.get();
		if (stamina == null) return;
		stamina.recover(value);
	}
}
