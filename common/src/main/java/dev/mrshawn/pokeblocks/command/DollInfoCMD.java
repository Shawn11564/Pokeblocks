package dev.mrshawn.pokeblocks.command;

import dev.mrshawn.pokeblocks.pokemon.PokemonRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class DollInfoCMD {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("dollinfo")
						.then(Commands.argument("pokemon", StringArgumentType.word())
								.executes(DollInfoCMD::execute))
		);
	}

	private static int execute(CommandContext<CommandSourceStack> context) {
		String name = StringArgumentType.getString(context, "pokemon");
		CommandSourceStack source = context.getSource();

		if (!PokemonRegistry.isRegistered(name)) {
			source.sendFailure(Component.literal("Pokemon '" + name + "' is not registered"));
			return 0;
		}

		// Assuming you have a PokemonData class with a modelFlags() method
		var data = PokemonRegistry.getPokemonData(name);
		StringBuilder sb = new StringBuilder();
		sb.append("Flags for ").append(name).append(": ");

		Map<?, ?> flags = data.modelFlags();
		for (Map.Entry<?, ?> entry : flags.entrySet()) {
			sb.append(entry.getKey().toString().toLowerCase())
					.append("=")
					.append(entry.getValue())
					.append(" ");
		}

		source.sendSuccess(() -> Component.literal(sb.toString()), false);
		return 1;
	}
}
