package dev.mrshawn.pokeblocks.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.RarityWeightConfig;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.DecorativeRegistry;
import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.*;

/**
 * Global Loot Modifier that adds Pokemon items, figurines, and decorative items to loot tables.
 * Uses the correct approach: roll for rarity first, choose item uniformly from that pool, then roll for shiny.
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
    
    /**
     * Represents a loot item that can be generated
     */
    private static class LootItem {
        final String name;
        final ItemType type;
        final DollRarity rarity;
        
        LootItem(String name, ItemType type, DollRarity rarity) {
            this.name = name;
            this.type = type;
            this.rarity = rarity;
        }
    }
    
    private enum ItemType {
        POKEMON,
        DECORATIVE,
        FIGURINE
    }
    
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        RandomSource random = context.getRandom();
        
        // Debug: Log all weights being used
        System.out.println("[Pokeblocks] === LOOT GENERATION DEBUG ===");
        for (DollRarity rarity : DollRarity.values()) {
            int weight = RarityWeightConfig.getWeight(rarity);
            System.out.println("[Pokeblocks] " + rarity.name() + " weight: " + weight);
        }
        
        // Step 1: Build weighted rarity pool (exclude SHINY - handled separately)
        List<DollRarity> validRarities = new ArrayList<>();
        List<Integer> cumulativeWeights = new ArrayList<>();
        int totalWeight = 0;
        
        for (DollRarity rarity : DollRarity.values()) {
            // Skip SHINY - it's handled separately as percentage
            if (rarity == DollRarity.SHINY) {
                continue;
            }
            
            int weight = RarityWeightConfig.getWeight(rarity);
            if (weight > 0) {
                validRarities.add(rarity);
                totalWeight += weight;
                cumulativeWeights.add(totalWeight);
                System.out.println("[Pokeblocks] Added " + rarity.name() + " with weight " + weight + " (cumulative: " + totalWeight + ")");
            }
        }
        
        if (totalWeight <= 0) {
            System.out.println("[Pokeblocks] No valid rarities with weight > 0, skipping loot generation");
            return generatedLoot;
        }
        
        // Step 2: Roll for rarity
        int rarityRoll = random.nextInt(totalWeight);
        DollRarity selectedRarity = null;
        
        for (int i = 0; i < cumulativeWeights.size(); i++) {
            if (rarityRoll < cumulativeWeights.get(i)) {
                selectedRarity = validRarities.get(i);
                break;
            }
        }
        
        if (selectedRarity == null) {
            System.out.println("[Pokeblocks] Failed to select rarity, skipping");
            return generatedLoot;
        }
        
        System.out.println("[Pokeblocks] Selected rarity: " + selectedRarity.name() + " (roll: " + rarityRoll + "/" + totalWeight + ")");
        
        // Step 3: Collect all items (Pokemon + Decoratives) that match the selected rarity
        List<LootItem> availableItems = new ArrayList<>();
        
        // Add Pokemon
        for (Map.Entry<String, PokemonData> entry : PokemonRegistry.ALL_POKEMON.entrySet()) {
            String pokemonName = entry.getKey();
            
            // Check if this Pokemon is assigned to the selected rarity using DollRarityOverrides
            Set<ModelFlag> baseFlags = EnumSet.noneOf(ModelFlag.class);
            DollRarity pokemonRarity = DollRarityOverrides.getOverride(pokemonName, baseFlags);
            
            // If no explicit override found, treat as NONE
            if (pokemonRarity == null) {
                pokemonRarity = DollRarity.NONE;
            }
            
            if (pokemonRarity == selectedRarity) {
                availableItems.add(new LootItem(pokemonName, ItemType.POKEMON, pokemonRarity));
            }
        }
        
        // Add Decorative items (decorative blocks like applin_basket, magikarp_fishbowl)
        for (DecorativeRegistry.DecorativeEntry entry : DecorativeRegistry.ALL_ENTRIES) {
            String decorativeName = entry.definition().id();
            
            // Check if this decorative item is assigned to the selected rarity
            Set<ModelFlag> baseFlags = EnumSet.noneOf(ModelFlag.class);
            DollRarity decorativeRarity = DollRarityOverrides.getOverride(decorativeName, baseFlags);
            
            // If no explicit override found, treat as NONE
            if (decorativeRarity == null) {
                decorativeRarity = DollRarity.NONE;
            }
            
            if (decorativeRarity == selectedRarity) {
                availableItems.add(new LootItem(decorativeName, ItemType.DECORATIVE, decorativeRarity));
            }
        }
        
        // Add Figurines (figurines like doncheadle_figurine, airuhsea_figurine)
        for (Map.Entry<String, Boolean> figurineEntry : FigurineRegistry.ALL_FIGURINES.entrySet()) {
            String figurineName = figurineEntry.getKey() + "_figurine";
            
            // Check if this figurine is assigned to the selected rarity
            Set<ModelFlag> baseFlags = EnumSet.noneOf(ModelFlag.class);
            DollRarity figurineRarity = DollRarityOverrides.getOverride(figurineName, baseFlags);
            
            // If no explicit override found, treat as NONE
            if (figurineRarity == null) {
                figurineRarity = DollRarity.NONE;
            }
            
            if (figurineRarity == selectedRarity) {
                availableItems.add(new LootItem(figurineName, ItemType.FIGURINE, figurineRarity));
            }
        }
        
        if (availableItems.isEmpty()) {
            System.out.println("[Pokeblocks] No items found for rarity " + selectedRarity.name() + ", skipping");
            return generatedLoot;
        }
        
        // Step 4: Select item uniformly from the rarity-specific pool
        LootItem selectedItem = availableItems.get(random.nextInt(availableItems.size()));
        System.out.println("[Pokeblocks] Selected " + selectedItem.type + ": " + selectedItem.name + " from " + availableItems.size() + " " + selectedRarity.name() + " items");
        
        // Step 5: Create the ItemStack based on item type
        ItemStack resultItem;
        
        if (selectedItem.type == ItemType.POKEMON) {
            // Handle Pokemon items
            Map<ModelFlag, Boolean> flags = new EnumMap<>(ModelFlag.class);
            for (ModelFlag flag : ModelFlag.values()) {
                flags.put(flag, false);
            }
            
            // Step 6: Apply rarity-specific flags only if Pokemon supports them
            PokemonData pokemonData = PokemonRegistry.getPokemonData(selectedItem.name);
            if (pokemonData != null) {
                // Apply GIGANTIC flag if this rarity is GIGANTIC and Pokemon supports it
                if (selectedRarity == DollRarity.GIGANTIC) {
                    if (pokemonData.modelFlags().getOrDefault(ModelFlag.GIGANTIC, false)) {
                        flags.put(ModelFlag.GIGANTIC, true);
                        System.out.println("[Pokeblocks] Applied GIGANTIC flag");
                    }
                }
            }
            
            // Step 7: Roll for shiny separately (independent of rarity)
            int shinyWeight = RarityWeightConfig.getWeight(DollRarity.SHINY);
            if (shinyWeight > 0) {
                // Treat shiny weight as percentage (0-100)
                if (random.nextInt(100) < shinyWeight) {
                    flags.put(ModelFlag.SHINY, true);
                    System.out.println("[Pokeblocks] Applied SHINY flag (weight: " + shinyWeight + "%)");
                }
            }

        // Step 8: Create and add the item
            resultItem = PokedollItem.createPokedoll(selectedItem.name, flags);
            
        } else if (selectedItem.type == ItemType.FIGURINE) {
            // Handle Figurine items
            // Remove the "_figurine" suffix to get the base name for FigurineItem.createFigurine()
            String baseName = selectedItem.name;
            if (baseName.endsWith("_figurine")) {
                baseName = baseName.substring(0, baseName.length() - "_figurine".length());
            }
            
            resultItem = FigurineItem.createFigurine(baseName);
            
        } else {
            // Handle Decorative items (figurines, decorative blocks)
            DecorativeRegistry.DecorativeEntry decorativeEntry = DecorativeRegistry.ALL_ENTRIES.stream()
                    .filter(entry -> entry.definition().id().equals(selectedItem.name))
                    .findFirst()
                    .orElse(null);
            
            if (decorativeEntry != null) {
                Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
                
                // Roll for shiny separately for decorative items too
                int shinyWeight = RarityWeightConfig.getWeight(DollRarity.SHINY);
                if (shinyWeight > 0) {
                    if (random.nextInt(100) < shinyWeight) {
                        // Only apply shiny if the decorative item supports it
                        if (decorativeEntry.definition().supportedFlags().contains(ModelFlag.SHINY)) {
                            flags.add(ModelFlag.SHINY);
                            System.out.println("[Pokeblocks] Applied SHINY flag to decorative item");
                        }
                    }
                }
                
                resultItem = DecorativeItem.createStack(decorativeEntry.item().get(), selectedItem.name, flags);
            } else {
                System.out.println("[Pokeblocks] Failed to find decorative entry for " + selectedItem.name);
                return generatedLoot;
            }
        }
        
        generatedLoot.add(resultItem);
        
        System.out.println("[Pokeblocks] Final item: " + selectedItem.type + " " + selectedItem.name);
        System.out.println("[Pokeblocks] === END DEBUG ===");
        
        return generatedLoot;
    }
}