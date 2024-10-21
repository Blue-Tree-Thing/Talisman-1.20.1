package net.bluetree.talisman.entities.custom;

import net.bluetree.talisman.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

import static software.bernie.geckolib.core.animation.Animation.LoopType.LOOP;

public class ShadowHandEntity extends HostileEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Define the animations as RawAnimation instances
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("animation.shadow_hand.idle", LOOP);
    private static final RawAnimation GRAB_ANIMATION = RawAnimation.begin().then("animation.shadow_hand.grab", LOOP);

    private int attackCooldown = 20;  // Cooldown between attacks (1 second in ticks)

    public ShadowHandEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)  // Health of the hand
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0) // Damage per tick
                .add(EntityAttributes.GENERIC_ARMOR, 5.0);  // Some armor to make it tough
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            detectEntitiesOnTop();  // Detect entities on top every tick
        }

        // Cooldown management
        if (attackCooldown > 0) {
            attackCooldown--;
        }
    }

    // Method to detect if any entities are directly above the hand
    private void detectEntitiesOnTop() {
        BlockPos blockPosAbove = this.getBlockPos().up();
        Box detectionBox = new Box(blockPosAbove).expand(0.5);  // Check within 0.5 block radius around the position
        List<HostileEntity> entitiesAbove = this.getWorld().getEntitiesByClass(HostileEntity.class, detectionBox, entity -> !entity.isSpectator() && entity != this);  // Exclude itself

        if (!entitiesAbove.isEmpty() && attackCooldown <= 0) {
            // Perform attack on the first detected entity
            HostileEntity target = entitiesAbove.get(0);
            performAttack(target);
        }
    }

    // Method to perform attack, deal damage, and apply slowness
    private void performAttack(HostileEntity target) {
        // Deal damage
        target.damage(this.getDamageSources().mobAttack(this), (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));

        // Apply Slowness effect for 3 seconds (60 ticks)
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));

        // Reset the cooldown for the next attack
        attackCooldown = 20;
    }

    // Override onDeath method to spawn Dark Essence on entity kill
    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (!this.getWorld().isClient && source.getAttacker() instanceof HostileEntity) {
            // Create an ItemStack of DARK_ESSENCE
            ItemStack darkEssence = new ItemStack(ModItems.DARK_ESSENCE);

            // Drop the item at the current position of the entity
            this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), darkEssence));

            // Emit death event
            this.emitGameEvent(GameEvent.ENTITY_DIE);
        }
    }

    private PlayState animationPredicate(AnimationState<ShadowHandEntity> state) {
        AnimationController<ShadowHandEntity> controller = state.getController();

        // If entities are detected above, play the attack (grab) animation
        BlockPos blockPosAbove = this.getBlockPos().up();
        Box detectionBox = new Box(blockPosAbove).expand(0.5);
        List<LivingEntity> entitiesAbove = this.getWorld().getEntitiesByClass(LivingEntity.class, detectionBox, entity -> !entity.isSpectator() && entity != this);  // Exclude itself

        if (!entitiesAbove.isEmpty()) {
            controller.setAnimation(GRAB_ANIMATION);
            return PlayState.CONTINUE;
        }

        // Otherwise, play the idle animation
        controller.setAnimation(IDLE_ANIMATION);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "shadow_hand_controller", 0, this::animationPredicate));
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
    public void travel(Vec3d movementInput) {
        // Do nothing to prevent any movement
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushAway(Entity entity) {
        // Prevent pushing entities
    }
}
