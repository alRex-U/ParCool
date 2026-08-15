package com.alrex.parcool.client.gui;

import com.alrex.parcool.api.client.skilltree.PrepareParCoolSkillTreeEvent;
import com.alrex.parcool.client.gui.screen.ParCoolGuideScreen;
import com.alrex.parcool.client.gui.screen.SkillTreeScreen;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;

public class GuiHelper {
    public static void openSkillTreeGui(Player player) {
        var prepareEvent = new PrepareParCoolSkillTreeEvent();
        NeoForge.EVENT_BUS.post(prepareEvent);
        var parkourability = Parkourability.get(player);
        Minecraft.getInstance().setScreen(new SkillTreeScreen(
                parkourability.getCapabilities(),
                parkourability.getEnabledActions(),
                prepareEvent.getSkillTrees()
        ));
    }

    public static void openGuideGui() {
        Minecraft.getInstance().setScreen(new ParCoolGuideScreen(ResourceLocation.fromNamespaceAndPath("parcool", "welcome.md")));
    }
}
