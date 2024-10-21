package net.bluetree.talisman.entities.custom;

import net.bluetree.talisman.entities.projectile.InkProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
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

import java.util.List;
import java.util.Random;

import static software.bernie.geckolib.core.animation.Animation.LoopType.LOOP;

public class LostDiligenceEntity extends HostileEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("animation.lost_dilligence.idle", LOOP);
    private static final RawAnimation MOVE_ANIMATION = RawAnimation.begin().then("animation.lost_dilligence.walk", LOOP);
    private static final RawAnimation SPECIAL_ATTACK_ANIMATION = RawAnimation.begin().then("animation.lost_dilligence.attack", LOOP);
    private static final RawAnimation SPECIAL_ATTACK_ANIMATION_TWO = RawAnimation.begin().then("animation.lost_dilligence.attack_two", LOOP);

    private int specialAttackWarmup = 0; // Timer to indicate special attack warmup state
    private int meleeAttackTimer = 0; // Timer for melee attack delay
    private static final Random random = new Random(); // Random number generator for deciding attacks
    private boolean hasPlayedSpecialAttackAnimation = false; // Flag to track if the special attack animation has been played
    private boolean useSecondAttackAnimation = false; // Flag to alternate between attack animations
    private boolean isAttacking = false; // Flag to indicate if the entity is currently attacking

    public LostDiligenceEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(false); // Optional: Allows for smooth movement on ground
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0) // Increased follow range for smoother tracking
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void initGoals() {
        // Add melee attack goal (highest priority for close combat)
        this.goalSelector.add(1, new MeleeAttackGoal(this, 0.4D, true));
        // Add wander goals
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.4D, 0.8f));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.4D, 5f));
        // Add look at entity goal (lowest priority)
        this.goalSelector.add(1, new LookAtEntityGoal(this, LivingEntity.class, 8.0F));
        // Add target player goal
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        // Handle melee attack timer to apply delayed damage
        if (meleeAttackTimer > 0) {
            meleeAttackTimer--;
            if (meleeAttackTimer == 0 && this.getTarget() != null) {
                LivingEntity target = this.getTarget();
                if (this.squaredDistanceTo(target) < 4.0) { // Ensure target is still in range
                    target.damage(getDamageSources().mobAttack(this), (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
                    System.out.println("Melee attack damage dealt.");
                }
                isAttacking = false; // Mark attack as finished
            }
        }

        // Only initiate the special attack if the entity is not in warmup, not currently attacking, and has a target
        if (specialAttackWarmup == 0 && !isAttacking && random.nextFloat() < 0.01) {
            specialAttackWarmup = 30; // Set special attack warmup to 1.5 seconds (30 ticks)
            hasPlayedSpecialAttackAnimation = false; // Reset animation flag
            useSecondAttackAnimation = random.nextBoolean(); // Randomly choose which attack animation to use
            System.out.println("Special attack initiated. Warmup started.");
            isAttacking = true; // Lock entity in attacking state
        }

        // Decrease the special attack warmup timer every tick if greater than zero
        if (specialAttackWarmup > 0) {
            specialAttackWarmup--;

            // Freeze the entity's movement on X and Z while warming up
            this.getNavigation().stop();
            this.setAiDisabled(true);
            this.forwardSpeed = 0;

            // Launch ink projectile when warmup ends
            if (specialAttackWarmup == 0) {
                launchInkProjectile();
                System.out.println("Ink projectile launched.");
                this.setAiDisabled(false); // Re-enable AI after launching
                isAttacking = false; // Mark attack as finished
            }

            // Debug: print remaining warmup time
            System.out.println("Special attack warmup remaining: " + specialAttackWarmup);
        }
    }

    // Method to launch an Ink Projectile in the direction the LostDiligenceEntity is facing
    private void launchInkProjectile() {
        if (!this.getWorld().isClient) {
            InkProjectileEntity inkProjectile = new InkProjectileEntity(this.getWorld(), this);
            inkProjectile.refreshPositionAndAngles(this.getX(), this.getEyeY() - 0.1, this.getZ(), this.getYaw(), this.getPitch());
            inkProjectile.setInitialVelocity(this, this.getPitch(), this.getYaw(), 0.0F, 1.5F, 1.0F); // Set speed and divergence
            this.getWorld().spawnEntity(inkProjectile);
        }
    }

    // Getter for specialAttackWarmup to ensure it can be properly accessed
    public int getSpecialAttackWarmup() {
        return specialAttackWarmup;
    }

    private PlayState animationPredicate(AnimationState<LostDiligenceEntity> state) {
        AnimationController<LostDiligenceEntity> controller = state.getController();

        // Create a detection box around the entity to check for nearby players in front
        Vec3d directionVec = this.getRotationVec(1.0F).multiply(2.0);
        Box detectionBox = new Box(this.getPos(), this.getPos().add(directionVec));
        List<PlayerEntity> playersInFront = this.getWorld().getEntitiesByClass(PlayerEntity.class, detectionBox, player ->
                !player.isSpectator() && player.isAlive() && !player.isCreative());

        // If there are players in front and the melee attack timer is not active, initiate an attack animation
        if (!playersInFront.isEmpty() && meleeAttackTimer == 0 && !isAttacking) {
            controller.setAnimation(SPECIAL_ATTACK_ANIMATION);
            meleeAttackTimer = 20; // Set melee attack timer to delay damage by 20 ticks (1 second)
            System.out.println("Attack animation triggered due to player in detection box.");
            isAttacking = true; // Lock entity in attacking state
            return PlayState.CONTINUE;
        }

        // If there are players within range and the special attack warmup is active, play the special attack animation
        if (this.getSpecialAttackWarmup() > 0) {
            if (!hasPlayedSpecialAttackAnimation) {
                if (useSecondAttackAnimation) {
                    controller.setAnimation(SPECIAL_ATTACK_ANIMATION_TWO);
                } else {
                    controller.setAnimation(SPECIAL_ATTACK_ANIMATION);
                }
                hasPlayedSpecialAttackAnimation = true; // Set the flag to prevent repeating the animation trigger
                System.out.println("Special attack animation playing.");
            }
            this.getNavigation().stop(); // Stop moving during special attack animation
            return PlayState.CONTINUE;
        }

        // Otherwise, play the appropriate movement animation
        if (state.isMoving()) {
            controller.setAnimation(MOVE_ANIMATION);
        } else {
            controller.setAnimation(IDLE_ANIMATION);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "lost_diligence_controller", 1, this::animationPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return this.age;
    }
}
