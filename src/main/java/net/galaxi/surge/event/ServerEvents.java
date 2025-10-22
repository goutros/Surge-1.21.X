package net.galaxi.surge.event;

import net.galaxi.surge.Surge;
import net.galaxi.surge.network.SyncSkillsPacket;
import net.galaxi.surge.skill.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = Surge.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            var skillData = player.getData(ModAttachments.PLAYER_SKILLS);
            List<String> skills = skillData.getSkills().stream()
                    .map(skill -> skill.getId())
                    .toList();
            PacketDistributor.sendToPlayer(player, new SyncSkillsPacket(skills));
            Surge.LOGGER.info("Synced {} skills to player on login", skills.size());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            var skillData = player.getData(ModAttachments.PLAYER_SKILLS);
            List<String> skills = skillData.getSkills().stream()
                    .map(skill -> skill.getId())
                    .toList();
            PacketDistributor.sendToPlayer(player, new SyncSkillsPacket(skills));
            Surge.LOGGER.info("Synced {} skills to player on respawn", skills.size());
        }
    }
}
