package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.block.entity.a09robert_figurine.A09RobertFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.absol.PokedollAbsolBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.absol.PokedollGiganticAbsolBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.absol.PokedollGiganticShinyAbsolBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.absol.PokedollShinyAbsolBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.airuhsea_figurine.PokedollAiruhseaFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ampharos.PokedollAmpharosBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ampharos.PokedollGiganticAmpharosBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ampharos.PokedollGiganticShinyAmpharosBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ampharos.PokedollShinyAmpharosBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.applin_basket.PokedollApplinBasketBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.applin_basket.PokedollShinyApplinBasketBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.arboliva.PokedollArbolivaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.arboliva.PokedollGiganticArbolivaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.arboliva.PokedollGiganticShinyArbolivaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.arboliva.PokedollShinyArbolivaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.blastoise.PokedollBlastoiseBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.blastoise.PokedollGiganticBlastoiseBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.blastoise.PokedollGiganticShinyBlastoiseBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.blastoise.PokedollShinyBlastoiseBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollBulbasaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollGiganticBulbasaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollGiganticShinyBulbasaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.PokedollShinyBulbasaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollBulbasaurPosedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollGiganticBulbasaurPosedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollGiganticShinyBulbasaurPosedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bulbasaur.posed.PokedollShinyBulbasaurPosedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollGiganticCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollGiganticShinyCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.PokedollShinyCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollCalyrexAnimatedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollGiganticCalyrexAnimatedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollGiganticShinyCalyrexAnimatedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.calyrex.animated.PokedollShinyCalyrexAnimatedBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollGiganticCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollGiganticShinyCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollShinyCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.damorgo_figurine.PokedollDamorgoFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.dolliv.PokedollDollivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.dolliv.PokedollGiganticDollivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.dolliv.PokedollGiganticShinyDollivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.dolliv.PokedollShinyDollivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.doncheadle_figurine.PokedollDoncheadleFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.flaaffy.PokedollFlaaffyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.flaaffy.PokedollGiganticFlaaffyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.flaaffy.PokedollGiganticShinyFlaaffyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.flaaffy.PokedollShinyFlaaffyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.furret.PokedollFurretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.furret.PokedollGiganticFurretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.furret.PokedollGiganticShinyFurretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.furret.PokedollShinyFurretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.happiny.PokedollGiganticHappinyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.happiny.PokedollGiganticShinyHappinyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.happiny.PokedollHappinyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.happiny.PokedollShinyHappinyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ivysaur.PokedollGiganticIvysaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ivysaur.PokedollGiganticShinyIvysaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ivysaur.PokedollIvysaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ivysaur.PokedollShinyIvysaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollGiganticLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollGiganticShinyLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollShinyLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.magikarp_fishbowl.PokedollMagikarpFishbowlBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.magikarp_fishbowl.PokedollShinyMagikarpFishbowlBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollGiganticMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollGiganticShinyMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollShinyMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.munchlax.PokedollGiganticMunchlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.munchlax.PokedollGiganticShinyMunchlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.munchlax.PokedollMunchlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.munchlax.PokedollShinyMunchlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.pokemon_trophy.PokedollPokemonTrophyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.quagsire.PokedollGiganticQuagsireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.quagsire.PokedollGiganticShinyQuagsireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.quagsire.PokedollQuagsireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.quagsire.PokedollShinyQuagsireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rabsca.PokedollGiganticRabscaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rabsca.PokedollGiganticShinyRabscaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rabsca.PokedollRabscaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rabsca.PokedollShinyRabscaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.red_communism_figurine.RedCommunismFigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rellor.PokedollGiganticRellorBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rellor.PokedollGiganticShinyRellorBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rellor.PokedollRellorBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rellor.PokedollShinyRellorBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sableye.PokedollGiganticSableyeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sableye.PokedollGiganticShinySableyeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sableye.PokedollSableyeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sableye.PokedollShinySableyeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sentret.PokedollGiganticSentretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sentret.PokedollGiganticShinySentretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sentret.PokedollSentretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sentret.PokedollShinySentretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.smoliv.PokedollGiganticShinySmolivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.smoliv.PokedollGiganticSmolivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.smoliv.PokedollShinySmolivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.smoliv.PokedollSmolivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollGiganticShinySnorlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollGiganticSnorlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollShinySnorlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollSnorlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollGiganticShinySquirtleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollGiganticSquirtleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollShinySquirtleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollSquirtleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.swinub.PokedollGiganticShinySwinubBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.swinub.PokedollGiganticSwinubBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.swinub.PokedollShinySwinubBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.swinub.PokedollSwinubBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.venusaur.PokedollGiganticShinyVenusaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.venusaur.PokedollGiganticVenusaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.venusaur.PokedollShinyVenusaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.venusaur.PokedollVenusaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wartortle.PokedollGiganticShinyWartortleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wartortle.PokedollGiganticWartortleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wartortle.PokedollShinyWartortleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wartortle.PokedollWartortleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.washing_machine.PokedollGiganticWashingMachineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.washing_machine.PokedollWashingMachineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wooper.PokedollGiganticShinyWooperBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wooper.PokedollGiganticWooperBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wooper.PokedollShinyWooperBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wooper.PokedollWooperBlockEntity;
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
	public static BlockEntityType<PokedollWartortleBlockEntity> POKEDOLL_WARTORTLE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyWartortleBlockEntity> POKEDOLL_SHINY_WARTORTLE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollSableyeBlockEntity> POKEDOLL_SABLEYE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinySableyeBlockEntity> POKEDOLL_SHINY_SABLEYE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollAbsolBlockEntity> POKEDOLL_ABSOL_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyAbsolBlockEntity> POKEDOLL_SHINY_ABSOL_BLOCK_ENTITY;
	public static BlockEntityType<PokedollHappinyBlockEntity> POKEDOLL_HAPPINY_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyHappinyBlockEntity> POKEDOLL_SHINY_HAPPINY_BLOCK_ENTITY;
	public static BlockEntityType<PokedollIvysaurBlockEntity> POKEDOLL_IVYSAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyIvysaurBlockEntity> POKEDOLL_SHINY_IVYSAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollVenusaurBlockEntity> POKEDOLL_VENUSAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyVenusaurBlockEntity> POKEDOLL_SHINY_VENUSAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollAiruhseaFigurineBlockEntity> POKEDOLL_AIRUHSEA_FIGURINE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollDamorgoFigurineBlockEntity> POKEDOLL_DAMORGO_FIGURINE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollDoncheadleFigurineBlockEntity> POKEDOLL_DONCHEADLE_FIGURINE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollMagikarpFishbowlBlockEntity> POKEDOLL_MAGIKARP_FISHBOWL_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyMagikarpFishbowlBlockEntity> POKEDOLL_SHINY_MAGIKARP_FISHBOWL_BLOCK_ENTITY;
	public static BlockEntityType<PokedollPokemonTrophyBlockEntity> POKEDOLL_POKEMON_TROPHY_BLOCK_ENTITY;
	public static BlockEntityType<PokedollBlastoiseBlockEntity> POKEDOLL_BLASTOISE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyBlastoiseBlockEntity> POKEDOLL_SHINY_BLASTOISE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollSwinubBlockEntity> POKEDOLL_SWINUB_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinySwinubBlockEntity> POKEDOLL_SHINY_SWINUB_BLOCK_ENTITY;
	public static BlockEntityType<PokedollWooperBlockEntity> POKEDOLL_WOOPER_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyWooperBlockEntity> POKEDOLL_SHINY_WOOPER_BLOCK_ENTITY;
	public static BlockEntityType<PokedollQuagsireBlockEntity> POKEDOLL_QUAGSIRE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollShinyQuagsireBlockEntity> POKEDOLL_SHINY_QUAGSIRE_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticAbsolBlockEntity> GIGANTIC_POKEDOLL_ABSOL_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyAbsolBlockEntity> GIGANTIC_POKEDOLL_SHINY_ABSOL_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticAmpharosBlockEntity> GIGANTIC_POKEDOLL_AMPHAROS_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyAmpharosBlockEntity> GIGANTIC_POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticArbolivaBlockEntity> GIGANTIC_POKEDOLL_ARBOLIVA_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyArbolivaBlockEntity> GIGANTIC_POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticBlastoiseBlockEntity> GIGANTIC_POKEDOLL_BLASTOISE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyBlastoiseBlockEntity> GIGANTIC_POKEDOLL_SHINY_BLASTOISE_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticBulbasaurBlockEntity> GIGANTIC_POKEDOLL_BULBASAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticBulbasaurPosedBlockEntity> GIGANTIC_POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyBulbasaurBlockEntity> GIGANTIC_POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyBulbasaurPosedBlockEntity> GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticCalyrexBlockEntity> GIGANTIC_POKEDOLL_CALYREX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticCalyrexAnimatedBlockEntity> GIGANTIC_POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyCalyrexBlockEntity> GIGANTIC_POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyCalyrexAnimatedBlockEntity> GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticCharmanderBlockEntity> GIGANTIC_POKEDOLL_CHARMANDER_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyCharmanderBlockEntity> GIGANTIC_POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticDollivBlockEntity> GIGANTIC_POKEDOLL_DOLLIV_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyDollivBlockEntity> GIGANTIC_POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticFlaaffyBlockEntity> GIGANTIC_POKEDOLL_FLAAFFY_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyFlaaffyBlockEntity> GIGANTIC_POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticFurretBlockEntity> GIGANTIC_POKEDOLL_FURRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyFurretBlockEntity> GIGANTIC_POKEDOLL_SHINY_FURRET_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticHappinyBlockEntity> GIGANTIC_POKEDOLL_HAPPINY_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyHappinyBlockEntity> GIGANTIC_POKEDOLL_SHINY_HAPPINY_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticIvysaurBlockEntity> GIGANTIC_POKEDOLL_IVYSAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyIvysaurBlockEntity> GIGANTIC_POKEDOLL_SHINY_IVYSAUR_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticLickitungBlockEntity> GIGANTIC_POKEDOLL_LICKITUNG_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyLickitungBlockEntity> GIGANTIC_POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticMareepBlockEntity> GIGANTIC_POKEDOLL_MAREEP_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyMareepBlockEntity> GIGANTIC_POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticMunchlaxBlockEntity> GIGANTIC_POKEDOLL_MUNCHLAX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyMunchlaxBlockEntity> GIGANTIC_POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticQuagsireBlockEntity> GIGANTIC_POKEDOLL_QUAGSIRE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyQuagsireBlockEntity> GIGANTIC_POKEDOLL_SHINY_QUAGSIRE_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticRabscaBlockEntity> GIGANTIC_POKEDOLL_RABSCA_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyRabscaBlockEntity> GIGANTIC_POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticRellorBlockEntity> GIGANTIC_POKEDOLL_RELLOR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyRellorBlockEntity> GIGANTIC_POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticSableyeBlockEntity> GIGANTIC_POKEDOLL_SABLEYE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinySableyeBlockEntity> GIGANTIC_POKEDOLL_SHINY_SABLEYE_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticSentretBlockEntity> GIGANTIC_POKEDOLL_SENTRET_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinySentretBlockEntity> GIGANTIC_POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticSmolivBlockEntity> GIGANTIC_POKEDOLL_SMOLIV_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinySmolivBlockEntity> GIGANTIC_POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticSnorlaxBlockEntity> GIGANTIC_POKEDOLL_SNORLAX_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinySnorlaxBlockEntity> GIGANTIC_POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticSquirtleBlockEntity> GIGANTIC_POKEDOLL_SQUIRTLE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinySquirtleBlockEntity> GIGANTIC_POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticSwinubBlockEntity> GIGANTIC_POKEDOLL_SWINUB_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinySwinubBlockEntity> GIGANTIC_POKEDOLL_SHINY_SWINUB_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticVenusaurBlockEntity> GIGANTIC_POKEDOLL_VENUSAUR_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyVenusaurBlockEntity> GIGANTIC_POKEDOLL_SHINY_VENUSAUR_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticWartortleBlockEntity> GIGANTIC_POKEDOLL_WARTORTLE_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyWartortleBlockEntity> GIGANTIC_POKEDOLL_SHINY_WARTORTLE_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticWashingMachineBlockEntity> GIGANTIC_POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY;

	public static BlockEntityType<PokedollGiganticWooperBlockEntity> GIGANTIC_POKEDOLL_WOOPER_BLOCK_ENTITY;
	public static BlockEntityType<PokedollGiganticShinyWooperBlockEntity> GIGANTIC_POKEDOLL_SHINY_WOOPER_BLOCK_ENTITY;
	public static BlockEntityType<A09RobertFigurineBlockEntity> A09ROBERT_FIGURINE_BLOCK_ENTITY;
	public static BlockEntityType<RedCommunismFigurineBlockEntity> RED_COMMUNISM_FIGURINE_BLOCK_ENTITY;

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
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.APPLIN_BASKET),
				FabricBlockEntityTypeBuilder.create(PokedollApplinBasketBlockEntity::new, ModBlocks.POKEDOLL_APPLIN_BASKET),
				PokedollApplinBasketBlockEntity.class
		);
		POKEDOLL_SHINY_APPLIN_BASKET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.SHINY_APPLIN_BASKET),
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

		POKEDOLL_WARTORTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WARTORTLE),
				FabricBlockEntityTypeBuilder.create(PokedollWartortleBlockEntity::new, ModBlocks.POKEDOLL_WARTORTLE),
				PokedollWartortleBlockEntity.class
		);
		POKEDOLL_SHINY_WARTORTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_WARTORTLE),
				FabricBlockEntityTypeBuilder.create(PokedollShinyWartortleBlockEntity::new, ModBlocks.POKEDOLL_SHINY_WARTORTLE),
				PokedollShinyWartortleBlockEntity.class
		);

		POKEDOLL_SABLEYE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SABLEYE),
				FabricBlockEntityTypeBuilder.create(PokedollSableyeBlockEntity::new, ModBlocks.POKEDOLL_SABLEYE),
				PokedollSableyeBlockEntity.class
		);
		POKEDOLL_SHINY_SABLEYE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SABLEYE),
				FabricBlockEntityTypeBuilder.create(PokedollShinySableyeBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SABLEYE),
				PokedollShinySableyeBlockEntity.class
		);

		POKEDOLL_ABSOL_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ABSOL),
				FabricBlockEntityTypeBuilder.create(PokedollAbsolBlockEntity::new, ModBlocks.POKEDOLL_ABSOL),
				PokedollAbsolBlockEntity.class
		);
		POKEDOLL_SHINY_ABSOL_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ABSOL),
				FabricBlockEntityTypeBuilder.create(PokedollShinyAbsolBlockEntity::new, ModBlocks.POKEDOLL_SHINY_ABSOL),
				PokedollShinyAbsolBlockEntity.class
		);

		POKEDOLL_HAPPINY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_HAPPINY),
				FabricBlockEntityTypeBuilder.create(PokedollHappinyBlockEntity::new, ModBlocks.POKEDOLL_HAPPINY),
				PokedollHappinyBlockEntity.class
		);
		POKEDOLL_SHINY_HAPPINY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_HAPPINY),
				FabricBlockEntityTypeBuilder.create(PokedollShinyHappinyBlockEntity::new, ModBlocks.POKEDOLL_SHINY_HAPPINY),
				PokedollShinyHappinyBlockEntity.class
		);

		POKEDOLL_IVYSAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_IVYSAUR),
				FabricBlockEntityTypeBuilder.create(PokedollIvysaurBlockEntity::new, ModBlocks.POKEDOLL_IVYSAUR),
				PokedollIvysaurBlockEntity.class
		);
		POKEDOLL_SHINY_IVYSAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_IVYSAUR),
				FabricBlockEntityTypeBuilder.create(PokedollShinyIvysaurBlockEntity::new, ModBlocks.POKEDOLL_SHINY_IVYSAUR),
				PokedollShinyIvysaurBlockEntity.class
		);

		POKEDOLL_VENUSAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_VENUSAUR),
				FabricBlockEntityTypeBuilder.create(PokedollVenusaurBlockEntity::new, ModBlocks.POKEDOLL_VENUSAUR),
				PokedollVenusaurBlockEntity.class
		);
		POKEDOLL_SHINY_VENUSAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_VENUSAUR),
				FabricBlockEntityTypeBuilder.create(PokedollShinyVenusaurBlockEntity::new, ModBlocks.POKEDOLL_SHINY_VENUSAUR),
				PokedollShinyVenusaurBlockEntity.class
		);

		POKEDOLL_AIRUHSEA_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.AIRUHSEA_FIGURINE),
				FabricBlockEntityTypeBuilder.create(PokedollAiruhseaFigurineBlockEntity::new, ModBlocks.POKEDOLL_AIRUHSEA_FIGURINE),
				PokedollAiruhseaFigurineBlockEntity.class
		);

		POKEDOLL_DAMORGO_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.DAMORGO_FIGURINE),
				FabricBlockEntityTypeBuilder.create(PokedollDamorgoFigurineBlockEntity::new, ModBlocks.POKEDOLL_DAMORGO_FIGURINE),
				PokedollDamorgoFigurineBlockEntity.class
		);

		POKEDOLL_DONCHEADLE_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.DONCHEADLE_FIGURINE),
				FabricBlockEntityTypeBuilder.create(PokedollDoncheadleFigurineBlockEntity::new, ModBlocks.POKEDOLL_DONCHEADLE_FIGURINE),
				PokedollDoncheadleFigurineBlockEntity.class
		);

		POKEDOLL_MAGIKARP_FISHBOWL_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.MAGIKARP_FISHBOWL),
				FabricBlockEntityTypeBuilder.create(PokedollMagikarpFishbowlBlockEntity::new, ModBlocks.POKEDOLL_MAGIKARP_FISHBOWL),
				PokedollMagikarpFishbowlBlockEntity.class
		);
		POKEDOLL_SHINY_MAGIKARP_FISHBOWL_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.SHINY_MAGIKARP_FISHBOWL),
				FabricBlockEntityTypeBuilder.create(PokedollShinyMagikarpFishbowlBlockEntity::new, ModBlocks.POKEDOLL_SHINY_MAGIKARP_FISHBOWL),
				PokedollShinyMagikarpFishbowlBlockEntity.class
		);

		POKEDOLL_POKEMON_TROPHY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEMON_TROPHY),
				FabricBlockEntityTypeBuilder.create(PokedollPokemonTrophyBlockEntity::new, ModBlocks.POKEDOLL_POKEMON_TROPHY),
				PokedollPokemonTrophyBlockEntity.class
		);

		POKEDOLL_BLASTOISE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BLASTOISE),
				FabricBlockEntityTypeBuilder.create(PokedollBlastoiseBlockEntity::new, ModBlocks.POKEDOLL_BLASTOISE),
				PokedollBlastoiseBlockEntity.class
		);
		POKEDOLL_SHINY_BLASTOISE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BLASTOISE),
				FabricBlockEntityTypeBuilder.create(PokedollShinyBlastoiseBlockEntity::new, ModBlocks.POKEDOLL_SHINY_BLASTOISE),
				PokedollShinyBlastoiseBlockEntity.class
		);

		POKEDOLL_SWINUB_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SWINUB),
				FabricBlockEntityTypeBuilder.create(PokedollSwinubBlockEntity::new, ModBlocks.POKEDOLL_SWINUB),
				PokedollSwinubBlockEntity.class
		);
		POKEDOLL_SHINY_SWINUB_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SWINUB),
				FabricBlockEntityTypeBuilder.create(PokedollShinySwinubBlockEntity::new, ModBlocks.POKEDOLL_SHINY_SWINUB),
				PokedollShinySwinubBlockEntity.class
		);
		POKEDOLL_WOOPER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WOOPER),
				FabricBlockEntityTypeBuilder.create(PokedollWooperBlockEntity::new, ModBlocks.POKEDOLL_WOOPER),
				PokedollWooperBlockEntity.class
		);
		POKEDOLL_SHINY_WOOPER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_WOOPER),
				FabricBlockEntityTypeBuilder.create(PokedollShinyWooperBlockEntity::new, ModBlocks.POKEDOLL_SHINY_WOOPER),
				PokedollShinyWooperBlockEntity.class
		);

		POKEDOLL_QUAGSIRE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_QUAGSIRE),
				FabricBlockEntityTypeBuilder.create(PokedollQuagsireBlockEntity::new, ModBlocks.POKEDOLL_QUAGSIRE),
				PokedollQuagsireBlockEntity.class
		);
		POKEDOLL_SHINY_QUAGSIRE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_QUAGSIRE),
				FabricBlockEntityTypeBuilder.create(PokedollShinyQuagsireBlockEntity::new, ModBlocks.POKEDOLL_SHINY_QUAGSIRE),
				PokedollShinyQuagsireBlockEntity.class
		);

		GIGANTIC_POKEDOLL_ABSOL_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ABSOL),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticAbsolBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_ABSOL),
				PokedollGiganticAbsolBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_ABSOL_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ABSOL),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyAbsolBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_ABSOL),
				PokedollGiganticShinyAbsolBlockEntity.class
		);

		GIGANTIC_POKEDOLL_AMPHAROS_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_AMPHAROS),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticAmpharosBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_AMPHAROS),
				PokedollGiganticAmpharosBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_AMPHAROS_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_AMPHAROS),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyAmpharosBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_AMPHAROS),
				PokedollGiganticShinyAmpharosBlockEntity.class
		);

		GIGANTIC_POKEDOLL_ARBOLIVA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ARBOLIVA),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticArbolivaBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_ARBOLIVA),
				PokedollGiganticArbolivaBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_ARBOLIVA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ARBOLIVA),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyArbolivaBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_ARBOLIVA),
				PokedollGiganticShinyArbolivaBlockEntity.class
		);

		GIGANTIC_POKEDOLL_BLASTOISE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BLASTOISE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticBlastoiseBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_BLASTOISE),
				PokedollGiganticBlastoiseBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_BLASTOISE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BLASTOISE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyBlastoiseBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_BLASTOISE),
				PokedollGiganticShinyBlastoiseBlockEntity.class
		);

		GIGANTIC_POKEDOLL_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BULBASAUR),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticBulbasaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_BULBASAUR),
				PokedollGiganticBulbasaurBlockEntity.class
		);
		GIGANTIC_POKEDOLL_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BULBASAUR_POSED),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticBulbasaurPosedBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_BULBASAUR_POSED),
				PokedollGiganticBulbasaurPosedBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_BULBASAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BULBASAUR),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyBulbasaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_BULBASAUR),
				PokedollGiganticShinyBulbasaurBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyBulbasaurPosedBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED),
				PokedollGiganticShinyBulbasaurPosedBlockEntity.class
		);

		GIGANTIC_POKEDOLL_CALYREX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CALYREX),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticCalyrexBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_CALYREX),
				PokedollGiganticCalyrexBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_CALYREX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CALYREX),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyCalyrexBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_CALYREX),
				PokedollGiganticShinyCalyrexBlockEntity.class
		);
		GIGANTIC_POKEDOLL_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CALYREX_ANIMATED),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticCalyrexAnimatedBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_CALYREX_ANIMATED),
				PokedollGiganticCalyrexAnimatedBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyCalyrexAnimatedBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED),
				PokedollGiganticShinyCalyrexAnimatedBlockEntity.class
		);

		GIGANTIC_POKEDOLL_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CHARMANDER),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticCharmanderBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_CHARMANDER),
				PokedollGiganticCharmanderBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_CHARMANDER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CHARMANDER),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyCharmanderBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_CHARMANDER),
				PokedollGiganticShinyCharmanderBlockEntity.class
		);

		GIGANTIC_POKEDOLL_DOLLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_DOLLIV),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticDollivBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_DOLLIV),
				PokedollGiganticDollivBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_DOLLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_DOLLIV),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyDollivBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_DOLLIV),
				PokedollGiganticShinyDollivBlockEntity.class
		);

		GIGANTIC_POKEDOLL_FLAAFFY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_FLAAFFY),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticFlaaffyBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_FLAAFFY),
				PokedollGiganticFlaaffyBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_FLAAFFY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_FLAAFFY),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyFlaaffyBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_FLAAFFY),
				PokedollGiganticShinyFlaaffyBlockEntity.class
		);

		GIGANTIC_POKEDOLL_FURRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_FURRET),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticFurretBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_FURRET),
				PokedollGiganticFurretBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_FURRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_FURRET),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyFurretBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_FURRET),
				PokedollGiganticShinyFurretBlockEntity.class
		);

		GIGANTIC_POKEDOLL_HAPPINY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_HAPPINY),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticHappinyBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_HAPPINY),
				PokedollGiganticHappinyBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_HAPPINY_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_HAPPINY),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyHappinyBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_HAPPINY),
				PokedollGiganticShinyHappinyBlockEntity.class
		);

		GIGANTIC_POKEDOLL_IVYSAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_IVYSAUR),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticIvysaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_IVYSAUR),
				PokedollGiganticIvysaurBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_IVYSAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_IVYSAUR),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyIvysaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_IVYSAUR),
				PokedollGiganticShinyIvysaurBlockEntity.class
		);

		GIGANTIC_POKEDOLL_LICKITUNG_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_LICKITUNG),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticLickitungBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_LICKITUNG),
				PokedollGiganticLickitungBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_LICKITUNG_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_LICKITUNG),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyLickitungBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_LICKITUNG),
				PokedollGiganticShinyLickitungBlockEntity.class
		);

		GIGANTIC_POKEDOLL_MAREEP_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MAREEP),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticMareepBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_MAREEP),
				PokedollGiganticMareepBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_MAREEP_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MAREEP),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyMareepBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_MAREEP),
				PokedollGiganticShinyMareepBlockEntity.class
		);

		GIGANTIC_POKEDOLL_MUNCHLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MUNCHLAX),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticMunchlaxBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_MUNCHLAX),
				PokedollGiganticMunchlaxBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_MUNCHLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MUNCHLAX),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyMunchlaxBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_MUNCHLAX),
				PokedollGiganticShinyMunchlaxBlockEntity.class
		);

		GIGANTIC_POKEDOLL_QUAGSIRE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_QUAGSIRE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticQuagsireBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_QUAGSIRE),
				PokedollGiganticQuagsireBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_QUAGSIRE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_QUAGSIRE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyQuagsireBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_QUAGSIRE),
				PokedollGiganticShinyQuagsireBlockEntity.class
		);

		GIGANTIC_POKEDOLL_RABSCA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_RABSCA),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticRabscaBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_RABSCA),
				PokedollGiganticRabscaBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_RABSCA_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_RABSCA),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyRabscaBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_RABSCA),
				PokedollGiganticShinyRabscaBlockEntity.class
		);

		GIGANTIC_POKEDOLL_RELLOR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_RELLOR),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticRellorBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_RELLOR),
				PokedollGiganticRellorBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_RELLOR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_RELLOR),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyRellorBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_RELLOR),
				PokedollGiganticShinyRellorBlockEntity.class
		);

		GIGANTIC_POKEDOLL_SABLEYE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SABLEYE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticSableyeBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SABLEYE),
				PokedollGiganticSableyeBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_SABLEYE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SABLEYE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySableyeBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SABLEYE),
				PokedollGiganticShinySableyeBlockEntity.class
		);

		GIGANTIC_POKEDOLL_SENTRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SENTRET),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticSentretBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SENTRET),
				PokedollGiganticSentretBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_SENTRET_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SENTRET),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySentretBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SENTRET),
				PokedollGiganticShinySentretBlockEntity.class
		);

		GIGANTIC_POKEDOLL_SMOLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SMOLIV),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticSmolivBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SMOLIV),
				PokedollGiganticSmolivBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_SMOLIV_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SMOLIV),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySmolivBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SMOLIV),
				PokedollGiganticShinySmolivBlockEntity.class
		);

		GIGANTIC_POKEDOLL_SNORLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SNORLAX),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticSnorlaxBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SNORLAX),
				PokedollGiganticSnorlaxBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_SNORLAX_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SNORLAX),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySnorlaxBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SNORLAX),
				PokedollGiganticShinySnorlaxBlockEntity.class
		);

		GIGANTIC_POKEDOLL_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SQUIRTLE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticSquirtleBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SQUIRTLE),
				PokedollGiganticSquirtleBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_SQUIRTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SQUIRTLE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySquirtleBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SQUIRTLE),
				PokedollGiganticShinySquirtleBlockEntity.class
		);

		GIGANTIC_POKEDOLL_SWINUB_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SWINUB),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticSwinubBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SWINUB),
				PokedollGiganticSwinubBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_SWINUB_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SWINUB),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinySwinubBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_SWINUB),
				PokedollGiganticShinySwinubBlockEntity.class
		);

		GIGANTIC_POKEDOLL_VENUSAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_VENUSAUR),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticVenusaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_VENUSAUR),
				PokedollGiganticVenusaurBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_VENUSAUR_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_VENUSAUR),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyVenusaurBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_VENUSAUR),
				PokedollGiganticShinyVenusaurBlockEntity.class
		);

		GIGANTIC_POKEDOLL_WARTORTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WARTORTLE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticWartortleBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_WARTORTLE),
				PokedollGiganticWartortleBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_WARTORTLE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_WARTORTLE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyWartortleBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_WARTORTLE),
				PokedollGiganticShinyWartortleBlockEntity.class
		);

		GIGANTIC_POKEDOLL_WASHING_MACHINE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WASHING_MACHINE),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticWashingMachineBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_WASHING_MACHINE),
				PokedollGiganticWashingMachineBlockEntity.class
		);

		GIGANTIC_POKEDOLL_WOOPER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WOOPER),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticWooperBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_WOOPER),
				PokedollGiganticWooperBlockEntity.class
		);
		GIGANTIC_POKEDOLL_SHINY_WOOPER_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_WOOPER),
				FabricBlockEntityTypeBuilder.create(PokedollGiganticShinyWooperBlockEntity::new, ModBlocks.GIGANTIC_POKEDOLL_SHINY_WOOPER),
				PokedollGiganticShinyWooperBlockEntity.class
		);
		A09ROBERT_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.A09ROBERT_FIGURINE),
				FabricBlockEntityTypeBuilder.create(A09RobertFigurineBlockEntity::new, ModBlocks.A09ROBERT_FIGURINE),
				A09RobertFigurineBlockEntity.class
		);
		RED_COMMUNISM_FIGURINE_BLOCK_ENTITY = registerBlockEntity(
				new Identifier(Pokeblocks.MOD_ID, PokeIDs.RED_COMMUNISM_FIGURINE),
				FabricBlockEntityTypeBuilder.create(RedCommunismFigurineBlockEntity::new, ModBlocks.RED_COMMUNISM_FIGURINE),
				RedCommunismFigurineBlockEntity.class
		);

	}

	private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(Identifier id, FabricBlockEntityTypeBuilder<T> builder, Class<T> blockEntityClass) {
		BlockEntityType<T> blockEntityType = Registry.register(Registries.BLOCK_ENTITY_TYPE, id, builder.build());
		BlockEntityTypeRegistry.register(blockEntityClass, blockEntityType);
		return blockEntityType;
	}

}
