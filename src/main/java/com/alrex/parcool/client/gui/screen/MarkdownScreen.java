package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.client.md.CompiledMarkdown;
import com.alrex.parcool.client.md.MarkdownResourceManager;
import com.alrex.parcool.client.md.ui.MarkdownWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class MarkdownScreen extends ParCoolTabletScreen {
    private static final int MD_VIEW_WIDTH = 180;
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
            addRenderableWidget(
                    new MarkdownWidget(
                            font,
                            contentOffsetX + CONTENT_WIDTH - MD_VIEW_WIDTH + 4,
                            contentOffsetY,
                            MD_VIEW_WIDTH - 4,
                            CONTENT_HEIGHT,
                            content,
                            0xFF111111,
                            (location) -> Minecraft.getInstance().setScreen(new MarkdownScreen(location)),
                            (url) -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen((b) -> this.confirmLink(b, url), url, false))
                    )
            );
        }
    }

    @Override
    protected void renderContent(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        fill(poseStack, contentOffsetX, contentOffsetY, contentOffsetX + CONTENT_WIDTH, contentOffsetY + CONTENT_HEIGHT, ~0);
        super.renderContent(poseStack, mouseX, mouseY, partial);
        fill(poseStack,
                contentOffsetX + CONTENT_WIDTH - MD_VIEW_WIDTH, contentOffsetY,
                contentOffsetX + CONTENT_WIDTH - MD_VIEW_WIDTH + 1, contentOffsetY + CONTENT_HEIGHT,
                0x88999999
        );
    }
}
