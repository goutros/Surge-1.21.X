package net.galaxi.surge.network;

import net.galaxi.surge.skill.ModAttachments;
import net.galaxi.surge.skill.PlayerSkillData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SelectSkillPacket(String skillId) implements CustomPacketPayload {
    public static final Type<SelectSkillPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("surge", "select_skill"));

    public static final StreamCodec<FriendlyByteBuf, SelectSkillPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeUtf(packet.skillId),
            buf -> new SelectSkillPacket(buf.readUtf())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SelectSkillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                var skillData = player.getData(ModAttachments.PLAYER_SKILLS);
                skillData.addSkill(packet.skillId);
                net.galaxi.surge.Surge.LOGGER.info("Added skill {} to player. Total skills: {}", packet.skillId, skillData.getSkills().size());

                // Sync to client
                List<String> skills = skillData.getSkills().stream()
                        .map(skill -> skill.getId())
                        .toList();
                PacketDistributor.sendToPlayer(player, new SyncSkillsPacket(skills));

                player.closeContainer();
            }
        });
    }
}