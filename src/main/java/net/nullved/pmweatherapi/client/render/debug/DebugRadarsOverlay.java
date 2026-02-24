package net.nullved.pmweatherapi.client.render.debug;

import net.nullved.pmweatherapi.PMWeatherAPI;
import net.nullved.pmweatherapi.client.render.RadarRenderData;
import net.nullved.pmweatherapi.client.render.radar.IRadarOverlay;
import net.nullved.pmweatherapi.radar.NearbyRadars;

public class DebugRadarsOverlay extends DebugOverlay {
    public static final IRadarOverlay INSTANCE = new DebugRadarsOverlay();

    @Override
    public void render(boolean canRender, RadarRenderData radarRenderData, Object... args) {
        NearbyRadars.client().forRadarNearBlock(radarRenderData.radarPos(), radarRenderData.simSize(),
            bp -> renderMarker(radarRenderData, bp.getCenter(), 0xFFFF0000));
    }

    @Override
    public String getModID() {
        return PMWeatherAPI.MODID;
    }

    @Override
    public String getIDPath() {
        return "debug_radars";
    }
}
