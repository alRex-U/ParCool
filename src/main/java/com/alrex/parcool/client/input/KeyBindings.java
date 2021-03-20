package com.alrex.parcool.client.input;

import net.java.games.input.Controller;
import net.java.games.input.Keyboard;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyBindings {
    private static final GameSettings settings= Minecraft.getInstance().gameSettings;
    private static final KeyBinding keyBindCrawl=new KeyBinding("key.crawl.description", GLFW.GLFW_KEY_C ,"key.categories.movement");
    private static final KeyBinding keyBindGrabWall=new KeyBinding("key.grab.description",GLFW.GLFW_MOUSE_BUTTON_RIGHT,"key.categories.movement");
    private static final KeyBinding keyBindActivateParCool=new KeyBinding("key.parcool.activate",GLFW.GLFW_KEY_P,"key.categories.parcool");

    public static KeyBinding getKeySprint(){ return settings.keyBindSprint; }
    public static KeyBinding getKeyJump(){return settings.keyBindJump;}
    public static KeyBinding getKeySneak(){return settings.keyBindSneak;}
    public static KeyBinding getKeyCrawl(){return keyBindCrawl;}
    public static KeyBinding getKeyGrabWall(){return keyBindGrabWall;}
    public static KeyBinding getKeyActivateParCool(){return keyBindActivateParCool;}

    @SubscribeEvent
    public static void register(FMLClientSetupEvent event){
        ClientRegistry.registerKeyBinding(keyBindCrawl);
        ClientRegistry.registerKeyBinding(keyBindGrabWall);
    }
}
