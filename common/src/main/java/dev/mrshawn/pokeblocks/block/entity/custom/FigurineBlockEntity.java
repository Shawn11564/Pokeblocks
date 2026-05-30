package dev.mrshawn.pokeblocks.block.entity.custom;

import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
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
	private boolean gigantic = false;

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
		PokeblocksItemData.apply(stack, PokeblocksItemData.figurineTag(this.figurine, activeFlags));
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putString("figurine", this.figurine);
		tag.putBoolean("gigantic", this.gigantic);
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
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		tag.putString("figurine", this.figurine);
		tag.putBoolean("gigantic", this.gigantic);
		return tag;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}