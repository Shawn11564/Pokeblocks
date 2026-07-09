package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.client.phone.PhoneClientHooks;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.phone.DigQuestManager;
import dev.mrshawn.pokeblocks.phone.DigQuestStore;
import dev.mrshawn.pokeblocks.phone.PhoneCalls;
import dev.mrshawn.pokeblocks.registry.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * The Pokedoll Phone. While it sits in a player's inventory it occasionally starts RINGING: the
 * item switches to its lit texture (see the {@code pokeblocks:ringing} model predicate) and buzzes
 * audibly for the configured ring window. Right-clicking a ringing phone picks up — a random doll
 * is on the line asking the player to dig up its buried favorite doll — and the client shows the
 * accept/hang-up call screen. Accepting starts the {@link DigQuestManager} dig-site quest.
 * <p>
 * All call state is per-stack inside the vanilla {@code minecraft:custom_data} component (same
 * approach as {@link dev.mrshawn.pokeblocks.item.ThrowableDolls}), written only on transitions
 * (ring start / pickup / end) so the stack isn't re-synced every tick. The lit texture rides the
 * vanilla {@code custom_model_data} predicate (set to {@value #MODEL_DATA_RINGING} while a call is
 * active — see {@code models/item/pokedoll_phone.json}), so no client-side property registration
 * is needed on any loader:
 * <ul>
 *   <li>{@code phone_ring_until} — game time the current ring (or pickup decision window) expires</li>
 *   <li>{@code phone_caller} — variant key of the doll on the line</li>
 *   <li>{@code phone_answered} — the ring was picked up; awaiting the accept/hang-up answer</li>
 *   <li>{@code phone_next_ring_at} — earliest game time the phone may ring again</li>
 * </ul>
 */
public class PokedollPhoneItem extends Item {

	public static final String KEY_RING_UNTIL = "phone_ring_until";
	public static final String KEY_CALLER = "phone_caller";
	public static final String KEY_ANSWERED = "phone_answered";
	public static final String KEY_NEXT_RING_AT = "phone_next_ring_at";

	/** {@code custom_model_data} value that selects the lit "on" model while a call is active. */
	public static final int MODEL_DATA_RINGING = 1;

	/** Fresh phones wait this long before they may ring at all (no ringing 5s after crafting). */
	private static final long INITIAL_GRACE_TICKS = 20 * 60;
	/** Floor between two calls on the same phone, on top of the random ring roll. */
	private static final long POST_CALL_COOLDOWN_TICKS = 20 * 60 * 2;
	/** How long a picked-up call waits for the accept/hang-up answer before giving up. */
	private static final long DECISION_WINDOW_TICKS = 20 * 60;

	/** Ring state is evaluated once a second (matches the compendium inventory-tick cadence). */
	private static final int CHECK_INTERVAL_TICKS = 20;

	public PokedollPhoneItem(Properties properties) {
		super(properties);
	}

	// ------------------------------------------------------------------
	// Per-stack call state
	// ------------------------------------------------------------------

	private static CompoundTag tagOf(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? new CompoundTag() : data.copyTag();
	}

	/** True while the phone should show its lit texture: ringing, or picked up and in the call. */
	public static boolean isRingingOrInCall(ItemStack stack, long gameTime) {
		return tagOf(stack).getLong(KEY_RING_UNTIL) > gameTime;
	}

	private static boolean isUnansweredRing(CompoundTag tag, long gameTime) {
		return tag.getLong(KEY_RING_UNTIL) > gameTime && !tag.getBoolean(KEY_ANSWERED);
	}

	/** The variant key of the doll on the line, or null when no call is active. */
	public static String getCaller(ItemStack stack) {
		String caller = tagOf(stack).getString(KEY_CALLER);
		return caller.isBlank() ? null : caller;
	}

	/** Ends any active call, unlights the texture and starts the between-calls cooldown. */
	public static void endCall(ItemStack stack, long gameTime) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.remove(KEY_RING_UNTIL);
			tag.remove(KEY_CALLER);
			tag.remove(KEY_ANSWERED);
			tag.putLong(KEY_NEXT_RING_AT, gameTime + POST_CALL_COOLDOWN_TICKS);
		});
		stack.remove(DataComponents.CUSTOM_MODEL_DATA);
	}

	/** The phone in the player's inventory whose call was picked up and awaits an answer, if any. */
	public static ItemStack findAwaitingDecision(ServerPlayer player) {
		long now = player.level().getGameTime();
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack stack = player.getInventory().getItem(slot);
			if (!(stack.getItem() instanceof PokedollPhoneItem)) continue;
			CompoundTag tag = tagOf(stack);
			if (tag.getBoolean(KEY_ANSWERED) && tag.getLong(KEY_RING_UNTIL) > now) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	/** Whether the player is carrying any Pokedoll Phone at all (used by the force-ring command). */
	public static boolean hasPhone(ServerPlayer player) {
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			if (player.getInventory().getItem(slot).getItem() instanceof PokedollPhoneItem) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The first phone in the player's inventory that isn't already ringing or mid-call — the one the
	 * force-ring command should start. {@link ItemStack#EMPTY} when every phone is busy (or none exists).
	 */
	public static ItemStack findIdlePhone(ServerPlayer player) {
		long now = player.level().getGameTime();
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack stack = player.getInventory().getItem(slot);
			if (stack.getItem() instanceof PokedollPhoneItem && !isRingingOrInCall(stack, now)) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	/** True when any phone in the inventory is mid-call (keeps two phones from ringing at once). */
	private static boolean hasBusyPhone(Player player, long gameTime) {
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack stack = player.getInventory().getItem(slot);
			if (stack.getItem() instanceof PokedollPhoneItem && isRingingOrInCall(stack, gameTime)) {
				return true;
			}
		}
		return false;
	}

	// ------------------------------------------------------------------
	// Ring lifecycle
	// ------------------------------------------------------------------

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof ServerPlayer player)) return;
		if (entity.tickCount % CHECK_INTERVAL_TICKS != 0) return;

		long now = level.getGameTime();
		CompoundTag tag = tagOf(stack);
		long ringUntil = tag.getLong(KEY_RING_UNTIL);

		if (ringUntil > 0) {
			if (now >= ringUntil) {
				boolean answered = tag.getBoolean(KEY_ANSWERED);
				endCall(stack, now);
				if (!answered) {
					player.displayClientMessage(Component.translatable("message.pokeblocks.phone.missed_call"), true);
				}
			} else if (!tag.getBoolean(KEY_ANSWERED)) {
				// Buzz once per check while ringing (the clip is ~1s, so it reads as continuous).
				playBuzz(player);
			}
			return;
		}

		// Not ringing — see whether a new call comes in. (An active ring above still expires
		// normally when the feature is disabled mid-call; only NEW calls are gated.)
		if (!PokeblocksConfig.isPhoneEnabled()) return;
		long nextRingAt = tag.getLong(KEY_NEXT_RING_AT);
		if (nextRingAt == 0) {
			CustomData.update(DataComponents.CUSTOM_DATA, stack,
					t -> t.putLong(KEY_NEXT_RING_AT, now + INITIAL_GRACE_TICKS));
			return;
		}
		if (now < nextRingAt) return;
		if (!PhoneCalls.canSendTo(player)) return; // client can't show the call screen — stay quiet
		if (DigQuestManager.hasQuest(player)) return;
		if (hasBusyPhone(player, now)) return;

		// One roll per second; averages one call per configured interval.
		double chancePerCheck = 1.0 / Math.max(1, PokeblocksConfig.getPhoneAverageCallIntervalMinutes() * 60);
		if (level.getRandom().nextDouble() >= chancePerCheck) return;

		String caller = PhoneCalls.pickCallerKey(level.getRandom());
		if (caller == null) return;

		beginRing(player, stack, caller, now);
	}

	/**
	 * Starts an incoming call on this phone right now: lights the texture, buzzes and shows the
	 * "ringing" prompt. Shared by the random {@link #inventoryTick} roll and the admin force-ring
	 * command ({@code /pokeblocks phonering}). Callers must ensure the phone isn't already mid-call
	 * (see {@link #isRingingOrInCall}) — this unconditionally overwrites any existing call state.
	 *
	 * @param caller the variant key of the doll on the line (non-blank)
	 */
	public static void beginRing(ServerPlayer player, ItemStack stack, String caller, long gameTime) {
		long ringTicks = 20L * PokeblocksConfig.getPhoneRingSeconds();
		CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
			t.putLong(KEY_RING_UNTIL, gameTime + ringTicks);
			t.putString(KEY_CALLER, caller);
			t.remove(KEY_ANSWERED);
		});
		stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(MODEL_DATA_RINGING));
		playBuzz(player);
		player.displayClientMessage(Component.translatable("message.pokeblocks.phone.ringing"), true);
	}

	/**
	 * Plays the ring buzz anchored to the player (an entity-bound sound) rather than at a fixed world
	 * point, so it always comes from the player's own position — a fixed-coordinate sound is left
	 * behind while the player moves and briefly seems to come from off to the side.
	 */
	private static void playBuzz(ServerPlayer player) {
		player.level().playSound(null, player, SoundRegistry.PHONE_BUZZ.get(), SoundSource.PLAYERS, 0.8f, 1.0f);
	}

	// ------------------------------------------------------------------
	// Picking up
	// ------------------------------------------------------------------

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		CompoundTag tag = tagOf(stack);
		long now = level.getGameTime();

		if (tag.getLong(KEY_RING_UNTIL) > now) {
			String caller = tag.getString(KEY_CALLER);
			if (level.isClientSide) {
				// Guarded client-only hook (same pattern as CompendiumItem -> CompendiumClientHooks).
				PhoneClientHooks.openCallScreen(caller);
			} else if (isUnansweredRing(tag, now)) {
				// Picked up: stop the buzzing and hold the line while the player decides.
				CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
					t.putBoolean(KEY_ANSWERED, true);
					t.putLong(KEY_RING_UNTIL, now + DECISION_WINDOW_TICKS);
				});
				level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.6f, 1.4f);
			}
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
		}

		if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
			DigQuestStore.Quest quest = DigQuestManager.getQuest(serverPlayer);
			if (quest != null) {
				serverPlayer.displayClientMessage(Component.translatable("message.pokeblocks.phone.reminder",
						PhoneCalls.dollName(quest.callerKey()), quest.sites().size()), false);
			} else {
				serverPlayer.displayClientMessage(Component.translatable("message.pokeblocks.phone.no_calls"), true);
			}
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.pokeblocks.pokedoll_phone")
				.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
		tooltip.add(Component.translatable("tooltip.pokeblocks.pokedoll_phone.detail")
				.withStyle(ChatFormatting.DARK_GRAY));
	}
}
