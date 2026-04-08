package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class SettingActionLimitationScreen extends ParCoolSettingScreen {
    private final ActionConfigSet[] actionList = new ActionConfigSet[Actions.LIST.size()];
    private final Checkbox[] actionButtons = new Checkbox[actionList.length];

    public SettingActionLimitationScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        currentScreen = 0;
        for (int i = 0; i < actionList.length; i++) {
            actionList[i] = new ActionConfigSet(Actions.getByIndex(i), info);
            actionButtons[i] = Checkbox
                    .builder(Component.literal(actionList[i].name), Minecraft.getInstance().font)
                    .selected(actionList[i].get())
                    .pos(0, 0)
                    .build();
            actionButtons[i].setHeight(Checkbox_Item_Height);
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
        graphics.drawString(font, Header_ActionName, offsetX + 5, headerTextY, color.getText());
        graphics.drawCenteredString(font, Header_Limitation, offsetX + nameColumnWidth + permissionColumnWidth / 2, headerTextY, color.getText());
        for (Checkbox actionButton : actionButtons) {
            actionButton.setWidth(0);
        }
        //draw separators
        graphics.fill(offsetX, contentOffsetY, width - offsetX, contentOffsetY - 1, color.getSeparator());
        graphics.fill(offsetX, topOffset, offsetX + 1, height - bottomOffset, color.getSeparator());
        graphics.fill(offsetX + nameColumnWidth, topOffset, offsetX + nameColumnWidth + 1, height - bottomOffset, color.getSeparator());
        graphics.fill(offsetX + nameColumnWidth + permissionColumnWidth, topOffset, offsetX + nameColumnWidth + permissionColumnWidth + 1, height - bottomOffset, color.getSeparator());
        for (int i = 0; i < viewableItemCount && i + topIndex < actionButtons.length; i++) {
            Checkbox button = actionButtons[i + topIndex];
            button.setX(offsetX + 1);
            button.setY(contentOffsetY + Checkbox_Item_Height * i);
            button.setWidth(nameColumnWidth - 5);
            button.setHeight(Checkbox_Item_Height - 1);
            button.render(graphics, mouseX, mouseY, partialTick);
            graphics.fill(offsetX, button.getY() + button.getHeight(), width - offsetX, button.getY() + button.getHeight() + 1, color.getSubSeparator());
            int rowY = contentOffsetY + Checkbox_Item_Height * i + Checkbox_Item_Height / 2;
            boolean permitted = actionList[topIndex + i].serverLimitation.getAsBoolean();
            graphics.drawCenteredString(
                    font,
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
                graphics.setTooltipForNextFrame(font, Header_Limitation_Text, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseEvent, boolean __) {
        for (Checkbox button : actionButtons) {
            if (button.mouseClicked(mouseEvent, __)) return true;
        }
        return super.mouseClicked(mouseEvent, __);
    }

    @Override
    protected void save() {
        for (int i = 0; i < actionList.length; i++) {
            actionList[i].set(actionButtons[i].selected());
            actionList[i].save();
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Parkourability parkourability = Parkourability.get(player);
        parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
        ClientPacketDistributor.sendToServer(new ClientInformationPayload(player.getUUID(), true, parkourability.getClientInfo()));
    }

    private static class ActionConfigSet {
        private final String name;
        private final Consumer<Boolean> setter;
        private final BooleanSupplier getter;
        private final BooleanSupplier serverLimitation;
        private final ModConfigSpec.BooleanValue configInstance;
        private boolean dirty = false;

        ActionConfigSet(Class<? extends Action> action, ActionInfo info) {
            name = Component.translatable("parcool.action." + action.getSimpleName()).getString();
            configInstance = ParCoolConfig.Client.getInstance().getPossibilityOf(action);
            setter = configInstance::set;
            getter = configInstance::get;
            serverLimitation = () -> info.getServerLimitation().isPermitted(action);
        }

        private boolean get() {
            return getter.getAsBoolean();
        }

        private boolean getServerLimitation() {
            return serverLimitation.getAsBoolean();
        }

        private void set(boolean value) {
            setter.accept(value);
            dirty = true;
        }

        private void save() {
            if (dirty) {
                configInstance.save();
            }
        }
    }
}
