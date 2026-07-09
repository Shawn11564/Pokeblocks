package dev.mrshawn.pokeblocks.phone;

/**
 * The pure rules of the dig quest, split from {@link DigQuestManager} (whose static initializers
 * touch registry-backed classes) so they stay unit-testable without a Minecraft bootstrap.
 */
public final class DigQuestRules {

	private DigQuestRules() {}

	/**
	 * The doll-guarantee rule: the doll surfaces on the pre-rolled attempt, or on the last
	 * remaining site if the sites somehow ran out first (external removals).
	 */
	public static boolean shouldFindDoll(int attemptsAfterThisDig, int targetAttempt, int sitesRemainingAfterThisDig) {
		return attemptsAfterThisDig >= targetAttempt || sitesRemainingAfterThisDig == 0;
	}
}
