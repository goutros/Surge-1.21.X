package net.galaxi.surge.skill;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public enum SkillTrigger {
    RIGHT_CLICK_FIRE_CHARGE(Items.FIRE_CHARGE),
    RIGHT_CLICK_SNOWBALL(Items.SNOWBALL),
    RIGHT_CLICK_ENDER_PEARL(Items.ENDER_PEARL),
    RIGHT_CLICK_EMPTY_HAND(null),
    ON_HIT,
    ON_KILL,
    ON_DAMAGED,
    ON_JUMP,
    NONE;

    private final Item requiredItem;

    SkillTrigger(Item requiredItem) {
        this.requiredItem = requiredItem;
    }

    SkillTrigger() {
        this.requiredItem = null;
    }

    public Item getRequiredItem() {
        return requiredItem;
    }

    public boolean hasRequiredItem() {
        return requiredItem != null;
    }
}
