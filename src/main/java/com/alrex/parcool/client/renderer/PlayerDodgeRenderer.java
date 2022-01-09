package com.alrex.parcool.client.renderer;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class PlayerDodgeRenderer {
	public static void onRender(RenderPlayerEvent.Pre event) {
		PlayerEntity player = event.getPlayer();

		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		Dodge dodge = parkourability.getDodge();

		if (dodge.isDodging() && dodge.getDodgeDirection() == Dodge.DodgeDirections.Back) {
			Vector3d lookVec = player.getLookVec().rotateYaw((float) Math.PI / 2);
			Vector3f vec = new Vector3f((float) lookVec.getX(), 0, (float) lookVec.getZ());

			event.getMatrixStack().translate(0, player.getHeight() / 2, 0);
			event.getMatrixStack().rotate(vec.rotationDegrees((dodge.getDodgingTick() + event.getPartialRenderTick()) * -30));
			event.getMatrixStack().translate(0, -player.getHeight() / 2, 0);
		} else if (dodge.isDodging() && dodge.getDodgeDirection() == Dodge.DodgeDirections.Front) {
			Vector3d lookVec = player.getLookVec().rotateYaw((float) Math.PI / 2);
			Vector3f vec = new Vector3f((float) lookVec.getX(), 0, (float) lookVec.getZ());

			event.getMatrixStack().translate(0, player.getHeight() / 2, 0);
			event.getMatrixStack().rotate(vec.rotationDegrees((dodge.getDodgingTick() + event.getPartialRenderTick()) * 30));
			event.getMatrixStack().translate(0, -player.getHeight() / 2, 0);
		}
	}
}
