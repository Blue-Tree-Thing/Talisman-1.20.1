package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.custom.OozeEntity;
import net.bluetree.talisman.entities.custom.SludgeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SludgeModel extends GeoModel<SludgeEntity> {

    @Override
    public Identifier getModelResource(SludgeEntity object) {
        return new Identifier(Talisman.MOD_ID, "geo/sludge_entity.geo.json");
    }

    @Override
    public Identifier getTextureResource(SludgeEntity object) {
        return new Identifier(Talisman.MOD_ID, "textures/entity/sludge_texture.png");
    }

    @Override
    public Identifier getAnimationResource(SludgeEntity animatable) {
        // Return an empty animation file or a default one
        return new Identifier(Talisman.MOD_ID, "animations/sludge.animation.json");
    }
}