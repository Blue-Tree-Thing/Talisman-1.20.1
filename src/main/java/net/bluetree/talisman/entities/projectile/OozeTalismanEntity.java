package net.bluetree.talisman.entities.projectile;

import net.bluetree.talisman.entities.ModEntities;
import net.bluetree.talisman.entities.custom.OozeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class OozeTalismanEntity extends PersistentProjectileEntity {

    public OozeTalismanEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public OozeTalismanEntity(World world, LivingEntity owner) {
        super(ModEntities.OOZE_TALISMAN_ENTITY, owner, world);
    }

    public OozeTalismanEntity(World world, double x, double y, double z) {
        super(ModEntities.OOZE_TALISMAN_ENTITY, x, y, z, world);
    }

    @Override
    public void tick() {
        super.tick();

        // Make the talisman trail ink particles while moving
        if (!this.getWorld().isClient && this.isAlive()) {
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.SQUID_INK,    // Ink particle type
                        this.getX(),                // X position
                        this.getY(),                // Y position
                        this.getZ(),                // Z position
                        1,                          // Number of particles
                        0.1, 0.1, 0.1,              // Offset for particle position (small random offset)
                        0.1                         // Velocity
                );
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {
            // Play the slime jumping sound on collision
            this.getWorld().playSound(
                    null,                                   // No specific entity
                    this.getX(), this.getY(), this.getZ(),  // Location of the sound
                    SoundEvents.ENTITY_SLIME_JUMP,          // Slime jump sound event
                    SoundCategory.NEUTRAL,                  // Sound category
                    1.0F,                                  // Volume
                    1.0F                                   // Pitch
            );

            // Check if the collision is with a block
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                // Summon the OozeEntity at the collision point
                OozeEntity ooze = new OozeEntity(ModEntities.OOZE, this.getWorld());
                ooze.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
                this.getWorld().spawnEntity(ooze);

                // Optional: Spawn particles
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(),
                            10, 0.2, 0.2, 0.2, 0.0
                    );
                }

                // Remove the projectile entity
                this.discard();
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.getWorld().isClient) {
            Entity entityHit = entityHitResult.getEntity();

            // Play the slime jumping sound on collision
            this.getWorld().playSound(
                    null,                                   // No specific entity
                    this.getX(), this.getY(), this.getZ(),  // Location of the sound
                    SoundEvents.ENTITY_SLIME_JUMP,          // Slime jump sound event
                    SoundCategory.NEUTRAL,                  // Sound category
                    1.0F,                                  // Volume
                    1.0F                                   // Pitch
            );

            // Spawn the OozeEntity at the collision point
            OozeEntity ooze = new OozeEntity(ModEntities.OOZE, this.getWorld());
            ooze.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
            this.getWorld().spawnEntity(ooze);

            // Set the OozeEntity's target to the entity that was hit
            if (entityHit instanceof LivingEntity livingEntity) {
                ooze.setTarget(livingEntity);
            }

            // Optional: Spawn particles
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(),
                        10, 0.2, 0.2, 0.2, 0.0
                );
            }

            // Remove the projectile entity
            this.discard();
        }
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    public void setItem(ItemStack itemStack) {
        // No-op since the projectile doesn't need to hold an item
    }
}
