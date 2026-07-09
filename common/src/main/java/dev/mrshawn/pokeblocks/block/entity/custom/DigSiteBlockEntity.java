package dev.mrshawn.pokeblocks.block.entity.custom;

import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Holds the item a dig-site mound is about to yield so the client can render it gradually emerging
 * as the player scoops — the vanilla-archaeology "brush reveal" (see {@code BrushableBlockEntity} /
 * {@code BrushableBlockRenderer}). The server sets it from the owning quest on each dig
 * ({@link dev.mrshawn.pokeblocks.phone.DigQuestManager#tryDig}). It is a cosmetic preview — the
 * authoritative doll/junk decision still happens at the final scoop — but it always matches the item
 * that actually pops out. Synced via the standard block-entity update packet (no custom networking).
 */
public class DigSiteBlockEntity extends BlockEntity {

	private static final String KEY_REVEAL_ITEM = "RevealItem";

	/** Per-tick easing rate for {@link #easeRenderY} — ~95% of the way to the target in about a second. */
	private static final double RENDER_EASE_RATE = 0.15;

	private ItemStack revealItem = ItemStack.EMPTY;

	// Server only, not persisted: game time of the owner's last scoop, read by DigSiteBlock#tick to
	// decide when the mound starts refilling (a reload just lets any pending refill proceed).
	private long lastDigTime;

	// Client only: the reveal item's eased render height and the render clock it was last updated at,
	// so the item glides between the per-stage seats instead of teleporting (see DigSiteBlockRenderer).
	private float renderY = Float.NaN;
	private double renderClock;

	public DigSiteBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.DIG_SITE_BLOCK_ENTITY.get(), pos, state);
	}

	/** Records a scoop so the refill countdown restarts (see {@code DigSiteBlock#tick}). */
	public void markDug(long gameTime) {
		this.lastDigTime = gameTime;
	}

	public long getLastDigTime() {
		return lastDigTime;
	}

	/**
	 * Eases the rendered reveal item toward {@code targetY}, starting from {@code initialY} on the
	 * first frame; {@code clock} is game time + partial tick. Render thread only.
	 */
	public float easeRenderY(float targetY, float initialY, double clock) {
		if (Float.isNaN(renderY)) {
			renderY = initialY;
			renderClock = clock;
		}
		double elapsed = Mth.clamp(clock - renderClock, 0.0, 4.0);
		renderClock = clock;
		renderY += (float) ((targetY - renderY) * (1.0 - Math.exp(-elapsed * RENDER_EASE_RATE)));
		return renderY;
	}

	/** The item currently peeking out of the mound, or {@link ItemStack#EMPTY} before the first scoop. */
	public ItemStack getRevealItem() {
		return revealItem;
	}

	public void setRevealItem(ItemStack stack) {
		ItemStack copy = stack == null ? ItemStack.EMPTY : stack.copy();
		if (ItemStack.matches(this.revealItem, copy)) return; // unchanged — skip the resync
		this.revealItem = copy;
		syncToClient();
	}

	private void syncToClient() {
		setChanged();
		if (this.level != null && !this.level.isClientSide()) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!revealItem.isEmpty()) {
			tag.put(KEY_REVEAL_ITEM, revealItem.save(registries));
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.revealItem = tag.contains(KEY_REVEAL_ITEM, Tag.TAG_COMPOUND)
				? ItemStack.parseOptional(registries, tag.getCompound(KEY_REVEAL_ITEM))
				: ItemStack.EMPTY;
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		if (!revealItem.isEmpty()) {
			tag.put(KEY_REVEAL_ITEM, revealItem.save(registries));
		}
		return tag;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
