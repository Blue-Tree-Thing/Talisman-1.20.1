package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.custom.OozeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class OozeModel extends GeoModel<OozeEntity> {

    @Override
    public Identifier getModelResource(OozeEntity object) {
        return new Identifier(Talisman.MOD_ID, "geo/ooze_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(OozeEntity object) {
        return new Identifier(Talisman.MOD_ID, "textures/entity/ooze_texture.png");
    }

    @Override
    public Identifier getAnimationResource(OozeEntity animatable) {
        // Return an empty animation file or a default one
        return new Identifier(Talisman.MOD_ID, "animations/ooze.animation.json");
    }
}