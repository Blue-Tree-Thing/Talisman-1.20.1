package net.bluetree.talisman;

import net.bluetree.talisman.entities.ModEntities;
import net.bluetree.talisman.entities.client.*;
import net.bluetree.talisman.screen.ModScreenHandlers;
import net.bluetree.talisman.screen.VirtueAltarScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;


public class TalismanClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register entity renderers
        EntityRendererRegistry.register(ModEntities.OOZE, OozeRenderer::new);
        EntityRendererRegistry.register(ModEntities.GUMGAR, GumgarRenderer::new);
        EntityRendererRegistry.register(ModEntities.SLUDGE, SludgeRenderer::new);
        EntityRendererRegistry.register(ModEntities.SHADOW_HAND, ShadowHandRenderer::new);
        EntityRendererRegistry.register(ModEntities.OOZE_TALISMAN_ENTITY, OozeTalismanRenderer::new);
        EntityRendererRegistry.register(ModEntities.SLUDGE_TALISMAN_ENTITY, SludgeTalismanRenderer::new);
        EntityRendererRegistry.register(ModEntities.SHADOW_HAND_TALISMAN_ENTITY, ShadowHandTalismanRenderer::new);
        EntityRendererRegistry.register(ModEntities.INK_PROJECTILE_ENTITY, InkRenderer::new);
        EntityRendererRegistry.register(ModEntities.LOST_DILIGENCE, LostDiligenceRenderer::new);
        EntityRendererRegistry.register(ModEntities.DILIGENT_GUARD, DiligentGuardRenderer::new);

        ScreenRegistry.register(ModScreenHandlers.VIRTUE_ALTAR_SCREEN_HANDLER, VirtueAltarScreen::new);



    }
}
