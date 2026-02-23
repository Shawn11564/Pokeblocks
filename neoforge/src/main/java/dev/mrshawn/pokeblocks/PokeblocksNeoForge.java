package dev.mrshawn.pokeblocks;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.command.ModCommands;
import dev.mrshawn.pokeblocks.loot.PokemonLootModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@Mod(PokeblocksCommon.MOD_ID)
public final class PokeblocksNeoForge {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PokeblocksCommon.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, PokeblocksCommon.MOD_ID);
	
	// Global Loot Modifier registry
	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
			DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, PokeblocksCommon.MOD_ID);
	
	public static final Supplier<MapCodec<PokemonLootModifier>> POKEMON_LOOT_MODIFIER =
			GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("pokemon_loot_modifier", () -> PokemonLootModifier.CODEC);

	public PokeblocksNeoForge(IEventBus modEventBus) {
		BLOCKS.register(modEventBus);
		ENTITIES.register(modEventBus);
		BLOCK_ENTITIES.register(modEventBus);
		CREATIVE_TABS.register(modEventBus);
		ITEMS.register(modEventBus);
		GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);

		PokeblocksCommon.doRegistrations();

		NeoForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		ModCommands.registerCommands(event.getDispatcher());
	}

	@SubscribeEvent
	public void onServerStarted(ServerStartedEvent event) {
		PokeblocksServerLifecycle.onServerStarted(event.getServer());
	}

	// Remove the old loot table load event since we're using GLM now
	// @SubscribeEvent
	// public void onLootTableLoad(LootTableLoadEvent event) {
	//     LootInjector.attemptInjection(event.getName(), builder -> event.getTable().addPool(builder.build()));
	// }
}
