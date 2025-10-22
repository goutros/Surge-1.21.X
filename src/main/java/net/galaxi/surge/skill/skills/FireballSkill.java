package net.galaxi.surge.skill.skills;

import net.galaxi.surge.Surge;
import net.galaxi.surge.skill.Skill;
import net.galaxi.surge.skill.SkillRarity;
import net.galaxi.surge.skill.SkillTag;
import net.galaxi.surge.skill.SkillTrigger;
import net.galaxi.surge.skill.SkillType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Set;

public class FireballSkill extends Skill {
    public FireballSkill() {
        super(
                "fireball",
                Component.literal("Fireball"),
                Component.literal("Launch a ghast fireball (Right-click Fire Charge)"),
                SkillRarity.COMMON,
                SkillType.ACTIVE,
                Set.of(SkillTag.FIRE, SkillTag.COMBAT),
                ResourceLocation.fromNamespaceAndPath(Surge.MOD_ID, "textures/skill_cards/skill_card_fireball.png"),
                Map.of("projectile_count", 1.0, "explosion_power", 1.0),
                SkillTrigger.RIGHT_CLICK_FIRE_CHARGE
        );
    }

    @Override
    public void execute(Player player) {
        if (!player.getInventory().contains(Items.FIRE_CHARGE.getDefaultInstance())) {
            return;
        }

        int count = (int) getModifiedValue("projectile_count", player);
        int power = (int) getModifiedValue("explosion_power", player);

        Vec3 look = player.getLookAngle();
        Vec3 up = new Vec3(0, 1, 0); // Use world up instead of player up
        Vec3 right = look.cross(up).normalize();

        // Dynamic spread: scales with projectile count to prevent collisions
        // Base spread of 45 degrees, increases by 5 degrees per additional projectile
        float spreadAngle = count > 1 ? Math.min(45.0F + (count - 2) * 5.0F, 180.0F) : 0.0F;
        float angleStep = count > 1 ? spreadAngle / (count - 1) : 0;
        float startAngle = count > 1 ? -spreadAngle / 2 : 0;

        for (int i = 0; i < count; i++) {
            float currentAngle = startAngle + (i * angleStep);
            float radians = (float) Math.toRadians(currentAngle);

            // Rotate look vector horizontally using quaternion-style rotation
            float cos = (float) Math.cos(radians);
            float sin = (float) Math.sin(radians);

            // Calculate rotated direction: rotate around vertical axis
            Vec3 rotatedLook = new Vec3(
                    look.x * cos - look.z * sin,
                    look.y,
                    look.x * sin + look.z * cos
            ).normalize();

            // Stagger spawn positions to prevent collisions
            Vec3 lateralOffset = right.scale(sin * 2.0);
            Vec3 startPos = player.getEyePosition()
                    .add(look.scale(2))
                    .add(lateralOffset)
                    .add(rotatedLook.scale(0.3 * i));

            LargeFireball fireball = new LargeFireball(
                    player.level(),
                    player,
                    rotatedLook,
                    power
            );

            fireball.setPos(startPos.x, startPos.y, startPos.z);

            player.level().addFreshEntity(fireball);
        }

        player.getInventory().clearOrCountMatchingItems(
                stack -> stack.is(Items.FIRE_CHARGE),
                1,
                player.inventoryMenu.getCraftSlots()
        );
    }
}