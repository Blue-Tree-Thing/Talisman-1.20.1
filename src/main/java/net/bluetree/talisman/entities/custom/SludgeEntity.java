package net.bluetree.talisman.entities.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.bluetree.talisman.items.ModItems;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class SludgeEntity extends PathAwareEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Define the animations as RawAnimation instances
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("animation.sludge.idle", Animation.LoopType.LOOP);
    private static final RawAnimation MOVE_ANIMATION = RawAnimation.begin().then("animation.sludge.move", Animation.LoopType.LOOP);

    public SludgeEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void initGoals() {
        // Adding default goals for the sludge
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.5D, true)); // Attacks entities in melee range
        this.goalSelector.add(2, new FollowPlayerWithTalismanGoal(this, 1.2D, 10.0F)); // Follow player with Sludge Talisman
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8D)); // Wanders around when idle
        this.goalSelector.add(4, new LookAtEntityGoal(this, PathAwareEntity.class, 8.0F)); // Looks at nearby entities
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, HostileEntity.class, true)); // Targets hostile entities
    }

    @Override
    public void tick() {
        super.tick();
    }

    private PlayState predicate(AnimationState<SludgeEntity> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(MOVE_ANIMATION);
        } else {
            state.getController().setAnimation(IDLE_ANIMATION);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 1, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return this.age;
    }

    // Custom goal for following players with a sludge talisman
    private static class FollowPlayerWithTalismanGoal extends Goal {
        private final SludgeEntity sludgeEntity;
        private final double speed;
        private final float followRange;
        private PlayerEntity targetPlayer;

        public FollowPlayerWithTalismanGoal(SludgeEntity sludgeEntity, double speed, float followRange) {
            this.sludgeEntity = sludgeEntity;
            this.speed = speed;
            this.followRange = followRange;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            // Find the nearest player within the follow range
            List<PlayerEntity> nearbyPlayers = sludgeEntity.getWorld().getEntitiesByClass(PlayerEntity.class, sludgeEntity.getBoundingBox().expand(followRange), player -> true);
            for (PlayerEntity player : nearbyPlayers) {
                if (hasSingleSludgeTalisman(player)) {
                    targetPlayer = player;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            // Continue following while the player is still in range and has the talisman
            return targetPlayer != null && targetPlayer.isAlive() && sludgeEntity.squaredDistanceTo(targetPlayer) <= followRange * followRange && hasSingleSludgeTalisman(targetPlayer);
        }

        @Override
        public void start() {
            sludgeEntity.getNavigation().startMovingTo(targetPlayer, speed);
        }

        @Override
        public void stop() {
            targetPlayer = null;
            sludgeEntity.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (targetPlayer != null) {
                sludgeEntity.getLookControl().lookAt(targetPlayer, 30.0F, 30.0F);
                sludgeEntity.getNavigation().startMovingTo(targetPlayer, speed);
            }
        }

        // Helper method to check if the player has exactly one Sludge Talisman item equipped
        private boolean hasSingleSludgeTalisman(PlayerEntity player) {
            int talismanCount = 0;

            // Check player's hand slots
            if (isSludgeTalisman(player.getMainHandStack())) {
                talismanCount++;
            }
            if (isSludgeTalisman(player.getOffHandStack())) {
                talismanCount++;
            }

            // Check player's armor slots
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (isSludgeTalisman(player.getEquippedStack(slot))) {
                    talismanCount++;
                }
            }

            // Return true only if the player has exactly one Sludge Talisman
            return talismanCount == 1;
        }

        // Method to determine if the given ItemStack is the Sludge Talisman
        private boolean isSludgeTalisman(ItemStack stack) {
            return stack.getItem() == ModItems.SLUDGE_TALISMAN;
        }
    }
}
