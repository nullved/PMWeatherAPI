package net.nullved.pmweatherapi.config;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.util.ColorMaps;

@EventBusSubscriber(modid = PMWeatherAPI.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PMWClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // RADAR RENDERING OPTIONS
    private static final ModConfigSpec.BooleanValue USE_ORIGINAL_PMWEATHER_RENDERING;
    public static boolean useOriginalPMWeatherRendering;
    private static final ModConfigSpec.BooleanValue USE_ORIGINAL_PMWEATHER_COLORS;
    public static boolean useOriginalPMWeatherColors;
    private static final ModConfigSpec.BooleanValue DARKEN_BIOMES_ON_RADAR;
    public static boolean darkenBiomesOnRadar;
    private static final ModConfigSpec.BooleanValue TRANSPARENT_BACKGROUND;
    public static boolean transparentBackground;
    private static final ModConfigSpec.BooleanValue DISABLE_CUSTOM_RADAR_MODE_RENDERING;
    public static boolean disableCustomRadarModeRendering;
    private static final ModConfigSpec.BooleanValue DISABLE_OVERLAYS_WHEN_DEBUGGING;
    public static boolean disableOverlaysWhenDebugging;
    private static final ModConfigSpec.BooleanValue SHOW_RADAR_MODE_ID;
    public static boolean showRadarModeId;
    private static final ModConfigSpec.EnumValue<RadarModeIDSide> RADAR_MODE_ID_SIDE;
    public static RadarModeIDSide radarModeIDSide;
    private static final ModConfigSpec.BooleanValue DEBUG;
    public static boolean debug;
    public static final ModConfigSpec SPEC;

    @SubscribeEvent
    private static void onLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC && !(event instanceof ModConfigEvent.Unloading)) {
            PMWeatherAPI.LOGGER.info("Loading Client PMWeatherAPI Configs");
            useOriginalPMWeatherRendering = USE_ORIGINAL_PMWEATHER_RENDERING.getAsBoolean();
            useOriginalPMWeatherColors = USE_ORIGINAL_PMWEATHER_COLORS.getAsBoolean();
            darkenBiomesOnRadar = DARKEN_BIOMES_ON_RADAR.getAsBoolean();
            transparentBackground = TRANSPARENT_BACKGROUND.getAsBoolean();
            disableCustomRadarModeRendering = DISABLE_CUSTOM_RADAR_MODE_RENDERING.getAsBoolean();
            disableOverlaysWhenDebugging = DISABLE_OVERLAYS_WHEN_DEBUGGING.getAsBoolean();
            showRadarModeId = SHOW_RADAR_MODE_ID.getAsBoolean();
            radarModeIDSide = RADAR_MODE_ID_SIDE.get();
            debug = DEBUG.getAsBoolean();
        }

        if (event instanceof ModConfigEvent.Reloading) {
            ColorMaps.recomputeAll();
        }
    }

    static {
        USE_ORIGINAL_PMWEATHER_RENDERING = BUILDER.comment("Disables all custom rendering and optimizations from PMWeatherAPI! Also disables all overlays and custom radar modes!").define("use_original_pmweather_rendering", false);
        USE_ORIGINAL_PMWEATHER_COLORS = BUILDER.comment("Use's PMWeathers ColorTables instead of ColorMaps. You may or may not see a loss of performance!").define("use_original_pmweather_colors", false);
        DARKEN_BIOMES_ON_RADAR = BUILDER.comment("Darkens biomes by 50% before displaying on the radar. This option also makes it more noisier.").define("darken_biomes_on_radar", false);
        TRANSPARENT_BACKGROUND = BUILDER.comment("Only renders reflectivity/velocity/IR data. Experimental").define("transparent_background", false);
        DISABLE_CUSTOM_RADAR_MODE_RENDERING = BUILDER.comment("Disables custom radar mode rendering").define("disable_custom_radar_mode_rendering", false);
        DISABLE_OVERLAYS_WHEN_DEBUGGING = BUILDER.comment("Disables all overlays when client radar debugging is on").define("disable_overlays_when_debugging", true);
        SHOW_RADAR_MODE_ID = BUILDER.comment("Shows the radar mode ID").define("show_radar_mode_id", false);
        RADAR_MODE_ID_SIDE = BUILDER.comment("The side to render the radar mode ID on").defineEnum("radar_mode_id_side", RadarModeIDSide.NORTH);
        DEBUG = BUILDER.comment("Used for debugging").define("debug", false);
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
