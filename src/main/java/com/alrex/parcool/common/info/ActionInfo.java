package com.alrex.parcool.common.info;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.config.ParCoolConfig;

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
		return ParCool.isActive()
                && getClientSetting().getPossibilityOf(action)
                && getServerLimitation().isPermitted(action);
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

	public boolean isStaminaInfinite(boolean creativeOrSpectator) {
        if (getClientSetting().get(ParCoolConfig.Client.Booleans.InfiniteStamina) && isInfiniteStaminaPermitted())
			return true;
        return creativeOrSpectator && getClientSetting().get(ParCoolConfig.Client.Booleans.InfiniteStaminaWhenCreative);
	}

	public boolean isInfiniteStaminaPermitted() {
		return true;
	}
}
