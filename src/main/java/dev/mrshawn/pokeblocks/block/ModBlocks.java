package dev.mrshawn.pokeblocks.block;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.block.custom.SittablePokedollBlock;
import dev.mrshawn.pokeblocks.block.entity.SkibidiMewlet.PokedollGiganticSkibidiMewletBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.SkibidiMewlet.PokedollSkibidiMewletBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.absol.PokedollAbsolBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.absol.PokedollGiganticAbsolBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.absol.PokedollGiganticShinyAbsolBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.absol.PokedollShinyAbsolBlockEntity;
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
import dev.mrshawn.pokeblocks.block.entity.beartic.PokedollBearticBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.beartic.PokedollGiganticBearticBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.beartic.PokedollGiganticShinyBearticBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.beartic.PokedollShinyBearticBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bellossom.PokedollBellossomBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bellossom.PokedollGiganticBellossomBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bellossom.PokedollGiganticShinyBellossomBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.bellossom.PokedollShinyBellossomBlockEntity;
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
import dev.mrshawn.pokeblocks.block.entity.cetoddle.PokedollCetoddleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.cetoddle.PokedollGiganticCetoddleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.cetoddle.PokedollGiganticShinyCetoddleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.cetoddle.PokedollShinyCetoddleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollGiganticCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollGiganticShinyCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.charmander.PokedollShinyCharmanderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.cloyster.PokedollCloysterBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.cloyster.PokedollGiganticCloysterBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.cloyster.PokedollGiganticShinyCloysterBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.cloyster.PokedollShinyCloysterBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.corviknight.PokedollCorviknightBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.corviknight.PokedollGiganticCorviknightBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.corviknight.PokedollGiganticShinyCorviknightBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.corviknight.PokedollShinyCorviknightBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.corvisquire.PokedollCorvisquireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.corvisquire.PokedollGiganticCorvisquireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.corvisquire.PokedollGiganticShinyCorvisquireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.corvisquire.PokedollShinyCorvisquireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.cubchoo.*;
import dev.mrshawn.pokeblocks.block.entity.delibird.PokedollDelibirdBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.delibird.PokedollGiganticDelibirdBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.delibird.PokedollGiganticShinyDelibirdBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.delibird.PokedollShinyDelibirdBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.dolliv.PokedollDollivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.dolliv.PokedollGiganticDollivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.dolliv.PokedollGiganticShinyDollivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.dolliv.PokedollShinyDollivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.drifloon.PokedollDrifloonBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.drifloon.PokedollGiganticDrifloonBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.drifloon.PokedollGiganticShinyDrifloonBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.drifloon.PokedollShinyDrifloonBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.eevee.PokedollEeveeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.eevee.PokedollGiganticEeveeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.eevee.PokedollGiganticShinyEeveeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.eevee.PokedollShinyEeveeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.eiscue.PokedollEiscueBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.eiscue.PokedollGiganticEiscueBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.eiscue.PokedollGiganticShinyEiscueBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.eiscue.PokedollShinyEiscueBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.figurines.*;
import dev.mrshawn.pokeblocks.block.entity.flaaffy.PokedollFlaaffyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.flaaffy.PokedollGiganticFlaaffyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.flaaffy.PokedollGiganticShinyFlaaffyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.flaaffy.PokedollShinyFlaaffyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.frigibax.PokedollFrigibaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.frigibax.PokedollGiganticFrigibaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.frigibax.PokedollGiganticShinyFrigibaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.frigibax.PokedollShinyFrigibaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.froslass.PokedollFroslassBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.froslass.PokedollGiganticFroslassBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.froslass.PokedollGiganticShinyFroslassBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.froslass.PokedollShinyFroslassBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.furret.PokedollFurretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.furret.PokedollGiganticFurretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.furret.PokedollGiganticShinyFurretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.furret.PokedollShinyFurretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.gastly.PokedollGastlyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.gastly.PokedollGiganticGastlyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.gastly.PokedollGiganticShinyGastlyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.gastly.PokedollShinyGastlyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.gengar.PokedollGengarBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.gengar.PokedollGiganticGengarBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.gengar.PokedollGiganticShinyGengarBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.gengar.PokedollShinyGengarBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.gholdengo.*;
import dev.mrshawn.pokeblocks.block.entity.glalie.PokedollGiganticGlalieBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.glalie.PokedollGiganticShinyGlalieBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.glalie.PokedollGlalieBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.glalie.PokedollShinyGlalieBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.happiny.PokedollGiganticHappinyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.happiny.PokedollGiganticShinyHappinyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.happiny.PokedollHappinyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.happiny.PokedollShinyHappinyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.headpile.EiscueHeadpileBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.headpile.EiscueShinyHeadpileBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.headpile.PokedollGiganticHeadpileBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.headpile.PokedollGiganticShinyHeadpileBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ivysaur.PokedollGiganticIvysaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ivysaur.PokedollGiganticShinyIvysaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ivysaur.PokedollIvysaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.ivysaur.PokedollShinyIvysaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.kyogre.PokedollGiganticKyogreBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.kyogre.PokedollGiganticShinyKyogreBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.kyogre.PokedollKyogreBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.kyogre.PokedollShinyKyogreBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollGiganticLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollGiganticShinyLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.lickitung.PokedollShinyLickitungBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.luvdisc.*;
import dev.mrshawn.pokeblocks.block.entity.magikarp_fishbowl.PokedollMagikarpFishbowlBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.magikarp_fishbowl.PokedollShinyMagikarpFishbowlBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollGiganticMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollGiganticShinyMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mareep.PokedollShinyMareepBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.marshadow.*;
import dev.mrshawn.pokeblocks.block.entity.mimikyu.PokedollGiganticMimikyuBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mimikyu.PokedollGiganticShinyMimikyuBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mimikyu.PokedollMimikyuBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.mimikyu.PokedollShinyMimikyuBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.munchlax.PokedollGiganticMunchlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.munchlax.PokedollGiganticShinyMunchlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.munchlax.PokedollMunchlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.munchlax.PokedollShinyMunchlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.noice.PokedollGiganticNoiceBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.noice.PokedollGiganticShinyNoiceBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.noice.PokedollNoiceBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.noice.PokedollShinyNoiceBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.palossand.PokedollGiganticPalossandBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.palossand.PokedollGiganticShinyPalossandBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.palossand.PokedollPalossandBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.palossand.PokedollShinyPalossandBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.phantump.PokedollGiganticPhantumpBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.phantump.PokedollGiganticShinyPhantumpBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.phantump.PokedollPhantumpBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.phantump.PokedollShinyPhantumpBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.piloswine.PokedollGiganticPiloswineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.piloswine.PokedollGiganticShinyPiloswineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.piloswine.PokedollPiloswineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.piloswine.PokedollShinyPiloswineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.pokemon_trophy.PokedollPokemonTrophyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.pumpkaboo.PokedollGiganticPumpkabooBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.pumpkaboo.PokedollGiganticShinyPumpkabooBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.pumpkaboo.PokedollPumpkabooBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.pumpkaboo.PokedollShinyPumpkabooBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.quagsire.PokedollGiganticQuagsireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.quagsire.PokedollGiganticShinyQuagsireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.quagsire.PokedollQuagsireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.quagsire.PokedollShinyQuagsireBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rabsca.PokedollGiganticRabscaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rabsca.PokedollGiganticShinyRabscaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rabsca.PokedollRabscaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rabsca.PokedollShinyRabscaBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rellor.PokedollGiganticRellorBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rellor.PokedollGiganticShinyRellorBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rellor.PokedollRellorBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rellor.PokedollShinyRellorBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.riolu.PokedollGiganticRioluBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.riolu.PokedollGiganticShinyRioluBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.riolu.PokedollRioluBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.riolu.PokedollShinyRioluBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rookidee.PokedollGiganticRookideeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rookidee.PokedollGiganticShinyRookideeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rookidee.PokedollRookideeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rookidee.PokedollShinyRookideeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rowlet.PokedollGiganticRowletBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rowlet.PokedollGiganticShinyRowletBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rowlet.PokedollRowletBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.rowlet.PokedollShinyRowletBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sableye.PokedollGiganticSableyeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sableye.PokedollGiganticShinySableyeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sableye.PokedollSableyeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sableye.PokedollShinySableyeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sandygast.PokedollGiganticSandygastBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sandygast.PokedollGiganticShinySandygastBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sandygast.PokedollSandygastBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sandygast.PokedollShinySandygastBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sentret.PokedollGiganticSentretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sentret.PokedollGiganticShinySentretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sentret.PokedollSentretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.sentret.PokedollShinySentretBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.shellder.PokedollGiganticShellderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.shellder.PokedollGiganticShinyShellderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.shellder.PokedollShellderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.shellder.PokedollShinyShellderBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.smoliv.PokedollGiganticShinySmolivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.smoliv.PokedollGiganticSmolivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.smoliv.PokedollShinySmolivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.smoliv.PokedollSmolivBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollGiganticShinySnorlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollGiganticSnorlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollShinySnorlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorlax.PokedollSnorlaxBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorunt.PokedollGiganticShinySnoruntBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorunt.PokedollGiganticSnoruntBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorunt.PokedollShinySnoruntBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorunt.PokedollSnoruntBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorunt.snorunt_family.PokedollGiganticShinySnoruntFamilyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorunt.snorunt_family.PokedollGiganticSnoruntFamilyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorunt.snorunt_family.PokedollShinySnoruntFamilyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.snorunt.snorunt_family.PokedollSnoruntFamilyBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.spheal.PokedollGiganticShinySphealBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.spheal.PokedollGiganticSphealBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.spheal.PokedollShinySphealBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.spheal.PokedollSphealBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollGiganticShinySquirtleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollGiganticSquirtleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollShinySquirtleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.squirtle.PokedollSquirtleBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.stonjourner.PokedollGiganticShinyStonjournerBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.stonjourner.PokedollGiganticStonjournerBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.stonjourner.PokedollShinyStonjournerBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.stonjourner.PokedollStonjournerBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.swinub.PokedollGiganticShinySwinubBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.swinub.PokedollGiganticSwinubBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.swinub.PokedollShinySwinubBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.swinub.PokedollSwinubBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.treecko.PokedollGiganticShinyTreeckoBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.treecko.PokedollGiganticTreeckoBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.treecko.PokedollShinyTreeckoBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.treecko.PokedollTreeckoBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.trevenant.PokedollGiganticShinyTrevenantBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.trevenant.PokedollGiganticTrevenantBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.trevenant.PokedollShinyTrevenantBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.trevenant.PokedollTrevenantBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.tropius.PokedollGiganticShinyTropiusBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.tropius.PokedollGiganticTropiusBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.tropius.PokedollShinyTropiusBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.tropius.PokedollTropiusBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.venusaur.PokedollGiganticShinyVenusaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.venusaur.PokedollGiganticVenusaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.venusaur.PokedollShinyVenusaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.venusaur.PokedollVenusaurBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wailmer.PokedollGiganticShinyWailmerBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wailmer.PokedollGiganticWailmerBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wailmer.PokedollShinyWailmerBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wailmer.PokedollWailmerBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wailord.PokedollGiganticShinyWailordBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wailord.PokedollGiganticWailordBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wailord.PokedollShinyWailordBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.wailord.PokedollWailordBlockEntity;
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
import dev.mrshawn.pokeblocks.constants.Shapes;
import dev.mrshawn.pokeblocks.item.ModItems;
import dev.mrshawn.pokeblocks.utils.ServerHandler;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModBlocks {

	public static final Block POKEDOLL_CALYREX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX),
			new PokedollBlock<>(() -> PokedollCalyrexBlockEntity.class));
	public static final Block POKEDOLL_SHINY_CALYREX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX),
			new PokedollBlock<>(() -> PokedollShinyCalyrexBlockEntity.class));
	public static final Block POKEDOLL_CALYREX_ANIMATED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CALYREX_ANIMATED),
			new PokedollBlock<>(() -> PokedollCalyrexAnimatedBlockEntity.class));
	public static final Block POKEDOLL_SHINY_CALYREX_ANIMATED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CALYREX_ANIMATED),
			new PokedollBlock<>(() -> PokedollShinyCalyrexAnimatedBlockEntity.class));
	public static final Block POKEDOLL_BULBASAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR),
			new PokedollBlock<>(() -> PokedollBulbasaurBlockEntity.class));
	public static final Block POKEDOLL_SHINY_BULBASAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR),
			new PokedollBlock<>(() -> PokedollShinyBulbasaurBlockEntity.class));
	public static final Block POKEDOLL_BULBASAUR_POSED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BULBASAUR_POSED),
			new PokedollBlock<>(() -> PokedollBulbasaurPosedBlockEntity.class));
	public static final Block POKEDOLL_SHINY_BULBASAUR_POSED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BULBASAUR_POSED),
			new PokedollBlock<>(() -> PokedollShinyBulbasaurPosedBlockEntity.class));
	public static final Block POKEDOLL_SQUIRTLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SQUIRTLE),
			new PokedollBlock<>(() -> PokedollSquirtleBlockEntity.class));
	public static final Block POKEDOLL_SHINY_SQUIRTLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SQUIRTLE),
			new PokedollBlock<>(() -> PokedollShinySquirtleBlockEntity.class));
	public static final Block POKEDOLL_CHARMANDER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CHARMANDER),
			new PokedollBlock<>(() -> PokedollCharmanderBlockEntity.class));
	public static final Block POKEDOLL_SHINY_CHARMANDER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CHARMANDER),
			new PokedollBlock<>(() -> PokedollShinyCharmanderBlockEntity.class));
	public static final Block POKEDOLL_LICKITUNG = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_LICKITUNG),
			new PokedollBlock<>(() -> PokedollLickitungBlockEntity.class));
	public static final Block POKEDOLL_SHINY_LICKITUNG = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_LICKITUNG),
			new PokedollBlock<>(() -> PokedollShinyLickitungBlockEntity.class));
	public static final Block POKEDOLL_MAREEP = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MAREEP),
			new PokedollBlock<>(() -> PokedollMareepBlockEntity.class));
	public static final Block POKEDOLL_SHINY_MAREEP = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MAREEP),
			new PokedollBlock<>(() -> PokedollShinyMareepBlockEntity.class));
	public static final Block POKEDOLL_FLAAFFY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FLAAFFY),
			new PokedollBlock<>(() -> PokedollFlaaffyBlockEntity.class));
	public static final Block POKEDOLL_SHINY_FLAAFFY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FLAAFFY),
			new PokedollBlock<>(() -> PokedollShinyFlaaffyBlockEntity.class));
	public static final Block POKEDOLL_SMOLIV = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SMOLIV),
			new PokedollBlock<>(() -> PokedollSmolivBlockEntity.class));
	public static final Block POKEDOLL_SHINY_SMOLIV = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SMOLIV),
			new PokedollBlock<>(() -> PokedollShinySmolivBlockEntity.class));
	public static final Block POKEDOLL_DOLLIV = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_DOLLIV),
			new PokedollBlock<>(() -> PokedollDollivBlockEntity.class));
	public static final Block POKEDOLL_SHINY_DOLLIV = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_DOLLIV),
			new PokedollBlock<>(() -> PokedollShinyDollivBlockEntity.class));
	public static final Block POKEDOLL_ARBOLIVA = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ARBOLIVA),
			new PokedollBlock<>(() -> PokedollArbolivaBlockEntity.class));
	public static final Block POKEDOLL_SHINY_ARBOLIVA = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ARBOLIVA),
			new PokedollBlock<>(() -> PokedollShinyArbolivaBlockEntity.class));
	public static final Block POKEDOLL_WASHING_MACHINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WASHING_MACHINE),
			new PokedollBlock<>(() -> PokedollWashingMachineBlockEntity.class) {
				@Override
				public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
					world.playSound(player, pos, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 1f, 1f);

					if (world.random.nextDouble() < 0.5) {
						Direction facing = state.get(Properties.HORIZONTAL_FACING);
						double x = pos.getX() + 0.5 + facing.getOffsetX() * 0.5;
						double y = pos.getY() + 0.25;
						double z = pos.getZ() + 0.5 + facing.getOffsetZ() * 0.5;

						double velocity = 0.5;
						double dx = facing.getOffsetX() * velocity;
						double dy = 0;
						double dz = facing.getOffsetZ() * velocity;

						world.addParticle(ParticleTypes.CLOUD, x, y, z, dx, dy, dz);

						// Check for collision with players
						Box particleBox = new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5);
						for (PlayerEntity playerEntity : world.getPlayers()) {
							if (playerEntity.getBoundingBox().intersects(particleBox)) {
								// Broadcast the message to the entire server
								ServerHandler.broadcast(playerEntity.getDisplayName().getString() + " got washed!", Formatting.GOLD);
							}
						}
					}

					return ActionResult.SUCCESS;
				}
			});
	public static final Block POKEDOLL_SNORLAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SNORLAX),
			new PokedollBlock<>(() -> PokedollSnorlaxBlockEntity.class));
	public static final Block POKEDOLL_SHINY_SNORLAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SNORLAX),
			new PokedollBlock<>(() -> PokedollShinySnorlaxBlockEntity.class));
	public static final Block POKEDOLL_AMPHAROS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_AMPHAROS),
			new PokedollBlock<>(() -> PokedollAmpharosBlockEntity.class));
	public static final Block POKEDOLL_SHINY_AMPHAROS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_AMPHAROS),
			new PokedollBlock<>(() -> PokedollShinyAmpharosBlockEntity.class));
	public static final Block POKEDOLL_SENTRET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SENTRET),
			new PokedollBlock<>(() -> PokedollSentretBlockEntity.class));
	public static final Block POKEDOLL_SHINY_SENTRET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SENTRET),
			new PokedollBlock<>(() -> PokedollShinySentretBlockEntity.class));
	public static final Block POKEDOLL_FURRET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FURRET),
			new PokedollBlock<>(() -> PokedollFurretBlockEntity.class));
	public static final Block POKEDOLL_SHINY_FURRET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FURRET),
			new PokedollBlock<>(() -> PokedollShinyFurretBlockEntity.class));
	public static final Block POKEDOLL_APPLIN_BASKET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.APPLIN_BASKET),
			new PokedollBlock<>(Blocks.OAK_PLANKS, null, () -> PokedollApplinBasketBlockEntity.class));
	public static final Block POKEDOLL_SHINY_APPLIN_BASKET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.SHINY_APPLIN_BASKET),
			new PokedollBlock<>(Blocks.OAK_PLANKS, null, () -> PokedollShinyApplinBasketBlockEntity.class));
	public static final Block POKEDOLL_MUNCHLAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MUNCHLAX),
			new PokedollBlock<>(() -> PokedollMunchlaxBlockEntity.class));
	public static final Block POKEDOLL_SHINY_MUNCHLAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MUNCHLAX),
			new PokedollBlock<>(() -> PokedollShinyMunchlaxBlockEntity.class));
	public static final Block POKEDOLL_RABSCA = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RABSCA),
			new PokedollBlock<>(() -> PokedollRabscaBlockEntity.class));
	public static final Block POKEDOLL_SHINY_RABSCA = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RABSCA),
			new PokedollBlock<>(() -> PokedollShinyRabscaBlockEntity.class));
	public static final Block POKEDOLL_RELLOR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RELLOR),
			new PokedollBlock<>(() -> PokedollRellorBlockEntity.class));
	public static final Block POKEDOLL_SHINY_RELLOR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RELLOR),
			new PokedollBlock<>(() -> PokedollShinyRellorBlockEntity.class));
	public static final Block POKEDOLL_WARTORTLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WARTORTLE),
			new PokedollBlock<>(() -> PokedollWartortleBlockEntity.class));
	public static final Block POKEDOLL_SHINY_WARTORTLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_WARTORTLE),
			new PokedollBlock<>(() -> PokedollShinyWartortleBlockEntity.class));
	public static final Block POKEDOLL_SABLEYE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SABLEYE),
			new PokedollBlock<>(() -> PokedollSableyeBlockEntity.class));
	public static final Block POKEDOLL_SHINY_SABLEYE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SABLEYE),
			new PokedollBlock<>(() -> PokedollShinySableyeBlockEntity.class));
	public static final Block POKEDOLL_ABSOL = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ABSOL),
			new PokedollBlock<>(() -> PokedollAbsolBlockEntity.class));
	public static final Block POKEDOLL_SHINY_ABSOL = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ABSOL),
			new PokedollBlock<>(() -> PokedollShinyAbsolBlockEntity.class));
	public static final Block POKEDOLL_HAPPINY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_HAPPINY),
			new PokedollBlock<>(() -> PokedollHappinyBlockEntity.class));
	public static final Block POKEDOLL_SHINY_HAPPINY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_HAPPINY),
			new PokedollBlock<>(() -> PokedollShinyHappinyBlockEntity.class));
	public static final Block POKEDOLL_IVYSAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_IVYSAUR),
			new PokedollBlock<>(() -> PokedollIvysaurBlockEntity.class));
	public static final Block POKEDOLL_SHINY_IVYSAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_IVYSAUR),
			new PokedollBlock<>(() -> PokedollShinyIvysaurBlockEntity.class));
	public static final Block POKEDOLL_VENUSAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_VENUSAUR),
			new PokedollBlock<>(() -> PokedollVenusaurBlockEntity.class));
	public static final Block POKEDOLL_SHINY_VENUSAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_VENUSAUR),
			new PokedollBlock<>(() -> PokedollShinyVenusaurBlockEntity.class));
	public static final Block POKEDOLL_MAGIKARP_FISHBOWL = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.MAGIKARP_FISHBOWL),
			new PokedollBlock<>(Blocks.GLASS, () -> PokedollMagikarpFishbowlBlockEntity.class) {
//				@Override
//				public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
//					world.setBlockState(pos, Blocks.WATER.getDefaultState(), 8);
//					if (world instanceof ServerWorld serverWorld) {
//						SalmonEntity salmon = EntityType.SALMON.create(serverWorld);
//						if (salmon != null) {
//							salmon.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
//							serverWorld.spawnEntity(salmon);
//						}
//					}
//				}
			}
	);
	public static final Block POKEDOLL_SHINY_MAGIKARP_FISHBOWL = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.SHINY_MAGIKARP_FISHBOWL),
			new PokedollBlock<>(Blocks.GLASS, () -> PokedollShinyMagikarpFishbowlBlockEntity.class) {
//				@Override
//				public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
//					world.setBlockState(pos, Blocks.WATER.getDefaultState(), 8);
//					if (world instanceof ServerWorld serverWorld) {
//						SalmonEntity salmon = EntityType.SALMON.create(serverWorld);
//						if (salmon != null) {
//							salmon.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
//							serverWorld.spawnEntity(salmon);
//						}
//					}
//				}
			});
	public static final Block POKEDOLL_POKEMON_TROPHY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEMON_TROPHY),
			new PokedollBlock<>(Shapes.TROPHY_SHAPE, () -> PokedollPokemonTrophyBlockEntity.class));
	public static final Block POKEDOLL_BLASTOISE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BLASTOISE),
			new PokedollBlock<>(() -> PokedollBlastoiseBlockEntity.class));
	public static final Block POKEDOLL_SHINY_BLASTOISE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BLASTOISE),
			new PokedollBlock<>(() -> PokedollShinyBlastoiseBlockEntity.class));
	public static final Block POKEDOLL_SWINUB = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SWINUB),
			new PokedollBlock<>(() -> PokedollSwinubBlockEntity.class));
	public static final Block POKEDOLL_SHINY_SWINUB = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SWINUB),
			new PokedollBlock<>(() -> PokedollShinySwinubBlockEntity.class));
	public static final Block POKEDOLL_AIRUHSEA_FIGURINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.AIRUHSEA_FIGURINE),
			new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> PokedollAiruhseaFigurineBlockEntity.class));
	public static final Block POKEDOLL_DAMORGO_FIGURINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.DAMORGO_FIGURINE),
			new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> PokedollDamorgoFigurineBlockEntity.class));
	public static final Block POKEDOLL_DONCHEADLE_FIGURINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.DONCHEADLE_FIGURINE),
			new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> PokedollDoncheadleFigurineBlockEntity.class));
	public static final Block POKEDOLL_WOOPER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WOOPER),
			new PokedollBlock<>(() -> PokedollWooperBlockEntity.class));
	public static final Block POKEDOLL_SHINY_WOOPER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_WOOPER),
			new PokedollBlock<>(() -> PokedollShinyWooperBlockEntity.class));
	public static final Block POKEDOLL_QUAGSIRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_QUAGSIRE),
			new PokedollBlock<>(() -> PokedollQuagsireBlockEntity.class));
	public static final Block POKEDOLL_SHINY_QUAGSIRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_QUAGSIRE),
			new PokedollBlock<>(() -> PokedollShinyQuagsireBlockEntity.class));

	public static final Block POKEDOLL_GASTLY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_GASTLY),
			new PokedollBlock<>(() -> PokedollGastlyBlockEntity.class));
	public static final Block POKEDOLL_SHINY_GASTLY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_GASTLY),
			new PokedollBlock<>(() -> PokedollShinyGastlyBlockEntity.class));

	public static final Block POKEDOLL_GENGAR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_GENGAR),
			new PokedollBlock<>(() -> PokedollGengarBlockEntity.class));
	public static final Block POKEDOLL_SHINY_GENGAR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_GENGAR),
			new PokedollBlock<>(() -> PokedollShinyGengarBlockEntity.class));

	public static final Block POKEDOLL_DRIFLOON = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_DRIFLOON),
			new PokedollBlock<>(() -> PokedollDrifloonBlockEntity.class));
	public static final Block POKEDOLL_SHINY_DRIFLOON = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_DRIFLOON),
			new PokedollBlock<>(() -> PokedollShinyDrifloonBlockEntity.class));

	public static final Block POKEDOLL_ROOKIDEE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ROOKIDEE),
			new PokedollBlock<>(() -> PokedollRookideeBlockEntity.class));
	public static final Block POKEDOLL_SHINY_ROOKIDEE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ROOKIDEE),
			new PokedollBlock<>(() -> PokedollShinyRookideeBlockEntity.class));

	public static final Block POKEDOLL_CORVISQUIRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CORVISQUIRE),
			new PokedollBlock<>(() -> PokedollCorvisquireBlockEntity.class));
	public static final Block POKEDOLL_SHINY_CORVISQUIRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CORVISQUIRE),
			new PokedollBlock<>(() -> PokedollShinyCorvisquireBlockEntity.class));

	public static final Block POKEDOLL_CORVIKNIGHT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CORVIKNIGHT),
			new PokedollBlock<>(() -> PokedollCorviknightBlockEntity.class));
	public static final Block POKEDOLL_SHINY_CORVIKNIGHT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CORVIKNIGHT),
			new PokedollBlock<>(() -> PokedollShinyCorviknightBlockEntity.class));

	public static final Block POKEDOLL_STONJOURNER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_STONJOURNER),
			new PokedollBlock<>(() -> PokedollStonjournerBlockEntity.class));
	public static final Block POKEDOLL_SHINY_STONJOURNER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_STONJOURNER),
			new PokedollBlock<>(() -> PokedollShinyStonjournerBlockEntity.class));

	public static final Block POKEDOLL_EEVEE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_EEVEE),
			new PokedollBlock<>(() -> PokedollEeveeBlockEntity.class));
	public static final Block POKEDOLL_SHINY_EEVEE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_EEVEE),
			new PokedollBlock<>(() -> PokedollShinyEeveeBlockEntity.class));

	public static final Block POKEDOLL_SANDYGAST = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SANDYGAST),
			new PokedollBlock<>(() -> PokedollSandygastBlockEntity.class));
	public static final Block POKEDOLL_SHINY_SANDYGAST = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SANDYGAST),
			new PokedollBlock<>(() -> PokedollShinySandygastBlockEntity.class));

	public static final Block POKEDOLL_PALOSSAND = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_PALOSSAND),
			new PokedollBlock<>(() -> PokedollPalossandBlockEntity.class));
	public static final Block POKEDOLL_SHINY_PALOSSAND = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_PALOSSAND),
			new PokedollBlock<>(() -> PokedollShinyPalossandBlockEntity.class));

	public static final Block POKEDOLL_GHOLDENGO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_GHOLDENGO),
			new PokedollBlock<>(() -> PokedollGholdengoBlockEntity.class));
	public static final Block POKEDOLL_SHINY_GHOLDENGO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_GHOLDENGO),
			new PokedollBlock<>(() -> PokedollShinyGholdengoBlockEntity.class));
	public static final Block POKEDOLL_NETHERITE_GHOLDENGO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_NETHERITE_GHOLDENGO),
			new PokedollBlock<>(() -> PokedollNetheriteGholdengoBlockEntity.class));
	public static final Block POKEDOLL_SHINY_NETHERITE_GHOLDENGO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_NETHERITE_GHOLDENGO),
			new PokedollBlock<>(() -> PokedollShinyNetheriteGholdengoBlockEntity.class));

	public static final Block POKEDOLL_SHELLDER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHELLDER),
			new PokedollBlock<>(() -> PokedollShellderBlockEntity.class));
	public static final Block POKEDOLL_SHINY_SHELLDER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SHELLDER),
			new PokedollBlock<>(() -> PokedollShinyShellderBlockEntity.class));

	public static final Block POKEDOLL_CLOYSTER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CLOYSTER),
			new PokedollBlock<>(() -> PokedollCloysterBlockEntity.class));
	public static final Block POKEDOLL_SHINY_CLOYSTER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CLOYSTER),
			new PokedollBlock<>(() -> PokedollShinyCloysterBlockEntity.class));

	public static final Block POKEDOLL_WAILMER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WAILMER),
			new PokedollBlock<>(() -> PokedollWailmerBlockEntity.class));
	public static final Block POKEDOLL_SHINY_WAILMER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_WAILMER),
			new PokedollBlock<>(() -> PokedollShinyWailmerBlockEntity.class));

	public static final Block POKEDOLL_WAILORD = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_WAILORD),
			new PokedollBlock<>(() -> PokedollWailordBlockEntity.class));
	public static final Block POKEDOLL_SHINY_WAILORD = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_WAILORD),
			new PokedollBlock<>(() -> PokedollShinyWailordBlockEntity.class));

	public static final Block POKEDOLL_TROPIUS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_TROPIUS),
			new PokedollBlock<>(() -> PokedollTropiusBlockEntity.class));
	public static final Block POKEDOLL_SHINY_TROPIUS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_TROPIUS),
			new PokedollBlock<>(() -> PokedollShinyTropiusBlockEntity.class));

	public static final Block POKEDOLL_KYOGRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_KYOGRE),
			new PokedollBlock<>(() -> PokedollKyogreBlockEntity.class));
	public static final Block POKEDOLL_SHINY_KYOGRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_KYOGRE),
			new PokedollBlock<>(() -> PokedollShinyKyogreBlockEntity.class));

	public static final Block POKEDOLL_PHANTUMP = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_PHANTUMP),
			new PokedollBlock<>(() -> PokedollPhantumpBlockEntity.class));
	public static final Block POKEDOLL_SHINY_PHANTUMP = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_PHANTUMP),
			new PokedollBlock<>(() -> PokedollShinyPhantumpBlockEntity.class));

	public static final Block POKEDOLL_PUMPKABOO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_PUMPKABOO),
			new PokedollBlock<>(() -> PokedollPumpkabooBlockEntity.class));
	public static final Block POKEDOLL_SHINY_PUMPKABOO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_PUMPKABOO),
			new PokedollBlock<>(() -> PokedollShinyPumpkabooBlockEntity.class));

	public static final Block POKEDOLL_TREVENANT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_TREVENANT),
			new PokedollBlock<>(() -> PokedollTrevenantBlockEntity.class));
	public static final Block POKEDOLL_SHINY_TREVENANT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_TREVENANT),
			new PokedollBlock<>(() -> PokedollShinyTrevenantBlockEntity.class));

	public static final Block POKEDOLL_MARSHADOW = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MARSHADOW),
			new PokedollBlock<>(() -> PokedollMarshadowBlockEntity.class));
	public static final Block POKEDOLL_SHINY_MARSHADOW = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MARSHADOW),
			new PokedollBlock<>(() -> PokedollShinyMarshadowBlockEntity.class));

	public static final Block POKEDOLL_MARSHADOW_ZENITH = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MARSHADOW_ZENITH),
			new PokedollBlock<>(() -> PokedollMarshadowZenithBlockEntity.class));
	public static final Block POKEDOLL_SHINY_MARSHADOW_ZENITH = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MARSHADOW_ZENITH),
			new PokedollBlock<>(() -> PokedollShinyMarshadowZenithBlockEntity.class));

    public static final Block POKEDOLL_BELLOSSOM = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BELLOSSOM),
            new PokedollBlock<>(() -> PokedollBellossomBlockEntity.class));
    public static final Block POKEDOLL_SHINY_BELLOSSOM = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BELLOSSOM),
            new PokedollBlock<>(() -> PokedollShinyBellossomBlockEntity.class));

    public static final Block POKEDOLL_ROWLET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ROWLET),
            new PokedollBlock<>(() -> PokedollRowletBlockEntity.class));
    public static final Block POKEDOLL_SHINY_ROWLET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ROWLET),
            new PokedollBlock<>(() -> PokedollShinyRowletBlockEntity.class));

    public static final Block POKEDOLL_MIMIKYU = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_MIMIKYU),
            new PokedollBlock<>(() -> PokedollMimikyuBlockEntity.class));
    public static final Block POKEDOLL_SHINY_MIMIKYU = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_MIMIKYU),
            new PokedollBlock<>(() -> PokedollShinyMimikyuBlockEntity.class));

    public static final Block POKEDOLL_PILOSWINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_PILOSWINE),
            new PokedollBlock<>(() -> PokedollPiloswineBlockEntity.class));
    public static final Block POKEDOLL_SHINY_PILOSWINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_PILOSWINE),
            new PokedollBlock<>(() -> PokedollShinyPiloswineBlockEntity.class));

    public static final Block POKEDOLL_CUBCHOO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CUBCHOO),
            new PokedollBlock<>(() -> PokedollCubchooBlockEntity.class));
    public static final Block POKEDOLL_SHINY_CUBCHOO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CUBCHOO),
            new PokedollBlock<>(() -> PokedollShinyCubchooBlockEntity.class));

    public static final Block POKEDOLL_BEARTIC = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_BEARTIC),
            new PokedollBlock<>(() -> PokedollBearticBlockEntity.class));
    public static final Block POKEDOLL_SHINY_BEARTIC = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_BEARTIC),
            new PokedollBlock<>(() -> PokedollShinyBearticBlockEntity.class));

	public static final Block POKEDOLL_EISCUE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_EISCUE),
			new PokedollBlock<>(() -> PokedollEiscueBlockEntity.class) {
				@Override
				public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
					ItemStack heldItem = player.getStackInHand(hand);

					// Check if the player is holding shears
					if (heldItem.getItem() instanceof ShearsItem) {
						// Only process on server side
						if (!world.isClient()) {
							Direction facing = state.get(FACING);
							// Change the block to POKEDOLL_NOICE
							world.setBlockState(pos, ModBlocks.POKEDOLL_NOICE.getDefaultState().with(FACING, facing));

							// Drop the EISCUE_HEAD_PILE item
							ItemEntity itemEntity = new ItemEntity(
									world,
									pos.getX() + 0.5,
									pos.getY() + 0.5,
									pos.getZ() + 0.5,
									new ItemStack(ModItems.EISCUE_HEAD_PILE_BLOCK_ITEM)
							);
							world.spawnEntity(itemEntity);

							// Play shearing sound
							world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);

							// Damage the shears
							if (!player.getAbilities().creativeMode) {
								heldItem.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
							}
						}

						return ActionResult.success(world.isClient());
					}

					// If not handling shears, defer to parent class behavior
					return super.onUse(state, world, pos, player, hand, hit);
				}
			});
	public static final Block POKEDOLL_SHINY_EISCUE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_EISCUE),
			new PokedollBlock<>(() -> PokedollShinyEiscueBlockEntity.class) {
				@Override
				public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
					ItemStack heldItem = player.getStackInHand(hand);

					// Check if the player is holding shears
					if (heldItem.getItem() instanceof ShearsItem) {
						// Only process on server side
						if (!world.isClient()) {
							Direction facing = state.get(FACING);
							// Change the block to POKEDOLL_NOICE
							world.setBlockState(pos, ModBlocks.POKEDOLL_SHINY_NOICE.getDefaultState().with(FACING, facing));

							// Drop the EISCUE_HEAD_PILE item
							ItemEntity itemEntity = new ItemEntity(
									world,
									pos.getX() + 0.5,
									pos.getY() + 0.5,
									pos.getZ() + 0.5,
									new ItemStack(ModItems.EISCUE_SHINY_HEAD_PILE_BLOCK_ITEM)
							);
							world.spawnEntity(itemEntity);

							// Play shearing sound
							world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);

							// Damage the shears
							if (!player.getAbilities().creativeMode) {
								heldItem.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
							}
						}

						return ActionResult.success(world.isClient());
					}

					// If not handling shears, defer to parent class behavior
					return super.onUse(state, world, pos, player, hand, hit);
				}
			});

    public static final Block POKEDOLL_NOICE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_EISCUE_NOICE),
            new PokedollBlock<>(() -> PokedollNoiceBlockEntity.class));
    public static final Block POKEDOLL_SHINY_NOICE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_EISCUE_NOICE),
            new PokedollBlock<>(() -> PokedollShinyNoiceBlockEntity.class));

    public static final Block POKEDOLL_CETODDLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_CETODDLE),
            new PokedollBlock<>(() -> PokedollCetoddleBlockEntity.class));
    public static final Block POKEDOLL_SHINY_CETODDLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_CETODDLE),
            new PokedollBlock<>(() -> PokedollShinyCetoddleBlockEntity.class));

    public static final Block POKEDOLL_LUVDISC_CUSHION = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.LUVDISC_CUSHION),
            new SittablePokedollBlock<>(() -> PokedollLuvdiscCushionBlockEntity.class));

    public static final Block POKEDOLL_DELIBIRD = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_DELIBIRD),
            new PokedollBlock<>(() -> PokedollDelibirdBlockEntity.class));
    public static final Block POKEDOLL_SHINY_DELIBIRD = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_DELIBIRD),
            new PokedollBlock<>(() -> PokedollShinyDelibirdBlockEntity.class));

    public static final Block POKEDOLL_TREECKO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_TREECKO),
            new PokedollBlock<>(() -> PokedollTreeckoBlockEntity.class));
    public static final Block POKEDOLL_SHINY_TREECKO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_TREECKO),
            new PokedollBlock<>(() -> PokedollShinyTreeckoBlockEntity.class));

    public static final Block POKEDOLL_SNORUNT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SNORUNT),
            new PokedollBlock<>(() -> PokedollSnoruntBlockEntity.class));
    public static final Block POKEDOLL_SHINY_SNORUNT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SNORUNT),
            new PokedollBlock<>(() -> PokedollShinySnoruntBlockEntity.class));

    public static final Block POKEDOLL_GLALIE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_GLALIE),
            new PokedollBlock<>(() -> PokedollGlalieBlockEntity.class));
    public static final Block POKEDOLL_SHINY_GLALIE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_GLALIE),
            new PokedollBlock<>(() -> PokedollShinyGlalieBlockEntity.class));

    public static final Block POKEDOLL_SPHEAL = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SPHEAL),
            new PokedollBlock<>(() -> PokedollSphealBlockEntity.class));
    public static final Block POKEDOLL_SHINY_SPHEAL = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SPHEAL),
            new PokedollBlock<>(() -> PokedollShinySphealBlockEntity.class));

    public static final Block POKEDOLL_LUVDISC = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_LUVDISC),
            new PokedollBlock<>(() -> PokedollLuvdiscBlockEntity.class));
    public static final Block POKEDOLL_SHINY_LUVDISC = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_LUVDISC),
            new PokedollBlock<>(() -> PokedollShinyLuvdiscBlockEntity.class));

    public static final Block POKEDOLL_RIOLU = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_RIOLU),
            new PokedollBlock<>(() -> PokedollRioluBlockEntity.class));
    public static final Block POKEDOLL_SHINY_RIOLU = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_RIOLU),
            new PokedollBlock<>(() -> PokedollShinyRioluBlockEntity.class));

    public static final Block POKEDOLL_FROSLASS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FROSLASS),
            new PokedollBlock<>(() -> PokedollFroslassBlockEntity.class));
    public static final Block POKEDOLL_SHINY_FROSLASS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FROSLASS),
            new PokedollBlock<>(() -> PokedollShinyFroslassBlockEntity.class));

    public static final Block POKEDOLL_FRIGIBAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_FRIGIBAX),
            new PokedollBlock<>(() -> PokedollFrigibaxBlockEntity.class));
    public static final Block POKEDOLL_SHINY_FRIGIBAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_FRIGIBAX),
            new PokedollBlock<>(() -> PokedollShinyFrigibaxBlockEntity.class));

    public static final Block POKEDOLL_ANIMATED_CUBCHOO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_ANIMATED_CUBCHOO),
            new PokedollBlock<>(() -> PokedollAnimatedCubchooBlockEntity.class));
    public static final Block POKEDOLL_SHINY_ANIMATED_CUBCHOO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_ANIMATED_CUBCHOO),
            new PokedollBlock<>(() -> PokedollShinyAnimatedCubchooBlockEntity.class));

    public static final Block POKEDOLL_SNORUNT_FAMILY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SNORUNT_FAMILY),
            new PokedollBlock<>(() -> PokedollSnoruntFamilyBlockEntity.class));
    public static final Block POKEDOLL_SHINY_SNORUNT_FAMILY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SHINY_SNORUNT_FAMILY),
            new PokedollBlock<>(() -> PokedollShinySnoruntFamilyBlockEntity.class));

    public static final Block POKEDOLL_SKIBIDI_MEWLET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POKEDOLL_SKIBIDI_MEWLET),
            new PokedollBlock<>(() -> PokedollSkibidiMewletBlockEntity.class));

    public static final Block GIGANTIC_POKEDOLL_SKIBIDI_MEWLET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SKIBIDI_MEWLET),
        new PokedollBlock<>(() -> PokedollGiganticSkibidiMewletBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_TREECKO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_TREECKO),
        new PokedollBlock<>(() -> PokedollGiganticTreeckoBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_TREECKO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_TREECKO),
        new PokedollBlock<>(() -> PokedollGiganticShinyTreeckoBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SPHEAL = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SPHEAL),
        new PokedollBlock<>(() -> PokedollGiganticSphealBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_SPHEAL = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SPHEAL),
        new PokedollBlock<>(() -> PokedollGiganticShinySphealBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SNORUNT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SNORUNT),
        new PokedollBlock<>(() -> PokedollGiganticSnoruntBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SNORUNT_FAMILY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SNORUNT_FAMILY),
			new PokedollBlock<>(() -> PokedollGiganticSnoruntFamilyBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_SNORUNT_FAMILY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SNORUNT_FAMILY),
			new PokedollBlock<>(() -> PokedollGiganticShinySnoruntFamilyBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_SNORUNT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SNORUNT),
        new PokedollBlock<>(() -> PokedollGiganticShinySnoruntBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_RIOLU = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_RIOLU),
        new PokedollBlock<>(() -> PokedollGiganticRioluBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_RIOLU = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_RIOLU),
        new PokedollBlock<>(() -> PokedollGiganticShinyRioluBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_PILOSWINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_PILOSWINE),
        new PokedollBlock<>(() -> PokedollGiganticPiloswineBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_PILOSWINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_PILOSWINE),
        new PokedollBlock<>(() -> PokedollGiganticShinyPiloswineBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_EISCUE_NOICE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_EISCUE_NOICE),
        new PokedollBlock<>(() -> PokedollGiganticNoiceBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_EISCUE_NOICE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_EISCUE_NOICE),
        new PokedollBlock<>(() -> PokedollGiganticShinyNoiceBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_LUVDISC = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_LUVDISC),
        new PokedollBlock<>(() -> PokedollGiganticLuvdiscBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_LUVDISC = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_LUVDISC),
        new PokedollBlock<>(() -> PokedollGiganticShinyLuvdiscBlockEntity.class));
    public static final Block GIGANTIC_EISCUE_HEAD_PILE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_EISCUE_HEAD_PILE),
        new PokedollBlock<>(() -> PokedollGiganticHeadpileBlockEntity.class) {
			@Override
			public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
				ItemStack heldItem = player.getStackInHand(hand);

				// Check if the player is holding another head pile item
				if (heldItem.getItem().equals(ModItems.GIGANTIC_POKEDOLL_HEAD_PILE_BLOCK_ITEM)) {
					// Get the block entity
					if (world.getBlockEntity(pos) instanceof PokedollGiganticHeadpileBlockEntity headPile) {
						// Cycle the model and texture
						boolean success = headPile.cycleModelAndTexture();

						if (success) {
							// Play a sound effect for feedback
							world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1.2f);

							// Only decrease stack if player is not in creative mode
							if (!player.getAbilities().creativeMode && !world.isClient()) {
								heldItem.decrement(1);
							}

							// Return success result
							return ActionResult.success(world.isClient());
						} else {
							return super.onUse(state, world, pos, player, hand, hit);
						}
					}
				}

				// If none of the conditions are met, defer to parent class behavior
				return super.onUse(state, world, pos, player, hand, hit);
			}

			@Override
			public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
				// First check if this is a head pile block entity
				if (blockEntity instanceof PokedollGiganticHeadpileBlockEntity headPile) {
					// Get the TextureIndex from the block entity
					NbtCompound nbt = new NbtCompound();
					headPile.writeNbt(nbt);
					int textureIndex = nbt.getInt("TextureIndex");

					// Calculate how many items to drop (TextureIndex + 1)
					int count = textureIndex + 1;

					// Create and drop the items
					ItemStack dropStack = new ItemStack(this.asItem(), count);
					ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
					world.spawnEntity(itemEntity);

					// Apply tool damage
					tool.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
				} else {
					// If it's not our special block entity, fall back to default behavior
					super.afterBreak(world, player, pos, state, blockEntity, tool);
				}
			}
		});
    public static final Block GIGANTIC_EISCUE_SHINY_HEAD_PILE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_EISCUE_SHINY_HEAD_PILE),
        new PokedollBlock<>(() -> PokedollGiganticShinyHeadpileBlockEntity.class) {
			@Override
			public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
				ItemStack heldItem = player.getStackInHand(hand);

				// Check if the player is holding another head pile item
				if (heldItem.getItem().equals(ModItems.GIGANTIC_POKEDOLL_SHINY_HEAD_PILE_BLOCK_ITEM)) {
					// Get the block entity
					if (world.getBlockEntity(pos) instanceof PokedollGiganticShinyHeadpileBlockEntity headPile) {
						// Cycle the model and texture
						boolean success = headPile.cycleModelAndTexture();

						if (success) {
							// Play a sound effect for feedback
							world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1.2f);

							// Only decrease stack if player is not in creative mode
							if (!player.getAbilities().creativeMode && !world.isClient()) {
								heldItem.decrement(1);
							}

							// Return success result
							return ActionResult.success(world.isClient());
						} else {
							return super.onUse(state, world, pos, player, hand, hit);
						}
					}
				}

				// If none of the conditions are met, defer to parent class behavior
				return super.onUse(state, world, pos, player, hand, hit);
			}

			@Override
			public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
				// First check if this is a head pile block entity
				if (blockEntity instanceof PokedollGiganticShinyHeadpileBlockEntity headPile) {
					// Get the TextureIndex from the block entity
					NbtCompound nbt = new NbtCompound();
					headPile.writeNbt(nbt);
					int textureIndex = nbt.getInt("TextureIndex");

					// Calculate how many items to drop (TextureIndex + 1)
					int count = textureIndex + 1;

					// Create and drop the items
					ItemStack dropStack = new ItemStack(this.asItem(), count);
					ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
					world.spawnEntity(itemEntity);

					// Apply tool damage
					tool.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
				} else {
					// If it's not our special block entity, fall back to default behavior
					super.afterBreak(world, player, pos, state, blockEntity, tool);
				}
			}
		});
    public static final Block GIGANTIC_POKEDOLL_GLALIE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_GLALIE),
        new PokedollBlock<>(() -> PokedollGiganticGlalieBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_GLALIE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_GLALIE),
        new PokedollBlock<>(() -> PokedollGiganticShinyGlalieBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_FROSLASS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_FROSLASS),
        new PokedollBlock<>(() -> PokedollGiganticFroslassBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_FROSLASS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_FROSLASS),
        new PokedollBlock<>(() -> PokedollGiganticShinyFroslassBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_FRIGIBAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_FRIGIBAX),
        new PokedollBlock<>(() -> PokedollGiganticFrigibaxBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_FRIGIBAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_FRIGIBAX),
        new PokedollBlock<>(() -> PokedollGiganticShinyFrigibaxBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_EISCUE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_EISCUE),
        new PokedollBlock<>(() -> PokedollGiganticEiscueBlockEntity.class) {
			@Override
			public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
				ItemStack heldItem = player.getStackInHand(hand);

				// Check if the player is holding shears
				if (heldItem.getItem() instanceof ShearsItem) {
					// Only process on server side
					if (!world.isClient()) {
						Direction facing = state.get(FACING);
						// Change the block to POKEDOLL_NOICE
						world.setBlockState(pos, ModBlocks.GIGANTIC_POKEDOLL_EISCUE_NOICE.getDefaultState().with(FACING, facing));

						// Drop the EISCUE_HEAD_PILE item
						ItemEntity itemEntity = new ItemEntity(
								world,
								pos.getX() + 0.5,
								pos.getY() + 0.5,
								pos.getZ() + 0.5,
								new ItemStack(ModItems.GIGANTIC_POKEDOLL_HEAD_PILE_BLOCK_ITEM)
						);
						world.spawnEntity(itemEntity);

						// Play shearing sound
						world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);

						// Damage the shears
						if (!player.getAbilities().creativeMode) {
							heldItem.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
						}
					}

					return ActionResult.success(world.isClient());
				}

				// If not handling shears, defer to parent class behavior
				return super.onUse(state, world, pos, player, hand, hit);
			}
		});
	public static final Block GIGANTIC_POKEDOLL_SHINY_EISCUE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_EISCUE),
			new PokedollBlock<>(() -> PokedollGiganticShinyEiscueBlockEntity.class) {
				@Override
				public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
					ItemStack heldItem = player.getStackInHand(hand);

					// Check if the player is holding shears
					if (heldItem.getItem() instanceof ShearsItem) {
						// Only process on server side
						if (!world.isClient()) {
							// Get the current facing direction
							Direction facing = state.get(FACING);

							// Change the block to POKEDOLL_NOICE while preserving the facing direction
							world.setBlockState(pos, ModBlocks.GIGANTIC_POKEDOLL_SHINY_EISCUE_NOICE.getDefaultState().with(FACING, facing));

							// Drop the EISCUE_HEAD_PILE item
							ItemEntity itemEntity = new ItemEntity(
									world,
									pos.getX() + 0.5,
									pos.getY() + 0.5,
									pos.getZ() + 0.5,
									new ItemStack(ModItems.GIGANTIC_POKEDOLL_SHINY_HEAD_PILE_BLOCK_ITEM)
							);
							world.spawnEntity(itemEntity);

							// Play shearing sound
							world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);

							// Damage the shears
							if (!player.getAbilities().creativeMode) {
								heldItem.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
							}
						}

						return ActionResult.success(world.isClient());
					}

					// If not handling shears, defer to parent class behavior
					return super.onUse(state, world, pos, player, hand, hit);
				}
			});
    public static final Block GIGANTIC_POKEDOLL_DELIBIRD = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_DELIBIRD),
        new PokedollBlock<>(() -> PokedollGiganticDelibirdBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_DELIBIRD = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_DELIBIRD),
        new PokedollBlock<>(() -> PokedollGiganticShinyDelibirdBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_CUBCHOO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CUBCHOO),
        new PokedollBlock<>(() -> PokedollGiganticCubchooBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_CUBCHOO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CUBCHOO),
			new PokedollBlock<>(() -> PokedollGiganticShinyCubchooBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_CUBCHOO_ANIMATED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ANIMATED_CUBCHOO),
        new PokedollBlock<>(() -> PokedollGiganticAnimatedCubchooBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_CUBCHOO_ANIMATED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ANIMATED_CUBCHOO),
			new PokedollBlock<>(() -> PokedollGiganticShinyAnimatedCubchooBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_CETODDLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CETODDLE),
        new PokedollBlock<>(() -> PokedollGiganticCetoddleBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_CETODDLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CETODDLE),
        new PokedollBlock<>(() -> PokedollGiganticShinyCetoddleBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_BEARTIC = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BEARTIC),
        new PokedollBlock<>(() -> PokedollGiganticBearticBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_BEARTIC = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BEARTIC),
        new PokedollBlock<>(() -> PokedollGiganticShinyBearticBlockEntity.class));
	public static final Block EISCUE_HEAD_PILE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.EISCUE_HEAD_PILE),
			new PokedollBlock<>(() -> EiscueHeadpileBlockEntity.class) {
				@Override
				public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
					ItemStack heldItem = player.getStackInHand(hand);

					// Check if the player is holding another head pile item
					if (heldItem.getItem().equals(ModItems.EISCUE_HEAD_PILE_BLOCK_ITEM)) {
						// Get the block entity
						if (world.getBlockEntity(pos) instanceof EiscueHeadpileBlockEntity headPile) {
							// Cycle the model and texture
							boolean success = headPile.cycleModelAndTexture();

							if (success) {
								// Play a sound effect for feedback
								world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1.2f);

								// Only decrease stack if player is not in creative mode
								if (!player.getAbilities().creativeMode && !world.isClient()) {
									heldItem.decrement(1);
								}

								// Return success result
								return ActionResult.success(world.isClient());
							} else {
								return super.onUse(state, world, pos, player, hand, hit);
							}
						}
					}

					// If none of the conditions are met, defer to parent class behavior
					return super.onUse(state, world, pos, player, hand, hit);
				}

				@Override
				public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
					// First check if this is a head pile block entity
					if (blockEntity instanceof EiscueHeadpileBlockEntity headPile) {
						// Get the TextureIndex from the block entity
						NbtCompound nbt = new NbtCompound();
						headPile.writeNbt(nbt);
						int textureIndex = nbt.getInt("TextureIndex");

						// Calculate how many items to drop (TextureIndex + 1)
						int count = textureIndex + 1;

						// Create and drop the items
						ItemStack dropStack = new ItemStack(this.asItem(), count);
						ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
						world.spawnEntity(itemEntity);

						// Apply tool damage
						tool.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
					} else {
						// If it's not our special block entity, fall back to default behavior
						super.afterBreak(world, player, pos, state, blockEntity, tool);
					}
				}
			}
	);
	public static final Block EISCUE_SHINY_HEAD_PILE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.EISCUE_SHINY_HEAD_PILE),
			new PokedollBlock<>(() -> EiscueShinyHeadpileBlockEntity.class) {
				@Override
				public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
					ItemStack heldItem = player.getStackInHand(hand);

					// Check if the player is holding another head pile item
					if (heldItem.getItem().equals(ModItems.EISCUE_SHINY_HEAD_PILE_BLOCK_ITEM)) {
						// Get the block entity
						if (world.getBlockEntity(pos) instanceof EiscueShinyHeadpileBlockEntity headPile) {
							// Cycle the model and texture
							boolean success = headPile.cycleModelAndTexture();

							if (success) {
								// Play a sound effect for feedback
								world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1.2f);

								// Only decrease stack if player is not in creative mode
								if (!player.getAbilities().creativeMode && !world.isClient()) {
									heldItem.decrement(1);
								}

								// Return success result
								return ActionResult.success(world.isClient());
							} else {
								return super.onUse(state, world, pos, player, hand, hit);
							}
						}
					}

					// If none of the conditions are met, defer to parent class behavior
					return super.onUse(state, world, pos, player, hand, hit);
				}

				@Override
				public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
					// First check if this is a head pile block entity
					if (blockEntity instanceof EiscueShinyHeadpileBlockEntity headPile) {
						// Get the TextureIndex from the block entity
						NbtCompound nbt = new NbtCompound();
						headPile.writeNbt(nbt);
						int textureIndex = nbt.getInt("TextureIndex");

						// Calculate how many items to drop (TextureIndex + 1)
						int count = textureIndex + 1;

						// Create and drop the items
						ItemStack dropStack = new ItemStack(this.asItem(), count);
						ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
						world.spawnEntity(itemEntity);

						// Apply tool damage
						tool.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
					} else {
						// If it's not our special block entity, fall back to default behavior
						super.afterBreak(world, player, pos, state, blockEntity, tool);
					}
				}
			}
	);

    public static final Block FIGURINE_MIK_03 = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.MIK_03_FIGURINE),
            new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> PokedollMik03FigurineBlockEntity.class));

    public static final Block FIGURINE_POHELLO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.POHELLO_FIGURINE),
            new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> PokedollPohelloFigurineBlockEntity.class));

    public static final Block FIGURINE_CHEEZYGRATE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.FIGURINE_CHEEZYGRATE),
            new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> PokedollCheezygrateFigurineBlockEntity.class));

    public static final Block FIGURINE_EXHS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.FIGURINE_EXHS),
            new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> PokedollExhsFigurineBlockEntity.class));

    public static final Block FIGURINE___MORPH = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.FIGURINE___MORPH),
            new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> PokedollMorphFigurineBlockEntity.class));

    public static final Block GIGANTIC_POKEDOLL_ROWLET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ROWLET),
        new PokedollBlock<>(() -> PokedollGiganticRowletBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_ROWLET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ROWLET),
        new PokedollBlock<>(() -> PokedollGiganticShinyRowletBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_MIMIKYU = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MIMIKYU),
        new PokedollBlock<>(() -> PokedollGiganticMimikyuBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_MIMIKYU = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MIMIKYU),
        new PokedollBlock<>(() -> PokedollGiganticShinyMimikyuBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_BELLOSSOM = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BELLOSSOM),
        new PokedollBlock<>(() -> PokedollGiganticBellossomBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_BELLOSSOM = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BELLOSSOM),
        new PokedollBlock<>(() -> PokedollGiganticShinyBellossomBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_TREVENANT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_TREVENANT),
        new PokedollBlock<>(() -> PokedollGiganticTrevenantBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_TREVENANT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_TREVENANT),
        new PokedollBlock<>(() -> PokedollGiganticShinyTrevenantBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_PUMPKABOO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_PUMPKABOO),
        new PokedollBlock<>(() -> PokedollGiganticPumpkabooBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_PUMPKABOO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_PUMPKABOO),
        new PokedollBlock<>(() -> PokedollGiganticShinyPumpkabooBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_PHANTUMP = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_PHANTUMP),
        new PokedollBlock<>(() -> PokedollGiganticPhantumpBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_PHANTUMP = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_PHANTUMP),
        new PokedollBlock<>(() -> PokedollGiganticShinyPhantumpBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_MARSHADOW = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MARSHADOW),
        new PokedollBlock<>(() -> PokedollGiganticMarshadowBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_MARSHADOW = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MARSHADOW),
        new PokedollBlock<>(() -> PokedollGiganticShinyMarshadowBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_MARSHADOW_ZENITH = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MARSHADOW_ZENITH),
        new PokedollBlock<>(() -> PokedollGiganticMarshadowZenithBlockEntity.class));
    public static final Block GIGANTIC_POKEDOLL_SHINY_MARSHADOW_ZENITH = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MARSHADOW_ZENITH),
        new PokedollBlock<>(() -> PokedollGiganticShinyMarshadowZenithBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_WAILORD = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WAILORD),
			new PokedollBlock<>(() -> PokedollGiganticWailordBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_WAILORD = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_WAILORD),
			new PokedollBlock<>(() -> PokedollGiganticShinyWailordBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_WAILMER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WAILMER),
			new PokedollBlock<>(() -> PokedollGiganticWailmerBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_WAILMER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_WAILMER),
			new PokedollBlock<>(() -> PokedollGiganticShinyWailmerBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_TROPIUS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_TROPIUS),
			new PokedollBlock<>(() -> PokedollGiganticTropiusBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_TROPIUS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_TROPIUS),
			new PokedollBlock<>(() -> PokedollGiganticShinyTropiusBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_SHELLDER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHELLDER),
			new PokedollBlock<>(() -> PokedollGiganticShellderBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_SHELLDER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SHELLDER),
			new PokedollBlock<>(() -> PokedollGiganticShinyShellderBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_KYOGRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_KYOGRE),
			new PokedollBlock<>(() -> PokedollGiganticKyogreBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_KYOGRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_KYOGRE),
			new PokedollBlock<>(() -> PokedollGiganticShinyKyogreBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_CLOYSTER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CLOYSTER),
			new PokedollBlock<>(() -> PokedollGiganticCloysterBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_CLOYSTER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CLOYSTER),
			new PokedollBlock<>(() -> PokedollGiganticShinyCloysterBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SANDYGAST = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SANDYGAST),
			new PokedollBlock<>(() -> PokedollGiganticSandygastBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_SANDYGAST = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SANDYGAST),
			new PokedollBlock<>(() -> PokedollGiganticShinySandygastBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_PALOSSAND = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_PALOSSAND),
			new PokedollBlock<>(() -> PokedollGiganticPalossandBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_PALOSSAND = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_PALOSSAND),
			new PokedollBlock<>(() -> PokedollGiganticShinyPalossandBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_GHOLDENGO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_GHOLDENGO),
			new PokedollBlock<>(() -> PokedollGiganticGholdengoBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_GHOLDENGO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_GHOLDENGO),
			new PokedollBlock<>(() -> PokedollGiganticShinyGholdengoBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_NETHERITE_GHOLDENGO),
			new PokedollBlock<>(() -> PokedollGiganticNetheriteGholdengoBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_NETHERITE_GHOLDENGO = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_NETHERITE_GHOLDENGO),
			new PokedollBlock<>(() -> PokedollGiganticShinyNetheriteGholdengoBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_EEVEE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_EEVEE),
			new PokedollBlock<>(() -> PokedollGiganticEeveeBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_EEVEE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_EEVEE),
			new PokedollBlock<>(() -> PokedollGiganticShinyEeveeBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_STONJOURNER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_STONJOURNER),
			new PokedollBlock<>(() -> PokedollGiganticStonjournerBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_STONJOURNER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_STONJOURNER),
			new PokedollBlock<>(() -> PokedollGiganticShinyStonjournerBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_ROOKIDEE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ROOKIDEE),
			new PokedollBlock<>(() -> PokedollGiganticRookideeBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_ROOKIDEE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ROOKIDEE),
			new PokedollBlock<>(() -> PokedollGiganticShinyRookideeBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_GENGAR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_GENGAR),
			new PokedollBlock<>(() -> PokedollGiganticGengarBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_GENGAR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_GENGAR),
			new PokedollBlock<>(() -> PokedollGiganticShinyGengarBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_GASTLY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_GASTLY),
			new PokedollBlock<>(() -> PokedollGiganticGastlyBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_GASTLY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_GASTLY),
			new PokedollBlock<>(() -> PokedollGiganticShinyGastlyBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_DRIFLOON = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_DRIFLOON),
			new PokedollBlock<>(() -> PokedollGiganticDrifloonBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_DRIFLOON = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_DRIFLOON),
			new PokedollBlock<>(() -> PokedollGiganticShinyDrifloonBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_CORVISQUIRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CORVISQUIRE),
			new PokedollBlock<>(() -> PokedollGiganticCorvisquireBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CORVISQUIRE),
			new PokedollBlock<>(() -> PokedollGiganticShinyCorvisquireBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_CORVIKNIGHT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CORVIKNIGHT),
			new PokedollBlock<>(() -> PokedollGiganticCorviknightBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CORVIKNIGHT),
			new PokedollBlock<>(() -> PokedollGiganticShinyCorviknightBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_WOOPER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WOOPER),
			new PokedollBlock<>(() -> PokedollGiganticWooperBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_WOOPER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_WOOPER),
			new PokedollBlock<>(() -> PokedollGiganticShinyWooperBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_WASHING_MACHINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WASHING_MACHINE),
			new PokedollBlock<>(() -> PokedollGiganticWashingMachineBlockEntity.class) {
				@Override
				public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
					world.playSound(player, pos, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 1f, 1f);

					if (world.random.nextDouble() < 0.5) {
						Direction facing = state.get(Properties.HORIZONTAL_FACING);
						double x = pos.getX() + 0.5 + facing.getOffsetX() * 0.5;
						double y = pos.getY() + 0.25;
						double z = pos.getZ() + 0.5 + facing.getOffsetZ() * 0.5;

						double velocity = 0.5;
						double dx = facing.getOffsetX() * velocity;
						double dy = 0;
						double dz = facing.getOffsetZ() * velocity;

						world.addParticle(ParticleTypes.CLOUD, x, y, z, dx, dy, dz);

						// Check for collision with players
						Box particleBox = new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5);
						for (PlayerEntity playerEntity : world.getPlayers()) {
							if (playerEntity.getBoundingBox().intersects(particleBox)) {
								// Broadcast the message to the entire server
								ServerHandler.broadcast(playerEntity.getDisplayName().getString() + " got washed!", Formatting.GOLD);
							}
						}
					}

					return ActionResult.SUCCESS;
				}
			});

	public static final Block GIGANTIC_POKEDOLL_WARTORTLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_WARTORTLE),
			new PokedollBlock<>(() -> PokedollGiganticWartortleBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_WARTORTLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_WARTORTLE),
			new PokedollBlock<>(() -> PokedollGiganticShinyWartortleBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_VENUSAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_VENUSAUR),
			new PokedollBlock<>(() -> PokedollGiganticVenusaurBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_VENUSAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_VENUSAUR),
			new PokedollBlock<>(() -> PokedollGiganticShinyVenusaurBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_SWINUB = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SWINUB),
			new PokedollBlock<>(() -> PokedollGiganticSwinubBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_SWINUB = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SWINUB),
			new PokedollBlock<>(() -> PokedollGiganticShinySwinubBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_SQUIRTLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SQUIRTLE),
			new PokedollBlock<>(() -> PokedollGiganticSquirtleBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_SQUIRTLE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SQUIRTLE),
			new PokedollBlock<>(() -> PokedollGiganticShinySquirtleBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_SNORLAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SNORLAX),
			new PokedollBlock<>(() -> PokedollGiganticSnorlaxBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_SNORLAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SNORLAX),
			new PokedollBlock<>(() -> PokedollGiganticShinySnorlaxBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_SMOLIV = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SMOLIV),
			new PokedollBlock<>(() -> PokedollGiganticSmolivBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_SMOLIV = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SMOLIV),
			new PokedollBlock<>(() -> PokedollGiganticShinySmolivBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_SENTRET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SENTRET),
			new PokedollBlock<>(() -> PokedollGiganticSentretBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_SENTRET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SENTRET),
			new PokedollBlock<>(() -> PokedollGiganticShinySentretBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_SABLEYE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SABLEYE),
			new PokedollBlock<>(() -> PokedollGiganticSableyeBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_SABLEYE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_SABLEYE),
			new PokedollBlock<>(() -> PokedollGiganticShinySableyeBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_RELLOR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_RELLOR),
			new PokedollBlock<>(() -> PokedollGiganticRellorBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_RELLOR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_RELLOR),
			new PokedollBlock<>(() -> PokedollGiganticShinyRellorBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_RABSCA = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_RABSCA),
			new PokedollBlock<>(() -> PokedollGiganticRabscaBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_RABSCA = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_RABSCA),
			new PokedollBlock<>(() -> PokedollGiganticShinyRabscaBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_QUAGSIRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_QUAGSIRE),
			new PokedollBlock<>(() -> PokedollGiganticQuagsireBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_QUAGSIRE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_QUAGSIRE),
			new PokedollBlock<>(() -> PokedollGiganticShinyQuagsireBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_MUNCHLAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MUNCHLAX),
			new PokedollBlock<>(() -> PokedollGiganticMunchlaxBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_MUNCHLAX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MUNCHLAX),
			new PokedollBlock<>(() -> PokedollGiganticShinyMunchlaxBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_MAREEP = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_MAREEP),
			new PokedollBlock<>(() -> PokedollGiganticMareepBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_MAREEP = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_MAREEP),
			new PokedollBlock<>(() -> PokedollGiganticShinyMareepBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_LICKITUNG = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_LICKITUNG),
			new PokedollBlock<>(() -> PokedollGiganticLickitungBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_LICKITUNG = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_LICKITUNG),
			new PokedollBlock<>(() -> PokedollGiganticShinyLickitungBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_IVYSAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_IVYSAUR),
			new PokedollBlock<>(() -> PokedollGiganticIvysaurBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_IVYSAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_IVYSAUR),
			new PokedollBlock<>(() -> PokedollGiganticShinyIvysaurBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_HAPPINY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_HAPPINY),
			new PokedollBlock<>(() -> PokedollGiganticHappinyBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_HAPPINY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_HAPPINY),
			new PokedollBlock<>(() -> PokedollGiganticShinyHappinyBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_FURRET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_FURRET),
			new PokedollBlock<>(() -> PokedollGiganticFurretBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_FURRET = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_FURRET),
			new PokedollBlock<>(() -> PokedollGiganticShinyFurretBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_FLAAFFY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_FLAAFFY),
			new PokedollBlock<>(() -> PokedollGiganticFlaaffyBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_FLAAFFY = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_FLAAFFY),
			new PokedollBlock<>(() -> PokedollGiganticShinyFlaaffyBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_DOLLIV = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_DOLLIV),
			new PokedollBlock<>(() -> PokedollGiganticDollivBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_DOLLIV = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_DOLLIV),
			new PokedollBlock<>(() -> PokedollGiganticShinyDollivBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_CHARMANDER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CHARMANDER),
			new PokedollBlock<>(() -> PokedollGiganticCharmanderBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_CHARMANDER = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CHARMANDER),
			new PokedollBlock<>(() -> PokedollGiganticShinyCharmanderBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_CALYREX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CALYREX),
			new PokedollBlock<>(() -> PokedollGiganticCalyrexBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_CALYREX_ANIMATED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_CALYREX_ANIMATED),
			new PokedollBlock<>(() -> PokedollGiganticCalyrexAnimatedBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_CALYREX = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CALYREX),
			new PokedollBlock<>(() -> PokedollGiganticShinyCalyrexBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_CALYREX_ANIMATED),
			new PokedollBlock<>(() -> PokedollGiganticShinyCalyrexAnimatedBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_BULBASAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BULBASAUR),
			new PokedollBlock<>(() -> PokedollGiganticBulbasaurBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_BULBASAUR_POSED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BULBASAUR_POSED),
			new PokedollBlock<>(() -> PokedollGiganticBulbasaurPosedBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_BULBASAUR = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BULBASAUR),
			new PokedollBlock<>(() -> PokedollGiganticShinyBulbasaurBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BULBASAUR_POSED),
			new PokedollBlock<>(() -> PokedollGiganticShinyBulbasaurPosedBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_BLASTOISE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_BLASTOISE),
			new PokedollBlock<>(() -> PokedollGiganticBlastoiseBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_BLASTOISE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_BLASTOISE),
			new PokedollBlock<>(() -> PokedollGiganticShinyBlastoiseBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_ARBOLIVA = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ARBOLIVA),
			new PokedollBlock<>(() -> PokedollGiganticArbolivaBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_ARBOLIVA = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ARBOLIVA),
			new PokedollBlock<>(() -> PokedollGiganticShinyArbolivaBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_AMPHAROS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_AMPHAROS),
			new PokedollBlock<>(() -> PokedollGiganticAmpharosBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_AMPHAROS = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_AMPHAROS),
			new PokedollBlock<>(() -> PokedollGiganticShinyAmpharosBlockEntity.class));

	public static final Block GIGANTIC_POKEDOLL_ABSOL = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_ABSOL),
			new PokedollBlock<>(() -> PokedollGiganticAbsolBlockEntity.class));
	public static final Block GIGANTIC_POKEDOLL_SHINY_ABSOL = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.GIGANTIC_POKEDOLL_SHINY_ABSOL),
			new PokedollBlock<>(() -> PokedollGiganticShinyAbsolBlockEntity.class));
	public static final Block A09ROBERT_FIGURINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.A09ROBERT_FIGURINE),
			new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> A09RobertFigurineBlockEntity.class));
	public static final Block RED_COMMUNISM_FIGURINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.RED_COMMUNISM_FIGURINE),
			new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> RedCommunismFigurineBlockEntity.class));
	public static final Block TROPSIC0_FIGURINE = Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, PokeIDs.TROPSIC0_FIGURINE),
			new PokedollBlock<>(Shapes.FIGURINE_SHAPE, () -> Tropsic0FigurineBlockEntity.class));

	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(Pokeblocks.MOD_ID, name), block);
	}

	private static Item registerBlockItem(String name, Block block) {
		return Registry.register(Registries.ITEM, new Identifier(Pokeblocks.MOD_ID, name),
				new BlockItem(block, new FabricItemSettings()));
	}

	public static Block getGiganticPokedollBlock(String pokemonName) {
		try {
			// Get the field corresponding to the gigantic Pokedoll block
			String fieldName = "GIGANTIC_POKEDOLL_" + pokemonName.toUpperCase();
			System.out.println("Looking for field " + fieldName + " in ModBlocks");
			Field field = ModBlocks.class.getDeclaredField(fieldName);
			field.setAccessible(true); // Allow accessing the field
			return (Block) field.get(null); // Return the value of the field (which should be the gigantic Pokedoll block)
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get gigantic Pokedoll block for " + pokemonName);
		}
	}

	public static List<Block> getAllDollBlocks(boolean includeGigantics) {
		List<Block> blocks = new ArrayList<>();
		for (Field field : ModBlocks.class.getDeclaredFields()) {
			if (field.getType() == Block.class) {
				if (!includeGigantics && field.getName().startsWith("GIGANTIC_")) continue;
				try {
					Block block = (Block) field.get(null);
					blocks.add(block);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return blocks;
	}

	public static void registerModBlocks() {
		Pokeblocks.LOGGER.info("Registering ModBlocks for " + Pokeblocks.MOD_ID);
	}

}
