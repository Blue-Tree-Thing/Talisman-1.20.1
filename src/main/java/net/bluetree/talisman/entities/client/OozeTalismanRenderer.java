package net.bluetree.talisman.entities.client;


import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.projectile.OozeTalismanEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class OozeTalismanRenderer extends ProjectileEntityRenderer<OozeTalismanEntity> {

    private static final Identifier TEXTURE = new Identifier(Talisman.MOD_ID, "textures/item/ooze_talisman.png");

    public OozeTalismanRenderer(EntityRendererFactory.Context context) {
        super(context);
    }



    @Override
    public Identifier getTexture(OozeTalismanEntity entity) {
        return TEXTURE;
    }
}