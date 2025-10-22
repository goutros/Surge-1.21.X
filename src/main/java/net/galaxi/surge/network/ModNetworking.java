package net.galaxi.surge.network;

import net.galaxi.surge.Surge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Surge.MOD_ID);

        registrar.playToServer(
                SelectSkillPacket.TYPE,
                SelectSkillPacket.STREAM_CODEC,
                SelectSkillPacket::handle
        );

        registrar.playToServer(
                ReorderSkillsPacket.TYPE,
                ReorderSkillsPacket.STREAM_CODEC,
                ReorderSkillsPacket::handle
        );

        registrar.playToClient(
                SyncSkillsPacket.TYPE,
                SyncSkillsPacket.STREAM_CODEC,
                SyncSkillsPacket::handle
        );
    }
}