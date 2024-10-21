package net.bluetree.talisman.items.custom;

import net.bluetree.talisman.entities.projectile.ShadowHandTalismanEntity;
import net.bluetree.talisman.entities.projectile.SludgeTalismanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ShadowHandTalismanItem extends Item implements Vanishable {

    public ShadowHandTalismanItem(Settings settings) {
        // Set the max damage (durability) here, for example, 100 uses
        super(settings.maxDamage(30));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS,
                0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!world.isClient) {
            ShadowHandTalismanEntity talismanEntity = new ShadowHandTalismanEntity(world, user);
            talismanEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(talismanEntity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));

        // Damage the item instead of decrementing it
        itemStack.damage(1, user, (player) -> {
            player.sendToolBreakStatus(hand);
        });

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
