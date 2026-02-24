package net.nullved.pmweatherapi.client.render.debug;

import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.render.RadarRenderData;
import net.nullved.pmweatherapi.client.render.radar.IRadarOverlay;
import net.nullved.pmweatherapi.radar.NearbyRadars;
import net.nullved.pmweatherapi.storm.NearbyStorms;

public class DebugStormsOverlay extends DebugOverlay {
    public static final IRadarOverlay INSTANCE = new DebugStormsOverlay();

    @Override
    public void render(boolean canRender, RadarRenderData radarRenderData, Object... args) {
        NearbyStorms.client().forStormNearBlock(radarRenderData.radarPos(), radarRenderData.simSize(),
            storm -> renderMarker(radarRenderData, storm.position, 0xFFFFFFFF));
    }

    @Override
    public String getModID() {
        return PMWeatherAPI.MODID;
    }

    @Override
    public String getIDPath() {
        return "debug_storms";
    }
}
