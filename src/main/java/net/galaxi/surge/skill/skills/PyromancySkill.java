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

public class PyromancySkill extends Skill {
    public PyromancySkill() {
        super(
                "pyromancy",
                Component.literal("Pyromancy"),
                Component.literal("Increases fire skill explosion power by 50%"),
                SkillRarity.RARE,
                SkillType.PASSIVE,
                Set.of(SkillTag.FIRE, SkillTag.UTILITY),
                ResourceLocation.fromNamespaceAndPath(Surge.MOD_ID, "textures/skill_cards/skill_card_pyromancy.png"),
                Map.of(
                        "fire_explosion_power_multiplier", 1.5
                )
        );
    }

    @Override
    public void execute(Player player) {
        // Passive skills don't execute, they modify values
    }
}
