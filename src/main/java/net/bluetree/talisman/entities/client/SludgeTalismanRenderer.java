package net.bluetree.talisman.entities.client;


import net.bluetree.talisman.Talisman;
import net.bluetree.talisman.entities.projectile.OozeTalismanEntity;
import net.bluetree.talisman.entities.projectile.SludgeTalismanEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class SludgeTalismanRenderer extends ProjectileEntityRenderer<SludgeTalismanEntity> {

    private static final Identifier TEXTURE = new Identifier(Talisman.MOD_ID, "textures/item/sludge_talisman.png");

    public SludgeTalismanRenderer(EntityRendererFactory.Context context) {
        super(context);
    }



    @Override
    public Identifier getTexture(SludgeTalismanEntity entity) {
        return TEXTURE;
    }
}