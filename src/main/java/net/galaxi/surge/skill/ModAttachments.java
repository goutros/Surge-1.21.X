package net.galaxi.surge.skill;

import net.galaxi.surge.Surge;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Surge.MOD_ID);

    public static final Supplier<AttachmentType<PlayerSkillData>> PLAYER_SKILLS =
            ATTACHMENT_TYPES.register("player_skills",
                    () -> AttachmentType.serializable(PlayerSkillData::new)
                            .copyOnDeath()
                            .build());
}