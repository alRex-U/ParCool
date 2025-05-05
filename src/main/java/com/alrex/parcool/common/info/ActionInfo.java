package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ActionInfo {
    public ActionInfo() {
    }

    public ClientSetting getClientSetting() {
        return clientSetting;
    }

    public void setClientSetting(ClientSetting clientSetting) {
        this.clientSetting = clientSetting;
	}

    private ClientSetting clientSetting = ClientSetting.UNSYNCED_INSTANCE;

    public ServerLimitation getServerLimitation() {
        return serverLimitation;
	}

    public void setServerLimitation(ServerLimitation serverLimitation) {
        this.serverLimitation = serverLimitation;
    }

    private ServerLimitation serverLimitation = ServerLimitation.UNSYNCED_INSTANCE;

	public boolean can(Class<? extends Action> action) {
        return getClientSetting().get(ParCoolConfig.Client.Booleans.ParCoolIsActive)
                && getClientSetting().getPossibilityOf(action)
                && getServerLimitation().isPermitted(action);
	}

    public StaminaType getStaminaType() {
        var forcedStamina = getServerLimitation().getForcedStamina();
        if (forcedStamina == StaminaType.NONE) {
            var requestedStamina = getClientSetting().getRequestedStamina();
            if (requestedStamina == StaminaType.NONE) {
                return isInfiniteStaminaPermitted() ? StaminaType.NONE : StaminaType.PARCOOL;
            }
            return requestedStamina;
        }
        return forcedStamina;
    }

	public int getStaminaConsumptionOf(Class<? extends Action> action) {
        return Math.max(
                getClientSetting().getStaminaConsumptionOf(action),
                getServerLimitation().getStaminaConsumptionOf(action)
        );
	}

    public int getStaminaRecoveryLimit() {
        return getServerLimitation().get(ParCoolConfig.Server.Integers.MaxStaminaRecovery);
	}

    public int getMaxStaminaLimit() {
        return getServerLimitation().get(ParCoolConfig.Server.Integers.MaxStaminaLimit);
	}

    @OnlyIn(Dist.CLIENT)
    public boolean isStaminaInfinite(LocalStamina stamina, LocalPlayer player) {
        return stamina.isInfinite(player);
	}

	public boolean isInfiniteStaminaPermitted() {
        return serverLimitation.get(ParCoolConfig.Server.Booleans.AllowInfiniteStamina);
	}

    @OnlyIn(Dist.CLIENT)
    public void updateStaminaType(LocalStamina stamina, LocalPlayer player) {
        stamina.changeType(player, getStaminaType());
    }
}
