package net.bluetree.talisman.entities.custom;

import net.bluetree.talisman.entities.projectile.InkProjectileEntity; // Import your InkProjectileEntity class
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import static software.bernie.geckolib.core.animation.Animation.LoopType.LOOP;

public class GumgarEntity extends HostileEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("animation.gumgar.idle", LOOP);
    private static final RawAnimation MOVE_ANIMATION = RawAnimation.begin().then("animation.gumgar.run", LOOP);
    private static final RawAnimation SPECIAL_ATTACK_ANIMATION = RawAnimation.begin().then("animation.gumgar.attack", LOOP);

    private int specialAttackWarmup = 0; // Timer to indicate special attack warmup state
    private static final Random random = new Random(); // Random number generator for deciding attacks
    private boolean hasPlayedSpecialAttackAnimation = false; // Flag to track if the special attack animation has been played

    public GumgarEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(false); // Optional: Allows for smooth movement on ground
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0) // Increased follow range for smoother tracking
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void initGoals() {
        // Add melee attack goal (highest priority for close combat)
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, true));
        // Add custom follow target goal for smooth tracking (lower priority than melee)
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.2D, 0.8f));
        this.goalSelector.add(3, new WanderNearTargetGoal(this, 1.2D, 5f));
        // Add look at entity goal (lowest priority)
        this.goalSelector.add(4, new LookAtEntityGoal(this, LivingEntity.class, 8.0F));
        // Add target player goal
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        // Only initiate the special attack if the entity is not in warmup and has a target
        if (specialAttackWarmup == 0 && random.nextFloat() < 0.01) {
            specialAttackWarmup = 20; // Set special attack warmup to 1 second (20 ticks)
            hasPlayedSpecialAttackAnimation = false; // Reset animation flag
            System.out.println("Special attack initiated. Warmup started.");
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
            }

            // Debug: print remaining warmup time
            System.out.println("Special attack warmup remaining: " + specialAttackWarmup);
        }
    }

    // Method to launch an Ink Projectile in the direction the GumgarEntity is facing
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

    private PlayState animationPredicate(AnimationState<GumgarEntity> state) {
        AnimationController<GumgarEntity> controller = state.getController();

        // Create a detection box around the entity to check for nearby players in survival mode
        Box detectionBox = new Box(this.getBlockPos()).expand(10.0);
        List<PlayerEntity> playersInRange = this.getWorld().getEntitiesByClass(PlayerEntity.class, detectionBox, player ->
                !player.isSpectator() && player.isAlive() && !player.isCreative());

        // If there are players within range and the special attack warmup is active, play the special attack animation
        if (this.getSpecialAttackWarmup() > 0 && !playersInRange.isEmpty()) {
            if (!hasPlayedSpecialAttackAnimation) {
                controller.setAnimation(SPECIAL_ATTACK_ANIMATION);
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
        controllers.add(new AnimationController<>(this, "gumgar_controller", 1, this::animationPredicate));
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
