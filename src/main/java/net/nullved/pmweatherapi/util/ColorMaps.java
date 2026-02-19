package net.nullved.pmweatherapi.util;


import dev.protomanly.pmweather.util.ColorTables;
import net.nullved.pmweatherapi.config.PMWClientConfig;

import java.awt.*;

/**
 * A collection of {@link ColorMap}s, mainly ones transferred from {@link ColorTables}
 * @since 0.14.15.6
 */
public class ColorMaps {
    public static void recomputeAll() {
        REFLECTIVITY.recomputeLookups(0.1f);
        MIXED_REFLECTIVITY.recomputeLookups(0.1f);
        SNOW_REFLECTIVITY.recomputeLookups(0.1f);
        NEGATIVE_VELOCITY.recomputeLookups(0.1f);
        POSITIVE_VELOCITY.recomputeLookups(0.1f);
        IR.recomputeLookups(0.1f);
        WINDSPEED.recomputeLookups(0.1f);
        SST.recomputeLookups(0.1f);
        HURRICANE_WINDSPEED.recomputeLookups(0.1f);
    }

    /**
     * A {@link ColorMap} equivalent to {@link ColorTables#getReflectivity(float, Color)}
     * @since 0.14.15.6
     */
    public static final ColorMap REFLECTIVITY = ColorMap.Builder.biome()
        .addPoint(0xFF5C9DAE, 19.0F)
        .addPoint(0xFF0B6409, 27.0F)
        .addPoint(0xFFC5B300, 40.0F)
        .override(0xFFFA9400, 40.0F)
        .addPoint(0xFFB2590C, 50.0F)
        .override(0xFFF9230B, 50.0F)
        .addPoint(0xFF822820, 60.0F)
        .override(0xFFCA99B4, 60.0F)
        .addPoint(0xFFC21C72, 70.0F)
        .build(0xFFFFFFFF, 70.0F);

    /**
     * A {@link ColorMap} equivalent to {@link ColorTables#getMixedReflectivity(float)}
     * @since 0.16.0.0
     */
    public static final ColorMap MIXED_REFLECTIVITY = ColorMap.Builder.of(0xFFFFFFFF)
        .override(0xFFFFFFF, 0.0F)
        .build(0xFF006FFF, 70.0F);

    /**
     * A {@link ColorMap} equivalent to {@link ColorTables#getSnowReflectivity(float)}
     * @since 0.16.0.0
     */
    public static final ColorMap SNOW_REFLECTIVITY = ColorMap.Builder.of(0xFFFAC3F8)
        .override(0xFFFAC3F8, 0.0F)
        .build(0xFFD200D2, 70.0F);

    /**
     * A {@link ColorMap} equivalent to {@link ColorTables#getVelocity(float)} for values &ge; 0
     * @since 0.14.15.6
     */
    public static final ColorMap POSITIVE_VELOCITY = ColorMap.Builder.of(0xFF969696)
        .overrideModeGreater()
        .override(0xFF8A7676, 0)
        .addPoint(0xFF843841, 12.0F)
        .override(0xFF6E0000, 12.0F)
        .addPoint(0xFFF30007, 39.0F)
        .override(0xFFFA3751, 39.0F)
        .addPoint(0xFFFFE8A3, 69.0F)
        .build(0xFF670602, 140);

    /**
     * A {@link ColorMap} equivalent to {@link ColorTables#getVelocity(float)} for values &le; 0
     * @since 0.14.15.6
     */
    public static final ColorMap NEGATIVE_VELOCITY = ColorMap.Builder.of(0xFF969696)
        .overrideModeGreater()
        .override(0xFF728570, 0)
        .addPoint(0xFF4E794C, 12.0F)
        .override(0xFF056603, 12.0F)
        .addPoint(0xFF30E0E3, 81.0F)
        .addPoint(0xFF160299, 106.0F)
        .build(0xFFFF0084, 140.0F);

    /**
     * A {@link ColorMap} equivalent to {@link ColorTables#getWindspeed(float)}
     * @since 0.14.15.6
     */
    public static final ColorMap WINDSPEED = ColorMap.Builder.of(0xFF000000)
        .addPoint(0xFF000000, 40.0F)
        .addPoint(0xFF6A80f1, 65.0F)
        .addPoint(0xFF75f3E0, 85.0F)
        .addPoint(0xFF74F151, 110.0F)
        .addPoint(0xFFF6DC35, 135.0F)
        .addPoint(0xFFF67F35, 165.0F)
        .addPoint(0xFFF63535, 200.0F)
        .addPoint(0xFFF035F6, 250.0F)
        .build(0xFFFFFFFF, 300.0F);

    /**
     * A {@link ColorMap} equivalent to {@link ColorTables#getIR(float)}
     * @since 0.15.0.0
     */
    public static final ColorMap IR = ColorMap.Builder.of(0xFF000000)
        .addPoint(0xFFFFFFFF, 100.0F)
        .addPoint(0xFF001074, 120.0F)
        .addPoint(0xFF69F8Fb, 140.0F)
        .addPoint(0xFF00FD00, 150.0F)
        .addPoint(0xFFFDFB47, 160.0F)
        .addPoint(0xFFEB3717, 180.0F)
        .addPoint(0xFF6E1A0A, 200.0F)
        .addPoint(0xFF000000, 220.0F)
        .build(0xFFFFFFFF, 260.0F);

    /**
     * A {@link ColorMap} equivalent to {@link ColorTables#getSST(float)}
     * @since 0.16.0.0
     */
    public static final ColorMap SST = ColorMap.Builder.of(0xFFFDFDFD)
        .addPoint(0xFF664DA6, 10.0F)
        .override(0xFF6C9DE9, 10.0F)
        .addPoint(0xFF6DFDFD, 20.0F)
        .override(0xFF6DFDFD, 20.0F)
        .addPoint(0xFF0BA332, 26.0F)
        .override(0xFFFDFD00, 26.0F)
        .addPoint(0xFFA70000, 30.0F)
        .override(0xFFA70043, 30.0F)
        .build(0xFFD900FD, 33.0F);

    /**
     * A {@link ColorMap} equivalent to {@link ColorTables#getHurricaneWindspeed(float)}
     * @since 0.16.0.0
     */
    public static final ColorMap HURRICANE_WINDSPEED = ColorMap.Builder.of(0xFFFDFDFD)
        .addPoint(0xFF2874DE, 39.0F)
        .override(0xFF9BBF82, 39.0F)
        .addPoint(0xFF76A357, 58.0F)
        .override(0xFF45722A, 58.0F)
        .addPoint(0xFF81A62C, 74.0F)
        .override(0xFFEBBF46, 74.0F)
        .addPoint(0xFFBA8F23, 96.0F)
        .override(0xFFDC9240, 96.0F)
        .addPoint(0xFFAB6116, 111.0F)
        .override(0xFFBE2600, 111.0F)
        .addPoint(0xFF8F1A00, 130.0F)
        .override(0xFF9C5376, 130.0F)
        .addPoint(0xFF6C2444, 157.0F)
        .override(0xFF6053A2, 157.0F)
        .addPoint(0xFF2F2471, 175.0F)
        .override(0xFF1C174A, 175.0F)
        .addPoint(0xFF656565, 200.0F)
        .override(0xFFFFFFFF, 200.0F)
        .build(0xFF000000, 300.0F);
}
