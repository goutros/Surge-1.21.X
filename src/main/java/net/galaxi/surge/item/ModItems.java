package net.galaxi.surge.item;

import net.galaxi.surge.Surge;
import net.galaxi.surge.item.custom.SkillOrbItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Surge.MOD_ID);

    public static final DeferredItem<Item> SKILL_ORB = ITEMS.register("skill_orb",
            () -> new SkillOrbItem(new Item.Properties().rarity(Rarity.RARE)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
