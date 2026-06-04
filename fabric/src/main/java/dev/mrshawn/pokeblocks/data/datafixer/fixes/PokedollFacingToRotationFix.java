package dev.mrshawn.pokeblocks.data.datafixer.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

/**
 * Converts a placed pokedoll's legacy 4-way {@code facing} block-state property into the new 16-step
 * {@code rotation} property (matching vanilla signs/banners), so dolls saved before the rotation change
 * keep their orientation instead of snapping back to north.
 * <p>
 * This runs <b>after</b> the v2 {@code BlockRenameFix} has already collapsed the per-variant ids into the
 * single {@code pokeblocks:pokedoll} block, so it only needs to match that one name. Older dolls — whether
 * from the Fabric build (no mod data version → migrated through the whole chain) or placed in an early
 * build of this mod (saved at data version 2) — both arrive here still carrying {@code facing}.
 *
 * <h2>Mapping</h2>
 * The segment is chosen so the migrated doll renders at the exact angle GeckoLib's old 4-direction
 * {@code rotateBlock} produced, given this mod's renderer applies {@code -RotationSegment.convertToDegrees}:
 * <pre>
 *   north → 0   (0°)     east → 4   (270°)
 *   south → 8   (180°)   west → 12  (90°)
 * </pre>
 * Note this is deliberately <i>not</i> {@code RotationSegment.convertToSegment(Direction)} — that uses
 * vanilla's standing-sign convention, which is offset 180° from how the doll model is oriented.
 */
public class PokedollFacingToRotationFix extends DataFix {
    private static final String POKEDOLL_NAME = PokeblocksCommon.MOD_ID + ":" + ModSettings.DOLL_ID;
    private static final String FACING_KEY = "facing";
    private static final String ROTATION_KEY = "rotation";

    private final String name;

    public PokedollFacingToRotationFix(Schema outputSchema, String name) {
        super(outputSchema, false);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(
                this.name,
                this.getInputSchema().getType(References.BLOCK_STATE),
                typed -> typed.update(DSL.remainderFinder(), PokedollFacingToRotationFix::fixBlockState)
        );
    }

    private static <T> Dynamic<T> fixBlockState(Dynamic<T> blockState) {
        boolean isPokedoll = blockState.get("Name").asString().result().filter(POKEDOLL_NAME::equals).isPresent();
        if (!isPokedoll) {
            return blockState;
        }

        return blockState.update("Properties", properties -> {
            Optional<String> facing = properties.get(FACING_KEY).asString().result();
            if (facing.isEmpty()) {
                return properties; // already migrated, or never had a facing to convert
            }

            int segment = switch (facing.get()) {
                case "east" -> 4;
                case "south" -> 8;
                case "west" -> 12;
                default -> 0; // north (and any unexpected value) → no rotation
            };

            return properties.remove(FACING_KEY)
                    .set(ROTATION_KEY, properties.createString(Integer.toString(segment)));
        });
    }
}
