package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.LogicalSide;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.architectury.event.ParCoolActionArchEvent;
import com.alrex.parcool.common.network.ActionCapabilitiesPacket;
import com.alrex.parcool.common.network.ActionStatePacket;
import com.alrex.parcool.common.network.ActionStateSetPacket;
import com.alrex.parcool.common.network.PacketHelper;
import com.alrex.parcool.common.stamina.StaminaSynchronizationDepot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class ActionProcessor {
    private final StaminaSynchronizationDepot serverStaminaDepot = new StaminaSynchronizationDepot();
    private final ActionSynchronizationDepot serverActionDepot = new ActionSynchronizationDepot();

    public StaminaSynchronizationDepot getStaminaSyncDepot() {
        return serverStaminaDepot;
    }

    public ActionSynchronizationDepot getActionSyncDepot() {
        return serverActionDepot;
    }

    public void onTickLevel(ServerLevel level) {
        getStaminaSyncDepot().tick();
        getActionSyncDepot().tick();
    }

    public void onTickPlayer(Player player) {

        var parkourability = Parkourability.get(player);
        Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData = new TreeMap<>();
        var side = ParCool.logicalSide();
        if (side.isClient()) {
            onTick$doPreprocessInClient(parkourability);
        } else {
            onTick$doPreprocessInServer(parkourability);
        }

        parkourability.getAdditionalProperties().onTick();
        for (Action action : parkourability.getActions()) {
            ParCoolActionArchEvent.Tick.Pre.EVENT.invoker().onPre(player, action);
            processAction(parkourability, side, action, synchronizedData);
            ParCoolActionArchEvent.Tick.Post.EVENT.invoker().onPost(player, action);
        }
        if (!synchronizedData.isEmpty()) {
            onTick$sendSyncPacket(parkourability, side, synchronizedData);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (parkourability.getCapabilities().isDirty()) {
                parkourability.getCapabilities().sync(serverPlayer, ActionCapabilitiesPacket.Target.CAPABILITY);
            }
            if (parkourability.getEnabledActions().isDirty()) {
                parkourability.getEnabledActions().sync(serverPlayer, ActionCapabilitiesPacket.Target.ENABLED_ACTIONS);
            }
        }

        parkourability.finishTicking();
    }

    private void onTick$doPreprocessInServer(Parkourability parkourability) {
    }

    @Environment(EnvType.CLIENT)
    private void onTick$doPreprocessInClient(Parkourability parkourability) {
        if (parkourability.getStamina() instanceof AbstractLocalStamina stamina) {
            stamina.tick();
        }
    }

    private void onTick$sendSyncPacket(Parkourability parkourability, LogicalSide side, Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData) {
        var list = new LinkedList<ActionStatePacket>();
        for (var entry : synchronizedData.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            list.add(new ActionStatePacket(entry.getKey(), entry.getValue()));
        }
        if (list.isEmpty()) return;

        var packet = side == LogicalSide.CLIENT
                ? ActionStateSetPacket.fromClient(parkourability.player().getUUID())
                : ActionStateSetPacket.fromServer(parkourability.player().getUUID());
        for (var subPacket : list) {
            packet.add(subPacket);
        }
        if (side.isClient()) {
            PacketHelper.sendToServer(
                    ActionStateSetPacket.HANDLER.id(),
                    packet,
                    ActionStateSetPacket.HANDLER::encode
            );
        } else {
            getActionSyncDepot().requestSync(packet);
        }
    }

    private void processAction(Parkourability parkourability, LogicalSide logicalSide, Action action, Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData) {
        var player = parkourability.player();
        var triggeredSide = action.getEntry().option().triggeredSide();
        boolean needSync = (triggeredSide.isClient() && player.isLocalPlayer())
                || (triggeredSide.isServer() && logicalSide.isServer());
        action.tick();
        ActionStatePacket.Type type = ActionStatePacket.Type.DATA;
        if (needSync) {
            if (action instanceof ContinuableAction continuableAction && continuableAction.isDoing()) {
                if (!(continuableAction.isPossibleToContinue())) {
                    ParCoolActionArchEvent.Finish.Pre.EVENT.invoker().onPre(player, continuableAction);
                    continuableAction.finish();
                    ParCoolActionArchEvent.Finish.Post.EVENT.invoker().onPost(player, continuableAction);
                    type = ActionStatePacket.Type.FINISH;
                }
            } else {
                if (action.isReadyToStart()) {
                    ParCoolActionArchEvent.Start.Pre.EVENT.invoker().onPre(player, action);
                    action.start();
                    ParCoolActionArchEvent.Start.Post.EVENT.invoker().onPost(player, action);
                    type = ActionStatePacket.Type.START;
                }
            }
        }
        if (action instanceof ContinuableAction continuableAction && continuableAction.isDoing()) {
            continuableAction.tickOnWorking();
        }
        var data = action.getSynchronizedData().packToEntry(type, action.getEntry());
        if (data != null) {
            var list = synchronizedData.computeIfAbsent(data.entry().id().getNamespace(), __ -> new LinkedList<>());
            list.add(data);
        }
    }
}
