package com.alrex.parcool.client.gui;

import com.alrex.parcool.client.gui.screen.ParCoolGuideScreen;
import com.alrex.parcool.client.gui.screen.ParCoolSettingScreen;
import com.alrex.parcool.client.gui.screen.ParCoolSkillTreeScreen;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class GuiHelper {
    public static void openSkillTreeGui(Player player) {
        var parkourability = Parkourability.get(player);
        Minecraft.getInstance().setScreen(new ParCoolSkillTreeScreen(
                parkourability, parkourability.getSkillTrees()
        ));
    }

    public static void openGuideGui() {
        Minecraft.getInstance().setScreen(new ParCoolGuideScreen(ResourceLocation.fromNamespaceAndPath("parcool", "welcome.md")));
    }

    public static void openSettingGui(Player player) {
        Minecraft.getInstance().setScreen(new ParCoolSettingScreen(Parkourability.get(player)));
    }
}
