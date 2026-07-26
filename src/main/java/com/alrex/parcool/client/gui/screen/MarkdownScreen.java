package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.client.md.CompiledMarkdown;
import com.alrex.parcool.client.md.MarkdownResourceManager;
import com.alrex.parcool.client.md.ui.MarkdownWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MarkdownScreen extends Screen {
    @Nullable
    private final CompiledMarkdown content;

    public MarkdownScreen(ResourceLocation dataLocation) {
        super(Component.empty());
        content = MarkdownResourceManager.getInstance().getResource().get(dataLocation);
    }

    @Override
    protected void init() {
        super.init();
        if (content != null) {
            addRenderableWidget(new MarkdownWidget(font, 20, 0, width - 20, height, content, ~0));
        }
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partial);
    }
}
