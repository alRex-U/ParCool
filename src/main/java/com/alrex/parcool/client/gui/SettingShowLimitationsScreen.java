package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.info.ActionInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class SettingShowLimitationsScreen extends ParCoolSettingScreen {
    private final InfoSet[] infoList;

    public SettingShowLimitationsScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        currentScreen = 3;
        infoList = new InfoSet[]{
                new InfoSet(
                        "Max Stamina Limit",
                        Integer.toString(info.getMaxStaminaLimit())
                ),
                new InfoSet(
                        "Infinite Stamina Permission",
                        Boolean.toString(info.isInfiniteStaminaPermitted())
                )
        };
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset) {
        final int offsetX = 40;
        int contentHeight = height - topOffset - bottomOffset;
        int itemHeight = font.lineHeight * 2;
        int valueWidth = Arrays.stream(infoList).map(it -> font.width(it.value)).max(Integer::compareTo).orElse(0);
        for (int i = 0; i < infoList.length; i++) {
            InfoSet item = infoList[i];
            graphics.drawString(
                    font,
                    item.name,
                    offsetX + 5,
                    topOffset + itemHeight * i + itemHeight / 2 - font.lineHeight / 2,
                    color.getText()
            );
            graphics.drawString(
                    font,
                    item.value,
                    width - offsetX - 5 - valueWidth,
                    topOffset + itemHeight * i + itemHeight / 2 - font.lineHeight / 2,
                    color.getText()
            );
            graphics.fill(offsetX, topOffset + itemHeight * (i + 1), width - offsetX, topOffset + itemHeight * (i + 1) + 1, color.getSubSeparator());
        }
        graphics.fill(width - offsetX, topOffset, width - offsetX - 1, topOffset + contentHeight, color.getSeparator());
        graphics.fill(offsetX, topOffset, offsetX + 1, topOffset + contentHeight, color.getSeparator());
    }

    private static class InfoSet {
        final String name;
        final String value;

        InfoSet(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
