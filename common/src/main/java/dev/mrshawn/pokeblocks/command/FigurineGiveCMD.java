package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public class FigurineGiveCMD {

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
						Commands.literal("figurinegive")
								.requires(src -> src.hasPermission(2))
								.then(Commands.argument("player", EntityArgument.player())
										.then(Commands.argument("figurine", StringArgumentType.word())
												.suggests(SUGGEST_FIGURINES)
												.executes(FigurineGiveCMD::execute)
												.then(Commands.argument("flags", StringArgumentType.greedyString())
														.suggests(SUGGEST_FLAGS)
														.executes(FigurineGiveCMD::executeWithFlags)
												)
										)
								)
				));
	}

	private static int execute(CommandContext<CommandSourceStack> context) {
		return give(context, EnumSet.noneOf(FigurineFlag.class), "");
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
		return give(context, flags, raw);
	}

	private static int give(CommandContext<CommandSourceStack> context, Set<FigurineFlag> flags, String flagLabel) {
		try {
			ServerPlayer player = EntityArgument.getPlayer(context, "player");
			String figurine = StringArgumentType.getString(context, "figurine").toLowerCase();
			CommandSourceStack source = context.getSource();

			if (!FigurineRegistry.isRegistered(figurine)) {
				source.sendFailure(Component.literal("Figurine '" + figurine + "' is not registered"));
				return 0;
			}

			ItemStack stack = FigurineItem.createFigurine(figurine, flags);

			if (!player.getInventory().add(stack)) {
				player.drop(stack, false);
			}

			String label = flagLabel.isEmpty() ? figurine : flagLabel + " " + figurine;
			source.sendSuccess(() -> Component.literal("Gave " + label + " figurine to " + player.getName().getString()), true);
			return 1;
		} catch (Exception e) {
			context.getSource().sendFailure(Component.literal("Failed to give figurine: " + e.getMessage()));
			return 0;
		}
	}
}
