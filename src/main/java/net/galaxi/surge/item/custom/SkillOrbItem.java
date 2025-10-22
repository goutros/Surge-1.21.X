package net.galaxi.surge.item.custom;

import net.galaxi.surge.screen.custom.SkillOrbMenu;
import net.galaxi.surge.skill.Skill;
import net.galaxi.surge.skill.SkillRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkillOrbItem extends Item {

    public SkillOrbItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            List<Skill> skills = generateRandomSkills(3);

            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, playerEntity) -> {
                        SkillOrbMenu menu = new SkillOrbMenu(containerId, playerInventory, null);
                        menu.getAvailableSkills().addAll(skills);
                        return menu;
                    },
                    Component.literal("Skill Selection")
            ), buf -> {
                buf.writeInt(skills.size());
                for (Skill skill : skills) {
                    buf.writeUtf(skill.getId());
                }
            });
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private List<Skill> generateRandomSkills(int count) {
        List<Skill> allSkills = new ArrayList<>(SkillRegistry.getAll());
        Collections.shuffle(allSkills);
        return allSkills.stream().limit(count).toList();
    }
}