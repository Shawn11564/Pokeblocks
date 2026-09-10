package dev.mrshawn.pokeblocks.integration.jei;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.recipe.GiganticDollRecipe;
import dev.mrshawn.pokeblocks.recipe.LaserPointerDyeRecipe;
import dev.mrshawn.pokeblocks.recipe.PokeblocksShapedRecipe;
import dev.mrshawn.pokeblocks.recipe.PokedollPhoneRecipe;
import dev.mrshawn.pokeblocks.recipe.ThrowableDollRecipe;
import dev.mrshawn.pokeblocks.recipe.TrappedDollRecipe;
import dev.mrshawn.pokeblocks.registry.DecorativeRegistry;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.IExtendableCraftingRecipeCategory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

/**
 * JEI (Just Enough Items) integration. Optional: this class only ever loads when JEI is installed —
 * Forge/NeoForge discover it by scanning for {@link JeiPlugin}, Fabric through the
 * {@code jei_mod_plugin} entrypoint in {@code fabric.mod.json}.
 *
 * <p>It does two things:
 * <ol>
 *   <li><b>Every doll variant becomes its own JEI entry.</b> All dolls share one registered item
 *       (species + variant flags live in components), so without a subtype interpreter JEI would
 *       collapse the whole collection into a single "Pokedoll". {@link PokeblocksSubtypes} keys each
 *       stack by its canonical variant. The entries themselves come from the creative tabs, which JEI
 *       reads on its own — {@code ItemGroupRegistry} is the single definition of "every valid variant".</li>
 *   <li><b>Every Pokeblocks crafting recipe is viewable.</b> The mod's recipe classes don't expose
 *       vanilla {@code Ingredient}s (doll ingredients can't be expressed that way), so JEI's built-in
 *       crafting support skips them. Each class gets an {@code ICraftingCategoryExtension} that lays the
 *       recipe out in the vanilla crafting category; the dynamic "any doll → transformed doll" recipes
 *       show every valid input doll with its exact output, cycling in lock-step.</li>
 * </ol>
 */
@JeiPlugin
public final class PokeblocksJeiPlugin implements IModPlugin {

	public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "jei");

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(ItemRegistry.POKEDOLL_ITEM.get(), PokeblocksSubtypes.POKEDOLL);
		registration.registerSubtypeInterpreter(ItemRegistry.FIGURINE_ITEM.get(), PokeblocksSubtypes.FIGURINE);
		registration.registerSubtypeInterpreter(ItemRegistry.CUSTOM_DECORATION_ITEM.get(), PokeblocksSubtypes.CUSTOM_DECORATION);
		// One registered item per decoration; the flag variants (shiny, gigantic, …) are components.
		for (DecorativeRegistry.DecorativeEntry entry : DecorativeRegistry.ALL_ENTRIES) {
			registration.registerSubtypeInterpreter(entry.item().get(), PokeblocksSubtypes.DECORATIVE);
		}
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		IExtendableCraftingRecipeCategory crafting = registration.getCraftingCategory();
		// One catalog per JEI (re)start so a resource reload that adds dolls is picked up, while the
		// enumeration is shared by every dynamic recipe instead of being rebuilt per recipe.
		DollCatalog catalog = new DollCatalog();
		crafting.addExtension(PokeblocksShapedRecipe.class, new PokeblocksShapedRecipeExtension());
		crafting.addExtension(GiganticDollRecipe.class, new GiganticDollRecipeExtension(catalog));
		crafting.addExtension(ThrowableDollRecipe.class, DollTransformRecipeExtension.throwable(catalog));
		crafting.addExtension(TrappedDollRecipe.class, DollTransformRecipeExtension.trapped(catalog));
		crafting.addExtension(PokedollPhoneRecipe.class, new PokedollPhoneRecipeExtension(catalog));
		crafting.addExtension(LaserPointerDyeRecipe.class, new LaserPointerDyeRecipeExtension());
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		// JEI's vanilla plugin validates crafting recipes by their vanilla getIngredients() list, which
		// is empty for all of ours, so it never adds them. Feed them in ourselves; the extensions
		// registered above take over rendering. (Vanilla-typed recipes such as the compendium books are
		// handled by JEI directly and must not be re-added here.)
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			PokeblocksCommon.LOGGER.warn("[JEI] No client level while registering recipes; Pokeblocks recipes will be missing");
			return;
		}
		boolean showIncomplete = PokeblocksConfig.isShowIncompleteItems();
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		for (RecipeHolder<CraftingRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
			CraftingRecipe recipe = holder.value();
			if (!isPokeblocksRecipe(recipe)) continue;
			// The laser pointer is an incomplete feature hidden from the creative tabs unless opted in;
			// keep its recipe hidden under the same switch.
			if (recipe instanceof LaserPointerDyeRecipe && !showIncomplete) continue;
			recipes.add(holder);
		}
		registration.addRecipes(RecipeTypes.CRAFTING, recipes);
		PokeblocksCommon.LOGGER.info("[JEI] Registered {} Pokeblocks crafting recipes", recipes.size());
	}

	/** True for the recipe classes this plugin renders (see {@link #registerVanillaCategoryExtensions}). */
	static boolean isPokeblocksRecipe(CraftingRecipe recipe) {
		return recipe instanceof PokeblocksShapedRecipe
				|| recipe instanceof GiganticDollRecipe
				|| recipe instanceof ThrowableDollRecipe
				|| recipe instanceof TrappedDollRecipe
				|| recipe instanceof PokedollPhoneRecipe
				|| recipe instanceof LaserPointerDyeRecipe;
	}
}
