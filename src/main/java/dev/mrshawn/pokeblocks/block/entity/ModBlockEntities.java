package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.block.entity.ampharos.PokedollAmpharosBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ampharos.PokedollShinyAmpharosBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.applin_basket.PokedollApplinBasketBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.applin_basket.PokedollShinyApplinBasketBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.arboliva.PokedollArbolivaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.arboliva.PokedollShinyArbolivaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollBulbasaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollShinyBulbasaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollBulbasaurPosedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollShinyBulbasaurPosedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollShinyCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollCalyrexAnimatedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollShinyCalyrexAnimatedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollShinyCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.dolliv.PokedollDollivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.dolliv.PokedollShinyDollivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.flaaffy.PokedollFlaaffyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.flaaffy.PokedollShinyFlaaffyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.furret.PokedollFurretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.furret.PokedollShinyFurretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollShinyLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollShinyMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.munchlax.PokedollMunchlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.munchlax.PokedollShinyMunchlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rabsca.PokedollRabscaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rabsca.PokedollShinyRabscaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rellor.PokedollRellorBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rellor.PokedollShinyRellorBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sentret.PokedollSentretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sentret.PokedollShinySentretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.smoliv.PokedollShinySmolivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.smoliv.PokedollSmolivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollShinySnorlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollSnorlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollWashingMachineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollShinySquirtleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollSquirtleBlockEntity;
import dev.mrshawn.pokeblocks.constants.PokeIDs;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

	public static BlockEntityType<PokedollCalyrexBlockEntity> POKEDOLL_CALYREX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyCalyrexBlockEntity> POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollCalyrexAnimatedBlockEntity> POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyCalyrexAnimatedBlockEntity> POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollBulbasaurBlockEntity> POKEDOLL_BULBASAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyBulbasaurBlockEntity> POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollBulbasaurPosedBlockEntity> POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyBulbasaurPosedBlockEntity> POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollSquirtleBlockEntity> POKEDOLL_SQUIRTLE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinySquirtleBlockEntity> POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollCharmanderBlockEntity> POKEDOLL_CHARMANDER_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyCharmanderBlockEntity> POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY;
	public static BlockEntityType<PokedollLickitungBlockEntity> POKEDOLL_LICKITUNG_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyLickitungBlockEntity> POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY;
	public static BlockEntityType<PokedollMareepBlockEntity> POKEDOLL_MAREEP_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyMareepBlockEntity> POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY;
	public static BlockEntityType<PokedollFlaaffyBlockEntity> POKEDOLL_FLAAFFY_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyFlaaffyBlockEntity> POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY;
	public static BlockEntityType<PokedollSmolivBlockEntity> POKEDOLL_SMOLIV_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinySmolivBlockEntity> POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY;
	public static BlockEntityType<PokedollDollivBlockEntity> POKEDOLL_DOLLIV_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyDollivBlockEntity> POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY;
	public static BlockEntityType<PokedollArbolivaBlockEntity> POKEDOLL_ARBOLIVA_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyArbolivaBlockEntity> POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY;
	public static BlockEntityType<PokedollWashingMachineBlockEntity> POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollSnorlaxBlockEntity> POKEDOLL_SNORLAX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinySnorlaxBlockEntity> POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollAmpharosBlockEntity> POKEDOLL_AMPHAROS_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyAmpharosBlockEntity> POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY;
	public static BlockEntityType<PokedollSentretBlockEntity> POKEDOLL_SENTRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinySentretBlockEntity> POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollFurretBlockEntity> POKEDOLL_FURRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyFurretBlockEntity> POKEDOLL_SHINY_FURRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollApplinBasketBlockEntity> POKEDOLL_APPLIN_BASKET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyApplinBasketBlockEntity> POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollMunchlaxBlockEntity> POKEDOLL_MUNCHLAX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyMunchlaxBlockEntity> POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollRabscaBlockEntity> POKEDOLL_RABSCA_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyRabscaBlockEntity> POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY;
	public static BlockEntityType<PokedollRellorBlockEntity> POKEDOLL_RELLOR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyRellorBlockEntity> POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY;

	public static void registerAllBlockEntities() {
		POKEDOLL_CALYREX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX),
				FabricBlockEntityTypeBuilder.create(PokedollCalyrexBlockEntity::new, ModBlocks.POKEDOLL_CALYREX),
				PokedollCalyrexBlockEntity.class
		);

		POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX),
				FabricBlockEntityTypeBuilder.create(PokedollShinyCalyrexBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CALYREX),
				PokedollShinyCalyrexBlockEntity.class
		);

		POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX_ANIMATED),
				FabricBlockEntityTypeBuilder.create(PokedollCalyrexAnimatedBlockEntity::new, ModBlocks.POKEDOLL_CALYREX_ANIMATED),
				PokedollCalyrexAnimatedBlockEntity.class
		);

		POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX_ANIMATED),
				FabricBlockEntityTypeBuilder.create(PokedollShinyCalyrexAnimatedBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CALYREX_ANIMATED),
				PokedollShinyCalyrexAnimatedBlockEntity.class
		);

		POKEDOLL_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR),
				FabricBlockEntityTypeBuilder.create(PokedollBulbasaurBlockEntity::new, ModBlocks.POKEDOLL_BULBASAUR),
				PokedollBulbasaurBlockEntity.class
		);

		POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR),
				FabricBlockEntityTypeBuilder.create(PokedollShinyBulbasaurBlockEntity::new, ModBlocks.POKEDOLL_SHINY_BULBASAUR),
				PokedollShinyBulbasaurBlockEntity.class
		);

		POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR_POSED),
				FabricBlockEntityTypeBuilder.create(PokedollBulbasaurPosedBlockEntity::new, ModBlocks.POKEDOLL_BULBASAUR_POSED),
				PokedollBulbasaurPosedBlockEntity.class
		);

		POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR_POSED),
				FabricBlockEntityTypeBuilder.create(PokedollShinyBulbasaurPosedBlockEntity::new, ModBlocks.POKEDOLL_SHINY_BULBASAUR_POSED),
				PokedollShinyBulbasaurPosedBlockEntity.class
		);

		POKEDOLL_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SQUIRTLE),
				FabricBlockEntityTypeBuilder.create(PokedollSquirtleBlockEntity::new, ModBlocks.POKEDOLL_SQUIRTLE),
				PokedollSquirtleBlockEntity.class
		);

		POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SQUIRTLE),
				FabricBlockEntityTypeBuilder.create(PokedollShinySquirtleBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SQUIRTLE),
				PokedollShinySquirtleBlockEntity.class
		);

		POKEDOLL_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CHARMANDER),
				FabricBlockEntityTypeBuilder.create(PokedollCharmanderBlockEntity::new, ModBlocks.POKEDOLL_CHARMANDER),
				PokedollCharmanderBlockEntity.class
		);

		POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CHARMANDER),
				FabricBlockEntityTypeBuilder.create(PokedollShinyCharmanderBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CHARMANDER),
				PokedollShinyCharmanderBlockEntity.class
		);

		POKEDOLL_LICKITUNG_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_LICKITUNG),
				FabricBlockEntityTypeBuilder.create(PokedollLickitungBlockEntity::new, ModBlocks.POKEDOLL_LICKITUNG),
				PokedollLickitungBlockEntity.class
		);

		POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_LICKITUNG),
				FabricBlockEntityTypeBuilder.create(PokedollShinyLickitungBlockEntity::new, ModBlocks.POKEDOLL_SHINY_LICKITUNG),
				PokedollShinyLickitungBlockEntity.class
		);

		POKEDOLL_MAREEP_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MAREEP),
				FabricBlockEntityTypeBuilder.create(PokedollMareepBlockEntity::new, ModBlocks.POKEDOLL_MAREEP),
				PokedollMareepBlockEntity.class
		);

		POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MAREEP),
				FabricBlockEntityTypeBuilder.create(PokedollShinyMareepBlockEntity::new, ModBlocks.POKEDOLL_SHINY_MAREEP),
				PokedollShinyMareepBlockEntity.class
		);

		POKEDOLL_FLAAFFY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FLAAFFY),
				FabricBlockEntityTypeBuilder.create(PokedollFlaaffyBlockEntity::new, ModBlocks.POKEDOLL_FLAAFFY),
				PokedollFlaaffyBlockEntity.class
		);

		POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FLAAFFY),
				FabricBlockEntityTypeBuilder.create(PokedollShinyFlaaffyBlockEntity::new, ModBlocks.POKEDOLL_SHINY_FLAAFFY),
				PokedollShinyFlaaffyBlockEntity.class
		);

		POKEDOLL_SMOLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SMOLIV),
				FabricBlockEntityTypeBuilder.create(PokedollSmolivBlockEntity::new, ModBlocks.POKEDOLL_SMOLIV),
				PokedollSmolivBlockEntity.class
		);

		POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SMOLIV),
				FabricBlockEntityTypeBuilder.create(PokedollShinySmolivBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SMOLIV),
				PokedollShinySmolivBlockEntity.class
		);

		POKEDOLL_DOLLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_DOLLIV),
				FabricBlockEntityTypeBuilder.create(PokedollDollivBlockEntity::new, ModBlocks.POKEDOLL_DOLLIV),
				PokedollDollivBlockEntity.class
		);

		POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_DOLLIV),
				FabricBlockEntityTypeBuilder.create(PokedollShinyDollivBlockEntity::new, ModBlocks.POKEDOLL_SHINY_DOLLIV),
				PokedollShinyDollivBlockEntity.class
		);

		POKEDOLL_ARBOLIVA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ARBOLIVA),
				FabricBlockEntityTypeBuilder.create(PokedollArbolivaBlockEntity::new, ModBlocks.POKEDOLL_ARBOLIVA),
				PokedollArbolivaBlockEntity.class
		);

		POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ARBOLIVA),
				FabricBlockEntityTypeBuilder.create(PokedollShinyArbolivaBlockEntity::new, ModBlocks.POKEDOLL_SHINY_ARBOLIVA),
				PokedollShinyArbolivaBlockEntity.class
		);

		POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WASHING_MACHINE),
				FabricBlockEntityTypeBuilder.create(PokedollWashingMachineBlockEntity::new, ModBlocks.POKEDOLL_WASHING_MACHINE),
				PokedollWashingMachineBlockEntity.class
		);

		POKEDOLL_SNORLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SNORLAX),
				FabricBlockEntityTypeBuilder.create(PokedollSnorlaxBlockEntity::new, ModBlocks.POKEDOLL_SNORLAX),
				PokedollSnorlaxBlockEntity.class
		);

		POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SNORLAX),
				FabricBlockEntityTypeBuilder.create(PokedollShinySnorlaxBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SNORLAX),
				PokedollShinySnorlaxBlockEntity.class
		);

		POKEDOLL_AMPHAROS_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_AMPHAROS),
				FabricBlockEntityTypeBuilder.create(PokedollAmpharosBlockEntity::new, ModBlocks.POKEDOLL_AMPHAROS),
				PokedollAmpharosBlockEntity.class
		);

		POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_AMPHAROS),
				FabricBlockEntityTypeBuilder.create(PokedollShinyAmpharosBlockEntity::new, ModBlocks.POKEDOLL_SHINY_AMPHAROS),
				PokedollShinyAmpharosBlockEntity.class
		);

		POKEDOLL_SENTRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SENTRET),
				FabricBlockEntityTypeBuilder.create(PokedollSentretBlockEntity::new, ModBlocks.POKEDOLL_SENTRET),
				PokedollSentretBlockEntity.class
		);

		POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SENTRET),
				FabricBlockEntityTypeBuilder.create(PokedollShinySentretBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SENTRET),
				PokedollShinySentretBlockEntity.class
		);

		POKEDOLL_FURRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FURRET),
				FabricBlockEntityTypeBuilder.create(PokedollFurretBlockEntity::new, ModBlocks.POKEDOLL_FURRET),
				PokedollFurretBlockEntity.class
		);

		POKEDOLL_SHINY_FURRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FURRET),
				FabricBlockEntityTypeBuilder.create(PokedollShinyFurretBlockEntity::new, ModBlocks.POKEDOLL_SHINY_FURRET),
				PokedollShinyFurretBlockEntity.class
		);

		POKEDOLL_APPLIN_BASKET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_APPLIN_BASKET),
				FabricBlockEntityTypeBuilder.create(PokedollApplinBasketBlockEntity::new, ModBlocks.POKEDOLL_APPLIN_BASKET),
				PokedollApplinBasketBlockEntity.class
		);
		POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_APPLIN_BASKET),
				FabricBlockEntityTypeBuilder.create(PokedollShinyApplinBasketBlockEntity::new, ModBlocks.POKEDOLL_SHINY_APPLIN_BASKET),
				PokedollShinyApplinBasketBlockEntity.class
		);

		POKEDOLL_MUNCHLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MUNCHLAX),
				FabricBlockEntityTypeBuilder.create(PokedollMunchlaxBlockEntity::new, ModBlocks.POKEDOLL_MUNCHLAX),
				PokedollMunchlaxBlockEntity.class
		);
		POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MUNCHLAX),
				FabricBlockEntityTypeBuilder.create(PokedollShinyMunchlaxBlockEntity::new, ModBlocks.POKEDOLL_SHINY_MUNCHLAX),
				PokedollShinyMunchlaxBlockEntity.class
		);

		POKEDOLL_RABSCA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RABSCA),
				FabricBlockEntityTypeBuilder.create(PokedollRabscaBlockEntity::new, ModBlocks.POKEDOLL_RABSCA),
				PokedollRabscaBlockEntity.class
		);
		POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RABSCA),
				FabricBlockEntityTypeBuilder.create(PokedollShinyRabscaBlockEntity::new, ModBlocks.POKEDOLL_SHINY_RABSCA),
				PokedollShinyRabscaBlockEntity.class
		);

		POKEDOLL_RELLOR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RELLOR),
				FabricBlockEntityTypeBuilder.create(PokedollRellorBlockEntity::new, ModBlocks.POKEDOLL_RELLOR),
				PokedollRellorBlockEntity.class
		);
		POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RELLOR),
				FabricBlockEntityTypeBuilder.create(PokedollShinyRellorBlockEntity::new, ModBlocks.POKEDOLL_SHINY_RELLOR),
				PokedollShinyRellorBlockEntity.class
		);
	}

	private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(Identifier id, FabricBlockEntityTypeBuilder<T> builder, Class<T> blockEntityClass) {
		BlockEntityType<T> blockEntityType = Registry.register(Registries.BLOCK_ENTITY_TYPE, id, builder.build());
		BlockEntityTypeRegistry.register(blockEntityClass, blockEntityType);
		return blockEntityType;
	}

}
