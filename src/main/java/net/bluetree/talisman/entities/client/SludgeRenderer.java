package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.entities.custom.OozeEntity;
import net.bluetree.talisman.entities.custom.SludgeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SludgeRenderer extends GeoEntityRenderer<SludgeEntity> {

    public SludgeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SludgeModel());
        this.shadowRadius = 0.7f; // Adjust shadow size if needed
    }

    @Override
    public Identifier getTextureLocation(SludgeEntity animatable) {
        return new Identifier("talisman", "textures/entity/sludge_texture.png");
    }
}
