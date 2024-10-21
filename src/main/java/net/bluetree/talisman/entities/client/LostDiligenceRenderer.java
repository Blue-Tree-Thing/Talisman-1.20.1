package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.entities.custom.LostDiligenceEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LostDiligenceRenderer extends GeoEntityRenderer<LostDiligenceEntity> {

    public LostDiligenceRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new LostDiligenceModel());
        this.shadowRadius = 1.5f; // Adjust shadow size if needed
    }

    @Override
    public Identifier getTextureLocation(LostDiligenceEntity animatable) {
        return new Identifier("talisman", "textures/entity/lost_dilligence_texture.png");
    }
}
