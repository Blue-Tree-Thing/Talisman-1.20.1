package net.bluetree.talisman.entities.projectile;

import net.bluetree.talisman.entities.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes; // Import for the ink-like particles
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
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

import static software.bernie.geckolib.core.animation.Animation.LoopType.LOOP;

public class InkProjectileEntity extends PersistentProjectileEntity implements GeoAnimatable {

    private static final RawAnimation SPIN_ANIMATION = RawAnimation.begin().then("animation.ink.spin", LOOP);

    // AnimatableInstanceCache is needed for GeoAnimatable
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Constructor that takes an EntityType
    public InkProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(false); // Enable gravity for the arc effect
    }

    // Constructor that uses a specific entity type (your custom Ink Projectile entity)
    public InkProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.INK_PROJECTILE_ENTITY, owner, world); // Use your registered entity type
        this.setNoGravity(false); // Enable gravity for arcing effect
    }

    @Override
    public void tick() {
        super.tick();

        // Add ink particles during each tick to give the appearance of ink dripping or trailing
        if (this.getWorld().isClient) {
            for (int i = 0; i < 2; i++) { // Spawn a couple of particles for visual effect
                this.getWorld().addParticle(
                        ParticleTypes.SQUID_INK, // Use squid ink particles for a more ink-like appearance
                        this.getX() + (this.random.nextDouble() - 0.5) * this.getWidth(), // X position with some randomness
                        this.getY() + (this.random.nextDouble() - 0.5) * this.getHeight(), // Y position with some randomness
                        this.getZ() + (this.random.nextDouble() - 0.5) * this.getWidth(), // Z position with some randomness
                        0.0D, 0.0D, 0.0D // Velocity of the particles (no movement)
                );
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        // Only handle collisions with blocks
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;

            // Check if the collision is on the top side of the block
            if (blockHitResult.getSide() == Direction.UP && !this.getWorld().isClient) {
                // Spawn the ink cloud effect
                spawnInkCloud();

                // Play a simple splash or hit sound when it collides with a block
                this.getWorld().playSound(
                        null,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        SoundEvents.ENTITY_SLIME_ATTACK, // Change the sound to something appropriate for ink impact
                        SoundCategory.NEUTRAL,
                        1.0F,
                        1.0F
                );

                // Debug: Log when the projectile is being discarded
                System.out.println("InkProjectileEntity collided with a block and is now being discarded.");

                // Remove the projectile entity
                this.discard();
            }
        }
    }

    // Method to spawn an ink cloud that deals damage and inflicts slowness
    private void spawnInkCloud() {
        // Define the radius of the ink cloud effect
        double radius = 3.0;

        // Get all living entities within the radius
        List<LivingEntity> entitiesInRange = this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(radius), entity -> entity.isAlive() && entity != this.getOwner());

        for (LivingEntity entity : entitiesInRange) {
            // Apply damage to the entity using the mob attack method similar to the shadow hand
            entity.damage(this.getDamageSources().magic(), 2); // Deal 4 damage (2 hearts)

            // Apply slowness effect to the entity
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1)); // Slowness for 5 seconds at level 1
        }

        // Spawn ink particles to visually represent the cloud
        for (int i = 0; i < 20; i++) {
            this.getWorld().addParticle(
                    ParticleTypes.SQUID_INK, // Use squid ink particles for a more ink-like appearance
                    this.getX() + (this.random.nextDouble() - 0.5) * radius * 2, // Randomized X position within the cloud radius
                    this.getY() + 0.1, // Slightly above the ground
                    this.getZ() + (this.random.nextDouble() - 0.5) * radius * 2, // Randomized Z position within the cloud radius
                    0.0D, 0.05D, 0.0D // Small upward velocity for visual effect
            );
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Override without implementation to disable collision with entities
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY; // No item representation for this projectile
    }

    // Method to set an initial velocity for the projectile to make it short-ranged and aim slightly upwards
    public void setInitialVelocity(LivingEntity shooter, float pitch, float yaw, float roll, float speed, float divergence) {
        // Reduce speed to make it short-ranged
        speed *= 0.6F; // Reduce the force by half to create a short-ranged effect

        // Calculate the initial velocity components
        float xVec = -((float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch)));
        float yVec = -((float) Math.sin(Math.toRadians(pitch))) + 0.5F; // Add upward bias to give an arcing effect
        float zVec = ((float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch)));

        this.setVelocity(xVec, yVec, zVec, speed, divergence);
    }

    private PlayState animationPredicate(AnimationState<InkProjectileEntity> state) {
        AnimationController<InkProjectileEntity> controller = state.getController();
        controller.setAnimation(SPIN_ANIMATION);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "ink_projectile_controller", 1, this::animationPredicate));
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
