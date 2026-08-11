package com.alrex.parcool.client.md;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public record CompiledMarkdown(List<MarkdownParagraph> components) {
}
