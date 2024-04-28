package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class SettingActionLimitationScreen extends ParCoolSettingScreen {
    private final ActionConfigSet[] actionList = new ActionConfigSet[ActionList.ACTIONS.size()];
    private final Checkbox[] actionButtons = new Checkbox[actionList.length];

    public SettingActionLimitationScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        currentScreen = 0;
        for (int i = 0; i < actionList.length; i++) {
            actionList[i] = new ActionConfigSet(ActionList.getByIndex(i), info);
            actionButtons[i] = new Checkbox(0, 0, 0, Checkbox_Item_Height, Component.literal(actionList[i].name), actionList[i].getter.getAsBoolean());
        }
    }

    @Override
    public boolean isDownScrollable() {
        return topIndex + viewableItemCount < actionButtons.length;
    }

    @Override
    public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset) {
        final int offsetX = 40, headerHeight = (int) (font.lineHeight * 1.5f);
        int contentOffsetY = topOffset + headerHeight + 2;
        int permissionColumnWidth = font.width(Permission_Permitted) * 5;
        int nameColumnWidth = width - offsetX * 2 - permissionColumnWidth;
        int contentHeight = height - contentOffsetY - bottomOffset;
        viewableItemCount = contentHeight / Checkbox_Item_Height;
        int headerTextY = topOffset + headerHeight / 2 - font.lineHeight / 2 + 1;
        drawString(poseStack, font, Header_ActionName, offsetX + 5, headerTextY, color.getText());
        drawCenteredString(poseStack, font, Header_Limitation, offsetX + nameColumnWidth + permissionColumnWidth / 2, headerTextY, color.getText());
        for (Checkbox actionButton : actionButtons) {
            actionButton.setWidth(0);
        }
        //draw separators
        fill(poseStack, offsetX, contentOffsetY, width - offsetX, contentOffsetY - 1, color.getSeparator());
        fill(poseStack, offsetX, topOffset, offsetX + 1, height - bottomOffset, color.getSeparator());
        fill(poseStack, offsetX + nameColumnWidth, topOffset, offsetX + nameColumnWidth + 1, height - bottomOffset, color.getSeparator());
        fill(poseStack, offsetX + nameColumnWidth + permissionColumnWidth, topOffset, offsetX + nameColumnWidth + permissionColumnWidth + 1, height - bottomOffset, color.getSeparator());
        for (int i = 0; i < viewableItemCount && i + topIndex < actionButtons.length; i++) {
            Checkbox button = actionButtons[i + topIndex];
            button.setX(offsetX + 1);
            button.setY(contentOffsetY + Checkbox_Item_Height * i);
            button.setWidth(nameColumnWidth - 5);
            button.setHeight(20);
            button.render(graphics, mouseX, mouseY, partialTick);
            graphics.fill(offsetX, button.getY() + button.getHeight(), width - offsetX, button.getY() + button.getHeight() + 1, color.getSubSeparator());
            int rowY = contentOffsetY + Checkbox_Item_Height * i + Checkbox_Item_Height / 2;
            boolean permitted = actionList[topIndex + i].serverLimitation.getAsBoolean();
            drawCenteredString(
                    poseStack, font,
                    permitted ? Permission_Permitted : Permission_Denied,
                    offsetX + nameColumnWidth + permissionColumnWidth / 2,
                    rowY - font.lineHeight / 2,
                    permitted ? 0x00AA00 : 0xAA0000
            );
        }
        {// draw tooltip
            int columnCenter = offsetX + nameColumnWidth + permissionColumnWidth / 2;
            if ((topOffset < mouseY && mouseY < topOffset + headerHeight)
                    && (columnCenter - permissionColumnWidth / 2 < mouseX && mouseX < columnCenter + permissionColumnWidth / 2)
            ) {
                renderComponentTooltip(poseStack, Collections.singletonList(Header_Limitation_Text), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int type) {
        for (Checkbox button : actionButtons) {
            if (button.mouseClicked(mouseX, mouseY, type)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, type);
    }

    @Override
    protected void save() {
        for (int i = 0; i < actionList.length; i++) {
            actionList[i].setter.accept(actionButtons[i].selected());
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
        SyncClientInformationMessage.sync(player, true);
    }

    private static class ActionConfigSet {
        final String name;
        final Consumer<Boolean> setter;
        final BooleanSupplier getter;
        final BooleanSupplier serverLimitation;

        ActionConfigSet(Class<? extends Action> action, ActionInfo info) {
            name = Component.translatable("parcool.action." + action.getSimpleName()).getString();
            ForgeConfigSpec.BooleanValue config = ParCoolConfig.Client.getPossibilityOf(action);
            setter = config::set;
            getter = config::get;
            serverLimitation = () -> info.getServerLimitation().isPermitted(action);
        }
    }
}
