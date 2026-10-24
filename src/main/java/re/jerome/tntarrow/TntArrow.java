package re.jerome.tntarrow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.Nullable;

// An arrow that explodes like TNT on its first impact (SPEC 2.3). It carries no
// state of its own, so an arrow saved in flight explodes once reloaded.
public class TntArrow extends AbstractArrow {
	public TntArrow(EntityType<? extends TntArrow> type, Level level) {
		super(type, level);
	}

	public TntArrow(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(TntArrowMod.TNT_ARROW_ENTITY, owner, level, pickupItemStack, firedFromWeapon);
	}

	public TntArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(TntArrowMod.TNT_ARROW_ENTITY, x, y, z, level, pickupItemStack, firedFromWeapon);
	}

	// Vanilla arrow damage first, then the blast whatever the hit did: super leaves
	// the arrow alive on a rebound (invulnerable or creative target, shield blocking
	// it all) or a pierce, and discards it on a plain hit. So the guard against a
	// second explosion comes before super, not after.
	@Override
	protected void onHitEntity(EntityHitResult hitResult) {
		if (isRemoved()) {
			return;
		}
		super.onHitEntity(hitResult);
		explode();
	}

	// Only Projectile.onHitBlock's part, so the block reacts as to any arrow (target,
	// bell, chorus flower), which shows when tnt_explodes is false. AbstractArrow's
	// part would plant the arrow in a block it is about to blow up; its hit_block
	// enchantment effects are skipped with it.
	@Override
	protected void onHitBlock(BlockHitResult hitResult) {
		if (isRemoved()) {
			return;
		}
		BlockState state = level().getBlockState(hitResult.getBlockPos());
		state.onProjectileHit(level(), state, hitResult, this);
		explode();
	}

	private void explode() {
		if (!(level() instanceof ServerLevel level)) {
			return;
		}
		// Level.explode ignores this gamerule: PrimedTnt.explode tests it itself.
		if (level.getGameRules().get(GameRules.TNT_EXPLODES)) {
			// PrimedTnt.explode's call, without fire. With the arrow as the source,
			// the explosion is blamed on whoever shot it.
			level.explode(this, Explosion.getDefaultDamageSource(level, this), null,
					getX(), getY(0.0625), getZ(), TntArrowConfig.get().explosion().power(), false, Level.ExplosionInteraction.TNT);
		}
		discard();
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return new ItemStack(TntArrowMod.TNT_ARROW);
	}
}
