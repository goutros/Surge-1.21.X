package net.galaxi.surge.network;

import net.galaxi.surge.skill.ModAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record ReorderSkillsPacket(List<String> skillOrder) implements CustomPacketPayload {
    public static final Type<ReorderSkillsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("surge", "reorder_skills"));

    public static final StreamCodec<FriendlyByteBuf, ReorderSkillsPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeInt(packet.skillOrder.size());
                for (String skillId : packet.skillOrder) {
                    buf.writeUtf(skillId);
                }
            },
            buf -> {
                int size = buf.readInt();
                List<String> order = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    order.add(buf.readUtf());
                }
                return new ReorderSkillsPacket(order);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ReorderSkillsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                var skillData = player.getData(ModAttachments.PLAYER_SKILLS);
                skillData.reorderSkills(packet.skillOrder);
                net.galaxi.surge.Surge.LOGGER.info("Reordered skills for player. New order: {}", packet.skillOrder);
            }
        });
    }
}
