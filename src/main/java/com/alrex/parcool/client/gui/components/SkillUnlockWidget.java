package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.client.textures.ParCoolTextures;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class SkillUnlockWidget extends AbstractWidget {
    private static final int MARGIN = 8;
    @Nullable
    private SkillTree.Entry<?> entry;
    @Nullable
    private Component actionName;
    private final Font font;

    public SkillUnlockWidget(Font font, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.font = font;
    }

    public void setSkillEntry(@Nullable SkillTree.Entry<?> entry) {
        this.entry = entry;
        if (entry == null) {
            actionName = null;
            active = false;
        } else {
            actionName = Component.literal(entry.getActionEntry().id().toString());
            active = true;
        }
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        if (entry == null || actionName == null) return;
        graphics.blit(getX() + MARGIN, getY() + MARGIN, 0, width - 2 * MARGIN, width - 2 * MARGIN, ParCoolTextures.action(entry.getActionEntry()));
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
