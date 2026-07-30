package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (entry == null || actionName == null) return;
        blit(poseStack, x + MARGIN, y + MARGIN, 0, width - 2 * MARGIN, width - 2 * MARGIN, ParCoolTextures.action(entry.getActionEntry()));
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
