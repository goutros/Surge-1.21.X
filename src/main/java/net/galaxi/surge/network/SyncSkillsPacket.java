package net.galaxi.surge.network;

import net.galaxi.surge.skill.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SyncSkillsPacket(List<String> skills) implements CustomPacketPayload {
    public static final Type<SyncSkillsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("surge", "sync_skills"));

    public static final StreamCodec<FriendlyByteBuf, SyncSkillsPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeInt(packet.skills.size());
                for (String skill : packet.skills) {
                    buf.writeUtf(skill);
                }
            },
            buf -> {
                int size = buf.readInt();
                List<String> skills = new java.util.ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    skills.add(buf.readUtf());
                }
                return new SyncSkillsPacket(skills);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncSkillsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound() && Minecraft.getInstance().player != null) {
                var skillData = Minecraft.getInstance().player.getData(ModAttachments.PLAYER_SKILLS);
                // Use reflection or direct field access to clear - for now just deserialize from NBT
                var tag = new net.minecraft.nbt.CompoundTag();
                var skillList = new net.minecraft.nbt.ListTag();
                for (String skillId : packet.skills) {
                    skillList.add(net.minecraft.nbt.StringTag.valueOf(skillId));
                }
                tag.put("skills", skillList);
                skillData.deserializeNBT(Minecraft.getInstance().player.registryAccess(), tag);
                net.galaxi.surge.Surge.LOGGER.info("Synced {} skills to client", packet.skills.size());
            }
        });
    }
}
