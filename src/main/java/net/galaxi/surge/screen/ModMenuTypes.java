package net.galaxi.surge.screen;

import net.galaxi.surge.Surge;
import net.galaxi.surge.screen.custom.SkillLoadoutMenu;
import net.galaxi.surge.screen.custom.SkillOrbMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Surge.MOD_ID);

    public static final Supplier<MenuType<SkillOrbMenu>> SKILL_ORB_MENU =
            MENUS.register("skill_orb_menu",
                    () -> IMenuTypeExtension.create(SkillOrbMenu::new));

    public static final Supplier<MenuType<SkillLoadoutMenu>> SKILL_LOADOUT_MENU =
            MENUS.register("skill_loadout_menu",
                    () -> new MenuType<>(SkillLoadoutMenu::new, FeatureFlags.DEFAULT_FLAGS));
}