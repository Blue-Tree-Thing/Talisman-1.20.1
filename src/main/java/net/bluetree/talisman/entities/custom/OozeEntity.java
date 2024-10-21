package net.bluetree.talisman.entities.custom;

import net.bluetree.talisman.sounds.ModSounds;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;

import static software.bernie.geckolib.core.animation.Animation.LoopType.LOOP;

public class OozeEntity extends PathAwareEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ItemStack carriedItem = ItemStack.EMPTY; // The item it carries
    private BlockPos targetChestPos; // Target chest position

    // Define the animations as RawAnimation instances
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("animation.ooze.idle", LOOP);
    private static final RawAnimation MOVE_ANIMATION = RawAnimation.begin().then("animation.ooze.moving", LOOP);

    public OozeEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0); // Added follow range for tracking enemies
    }

    @Override
    protected void initGoals() {
        // Add custom goals
        this.goalSelector.add(1, new PickUpItemsGoal());
        this.goalSelector.add(2, new DepositInChestGoal());
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(1, new RevengeGoal(this));
        // Add target goal to attack hostile entities, but ignore ShadowHandEntity
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, HostileEntity.class, true, entity -> !(entity instanceof ShadowHandEntity)));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            // Spawn particles every few ticks (e.g., every 5 ticks)
            if (this.age % 5 == 0) {
                this.getWorld().addParticle(
                        ParticleTypes.SQUID_INK,   // Squid ink particle
                        this.getX(),               // X position
                        this.getY(),               // Y position
                        this.getZ(),               // Z position
                        0.0, 0.0, 0.0              // Zero velocity to keep them stationary
                );
            }
        }
    }

    private PlayState predicate(AnimationState<OozeEntity> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(MOVE_ANIMATION);
        } else {
            state.getController().setAnimation(IDLE_ANIMATION);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 3, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return this.age;
    }

    // Custom goal to pick up items
    private class PickUpItemsGoal extends Goal {
        @Override
        public boolean canStart() {
            // If already carrying an item, don't start
            if (!carriedItem.isEmpty()) {
                return false;
            }

            // Find nearby items
            List<ItemEntity> nearbyItems = getWorld().getEntitiesByClass(ItemEntity.class, new Box(getBlockPos()).expand(5), itemEntity -> true);
            return !nearbyItems.isEmpty();
        }

        @Override
        public void start() {
            // Get the closest item and start moving toward it
            List<ItemEntity> nearbyItems = getWorld().getEntitiesByClass(ItemEntity.class, new Box(getBlockPos()).expand(5), itemEntity -> true);
            if (!nearbyItems.isEmpty()) {
                ItemEntity closestItem = nearbyItems.get(0);
                getNavigation().startMovingTo(closestItem, 1.0D);
            }
        }

        @Override
        public void tick() {
            List<ItemEntity> nearbyItems = getWorld().getEntitiesByClass(ItemEntity.class, new Box(getBlockPos()).expand(2), itemEntity -> true);

            // Pick up the closest item when within range
            if (!nearbyItems.isEmpty()) {
                ItemEntity closestItem = nearbyItems.get(0);

                // Check if it is indeed an ItemEntity before interacting
                if (closestItem instanceof ItemEntity) {
                    carriedItem = closestItem.getStack();
                    closestItem.discard(); // Remove the item from the world
                }
            }
        }

        @Override
        public boolean shouldContinue() {
            return carriedItem.isEmpty();
        }
    }

    // Custom goal to deposit items in chests
    private class DepositInChestGoal extends Goal {
        @Override
        public boolean canStart() {
            if (carriedItem.isEmpty()) {
                return false;
            }

            // Find the closest chest
            Optional<BlockPos> chestPos = findNearbyChest();
            if (chestPos.isPresent()) {
                targetChestPos = chestPos.get();
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            // Move towards the chest
            if (targetChestPos != null) {
                getNavigation().startMovingTo(targetChestPos.getX(), targetChestPos.getY(), targetChestPos.getZ(), 1.0D);
            }
        }

        @Override
        public void tick() {
            if (targetChestPos != null && getBlockPos().isWithinDistance(targetChestPos, 2.0)) {
                BlockEntity blockEntity = getWorld().getBlockEntity(targetChestPos);

                if (blockEntity instanceof ChestBlockEntity chest) {
                    for (int i = 0; i < chest.size(); i++) {
                        ItemStack stackInSlot = chest.getStack(i);

                        if (stackInSlot.isEmpty()) {
                            chest.setStack(i, carriedItem);
                            carriedItem = ItemStack.EMPTY;
                            break;
                        } else if (ItemStack.areItemsEqual(stackInSlot, carriedItem) && stackInSlot.getCount() < stackInSlot.getMaxCount()) {
                            int transferableAmount = Math.min(carriedItem.getCount(), stackInSlot.getMaxCount() - stackInSlot.getCount());
                            stackInSlot.increment(transferableAmount);
                            carriedItem.decrement(transferableAmount);

                            if (carriedItem.isEmpty()) {
                                break;
                            }
                        }
                    }
                }
            }
        }


        @Override
        public boolean shouldContinue() {
            return !carriedItem.isEmpty();
        }

        // Find the closest chest within a 10-block radius
        private Optional<BlockPos> findNearbyChest() {
            BlockPos entityPos = getBlockPos();
            BlockPos.Mutable searchPos = new BlockPos.Mutable();
            for (int x = -10; x <= 10; x++) {
                for (int y = -3; y <= 3; y++) {
                    for (int z = -10; z <= 10; z++) {
                        searchPos.set(entityPos.getX() + x, entityPos.getY() + y, entityPos.getZ() + z);
                        BlockEntity blockEntity = getWorld().getBlockEntity(searchPos);
                        if (blockEntity instanceof ChestBlockEntity) {
                            return Optional.of(searchPos);
                        }
                    }
                }
            }
            return Optional.empty();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GENERIC_OOZE_AMBIENT_1;
    }


}
