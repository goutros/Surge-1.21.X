package net.galaxi.surge.skill;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillRegistry {
    private static final Map<String, Skill> SKILLS = new HashMap<>();

    public static void register(Skill skill) {
        SKILLS.put(skill.getId(), skill);
    }

    public static Skill get(String id) {
        return SKILLS.get(id);
    }

    public static Collection<Skill> getAll() {
        return SKILLS.values();
    }

    public static List<Skill> getByTag(SkillTag tag) {
        return SKILLS.values().stream()
                .filter(skill -> skill.hasTag(tag))
                .toList();
    }
}