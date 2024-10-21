package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.entities.custom.ShadowHandEntity;
import net.bluetree.talisman.entities.custom.SludgeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShadowHandRenderer extends GeoEntityRenderer<ShadowHandEntity> {

    public ShadowHandRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ShadowHandModel());
        this.shadowRadius = 0.8f; // Adjust shadow size if needed
    }

    @Override
    public Identifier getTextureLocation(ShadowHandEntity animatable) {
        return new Identifier("talisman", "textures/entity/shadow_hand_texture.png");
    }
}
