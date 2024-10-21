package net.bluetree.talisman.entities.custom;

import net.bluetree.talisman.entities.projectile.InkProjectileEntity;
import net.bluetree.talisman.sounds.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

import static software.bernie.geckolib.core.animation.Animation.LoopType.LOOP;

public class DiligentGuardEntity extends PathAwareEntity implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("animation.diligent_guard.idle", LOOP);
    private static final RawAnimation MOVE_ANIMATION = RawAnimation.begin().then("animation.diligent_guard.move", LOOP);
    private static final RawAnimation SPECIAL_ATTACK_ANIMATION = RawAnimation.begin().then("animation.diligent_guard.attack", LOOP);

    private static final TrackedData<Integer> ATTACK_TIMER = DataTracker.registerData(DiligentGuardEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<BlockPos> HOME_POS = DataTracker.registerData(DiligentGuardEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

    private static final double MAX_DISTANCE_FROM_HOME = 10.0;
    private boolean hasPlayedAttackAnimation = false;
    private final Random random = new Random();

    public DiligentGuardEntity(EntityType<DiligentGuardEntity> entityType, World world) {
        super(entityType, world);

    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, true));

        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 1.0D, false));
        this.goalSelector.add(3, new LookAtEntityGoal(this, LivingEntity.class, 8.0F));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, HostileEntity.class, true));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACK_TIMER, 0);
        this.dataTracker.startTracking(HOME_POS, BlockPos.ORIGIN);
    }

    public void setHomePos(BlockPos pos) {
        this.dataTracker.set(HOME_POS, pos);
    }

    public BlockPos getHomePos() {
        return this.dataTracker.get(HOME_POS);
    }

    @Override
    public void tick() {
        super.tick();

        // Check distance from home and return if too far
        BlockPos homePos = getHomePos();
        if (!homePos.equals(BlockPos.ORIGIN)) {
            double distanceFromHome = Math.sqrt(this.squaredDistanceTo(
                    homePos.getX() + 0.5,
                    homePos.getY(),
                    homePos.getZ() + 0.5
            ));

            if (distanceFromHome > MAX_DISTANCE_FROM_HOME) {
                Vec3d moveTarget = new Vec3d(
                        homePos.getX() + 0.5,
                        homePos.getY(),
                        homePos.getZ() + 0.5
                );
                this.getNavigation().startMovingTo(
                        moveTarget.x,
                        moveTarget.y,
                        moveTarget.z,
                        1.0
                );
            }
        }

        // Handle attack timer and animation
        int attackTimer = this.dataTracker.get(ATTACK_TIMER);
        if (attackTimer > 0) {
            attackTimer--;
            this.dataTracker.set(ATTACK_TIMER, attackTimer);

            // Set X and Z velocity to 0 to prevent movement during attack
            this.setVelocity(0.0, this.getVelocity().y, 0.0);

            if (attackTimer == 0) {
                completeAttack();
            }
        }

        if (attackTimer == 0 && this.getTarget() != null && random.nextFloat() < 0.01) {
            startAttack();
        }
    }

    private void startAttack() {
        this.dataTracker.set(ATTACK_TIMER, 20);
        hasPlayedAttackAnimation = false;
        this.getNavigation().stop();
    }

    private void completeAttack() {
        LivingEntity target = this.getTarget();
        if (target != null && target.distanceTo(this) <= 30.0) {
            Vec3d directionToTarget = target.getPos().subtract(this.getPos()).normalize();

            InkProjectileEntity inkProjectile = new InkProjectileEntity(this.getWorld(), this);
            inkProjectile.refreshPositionAndAngles(this.getX(), this.getEyeY() - 0.1, this.getZ(), this.getYaw(), this.getPitch());
            inkProjectile.setVelocity(directionToTarget.x, directionToTarget.y, directionToTarget.z, 0.5F, 1.0F);

            this.getWorld().spawnEntity(inkProjectile);
        }
        hasPlayedAttackAnimation = false;
    }

    private PlayState animationPredicate(AnimationState<DiligentGuardEntity> state) {
        AnimationController<DiligentGuardEntity> controller = state.getController();
        int attackTimer = this.dataTracker.get(ATTACK_TIMER);

        if (attackTimer > 0) {
            if (!hasPlayedAttackAnimation) {
                controller.setAnimation(SPECIAL_ATTACK_ANIMATION);
                hasPlayedAttackAnimation = true;
            }
            return PlayState.CONTINUE;
        }

        if (state.isMoving()) {
            controller.setAnimation(MOVE_ANIMATION);
        } else {
            controller.setAnimation(IDLE_ANIMATION);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        BlockPos homePos = getHomePos();
        nbt.putInt("HomePosX", homePos.getX());
        nbt.putInt("HomePosY", homePos.getY());
        nbt.putInt("HomePosZ", homePos.getZ());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        BlockPos homePos = new BlockPos(
                nbt.getInt("HomePosX"),
                nbt.getInt("HomePosY"),
                nbt.getInt("HomePosZ")
        );
        setHomePos(homePos);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "guard_controller", 3, this::animationPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return this.age;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true; // Make the entity invulnerable to all damage sources
    }


    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DILLIGENT_GUARD_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DILLIGENT_GUARD_ATTACK;
    }
}