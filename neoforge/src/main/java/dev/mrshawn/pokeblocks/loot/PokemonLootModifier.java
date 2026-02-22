package dev.mrshawn.pokeblocks.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.RarityWeightConfig;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.*;

/**
 * Global Loot Modifier that adds Pokemon items to loot tables.
 * Uses the correct approach: roll for rarity first, choose Pokemon uniformly from that pool, then roll for shiny.
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
        
        // Step 3: Get Pokemon that are assigned to the selected rarity
        List<String> rarityPokemon = new ArrayList<>();
        for (Map.Entry<String, PokemonData> entry : PokemonRegistry.ALL_POKEMON.entrySet()) {
            String pokemonName = entry.getKey();
            
            // Check if this Pokemon is assigned to the selected rarity using DollRarityOverrides
            // Use base flags (no special variants) to get the base rarity assignment
            Set<ModelFlag> baseFlags = EnumSet.noneOf(ModelFlag.class);
            DollRarity pokemonRarity = DollRarityOverrides.getOverride(pokemonName, baseFlags);
            
            // If no explicit override found, treat as NONE
            if (pokemonRarity == null) {
                pokemonRarity = DollRarity.NONE;
            }
            
            if (pokemonRarity == selectedRarity) {
                rarityPokemon.add(pokemonName);
            }
        }
        
        if (rarityPokemon.isEmpty()) {
            System.out.println("[Pokeblocks] No Pokemon found for rarity " + selectedRarity.name() + ", skipping");
            return generatedLoot;
        }
        
        // Step 4: Select Pokemon uniformly from the rarity-specific pool
        String selectedPokemon = rarityPokemon.get(random.nextInt(rarityPokemon.size()));
        System.out.println("[Pokeblocks] Selected Pokemon: " + selectedPokemon + " from " + rarityPokemon.size() + " " + selectedRarity.name() + " Pokemon");
        
        // Step 5: Initialize flags - start with base model
        Map<ModelFlag, Boolean> flags = new EnumMap<>(ModelFlag.class);
        for (ModelFlag flag : ModelFlag.values()) {
            flags.put(flag, false);
        }
        
        // Step 6: Apply rarity-specific flags only if Pokemon supports them
        PokemonData pokemonData = PokemonRegistry.getPokemonData(selectedPokemon);
        if (pokemonData != null) {
            // Apply GIGANTIC flag if this rarity is GIGANTIC and Pokemon supports it
            if (selectedRarity == DollRarity.GIGANTIC) {
                if (pokemonData.modelFlags().getOrDefault(ModelFlag.GIGANTIC, false)) {
                    flags.put(ModelFlag.GIGANTIC, true);
                    System.out.println("[Pokeblocks] Applied GIGANTIC flag");
                }
            }
            // For other rarities, just use base model (no special flags)
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
        ItemStack selectedItem = PokedollItem.createPokedoll(selectedPokemon, flags);
        generatedLoot.add(selectedItem);
        
        System.out.println("[Pokeblocks] Final item: " + selectedPokemon + " with flags " + flags);
        System.out.println("[Pokeblocks] === END DEBUG ===");
        
        return generatedLoot;
    }
}