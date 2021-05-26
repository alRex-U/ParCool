package com.alrex.parcool.client.renderer;

import com.alrex.parcool.common.capability.IDodge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class PlayerDodgeRenderer {
	public static void onRender(RenderPlayerEvent.Pre event) {
		PlayerEntity player = event.getPlayer();
		IDodge dodge = IDodge.get(player);
		if (dodge == null) return;

		if (dodge.isDodging() && dodge.getDirection() == IDodge.DodgeDirection.Back) {
			Vector3d lookVec = player.getLookVec().rotateYaw((float) Math.PI / 2);
			Vector3f vec = new Vector3f((float) lookVec.getX(), 0, (float) lookVec.getZ());

			event.getMatrixStack().translate(0, player.getHeight() / 2, 0);
			event.getMatrixStack().rotate(vec.rotationDegrees((dodge.getDodgingTime() + event.getPartialRenderTick()) * -30));
			event.getMatrixStack().translate(0, -player.getHeight() / 2, 0);
		} else if (dodge.isDodging() && dodge.getDirection() == IDodge.DodgeDirection.Front) {
			Vector3d lookVec = player.getLookVec().rotateYaw((float) Math.PI / 2);
			Vector3f vec = new Vector3f((float) lookVec.getX(), 0, (float) lookVec.getZ());

			event.getMatrixStack().translate(0, player.getHeight() / 2, 0);
			event.getMatrixStack().rotate(vec.rotationDegrees((dodge.getDodgingTime() + event.getPartialRenderTick()) * 30));
			event.getMatrixStack().translate(0, -player.getHeight() / 2, 0);
		}
	}
}
