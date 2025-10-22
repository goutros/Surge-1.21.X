package net.galaxi.surge.event;

import net.galaxi.surge.Surge;
import net.galaxi.surge.client.ModKeybinds;
import net.galaxi.surge.screen.ModMenuTypes;
import net.galaxi.surge.screen.custom.SkillLoadoutMenu;
import net.galaxi.surge.screen.custom.SkillLoadoutScreen;
import net.galaxi.surge.screen.custom.SkillOrbScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Surge.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.SKILL_ORB_MENU.get(), SkillOrbScreen::new);
        event.register(ModMenuTypes.SKILL_LOADOUT_MENU.get(), SkillLoadoutScreen::new);
    }

    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        Surge.LOGGER.info("Registering keybind: {}", ModKeybinds.OPEN_SKILL_LOADOUT.getName());
        event.register(ModKeybinds.OPEN_SKILL_LOADOUT);
    }

    @SubscribeEvent
    public static void onClientTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide() && event.getEntity() == Minecraft.getInstance().player) {
            while (ModKeybinds.OPEN_SKILL_LOADOUT.consumeClick()) {
                Surge.LOGGER.info("Opening Skill Loadout GUI!");
                Minecraft mc = Minecraft.getInstance();
                mc.setScreen(new SkillLoadoutScreen(
                        new SkillLoadoutMenu(0, mc.player.getInventory()),
                        mc.player.getInventory(),
                        net.minecraft.network.chat.Component.literal("Skill Loadout")
                ));
            }
        }
    }
}
