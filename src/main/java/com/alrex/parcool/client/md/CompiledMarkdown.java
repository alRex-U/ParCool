package com.alrex.parcool.client.md;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public record CompiledMarkdown(List<MarkdownParagraph> components) {
}
