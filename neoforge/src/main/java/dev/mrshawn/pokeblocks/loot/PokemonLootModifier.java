package dev.mrshawn.pokeblocks.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.*;
import java.util.Comparator;

/**
 * Global Loot Modifier that adds Pokemon items to loot tables.
 * This replaces the event-based loot injection system.
 */
public class PokemonLootModifier extends LootModifier {
    
    public static final MapCodec<PokemonLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).apply(inst, PokemonLootModifier::new)
    );
    
    public PokemonLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }
    
    @Override
    public MapCodec<? extends LootModifier> codec() {
        return CODEC;
    }
    
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Get all Pokemon items that should appear in loot tables
        List<ItemStack> lootItems = getAllLootTableItems();
        
        if (lootItems.isEmpty()) {
            return generatedLoot;
        }
        
        // Calculate total weight for probability calculations
        int totalWeight = getCombinedWeight(lootItems);
        
        // Use the same probability logic as the original Fabric mod
        // Original formula: airWeight = (int)(totalWeight / 0.3) - totalWeight
        // This means Pokemon items have a 30% chance to appear
        int airWeight = (int)(totalWeight / 0.3) - totalWeight;
        RandomSource random = context.getRandom();
        
        // Select from the weighted pool (including air for no item)
        int totalPoolWeight = totalWeight + airWeight;
        int randomWeight = random.nextInt(totalPoolWeight);
        
        // If we hit the "air" weight, don't add anything
        if (randomWeight >= totalWeight) {
            return generatedLoot;
        }
        
        // Otherwise, select a Pokemon item based on weights
        ItemStack selectedItem = selectWeightedRandomItem(lootItems, randomWeight);
        if (selectedItem != null) {
            generatedLoot.add(selectedItem.copy());
        }
        
        return generatedLoot;
    }
    
    /**
     * Gets all Pokemon items that should appear in loot tables
     * This replicates the original Fabric mod's approach where each Pokemon has:
     * - Base variant with Pokemon's base rarity (COMMON, RARE, EPIC, LEGENDARY)
     * - Shiny variant with SHINY rarity (weight 5)
     * - Other variants (posed, animated) with their specific rarities
     * - Excludes gigantic variants and figurines
     */
    private static List<ItemStack> getAllLootTableItems() {
        List<ItemStack> lootItems = new ArrayList<>();
        
        // Replicate original Fabric approach: create specific variants with known rarities
        for (Map.Entry<String, PokemonData> entry : PokemonRegistry.ALL_POKEMON.entrySet()) {
            String pokemonName = entry.getKey();
            PokemonData pokemonData = entry.getValue();
            
            // Skip figurines (they had DollRarity.NONE in original)
            if (pokemonName.contains("figurine") || pokemonName.contains("trophy")) {
                continue;
            }
            
            // Add base variant (no flags) with Pokemon's base rarity
            DollRarity baseRarity = getBasePokemonRarity(pokemonName, pokemonData);
            if (baseRarity != DollRarity.NONE && baseRarity != DollRarity.UNCLASSIFIED && baseRarity.getWeight() > 0) {
                ItemStack baseVariant = PokedollItem.createPokedoll(pokemonName, new EnumMap<>(ModelFlag.class));
                lootItems.add(baseVariant);
            }
            
            // Add shiny variant (SHINY flag only) with SHINY rarity (weight 5)
            Map<ModelFlag, Boolean> shinyFlags = new EnumMap<>(ModelFlag.class);
            shinyFlags.put(ModelFlag.SHINY, true);
            ItemStack shinyVariant = PokedollItem.createPokedoll(pokemonName, shinyFlags);
            lootItems.add(shinyVariant);
            
            // Add other specific variants that existed in original Fabric mod
            // Only add variants that have valid combinations in PokemonData
            if (pokemonData.modelFlags().getOrDefault(ModelFlag.POSED, false)) {
                // Add posed variant (non-shiny) with higher rarity than base
                Map<ModelFlag, Boolean> posedFlags = new EnumMap<>(ModelFlag.class);
                posedFlags.put(ModelFlag.POSED, true);
                ItemStack posedVariant = PokedollItem.createPokedoll(pokemonName, posedFlags);
                lootItems.add(posedVariant);
                
                // Add shiny posed variant with SHINY rarity
                Map<ModelFlag, Boolean> shinyPosedFlags = new EnumMap<>(ModelFlag.class);
                shinyPosedFlags.put(ModelFlag.SHINY, true);
                shinyPosedFlags.put(ModelFlag.POSED, true);
                ItemStack shinyPosedVariant = PokedollItem.createPokedoll(pokemonName, shinyPosedFlags);
                lootItems.add(shinyPosedVariant);
            }
            
            if (pokemonData.modelFlags().getOrDefault(ModelFlag.ANIMATED, false)) {
                // Add animated variant (non-shiny) with LEGENDARY rarity like original
                Map<ModelFlag, Boolean> animatedFlags = new EnumMap<>(ModelFlag.class);
                animatedFlags.put(ModelFlag.ANIMATED, true);
                ItemStack animatedVariant = PokedollItem.createPokedoll(pokemonName, animatedFlags);
                lootItems.add(animatedVariant);
                
                // Add shiny animated variant with SHINY rarity
                Map<ModelFlag, Boolean> shinyAnimatedFlags = new EnumMap<>(ModelFlag.class);
                shinyAnimatedFlags.put(ModelFlag.SHINY, true);
                shinyAnimatedFlags.put(ModelFlag.ANIMATED, true);
                ItemStack shinyAnimatedVariant = PokedollItem.createPokedoll(pokemonName, shinyAnimatedFlags);
                lootItems.add(shinyAnimatedVariant);
            }
        }
        
        // Debug: Log the total number of items and their weight distribution
        System.out.println("[Pokeblocks] Loot table items: " + lootItems.size());
        Map<Integer, Integer> weightCounts = new HashMap<>();
        for (ItemStack item : lootItems) {
            int weight = getRarityWeight(item);
            weightCounts.put(weight, weightCounts.getOrDefault(weight, 0) + 1);
        }
        System.out.println("[Pokeblocks] Weight distribution: " + weightCounts);
        
        return lootItems;
    }
    
    /**
     * Gets the base rarity for a Pokemon (without considering shiny status)
     * This replicates the original Fabric mod's individual item rarities
     */
    private static DollRarity getBasePokemonRarity(String pokemonName, PokemonData pokemonData) {
        // Map Pokemon to their original Fabric rarities based on the ModItems.java patterns
        switch (pokemonName.toLowerCase()) {
            // COMMON Pokemon (weight 500)
            case "bulbasaur":
            case "squirtle": 
            case "charmander":
            case "smoliv":
            case "rellor":
            case "swinub":
            case "happiny":
                return DollRarity.COMMON;
                
            // UNCOMMON Pokemon (weight 300)
            case "lickitung":
            case "mareep":
            case "dolliv":
            case "arboliva":
            case "wooper":
                return DollRarity.UNCOMMON;
                
            // RARE Pokemon (weight 150)
            case "flaaffy":
            case "snorlax":
            case "sentret":
            case "furret":
            case "munchlax":
            case "rabsca":
            case "wartortle":
            case "quagsire":
                return DollRarity.RARE;
                
            // EPIC Pokemon (weight 70)
            case "calyrex":
            case "ampharos":
            case "sableye":
            case "absol":
            case "ivysaur":
            case "applin_basket":
            case "magikarp_fishbowl":
                return DollRarity.EPIC;
                
            // LEGENDARY Pokemon (weight 30)
            case "venusaur":
            case "blastoise":
            case "washing_machine":
                return DollRarity.LEGENDARY;
                
            default:
                // For any Pokemon not explicitly mapped, use COMMON as default
                return DollRarity.COMMON;
        }
    }
    
    /**
     * Gets the combined weight of all loot items
     */
    private static int getCombinedWeight(List<ItemStack> lootItems) {
        return lootItems.stream()
                .mapToInt(PokemonLootModifier::getRarityWeight)
                .sum();
    }
    
    /**
     * Gets the rarity weight for an item stack based on its flags
     * This replicates the original Fabric mod's approach:
     * - Shiny variants always use SHINY rarity (weight 5)
     * - Non-shiny variants use their specific rarity based on flags and Pokemon
     */
    private static int getRarityWeight(ItemStack stack) {
        Set<ModelFlag> flags = PokedollItem.getFlagsFromStack(stack);
        String pokemon = PokedollItem.getPokemonFromStack(stack);
        
        // If shiny, always use SHINY rarity (weight 5) like original Fabric mod
        if (flags.contains(ModelFlag.SHINY)) {
            return DollRarity.SHINY.getWeight();
        }
        
        // For non-shiny variants, determine rarity based on flags
        if (flags.contains(ModelFlag.ANIMATED)) {
            // Animated variants had LEGENDARY rarity in original (like CALYREX_ANIMATED)
            return DollRarity.LEGENDARY.getWeight();
        }
        
        if (flags.contains(ModelFlag.POSED)) {
            // Posed variants had RARE rarity in original (like BULBASAUR_POSED)
            return DollRarity.RARE.getWeight();
        }
        
        // Base variants (no special flags) use Pokemon's base rarity
        PokemonData pokemonData = PokemonRegistry.getPokemonData(pokemon);
        return getBasePokemonRarity(pokemon, pokemonData).getWeight();
    }
    
    /**
     * Selects a random item from the list based on weights using the exact weight value
     */
    private static ItemStack selectWeightedRandomItem(List<ItemStack> items, int targetWeight) {
        if (items.isEmpty()) {
            return null;
        }
        
        int currentWeight = 0;
        
        // Find the item at the target weight point
        for (ItemStack item : items) {
            int itemWeight = getRarityWeight(item);
            currentWeight += itemWeight;
            if (targetWeight < currentWeight) {
                // Debug: Log what was selected
                Set<ModelFlag> flags = PokedollItem.getFlagsFromStack(item);
                String pokemon = PokedollItem.getPokemonFromStack(item);
                System.out.println("[Pokeblocks] Selected: " + pokemon + " with flags " + flags + " (weight: " + itemWeight + ", target: " + targetWeight + ")");
                return item;
            }
        }
        
        // Fallback (shouldn't happen)
        return items.get(items.size() - 1);
    }
}