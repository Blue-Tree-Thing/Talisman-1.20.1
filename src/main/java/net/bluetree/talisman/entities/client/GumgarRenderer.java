package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.entities.custom.GumgarEntity;
import net.bluetree.talisman.entities.custom.SludgeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GumgarRenderer extends GeoEntityRenderer<GumgarEntity> {

    public GumgarRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new GumgarModel());
        this.shadowRadius = 0.7f; // Adjust shadow size if needed
    }

    @Override
    public Identifier getTextureLocation(GumgarEntity animatable) {
        return new Identifier("talisman", "textures/entity/gumgar_texture.png");
    }
}
