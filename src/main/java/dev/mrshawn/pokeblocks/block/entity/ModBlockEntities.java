package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.block.entity.a09robert_figurine.A09RobertFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.airuhsea_figurine.PokedollAiruhseaFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.applin_basket.PokedollApplinBasketBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.applin_basket.PokedollShinyApplinBasketBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.damorgo_figurine.PokedollDamorgoFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.doncheadle_figurine.PokedollDoncheadleFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.magikarp_fishbowl.PokedollMagikarpFishbowlBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.magikarp_fishbowl.PokedollShinyMagikarpFishbowlBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mik_03_figurine.PokedollMik03FigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.pohello_figurine.PokedollPohelloFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.pokemon_trophy.PokedollPokemonTrophyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.red_communism_figurine.RedCommunismFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.tropsic0_figurine.Tropsic0FigurineBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

	public static BlockEntityType<PokedollBlockEntity> POKEDOLL_CHARMANDER_BLOCK_ENTITY;

//	public static BlockEntityType<PokedollCalyrexBlockEntity> POKEDOLL_CALYREX_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyCalyrexBlockEntity> POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollCalyrexAnimatedBlockEntity> POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyCalyrexAnimatedBlockEntity> POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollBulbasaurBlockEntity> POKEDOLL_BULBASAUR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyBulbasaurBlockEntity> POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollBulbasaurPosedBlockEntity> POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyBulbasaurPosedBlockEntity> POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollSquirtleBlockEntity> POKEDOLL_SQUIRTLE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinySquirtleBlockEntity> POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollCharmanderBlockEntity> POKEDOLL_CHARMANDER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyCharmanderBlockEntity> POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollLickitungBlockEntity> POKEDOLL_LICKITUNG_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyLickitungBlockEntity> POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollMareepBlockEntity> POKEDOLL_MAREEP_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyMareepBlockEntity> POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollFlaaffyBlockEntity> POKEDOLL_FLAAFFY_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyFlaaffyBlockEntity> POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollSmolivBlockEntity> POKEDOLL_SMOLIV_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinySmolivBlockEntity> POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollDollivBlockEntity> POKEDOLL_DOLLIV_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyDollivBlockEntity> POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollArbolivaBlockEntity> POKEDOLL_ARBOLIVA_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyArbolivaBlockEntity> POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollWashingMachineBlockEntity> POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollSnorlaxBlockEntity> POKEDOLL_SNORLAX_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinySnorlaxBlockEntity> POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollAmpharosBlockEntity> POKEDOLL_AMPHAROS_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyAmpharosBlockEntity> POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollSentretBlockEntity> POKEDOLL_SENTRET_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinySentretBlockEntity> POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollFurretBlockEntity> POKEDOLL_FURRET_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyFurretBlockEntity> POKEDOLL_SHINY_FURRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollApplinBasketBlockEntity> POKEDOLL_APPLIN_BASKET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyApplinBasketBlockEntity> POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollMunchlaxBlockEntity> POKEDOLL_MUNCHLAX_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyMunchlaxBlockEntity> POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollRabscaBlockEntity> POKEDOLL_RABSCA_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyRabscaBlockEntity> POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollRellorBlockEntity> POKEDOLL_RELLOR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyRellorBlockEntity> POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollWartortleBlockEntity> POKEDOLL_WARTORTLE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyWartortleBlockEntity> POKEDOLL_SHINY_WARTORTLE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollSableyeBlockEntity> POKEDOLL_SABLEYE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinySableyeBlockEntity> POKEDOLL_SHINY_SABLEYE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollAbsolBlockEntity> POKEDOLL_ABSOL_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyAbsolBlockEntity> POKEDOLL_SHINY_ABSOL_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollHappinyBlockEntity> POKEDOLL_HAPPINY_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyHappinyBlockEntity> POKEDOLL_SHINY_HAPPINY_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollIvysaurBlockEntity> POKEDOLL_IVYSAUR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyIvysaurBlockEntity> POKEDOLL_SHINY_IVYSAUR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollVenusaurBlockEntity> POKEDOLL_VENUSAUR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyVenusaurBlockEntity> POKEDOLL_SHINY_VENUSAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollAiruhseaFigurineBlockEntity> POKEDOLL_AIRUHSEA_FIGURINE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollDamorgoFigurineBlockEntity> POKEDOLL_DAMORGO_FIGURINE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollDoncheadleFigurineBlockEntity> POKEDOLL_DONCHEADLE_FIGURINE_BLOCK_ENTITY;

    public static BlockEntityType<PokedollMik03FigurineBlockEntity> FIGURINE_MIK_03_BLOCK_ENTITY;

    public static BlockEntityType<PokedollPohelloFigurineBlockEntity> FIGURINE_POHELLO_BLOCK_ENTITY;
	public static BlockEntityType<PokedollMagikarpFishbowlBlockEntity> POKEDOLL_MAGIKARP_FISHBOWL_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyMagikarpFishbowlBlockEntity> POKEDOLL_SHINY_MAGIKARP_FISHBOWL_BLOCK_ENTITY;
	public static BlockEntityType<PokedollPokemonTrophyBlockEntity> POKEDOLL_POKEMON_TROPHY_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollBlastoiseBlockEntity> POKEDOLL_BLASTOISE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyBlastoiseBlockEntity> POKEDOLL_SHINY_BLASTOISE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollSwinubBlockEntity> POKEDOLL_SWINUB_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinySwinubBlockEntity> POKEDOLL_SHINY_SWINUB_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollWooperBlockEntity> POKEDOLL_WOOPER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyWooperBlockEntity> POKEDOLL_SHINY_WOOPER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollQuagsireBlockEntity> POKEDOLL_QUAGSIRE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyQuagsireBlockEntity> POKEDOLL_SHINY_QUAGSIRE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticAbsolBlockEntity> GIGANTIC_POKEDOLL_ABSOL_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyAbsolBlockEntity> GIGANTIC_POKEDOLL_SHINY_ABSOL_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticAmpharosBlockEntity> GIGANTIC_POKEDOLL_AMPHAROS_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyAmpharosBlockEntity> GIGANTIC_POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticArbolivaBlockEntity> GIGANTIC_POKEDOLL_ARBOLIVA_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyArbolivaBlockEntity> GIGANTIC_POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticBlastoiseBlockEntity> GIGANTIC_POKEDOLL_BLASTOISE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyBlastoiseBlockEntity> GIGANTIC_POKEDOLL_SHINY_BLASTOISE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticBulbasaurBlockEntity> GIGANTIC_POKEDOLL_BULBASAUR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticBulbasaurPosedBlockEntity> GIGANTIC_POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyBulbasaurBlockEntity> GIGANTIC_POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyBulbasaurPosedBlockEntity> GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticCalyrexBlockEntity> GIGANTIC_POKEDOLL_CALYREX_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticCalyrexAnimatedBlockEntity> GIGANTIC_POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyCalyrexBlockEntity> GIGANTIC_POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyCalyrexAnimatedBlockEntity> GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticCharmanderBlockEntity> GIGANTIC_POKEDOLL_CHARMANDER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyCharmanderBlockEntity> GIGANTIC_POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticDollivBlockEntity> GIGANTIC_POKEDOLL_DOLLIV_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyDollivBlockEntity> GIGANTIC_POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticFlaaffyBlockEntity> GIGANTIC_POKEDOLL_FLAAFFY_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyFlaaffyBlockEntity> GIGANTIC_POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticFurretBlockEntity> GIGANTIC_POKEDOLL_FURRET_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyFurretBlockEntity> GIGANTIC_POKEDOLL_SHINY_FURRET_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticHappinyBlockEntity> GIGANTIC_POKEDOLL_HAPPINY_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyHappinyBlockEntity> GIGANTIC_POKEDOLL_SHINY_HAPPINY_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticIvysaurBlockEntity> GIGANTIC_POKEDOLL_IVYSAUR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyIvysaurBlockEntity> GIGANTIC_POKEDOLL_SHINY_IVYSAUR_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticLickitungBlockEntity> GIGANTIC_POKEDOLL_LICKITUNG_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyLickitungBlockEntity> GIGANTIC_POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticMareepBlockEntity> GIGANTIC_POKEDOLL_MAREEP_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyMareepBlockEntity> GIGANTIC_POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticMunchlaxBlockEntity> GIGANTIC_POKEDOLL_MUNCHLAX_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyMunchlaxBlockEntity> GIGANTIC_POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticQuagsireBlockEntity> GIGANTIC_POKEDOLL_QUAGSIRE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyQuagsireBlockEntity> GIGANTIC_POKEDOLL_SHINY_QUAGSIRE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticRabscaBlockEntity> GIGANTIC_POKEDOLL_RABSCA_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyRabscaBlockEntity> GIGANTIC_POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticRellorBlockEntity> GIGANTIC_POKEDOLL_RELLOR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyRellorBlockEntity> GIGANTIC_POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticSableyeBlockEntity> GIGANTIC_POKEDOLL_SABLEYE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinySableyeBlockEntity> GIGANTIC_POKEDOLL_SHINY_SABLEYE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticSentretBlockEntity> GIGANTIC_POKEDOLL_SENTRET_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinySentretBlockEntity> GIGANTIC_POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticSmolivBlockEntity> GIGANTIC_POKEDOLL_SMOLIV_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinySmolivBlockEntity> GIGANTIC_POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticSnorlaxBlockEntity> GIGANTIC_POKEDOLL_SNORLAX_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinySnorlaxBlockEntity> GIGANTIC_POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticSquirtleBlockEntity> GIGANTIC_POKEDOLL_SQUIRTLE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinySquirtleBlockEntity> GIGANTIC_POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticSwinubBlockEntity> GIGANTIC_POKEDOLL_SWINUB_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinySwinubBlockEntity> GIGANTIC_POKEDOLL_SHINY_SWINUB_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticVenusaurBlockEntity> GIGANTIC_POKEDOLL_VENUSAUR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyVenusaurBlockEntity> GIGANTIC_POKEDOLL_SHINY_VENUSAUR_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticWartortleBlockEntity> GIGANTIC_POKEDOLL_WARTORTLE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyWartortleBlockEntity> GIGANTIC_POKEDOLL_SHINY_WARTORTLE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticWashingMachineBlockEntity> GIGANTIC_POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticWooperBlockEntity> GIGANTIC_POKEDOLL_WOOPER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyWooperBlockEntity> GIGANTIC_POKEDOLL_SHINY_WOOPER_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGastlyBlockEntity> POKEDOLL_GASTLY_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyGastlyBlockEntity> POKEDOLL_SHINY_GASTLY_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGengarBlockEntity> POKEDOLL_GENGAR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyGengarBlockEntity> POKEDOLL_SHINY_GENGAR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollDrifloonBlockEntity> POKEDOLL_DRIFLOON_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyDrifloonBlockEntity> POKEDOLL_SHINY_DRIFLOON_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollRookideeBlockEntity> POKEDOLL_ROOKIDEE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyRookideeBlockEntity> POKEDOLL_SHINY_ROOKIDEE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollCorvisquireBlockEntity> POKEDOLL_CORVISQUIRE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyCorvisquireBlockEntity> POKEDOLL_SHINY_CORVISQUIRE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollCorviknightBlockEntity> POKEDOLL_CORVIKNIGHT_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyCorviknightBlockEntity> POKEDOLL_SHINY_CORVIKNIGHT_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollStonjournerBlockEntity> POKEDOLL_STONJOURNER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyStonjournerBlockEntity> POKEDOLL_SHINY_STONJOURNER_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticCorviknightBlockEntity> GIGANTIC_POKEDOLL_CORVIKNIGHT_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyCorviknightBlockEntity> GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticCorvisquireBlockEntity> GIGANTIC_POKEDOLL_CORVISQUIRE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyCorvisquireBlockEntity> GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticDrifloonBlockEntity> GIGANTIC_POKEDOLL_DRIFLOON_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyDrifloonBlockEntity> GIGANTIC_POKEDOLL_SHINY_DRIFLOON_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticGastlyBlockEntity> GIGANTIC_POKEDOLL_GASTLY_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyGastlyBlockEntity> GIGANTIC_POKEDOLL_SHINY_GASTLY_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticGengarBlockEntity> GIGANTIC_POKEDOLL_GENGAR_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyGengarBlockEntity> GIGANTIC_POKEDOLL_SHINY_GENGAR_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticRookideeBlockEntity> GIGANTIC_POKEDOLL_ROOKIDEE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyRookideeBlockEntity> GIGANTIC_POKEDOLL_SHINY_ROOKIDEE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticStonjournerBlockEntity> GIGANTIC_POKEDOLL_STONJOURNER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyStonjournerBlockEntity> GIGANTIC_POKEDOLL_SHINY_STONJOURNER_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollEeveeBlockEntity> POKEDOLL_EEVEE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyEeveeBlockEntity> POKEDOLL_SHINY_EEVEE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollSandygastBlockEntity> POKEDOLL_SANDYGAST_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinySandygastBlockEntity> POKEDOLL_SHINY_SANDYGAST_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollPalossandBlockEntity> POKEDOLL_PALOSSAND_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyPalossandBlockEntity> POKEDOLL_SHINY_PALOSSAND_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGholdengoBlockEntity> POKEDOLL_GHOLDENGO_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyGholdengoBlockEntity> POKEDOLL_SHINY_GHOLDENGO_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollNetheriteGholdengoBlockEntity> POKEDOLL_NETHERITE_GHOLDENGO_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyNetheriteGholdengoBlockEntity> POKEDOLL_SHINY_NETHERITE_GHOLDENGO_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticEeveeBlockEntity> GIGANTIC_POKEDOLL_EEVEE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyEeveeBlockEntity> GIGANTIC_POKEDOLL_SHINY_EEVEE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticGholdengoBlockEntity> GIGANTIC_POKEDOLL_GHOLDENGO_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyGholdengoBlockEntity> GIGANTIC_POKEDOLL_SHINY_GHOLDENGO_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticNetheriteGholdengoBlockEntity> GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyNetheriteGholdengoBlockEntity> GIGANTIC_POKEDOLL_SHINY_NETHERITE_GHOLDENGO_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticPalossandBlockEntity> GIGANTIC_POKEDOLL_PALOSSAND_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyPalossandBlockEntity> GIGANTIC_POKEDOLL_SHINY_PALOSSAND_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticSandygastBlockEntity> GIGANTIC_POKEDOLL_SANDYGAST_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinySandygastBlockEntity> GIGANTIC_POKEDOLL_SHINY_SANDYGAST_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollShellderBlockEntity> POKEDOLL_SHELLDER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyShellderBlockEntity> POKEDOLL_SHINY_SHELLDER_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollCloysterBlockEntity> POKEDOLL_CLOYSTER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyCloysterBlockEntity> POKEDOLL_SHINY_CLOYSTER_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollWailmerBlockEntity> POKEDOLL_WAILMER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyWailmerBlockEntity> POKEDOLL_SHINY_WAILMER_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollWailordBlockEntity> POKEDOLL_WAILORD_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyWailordBlockEntity> POKEDOLL_SHINY_WAILORD_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollTropiusBlockEntity> POKEDOLL_TROPIUS_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyTropiusBlockEntity> POKEDOLL_SHINY_TROPIUS_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollKyogreBlockEntity> POKEDOLL_KYOGRE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyKyogreBlockEntity> POKEDOLL_SHINY_KYOGRE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticCloysterBlockEntity> GIGANTIC_POKEDOLL_CLOYSTER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyCloysterBlockEntity> GIGANTIC_POKEDOLL_SHINY_CLOYSTER_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticKyogreBlockEntity> GIGANTIC_POKEDOLL_KYOGRE_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyKyogreBlockEntity> GIGANTIC_POKEDOLL_SHINY_KYOGRE_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticShellderBlockEntity> GIGANTIC_POKEDOLL_SHELLDER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyShellderBlockEntity> GIGANTIC_POKEDOLL_SHINY_SHELLDER_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticTropiusBlockEntity> GIGANTIC_POKEDOLL_TROPIUS_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyTropiusBlockEntity> GIGANTIC_POKEDOLL_SHINY_TROPIUS_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticWailmerBlockEntity> GIGANTIC_POKEDOLL_WAILMER_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyWailmerBlockEntity> GIGANTIC_POKEDOLL_SHINY_WAILMER_BLOCK_ENTITY;
