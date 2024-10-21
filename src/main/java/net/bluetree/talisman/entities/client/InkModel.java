package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.custom.GumgarEntity;
import net.bluetree.talisman.entities.projectile.InkProjectileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class InkModel extends GeoModel<InkProjectileEntity> {

    @Override
    public Identifier getModelResource(InkProjectileEntity object) {
        return new Identifier(Talisman.MOD_ID, "geo/ink_projectile.geo.json");
    }

    @Override
    public Identifier getTextureResource(InkProjectileEntity object) {
        return new Identifier(Talisman.MOD_ID, "textures/entity/ink_projectile_texture.png");
    }

    @Override
    public Identifier getAnimationResource(InkProjectileEntity animatable) {
        // Return an empty animation file or a default one
        return new Identifier(Talisman.MOD_ID, "animations/ink.animation.json");
    }
}