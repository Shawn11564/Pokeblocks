package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationResolver;
import dev.mrshawn.pokeblocks.client.renderer.item.PokedollItemRenderer;
import dev.mrshawn.pokeblocks.compendium.CompendiumKind;
import dev.mrshawn.pokeblocks.compendium.CompendiumProgressTracker;
import dev.mrshawn.pokeblocks.compendium.CompendiumVariantKey;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.entity.custom.ThrownPokedollEntity;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.item.ThrowableDolls;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Consumer;

public class PokedollItem extends BlockItem implements GeoItem, Equipable, ProjectileItem {

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public PokedollItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private PokedollItemRenderer renderer = null;

			@Override
			public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new PokedollItemRenderer();
				return this.renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "pokedoll_item_controller", 0, state -> {
			ItemStack stack = state.getData(DataTickets.ITEMSTACK);
			if (stack == null || stack.isEmpty()) return PlayState.STOP;

			String pokemon = getPokemonFromStack(stack);
			PokemonData data = PokemonRegistry.getPokemonData(pokemon);

			if (data == null) return PlayState.STOP;
			Set<ModelFlag> flags = getFlagsFromStack(stack);

			AnimationResolver.AnimationType type = AnimationResolver.resolve(pokemon, flags, data.animationProfile());

			switch (type) {
				case VARIANT, BASE -> {
					return state.setAndContinue(RawAnimation.begin().thenLoop("animation.idle"));
				}
				default -> {
					return PlayState.STOP;
				}
			}
		}));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	@SuppressWarnings({"override.param.invalid", "override.return.invalid"})
	public Component getName(ItemStack stack) {
		String pokemon = getPokemonFromStack(stack);
		Set<ModelFlag> flags = getFlagsFromStack(stack);
		String name = buildDisplayName(pokemon, flags);
		if (ThrowableDolls.isThrowable(stack)) name = "Throwable " + name;
		return Component.literal(name).withStyle(ChatFormatting.WHITE);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flag) {
		String pokemon = getPokemonFromStack(stack);
		Set<ModelFlag> activeFlags = getFlagsFromStack(stack);

		if (ThrowableDolls.isThrowable(stack)) {
			tooltip.add(Component.literal("Right-click to throw").withStyle(ChatFormatting.AQUA));
		}

		DollRarity rarity = DollRarity.getRarity(stack);
		if (rarity != null && rarity != DollRarity.NONE) {
			tooltip.add(Component.empty());
			tooltip.add(Component.literal(rarity.getDisplayName()).withStyle(rarity.getFormatting()));
		}

		// Rarity score with rainbow gradient
		double chance = RarityScoreCalculator.computeChance(pokemon, activeFlags);
		String scoreDisplay = RarityScoreCalculator.getDisplayString(pokemon, activeFlags);
		tooltip.add(buildRarityComponent(scoreDisplay, chance));
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	/**
	 * A throwable doll (see {@link ThrowableDolls}) never places by clicking a block — returning
	 * PASS lets the interaction pipeline fall through to {@link #use}, which throws it instead.
	 */
	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (ThrowableDolls.isThrowable(context.getItemInHand())) return InteractionResult.PASS;
		return super.useOn(context);
	}

	/**
	 * Right-click: a throwable doll is thrown exactly like a snowball ({@code SnowballItem#use}),
	 * carrying the full doll stack on the projectile; a plain doll keeps the vanilla
	 * {@link Equipable} behaviour (equips onto the head).
	 */
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!ThrowableDolls.isThrowable(stack)) {
			return super.use(level, player, hand);
		}

		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW,
				SoundSource.NEUTRAL, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));
		if (!level.isClientSide) {
			ThrownPokedollEntity thrown = new ThrownPokedollEntity(level, player);
			thrown.setItem(stack);
			thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.5f, 1.0f);
			level.addFreshEntity(thrown);
		}
		player.awardStat(Stats.ITEM_USED.get(this));
		stack.consume(1, player);
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	/**
	 * Dispenser support, mirroring {@code SnowballItem}: the default {@link ProjectileItem} dispense
	 * config is exactly the snowball's launch. Only ever invoked for throwable dolls —
	 * {@link dev.mrshawn.pokeblocks.interaction.PokeblocksDispenseBehaviors} gates on the marker and
	 * ejects plain dolls like any other item.
	 */
	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		ThrownPokedollEntity thrown = new ThrownPokedollEntity(level, pos.x(), pos.y(), pos.z());
		thrown.setItem(stack);
		return thrown;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		// Carrying a doll marks this exact variant discovered in the player's compendium progress
		// (a one-shot server-side latch, so the throttled cadence is invisible). The key carries
		// the active flags, so the compendium can show per-variant completion; species-level
		// "collected" derives from any variant key of that species.
		if (!level.isClientSide && entity instanceof ServerPlayer player
				&& entity.tickCount % CompendiumProgressTracker.RECORD_INTERVAL_TICKS == 0) {
			CompendiumProgressTracker.record(player, CompendiumKind.DOLL, compendiumKey(stack));
		}
	}

	/** The canonical compendium progress key of a doll stack: species + sorted flag names. */
	public static String compendiumKey(ItemStack stack) {
		List<String> flagNames = new ArrayList<>();
		for (ModelFlag flag : getFlagsFromStack(stack)) {
			flagNames.add(flag.getTagName());
		}
		return CompendiumVariantKey.of(getPokemonFromStack(stack), flagNames);
	}

	/**
	 * Builds the rarity tooltip line with a per-character gradient from white to rainbow.
	 * <p>
	 * The saturation of the rainbow is driven by rarity on a log scale:
	 *   - Common dolls (~50% chance) → saturation ≈ 0 → appears white
	 *   - Rare dolls (~0.001% chance) → saturation ≈ 1 → full vivid rainbow
	 * <p>
	 * Each character in the percentage value cycles through rainbow hues,
	 * with a time-based offset so the colors shimmer on rare dolls.
	 */
	private static MutableComponent buildRarityComponent(String scoreDisplay, double chance) {
		MutableComponent result = Component.literal("Rarity: ").withStyle(ChatFormatting.GRAY);

		// Map chance to saturation using log scale.
		// High chance (common) = low saturation (white).
		// Low chance (rare) = high saturation (rainbow).
		double clamped = Math.max(chance, 0.0001);
		double logMin = Math.log10(0.0001); // -4
		double logMax = Math.log10(100.0);  //  2
		double logVal = Math.log10(clamped);

		// 0 = rarest, 1 = most common
		double normalized = (logVal - logMin) / (logMax - logMin);
		normalized = Math.max(0.0, Math.min(1.0, normalized));

		// Invert so rare = high saturation
		float saturation = (float) (1.0 - normalized);

		// Time-based hue offset for shimmer animation
		long tick = System.currentTimeMillis() / 50;

		for (int i = 0; i < scoreDisplay.length(); i++) {
			float hue = ((tick + i * 12) % 360) / 360.0f;
			int rgb = hsbToRgb(hue, saturation, 1.0f);
			result.append(Component.literal(String.valueOf(scoreDisplay.charAt(i)))
					.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb))));
		}

		return result;
	}

	/**
	 * Converts HSB (hue 0-1, saturation 0-1, brightness 0-1) to packed RGB int.
	 */
	private static int hsbToRgb(float hue, float saturation, float brightness) {
		int r, g, b;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0f;
			float f = h - (float) Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));

			switch ((int) h) {
				case 0 -> { r = (int) (brightness * 255.0f + 0.5f); g = (int) (t * 255.0f + 0.5f); b = (int) (p * 255.0f + 0.5f); }
				case 1 -> { r = (int) (q * 255.0f + 0.5f); g = (int) (brightness * 255.0f + 0.5f); b = (int) (p * 255.0f + 0.5f); }
				case 2 -> { r = (int) (p * 255.0f + 0.5f); g = (int) (brightness * 255.0f + 0.5f); b = (int) (t * 255.0f + 0.5f); }
				case 3 -> { r = (int) (p * 255.0f + 0.5f); g = (int) (q * 255.0f + 0.5f); b = (int) (brightness * 255.0f + 0.5f); }
				case 4 -> { r = (int) (t * 255.0f + 0.5f); g = (int) (p * 255.0f + 0.5f); b = (int) (brightness * 255.0f + 0.5f); }
				default -> { r = (int) (brightness * 255.0f + 0.5f); g = (int) (p * 255.0f + 0.5f); b = (int) (q * 255.0f + 0.5f); }
			}
		}
		return (r << 16) | (g << 8) | b;
	}

	/**
	 * The doll's display name as a component, read from the same stack data the model uses (so the
	 * name can never disagree with the rendered doll). Pass {@code includeSuffix = false} to drop the
	 * trailing "Pokedoll" word — e.g. the phone call screen shows just "Shiny Bulbasaur".
	 */
	public static Component displayName(ItemStack stack, boolean includeSuffix) {
		return Component.literal(buildDisplayName(getPokemonFromStack(stack), getFlagsFromStack(stack), includeSuffix));
	}

	private static String buildDisplayName(String pokemon, Set<ModelFlag> flags) {
		return buildDisplayName(pokemon, flags, true);
	}

	/**
	 * Builds display name in format: [Shiny] [Gigantic] [Pokemon] [other flags] [Pokedoll].
	 *
	 * @param includeSuffix whether to append the trailing "Pokedoll" word
	 */
	private static String buildDisplayName(String pokemon, Set<ModelFlag> flags, boolean includeSuffix) {
		StringBuilder sb = new StringBuilder();

		List<ModelFlag> sorted = new ArrayList<>(flags);
		sorted.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

		// Leading flags: SHINY and GIGANTIC come before the pokemon name
		for (ModelFlag f : sorted) {
			if (isPrefixFlag(f)) {
				sb.append(capitalize(f.getTagName())).append(" ");
			}
		}

		// Pokemon name
		pokemon = pokemon.replace("_", " ");
		sb.append(capitalize(pokemon)).append(" ");

		// Trailing flags: everything else in sort order
		for (ModelFlag f : sorted) {
			if (!isPrefixFlag(f)) {
				sb.append(capitalize(f.getTagName())).append(" ");
			}
		}

		if (includeSuffix) {
			sb.append("Pokedoll");
		}
		// Collapse any doubled/trailing spaces (e.g. when the suffix is omitted).
		return sb.toString().trim().replaceAll("\\s+", " ");
	}

	private static boolean isPrefixFlag(ModelFlag flag) {
		return flag == ModelFlag.SHINY || flag == ModelFlag.GIGANTIC || flag == ModelFlag.ANIMATED || flag == ModelFlag.POSED;
	}

	private static String capitalize(String s) {
		String[] words = s.split(" ");
		for (int i = 0; i < words.length; i++) {
			if (!words[i].isEmpty()) {
				words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
			}
		}
		return String.join(" ", words);
	}

	/**
	 * Gets the pokemon name from the item's NBT data
	 */
	public static String getPokemonFromStack(ItemStack stack) {
		return PokeblocksItemData.readString(stack, PokeblocksItemData.KEY_POKEMON, ModSettings.DEFAULT_POKEMON);
	}

	/**
	 * Reads all ModelFlag values from the item's BLOCK_ENTITY_DATA CompoundTag.
	 * returns a set containing all ModelFlags that were present and true
	 */
	public static Set<ModelFlag> getFlagsFromStack(ItemStack stack) {
		return PokeblocksItemData.readActiveFlags(stack);
	}

	public static Map<ModelFlag, Boolean> getFlagsMapFromStack(ItemStack stack) {
		return PokeblocksItemData.readFlagsMap(stack);
	}

	/**
	 * Create a pokedoll with PokemonData and optional flags written to NBT.
	 */
	public static ItemStack createPokedoll(PokemonData pokemonData) {
		return createPokedoll(ModSettings.DEFAULT_POKEMON, pokemonData);
	}

	public static ItemStack createPokedoll(String name, PokemonData pokemonData) {
		return createPokedoll(name, pokemonData.modelFlags());
	}

	public static ItemStack createPokedoll(String name, ModelFlag... activeFlags) {
		Map<ModelFlag, Boolean> flags = new EnumMap<>(ModelFlag.class);
		for (ModelFlag flag : activeFlags) {
			flags.put(flag, true);
		}
		return createPokedoll(name, flags);
	}

	public static ItemStack createPokedoll(String name, Map<ModelFlag, Boolean> flags) {
		ItemStack stack = new ItemStack(ItemRegistry.POKEDOLL_ITEM.get());
		// Only the true flags reach the tag; PokeblocksItemData writes the canonical minimal form.
		List<ModelFlag> activeFlags = flags.entrySet().stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.toList();
		PokeblocksItemData.apply(stack, PokeblocksItemData.pokedollTag(name, activeFlags));
		return stack;
	}

	public static List<ItemStack> getAllMutations(String name, PokemonData pokemonData) {
		List<ItemStack> mutations = new ArrayList<>();

		if (pokemonData == null) {
			// If no data, return base doll with no flags
			mutations.add(createPokedoll(name, new EnumMap<>(ModelFlag.class)));
			return mutations;
		}

		// Generate all possible combinations (power set) of available flags
		List<Set<ModelFlag>> allCombinations = pokemonData.generatePowerSet();

		// Filter out invalid combinations based on required combinations
		for (Set<ModelFlag> combination : allCombinations) {
			if (pokemonData.isValidCombination(combination)) {
				Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
				// Initialize all flags to false
				for (ModelFlag flag : ModelFlag.values()) {
					flagMap.put(flag, false);
				}
				// Set active flags to true
				for (ModelFlag flag : combination) {
					flagMap.put(flag, true);
				}
				mutations.add(createPokedoll(name, flagMap));
			}
		}

		return mutations;
	}

}