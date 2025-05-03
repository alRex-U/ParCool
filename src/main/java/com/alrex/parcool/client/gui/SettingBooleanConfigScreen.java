package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import com.alrex.parcool.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;

public class SettingBooleanConfigScreen extends ParCoolSettingScreen {
    private final ParCoolConfig.Client.Booleans[] booleans = ParCoolConfig.Client.Booleans.values();
    private final CheckboxButton[] configButtons = new CheckboxButton[booleans.length];

    public SettingBooleanConfigScreen(ITextComponent titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        currentScreen = 1;
        for (int i = 0; i < booleans.length; i++) {
            configButtons[i] = new CheckboxButton(0, 0, 0, Checkbox_Item_Height, new TranslationTextComponent(booleans[i].Path), booleans[i].get());
        }
    }

    @Override
    protected boolean isDownScrollable() {
        return topIndex + viewableItemCount < configButtons.length;
    }

    @Override
    protected void renderContents(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset) {
        final int offsetX = 40;
        int contentWidth = width - offsetX * 2;
        int contentHeight = height - topOffset - bottomOffset;
        viewableItemCount = contentHeight / Checkbox_Item_Height;
        for (CheckboxButton configButton : configButtons) {
            configButton.setWidth(0);
        }
        for (int i = 0; i < viewableItemCount && i + topIndex < booleans.length; i++) {
            CheckboxButton button = configButtons[i + topIndex];
            button.x = offsetX + 1;
            button.y = topOffset + Checkbox_Item_Height * i;
            button.setWidth(contentWidth);
            button.setHeight(20);
            button.render(matrixStack, mouseX, mouseY, partialTick);
            fill(matrixStack, offsetX, button.y + button.getHeight(), width - offsetX, button.y + button.getHeight() + 1, color.getSubSeparator());
            String comment = booleans[i + topIndex].Comment;
            if (comment != null && button.x < mouseX && mouseX < button.x + contentWidth && button.y < mouseY && mouseY < button.y + 20) {
                renderComponentTooltip(
                        matrixStack,
                        Collections.singletonList(new StringTextComponent(comment)),
                        mouseX, mouseY);
            }
        }
        fill(matrixStack, width - offsetX, topOffset, width - offsetX - 1, topOffset + contentHeight, color.getSeparator());
        fill(matrixStack, offsetX, topOffset, offsetX + 1, topOffset + contentHeight, color.getSeparator());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int type) {
        for (CheckboxButton button : configButtons) {
            if (button.mouseClicked(mouseX, mouseY, type)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, type);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void save() {
        for (int i = 0; i < booleans.length; i++) {
            booleans[i].set(configButtons[i].selected());
        }
        ClientPlayerWrapper player = ClientPlayerWrapper.get();
        if (player == null) return;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
        SyncClientInformationMessage.sync(player, true);
    }
}
