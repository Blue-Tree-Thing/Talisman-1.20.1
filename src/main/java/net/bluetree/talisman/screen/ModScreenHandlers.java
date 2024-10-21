package net.bluetree.talisman.screen;

import net.bluetree.talisman.Talisman;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<VirtueAltarScreenHandler> VIRTUE_ALTAR_SCREEN_HANDLER;

    public static void registerAllScreenHandlers() {
        VIRTUE_ALTAR_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                new Identifier(Talisman.MOD_ID, "virtue_altar_screen_handler"),
                new ScreenHandlerType<>(VirtueAltarScreenHandler::new, FeatureSet.empty())
        );
    }
}
