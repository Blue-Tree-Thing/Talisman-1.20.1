package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.custom.ShadowHandEntity;
import net.bluetree.talisman.entities.custom.SludgeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ShadowHandModel extends GeoModel<ShadowHandEntity> {

    @Override
    public Identifier getModelResource(ShadowHandEntity object) {
        return new Identifier(Talisman.MOD_ID, "geo/shadow_hand.geo.json");
    }

    @Override
    public Identifier getTextureResource(ShadowHandEntity object) {
        return new Identifier(Talisman.MOD_ID, "textures/entity/shadow_hand_texture.png");
    }

    @Override
    public Identifier getAnimationResource(ShadowHandEntity animatable) {
        // Return an empty animation file or a default one
        return new Identifier(Talisman.MOD_ID, "animations/shadow_hand.animation.json");
    }
}