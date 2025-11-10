package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.command.ModCommands;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(PokeblocksCommon.MOD_ID)
public final class PokeblocksNeoForge {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, PokeblocksCommon.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PokeblocksCommon.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PokeblocksCommon.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, PokeblocksCommon.MOD_ID);

    public PokeblocksNeoForge(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        ITEMS.register(modEventBus);

        PokeblocksCommon.doRegistrations();

		NeoForge.EVENT_BUS.register(this);
    }

	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		ModCommands.registerCommands(event.getDispatcher());
	}
}
