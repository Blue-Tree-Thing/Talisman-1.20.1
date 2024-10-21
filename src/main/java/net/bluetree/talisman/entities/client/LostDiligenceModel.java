package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.custom.GumgarEntity;
import net.bluetree.talisman.entities.custom.LostDiligenceEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class LostDiligenceModel extends GeoModel<LostDiligenceEntity> {

    @Override
    public Identifier getModelResource(LostDiligenceEntity object) {
        return new Identifier(Talisman.MOD_ID, "geo/lost_dilligence.geo.json");
    }

    @Override
    public Identifier getTextureResource(LostDiligenceEntity object) {
        return new Identifier(Talisman.MOD_ID, "textures/entity/lost_dilligence_texture.png");
    }

    @Override
    public Identifier getAnimationResource(LostDiligenceEntity animatable) {
        // Return an empty animation file or a default one
        return new Identifier(Talisman.MOD_ID, "animations/lost_dilligence.animation.json");
    }
}