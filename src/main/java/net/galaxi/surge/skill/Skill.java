package net.galaxi.surge.skill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Skill {
    private final String id;
    private final Component name;
    private final Component description;
    private final SkillRarity rarity;
    private final SkillType type;
    private final Set<SkillTag> tags;
    private final ResourceLocation texture;
    private final Map<String, Double> values;
    private final SkillTrigger trigger;

    public Skill(String id, Component name, Component description, SkillRarity rarity,
                 SkillType type, Set<SkillTag> tags, ResourceLocation texture, Map<String, Double> values, SkillTrigger trigger) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.type = type;
        this.tags = tags;
        this.texture = texture;
        this.values = values != null ? values : new HashMap<>();
        this.trigger = trigger != null ? trigger : SkillTrigger.NONE;
    }

    public Skill(String id, Component name, Component description, SkillRarity rarity,
                 SkillType type, Set<SkillTag> tags, ResourceLocation texture, Map<String, Double> values) {
        this(id, name, description, rarity, type, tags, texture, values, SkillTrigger.NONE);
    }

    public Skill(String id, Component name, Component description, SkillRarity rarity,
                 SkillType type, Set<SkillTag> tags, ResourceLocation texture) {
        this(id, name, description, rarity, type, tags, texture, null, SkillTrigger.NONE);
    }

    public abstract void execute(Player player);

    public String getId() {
        return id;
    }

    public Component getName() {
        return name;
    }

    public Component getDescription() {
        return description;
    }

    public SkillRarity getRarity() {
        return rarity;
    }

    public SkillType getType() {
        return type;
    }

    public Set<SkillTag> getTags() {
        return tags;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public double getValue(String key) {
        return values.getOrDefault(key, 0.0);
    }

    public Map<String, Double> getValues() {
        return values;
    }

    public SkillTrigger getTrigger() {
        return trigger;
    }

    public double getModifiedValue(String key, Player player) {
        double baseValue = getValue(key);
        PlayerSkillData data = player.getData(ModAttachments.PLAYER_SKILLS);

        for (Skill skill : data.getSkills()) {
            if (skill.getType() == SkillType.PASSIVE) {
                boolean sharesTag = this.tags.stream().anyMatch(skill::hasTag);
                if (!sharesTag) continue;

                for (SkillTag tag : this.tags) {
                    if (skill.hasTag(tag)) {
                        String modifierKey = tag.name().toLowerCase() + "_" + key + "_multiplier";
                        double multiplier = skill.getValue(modifierKey);
                        if (multiplier != 0.0) {
                            baseValue *= multiplier;
                        }

                        String flatKey = tag.name().toLowerCase() + "_" + key + "_flat";
                        double flat = skill.getValue(flatKey);
                        if (flat != 0.0) {
                            baseValue += flat;
                        }
                    }
                }
            }
        }

        return baseValue;
    }

    public boolean hasTag(SkillTag tag) {
        return tags.contains(tag);
    }
}