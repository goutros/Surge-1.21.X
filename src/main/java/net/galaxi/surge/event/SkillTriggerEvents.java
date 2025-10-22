package net.galaxi.surge.event;

import net.galaxi.surge.Surge;
import net.galaxi.surge.skill.ModAttachments;
import net.galaxi.surge.skill.PlayerSkillData;
import net.galaxi.surge.skill.Skill;
import net.galaxi.surge.skill.SkillTrigger;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Surge.MOD_ID)
public class SkillTriggerEvents {

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);

        if (stack.isEmpty()) {
            return;
        }

        PlayerSkillData skillData = player.getData(ModAttachments.PLAYER_SKILLS);

        // Execute skills in order (1 to X) that match the trigger
        boolean triggeredAny = false;
        for (Skill skill : skillData.getActiveSkills()) {
            SkillTrigger trigger = skill.getTrigger();

            if (trigger.hasRequiredItem() && stack.is(trigger.getRequiredItem())) {
                Surge.LOGGER.info("Executing skill: {} in order", skill.getId());
                skill.execute(player);
                triggeredAny = true;
                // Continue to next skill - execute ALL matching skills in order
            }
        }

        if (triggeredAny) {
            event.setCanceled(true);
        }
    }
}
