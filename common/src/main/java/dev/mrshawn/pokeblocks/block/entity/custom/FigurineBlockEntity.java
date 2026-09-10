package dev.mrshawn.pokeblocks.block.entity.custom;

import dev.mrshawn.pokeblocks.block.FigurinePose;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.Set;

public class FigurineBlockEntity extends BlockEntity implements GeoBlockEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private String figurine = ModSettings.DEFAULT_FIGURINE;
	private Set<FigurineFlag> figurineFlags = EnumSet.noneOf(FigurineFlag.class);
	private boolean gigantic = false;

	// --- Boxless figurine doll state (see FigurinePose). A boxless doll renders without its display
	// case, uses the fine 16-segment rotation below instead of the block's 4-way FACING (set from the
	// placer's yaw in FigurineBlock#setPlacedBy, like a pokedoll), and can be posed by sneak-right-click.
	private boolean boxless = false;
	private FigurinePose pose = FigurinePose.STANDING;
	/** Sign-style rotation segment 0-15; only meaningful while {@link #boxless}. */
	private int rotation16 = 0;

	/**
	 * Honeycomb seals the display box shut (see {@code FigurineBlock#useItemOn}): a waxed box can't be
	 * sheared open to free its figurine. Deliberately block-only — it is persisted in world NBT and
	 * synced to clients, but never written to the dropped/picked item ({@link #saveToItem}), so breaking
	 * the box wipes the wax (a re-placed figurine starts un-waxed).
	 */
	private boolean waxed = false;
	/** BE-only NBT key for {@link #waxed}; never appears on an item stack, unlike {@link PokeblocksItemData#KEY_BOXLESS}. */
	private static final String TAG_WAXED = "waxed";

	public FigurineBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.FIGURINE_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, state -> PlayState.STOP));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	public void setFigurine(String figurine) {
		this.figurine = figurine == null || figurine.isEmpty() ? ModSettings.DEFAULT_FIGURINE : figurine;
		setChanged();
		if (this.level != null && !this.level.isClientSide()) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	public String getFigurine() {
		return this.figurine;
	}

	public void setFigurineFlags(Set<FigurineFlag> flags) {
		this.figurineFlags = flags == null || flags.isEmpty() ? EnumSet.noneOf(FigurineFlag.class) : EnumSet.copyOf(flags);
		setChanged();
		if (this.level != null && !this.level.isClientSide()) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	public Set<FigurineFlag> getFigurineFlags() {
		return this.figurineFlags;
	}

	public void setGigantic(boolean gigantic) {
		this.gigantic = gigantic;
		setChanged();
		if (this.level != null && !this.level.isClientSide()) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	public boolean isGigantic() {
		return this.gigantic;
	}

	public boolean isBoxless() {
		return this.boxless;
	}

	public void setBoxless(boolean boxless) {
		this.boxless = boxless;
		sync();
	}

	/** Whether honeycomb has sealed this box shut so shears can't free the figurine. */
	public boolean isWaxed() {
		return this.waxed;
	}

	public void setWaxed(boolean waxed) {
		this.waxed = waxed;
		sync();
	}

	public FigurinePose getPose() {
		return this.pose;
	}

	public void setPose(FigurinePose pose) {
		this.pose = pose == null ? FigurinePose.STANDING : pose;
		sync();
	}

	/** The sign-style 0-15 rotation segment a boxless doll renders at (mirrors PokedollBlock's ROTATION). */
	public int getRotation16() {
		return this.rotation16;
	}

	public void setRotation16(int segment) {
		this.rotation16 = Math.floorMod(segment, 16);
		sync();
	}

	private void sync() {
		setChanged();
		if (this.level != null && !this.level.isClientSide()) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	/**
	 * Writes the canonical minimal item tag on pick-block: just the figurine id (plus the gigantic
	 * flag only when set), matching {@link dev.mrshawn.pokeblocks.item.custom.FigurineItem#createFigurine}
	 * so a picked figurine stacks with a given one. Routed through {@link PokeblocksItemData} like every
	 * other creation path.
	 */
	@Override
	public void saveToItem(ItemStack stack, HolderLookup.Provider registries) {
		Set<ModelFlag> activeFlags = EnumSet.noneOf(ModelFlag.class);
		if (this.gigantic) activeFlags.add(ModelFlag.GIGANTIC);
		// Boxless-ness survives the break→place round trip (a freed doll never regains its case);
		// the pose deliberately doesn't — a re-placed doll stands, like the canonical-minimal rule.
		// The wax (see #waxed) deliberately isn't written either: breaking the box wipes the honeycomb.
		PokeblocksItemData.apply(stack, PokeblocksItemData.figurineTag(this.figurine, activeFlags, this.figurineFlags, this.boxless));
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putString("figurine", this.figurine);
		tag.putBoolean("gigantic", this.gigantic);
		writeFigurineFlags(tag);
		writeBoxlessState(tag);
		writeWaxState(tag);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("figurine")) {
			this.figurine = tag.getString("figurine");
			if (this.figurine.isEmpty()) {
				this.figurine = ModSettings.DEFAULT_FIGURINE;
			}
		}
		if (tag.contains("gigantic")) {
			this.gigantic = tag.getBoolean("gigantic");
		}
		readFigurineFlags(tag);
		this.boxless = tag.getBoolean(PokeblocksItemData.KEY_BOXLESS);
		this.pose = FigurinePose.byId(tag.getInt("pose"));
		this.rotation16 = Math.floorMod(tag.getInt("rotation16"), 16);
		this.waxed = tag.getBoolean(TAG_WAXED);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		tag.putString("figurine", this.figurine);
		tag.putBoolean("gigantic", this.gigantic);
		writeFigurineFlags(tag);
		writeBoxlessState(tag);
		writeWaxState(tag);
		return tag;
	}

	/** Wax state, synced to clients so shears/honeycomb interactions predict correctly (canonical-minimal). */
	private void writeWaxState(CompoundTag tag) {
		if (this.waxed) {
			tag.putBoolean(TAG_WAXED, true);
		}
	}

	/** Boxless doll state, written only when it deviates from the boxed default (canonical-minimal). */
	private void writeBoxlessState(CompoundTag tag) {
		if (this.boxless) {
			tag.putBoolean(PokeblocksItemData.KEY_BOXLESS, true);
		}
		if (this.pose != FigurinePose.STANDING) {
			tag.putInt("pose", this.pose.ordinal());
		}
		if (this.rotation16 != 0) {
			tag.putInt("rotation16", this.rotation16);
		}
	}

	/**
	 * Figurine flags are stored as individual boolean keys (only the active ones), matching the item's
	 * {@code BLOCK_ENTITY_DATA} format so a placed figurine's variant survives item → block-entity copy.
	 */
	private void writeFigurineFlags(CompoundTag tag) {
		for (FigurineFlag flag : this.figurineFlags) {
			tag.putBoolean(flag.getTagName(), true);
		}
	}

	private void readFigurineFlags(CompoundTag tag) {
		Set<FigurineFlag> flags = EnumSet.noneOf(FigurineFlag.class);
		for (FigurineFlag flag : FigurineFlag.values()) {
			if (tag.contains(flag.getTagName()) && tag.getBoolean(flag.getTagName())) {
				flags.add(flag);
			}
		}
		this.figurineFlags = flags;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}