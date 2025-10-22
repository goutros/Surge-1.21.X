package net.galaxi.surge.skill;

public enum SkillRarity {
    COMMON(0xFFFFFF),
    UNCOMMON(0x55FF55),
    RARE(0x5555FF),
    EPIC(0xAA00AA),
    LEGENDARY(0xFFAA00),
    MYTHIC(0xFF55FF),
    SPECIAL(0xFF0000);

    private final int color;

    SkillRarity(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}