package net.bluetree.talisman.entities.custom;

import net.bluetree.talisman.entities.projectile.InkProjectileEntity;
import net.bluetree.talisman.sounds.ModSounds;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
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

import java.util.Collection;
import java.util.List;
import java.util.Random;

import static software.bernie.geckolib.core.animation.Animation.LoopType.LOOP;

public class LostDiligenceEntity extends HostileEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("animation.lost_dilligence.idle", LOOP);
    private static final RawAnimation MOVE_ANIMATION = RawAnimation.begin().then("animation.lost_dilligence.walk", LOOP);
    private static final RawAnimation SPECIAL_ATTACK_ANIMATION = RawAnimation.begin().then("animation.lost_dilligence.attack", LOOP);
    private static final RawAnimation RANGED_ATTACK_ANIMATION = RawAnimation.begin().then("animation.lost_dilligence.attack_two", LOOP);

    private static final TrackedData<Integer> ATTACK_TIMER = DataTracker.registerData(LostDiligenceEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> RANGED_ATTACK_TIMER = DataTracker.registerData(LostDiligenceEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private boolean hasPlayedAttackAnimation = false;
    private boolean hasPlayedRangedAttackAnimation = false;

    private final ServerBossBar bossBar;
    private final Random random = new Random();
    private int nextRangedAttackTime = 10 * 20; // 10 seconds in ticks

    public LostDiligenceEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(false); // Optional: Allows for smooth movement on ground

        this.bossBar = new ServerBossBar(Text.literal("Beast of Lost Diligence"), BossBar.Color.RED, BossBar.Style.PROGRESS);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0) // Increased follow range for smoother tracking
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0); // Set knockback resistance to prevent being pushed back
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 0.4D, true));
        this.goalSelector.add(1, new RevengeGoal(this));

        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.4D, 0.8f));
        this.goalSelector.add(1, new LookAtEntityGoal(this, LivingEntity.class, 8.0F));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACK_TIMER, 0);
        this.dataTracker.startTracking(RANGED_ATTACK_TIMER, 0);
    }

    @Override
    public void tick() {
        super.tick();

        int attackTimer = this.dataTracker.get(ATTACK_TIMER);
        int rangedAttackTimer = this.dataTracker.get(RANGED_ATTACK_TIMER);

        if (attackTimer > 0) {
            attackTimer--;
            this.dataTracker.set(ATTACK_TIMER, attackTimer);


            // Set X and Z velocity to 0 to prevent movement during attack
            this.setVelocity(0.0, this.getVelocity().y, 0.0);

            if (attackTimer == 0) {
                completeAttack();
            }
        }

        if (rangedAttackTimer > 0) {
            rangedAttackTimer--;
            this.dataTracker.set(RANGED_ATTACK_TIMER, rangedAttackTimer);


            // Set X and Z velocity to 0 to prevent movement during ranged attack
            this.setVelocity(0.0, this.getVelocity().y, 0.0);

            if (rangedAttackTimer == 0) {
                launchInkProjectile(); // Launch ink projectiles when ranged attack finishes
                LivingEntity target = this.getTarget();
                if (target != null) {
                    this.getNavigation().startMovingTo(target, 0.4D); // Resume movement
                }
                hasPlayedRangedAttackAnimation = false;

            }
        }

        if (attackTimer == 0 && rangedAttackTimer == 0 && this.getTarget() != null) {
            LivingEntity target = this.getTarget();

            if (target != null && isTargetInFunctionBox(target)) {
                startAttack();
            } else if (target != null && target.distanceTo(this) > 4.0 && target.distanceTo(this) <= 30.0 && this.age % nextRangedAttackTime == 0) {
                startRangedAttack();
            }
        }

        // Update the boss bar to match the entity's current health percentage
        bossBar.setPercent(this.getHealth() / this.getMaxHealth());

        if(this.isAlive()){
            // Update boss bar visibility for nearby players
            if (!this.getWorld().isClient) {
                List<PlayerEntity> nearbyPlayers = this.getWorld().getEntitiesByClass(PlayerEntity.class, this.getBoundingBox().expand(50.0D), player -> true);
                for (PlayerEntity player : nearbyPlayers) {
                    if (!bossBar.getPlayers().contains(player)) {
                        bossBar.addPlayer((ServerPlayerEntity) player);
                    }
                }

                Collection<ServerPlayerEntity> bossBarPlayers = bossBar.getPlayers();
                for (ServerPlayerEntity player : bossBarPlayers) {
                    if (!nearbyPlayers.contains(player)) {
                        bossBar.removePlayer(player);
                    }
                }
            }
        }

    }

    private void startAttack() {
        this.dataTracker.set(ATTACK_TIMER, 26);
        hasPlayedAttackAnimation = false; // Ensure attack animation resets
    }

    private void completeAttack() {
        LivingEntity target = this.getTarget();
        if (target != null && isTargetInFunctionBox(target)) {
            target.damage(getDamageSources().mobAttack(this), 8);
            this.playSound(SoundEvents.BLOCK_SLIME_BLOCK_BREAK, 1.0F, 1.0F);

        }
        hasPlayedAttackAnimation = false;

    }

    private void startRangedAttack() {
        this.dataTracker.set(RANGED_ATTACK_TIMER, 40);
        hasPlayedRangedAttackAnimation = false; // Ensure ranged attack animation resets
        this.getNavigation().stop(); // Stop movement during ranged attack
    }

    private void launchInkProjectile() {
        LivingEntity target = this.getTarget();
        if (!this.getWorld().isClient && target != null) {
            Vec3d directionToTarget = target.getPos().subtract(this.getPos()).normalize();
            double distance = this.squaredDistanceTo(target);

            // Determine the speed based on the distance to the target
            float speed = (float) Math.min(2.0, distance / 10.0); // Speed increases with distance, max of 2.0

            // Launch multiple projectiles in a spread pattern
            int numProjectiles = 5; // Number of projectiles to launch
            for (int i = 0; i < numProjectiles; i++) {
                InkProjectileEntity inkProjectile = new InkProjectileEntity(this.getWorld(), this);
                inkProjectile.refreshPositionAndAngles(this.getX(), this.getEyeY() - 0.1, this.getZ(), this.getYaw(), this.getPitch());

                // Add a slight random spread to each projectile
                float spread = 0.1F * (random.nextFloat() - 0.5F);
                Vec3d spreadVec = directionToTarget.add(spread, spread, spread).normalize();
                inkProjectile.setVelocity(spreadVec.x, spreadVec.y, spreadVec.z, speed, 1.0F); // Set speed and divergence

                this.getWorld().spawnEntity(inkProjectile);
            }
        }
    }

    private boolean isTargetInFunctionBox(LivingEntity target) {
        Vec3d directionVec = this.getRotationVec(1.0F).multiply(4.0);
        Box functionBox = new Box(this.getPos().subtract(0.5, 0, 0.5), this.getPos().add(0.5, 3.0, 0.5)).stretch(directionVec);
        return functionBox.contains(target.getPos());
    }

    private PlayState animationPredicate(AnimationState<LostDiligenceEntity> state) {
        AnimationController<LostDiligenceEntity> controller = state.getController();
        int attackTimer = this.dataTracker.get(ATTACK_TIMER);
        int rangedAttackTimer = this.dataTracker.get(RANGED_ATTACK_TIMER);

        if (attackTimer > 0) {
            if (!hasPlayedAttackAnimation) {
                controller.setAnimation(SPECIAL_ATTACK_ANIMATION);
                hasPlayedAttackAnimation = true;

            }
            return PlayState.CONTINUE;
        }

        if (rangedAttackTimer > 0) {
            if (!hasPlayedRangedAttackAnimation) {
                controller.setAnimation(RANGED_ATTACK_ANIMATION);
                hasPlayedRangedAttackAnimation = true;

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
        controllers.add(new AnimationController<>(this, "lost_diligence_controller", 4, this::animationPredicate));
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
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        bossBar.clearPlayers(); // Clear the boss bar

    }





    @Override
    public void onRemoved() {
        super.onRemoved();
        bossBar.clearPlayers(); // Ensure all players are removed if the entity is removed
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        bossBar.clearPlayers(); // Clear players upon any removal reason
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.LOST_DILIGENCE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.LOST_DILIGENCE_HURT;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.LOST_DILIGENCE_DEATH;
    }

}
