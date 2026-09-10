package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.mrshawn.pokeblocks.entity.custom.FigurineEntity;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.registry.EntityRegistry;
import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Set;

/**
 * {@code /pokeblocks figurinespawn <figurine> [flags]} — spawns a living, walking
 * {@link FigurineEntity} of the given figurine at the command source's position, facing the way
 * the source faces. Figurine ids and flags parse exactly like {@link FigurineGiveCMD}'s, so the
 * two commands accept the same inputs.
 */
public class FigurineSpawnCMD {

	private static final SuggestionProvider<CommandSourceStack> SUGGEST_FIGURINES = (context, builder) -> {
		for (String id : FigurineRegistry.ALL_FIGURINES) builder.suggest(id);
		return builder.buildFuture();
	};

	/** Suggests only the flags the chosen figurine actually ships (falls back to every flag). */
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_FLAGS = (context, builder) -> {
		String figurine = StringArgumentType.getString(context, "figurine").toLowerCase();
		Set<FigurineFlag> available = FigurineRegistry.availableFlags(figurine);
		if (available.isEmpty()) {
			for (FigurineFlag flag : FigurineFlag.values()) builder.suggest(flag.getTagName());
		} else {
			for (FigurineFlag flag : available) builder.suggest(flag.getTagName());
		}
		return builder.buildFuture();
	};

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("pokeblocks").then(
						Commands.literal("figurinespawn")
								.requires(src -> src.hasPermission(2))
								.then(Commands.argument("figurine", StringArgumentType.word())
										.suggests(SUGGEST_FIGURINES)
										.executes(FigurineSpawnCMD::execute)
										.then(Commands.argument("flags", StringArgumentType.greedyString())
												.suggests(SUGGEST_FLAGS)
												.executes(FigurineSpawnCMD::executeWithFlags)
										)
								)
				));
	}

	private static int execute(CommandContext<CommandSourceStack> context) {
		return spawn(context, EnumSet.noneOf(FigurineFlag.class), "");
	}

	private static int executeWithFlags(CommandContext<CommandSourceStack> context) {
		String raw = StringArgumentType.getString(context, "flags").trim();
		Set<FigurineFlag> flags = EnumSet.noneOf(FigurineFlag.class);
		for (String token : raw.split("[\\s,]+")) {
			if (token.isEmpty()) continue;
			FigurineFlag flag = FigurineFlag.fromTagName(token);
			if (flag == null) {
				context.getSource().sendFailure(Component.literal(
						"Unknown figurine flag '" + token + "'. Valid flags: " + FigurineFlag.allTagNames()));
				return 0;
			}
			flags.add(flag);
		}
		if (FigurineFlag.hasExclusionConflict(flags)) {
			context.getSource().sendFailure(Component.literal("Those figurine flags are mutually exclusive"));
			return 0;
		}
		return spawn(context, flags, raw);
	}

	private static int spawn(CommandContext<CommandSourceStack> context, Set<FigurineFlag> flags, String flagLabel) {
		try {
			CommandSourceStack source = context.getSource();
			String figurine = StringArgumentType.getString(context, "figurine").toLowerCase();

			if (!FigurineRegistry.isRegistered(figurine)) {
				source.sendFailure(Component.literal("Figurine '" + figurine + "' is not registered"));
				return 0;
			}

			ServerLevel level = source.getLevel();
			FigurineEntity entity = EntityRegistry.FIGURINE_ENTITY.get().create(level);
			if (entity == null) {
				source.sendFailure(Component.literal("Could not create the figurine entity"));
				return 0;
			}

			entity.setFigurine(figurine, flags);
			Vec3 pos = source.getPosition();
			entity.moveTo(pos.x, pos.y, pos.z, source.getRotation().y, 0.0f);
			entity.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null);
			level.addFreshEntity(entity);

			String label = flagLabel.isEmpty() ? figurine : flagLabel + " " + figurine;
			source.sendSuccess(() -> Component.literal("Spawned a walking " + label + " figurine"), true);
			return 1;
		} catch (Exception e) {
			context.getSource().sendFailure(Component.literal("Failed to spawn figurine: " + e.getMessage()));
			return 0;
		}
	}
}
