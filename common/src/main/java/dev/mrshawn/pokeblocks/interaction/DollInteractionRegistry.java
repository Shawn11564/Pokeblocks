package dev.mrshawn.pokeblocks.interaction;

import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for custom right-click (useItemOn) interactions on pokedoll blocks.
 *
 * Handlers are registered per pokemon name. When a player right-clicks a doll,
 * {@link #dispatch} walks the handlers registered for that pokemon in order and
 * returns the first non-null result. A null return means "not handled — fall through
 * to the next handler or the default block interaction (honeycomb waxing, etc.)".
 *
 * Handlers run server-side only; {@link PokedollBlock} already guards with
 * {@code level.isClientSide()} before calling dispatch.
 */
public final class DollInteractionRegistry {

    @FunctionalInterface
    public interface Handler {
        /**
         * @return the result to propagate from {@code useItemOn}, or {@code null} to pass
         *         through to the next handler / default block interaction.
         */
        @Nullable
        ItemInteractionResult handle(ItemStack heldStack, BlockState state, Level level, BlockPos pos,
                                     Player player, InteractionHand hand, PokedollBlockEntity doll);
    }

    private static final Map<String, List<Handler>> HANDLERS = new LinkedHashMap<>();

    /** Registers a handler that fires whenever a player right-clicks a doll of the given pokemon. */
    public static void register(String pokemon, Handler handler) {
        HANDLERS.computeIfAbsent(pokemon, k -> new ArrayList<>()).add(handler);
    }

    /**
     * Dispatches to registered handlers for the given pokemon. Returns the first non-null
     * result, or {@code null} if no handler consumed the interaction.
     */
    @Nullable
    public static ItemInteractionResult dispatch(String pokemon, ItemStack stack, BlockState state,
                                                  Level level, BlockPos pos, Player player,
                                                  InteractionHand hand, PokedollBlockEntity doll) {
        List<Handler> handlers = HANDLERS.get(pokemon);
        if (handlers == null) return null;
        for (Handler handler : handlers) {
            ItemInteractionResult result = handler.handle(stack, state, level, pos, player, hand, doll);
            if (result != null) return result;
        }
        return null;
    }

    private DollInteractionRegistry() {}
}
