package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.entities.custom.DiligentGuardEntity;
import net.bluetree.talisman.entities.custom.GumgarEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DiligentGuardRenderer extends GeoEntityRenderer<DiligentGuardEntity> {

    public DiligentGuardRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DIligentGuardModel());
        this.shadowRadius = 0.7f; // Adjust shadow size if needed
    }

    @Override
    public Identifier getTextureLocation(DiligentGuardEntity animatable) {
        return new Identifier("talisman", "textures/entity/diligent_guard_texture_2.png");
    }
}
