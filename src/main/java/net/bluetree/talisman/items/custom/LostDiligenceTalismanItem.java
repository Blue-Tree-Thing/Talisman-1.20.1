package net.bluetree.talisman.items.custom;

import net.bluetree.talisman.entities.custom.LostDiligenceEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class LostDiligenceTalismanItem extends Item implements Vanishable {

    private final EntityType<LostDiligenceEntity> lostDiligenceEntityType;

    public LostDiligenceTalismanItem(Settings settings, EntityType<LostDiligenceEntity> lostDiligenceEntityType) {
        super(settings.maxDamage(1));
        this.lostDiligenceEntityType = lostDiligenceEntityType;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS,
                0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            BlockPos spawnPosition = new BlockPos(MathHelper.floor(user.getX()), MathHelper.floor(user.getY()), MathHelper.floor(user.getZ()));
            LostDiligenceEntity lostDiligenceEntity = lostDiligenceEntityType.create(serverWorld);

            if (lostDiligenceEntity != null) {
                lostDiligenceEntity.refreshPositionAndAngles(spawnPosition, user.getYaw(), 0.0F);
                serverWorld.spawnEntity(lostDiligenceEntity);
            }
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));

        // Damage the item instead of decrementing it
        itemStack.damage(1, user, (player) -> player.sendToolBreakStatus(hand));

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
