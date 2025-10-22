package net.galaxi.surge.screen.custom;

import net.galaxi.surge.screen.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class SkillLoadoutMenu extends AbstractContainerMenu {

    public SkillLoadoutMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.SKILL_LOADOUT_MENU.get(), containerId);
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
