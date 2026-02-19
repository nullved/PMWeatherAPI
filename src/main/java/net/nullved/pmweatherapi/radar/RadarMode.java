package net.nullved.pmweatherapi.radar;

import dev.protomanly.pmweather.PMWeather;
import dev.protomanly.pmweather.block.entity.RadarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.nullved.pmweatherapi.client.render.PixelRenderData;
import net.nullved.pmweatherapi.client.render.radar.RadarOverlays;
import net.nullved.pmweatherapi.config.PMWClientConfig;
import net.nullved.pmweatherapi.data.PMWExtras;
import net.nullved.pmweatherapi.util.ColorMap;
import net.nullved.pmweatherapi.util.ColorMaps;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.function.Function;

/**
 * A class representing a Radar Mode.
 * <br><br>
 * To register your own radar mode, you must call {@link #create} somewhere from your MAIN MOD CLASS CONSTRUCTOR.
 * This is to ensure the radar mode gets added to {@link PMWExtras#RADAR_MODE} so that it will be recognized by Minecraft as a valid property value
 * <br><br>
 * For each radar mode, you must define a function taking in a {@link PixelRenderData} and returning a {@link Color}.
 * This function is run for every pixel on the radar, so try to make it performant.
 * <br><br>
 * You can also define a custom dot color with {@link #create(ResourceLocation, Function, int)} (this supports transparency)
 * <br><br>
 * You may also be looking to overlay something on top of the radar instead.
 * If this is what you are looking to do, checkout {@link RadarOverlays} instead
 *
 * @since 0.14.15.6
 * @see RadarOverlays
 */
public class RadarMode implements StringRepresentable, Comparable<RadarMode> {
    private static final LinkedHashMap<ResourceLocation, RadarMode> MODES = new LinkedHashMap<>();
    private static boolean disableBaseRendering = false;

    /**
     * A "Null" Radar Mode mimicking Minecraft's missing texture.
     * This radar mode is not accessible by normal means, and if you see it that is an error.
     * @since 0.14.15.6
     */
    public static final RadarMode NULL = new RadarMode(ResourceLocation.parse("null"), prd -> {
        if ((prd.x() > 0 && prd.z() > 0) || (prd.x() <= 0 && prd.z() <= 0)) return 0xFFFF00FF;
        else return 0xFF000000;
    }, 0x00000000, true);

    /**
     * A Radar Mode that is a copy of PMWeather's Reflectivity
     * @since 0.14.15.6
     */
    public static final RadarMode REFLECTIVITY = createInternal(PMWeather.getPath("reflectivity"), prd -> {
        Holder<Biome> biome = prd.radarRenderData().blockEntity().getNearestBiome(new BlockPos((int) prd.wx(), prd.radarRenderData().blockEntity().getBlockPos().getY(), (int) prd.wz()));
//        if (prd.rdbz() < 5.0f && PMWClientConfig.transparentBackground) return 0x00000000;

        int color = biome != null
            ? ColorMaps.REFLECTIVITY.getWithBiome(prd.rdbz(), biome, prd.x(), prd.z())
            : ColorMaps.REFLECTIVITY.get(prd.rdbz());

        if (prd.rdbz() > 5.0F && !prd.radarRenderData().blockEntity().hasRangeUpgrade) {
            if (prd.temp() < 3.0F && prd.temp() > -1.0F) color = ColorMaps.MIXED_REFLECTIVITY.get(prd.rdbz());
            else if (prd.temp() <= -1.0F) color = ColorMaps.SNOW_REFLECTIVITY.get(prd.rdbz());
        }

        return color;
    });

    /**
     * A Radar Mode that is a copy of PMWeather's Velocity
     * @since 0.14.15.6
     */
    public static final RadarMode VELOCITY = createInternal(PMWeather.getPath("velocity"), prd -> {
        int velCol = prd.velocity() >= 0.0F ? ColorMaps.POSITIVE_VELOCITY.get(prd.velocity() / 1.75F) : ColorMaps.NEGATIVE_VELOCITY.get(-prd.velocity() / 1.75F);

        return ColorMap.lerp(Mth.clamp(Math.max(prd.rdbz(), (Mth.abs(prd.velocity() / 1.75F) - 18.0F) / 0.65F) / 12.0F, 0.0F, 1.0F), 0xFF000000, velCol);
    });

    /**
     * A Radar Mode that is a copy of PMWeather's IR
     * @since 0.15.0.0
     */
    public static final RadarMode IR = createInternal(PMWeather.getPath("ir"), prd -> {
        float rdbz = prd.rdbz();
        float ir = rdbz * 10.0F;

        if (rdbz > 10.0F) {
            ir = 100.0F + (rdbz - 10.0F) * 2.5F;
        }

        if (rdbz > 50.0F) {
            ir += (rdbz - 50.0F) * 5.0F;
        }

        return ColorMaps.IR.get(ir);
    });

    private final ResourceLocation id;
    private final Function<PixelRenderData, Integer> colorFunction;
    private final Integer dotColor;
    private final boolean custom;
    private RadarMode(ResourceLocation id, Function<PixelRenderData, Integer> colorFunction, Integer dotColor, boolean custom) {
        this.id = id;
        this.colorFunction = colorFunction;
        this.dotColor = dotColor;
        this.custom = custom;
    }

