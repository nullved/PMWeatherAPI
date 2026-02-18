package net.nullved.pmweatherapi.util;

import dev.protomanly.pmweather.block.entity.RadarBlockEntity;
import dev.protomanly.pmweather.util.ColorTables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.nullved.pmweatherapi.config.PMWClientConfig;

import java.awt.Color;
import java.util.*;

/**
 * A builder-based representation of {@link ColorTables}.
 * Should hopefully be able to produce identical results.
 * <br><br>
 * You can create a new {@link ColorMap} builder with {@link ColorMap.Builder#of(int)}.
 * Use {@link Builder#addPoint(int, float)} to add a new point to lerp between.
 * Use {@link Builder#override(int, float)} to add a new point to override the color at.
 * @since 0.14.15.6
 */
public class ColorMap {
    private final boolean overrideModeGreater;
    private final NavigableMap<Float, LerpSegment> segments;
    private final NavigableMap<Float, Integer> overridePoints;
    private final int base;

    private final float min, max, firstThreshold;
    private int[] lookup;
    private float resolution;

    private ColorMap(int base, boolean overrideModeGreater, List<LerpSegment> segments, NavigableMap<Float, Integer> overridePoints, float resolution) {
        this.min = Math.round(segments.getFirst().start / resolution) * resolution;
        this.max = Math.round(segments.getLast().end / resolution) * resolution;
        this.firstThreshold = segments.getFirst().end;

        this.base = base;
        this.overrideModeGreater = overrideModeGreater;
        this.segments = new TreeMap<>();
        for (LerpSegment seg : segments) {
            this.segments.put(seg.start, seg);
        }
        this.overridePoints = overridePoints;

        recomputeLookups(resolution);
    }

    /**
     * Gets the smallest value a color is defined for
     * @return The minimum value
     * @since 0.14.15.6
     */
    public float minValue() {
        return min;
    }

    /**
     * Gets the largest value a color is defined for
     * @return The maximum value
     * @since 0.14.15.6
     */
    public float maxValue() {
        return max;
    }

    /**
     * Recomputes the lookup table with the given resolution.
     * @param resolution The new resolution
     * @since 0.14.16.1
     */
    public void recomputeLookups(float resolution) {
        this.resolution = resolution;
        int size = (int) (((max - min) / resolution) + 1);
        this.lookup = new int[size];

        for (int i = 0; i < size; i++) {
            float val = min + i * resolution;
            lookup[i] = getAccurate(val);
        }
    }

    /**
     * Retrieves the color value using the closest value from the lookup table.
     * If you need the accurate value, use {@link #getAccurate(float)} instead
     * @param val The value to get a color for
     * @return The approximate color for this value
     * @since 0.14.16.1
     * @see #getAccurate(float)
     */
    public int get(float val) {
        float newVal = Math.round(val / resolution) * resolution;
        if (newVal <= min) return lookup[0];
        if (newVal >= max) return lookup[lookup.length - 1];
        int idx = (int) ((newVal - min) / resolution);
        return lookup[idx];
    }

    /**
     * Retrieves the color value using a color derived from the biome
     * @param val The value to get a color for
     * @param biome The biome to derive the starting color from
     * @param wx The world x position (for grass color checks)
     * @param wz The world z position (for grass color checks)
     * @return The approximate color for this value
     * @since 0.15.0.0
     */
    public int getWithBiome(float val, Holder<Biome> biome, double wx, double wz) {
        int startColor;
        String rn = biome.getRegisteredName().toLowerCase();
        if (rn.contains("ocean") || rn.contains("river")) startColor = 0xFF000000 | biome.value().getWaterColor();
        else if (rn.contains("beach") || rn.contains("desert")) startColor = 0xFFE3C696;
        else if (rn.contains("badlands")) startColor = 0xFFD66F2A;
        else startColor = 0xFF000000 | biome.value().getGrassColor(wx, wz);

        if (PMWClientConfig.darkenBiomesOnRadar) startColor = 0xFF000000 | ColorMap.lerp(0.5F, startColor, 0xFF000000);

        if (val < firstThreshold) {
            return lerp(Math.clamp(val / (firstThreshold - min), 0.0F, 1.0F), startColor, segments.firstEntry().getValue().to);
        } else return get(val);
    }

    public int getWithTerrainMap(float val, RadarBlockEntity rbe, double x, double z) {
        if (val < firstThreshold) {
            long longID = (long) (x + resolution + 1) + (long) (z + resolution + 1) * ((long) resolution * 2L + 1L);
            return lerp(Math.clamp(val / (firstThreshold - min), 0.0F, 1.0F), rbe.terrainMap.getOrDefault(longID, Color.BLACK).getRGB(), segments.firstEntry().getValue().to);
        } else return get(val);
    }

    /**
     * Gets the {@link Color} for the specific value.
     * <br>
     * This method is <strong>SLOWER</strong> and oftentimes the same as {@link #get(float)}.
     * Use that instead if you only need the approximate value
     * @param val The value to get the {@link Color} of
     * @return The {@link Color} for the given value
     * @since 0.14.15.6
     * @see #get(float)
     */
    public int getAccurate(float val) {
        int currentColor = base;

        Map.Entry<Float, Integer> override = overridePoints.floorEntry(val);
        if (override != null) {
            currentColor = override.getValue();
        }

        Map.Entry<Float, LerpSegment> entry = segments.floorEntry(val);
        if (entry != null) {
            LerpSegment seg = entry.getValue();
            if (overrideModeGreater ? val <= seg.end : val < seg.end) {
                float delta = (val - seg.start) / (seg.end - seg.start);
                delta = delta > 1 ? 1 : delta < 0 ? 0 : delta;
                return lerp(delta, seg.from, seg.to);
            }
        }

        return currentColor;
    }

