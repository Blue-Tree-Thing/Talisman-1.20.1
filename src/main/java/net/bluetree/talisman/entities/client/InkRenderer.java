package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.entities.custom.GumgarEntity;
import net.bluetree.talisman.entities.projectile.InkProjectileEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class InkRenderer extends GeoEntityRenderer<InkProjectileEntity> {

    public InkRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new InkModel());
        this.shadowRadius = 0.3f; // Adjust shadow size if needed
    }

    @Override
    public Identifier getTextureLocation(InkProjectileEntity animatable) {
        return new Identifier("talisman", "textures/entity/ink_projectile_texture.png");
    }
}
