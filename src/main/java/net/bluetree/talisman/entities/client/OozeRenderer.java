package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.entities.custom.OozeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OozeRenderer extends GeoEntityRenderer<OozeEntity> {

    public OozeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OozeModel());
        this.shadowRadius = 0.5f; // Adjust shadow size if needed
    }

    @Override
    public Identifier getTextureLocation(OozeEntity animatable) {
        return new Identifier("talisman", "textures/entity/ooze_texture.png");
    }
}