//
//	public static BlockEntityType<PokedollGiganticWailordBlockEntity> GIGANTIC_POKEDOLL_WAILORD_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyWailordBlockEntity> GIGANTIC_POKEDOLL_SHINY_WAILORD_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollPhantumpBlockEntity> POKEDOLL_PHANTUMP_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyPhantumpBlockEntity> POKEDOLL_SHINY_PHANTUMP_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollPumpkabooBlockEntity> POKEDOLL_PUMPKABOO_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyPumpkabooBlockEntity> POKEDOLL_SHINY_PUMPKABOO_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollTrevenantBlockEntity> POKEDOLL_TREVENANT_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyTrevenantBlockEntity> POKEDOLL_SHINY_TREVENANT_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollMarshadowBlockEntity> POKEDOLL_MARSHADOW_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyMarshadowBlockEntity> POKEDOLL_SHINY_MARSHADOW_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollMarshadowZenithBlockEntity> POKEDOLL_MARSHADOW_ZENITH_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollShinyMarshadowZenithBlockEntity> POKEDOLL_SHINY_MARSHADOW_ZENITH_BLOCK_ENTITY;
//
//    public static BlockEntityType<PokedollGiganticMarshadowBlockEntity> GIGANTIC_POKEDOLL_MARSHADOW_BLOCK_ENTITY;
//    public static BlockEntityType<PokedollGiganticShinyMarshadowBlockEntity> GIGANTIC_POKEDOLL_SHINY_MARSHADOW_BLOCK_ENTITY;
//
//    public static BlockEntityType<PokedollGiganticPhantumpBlockEntity> GIGANTIC_POKEDOLL_PHANTUMP_BLOCK_ENTITY;
//    public static BlockEntityType<PokedollGiganticShinyPhantumpBlockEntity> GIGANTIC_POKEDOLL_SHINY_PHANTUMP_BLOCK_ENTITY;
//
//    public static BlockEntityType<PokedollGiganticPumpkabooBlockEntity> GIGANTIC_POKEDOLL_PUMPKABOO_BLOCK_ENTITY;
//    public static BlockEntityType<PokedollGiganticShinyPumpkabooBlockEntity> GIGANTIC_POKEDOLL_SHINY_PUMPKABOO_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticMarshadowZenithBlockEntity> GIGANTIC_POKEDOLL_MARSHADOW_ZENITH_BLOCK_ENTITY;
//	public static BlockEntityType<PokedollGiganticShinyMarshadowZenithBlockEntity> GIGANTIC_POKEDOLL_SHINY_MARSHADOW_ZENITH_BLOCK_ENTITY;
//
//    public static BlockEntityType<PokedollGiganticTrevenantBlockEntity> GIGANTIC_POKEDOLL_TREVENANT_BLOCK_ENTITY;
//    public static BlockEntityType<PokedollGiganticShinyTrevenantBlockEntity> GIGANTIC_POKEDOLL_SHINY_TREVENANT_BLOCK_ENTITY;
//
//    public static BlockEntityType<PokedollBellossomBlockEntity> POKEDOLL_BELLOSSOM_BLOCK_ENTITY;
//    public static BlockEntityType<PokedollShinyBellossomBlockEntity> POKEDOLL_SHINY_BELLOSSOM_BLOCK_ENTITY;
//
//    public static BlockEntityType<PokedollRowletBlockEntity> POKEDOLL_ROWLET_BLOCK_ENTITY;
//    public static BlockEntityType<PokedollShinyRowletBlockEntity> POKEDOLL_SHINY_ROWLET_BLOCK_ENTITY;
//
//    public static BlockEntityType<PokedollMimikyuBlockEntity> POKEDOLL_MIMIKYU_BLOCK_ENTITY;
//    public static BlockEntityType<PokedollShinyMimikyuBlockEntity> POKEDOLL_SHINY_MIMIKYU_BLOCK_ENTITY;
//
//    public static BlockEntityType<PokedollGiganticBellossomBlockEntity> GIGANTIC_POKEDOLL_BELLOSSOM_BLOCK_ENTITY;
//    public static BlockEntityType<PokedollGiganticShinyBellossomBlockEntity> GIGANTIC_POKEDOLL_SHINY_BELLOSSOM_BLOCK_ENTITY;
//
//    public static BlockEntityType<PokedollGiganticMimikyuBlockEntity> GIGANTIC_POKEDOLL_MIMIKYU_BLOCK_ENTITY;
//    public static BlockEntityType<PokedollGiganticShinyMimikyuBlockEntity> GIGANTIC_POKEDOLL_SHINY_MIMIKYU_BLOCK_ENTITY;
//
//    public static BlockEntityType<PokedollGiganticRowletBlockEntity> GIGANTIC_POKEDOLL_ROWLET_BLOCK_ENTITY;
//    public static BlockEntityType<PokedollGiganticShinyRowletBlockEntity> GIGANTIC_POKEDOLL_SHINY_ROWLET_BLOCK_ENTITY;
	public static BlockEntityType<A09RobertFigurineBlockEntity> A09ROBERT_FIGURINE_BLOCK_ENTITY;
	public static BlockEntityType<RedCommunismFigurineBlockEntity> RED_COMMUNISM_FIGURINE_BLOCK_ENTITY;
	public static BlockEntityType<Tropsic0FigurineBlockEntity> TROPSIC0_FIGURINE_BLOCK_ENTITY;

	public static void registerAllBlockEntities() {
//		POKEDOLL_CALYREX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX),
//				FabricBlockEntityTypeBuilder.create(PokedollCalyrexBlockEntity::new, ModBlocks.POKEDOLL_CALYREX),
//				PokedollCalyrexBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyCalyrexBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CALYREX),
//				PokedollShinyCalyrexBlockEntity.class
//		);
//
//		POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX_ANIMATED),
//				FabricBlockEntityTypeBuilder.create(PokedollCalyrexAnimatedBlockEntity::new, ModBlocks.POKEDOLL_CALYREX_ANIMATED),
//				PokedollCalyrexAnimatedBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX_ANIMATED),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyCalyrexAnimatedBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CALYREX_ANIMATED),
//				PokedollShinyCalyrexAnimatedBlockEntity.class
//		);
//
//		POKEDOLL_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollBulbasaurBlockEntity::new, ModBlocks.POKEDOLL_BULBASAUR),
//				PokedollBulbasaurBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyBulbasaurBlockEntity::new, ModBlocks.POKEDOLL_SHINY_BULBASAUR),
//				PokedollShinyBulbasaurBlockEntity.class
//		);
//
//		POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR_POSED),
//				FabricBlockEntityTypeBuilder.create(PokedollBulbasaurPosedBlockEntity::new, ModBlocks.POKEDOLL_BULBASAUR_POSED),
//				PokedollBulbasaurPosedBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR_POSED),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyBulbasaurPosedBlockEntity::new, ModBlocks.POKEDOLL_SHINY_BULBASAUR_POSED),
//				PokedollShinyBulbasaurPosedBlockEntity.class
//		);
//
//		POKEDOLL_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SQUIRTLE),
//				FabricBlockEntityTypeBuilder.create(PokedollSquirtleBlockEntity::new, ModBlocks.POKEDOLL_SQUIRTLE),
//				PokedollSquirtleBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SQUIRTLE),
//				FabricBlockEntityTypeBuilder.create(PokedollShinySquirtleBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SQUIRTLE),
//				PokedollShinySquirtleBlockEntity.class
//		);

//		POKEDOLL_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CHARMANDER),
//				FabricBlockEntityTypeBuilder.create(PokedollCharmanderBlockEntity::new, ModBlocks.POKEDOLL_CHARMANDER),
//				PokedollCharmanderBlockEntity.class
//		);

		POKEDOLL_CHARMANDER_BLOCK_ENTITY = PokedollBlockEntityFactory.createDefault(
				"pokedoll_charmander",
				ModBlocks.POKEDOLL_CHARMANDER,
				ResourceConstants.POKEDOLL_CHARMANDER_MODEL,
				ResourceConstants.POKEDOLL_CHARMANDER_TEXTURE
		);

//		POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CHARMANDER),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyCharmanderBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CHARMANDER),
//				PokedollShinyCharmanderBlockEntity.class
//		);
//
//		POKEDOLL_LICKITUNG_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_LICKITUNG),
//				FabricBlockEntityTypeBuilder.create(PokedollLickitungBlockEntity::new, ModBlocks.POKEDOLL_LICKITUNG),
//				PokedollLickitungBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_LICKITUNG),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyLickitungBlockEntity::new, ModBlocks.POKEDOLL_SHINY_LICKITUNG),
//				PokedollShinyLickitungBlockEntity.class
//		);
//
//		POKEDOLL_MAREEP_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MAREEP),
//				FabricBlockEntityTypeBuilder.create(PokedollMareepBlockEntity::new, ModBlocks.POKEDOLL_MAREEP),
//				PokedollMareepBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MAREEP),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyMareepBlockEntity::new, ModBlocks.POKEDOLL_SHINY_MAREEP),
//				PokedollShinyMareepBlockEntity.class
//		);
//
//		POKEDOLL_FLAAFFY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FLAAFFY),
//				FabricBlockEntityTypeBuilder.create(PokedollFlaaffyBlockEntity::new, ModBlocks.POKEDOLL_FLAAFFY),
//				PokedollFlaaffyBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FLAAFFY),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyFlaaffyBlockEntity::new, ModBlocks.POKEDOLL_SHINY_FLAAFFY),
//				PokedollShinyFlaaffyBlockEntity.class
//		);
//
//		POKEDOLL_SMOLIV_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SMOLIV),
//				FabricBlockEntityTypeBuilder.create(PokedollSmolivBlockEntity::new, ModBlocks.POKEDOLL_SMOLIV),
//				PokedollSmolivBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SMOLIV),
//				FabricBlockEntityTypeBuilder.create(PokedollShinySmolivBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SMOLIV),
//				PokedollShinySmolivBlockEntity.class
//		);
//
//		POKEDOLL_DOLLIV_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_DOLLIV),
//				FabricBlockEntityTypeBuilder.create(PokedollDollivBlockEntity::new, ModBlocks.POKEDOLL_DOLLIV),
//				PokedollDollivBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_DOLLIV),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyDollivBlockEntity::new, ModBlocks.POKEDOLL_SHINY_DOLLIV),
//				PokedollShinyDollivBlockEntity.class
//		);
//
//		POKEDOLL_ARBOLIVA_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ARBOLIVA),
//				FabricBlockEntityTypeBuilder.create(PokedollArbolivaBlockEntity::new, ModBlocks.POKEDOLL_ARBOLIVA),
//				PokedollArbolivaBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ARBOLIVA),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyArbolivaBlockEntity::new, ModBlocks.POKEDOLL_SHINY_ARBOLIVA),
//				PokedollShinyArbolivaBlockEntity.class
//		);
//
//		POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WASHING_MACHINE),
//				FabricBlockEntityTypeBuilder.create(PokedollWashingMachineBlockEntity::new, ModBlocks.POKEDOLL_WASHING_MACHINE),
//				PokedollWashingMachineBlockEntity.class
//		);
//
//		POKEDOLL_SNORLAX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SNORLAX),
//				FabricBlockEntityTypeBuilder.create(PokedollSnorlaxBlockEntity::new, ModBlocks.POKEDOLL_SNORLAX),
//				PokedollSnorlaxBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SNORLAX),
//				FabricBlockEntityTypeBuilder.create(PokedollShinySnorlaxBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SNORLAX),
//				PokedollShinySnorlaxBlockEntity.class
//		);
//
//		POKEDOLL_AMPHAROS_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_AMPHAROS),
//				FabricBlockEntityTypeBuilder.create(PokedollAmpharosBlockEntity::new, ModBlocks.POKEDOLL_AMPHAROS),
//				PokedollAmpharosBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_AMPHAROS),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyAmpharosBlockEntity::new, ModBlocks.POKEDOLL_SHINY_AMPHAROS),
//				PokedollShinyAmpharosBlockEntity.class
//		);
//
//		POKEDOLL_SENTRET_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SENTRET),
//				FabricBlockEntityTypeBuilder.create(PokedollSentretBlockEntity::new, ModBlocks.POKEDOLL_SENTRET),
//				PokedollSentretBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SENTRET),
//				FabricBlockEntityTypeBuilder.create(PokedollShinySentretBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SENTRET),
//				PokedollShinySentretBlockEntity.class
//		);
//
//		POKEDOLL_FURRET_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FURRET),
//				FabricBlockEntityTypeBuilder.create(PokedollFurretBlockEntity::new, ModBlocks.POKEDOLL_FURRET),
//				PokedollFurretBlockEntity.class
//		);
//
//		POKEDOLL_SHINY_FURRET_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FURRET),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyFurretBlockEntity::new, ModBlocks.POKEDOLL_SHINY_FURRET),
//				PokedollShinyFurretBlockEntity.class
//		);

//		POKEDOLL_APPLIN_BASKET_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.APPLIN_BASKET),
//				FabricBlockEntityTypeBuilder.create(PokedollApplinBasketBlockEntity::new, ModBlocks.POKEDOLL_APPLIN_BASKET),
//				PokedollApplinBasketBlockEntity.class
//		);
//		POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.SHINY_APPLIN_BASKET),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyApplinBasketBlockEntity::new, ModBlocks.POKEDOLL_SHINY_APPLIN_BASKET),
//				PokedollShinyApplinBasketBlockEntity.class
//		);

//		POKEDOLL_MUNCHLAX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MUNCHLAX),
//				FabricBlockEntityTypeBuilder.create(PokedollMunchlaxBlockEntity::new, ModBlocks.POKEDOLL_MUNCHLAX),
//				PokedollMunchlaxBlockEntity.class
//		);
//		POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MUNCHLAX),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyMunchlaxBlockEntity::new, ModBlocks.POKEDOLL_SHINY_MUNCHLAX),
//				PokedollShinyMunchlaxBlockEntity.class
//		);
//
//		POKEDOLL_RABSCA_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RABSCA),
//				FabricBlockEntityTypeBuilder.create(PokedollRabscaBlockEntity::new, ModBlocks.POKEDOLL_RABSCA),
//				PokedollRabscaBlockEntity.class
//		);
//		POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RABSCA),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyRabscaBlockEntity::new, ModBlocks.POKEDOLL_SHINY_RABSCA),
//				PokedollShinyRabscaBlockEntity.class
//		);
//
//		POKEDOLL_RELLOR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RELLOR),
//				FabricBlockEntityTypeBuilder.create(PokedollRellorBlockEntity::new, ModBlocks.POKEDOLL_RELLOR),
//				PokedollRellorBlockEntity.class
//		);
//		POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RELLOR),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyRellorBlockEntity::new, ModBlocks.POKEDOLL_SHINY_RELLOR),
//				PokedollShinyRellorBlockEntity.class
//		);
//
//		POKEDOLL_WARTORTLE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WARTORTLE),
//				FabricBlockEntityTypeBuilder.create(PokedollWartortleBlockEntity::new, ModBlocks.POKEDOLL_WARTORTLE),
//				PokedollWartortleBlockEntity.class
//		);
//		POKEDOLL_SHINY_WARTORTLE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_WARTORTLE),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyWartortleBlockEntity::new, ModBlocks.POKEDOLL_SHINY_WARTORTLE),
//				PokedollShinyWartortleBlockEntity.class
//		);
//
//		POKEDOLL_SABLEYE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SABLEYE),
//				FabricBlockEntityTypeBuilder.create(PokedollSableyeBlockEntity::new, ModBlocks.POKEDOLL_SABLEYE),
//				PokedollSableyeBlockEntity.class
//		);
//		POKEDOLL_SHINY_SABLEYE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SABLEYE),
//				FabricBlockEntityTypeBuilder.create(PokedollShinySableyeBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SABLEYE),
//				PokedollShinySableyeBlockEntity.class
//		);
//
//		POKEDOLL_ABSOL_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ABSOL),
//				FabricBlockEntityTypeBuilder.create(PokedollAbsolBlockEntity::new, ModBlocks.POKEDOLL_ABSOL),
//				PokedollAbsolBlockEntity.class
//		);
//		POKEDOLL_SHINY_ABSOL_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ABSOL),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyAbsolBlockEntity::new, ModBlocks.POKEDOLL_SHINY_ABSOL),
//				PokedollShinyAbsolBlockEntity.class
//		);
//
//		POKEDOLL_HAPPINY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_HAPPINY),
//				FabricBlockEntityTypeBuilder.create(PokedollHappinyBlockEntity::new, ModBlocks.POKEDOLL_HAPPINY),
//				PokedollHappinyBlockEntity.class
//		);
//		POKEDOLL_SHINY_HAPPINY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_HAPPINY),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyHappinyBlockEntity::new, ModBlocks.POKEDOLL_SHINY_HAPPINY),
//				PokedollShinyHappinyBlockEntity.class
//		);
//
//		POKEDOLL_IVYSAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_IVYSAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollIvysaurBlockEntity::new, ModBlocks.POKEDOLL_IVYSAUR),
//				PokedollIvysaurBlockEntity.class
//		);
//		POKEDOLL_SHINY_IVYSAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_IVYSAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyIvysaurBlockEntity::new, ModBlocks.POKEDOLL_SHINY_IVYSAUR),
//				PokedollShinyIvysaurBlockEntity.class
//		);
//
//		POKEDOLL_VENUSAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_VENUSAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollVenusaurBlockEntity::new, ModBlocks.POKEDOLL_VENUSAUR),
//				PokedollVenusaurBlockEntity.class
//		);
//		POKEDOLL_SHINY_VENUSAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_VENUSAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyVenusaurBlockEntity::new, ModBlocks.POKEDOLL_SHINY_VENUSAUR),
//				PokedollShinyVenusaurBlockEntity.class
//		);

//		POKEDOLL_AIRUHSEA_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.AIRUHSEA_FIGURINE),
//				FabricBlockEntityTypeBuilder.create(PokedollAiruhseaFigurineBlockEntity::new, ModBlocks.POKEDOLL_AIRUHSEA_FIGURINE),
//				PokedollAiruhseaFigurineBlockEntity.class
//		);
//
//		POKEDOLL_DAMORGO_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.DAMORGO_FIGURINE),
//				FabricBlockEntityTypeBuilder.create(PokedollDamorgoFigurineBlockEntity::new, ModBlocks.POKEDOLL_DAMORGO_FIGURINE),
//				PokedollDamorgoFigurineBlockEntity.class
//		);
//
//		POKEDOLL_DONCHEADLE_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.DONCHEADLE_FIGURINE),
//				FabricBlockEntityTypeBuilder.create(PokedollDoncheadleFigurineBlockEntity::new, ModBlocks.POKEDOLL_DONCHEADLE_FIGURINE),
//				PokedollDoncheadleFigurineBlockEntity.class
//		);
//
//		POKEDOLL_MAGIKARP_FISHBOWL_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.MAGIKARP_FISHBOWL),
//				FabricBlockEntityTypeBuilder.create(PokedollMagikarpFishbowlBlockEntity::new, ModBlocks.POKEDOLL_MAGIKARP_FISHBOWL),
//				PokedollMagikarpFishbowlBlockEntity.class
//		);
//		POKEDOLL_SHINY_MAGIKARP_FISHBOWL_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.SHINY_MAGIKARP_FISHBOWL),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyMagikarpFishbowlBlockEntity::new, ModBlocks.POKEDOLL_SHINY_MAGIKARP_FISHBOWL),
//				PokedollShinyMagikarpFishbowlBlockEntity.class
//		);
//
//		POKEDOLL_POKEMON_TROPHY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEMON_TROPHY),
//				FabricBlockEntityTypeBuilder.create(PokedollPokemonTrophyBlockEntity::new, ModBlocks.POKEDOLL_POKEMON_TROPHY),
//				PokedollPokemonTrophyBlockEntity.class
//		);

//		POKEDOLL_BLASTOISE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BLASTOISE),
//				FabricBlockEntityTypeBuilder.create(PokedollBlastoiseBlockEntity::new, ModBlocks.POKEDOLL_BLASTOISE),
//				PokedollBlastoiseBlockEntity.class
//		);
//		POKEDOLL_SHINY_BLASTOISE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BLASTOISE),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyBlastoiseBlockEntity::new, ModBlocks.POKEDOLL_SHINY_BLASTOISE),
//				PokedollShinyBlastoiseBlockEntity.class
//		);
//
//		POKEDOLL_SWINUB_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SWINUB),
//				FabricBlockEntityTypeBuilder.create(PokedollSwinubBlockEntity::new, ModBlocks.POKEDOLL_SWINUB),
//				PokedollSwinubBlockEntity.class
//		);
//		POKEDOLL_SHINY_SWINUB_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SWINUB),
//				FabricBlockEntityTypeBuilder.create(PokedollShinySwinubBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SWINUB),
//				PokedollShinySwinubBlockEntity.class
//		);
//		POKEDOLL_WOOPER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WOOPER),
//				FabricBlockEntityTypeBuilder.create(PokedollWooperBlockEntity::new, ModBlocks.POKEDOLL_WOOPER),
//				PokedollWooperBlockEntity.class
//		);
//		POKEDOLL_SHINY_WOOPER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_WOOPER),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyWooperBlockEntity::new, ModBlocks.POKEDOLL_SHINY_WOOPER),
//				PokedollShinyWooperBlockEntity.class
//		);
//
//		POKEDOLL_QUAGSIRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_QUAGSIRE),
//				FabricBlockEntityTypeBuilder.create(PokedollQuagsireBlockEntity::new, ModBlocks.POKEDOLL_QUAGSIRE),
//				PokedollQuagsireBlockEntity.class
//		);
//		POKEDOLL_SHINY_QUAGSIRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_QUAGSIRE),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyQuagsireBlockEntity::new, ModBlocks.POKEDOLL_SHINY_QUAGSIRE),
//				PokedollShinyQuagsireBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_ABSOL_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ABSOL),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticAbsolBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_ABSOL),
//				PokedollGiganticAbsolBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_ABSOL_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ABSOL),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyAbsolBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_ABSOL),
//				PokedollGiganticShinyAbsolBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_AMPHAROS_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_AMPHAROS),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticAmpharosBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_AMPHAROS),
//				PokedollGiganticAmpharosBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_AMPHAROS),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyAmpharosBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_AMPHAROS),
//				PokedollGiganticShinyAmpharosBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_ARBOLIVA_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ARBOLIVA),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticArbolivaBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_ARBOLIVA),
//				PokedollGiganticArbolivaBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ARBOLIVA),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyArbolivaBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_ARBOLIVA),
//				PokedollGiganticShinyArbolivaBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_BLASTOISE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BLASTOISE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticBlastoiseBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_BLASTOISE),
//				PokedollGiganticBlastoiseBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_BLASTOISE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BLASTOISE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyBlastoiseBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_BLASTOISE),
//				PokedollGiganticShinyBlastoiseBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BULBASAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticBulbasaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_BULBASAUR),
//				PokedollGiganticBulbasaurBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BULBASAUR_POSED),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticBulbasaurPosedBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_BULBASAUR_POSED),
//				PokedollGiganticBulbasaurPosedBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BULBASAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyBulbasaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_BULBASAUR),
//				PokedollGiganticShinyBulbasaurBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyBulbasaurPosedBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED),
//				PokedollGiganticShinyBulbasaurPosedBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_CALYREX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CALYREX),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticCalyrexBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_CALYREX),
//				PokedollGiganticCalyrexBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CALYREX),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyCalyrexBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_CALYREX),
//				PokedollGiganticShinyCalyrexBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CALYREX_ANIMATED),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticCalyrexAnimatedBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_CALYREX_ANIMATED),
//				PokedollGiganticCalyrexAnimatedBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyCalyrexAnimatedBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED),
//				PokedollGiganticShinyCalyrexAnimatedBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CHARMANDER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticCharmanderBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_CHARMANDER),
//				PokedollGiganticCharmanderBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CHARMANDER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyCharmanderBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_CHARMANDER),
//				PokedollGiganticShinyCharmanderBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_DOLLIV_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_DOLLIV),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticDollivBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_DOLLIV),
//				PokedollGiganticDollivBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_DOLLIV),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyDollivBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_DOLLIV),
//				PokedollGiganticShinyDollivBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_FLAAFFY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_FLAAFFY),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticFlaaffyBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_FLAAFFY),
//				PokedollGiganticFlaaffyBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_FLAAFFY),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyFlaaffyBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_FLAAFFY),
//				PokedollGiganticShinyFlaaffyBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_FURRET_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_FURRET),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticFurretBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_FURRET),
//				PokedollGiganticFurretBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_FURRET_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_FURRET),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyFurretBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_FURRET),
//				PokedollGiganticShinyFurretBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_HAPPINY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_HAPPINY),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticHappinyBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_HAPPINY),
//				PokedollGiganticHappinyBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_HAPPINY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_HAPPINY),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyHappinyBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_HAPPINY),
//				PokedollGiganticShinyHappinyBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_IVYSAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_IVYSAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticIvysaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_IVYSAUR),
//				PokedollGiganticIvysaurBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_IVYSAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_IVYSAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyIvysaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_IVYSAUR),
//				PokedollGiganticShinyIvysaurBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_LICKITUNG_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_LICKITUNG),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticLickitungBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_LICKITUNG),
//				PokedollGiganticLickitungBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_LICKITUNG),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyLickitungBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_LICKITUNG),
//				PokedollGiganticShinyLickitungBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_MAREEP_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MAREEP),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticMareepBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_MAREEP),
//				PokedollGiganticMareepBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MAREEP),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyMareepBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_MAREEP),
//				PokedollGiganticShinyMareepBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_MUNCHLAX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MUNCHLAX),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticMunchlaxBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_MUNCHLAX),
//				PokedollGiganticMunchlaxBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MUNCHLAX),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyMunchlaxBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_MUNCHLAX),
//				PokedollGiganticShinyMunchlaxBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_QUAGSIRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_QUAGSIRE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticQuagsireBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_QUAGSIRE),
//				PokedollGiganticQuagsireBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_QUAGSIRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_QUAGSIRE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyQuagsireBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_QUAGSIRE),
//				PokedollGiganticShinyQuagsireBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_RABSCA_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_RABSCA),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticRabscaBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_RABSCA),
//				PokedollGiganticRabscaBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_RABSCA),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyRabscaBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_RABSCA),
//				PokedollGiganticShinyRabscaBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_RELLOR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_RELLOR),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticRellorBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_RELLOR),
//				PokedollGiganticRellorBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_RELLOR),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyRellorBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_RELLOR),
//				PokedollGiganticShinyRellorBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_SABLEYE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SABLEYE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticSableyeBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SABLEYE),
//				PokedollGiganticSableyeBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_SABLEYE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SABLEYE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySableyeBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SABLEYE),
//				PokedollGiganticShinySableyeBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_SENTRET_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SENTRET),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticSentretBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SENTRET),
//				PokedollGiganticSentretBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SENTRET),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySentretBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SENTRET),
//				PokedollGiganticShinySentretBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_SMOLIV_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SMOLIV),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticSmolivBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SMOLIV),
//				PokedollGiganticSmolivBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SMOLIV),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySmolivBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SMOLIV),
//				PokedollGiganticShinySmolivBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_SNORLAX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SNORLAX),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticSnorlaxBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SNORLAX),
//				PokedollGiganticSnorlaxBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SNORLAX),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySnorlaxBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SNORLAX),
//				PokedollGiganticShinySnorlaxBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SQUIRTLE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticSquirtleBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SQUIRTLE),
//				PokedollGiganticSquirtleBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SQUIRTLE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySquirtleBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SQUIRTLE),
//				PokedollGiganticShinySquirtleBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_SWINUB_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SWINUB),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticSwinubBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SWINUB),
//				PokedollGiganticSwinubBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_SWINUB_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SWINUB),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySwinubBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SWINUB),
//				PokedollGiganticShinySwinubBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_VENUSAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_VENUSAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticVenusaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_VENUSAUR),
//				PokedollGiganticVenusaurBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_VENUSAUR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_VENUSAUR),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyVenusaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_VENUSAUR),
//				PokedollGiganticShinyVenusaurBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_WARTORTLE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WARTORTLE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticWartortleBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_WARTORTLE),
//				PokedollGiganticWartortleBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_WARTORTLE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_WARTORTLE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyWartortleBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_WARTORTLE),
//				PokedollGiganticShinyWartortleBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WASHING_MACHINE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticWashingMachineBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_WASHING_MACHINE),
//				PokedollGiganticWashingMachineBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_WOOPER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WOOPER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticWooperBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_WOOPER),
//				PokedollGiganticWooperBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_WOOPER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_WOOPER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyWooperBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_WOOPER),
//				PokedollGiganticShinyWooperBlockEntity.class
//		);
//
//		POKEDOLL_GASTLY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_GASTLY),
//				FabricBlockEntityTypeBuilder.create(PokedollGastlyBlockEntity::new, ModBlocks.POKEDOLL_GASTLY),
//				PokedollGastlyBlockEntity.class
//		);
//		POKEDOLL_SHINY_GASTLY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_GASTLY),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyGastlyBlockEntity::new, ModBlocks.POKEDOLL_SHINY_GASTLY),
//				PokedollShinyGastlyBlockEntity.class
//		);
//
//		POKEDOLL_GENGAR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_GENGAR),
//				FabricBlockEntityTypeBuilder.create(PokedollGengarBlockEntity::new, ModBlocks.POKEDOLL_GENGAR),
//				PokedollGengarBlockEntity.class
//		);
//		POKEDOLL_SHINY_GENGAR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_GENGAR),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyGengarBlockEntity::new, ModBlocks.POKEDOLL_SHINY_GENGAR),
//				PokedollShinyGengarBlockEntity.class
//		);
//
//		POKEDOLL_DRIFLOON_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_DRIFLOON),
//				FabricBlockEntityTypeBuilder.create(PokedollDrifloonBlockEntity::new, ModBlocks.POKEDOLL_DRIFLOON),
//				PokedollDrifloonBlockEntity.class
//		);
//		POKEDOLL_SHINY_DRIFLOON_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_DRIFLOON),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyDrifloonBlockEntity::new, ModBlocks.POKEDOLL_SHINY_DRIFLOON),
//				PokedollShinyDrifloonBlockEntity.class
//		);
//
//		POKEDOLL_ROOKIDEE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ROOKIDEE),
//				FabricBlockEntityTypeBuilder.create(PokedollRookideeBlockEntity::new, ModBlocks.POKEDOLL_ROOKIDEE),
//				PokedollRookideeBlockEntity.class
//		);
//		POKEDOLL_SHINY_ROOKIDEE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ROOKIDEE),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyRookideeBlockEntity::new, ModBlocks.POKEDOLL_SHINY_ROOKIDEE),
//				PokedollShinyRookideeBlockEntity.class
//		);
//
//		POKEDOLL_CORVISQUIRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CORVISQUIRE),
//				FabricBlockEntityTypeBuilder.create(PokedollCorvisquireBlockEntity::new, ModBlocks.POKEDOLL_CORVISQUIRE),
//				PokedollCorvisquireBlockEntity.class
//		);
//		POKEDOLL_SHINY_CORVISQUIRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CORVISQUIRE),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyCorvisquireBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CORVISQUIRE),
//				PokedollShinyCorvisquireBlockEntity.class
//		);
//
//		POKEDOLL_CORVIKNIGHT_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CORVIKNIGHT),
//				FabricBlockEntityTypeBuilder.create(PokedollCorviknightBlockEntity::new, ModBlocks.POKEDOLL_CORVIKNIGHT),
//				PokedollCorviknightBlockEntity.class
//		);
//		POKEDOLL_SHINY_CORVIKNIGHT_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CORVIKNIGHT),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyCorviknightBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CORVIKNIGHT),
//				PokedollShinyCorviknightBlockEntity.class
//		);
//
//		POKEDOLL_STONJOURNER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_STONJOURNER),
//				FabricBlockEntityTypeBuilder.create(PokedollStonjournerBlockEntity::new, ModBlocks.POKEDOLL_STONJOURNER),
//				PokedollStonjournerBlockEntity.class
//		);
//		POKEDOLL_SHINY_STONJOURNER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_STONJOURNER),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyStonjournerBlockEntity::new, ModBlocks.POKEDOLL_SHINY_STONJOURNER),
//				PokedollShinyStonjournerBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_CORVIKNIGHT_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CORVIKNIGHT),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticCorviknightBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_CORVIKNIGHT),
//				PokedollGiganticCorviknightBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyCorviknightBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT),
//				PokedollGiganticShinyCorviknightBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_CORVISQUIRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CORVISQUIRE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticCorvisquireBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_CORVISQUIRE),
//				PokedollGiganticCorvisquireBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyCorvisquireBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE),
//				PokedollGiganticShinyCorvisquireBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_DRIFLOON_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_DRIFLOON),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticDrifloonBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_DRIFLOON),
//				PokedollGiganticDrifloonBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_DRIFLOON_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_DRIFLOON),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyDrifloonBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_DRIFLOON),
//				PokedollGiganticShinyDrifloonBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_GASTLY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_GASTLY),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticGastlyBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_GASTLY),
//				PokedollGiganticGastlyBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_GASTLY_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_GASTLY),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyGastlyBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_GASTLY),
//				PokedollGiganticShinyGastlyBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_GENGAR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_GENGAR),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticGengarBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_GENGAR),
//				PokedollGiganticGengarBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_GENGAR_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_GENGAR),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyGengarBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_GENGAR),
//				PokedollGiganticShinyGengarBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_ROOKIDEE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ROOKIDEE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticRookideeBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_ROOKIDEE),
//				PokedollGiganticRookideeBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_ROOKIDEE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ROOKIDEE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyRookideeBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_ROOKIDEE),
//				PokedollGiganticShinyRookideeBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_STONJOURNER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_STONJOURNER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticStonjournerBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_STONJOURNER),
//				PokedollGiganticStonjournerBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_STONJOURNER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_STONJOURNER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyStonjournerBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_STONJOURNER),
//				PokedollGiganticShinyStonjournerBlockEntity.class
//		);
//
//		POKEDOLL_EEVEE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_EEVEE),
//				FabricBlockEntityTypeBuilder.create(PokedollEeveeBlockEntity::new, ModBlocks.POKEDOLL_EEVEE),
//				PokedollEeveeBlockEntity.class
//		);
//		POKEDOLL_SHINY_EEVEE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_EEVEE),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyEeveeBlockEntity::new, ModBlocks.POKEDOLL_SHINY_EEVEE),
//				PokedollShinyEeveeBlockEntity.class
//		);
//
//		POKEDOLL_SANDYGAST_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SANDYGAST),
//				FabricBlockEntityTypeBuilder.create(PokedollSandygastBlockEntity::new, ModBlocks.POKEDOLL_SANDYGAST),
//				PokedollSandygastBlockEntity.class
//		);
//		POKEDOLL_SHINY_SANDYGAST_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SANDYGAST),
//				FabricBlockEntityTypeBuilder.create(PokedollShinySandygastBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SANDYGAST),
//				PokedollShinySandygastBlockEntity.class
//		);
//
//		POKEDOLL_PALOSSAND_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_PALOSSAND),
//				FabricBlockEntityTypeBuilder.create(PokedollPalossandBlockEntity::new, ModBlocks.POKEDOLL_PALOSSAND),
//				PokedollPalossandBlockEntity.class
//		);
//		POKEDOLL_SHINY_PALOSSAND_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_PALOSSAND),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyPalossandBlockEntity::new, ModBlocks.POKEDOLL_SHINY_PALOSSAND),
//				PokedollShinyPalossandBlockEntity.class
//		);
//
//		POKEDOLL_GHOLDENGO_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_GHOLDENGO),
//				FabricBlockEntityTypeBuilder.create(PokedollGholdengoBlockEntity::new, ModBlocks.POKEDOLL_GHOLDENGO),
//				PokedollGholdengoBlockEntity.class
//		);
//		POKEDOLL_SHINY_GHOLDENGO_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_GHOLDENGO),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyGholdengoBlockEntity::new, ModBlocks.POKEDOLL_SHINY_GHOLDENGO),
//				PokedollShinyGholdengoBlockEntity.class
//		);
//		POKEDOLL_NETHERITE_GHOLDENGO_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_NETHERITE_GHOLDENGO),
//				FabricBlockEntityTypeBuilder.create(PokedollNetheriteGholdengoBlockEntity::new, ModBlocks.POKEDOLL_NETHERITE_GHOLDENGO),
//				PokedollNetheriteGholdengoBlockEntity.class
//		);
//		POKEDOLL_SHINY_NETHERITE_GHOLDENGO_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_NETHERITE_GHOLDENGO),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyNetheriteGholdengoBlockEntity::new, ModBlocks.POKEDOLL_SHINY_NETHERITE_GHOLDENGO),
//				PokedollShinyNetheriteGholdengoBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_EEVEE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_EEVEE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticEeveeBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_EEVEE),
//				PokedollGiganticEeveeBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_EEVEE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_EEVEE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyEeveeBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_EEVEE),
//				PokedollGiganticShinyEeveeBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_GHOLDENGO_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_GHOLDENGO),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticGholdengoBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_GHOLDENGO),
//				PokedollGiganticGholdengoBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_GHOLDENGO_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_GHOLDENGO),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyGholdengoBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_GHOLDENGO),
//				PokedollGiganticShinyGholdengoBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticNetheriteGholdengoBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO),
//				PokedollGiganticNetheriteGholdengoBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_NETHERITE_GHOLDENGO_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_NETHERITE_GHOLDENGO),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyNetheriteGholdengoBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_NETHERITE_GHOLDENGO),
//				PokedollGiganticShinyNetheriteGholdengoBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_PALOSSAND_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_PALOSSAND),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticPalossandBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_PALOSSAND),
//				PokedollGiganticPalossandBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_PALOSSAND_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_PALOSSAND),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyPalossandBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_PALOSSAND),
//				PokedollGiganticShinyPalossandBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_SANDYGAST_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SANDYGAST),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticSandygastBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SANDYGAST),
//				PokedollGiganticSandygastBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_SANDYGAST_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SANDYGAST),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySandygastBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SANDYGAST),
//				PokedollGiganticShinySandygastBlockEntity.class
//		);
//
//		POKEDOLL_SHELLDER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHELLDER),
//				FabricBlockEntityTypeBuilder.create(PokedollShellderBlockEntity::new, ModBlocks.POKEDOLL_SHELLDER),
//				PokedollShellderBlockEntity.class
//		);
//		POKEDOLL_SHINY_SHELLDER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SHELLDER),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyShellderBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SHELLDER),
//				PokedollShinyShellderBlockEntity.class
//		);
//
//		POKEDOLL_CLOYSTER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CLOYSTER),
//				FabricBlockEntityTypeBuilder.create(PokedollCloysterBlockEntity::new, ModBlocks.POKEDOLL_CLOYSTER),
//				PokedollCloysterBlockEntity.class
//		);
//		POKEDOLL_SHINY_CLOYSTER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CLOYSTER),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyCloysterBlockEntity::new, ModBlocks.POKEDOLL_SHINY_CLOYSTER),
//				PokedollShinyCloysterBlockEntity.class
//		);
//
//		POKEDOLL_WAILMER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WAILMER),
//				FabricBlockEntityTypeBuilder.create(PokedollWailmerBlockEntity::new, ModBlocks.POKEDOLL_WAILMER),
//				PokedollWailmerBlockEntity.class
//		);
//		POKEDOLL_SHINY_WAILMER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_WAILMER),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyWailmerBlockEntity::new, ModBlocks.POKEDOLL_SHINY_WAILMER),
//				PokedollShinyWailmerBlockEntity.class
//		);
//
//		POKEDOLL_WAILORD_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WAILORD),
//				FabricBlockEntityTypeBuilder.create(PokedollWailordBlockEntity::new, ModBlocks.POKEDOLL_WAILORD),
//				PokedollWailordBlockEntity.class
//		);
//		POKEDOLL_SHINY_WAILORD_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_WAILORD),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyWailordBlockEntity::new, ModBlocks.POKEDOLL_SHINY_WAILORD),
//				PokedollShinyWailordBlockEntity.class
//		);
//
//		POKEDOLL_TROPIUS_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_TROPIUS),
//				FabricBlockEntityTypeBuilder.create(PokedollTropiusBlockEntity::new, ModBlocks.POKEDOLL_TROPIUS),
//				PokedollTropiusBlockEntity.class
//		);
//		POKEDOLL_SHINY_TROPIUS_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_TROPIUS),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyTropiusBlockEntity::new, ModBlocks.POKEDOLL_SHINY_TROPIUS),
//				PokedollShinyTropiusBlockEntity.class
//		);
//
//		POKEDOLL_KYOGRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_KYOGRE),
//				FabricBlockEntityTypeBuilder.create(PokedollKyogreBlockEntity::new, ModBlocks.POKEDOLL_KYOGRE),
//				PokedollKyogreBlockEntity.class
//		);
//		POKEDOLL_SHINY_KYOGRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_KYOGRE),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyKyogreBlockEntity::new, ModBlocks.POKEDOLL_SHINY_KYOGRE),
//				PokedollShinyKyogreBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_CLOYSTER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CLOYSTER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticCloysterBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_CLOYSTER),
//				PokedollGiganticCloysterBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_CLOYSTER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CLOYSTER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyCloysterBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_CLOYSTER),
//				PokedollGiganticShinyCloysterBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_KYOGRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_KYOGRE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticKyogreBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_KYOGRE),
//				PokedollGiganticKyogreBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_KYOGRE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_KYOGRE),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyKyogreBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_KYOGRE),
//				PokedollGiganticShinyKyogreBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_SHELLDER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHELLDER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShellderBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHELLDER),
//				PokedollGiganticShellderBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_SHELLDER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SHELLDER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyShellderBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SHELLDER),
//				PokedollGiganticShinyShellderBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_TROPIUS_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_TROPIUS),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticTropiusBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_TROPIUS),
//				PokedollGiganticTropiusBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_TROPIUS_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_TROPIUS),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyTropiusBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_TROPIUS),
//				PokedollGiganticShinyTropiusBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_WAILMER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WAILMER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticWailmerBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_WAILMER),
//				PokedollGiganticWailmerBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_WAILMER_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_WAILMER),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyWailmerBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_WAILMER),
//				PokedollGiganticShinyWailmerBlockEntity.class
//		);
//
//		GIGANTIC_POKEDOLL_WAILORD_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WAILORD),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticWailordBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_WAILORD),
//				PokedollGiganticWailordBlockEntity.class
//		);
//		GIGANTIC_POKEDOLL_SHINY_WAILORD_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_WAILORD),
//				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyWailordBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_WAILORD),
//				PokedollGiganticShinyWailordBlockEntity.class
//		);
//
//        GIGANTIC_POKEDOLL_MARSHADOW_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MARSHADOW),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticMarshadowBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_MARSHADOW),
//            PokedollGiganticMarshadowBlockEntity.class
//        );
//        GIGANTIC_POKEDOLL_SHINY_MARSHADOW_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MARSHADOW),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyMarshadowBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_MARSHADOW),
//            PokedollGiganticShinyMarshadowBlockEntity.class
//        );
//        GIGANTIC_POKEDOLL_MARSHADOW_ZENITH_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MARSHADOW_ZENITH),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticMarshadowZenithBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_MARSHADOW_ZENITH),
//            PokedollGiganticMarshadowZenithBlockEntity.class
//        );
//        GIGANTIC_POKEDOLL_SHINY_MARSHADOW_ZENITH_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MARSHADOW_ZENITH),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyMarshadowZenithBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_MARSHADOW_ZENITH),
//            PokedollGiganticShinyMarshadowZenithBlockEntity.class
//        );
//
//        GIGANTIC_POKEDOLL_PHANTUMP_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_PHANTUMP),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticPhantumpBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_PHANTUMP),
//            PokedollGiganticPhantumpBlockEntity.class
//        );
//        GIGANTIC_POKEDOLL_SHINY_PHANTUMP_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_PHANTUMP),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyPhantumpBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_PHANTUMP),
//            PokedollGiganticShinyPhantumpBlockEntity.class
//        );
//
//        GIGANTIC_POKEDOLL_PUMPKABOO_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_PUMPKABOO),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticPumpkabooBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_PUMPKABOO),
//            PokedollGiganticPumpkabooBlockEntity.class
//        );
//        GIGANTIC_POKEDOLL_SHINY_PUMPKABOO_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_PUMPKABOO),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyPumpkabooBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_PUMPKABOO),
//            PokedollGiganticShinyPumpkabooBlockEntity.class
//        );
//
//        GIGANTIC_POKEDOLL_TREVENANT_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_TREVENANT),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticTrevenantBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_TREVENANT),
//            PokedollGiganticTrevenantBlockEntity.class
//        );
//        GIGANTIC_POKEDOLL_SHINY_TREVENANT_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_TREVENANT),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyTrevenantBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_TREVENANT),
//            PokedollGiganticShinyTrevenantBlockEntity.class
//        );
//
//        GIGANTIC_POKEDOLL_BELLOSSOM_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BELLOSSOM),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticBellossomBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_BELLOSSOM),
//            PokedollGiganticBellossomBlockEntity.class
//        );
//        GIGANTIC_POKEDOLL_SHINY_BELLOSSOM_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BELLOSSOM),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyBellossomBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_BELLOSSOM),
//            PokedollGiganticShinyBellossomBlockEntity.class
//        );
//
//        GIGANTIC_POKEDOLL_MIMIKYU_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MIMIKYU),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticMimikyuBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_MIMIKYU),
//            PokedollGiganticMimikyuBlockEntity.class
//        );
//        GIGANTIC_POKEDOLL_SHINY_MIMIKYU_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MIMIKYU),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyMimikyuBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_MIMIKYU),
//            PokedollGiganticShinyMimikyuBlockEntity.class
//        );
//
//        GIGANTIC_POKEDOLL_ROWLET_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ROWLET),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticRowletBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_ROWLET),
//            PokedollGiganticRowletBlockEntity.class
//        );
//        GIGANTIC_POKEDOLL_SHINY_ROWLET_BLOCK_ENTITY = registerBlockEntity(
//            Identifier.of(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ROWLET),
//            FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyRowletBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_ROWLET),
//            PokedollGiganticShinyRowletBlockEntity.class
//        );
//		POKEDOLL_PHANTUMP_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_PHANTUMP),
//				FabricBlockEntityTypeBuilder.create(PokedollPhantumpBlockEntity::new, ModBlocks.POKEDOLL_PHANTUMP),
//				PokedollPhantumpBlockEntity.class
//		);
//		POKEDOLL_SHINY_PHANTUMP_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_PHANTUMP),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyPhantumpBlockEntity::new, ModBlocks.POKEDOLL_SHINY_PHANTUMP),
//				PokedollShinyPhantumpBlockEntity.class
//		);
//
//		POKEDOLL_PUMPKABOO_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_PUMPKABOO),
//				FabricBlockEntityTypeBuilder.create(PokedollPumpkabooBlockEntity::new, ModBlocks.POKEDOLL_PUMPKABOO),
//				PokedollPumpkabooBlockEntity.class
//		);
//		POKEDOLL_SHINY_PUMPKABOO_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_PUMPKABOO),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyPumpkabooBlockEntity::new, ModBlocks.POKEDOLL_SHINY_PUMPKABOO),
//				PokedollShinyPumpkabooBlockEntity.class
//		);
//
//		POKEDOLL_TREVENANT_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_TREVENANT),
//				FabricBlockEntityTypeBuilder.create(PokedollTrevenantBlockEntity::new, ModBlocks.POKEDOLL_TREVENANT),
//				PokedollTrevenantBlockEntity.class
//		);
//		POKEDOLL_SHINY_TREVENANT_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_TREVENANT),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyTrevenantBlockEntity::new, ModBlocks.POKEDOLL_SHINY_TREVENANT),
//				PokedollShinyTrevenantBlockEntity.class
//		);
//
//		POKEDOLL_MARSHADOW_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MARSHADOW),
//				FabricBlockEntityTypeBuilder.create(PokedollMarshadowBlockEntity::new, ModBlocks.POKEDOLL_MARSHADOW),
//				PokedollMarshadowBlockEntity.class
//		);
//		POKEDOLL_SHINY_MARSHADOW_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MARSHADOW),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyMarshadowBlockEntity::new, ModBlocks.POKEDOLL_SHINY_MARSHADOW),
//				PokedollShinyMarshadowBlockEntity.class
//		);
//
//		POKEDOLL_MARSHADOW_ZENITH_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MARSHADOW_ZENITH),
//				FabricBlockEntityTypeBuilder.create(PokedollMarshadowZenithBlockEntity::new, ModBlocks.POKEDOLL_MARSHADOW_ZENITH),
//				PokedollMarshadowZenithBlockEntity.class
//		);
//		POKEDOLL_SHINY_MARSHADOW_ZENITH_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MARSHADOW_ZENITH),
//				FabricBlockEntityTypeBuilder.create(PokedollShinyMarshadowZenithBlockEntity::new, ModBlocks.POKEDOLL_SHINY_MARSHADOW_ZENITH),
//				PokedollShinyMarshadowZenithBlockEntity.class
//		);
//
//        POKEDOLL_BELLOSSOM_BLOCK_ENTITY = registerBlockEntity(
//                Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BELLOSSOM),
//                FabricBlockEntityTypeBuilder.create(PokedollBellossomBlockEntity::new, ModBlocks.POKEDOLL_BELLOSSOM),
//                PokedollBellossomBlockEntity.class
//        );
//        POKEDOLL_SHINY_BELLOSSOM_BLOCK_ENTITY = registerBlockEntity(
//                Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BELLOSSOM),
//                FabricBlockEntityTypeBuilder.create(PokedollShinyBellossomBlockEntity::new, ModBlocks.POKEDOLL_SHINY_BELLOSSOM),
//                PokedollShinyBellossomBlockEntity.class
//        );
//
//        POKEDOLL_ROWLET_BLOCK_ENTITY = registerBlockEntity(
//                Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ROWLET),
//                FabricBlockEntityTypeBuilder.create(PokedollRowletBlockEntity::new, ModBlocks.POKEDOLL_ROWLET),
//                PokedollRowletBlockEntity.class
//        );
//        POKEDOLL_SHINY_ROWLET_BLOCK_ENTITY = registerBlockEntity(
//                Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ROWLET),
//                FabricBlockEntityTypeBuilder.create(PokedollShinyRowletBlockEntity::new, ModBlocks.POKEDOLL_SHINY_ROWLET),
//                PokedollShinyRowletBlockEntity.class
//        );
//
//        POKEDOLL_MIMIKYU_BLOCK_ENTITY = registerBlockEntity(
//                Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MIMIKYU),
//                FabricBlockEntityTypeBuilder.create(PokedollMimikyuBlockEntity::new, ModBlocks.POKEDOLL_MIMIKYU),
//                PokedollMimikyuBlockEntity.class
//        );
//        POKEDOLL_SHINY_MIMIKYU_BLOCK_ENTITY = registerBlockEntity(
//                Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MIMIKYU),
//                FabricBlockEntityTypeBuilder.create(PokedollShinyMimikyuBlockEntity::new, ModBlocks.POKEDOLL_SHINY_MIMIKYU),
//                PokedollShinyMimikyuBlockEntity.class
//        );
//		A09ROBERT_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.A09ROBERT_FIGURINE),
//				FabricBlockEntityTypeBuilder.create(A09RobertFigurineBlockEntity::new, ModBlocks.A09ROBERT_FIGURINE),
//				A09RobertFigurineBlockEntity.class
//		);
//		RED_COMMUNISM_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.RED_COMMUNISM_FIGURINE),
//				FabricBlockEntityTypeBuilder.create(RedCommunismFigurineBlockEntity::new, ModBlocks.RED_COMMUNISM_FIGURINE),
//				RedCommunismFigurineBlockEntity.class
//		);
//		TROPSIC0_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
//				Identifier.of(Pokeblocks.MOD_ID, PokeIDs.TROPSIC0_FIGURINE),
//				FabricBlockEntityTypeBuilder.create(Tropsic0FigurineBlockEntity::new, ModBlocks.TROPSIC0_FIGURINE),
//				Tropsic0FigurineBlockEntity.class
//		);
//
//        FIGURINE_MIK_03_BLOCK_ENTITY = registerBlockEntity(
//                Identifier.of(Pokeblocks.MOD_ID, PokeIDs.MIK_03_FIGURINE),
//                FabricBlockEntityTypeBuilder.create(PokedollMik03FigurineBlockEntity::new, ModBlocks.FIGURINE_MIK_03),
//                PokedollMik03FigurineBlockEntity.class
//        );
//
//        FIGURINE_POHELLO_BLOCK_ENTITY = registerBlockEntity(
//                Identifier.of(Pokeblocks.MOD_ID, PokeIDs.POHELLO_FIGURINE),
//                FabricBlockEntityTypeBuilder.create(PokedollPohelloFigurineBlockEntity::new, ModBlocks.FIGURINE_POHELLO),
//                PokedollPohelloFigurineBlockEntity.class
//        );

	}

	private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(Identifier id, FabricBlockEntityTypeBuilder<T> builder, Class<T> blockEntityClass) {
		BlockEntityType<T> blockEntityType = Registry.register(Registries.BLOCK_ENTITY_TYPE, id, builder.build());
		BlockEntityTypeRegistry.register(blockEntityClass, blockEntityType);
		return blockEntityType;
	}

}
