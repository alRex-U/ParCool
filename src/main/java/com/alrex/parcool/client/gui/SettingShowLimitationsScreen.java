package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.LinkedList;

public class SettingShowLimitationsScreen extends ParCoolSettingScreen {
    private final InfoSet[] infoList;

    public SettingShowLimitationsScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        currentScreen = 3;
        LinkedList<InfoSet> infoSets = new LinkedList<>();
        for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
            infoSets.add(new InfoSet(item.Path, Boolean.toString(info.getServerLimitation().get(item))));
        }
        for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
            infoSets.add(new InfoSet(item.Path, Integer.toString(info.getServerLimitation().get(item))));
        }
        for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
            infoSets.add(new InfoSet(item.Path, Double.toString(info.getServerLimitation().get(item))));
        }
        infoList = infoSets.toArray(new InfoSet[0]);
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
