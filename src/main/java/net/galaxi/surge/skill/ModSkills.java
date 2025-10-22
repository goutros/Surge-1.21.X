package net.galaxi.surge.skill;

import net.galaxi.surge.skill.skills.FireballSkill;
import net.galaxi.surge.skill.skills.MultishotSkill;
import net.galaxi.surge.skill.skills.PyromancySkill;

public class ModSkills {
    public static void register() {
        SkillRegistry.register(new FireballSkill());
        SkillRegistry.register(new PyromancySkill());
        SkillRegistry.register(new MultishotSkill());
    }
}
