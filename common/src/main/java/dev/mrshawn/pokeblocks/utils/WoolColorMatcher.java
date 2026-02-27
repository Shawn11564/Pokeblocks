package dev.mrshawn.pokeblocks.utils;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Maps an average RGB color to the closest Minecraft wool color(s).
 */
public class WoolColorMatcher {

    private record WoolEntry(DyeColor dye, Block wool, int r, int g, int b) {}

    // Approximate RGB values for each wool color
    private static final List<WoolEntry> WOOL_COLORS = List.of(
            new WoolEntry(DyeColor.WHITE, Blocks.WHITE_WOOL, 233, 236, 236),
            new WoolEntry(DyeColor.ORANGE, Blocks.ORANGE_WOOL, 241, 118, 19),
            new WoolEntry(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL, 189, 68, 179),
            new WoolEntry(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL, 58, 175, 217),
            new WoolEntry(DyeColor.YELLOW, Blocks.YELLOW_WOOL, 248, 198, 39),
            new WoolEntry(DyeColor.LIME, Blocks.LIME_WOOL, 112, 185, 25),
            new WoolEntry(DyeColor.PINK, Blocks.PINK_WOOL, 237, 141, 172),
            new WoolEntry(DyeColor.GRAY, Blocks.GRAY_WOOL, 62, 68, 71),
            new WoolEntry(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL, 142, 142, 134),
            new WoolEntry(DyeColor.CYAN, Blocks.CYAN_WOOL, 21, 137, 145),
            new WoolEntry(DyeColor.PURPLE, Blocks.PURPLE_WOOL, 121, 42, 172),
            new WoolEntry(DyeColor.BLUE, Blocks.BLUE_WOOL, 53, 57, 157),
            new WoolEntry(DyeColor.BROWN, Blocks.BROWN_WOOL, 114, 71, 40),
            new WoolEntry(DyeColor.GREEN, Blocks.GREEN_WOOL, 84, 109, 27),
            new WoolEntry(DyeColor.RED, Blocks.RED_WOOL, 160, 39, 34),
            new WoolEntry(DyeColor.BLACK, Blocks.BLACK_WOOL, 20, 21, 25)
    );

    /**
     * Returns the closest wool block to the given RGB color.
     */
    public static Block getClosestWool(float r, float g, float b) {
        int ri = (int) (r * 255);
        int gi = (int) (g * 255);
        int bi = (int) (b * 255);

        Block closest = Blocks.WHITE_WOOL;
        double bestDist = Double.MAX_VALUE;

        for (WoolEntry entry : WOOL_COLORS) {
            double dist = colorDistanceSq(ri, gi, bi, entry.r, entry.g, entry.b);
            if (dist < bestDist) {
                bestDist = dist;
                closest = entry.wool;
            }
        }

        return closest;
    }

    /**
     * Returns the top N closest wool blocks (no duplicates), ordered by closeness.
     */
    public static List<Block> getClosestWools(float r, float g, float b, int count) {
        int ri = (int) (r * 255);
        int gi = (int) (g * 255);
        int bi = (int) (b * 255);

        List<WoolEntry> sorted = new ArrayList<>(WOOL_COLORS);
        sorted.sort(Comparator.comparingDouble(e -> colorDistanceSq(ri, gi, bi, e.r, e.g, e.b)));

        List<Block> result = new ArrayList<>();
        for (WoolEntry entry : sorted) {
            if (result.size() >= count) break;
            result.add(entry.wool);
        }

        return result;
    }

    /**
     * Squared Euclidean distance in RGB space — good enough for matching
     * and avoids the sqrt cost.
     */
    private static double colorDistanceSq(int r1, int g1, int b1, int r2, int g2, int b2) {
        int dr = r1 - r2;
        int dg = g1 - g2;
        int db = b1 - b2;
        // Weight green more heavily since human eyes are most sensitive to it
        return 2.0 * dr * dr + 4.0 * dg * dg + 3.0 * db * db;
    }
}