    public static int lerpBlackTransparent(float delta, int c1, int c2) {
        if (c2 == 0xFF000000 && PMWClientConfig.transparentBackground) c2 = 0x00000000;
        return lerp(delta, c1, c2);
    }

    /**
     * Lerps between two colors
     * @param delta The t-value from 0 to 1
     * @param c1 The first {@link Color}
     * @param c2 The second {@link Color}
     * @return A {@link Color} lerped between c1 and c2
     * @since 0.14.15.6
     */
    public static int lerp(float delta, int c1, int c2) {
        return FastColor.ARGB32.lerp(delta, c1, c2);
//
//        int a1 = (c1 >> 24) & 0xFF;
//        int r1 = (c1 >> 16) & 0xFF;
//        int g1 = (c1 >> 8) & 0xFF;
//        int b1 = c1 & 0xFF;
//
//        int a2 = (c2 >> 24) & 0xFF;
//        int r2 = (c2 >> 16) & 0xFF;
//        int g2 = (c2 >> 8) & 0xFF;
//        int b2 = c2 & 0xFF;
//
//        int a = (int) (a1 + delta * (a2 - a1));
//        int r = (int) (r1 + delta * (r2 - r1));
//        int g = (int) (g1 + delta * (g2 - g1));
//        int b = (int) (b1 + delta * (b2 - b1));
//        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Represents a Lerp Segment
     * @param start The starting value
     * @param from The starting {@link Color}
     * @param end The ending value
     * @param to The ending {@link Color}
     * @since 0.14.15.6
     */
    public record LerpSegment(float start, int from, float end, int to) {}

    /**
     * A Builder pattern for creating a {@link ColorMap}
     * @since 0.14.15.6
     */
    public static class Builder {
        private final List<LerpSegment> segments = new ArrayList<>();
        private final NavigableMap<Float, Integer> overridePoints = new TreeMap<>();
        private final Integer base;
        private boolean overrideModeGreater = false;
        private float lastThreshold = Float.NEGATIVE_INFINITY, resolution = 0.1F;
        private Integer lastColor;

        private Builder(Integer base) {
            this.base = base;
            this.lastColor = base;
        }

        /**
         * Creates a new {@link Builder} with the given {@link Color} as the base
         * @param base The base {@link Color}
         * @return The created {@link Builder}
         * @since 0.14.15.6
         */
        public static Builder of(int base) {
            return new Builder(base);
        }

        /**
         * Creates a new {@link ColorMap.Builder} that has a default {@link Color#BLACK} base.
         * This method has no effect unless you use {@link ColorMap#getWithBiome(float, Holder, double, double)}
         * @return The created {@link ColorMap.Builder}
         * @since 0.15.0.0
         */
        public static Builder biome() {
            return new Builder(0xFF000000);
        }

        /**
         * Sets the step size between each value in the lookup table.
         * A value too small may be storing the same color multiple times!
         * @param resolution The resolution of the lookup table. Default 0.1F
         * @return This {@link Builder}
         * @since 0.14.16.1
         */
        public Builder lookupResolution(float resolution) {
            this.resolution = resolution;
            return this;
        }

        /**
         * Use in cases where you want overrides to only apply when the value is greater, but not equal to the threshold.
         * Used in {@link ColorMaps#POSITIVE_VELOCITY} and {@link ColorMaps#NEGATIVE_VELOCITY}
         * @return This {@link Builder}
         * @since 0.14.15.6
         */
        public Builder overrideModeGreater() {
            this.overrideModeGreater = true;
            return this;
        }

        /**
         * Adds a point to lerp between.
         * @param color The color at the end of the last {@link LerpSegment} and the start of this {@link LerpSegment}
         * @param threshold The threshold value
         * @return This {@link Builder}
         * @since 0.14.15.6
         */
        public Builder addPoint(int color, float threshold) {
            if (lastThreshold != Float.NEGATIVE_INFINITY) {
                segments.add(new LerpSegment(lastThreshold, lastColor, threshold, color));
            }
            lastThreshold = threshold;
            lastColor = color;
            return this;
        }

        /**
         * Adds an override point. The final {@link Color} will get overwritten once it reaches the threshold value specified
         * @param color The {@link Color} to override the final {@link Color} with
         * @param threshold The threshold value
         * @return This {@link Builder}
         * @since 0.14.15.6
         */
        public Builder override(int color, float threshold) {
            overridePoints.put(threshold, color);
            lastThreshold = threshold;
            lastColor = color;
            return this;
        }

        /**
         * Builds this {@link Builder} into a proper {@link ColorMap}
         * @param finalColor The max {@link Color}
         * @param finalThreshold The max threshold value
         * @return A completed {@link ColorMap}
         * @since 0.14.15.6
         */
        public ColorMap build(int finalColor, float finalThreshold) {
            if (!segments.isEmpty()) {
                LerpSegment first = segments.getFirst();
                if (first.start > 0.0F) {
                    segments.add(0, new LerpSegment(0.0F, base, first.start, first.from));
                }
            }

            if (overrideModeGreater) overridePoints.put(finalThreshold + 0.0001F, finalColor);
            else overridePoints.put(finalThreshold, finalColor);

            segments.add(new LerpSegment(lastThreshold, lastColor, finalThreshold, finalColor));
            return new ColorMap(base, overrideModeGreater, segments, overridePoints, resolution);
        }
    }
}