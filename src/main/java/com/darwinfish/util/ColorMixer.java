package com.darwinfish.util;

import net.minecraft.world.item.DyeColor;

/**
 * Utilities for mixing DyeColor values using physically-based RMS blending
 * (not a naive numeric average), then snapping to the nearest DyeColor.
 */
public final class ColorMixer {

    private ColorMixer() {}

    /**
     * Mixes two DyeColors using Root-Mean-Square blending of their RGB
     * components, then returns the DyeColor whose texture-diffuse color is
     * closest to the result in Euclidean RGB space.
     *
     * <p>This method intentionally does NOT just average the ordinal indices —
     * it operates on actual color values, so e.g. mixing white and black gives
     * a mid-grey-toned color rather than an arbitrary middle enum constant.
     *
     * <p>If both parents have the same color the result is that same color.
     */
    public static DyeColor mixDyeColors(DyeColor a, DyeColor b) {
        if (a == b) return a;

        int ca = a.getTextureDiffuseColor();
        int cb = b.getTextureDiffuseColor();

        int r = rms(red(ca), red(cb));
        int g = rms(green(ca), green(cb));
        int bv = rms(blue(ca), blue(cb));

        return nearestDyeColor(r, g, bv);
    }

    // ---- Private helpers -------------------------------------------------------

    private static int red(int rgb)   { return (rgb >> 16) & 0xFF; }
    private static int green(int rgb) { return (rgb >> 8)  & 0xFF; }
    private static int blue(int rgb)  { return  rgb        & 0xFF; }

    /** Root-mean-square of two 0-255 channel values. */
    private static int rms(int v1, int v2) {
        return (int) Math.round(Math.sqrt(((double) v1 * v1 + (double) v2 * v2) / 2.0));
    }

    /** Finds the DyeColor whose getTextureDiffuseColor() is closest in RGB space. */
    private static DyeColor nearestDyeColor(int r, int g, int b) {
        DyeColor best = DyeColor.WHITE;
        double bestDist = Double.MAX_VALUE;

        for (DyeColor color : DyeColor.values()) {
            int c = color.getTextureDiffuseColor();
            double dist = dist3(r, g, b, red(c), green(c), blue(c));
            if (dist < bestDist) {
                bestDist = dist;
                best = color;
            }
        }
        return best;
    }

    private static double dist3(int r1, int g1, int b1, int r2, int g2, int b2) {
        double dr = r1 - r2;
        double dg = g1 - g2;
        double db = b1 - b2;
        return dr * dr + dg * dg + db * db; // squared distance is fine for comparison
    }

    /**
     * Returns an approximate mixed RGB integer (for rendering hints).
     */
    public static int mixRgb(int rgb1, int rgb2) {
        int r = rms(red(rgb1), red(rgb2));
        int g = rms(green(rgb1), green(rgb2));
        int b = rms(blue(rgb1), blue(rgb2));
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
