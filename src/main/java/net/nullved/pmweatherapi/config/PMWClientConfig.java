package net.nullved.pmweatherapi.config;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.render.debug.DebugMetarsOverlay;
import net.nullved.pmweatherapi.client.render.debug.DebugRadarsOverlay;
import net.nullved.pmweatherapi.client.render.IDOverlay;
import net.nullved.pmweatherapi.client.render.debug.DebugStormsOverlay;
import net.nullved.pmweatherapi.client.render.debug.DebugWSRSOverlay;
import net.nullved.pmweatherapi.client.render.radar.RadarOverlays;
import net.nullved.pmweatherapi.util.ColorMaps;

@EventBusSubscriber(modid = PMWeatherAPI.MODID, value = Dist.CLIENT)
public class PMWClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // RADAR RENDERING OPTIONS
    private static final ModConfigSpec.BooleanValue USE_ORIGINAL_PMWEATHER_RENDERING;
    public static boolean useOriginalPMWeatherRendering;
    private static final ModConfigSpec.BooleanValue USE_ORIGINAL_PMWEATHER_COLORS;
    public static boolean useOriginalPMWeatherColors;
    private static final ModConfigSpec.BooleanValue DARKEN_BIOMES_ON_RADAR;
    public static boolean darkenBiomesOnRadar;
    private static final ModConfigSpec.BooleanValue DISABLE_CUSTOM_RADAR_MODE_RENDERING;
    public static boolean disableCustomRadarModeRendering;

    // DEBUGGING
    private static final ModConfigSpec.BooleanValue SHOW_RADAR_MODE_ID;
    public static boolean showRadarModeId;
    private static final ModConfigSpec.EnumValue<RadarModeIDSide> RADAR_MODE_ID_SIDE;
    public static RadarModeIDSide radarModeIDSide;
    private static final ModConfigSpec.BooleanValue DISABLE_OVERLAYS_WHEN_DEBUGGING;
    public static boolean disableOverlaysWhenDebugging;
    private static final ModConfigSpec.BooleanValue DEBUG_RADARS;
    public static boolean debugRadars;
    private static final ModConfigSpec.BooleanValue DEBUG_WSRS;
    public static boolean debugWSRS;
    private static final ModConfigSpec.BooleanValue DEBUG_METARS;
    public static boolean debugMetars;
    private static final ModConfigSpec.BooleanValue DEBUG_STORMS;
    public static boolean debugStorms;
    private static final ModConfigSpec.BooleanValue DEBUG;
    public static boolean debug;

    // EXPERIMENTAL
    private static final ModConfigSpec.BooleanValue TRANSPARENT_BACKGROUND;
    public static boolean transparentBackground;
    private static final ModConfigSpec.BooleanValue LOWER_RADAR;
    public static boolean lowerRadar;
    public static final ModConfigSpec SPEC;

    @SubscribeEvent
    private static void onLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC && !(event instanceof ModConfigEvent.Unloading)) {
            PMWeatherAPI.LOGGER.info("Loading Client PMWeatherAPI Configs");
            useOriginalPMWeatherRendering = USE_ORIGINAL_PMWEATHER_RENDERING.getAsBoolean();
            useOriginalPMWeatherColors = USE_ORIGINAL_PMWEATHER_COLORS.getAsBoolean();
            darkenBiomesOnRadar = DARKEN_BIOMES_ON_RADAR.getAsBoolean();
            disableCustomRadarModeRendering = DISABLE_CUSTOM_RADAR_MODE_RENDERING.getAsBoolean();
            showRadarModeId = SHOW_RADAR_MODE_ID.getAsBoolean();
            radarModeIDSide = RADAR_MODE_ID_SIDE.get();
            disableOverlaysWhenDebugging = DISABLE_OVERLAYS_WHEN_DEBUGGING.getAsBoolean();
            debugRadars = DEBUG_RADARS.getAsBoolean();
            debugWSRS = DEBUG_WSRS.getAsBoolean();
            debugMetars = DEBUG_METARS.getAsBoolean();
            debugStorms = DEBUG_STORMS.getAsBoolean();
            debug = DEBUG.getAsBoolean();
            transparentBackground = TRANSPARENT_BACKGROUND.getAsBoolean();
            lowerRadar = LOWER_RADAR.getAsBoolean();
        }

        if (event instanceof ModConfigEvent.Reloading) ColorMaps.recomputeAll();

        if (showRadarModeId) RadarOverlays.registerOverlay(IDOverlay.INSTANCE);
        else RadarOverlays.unregisterOverlay(IDOverlay.INSTANCE);

        if (debugRadars) RadarOverlays.registerOverlay(DebugRadarsOverlay.INSTANCE);
        else RadarOverlays.unregisterOverlay(DebugRadarsOverlay.INSTANCE);

        if (debugWSRS) RadarOverlays.registerOverlay(DebugWSRSOverlay.INSTANCE);
        else RadarOverlays.unregisterOverlay(DebugWSRSOverlay.INSTANCE);

        if (debugMetars) RadarOverlays.registerOverlay(DebugMetarsOverlay.INSTANCE);
        else RadarOverlays.unregisterOverlay(DebugMetarsOverlay.INSTANCE);

        if (debugStorms) RadarOverlays.registerOverlay(DebugStormsOverlay.INSTANCE);
        else RadarOverlays.unregisterOverlay(DebugStormsOverlay.INSTANCE);
    }

    static {
        USE_ORIGINAL_PMWEATHER_RENDERING = BUILDER.push("radar").comment("Disables all custom rendering and optimizations from PMWeatherAPI! Also disables all overlays and custom radar modes!").define("use_original_pmweather_rendering", false);
        USE_ORIGINAL_PMWEATHER_COLORS = BUILDER.comment("Use's PMWeather's ColorTables instead of ColorMaps. You may or may not see a loss of performance!").define("use_original_pmweather_colors", false);
        DISABLE_CUSTOM_RADAR_MODE_RENDERING = BUILDER.comment("Disables custom radar mode rendering").define("disable_custom_radar_mode_rendering", false);
        DARKEN_BIOMES_ON_RADAR = BUILDER.comment("Darkens biomes by 50% before displaying on the radar. This option also makes it more noisier.").define("darken_biomes_on_radar", false);
        SHOW_RADAR_MODE_ID = BUILDER.pop().push("debug").comment("Shows the radar mode ID").define("show_radar_mode_id", false);
        RADAR_MODE_ID_SIDE = BUILDER.comment("The side to render the radar mode ID on").defineEnum("radar_mode_id_side", RadarModeIDSide.NORTH);
        DISABLE_OVERLAYS_WHEN_DEBUGGING = BUILDER.comment("Disables all overlays when client radar debugging is on").define("disable_overlays_when_debugging", true);
        DEBUG_RADARS = BUILDER.comment("Shows the location of all nearby radars as an overlay").define("debug_radars", false);
        DEBUG_WSRS = BUILDER.comment("Shows the location of all nearby WSRs as an overlay").define("debug_wsrs", false);
        DEBUG_METARS = BUILDER.comment("Shows the location of all nearby metars as an overlay").define("debug_metars", false);
        DEBUG_STORMS = BUILDER.comment("Shows the location of all nearby storms as an overlay").define("debug_storms", false);
        DEBUG = BUILDER.comment("Used for debugging").define("debug", false);
        TRANSPARENT_BACKGROUND = BUILDER.pop().push("experimental").comment("Only renders reflectivity/velocity/IR data. Experimental").define("transparent_background", false);
        LOWER_RADAR = BUILDER.comment("Lowers the radar to be closer to the actual block. May break overlays!").define("lower_radar", false);
        SPEC = BUILDER.build();
    }

    public enum RadarModeIDSide {
        NORTH(0, -1, -1),
        EAST(90, 2, -1),
        SOUTH(180, 2, 2),
        WEST(-90, -1, 2);

        public final int rotation, x, z;

        RadarModeIDSide(int rotation, int x, int z) {
            this.rotation = rotation;
            this.x = x;
            this.z = z;
        }
    }
}
