package net.bluetree.talisman.entities.client;


import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.projectile.ShadowHandTalismanEntity;
import net.bluetree.talisman.entities.projectile.SludgeTalismanEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class ShadowHandTalismanRenderer extends ProjectileEntityRenderer<ShadowHandTalismanEntity> {

    private static final Identifier TEXTURE = new Identifier(Talisman.MOD_ID, "textures/item/shadow_hand_talisman.png");

    public ShadowHandTalismanRenderer(EntityRendererFactory.Context context) {
        super(context);
    }



    @Override
    public Identifier getTexture(ShadowHandTalismanEntity entity) {
        return TEXTURE;
    }
}