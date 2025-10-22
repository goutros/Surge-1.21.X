package net.galaxi.surge.event;


import net.galaxi.surge.Surge;
import net.galaxi.surge.screen.ModMenuTypes;
import net.galaxi.surge.screen.custom.SkillOrbScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Surge.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.SKILL_ORB_MENU.get(), SkillOrbScreen::new);
    }
}
