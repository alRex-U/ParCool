package com.alrex.parcool.extern.controllable;

import com.alrex.parcool.client.input.KeyBindings;
import com.mrcrayfish.controllable.client.BindingRegistry;
import com.mrcrayfish.controllable.client.Buttons;
import com.mrcrayfish.controllable.client.KeyAdapterBinding;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.TreeMap;

@OnlyIn(Dist.CLIENT)
public class ButtonAdapterBindings {
	private static final TreeMap<KeyBinding, KeyAdapterBinding> bindingMap = new TreeMap<>();

	public static void adapt() {
		bindingMap.put(
				KeyBindings.getKeyBreakfall(),
				new KeyAdapterBinding(
						Buttons.X,
						KeyBindings.getKeyBreakfall()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyCrawl(),
				new KeyAdapterBinding(
						Buttons.RIGHT_THUMB_STICK,
						KeyBindings.getKeyCrawl()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyDodge(),
				new KeyAdapterBinding(
						-1,
						KeyBindings.getKeyDodge()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyFlipping(),
				new KeyAdapterBinding(
						-1,
						KeyBindings.getKeyFlipping()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyFastRunning(),
				new KeyAdapterBinding(
						Buttons.LEFT_THUMB_STICK,
						KeyBindings.getKeyFastRunning()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyGrabWall(),
				new KeyAdapterBinding(
						Buttons.LEFT_TRIGGER,
						KeyBindings.getKeyGrabWall()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyHangDown(),
				new KeyAdapterBinding(
						Buttons.LEFT_TRIGGER,
						KeyBindings.getKeyHangDown()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyVault(),
				new KeyAdapterBinding(
						Buttons.LEFT_THUMB_STICK,
						KeyBindings.getKeyVault()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyWallJump(),
				new KeyAdapterBinding(
						Buttons.A,
						KeyBindings.getKeyWallJump()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyWallSlide(),
				new KeyAdapterBinding(
						Buttons.LEFT_TRIGGER,
						KeyBindings.getKeyWallSlide()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyHorizontalWallRun(),
				new KeyAdapterBinding(
						Buttons.LEFT_THUMB_STICK,
						KeyBindings.getKeyHorizontalWallRun()
				)
		);
		bindingMap.put(
				KeyBindings.getKeyQuickTurn(),
				new KeyAdapterBinding(
						-1,
						KeyBindings.getKeyQuickTurn()
				)
		);
		bindingMap.values().forEach(BindingRegistry.getInstance()::addKeyAdapter);
	}
}
