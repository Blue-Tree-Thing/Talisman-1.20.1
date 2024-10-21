package net.bluetree.talisman.items.custom;

import net.bluetree.talisman.entities.ModEntities;
import net.bluetree.talisman.entities.custom.GumgarEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GumgarSpawnItem extends Item {

    public GumgarSpawnItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            BlockPos blockPos = context.getBlockPos().offset(context.getSide());
            ItemStack itemStack = context.getStack();
            PlayerEntity playerEntity = context.getPlayer();

            // Spawn the GumgarEntity at the targeted position
            GumgarEntity gumgarEntity = ModEntities.GUMGAR.create(world);
            if (gumgarEntity != null) {
                gumgarEntity.refreshPositionAndAngles(blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, world.random.nextFloat() * 360F, 0);
                world.spawnEntity(gumgarEntity);

                // Decrease the item count if the player is not in creative mode
                if (playerEntity != null && !playerEntity.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}

