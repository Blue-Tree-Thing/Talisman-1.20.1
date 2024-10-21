package net.bluetree.talisman.entities.client;

import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.custom.DiligentGuardEntity;
import net.bluetree.talisman.entities.custom.GumgarEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DIligentGuardModel extends GeoModel<DiligentGuardEntity> {

    @Override
    public Identifier getModelResource(DiligentGuardEntity object) {
        return new Identifier(Talisman.MOD_ID, "geo/diligent_guard.geo.json");
    }

    @Override
    public Identifier getTextureResource(DiligentGuardEntity object) {
        return new Identifier(Talisman.MOD_ID, "textures/entity/diligent_guard_texture_2.png");
    }

    @Override
    public Identifier getAnimationResource(DiligentGuardEntity animatable) {
        // Return an empty animation file or a default one
        return new Identifier(Talisman.MOD_ID, "animations/diligent_guard.animation.json");
    }
}