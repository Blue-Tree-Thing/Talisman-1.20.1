package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.custom.GumgarEntity;
import net.bluetree.talisman.entities.custom.SludgeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class GumgarModel extends GeoModel<GumgarEntity> {

    @Override
    public Identifier getModelResource(GumgarEntity object) {
        return new Identifier(Talisman.MOD_ID, "geo/gumgar.geo.json");
    }

    @Override
    public Identifier getTextureResource(GumgarEntity object) {
        return new Identifier(Talisman.MOD_ID, "textures/entity/gumgar_texture.png");
    }

    @Override
    public Identifier getAnimationResource(GumgarEntity animatable) {
        // Return an empty animation file or a default one
        return new Identifier(Talisman.MOD_ID, "animations/gumgar.animation.json");
    }
}