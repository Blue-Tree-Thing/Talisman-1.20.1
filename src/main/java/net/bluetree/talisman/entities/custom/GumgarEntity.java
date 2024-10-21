package net.bluetree.talisman.entities.custom;

import net.bluetree.talisman.entities.projectile.InkProjectileEntity;
import net.bluetree.talisman.sounds.ModSounds;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

import static software.bernie.geckolib.core.animation.Animation.LoopType.LOOP;

public class GumgarEntity extends HostileEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("animation.gumgar.idle", LOOP);
    private static final RawAnimation MOVE_ANIMATION = RawAnimation.begin().then("animation.gumgar.run", LOOP);
    private static final RawAnimation SPECIAL_ATTACK_ANIMATION = RawAnimation.begin().then("animation.gumgar.attack", LOOP);

    private static final TrackedData<Integer> ATTACK_TIMER = DataTracker.registerData(GumgarEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private boolean hasPlayedAttackAnimation = false;

    private final Random random = new Random();

    public GumgarEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(false); // Optional: Allows for smooth movement on ground
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
        this.goalSelector.add(1, new RevengeGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.2D, 0.8f));
        this.goalSelector.add(3, new LookAtEntityGoal(this, LivingEntity.class, 8.0F));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACK_TIMER, 0);
    }

    @Override
    public void tick() {
        super.tick();

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
        this.dataTracker.set(ATTACK_TIMER, 20); // Set the attack timer
        hasPlayedAttackAnimation = false; // Reset the attack animation flag
        this.getNavigation().stop(); // Stop movement during the attack
    }

    private void completeAttack() {
        LivingEntity target = this.getTarget();
        if (target != null && target.distanceTo(this) <= 30.0) { // Set a range for projectile attack
            // Launch an ink projectile towards the target
            Vec3d directionToTarget = target.getPos().subtract(this.getPos()).normalize();

            InkProjectileEntity inkProjectile = new InkProjectileEntity(this.getWorld(), this);
            inkProjectile.refreshPositionAndAngles(this.getX(), this.getEyeY() - 0.1, this.getZ(), this.getYaw(), this.getPitch());

            // Set the projectile velocity to target the player
            inkProjectile.setVelocity(directionToTarget.x, directionToTarget.y, directionToTarget.z, 0.5F, 1.0F); // Set speed and divergence

            this.getWorld().spawnEntity(inkProjectile);

        }
        hasPlayedAttackAnimation = false;

    }

    private PlayState animationPredicate(AnimationState<GumgarEntity> state) {
        AnimationController<GumgarEntity> controller = state.getController();
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "gumgar_controller", 3, this::animationPredicate));
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
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GUMGAR_ATTACK;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.LOST_DILIGENCE_DEATH;
    }
}