    public boolean isCustom() {
        return this.custom;
    }

    /**
     * Disables all rendering of pixels from any radar mode
     * @param disable Whether to disable rendering or not
     * @since 0.15.3.3
     */
    public static void disableBaseRendering(boolean disable) {
        disableBaseRendering = disable;
    }

    /**
     * Returns whether base rendering is disabled or not
     * @return Base rendering disable state
     * @since 0.15.3.3
     */
    public static boolean isBaseRenderingDisabled() {
        return disableBaseRendering;
    }

    /**
     * Create a new {@link RadarMode}
     * @param id The {@link ResourceLocation} of this radar mode
     * @param colorFunction The {@link Function} mapping a {@link PixelRenderData} to a {@link Color}. Runs for every pixel
     * @param renderDotColor The {@link Color} of the dot. Supports transparency
     * @return A new {@link RadarMode}
     * @since 0.14.15.6
     */
    public static RadarMode create(ResourceLocation id, Function<PixelRenderData, Integer> colorFunction, int renderDotColor) {
        return MODES.computeIfAbsent(id, nm -> new RadarMode(id, colorFunction, renderDotColor, true));
    }

    static RadarMode createInternal(ResourceLocation id, Function<PixelRenderData, Integer> colorFunction, int renderDotColor) {
        return MODES.computeIfAbsent(id, nm -> new RadarMode(id, colorFunction, renderDotColor, false));
    }

    /**
     * Create a new {@link RadarMode} with a red dot at the center.
     * To set a custom dot color, use {@link #create(ResourceLocation, Function, int)}
     * @param id The {@link ResourceLocation} of this radar mode
     * @param colorFunction The {@link Function} mapping a {@link PixelRenderData} to a {@link Color}. Runs for every pixel
     * @return A new {@link RadarMode}
     * @since 0.14.15.6
     */
    public static RadarMode create(ResourceLocation id, Function<PixelRenderData, Integer> colorFunction) {
        return create(id, colorFunction, 0xFFFF0000);
    }

    static RadarMode createInternal(ResourceLocation id, Function<PixelRenderData, Integer> colorFunction) {
        return MODES.computeIfAbsent(id, nm -> new RadarMode(id, colorFunction, 0xFFFF0000, false));
    }

    /**
     * Returns a {@link Collection} of {@link RadarMode}s
     * @return All Radar Modes
     * @since 0.14.15.6
     */
    public static Collection<RadarMode> values() {
        return MODES.values();
    }

    /**
     * Gets a specific {@link RadarMode} based on ID
     * @param id The {@link ResourceLocation} of the {@link RadarMode}
     * @return The associated {@link RadarMode}, or {@link #NULL} if not found
     * @since 0.14.15.6
     */
    public static RadarMode get(ResourceLocation id) {
        return MODES.getOrDefault(id, NULL);
    }

    /**
     * Gets a specific {@link RadarMode} based on ID in string format
     * @param id The ID of the {@link RadarMode}
     * @return The associated {@link RadarMode}, or {@link #NULL} if not found
     * @since 0.14.15.6
     */
    public static RadarMode get(String id) {
        RadarMode radarMode = MODES.get(ResourceLocation.tryParse(id.replaceFirst("_", ":")));
        if (radarMode != null) return radarMode;
        for (RadarMode mode: MODES.values()) {
            if (id.equals(mode.id.toString().replace(":", "_"))) return mode;
        }
        return NULL;
    }

    /**
     * Gets the next {@link RadarMode} in the cycle
     * @return The next {@link RadarMode}
     * @since 0.14.15.6
     */
    public RadarMode cycle() {
        RadarMode[] values = MODES.values().toArray(RadarMode[]::new);
        int idx = Arrays.binarySearch(values, this);
        return values[(idx + 1) % values.length];
    }

    /**
     * Gets the {@link ResourceLocation} of the radar mode
     * @return The {@link ResourceLocation} of this radar mode
     */
    public ResourceLocation getId() {
        return id;
    }

    /**
     * Gets the color of the dot at the center of the radar
     * @return The dot {@link Color}
     * @since 0.14.15.6
     */
    public int getDotColor() {
        return dotColor;
    }

    /**
     * Gets the color for a certain pixel. Applies the function from the {@link #create} method
     * @param pixelRenderData The {@link PixelRenderData} of the pixel
     * @return The {@link Color} to draw to the pixel
     * @since 0.14.15.6
     */
    public int getColorForPixel(PixelRenderData pixelRenderData) {
        return colorFunction.apply(pixelRenderData);
    }

    /**
     * Gets the serialized name of the {@link RadarMode}
     * @return The serialized name
     * @since 0.14.15.6
     */
    @Override
    public String getSerializedName() {
        return id.toString().replace(":", "_");
    }

    /**
     * Compares this {@link RadarMode} to the given {@link RadarMode}.
     * Used to efficiently search for the next {@link RadarMode} in the cycle
     * @param o The other {@link RadarMode}
     * @return 0 if same position in the cycle, 1 if later, -1 if earlier
     * @since 0.14.15.6
     */
    @Override
    public int compareTo(@NotNull RadarMode o) {
        ArrayList<ResourceLocation> modeKeys = new ArrayList<>(MODES.keySet());
        return Integer.compare(modeKeys.indexOf(id), modeKeys.indexOf(o.id));
    }
}
