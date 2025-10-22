package net.galaxi.surge.screen.custom;

import net.galaxi.surge.screen.ModMenuTypes;
import net.galaxi.surge.skill.Skill;
import net.galaxi.surge.skill.SkillRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SkillOrbMenu extends AbstractContainerMenu {
    private final List<Skill> availableSkills;

    public SkillOrbMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(ModMenuTypes.SKILL_ORB_MENU.get(), containerId);
        this.availableSkills = new ArrayList<>();

        if (extraData != null) {
            int count = extraData.readInt();
            for (int i = 0; i < count; i++) {
                String skillId = extraData.readUtf();
                Skill skill = SkillRegistry.get(skillId);
                if (skill != null) {
                    availableSkills.add(skill);
                }
            }
        }
    }

    public List<Skill> getAvailableSkills() {
        return availableSkills;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}