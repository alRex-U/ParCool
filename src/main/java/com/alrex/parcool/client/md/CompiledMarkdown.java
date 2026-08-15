package com.alrex.parcool.client.md;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public record CompiledMarkdown(List<MarkdownParagraph> components) {
}
