package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class SettingActionLimitationScreen extends ParCoolSettingScreen {
    private final ActionConfigSet[] actionList = new ActionConfigSet[ActionList.ACTIONS.size()];
    private final CheckboxButton[] actionButtons = new CheckboxButton[actionList.length];

    public SettingActionLimitationScreen(ITextComponent titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        currentScreen = 0;
        for (int i = 0; i < actionList.length; i++) {
            actionList[i] = new ActionConfigSet(ActionList.getByIndex(i), info);
            actionButtons[i] = new CheckboxButton(0, 0, 0, Checkbox_Item_Height, new StringTextComponent(actionList[i].name), actionList[i].getter.getAsBoolean());
        }
    }

    @Override
    public boolean isDownScrollable() {
        return topIndex + viewableItemCount < actionButtons.length;
    }

    @Override
    public void renderContents(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset) {
        final int offsetX = 40, headerHeight = (int) (font.lineHeight * 1.5f);
        int contentOffsetY = topOffset + headerHeight + 2;
        int permissionColumnWidth = font.width(Permission_Permitted) * 5;
        int nameColumnWidth = width - offsetX * 2 - permissionColumnWidth * 2;
        int contentHeight = height - contentOffsetY - bottomOffset;
        viewableItemCount = contentHeight / Checkbox_Item_Height;
        int headerTextY = topOffset + headerHeight / 2 - font.lineHeight / 2 + 1;
        drawString(matrixStack, font, Header_ActionName, offsetX + 5, headerTextY, color.getText());
        drawCenteredString(matrixStack, font, Header_ServerPermission, offsetX + nameColumnWidth + permissionColumnWidth / 2, headerTextY, color.getText());
        drawCenteredString(matrixStack, font, Header_IndividualPermission, offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2, headerTextY, color.getText());
        for (CheckboxButton actionButton : actionButtons) {
            actionButton.setWidth(0);
        }
        for (int i = 0; i < viewableItemCount && i + topIndex < actionButtons.length; i++) {
            CheckboxButton button = actionButtons[i + topIndex];
            button.x = offsetX + 1;
            button.y = contentOffsetY + Checkbox_Item_Height * i;
            button.setWidth(nameColumnWidth - 5);
            button.setHeight(20);
            button.render(matrixStack, mouseX, mouseY, partialTick);
            fill(matrixStack, offsetX, button.y + button.getHeight(), width - offsetX, button.y + button.getHeight() + 1, color.getSubSeparator());
            int rowY = contentOffsetY + Checkbox_Item_Height * i + Checkbox_Item_Height / 2;
            boolean permitted = actionList[topIndex + i].serverWideLimitation.getAsBoolean();
            drawCenteredString(
                    matrixStack, font,
                    permitted ? Permission_Permitted : Permission_Denied,
                    offsetX + nameColumnWidth + permissionColumnWidth / 2,
                    rowY - font.lineHeight / 2,
                    permitted ? 0x00AA00 : 0xAA0000
            );
            permitted = actionList[topIndex + i].individualLimitation.getAsBoolean();
            drawCenteredString(
                    matrixStack, font,
                    permitted ? Permission_Permitted : Permission_Denied,
                    offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2,
                    rowY - font.lineHeight / 2,
                    permitted ? 0x00AA00 : 0xAA0000
            );
        }
        //draw separators
        fill(matrixStack, offsetX, contentOffsetY, width - offsetX, contentOffsetY - 1, color.getSeparator());
        fill(matrixStack, offsetX, topOffset, offsetX + 1, height - bottomOffset, color.getSeparator());
        fill(matrixStack, offsetX + nameColumnWidth, topOffset, offsetX + nameColumnWidth + 1, height - bottomOffset, color.getSeparator());
        fill(matrixStack, offsetX + nameColumnWidth + permissionColumnWidth, topOffset, offsetX + nameColumnWidth + permissionColumnWidth + 1, height - bottomOffset, color.getSeparator());
        fill(matrixStack, offsetX + nameColumnWidth + permissionColumnWidth * 2, topOffset, offsetX + nameColumnWidth + permissionColumnWidth * 2 + 1, height - bottomOffset, color.getSeparator());
        {// draw tooltip
            int columnCenter = offsetX + nameColumnWidth + permissionColumnWidth / 2;
            if ((topOffset < mouseY && mouseY < topOffset + headerHeight)
                    && (columnCenter - permissionColumnWidth / 2 < mouseX && mouseX < columnCenter + permissionColumnWidth / 2)
            ) {
                renderComponentTooltip(matrixStack, Collections.singletonList(Header_ServerPermissionText), mouseX, mouseY);
            }

            columnCenter = offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2;
            if ((topOffset < mouseY && mouseY < topOffset + headerHeight)
                    && (columnCenter - permissionColumnWidth / 2 < mouseX && mouseX < columnCenter + permissionColumnWidth / 2)
            ) {
                renderComponentTooltip(matrixStack, Collections.singletonList(Header_IndividualPermissionText), mouseX, mouseY);
            }
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int type) {
        for (CheckboxButton button : actionButtons) {
            if (button.mouseClicked(mouseX, mouseY, type)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, type);
    }

    @Override
    protected void save() {
        for (int i = 0; i < actionList.length; i++) {
            actionList[i].setter.accept(actionButtons[i].selected());
        }
        ClientPlayerEntity player = Minecraft.getInstance().player;
        SyncClientInformationMessage.sync(player, true);
    }

    private static class ActionConfigSet {
        final String name;
        final Consumer<Boolean> setter;
        final BooleanSupplier getter;
        final BooleanSupplier serverWideLimitation;
        final BooleanSupplier individualLimitation;

        ActionConfigSet(Class<? extends Action> action, ActionInfo info) {
            name = new TranslationTextComponent("parcool.action." + action.getSimpleName()).getString();
            ForgeConfigSpec.BooleanValue config = ParCoolConfig.Client.getPossibilityOf(action);
            setter = config::set;
            getter = config::get;
            serverWideLimitation = () -> info.getServerLimitation().isPermitted(action);
            individualLimitation = () -> info.getIndividualLimitation().isPermitted(action);
        }
    }
}
