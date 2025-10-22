package net.galaxi.surge.skill.skills;

import net.galaxi.surge.Surge;
import net.galaxi.surge.skill.Skill;
import net.galaxi.surge.skill.SkillRarity;
import net.galaxi.surge.skill.SkillTag;
import net.galaxi.surge.skill.SkillType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Set;

public class MultishotSkill extends Skill {
    public MultishotSkill() {
        super(
                "multishot",
                Component.literal("Multishot"),
                Component.literal("Shoot +1 additional projectile"),
                SkillRarity.UNCOMMON,
                SkillType.PASSIVE,
                Set.of(SkillTag.COMBAT, SkillTag.UTILITY),
                ResourceLocation.fromNamespaceAndPath(Surge.MOD_ID, "textures/skill_cards/skill_card_multishot.png"),
                Map.of(
                        "combat_projectile_count_flat", 1.0,
                        "fire_projectile_count_flat", 1.0,
                        "water_projectile_count_flat", 1.0,
                        "earth_projectile_count_flat", 1.0,
                        "air_projectile_count_flat", 1.0,
                        "lightning_projectile_count_flat", 1.0,
                        "dark_projectile_count_flat", 1.0,
                        "light_projectile_count_flat", 1.0
                )
        );
    }

    @Override
    public void execute(Player player) {
        // Passive skill - doesn't execute
    }
}
