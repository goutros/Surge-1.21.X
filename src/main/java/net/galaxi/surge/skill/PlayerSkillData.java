package net.galaxi.surge.skill;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerSkillData implements INBTSerializable<CompoundTag> {
    private final List<String> skills = new ArrayList<>();

    public void addSkill(String skillId) {
        skills.add(skillId);
    }

    public void removeSkill(int index) {
        if (index >= 0 && index < skills.size()) {
            skills.remove(index);
        }
    }

    public void reorderSkills(List<String> newOrder) {
        skills.clear();
        skills.addAll(newOrder);
    }

    public List<String> getSkillIds() {
        return new ArrayList<>(skills);
    }

    public List<Skill> getSkills() {
        return skills.stream()
                .map(SkillRegistry::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public boolean hasSkill(String skillId) {
        return skills.contains(skillId);
    }

    public List<Skill> getActiveSkills() {
        return getSkills().stream()
                .filter(skill -> skill.getType() == SkillType.ACTIVE)
                .toList();
    }

    public List<Skill> getPassiveSkills() {
        return getSkills().stream()
                .filter(skill -> skill.getType() == SkillType.PASSIVE)
                .toList();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag skillList = new ListTag();
        for (String skillId : skills) {
            skillList.add(StringTag.valueOf(skillId));
        }
        tag.put("skills", skillList);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        skills.clear();
        ListTag skillList = tag.getList("skills", 8);
        for (int i = 0; i < skillList.size(); i++) {
            skills.add(skillList.getString(i));
        }
    }
}