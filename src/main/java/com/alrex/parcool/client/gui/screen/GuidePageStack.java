package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.client.md.CompiledMarkdown;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Stack;

public class GuidePageStack {
    public GuidePageStack(@Nullable Runnable onContentChangedListener) {
        this.onContentChangedListener = onContentChangedListener;
    }

    @Nullable
    private Runnable onContentChangedListener;
    private final Stack<Entry> pageStack = new Stack<>();

    public record Entry(ResourceLocation pageId, @Nullable CompiledMarkdown content) {
    }

    public void pushPage(ResourceLocation pageId) {
        if (!pageStack.isEmpty()) {
            var currentPage = pageStack.lastElement();
            if (currentPage != null && currentPage.pageId.equals(pageId)) return;
        }
        pageStack.push(new Entry(pageId, GuideResourceManager.getInstance().getResource().get(pageId)));
        if (onContentChangedListener != null) onContentChangedListener.run();
    }

    public void popPage() {
        if (!pageStack.isEmpty()) pageStack.pop();
        if (pageStack.isEmpty()) {
            Minecraft.getInstance().setScreen(null);
        } else {
            if (onContentChangedListener != null) onContentChangedListener.run();
        }
    }

    @Nullable
    public ResourceLocation getCurrentPageID() {
        if (pageStack.isEmpty()) return null;
        return pageStack.lastElement().pageId();
    }

    @Nullable
    public CompiledMarkdown getCurrentContent() {
        if (pageStack.isEmpty()) return null;
        return pageStack.lastElement().content();
    }

    public void setContentChangedListener(@Nullable Runnable onContentChangedListener) {
        this.onContentChangedListener = onContentChangedListener;
    }

    public boolean isEmpty() {
        return this.pageStack.isEmpty();
    }
}
